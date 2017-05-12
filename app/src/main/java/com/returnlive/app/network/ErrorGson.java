package com.returnlive.app.network;

import com.google.gson.Gson;
/**
 * Created by zzb on 2017/4/10.
 */

public class ErrorGson {

    //获取返回code
    public static String gsoncode(String json){
        ErrorJson result = GsonUtil.parseJsonWithGson(json,ErrorJson.class);
        return result.getCode();
    }



    //注册获取返回state
    public static String gsonState(String json){
        ErrorJson result = GsonUtil.parseJsonWithGson(json,ErrorJson.class);
        return result.getState();
    }


}

 class GsonUtil {
    //将Json数据解析成相应的映射对象
    public static <T> T parseJsonWithGson(String jsonData, Class<T> type) {
        Gson gson = new Gson();
        T result = gson.fromJson(jsonData, type);
        return result;
    }

}
