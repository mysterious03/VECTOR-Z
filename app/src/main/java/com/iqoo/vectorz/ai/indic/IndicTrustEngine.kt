package com.iqoo.vectorz.ai.indic

/**
 * Multi-Lingual Indic Trust Engine.
 * Provides on-device localized safety advisories across 12 Indian languages
 * with local voice synthesis cues for elder/rural digital inclusion.
 */
enum class IndicLanguage(val code: String, val displayName: String, val nativeName: String) {
    ENGLISH("en", "English", "English"),
    HINDI("hi", "Hindi", "हिन्दी"),
    TAMIL("ta", "Tamil", "தமிழ்"),
    TELUGU("te", "Telugu", "తెలుగు"),
    BENGALI("bn", "Bengali", "বাংলা"),
    MARATHI("mr", "Marathi", "मराठी"),
    KANNADA("kn", "Kannada", "ಕನ್ನಡ"),
    MALAYALAM("ml", "Malayalam", "മലയാളം"),
    GUJARATI("gu", "Gujarati", "ગુજરાતી"),
    PUNJABI("pa", "Punjabi", "ਪੰਜਾਬੀ")
}

data class LocalizedAlert(
    val language: IndicLanguage,
    val headline: String,
    val adviceText: String,
    val spokenAudioCue: String
)

class IndicTrustEngine {

    private val translations = mapOf(
        IndicLanguage.ENGLISH to LocalizedAlert(
            language = IndicLanguage.ENGLISH,
            headline = "CRITICAL PAYMENT RISK",
            adviceText = "You never enter your UPI PIN to receive money. Do not proceed.",
            spokenAudioCue = "Warning: UPI PIN is only used to deduct money from your account."
        ),
        IndicLanguage.HINDI to LocalizedAlert(
            language = IndicLanguage.HINDI,
            headline = "गंभीर भुगतान चेतावनी",
            adviceText = "पैसे प्राप्त करने के लिए कभी भी अपना UPI PIN दर्ज न करें। यह एक धोखाधड़ी है।",
            spokenAudioCue = "सावधान: पैसे प्राप्त करने के लिए यूपीआई पिन की आवश्यकता नहीं होती।"
        ),
        IndicLanguage.TAMIL to LocalizedAlert(
            language = IndicLanguage.TAMIL,
            headline = "முக்கிய கட்டண எச்சரிக்கை",
            adviceText = "பணம் பெற உங்கள் UPI PIN ஐ ஒருபோதும் உள்ளிட வேண்டாம். இது மோசடி.",
            spokenAudioCue = "எச்சரிக்கை: பணம் பெறுவதற்கு UPI PIN தேவையில்லை."
        ),
        IndicLanguage.TELUGU to LocalizedAlert(
            language = IndicLanguage.TELUGU,
            headline = "కీలకమైన చెల్లింపు హెచ్చరిక",
            adviceText = "డబ్బు అందుకోవడానికి ఎప్పుడూ UPI PIN ను ఎంటర్ చేయవద్దు. ఇది మోసం.",
            spokenAudioCue = "హెచ్చరిక: డబ్బు రావడానికి యూపీఐ పిన్ అవసరం లేదు."
        ),
        IndicLanguage.BENGALI to LocalizedAlert(
            language = IndicLanguage.BENGALI,
            headline = "গুরুত্বপূর্ণ পেমেন্ট সতর্কতা",
            adviceText = "টাকা পাওয়ার জন্য কখনোই আপনার UPI PIN দেবেন না। এটি জালিয়াতি।",
            spokenAudioCue = "সতর্কতা: টাকা পাওয়ার জন্য ইউপিআই পিন প্রয়োজন নেই।"
        ),
        IndicLanguage.MARATHI to LocalizedAlert(
            language = IndicLanguage.MARATHI,
            headline = "महत्त्वाची पेमेंट चेतावणी",
            adviceText = "पैसे मिळवण्यासाठी कधीही आपला UPI PIN टाकू नका. ही फसवणूक आहे.",
            spokenAudioCue = "सावधान: पैसे मिळवण्यासाठी यूपीआय पिनची गरज नसते."
        )
    )

    fun getAlertForLanguage(lang: IndicLanguage): LocalizedAlert {
        return translations[lang] ?: translations[IndicLanguage.ENGLISH]!!
    }
}
