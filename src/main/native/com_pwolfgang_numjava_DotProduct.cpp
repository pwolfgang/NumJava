#include <jni.h>
#include <stdlib.h>
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

/*
 * Class:     com_pwolfgang_numjava_DotProduct
 * Method:    iXiMMUL
 * Signature: (IIIIIII[I[II[II)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_com_pwolfgang_numjava_DotProduct_iXiMMUL
  (JNIEnv *env, jclass, jint nRows, jint nCols, jint innerCount, jint aOffset, 
        jint aColStride, jint bOffset, jint bRowStride, jintArray aData, 
        jintArray bData, jint bColStride, jint aRowStride) {    
    long int* aDataP = env->GetIntArrayElements(aData, NULL);
    long int* bDataP = env->GetIntArrayElements(bData, NULL);
    aDataP += aOffset;
    bDataP += bOffset;
    long int* result = (long int*)malloc(nRows * nCols * sizeof(long int));
    int cijIndex = 0;
    int aRowIndex = 0;
    for (int i = 0; i < nRows; ++i) {
        int bColIndex = 0;
        for (int j = 0; j < nCols; ++j) {
            long int sum = 0;
            int bRowIndex = 0;
            int aColIndex = 0;
            for (int k = 0; k < innerCount; ++k) {
                int aikIndex = aRowIndex + aColIndex;
                int bkjIndex = bRowIndex + bColIndex;
                sum += aDataP[aikIndex] * bDataP[bkjIndex];
                bRowIndex += bRowStride;
                aColIndex += aColStride;
            }
            result[cijIndex++] = sum;
            bColIndex += bColStride;
        }
        aRowIndex += aRowStride;
    }
    jintArray resultData = env->NewIntArray(nRows * nCols);
    env->SetIntArrayRegion(resultData, 0, nRows*nCols, result);
    free(result);
    env->ReleaseIntArrayElements(aData, aDataP, 0);
    env->ReleaseIntArrayElements(bData, bDataP, 0);
    return resultData;
}

/*
 * Class:     com_pwolfgang_numjava_DotProduct
 * Method:    fXfMMUL
 * Signature: (IIIIIII[F[FI[FI)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_com_pwolfgang_numjava_DotProduct_fXfMMUL
  (JNIEnv *env, jclass, jint nRows, jint nCols, jint innerCount, jint aOffset, 
        jint aColStride, jint bOffset, jint bRowStride, jfloatArray aData, 
        jfloatArray bData, jint bColStride, jint aRowStride) {
    float* aDataP = env->GetFloatArrayElements(aData, NULL);
    float* bDataP = env->GetFloatArrayElements(bData, NULL);
    aDataP += aOffset;
    bDataP += bOffset;
    float* result = (float*)malloc(nRows * nCols * sizeof(float));
    int cijIndex = 0;
    int aRowIndex = 0;
    for (int i = 0; i < nRows; ++i) {
        int bColIndex = 0;
        for (int j = 0; j < nCols; ++j) {
            float sum = 0;
            int bRowIndex = 0;
            int aColIndex = 0;
            for (int k = 0; k < innerCount; ++k) {
                int aikIndex = aRowIndex + aColIndex;
                int bkjIndex = bRowIndex + bColIndex;
                sum += aDataP[aikIndex] * bDataP[bkjIndex];
                bRowIndex += bRowStride;
                aColIndex += aColStride;
            }
            result[cijIndex++] = sum;
            bColIndex += bColStride;
        }
        aRowIndex += aRowStride;
    }
    jfloatArray resultData = env->NewFloatArray(nRows * nCols);
    env->SetFloatArrayRegion(resultData, 0, nRows*nCols, result);
    free(result);
    env->ReleaseFloatArrayElements(bData, bDataP, 0);
    env->ReleaseFloatArrayElements(aData, aDataP, 0);
    return resultData;

}

