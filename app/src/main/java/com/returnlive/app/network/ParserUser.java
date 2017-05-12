package com.returnlive.app.network;


import com.google.gson.Gson;
import com.returnlive.app.bean.User;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 王建法 on 2017/4/27.
 * <p>
 * 解析Json数据
 */
public class ParserUser {
    public static String uid;
    public static String zSesson = "z_session_";
    public static String mSesson;

    //---------------------------失败返回的数据  ErrorJson实体---------------------------------

    //注册获取返回state
    public static ErrorJson parserStateCode(String json) {
        ErrorJson result = GsonUtil.parseJsonWithGson(json, ErrorJson.class);
        return result;
    }

    //--------------------------成功返回的数据---------------------------------
    //登入获取的User对象    Gson解析
    public static User parserUser(String json) {
        User result = GsonUtils.parseJsonWithGson(json, User.class);
        return result;
    }

    public static String parserSesson(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        User user = new User();
        user.setZ_session_id(jsonObject.getString(zSesson+uid));
        return user.getZ_session_id();
    }
}






class GsonUtils {
    //将Json数据解析成相应的映射对象
    public static <T> T parseJsonWithGson(String jsonData, Class<T> type) {
        Gson gson = new Gson();
        T result = gson.fromJson(jsonData, type);
        return result;
    }
}