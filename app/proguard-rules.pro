# ==========================================================================
# VECTOR-Z Production ProGuard & R8 Hardening Rules
# ==========================================================================

# 1. Preserve Cryptographic Enclaves and Android Keystore JNI
-keep class com.iqoo.vectorz.core.security.** { *; }
-keepclassmembers class * extends java.security.KeyStore { *; }
-keep class androidx.security.crypto.** { *; }
-keep class androidx.biometric.** { *; }

# 2. Preserve Room Database & DAOs
-keep class com.iqoo.vectorz.core.database.** { *; }
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# 3. Preserve ML Kit & LiteRT / Qualcomm Hexagon bindings
-keep class com.google.mlkit.vision.** { *; }
-keep class org.tensorflow.lite.** { *; }

# 4. Strip Debug Logging in Production Release Builds
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
}

# 5. Obfuscate internal AI engines and Heuristics
-repackageclasses 'com.iqoo.vectorz.internal'
-allowaccessmodification
