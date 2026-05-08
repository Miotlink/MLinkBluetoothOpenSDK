# Keep SDK public entrypoint.
-keep class com.miotlink.MLinkSmartBluetoothSDK { *; }

# Keep public listener and callback APIs used by app integrations.
-keep class com.miotlink.bluetooth.listener.** { *; }
-keep class com.miotlink.bluetooth.callback.** { *; }

# Keep model classes that are commonly exposed across SDK API.
-keep class com.miotlink.bluetooth.model.** { *; }
-keep class com.miotlink.bluetooth.utils.** { *; }

# Keep annotations and generic signatures for reflection/serialization.
-keepattributes Signature,*Annotation*,InnerClasses,EnclosingMethod,Exceptions

# Fastjson compatibility.
-keep class com.alibaba.fastjson.** { *; }
-dontwarn com.alibaba.fastjson.**