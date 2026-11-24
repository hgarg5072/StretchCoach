# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Preserve line numbers for readable crash stack traces
-keepattributes SourceFile,LineNumberTable

# Keep data entities, backup/export models, and Firestore/Moshi reflection DTOs
-keep class com.example.data.** { *; }

# Keep Android R.drawable entry names intact for ArtworkMapper/PoseManifestRepository lookup
-keepclassmembers class com.example.R$drawable {
    public static <fields>;
}

# Moshi / Kotlin reflection safety
-keepclasseswithmembers class * {
    @com.squareup.moshi.FromJson <methods>;
    @com.squareup.moshi.ToJson <methods>;
}
-keep @com.squareup.moshi.JsonClass class * { *; }

# Suppress harmless optional warnings from Retrofit / OkHttp / Coroutines
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn retrofit2.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
