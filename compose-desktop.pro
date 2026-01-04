-dontwarn net.bytebuddy.**
-dontwarn com.sun.jna.**
-dontwarn javax.annotation.**
-dontwarn edu.umd.cs.findbugs.annotations.**
-dontwarn org.objectweb.asm.**
-dontwarn com.fasterxml.jackson.**
-dontwarn androidx.lifecycle.**

# Fix for VerifyError: Bad return type in Compose Runtime
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.compose.ui.** { *; }

# Fix for Jackson Initialization Error
-keep class com.fasterxml.jackson.** { *; }
-keep interface com.fasterxml.jackson.** { *; }
-keepattributes Signature,RuntimeVisibleAnnotations,AnnotationDefault
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep data model classes used by Jackson for JSON serialization
-keep class Main.** { *; }
-keep class Data.** { *; }
