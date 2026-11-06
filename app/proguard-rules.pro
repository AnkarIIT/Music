# Media3 / ExoPlayer
-keep class androidx.media3.** { *; }
-keep class com.google.android.exoplayer2.** { *; }

# NewPipe Extractor
-keep class org.schabi.newpipe.** { *; }

# Hilt
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewModelAssistedFactory { *; }

# Room
-keep class androidx.room.** { *; }
-keep class * extends androidx.room.RoomDatabase { *; }
-keep class * implements androidx.room.EntityDeletionOrUpdateAdapter { *; }

# Coil
-keep class coil.** { *; }

# OkHttp
-keep class okhttp3.** { *; }
-keep class okio.** { *; }

# Kotlinx Serialization
-keep class kotlinx.serialization.** { *; }
-keep @kotlinx.serialization.Serializable class * { *; }

# Coroutines
-keep class kotlinx.coroutines.** { *; }

# WorkManager
-keep class androidx.work.** { *; }

# DataStore
-keep class androidx.datastore.** { *; }

# Prevent obfuscation of generated code
-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
}

# Keep annotation processors
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes EnclosingMethod
-keepattributes InnerClasses

# Keep our app classes (for serialization, Room, Hilt)
-keep class com.example.music.** { *; }
-keepclassmembers class com.example.music.** { *; }

# Strip Log calls in release builds
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
    public static int w(...);
    public static int e(...);
    public static int wtf(...);
}

# Keep names for reflection
-keepnames class * {
    *;
}