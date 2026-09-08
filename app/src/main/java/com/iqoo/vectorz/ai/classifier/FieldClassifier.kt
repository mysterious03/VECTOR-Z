package com.iqoo.vectorz.ai.classifier

import com.iqoo.vectorz.core.model.CanonicalFieldType

class FieldClassifier {

    /**
     * Deterministic schema matcher: maps Autofill hints, HTML input names, resource IDs,
     * and surrounding labels directly to a CanonicalFieldType using alias dictionary.
     */
    fun classifyField(
        autofillHints: List<String>,
        idEntry: String?,
        hintText: String?,
        labelText: String?
    ): CanonicalFieldType? {
        val tokens = buildString {
            append(autofillHints.joinToString(" "))
            append(" ")
            append(idEntry ?: "")
            append(" ")
            append(hintText ?: "")
            append(" ")
            append(labelText ?: "")
        }.lowercase()

        // Match against canonical aliases
        for (canonical in CanonicalFieldType.entries) {
            for (alias in canonical.aliases) {
                if (tokens.contains(alias.lowercase())) {
                    return canonical
                }
            }
        }

        return null
    }
}
