package com.iqoo.vectorz.core.policy

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AppFieldRule(
    val packageName: String,
    val appDisplayName: String,
    val allowedFields: Set<String>, // e.g. "email", "phone", "pan_number", "aadhaar_number"
    val requiresBiometricsAlways: Boolean = true,
    val isCompletelyBlocked: Boolean = false
)

class AppPermissionMatrix {

    private val _rules = MutableStateFlow<Map<String, AppFieldRule>>(
        mapOf(
            "com.zerodha.kite3" to AppFieldRule(
                packageName = "com.zerodha.kite3",
                appDisplayName = "Zerodha Kite (Stock Broker)",
                allowedFields = setOf("pan_number", "date_of_birth", "email", "phone"),
                requiresBiometricsAlways = true
            ),
            "in.amazon.mShop.android.shopping" to AppFieldRule(
                packageName = "in.amazon.mShop.android.shopping",
                appDisplayName = "Amazon Shopping",
                allowedFields = setOf("address", "postal_code", "phone", "email"),
                requiresBiometricsAlways = false
            ),
            "com.phonepe.app" to AppFieldRule(
                packageName = "com.phonepe.app",
                appDisplayName = "PhonePe UPI",
                allowedFields = setOf("phone", "email"),
                requiresBiometricsAlways = true
            ),
            "com.unverified.loanapp" to AppFieldRule(
                packageName = "com.unverified.loanapp",
                appDisplayName = "Quick Cash Loan (Suspicious)",
                allowedFields = emptySet(),
                requiresBiometricsAlways = true,
                isCompletelyBlocked = true
            )
        )
    )
    val rules: StateFlow<Map<String, AppFieldRule>> = _rules.asStateFlow()

    fun isFieldAllowedForApp(packageName: String, canonicalFieldKey: String): Boolean {
        val rule = _rules.value[packageName] ?: return false
        if (rule.isCompletelyBlocked) return false
        return rule.allowedFields.contains(canonicalFieldKey)
    }

    fun setFieldPermission(packageName: String, canonicalFieldKey: String, isAllowed: Boolean) {
        val currentRule = _rules.value[packageName] ?: return
        val updatedFields = if (isAllowed) {
            currentRule.allowedFields + canonicalFieldKey
        } else {
            currentRule.allowedFields - canonicalFieldKey
        }
        val updatedMap = _rules.value.toMutableMap()
        updatedMap[packageName] = currentRule.copy(allowedFields = updatedFields)
        _rules.value = updatedMap
    }

    fun toggleAppBlock(packageName: String, block: Boolean) {
        val currentRule = _rules.value[packageName] ?: return
        val updatedMap = _rules.value.toMutableMap()
        updatedMap[packageName] = currentRule.copy(isCompletelyBlocked = block)
        _rules.value = updatedMap
    }
}
