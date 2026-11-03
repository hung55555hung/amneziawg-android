# JNI entry points in libawg-go.so are bound by name.
-keep class org.amnezia.awg.GoBackend { native <methods>; }
-keep class org.amnezia.awg.backend.GoBackend$VpnService { *; }
