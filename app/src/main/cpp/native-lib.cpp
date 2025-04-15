#include <jni.h>
#include <string>

extern "C" int32_t test();

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_rustnativecpp_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++" + std::to_string(test());
    return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_rustnativecpp_MainActivity_stringFromRust(
        JNIEnv* env,
        jobject /* this */);
