package com.larack.crosshiddenapi;

import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "CrossHiddenApi-Java";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUi();
    }

    private void initUi() {
        Log.i(TAG, "version : " + Build.VERSION.SDK_INT + " fingerprint: " + Build.FINGERPRINT);

        findViewById(R.id.openCross).setOnClickListener(v -> {
            int ret = CrossUtils.openCrossHiddenApi(MainActivity.this);
            toast("Open Cross Hidden Apis by native " + (ret > 0) + ", result code is " + ret);
        });

        findViewById(R.id.closeCross).setOnClickListener(v -> {
            int ret = CrossUtils.closeCrossHiddenApi(MainActivity.this);
            toast("Close Cross Hidden Apis by native " + (ret > 0) + ", result code is " + ret);
        });

        findViewById(R.id.testReflect).setOnClickListener(v -> {
            toast("Test reflect framework black list, call result is " + getTestResult());
        });

        findViewById(R.id.testAddAssetPath).setOnClickListener(v -> {
            toast("Test reflect AssetManager.addAssetPath with /sdcard/, return is " + addAssetPath("/sdcard/"));
        });
    }

    private void toast(String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        Log.i(TAG, msg);
    }

    private String getTestResult() {
        Class<?> clazz = null;
        String obj = null;
        try {
            clazz = Class.forName("com.android.okhttp.internal.DiskLruCache");
            Field field = clazz.getDeclaredField("JOURNAL_FILE_BACKUP");
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            obj = (String) field.get(null);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static Object addAssetPath(String path) {
        AssetManager assets = null;
        Object callResult = 0;
        try {
            Class<AssetManager> CLASS_ASSET = AssetManager.class;
            Method METHOD_ADD_ASSET = CLASS_ASSET.getDeclaredMethod("addAssetPath", String.class);
            METHOD_ADD_ASSET.setAccessible(true);
            assets = CLASS_ASSET.newInstance();
            callResult = METHOD_ADD_ASSET.invoke(assets, path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "addAssetPath for " + path + " , call result is " + callResult);
        return callResult;
    }
}
