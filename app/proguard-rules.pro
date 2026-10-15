# Proguard / R8 Configuration for Bank Soal Pro (Production Release)

# 1. Attributes & Annotations
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod,SourceFile,LineNumberTable
-keepparameternames

# 2. AndroidX Room Database
-keepclassmembers class * {
    @androidx.room.* *;
}
-keep class androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }

# 3. Domain Models & Local Entities (Prevent field obfuscation for DB & JSON)
-keep class com.banksoalpro.app.domain.model.** { *; }
-keep class com.banksoalpro.app.data.local.entity.** { *; }
-keep class com.banksoalpro.app.data.remote.dto.** { *; }
-keep class com.banksoalpro.app.data.billing.model.** { *; }

# 4. Kotlin Coroutines & Flow
-keepclassmembers class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**

# 5. Google Play Billing Library
-keep class com.android.billingclient.api.** { *; }
-dontwarn com.android.billingclient.**

# 6. Google Mobile Ads (AdMob)
-keep class com.google.android.gms.ads.** { *; }
-keep interface com.google.android.gms.ads.** { *; }
-dontwarn com.google.android.gms.ads.**

# 7. Google Sign-In & Play Services Auth
-keep class com.google.android.gms.auth.api.signin.** { *; }
-keep class com.google.android.gms.common.** { *; }
-dontwarn com.google.android.gms.**

# 8. WebKit / Math & LaTeX Rendering
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebChromeClient {
    public void *(android.webkit.WebView, java.lang.String);
}

# 9. General Safe Optimizations
-dontwarn org.json.**
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
