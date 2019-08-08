#include <jni.h>
#include "include/com_pwolfgang_numjava_DotProduct.h"

/*
 * Class:     com_pwolfgang_numjava_DotProduct
 * Method:    intXintInnerProduct
 * Signature: (IIIII[I[I)I
 */
JNIEXPORT jint JNICALL Java_com_pwolfgang_numjava_DotProduct_intXintInnerProduct
  (JNIEnv *env, jclass, jint leftStride, jint leftLastIndex, jint leftIndex, 
        jint rightStride, jint rightIndex, jintArray leftData, jintArray rightData) {
    
    long int result = 0;
    
    long int* leftDataP = env->GetIntArrayElements(leftData, NULL);
    long int* rightDataP = env->GetIntArrayElements(rightData, NULL);
    
    while (leftIndex < leftLastIndex) {
        result += leftDataP[leftIndex] * rightDataP[rightIndex];
        leftIndex += leftStride;
        rightIndex += rightStride;
    }
    env->ReleaseIntArrayElements(rightData, rightDataP, 0);
    env->ReleaseIntArrayElements(leftData, leftDataP, 0);
    return result;
    
}

/*
 * Class:     com_pwolfgang_numjava_DotProduct
 * Method:    intXfloatInnerProduct
 * Signature: (IIIII[I[F)F
 */
JNIEXPORT jfloat JNICALL Java_com_pwolfgang_numjava_DotProduct_intXfloatInnerProduct
  (JNIEnv *env , jclass, jint leftStride, jint leftLastIndex, jint leftIndex, 
        jint rightStride, jint rightIndex, jintArray leftData, jfloatArray rightData) {
    
    float result = 0;
    
    long int* leftDataP = env->GetIntArrayElements(leftData, NULL);
    float* rightDataP = env->GetFloatArrayElements(rightData, NULL);
    
    while (leftIndex < leftLastIndex) {
        result += leftDataP[leftIndex] * rightDataP[rightIndex];
        leftIndex += leftStride;
        rightIndex += rightStride;
    }
    env->ReleaseFloatArrayElements(rightData, rightDataP, 0);
    env->ReleaseIntArrayElements(leftData, leftDataP, 0);
    return result;
}


/*
 * Class:     com_pwolfgang_numjava_DotProduct
 * Method:    floatXfloatInnerProduct
 * Signature: (IIIII[F[F)F
 */
JNIEXPORT jfloat JNICALL Java_com_pwolfgang_numjava_DotProduct_floatXfloatInnerProduct
  (JNIEnv *env, jclass, jint leftStride, jint leftLastIndex, jint leftIndex, 
        jint rightStride, jint rightIndex, jfloatArray leftData, jfloatArray rightData){
    float result = 0;
    
    float* leftDataP = env->GetFloatArrayElements(leftData, NULL);
    float* rightDataP = env->GetFloatArrayElements(rightData, NULL);
    
    while (leftIndex < leftLastIndex) {
        result += leftDataP[leftIndex] * rightDataP[rightIndex];
        leftIndex += leftStride;
        rightIndex += rightStride;
    }
    env->ReleaseFloatArrayElements(rightData, rightDataP, 0);
    env->ReleaseFloatArrayElements(leftData, leftDataP, 0);
    return result; 
}

