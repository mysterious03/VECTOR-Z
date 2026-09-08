package com.iqoo.vectorz.service.autofill

import android.app.assist.AssistStructure
import android.os.CancellationSignal
import android.service.autofill.AutofillService
import android.service.autofill.Dataset
import android.service.autofill.FillCallback
import android.service.autofill.FillRequest
import android.service.autofill.FillResponse
import android.service.autofill.SaveCallback
import android.service.autofill.SaveRequest
import android.view.autofill.AutofillId
import android.view.autofill.AutofillValue
import android.widget.RemoteViews
import com.iqoo.vectorz.R
import com.iqoo.vectorz.ai.classifier.FieldClassifier
import com.iqoo.vectorz.core.database.VectorZDatabase
import com.iqoo.vectorz.core.model.CanonicalFieldType
import com.iqoo.vectorz.core.security.CryptoEngine
import com.iqoo.vectorz.data.repository.AuditRepository
import com.iqoo.vectorz.data.repository.VaultRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VectorZAutofillService : AutofillService() {

    private val fieldClassifier = FieldClassifier()
    private val scope = CoroutineScope(Dispatchers.IO)

    private val database by lazy { VectorZDatabase.getInstance(this) }
    private val cryptoEngine by lazy { CryptoEngine(this) }
    private val vaultRepo by lazy { VaultRepository(database.vaultDao(), cryptoEngine) }
    private val auditRepo by lazy { AuditRepository(database.auditDao(), database.appPolicyDao()) }

    override fun onFillRequest(
        request: FillRequest,
        cancellationSignal: CancellationSignal,
        callback: FillCallback
    ) {
        val structure = request.fillContexts.lastOrNull()?.structure ?: run {
            callback.onSuccess(null)
            return
        }

        val packageName = request.fillContexts.lastOrNull()?.structure?.activityComponent?.packageName ?: "unknown.app"

        scope.launch {
            // Check per-app policy
            val policy = auditRepo.getPolicyForApp(packageName)
            if (!policy.isAutofillAllowed) {
                callback.onSuccess(null)
                return@launch
            }

            val targetNodes = mutableListOf<Triple<AutofillId, CanonicalFieldType, String>>()
            traverseStructure(structure.getWindowNodeAt(0).rootViewNode, targetNodes)

            if (targetNodes.isEmpty()) {
                callback.onSuccess(null)
                return@launch
            }

            val responseBuilder = FillResponse.Builder()

            for ((autofillId, fieldType, _) in targetNodes) {
                val match = vaultRepo.findFirstMatchingField(fieldType)
                if (match != null) {
                    val (field, decryptedValue) = match
                    val masked = field.maskedValue

                    val presentation = RemoteViews(packageName, android.R.layout.simple_list_item_2).apply {
                        setTextViewText(android.R.id.text1, "🛡️ Vector-Z • ${fieldType.label}")
                        setTextViewText(android.R.id.text2, "Insert $masked (Tap to confirm)")
                    }

                    val dataset = Dataset.Builder(presentation)
                        .setValue(autofillId, AutofillValue.forText(decryptedValue))
                        .build()

                    responseBuilder.addDataset(dataset)

                    // Audit event
                    auditRepo.recordAudit(
                        category = "AUTOFILL",
                        appPackage = packageName,
                        actionSummary = "Autofill suggested ${fieldType.label}",
                        riskLevel = "SAFE",
                        resultStatus = "SUGGESTED"
                    )
                }
            }

            callback.onSuccess(responseBuilder.build())
        }
    }

    private fun traverseStructure(
        node: AssistStructure.ViewNode,
        targetNodes: MutableList<Triple<AutofillId, CanonicalFieldType, String>>
    ) {
        val autofillId = node.autofillId
        val hints = node.autofillHints?.toList() ?: emptyList()
        val idEntry = node.idEntry
        val hintText = node.hint
        val text = node.text?.toString()

        val classifiedType = fieldClassifier.classifyField(
            autofillHints = hints,
            idEntry = idEntry,
            hintText = hintText,
            labelText = text
        )

        if (autofillId != null && classifiedType != null) {
            targetNodes.add(Triple(autofillId, classifiedType, hintText ?: idEntry ?: ""))
        }

        for (i in 0 until node.childCount) {
            traverseStructure(node.getChildAt(i), targetNodes)
        }
    }

    override fun onSaveRequest(request: SaveRequest, callback: SaveCallback) {
        callback.onSuccess()
    }
}
