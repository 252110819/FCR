#include <jni.h>
#include <string>

JNIEXPORT jstring JNICALL
Java_scholardesign_awei_com_flowercapturingrecognize_Activity_MainActivity_hellowolrd(JNIEnv *env,
                                                                                      jobject instance) {

    return env->NewStringUTF("");
}

extern "C"
JNIEXPORT jstring JNICALL
Java_scholardesign_awei_com_flowercapturingrecognize_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());

}
