package com.cylinder.www.facedetect;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;

public class DetectionBasedTracker {
	public DetectionBasedTracker(String cascadeName, int minFaceSize) {
		mNativeObj = nativeCreateObject(cascadeName, minFaceSize);
	}

	public void start() {
		nativeStart(mNativeObj);
	}

	public void stop() {
		nativeStop(mNativeObj);
	}

	public void setMinFaceSize(int size) {
		nativeSetFaceSize(mNativeObj, size);
	}

	public void detect(Mat imageGray, MatOfRect faces) {
		nativeDetect(mNativeObj, imageGray.getNativeObjAddr(), faces.getNativeObjAddr());
	}

	public void release() {
		nativeDestroyObject(mNativeObj);
		mNativeObj = 0;
	}

	public long InitWriter(String fileName, int w, int h) {
		return nativeInitWriter(fileName, w, h);
	}

	public int WriteFrame(long write, Mat frame) {
		return nativeWriteFrame(write, frame.getNativeObjAddr());
	}

	public void ReleaseWriter(long write) {
		nativeReleaseWriter(write);
	}

	private long mNativeObj = 0;

	private static native long nativeCreateObject(String cascadeName, int minFaceSize);

	private static native void nativeDestroyObject(long thiz);

	private static native void nativeStart(long thiz);

	private static native void nativeStop(long thiz);

	private static native void nativeSetFaceSize(long thiz, int size);

	private static native void nativeDetect(long thiz, long inputImage, long faces);

	private static native long nativeInitWriter(String fileName, int w, int h);

	private static native int nativeWriteFrame(long write, long frame);

	private static native void nativeReleaseWriter(long write);
}
