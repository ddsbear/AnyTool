package com.dds.camera;


/**
 * Holds an error with the camera configuration.
 */
public class CameraException extends RuntimeException {


    /**
     * 未知错误
     */
    public static final int REASON_UNKNOWN = 0;


    /**
     * 连接相机失败
     */
    public static final int REASON_FAILED_TO_CONNECT = 1;


    /**
     * 预览失败
     */
    public static final int REASON_FAILED_TO_START_PREVIEW = 2;

    /**
     * 相机被强制关闭
     */
    public static final int REASON_DISCONNECTED = 3;


    /**
     * 拍照失败
     */
    public static final int REASON_PICTURE_FAILED = 4;


    /**
     * 录像失败
     */
    public static final int REASON_VIDEO_FAILED = 5;

    /**
     * 找不到相机
     */
    public static final int REASON_NO_CAMERA = 6;

    private int reason = REASON_UNKNOWN;

    public CameraException(Throwable cause) {
        super(cause);
    }

    public CameraException(Throwable cause, int reason) {
        super(cause);
        this.reason = reason;
    }

    public CameraException(int reason) {
        super();
        this.reason = reason;
    }

    public int getReason() {
        return reason;
    }

    public boolean isUnrecoverable() {
        switch (getReason()) {
            case REASON_FAILED_TO_CONNECT:
                return true;
            case REASON_FAILED_TO_START_PREVIEW:
                return true;
            case REASON_DISCONNECTED:
                return true;
            default:
                return false;
        }
    }
}
