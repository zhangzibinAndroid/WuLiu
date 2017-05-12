package com.returnlive.app.utils;

import android.util.Log;

/**
 * Created by zzb on 2017/3/15.
 */

public class LogUtils {
    private static boolean isLog = false;
    private static String TAG = "TAG";

    public static void Logd(String msg){
        if (isLog){
            Log.d(TAG,msg);
        }
    }



    public static void Logi(String msg){
        if (isLog){
            Log.i(TAG,msg);
        }
    }


    public static void Loge(String msg){
        if (isLog){
            Log.e(TAG,msg);
        }
    }

}
