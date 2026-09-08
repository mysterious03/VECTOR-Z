package com.iqoo.vectorz.feature.simulator

/**
 * 15 Indian FinTech & Digital Life Test Vectors for Hackathon Stress Testing.
 */
data class TestVector(
    val id: String,
    val title: String,
    val category: String,
    val rawPayload: String,
    val expectedThreatLevel: String, // HIGH_RISK, SUSPICIOUS, CLEAN
    val keyScamIndicator: String
)

object TestVectorLibrary {

    val vectors = listOf(
        TestVector(
            id = "VEC_01",
            title = "Electricity Disconnection Threat",
            category = "NOTIFICATION_SMS",
            rawPayload = "Dear consumer, your electricity power will be disconnected at 9:30 PM tonight due to unpaid bill. Immediately update here: http://power-board.in/update.apk",
            expectedThreatLevel = "HIGH_RISK",
            keyScamIndicator = "Malicious APK Dropper + Urgent Disconnection Panic"
        ),
        TestVector(
            id = "VEC_02",
            title = "TRAI / DoT SIM Block Notice",
            category = "NOTIFICATION_SMS",
            rawPayload = "TRAI Warning: Your mobile number will be blocked within 2 hours due to illegal activity. Call Department officer 9811223344 immediately.",
            expectedThreatLevel = "HIGH_RISK",
            keyScamIndicator = "Impersonation of Telecom Authority + Immediate Block Threat"
        ),
        TestVector(
            id = "VEC_03",
            title = "FedEx Customs Drug Parcel Trap",
            category = "NOTIFICATION_SMS",
            rawPayload = "FedEx Mumbai: Your parcel containing contraband passport and illegal items is seized by Narcotics Bureau. Contact officer at WhatsApp link: http://fedex-customs.co",
            expectedThreatLevel = "HIGH_RISK",
            keyScamIndicator = "Digital Arrest Extortion / Fake Law Enforcement Lure"
        ),
        TestVector(
            id = "VEC_04",
            title = "Telegram Part-Time YouTube Rating Scam",
            category = "COMMERCE_LURE",
            rawPayload = "Earn ₹3,000 - ₹8,000 daily working 10 mins from home by liking YouTube videos. Deposit ₹1,000 security fee to start.",
            expectedThreatLevel = "HIGH_RISK",
            keyScamIndicator = "Pre-payment task scam with guaranteed unrealistic ROI"
        ),
        TestVector(
            id = "VEC_05",
            title = "UPI Intent Inversion (Refund Collect)",
            category = "PAYMENT_UPI",
            rawPayload = "upi://pay?pa=refund-desk@fakebank&pn=Refund+Desk&am=25000&cu=INR",
            expectedThreatLevel = "HIGH_RISK",
            keyScamIndicator = "Claiming refund while executing a DEBIT transaction"
        ),
        TestVector(
            id = "VEC_06",
            title = "Fake 90% Discount Markdown Anchor",
            category = "COMMERCE_LURE",
            rawPayload = "Pro Bluetooth Headset: Originally ₹19,999, now only ₹999 (95% OFF!). Strictly Non-refundable.",
            expectedThreatLevel = "HIGH_RISK",
            keyScamIndicator = "Inflated fake MSRP anchor + hidden non-returnable clause"
        ),
        TestVector(
            id = "VEC_07",
            title = "WhatsApp Off-Platform Payment",
            category = "COMMERCE_LURE",
            rawPayload = "Special clearance price ₹4,500. Do not checkout on website. Send UPI to 9876543210@paytm and share screenshot.",
            expectedThreatLevel = "HIGH_RISK",
            keyScamIndicator = "Direct off-platform payment bypassing buyer protection"
        ),
        TestVector(
            id = "VEC_08",
            title = "AI Voice Clone Emergency Call",
            category = "MEDIA_AUDIO",
            rawPayload = "Voice note: 'Dad, I met with an accident in Bangalore. Hospital needs ₹40,000 immediately, please transfer to this doctor's UPI.'",
            expectedThreatLevel = "HIGH_RISK",
            keyScamIndicator = "Spectral discontinuity + high-urgency distress lure"
        ),
        TestVector(
            id = "VEC_09",
            title = "SBI YONO PAN Expiry Warning",
            category = "NOTIFICATION_SMS",
            rawPayload = "Dear customer, your SBI netbanking is blocked due to unlinked PAN card. Update Aadhaar details here: http://sbi-kyc-verify.top",
            expectedThreatLevel = "HIGH_RISK",
            keyScamIndicator = "Phishing domain mimicking State Bank of India"
        ),
        TestVector(
            id = "VEC_10",
            title = "Income Tax Refund Phishing",
            category = "NOTIFICATION_SMS",
            rawPayload = "Income Tax Dept: An IT refund of ₹15,480 has been approved. Enter your bank account and debit card PIN to receive: http://incometax-efile.top",
            expectedThreatLevel = "HIGH_RISK",
            keyScamIndicator = "Asking for Debit Card PIN to receive tax refund"
        ),
        TestVector(
            id = "VEC_11",
            title = "Free Government Electricity Relief Scheme",
            category = "TRUTH_CONTENT",
            rawPayload = "Under Prime Minister Energy Scheme, all citizen accounts are credited with ₹25,000 relief. Forward this to 10 groups to activate voucher.",
            expectedThreatLevel = "HIGH_RISK",
            keyScamIndicator = "Fake viral government subsidy scheme with chain forwarding"
        ),
        TestVector(
            id = "VEC_12",
            title = "Credit Card Limit Double Offer",
            category = "NOTIFICATION_SMS",
            rawPayload = "HDFC Card alert: Your credit limit is increased to ₹5,00,000. Submit OTP to activate upgrade instantly: http://hdfc-limit.xyz",
            expectedThreatLevel = "HIGH_RISK",
            keyScamIndicator = "Phishing link asking for OTP to upgrade credit card"
        ),
        TestVector(
            id = "VEC_13",
            title = "Deepfake Video KYC Synthetic Face",
            category = "MEDIA_VIDEO",
            rawPayload = "Video stream frame with 0.88 facial boundary blending score and asynchronous corneal reflections.",
            expectedThreatLevel = "HIGH_RISK",
            keyScamIndicator = "Facial mesh generative artifact in live video stream"
        ),
        TestVector(
            id = "VEC_14",
            title = "Smart Electricity Meter Upgrade APK",
            category = "NOTIFICATION_SMS",
            rawPayload = "Mandatory Smart Meter transition for all Delhi consumers. Install helper utility: http://bescom-portal.in/meter.apk",
            expectedThreatLevel = "HIGH_RISK",
            keyScamIndicator = "Trojan utility APK disguised as government meter tool"
        ),
        TestVector(
            id = "VEC_15",
            title = "Legitimate Swiggy Order Confirmation",
            category = "NOTIFICATION_SMS",
            rawPayload = "Your Swiggy order #91823 has been delivered. Total paid: ₹384 via UPI. Rate your delivery partner.",
            expectedThreatLevel = "CLEAN",
            keyScamIndicator = "Authentic informational delivery receipt"
        )
    )
}
