package com.returnlive.app.network;

/**
 * Created by 王建法 on 2017/4/27.
 *
 * URL接口
 */

public class HttpUtilsApi {

    public static final String httpUrl = "http://wuliu.returnlive.com/mobile/";
    //注册
    public static final String registerUrl = "User/register";
    public static final String sendCodeUrl = "User/sendCode";
    //登录
    public static final String loginUrl="User/login";
    //忘记密码
    public static final String forgetPwdUrl="User/resetPwd";
    //车主认证
    public static  final String onwerCertification="Carsource/auth";
    //用户详情
    public static final String userDetails="";

    //货主认证
    public static final String shipperCertification="Goods/auth/uid/";
}
