-keepattributes *Annotation*
-keep class com.shreyash.antitheft.** { *; }

# Tink / security-crypto: referenced annotations not bundled in the AAR
-dontwarn com.google.errorprone.annotations.**
-dontwarn javax.annotation.**
