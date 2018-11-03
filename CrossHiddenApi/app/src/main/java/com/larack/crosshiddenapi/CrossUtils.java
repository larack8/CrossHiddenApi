package com.larack.crosshiddenapi;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.util.Log;

public class CrossUtils {

    public static final String TAG = "CrossHiddenApi-Java";

    private static final int ERR_CODE_UNKNOW = -9999;

    private static final int ERR_CROSS_OK = 100;

    private static final int ERR_BUILD_SDK_LOW = -110;

    private static final int ERR_NULL_CONTEXT = -120;

    private static int mCrossResult = ERR_CODE_UNKNOW;

    private static native int openCrossHiddenApiNative(int targetSdkVersion);

    private static native int closeCrossHiddenApiNative(int targetSdkVersion);

    static {
        System.loadLibrary("native-lib");
    }

    public static int openCrossHiddenApi(Context context) {
        return crossHiddenApi(context, true);
    }

    public static int closeCrossHiddenApi(Context context) {
        return crossHiddenApi(context, false);
    }

    public static int crossHiddenApi(Context context, boolean open) {
        mCrossResult = checkCanCross(context, open);

        if (mCrossResult >= 0) {
            ApplicationInfo applicationInfo = context.getApplicationInfo();
            int targetSdkVersion = applicationInfo.targetSdkVersion;

            synchronized (CrossUtils.class) {
                if (open) {
                    mCrossResult = openCrossHiddenApiNative(targetSdkVersion);
                } else {
                    mCrossResult = closeCrossHiddenApiNative(targetSdkVersion);
                }
                mCrossResult = ERR_CROSS_OK;
            }
        }
        return mCrossResult;
    }

    public static int checkCanCross(Context context, boolean open) {
        int checkResult = ERR_CROSS_OK;
        if (Build.VERSION.SDK_INT < 28) {
            // Below Android P, ignore
            Log.d(TAG, (open ? "open" : "close") + " checkCanCross, Below Android P, ignore cross!");
            checkResult = ERR_BUILD_SDK_LOW;
        } else if (context == null) {
            Log.d(TAG, (open ? "open" : "close") + "checkCanCross, context is null!");
            checkResult = ERR_NULL_CONTEXT;
        }
        return checkResult;
    }

}
