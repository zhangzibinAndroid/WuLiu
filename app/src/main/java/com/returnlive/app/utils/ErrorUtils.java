package com.returnlive.app.utils;

/**
 * @author 张梓彬
 * @// TODO: 2017/4/10 返回码
 */

public class ErrorUtils {
    public static final String success = "success";//成功状态


    public static final String nameEmpity = "-11101";//用户名为空
    public static final String nameAlreadySaves = "-11102";//手机号码已注册
    public static final String phoneError = "-11103";//手机号格式错误
    public static final String pewEmpity = "-11104";//密码为空
    public static final String pewError = "-11105";//密码应在6-30位之间
    public static final String cendCodeError = "-11106";//验证码错误
    public static final String cendCodeStart = "-11107";//短信已发送
    public static final String cendCodeUrlError = "-11108";//短信接口错误
    public static final String cendCodeRceiveError = "-11109";//短信码获取失败
    public static final String phoneNotRegistered = "-11110";//用户名未注册
    public static final String pwdError = "-11111";//密码错误
    public static final String shipperAuthenticationFailed = "-11112";//货主认证失败
    public static final String optionsAuthenticationFailed = "-11113";//车源认证失败
    public static final String userDetailsObtainFailed= "-11114";//用户详情获取失败
    public static final String illegalRequest= "-11201";//非法请求
    public static final String loginTimeout= "-11202";//登录超时 24小时内无任何操作
    public static final String anotherPlaceLoing= "-11203";//异地登录
    public static final String ExitFailed= "-11204";//退出失败
    public static final String goodsoptionsInformationAddFailed = "-11301";//货源OR车源信息添加失败
    public static final String goodsoptionsListObtainFailed= "-11302";//货源OR车源列表获取失败
    public static final String goodsoptionsIDEmpityl= "-11303";//	货源OR车源id不可为空
    public static final String goodsoptionsDetailsObbtainFailed= "-11304";//货源OR车源详情获取失败



}
