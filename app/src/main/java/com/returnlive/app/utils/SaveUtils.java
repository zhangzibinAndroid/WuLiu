package com.returnlive.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by zzb on 2017/4/6.
 */

public class SaveUtils {
    Context mcontext;

    public SaveUtils(Context mcontext) {
        this.mcontext = mcontext;
    }

    public void saveStatus(boolean isChecked) {
        SharedPreferences saveStatus =mcontext.getSharedPreferences("Status", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = saveStatus.edit();
        editor.putBoolean("saveStatus", isChecked);
        editor.commit();
    }

    public boolean getStatus(){
        SharedPreferences isChecked =mcontext.getSharedPreferences("Status", Context.MODE_PRIVATE);
        boolean ischecked = isChecked.getBoolean("saveStatus",false);
        return ischecked;
    }
}
