package com.returnlive.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.returnlive.app.base.BaseActivity;
import com.returnlive.app.network.ErrorJson;
import com.returnlive.app.network.HttpUtilsApi;
import com.returnlive.app.network.ParserUser;
import com.returnlive.app.utils.ActivityUtils;
import com.returnlive.app.utils.ErrorUtils;
import com.returnlive.app.utils.ResourceUtil;
import com.returnlive.app.utils.SystemBarCompat;
import com.zhy.autolayout.AutoRelativeLayout;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;


public class ForgetPassWordActivity extends BaseActivity {

    @BindView(R.id.ftoolbar)
    Toolbar toolbar;
    @BindView(R.id.edt_phone)
    EditText edtPhone;
    @BindView(R.id.edt_code)
    EditText edtCode;
    @BindView(R.id.edt_new_pwds)
    EditText edtNewPwds;
    @BindView(R.id.edt_pwds_ok)
    EditText edtPwdsOk;
    @BindView(R.id.btn_ok)
    Button btnOk;

    @BindView(R.id.activity_forget_pass_word)
    AutoRelativeLayout activityForgetPassWord;
    @BindView(R.id.rel_loading)
    AutoRelativeLayout relLoading;//加载页面

    private CountDownTimer mCountDownTimer;
    private static final long COUNTER_TIME = 120;
    private Button btn_code;
    private String TAG = "ForgetPassWordActivity";
    private String messsage;
    /**
     * 验证码
     */
    private Handler handlerCode = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String response = (String) msg.obj;
            //获取请求返回码
            ErrorJson errorJsonn = ParserUser.parserStateCode(response);
            String state = errorJsonn.getState();
            String errorCode = errorJsonn.getCode();
            if (state.equals(ErrorUtils.success)) {
                createTimer(COUNTER_TIME);//验证码倒计时时间方法
                activityUtils.showToast("短信获取成功");
            } else {
                switch (errorCode) {
                    case ErrorUtils.nameEmpity:
                        messsage = "手机号不能为空";
                        break;
                    case ErrorUtils.phoneNotRegistered:
                        messsage = "手机号未注册";
                        break;
                    case ErrorUtils.phoneError:
                        messsage = "手机号输入有误";
                        break;
                    case ErrorUtils.cendCodeUrlError:
                        messsage = "短信接口错误";
                        break;
                    case ErrorUtils.cendCodeStart:
                        messsage = "短信已发送";
                        break;
                    case ErrorUtils.cendCodeRceiveError:
                        messsage = "短信码获取失败";
                        break;
                }
                activityUtils.showDialog("忘记密码", messsage);

            }

        }
    };

    /**
     * 忘记密码修改
     */
    private Handler handlerForgetPwd = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String response = (String) msg.obj;
            //获取请求返回码
            ErrorJson errorJsonn = ParserUser.parserStateCode(response);
            String state = errorJsonn.getState();
            String errorCode = errorJsonn.getCode();

            if (state.equals(ErrorUtils.success)) {
                activityUtils.showToast("密码修改成功");
                startActivity(new Intent(ForgetPassWordActivity.this, LoginActivity.class));
                finish();
            } else {
                relLoading.setVisibility(View.GONE);//隐藏加载页面
                btnOk.setClickable(true);//可点击

                switch (errorCode) {
                    case ErrorUtils.nameEmpity:
                        messsage = "手机号不能为空";
                        break;
                    case ErrorUtils.phoneNotRegistered:
                        messsage = "手机号未注册";
                        break;
                    case ErrorUtils.phoneError:
                        messsage = "手机号输入有误";
                        break;
                    case ErrorUtils.pewEmpity:
                        messsage = "密码不能为空";
                        break;
                    case ErrorUtils.pewError:
                        messsage = "密码必须在6";
                        break;
                    case ErrorUtils.cendCodeError:
                        messsage = "验证码错误";
                        break;
                }
                if (relLoading.getVisibility() == View.GONE) {
                    activityUtils.showDialog("忘记密码", messsage);
                }
            }

        }
    };
    private ActivityUtils activityUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pass_word);
        ButterKnife.bind(this);

        activityUtils = new ActivityUtils(this);
        //获取验证码按钮
        btn_code = (Button) findViewById(R.id.btn_codenumber);

        setSupportActionBar(toolbar);
        //给左上角加一个返回图标,需要重写菜单点击事件，否则点击无效
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //设置标题栏和Toolbar颜色一致
        SystemBarCompat.setTranslucentStatusOnKitkat(this);
        SystemBarCompat.setupStatusBarColorAfterLollipop(this, ResourceUtil.getColor(R.color.snow, this));

        //设置控件获取焦点监听
        edtPhone.addTextChangedListener(textWatcher);
        edtCode.addTextChangedListener(textWatcher);
        edtNewPwds.addTextChangedListener(textWatcher);
        edtPwdsOk.addTextChangedListener(textWatcher);
        btnOk.setBackgroundResource(R.drawable.blue_white_bg);
        btnOk.setClickable(false);
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            if (edtPhone.length() == 0 || edtCode.length() == 0 || edtNewPwds.length() == 0 || edtPwdsOk.length() == 0) {
                btnOk.setBackgroundResource(R.drawable.blue_white_bg);
                btnOk.setClickable(false);
            } else {
                btnOk.setBackgroundResource(R.drawable.blue_dep_bg);
                btnOk.setClickable(true);

            }

        }
    };

    @OnClick({R.id.btn_codenumber, R.id.btn_ok})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_codenumber://获取验证码
                final String phone = edtPhone.getText().toString().trim();
                if (phone.length() == 0) {
                    activityUtils.showDialog("忘记密码", "手机号不能为空");
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            OkHttpUtils.post().url(HttpUtilsApi.httpUrl + HttpUtilsApi.sendCodeUrl)
                                    .addParams("phone", phone)
                                    .addParams("type", "2")//短信获取接口 type：1 注册短信获取 (默认)  2 ：密码修改短信获取
                                    .build().execute(new StringCallback() {
                                @Override
                                public void onError(Call call, Exception e, int id) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            activityUtils.showDialog("忘记密码", "网络异常，请重试！");
                                        }
                                    });
                                }

                                @Override
                                public void onResponse(String response, int id) {
                                    Log.e(TAG, "onResponse: 忘记密码  验证码  ：" + response);
                                    Message msg = new Message();
                                    msg.obj = response;
                                    handlerCode.sendMessage(msg);
                                }
                            });

                        }
                    }).start();
                }
                break;
            case R.id.btn_ok://忘记密码修改确定
                setForgetPwdChange();
                break;
        }
    }

    /**
     * 忘记密码修改
     */
    private void setForgetPwdChange() {
        final String phone = edtPhone.getText().toString().trim();
        final String msgcode = edtCode.getText().toString().trim();
        final String pwd = edtNewPwds.getText().toString().trim();
        String pwdagain = edtPwdsOk.getText().toString().trim();
        if (phone.length() == 0) {
            activityUtils.showDialog("忘记密码", "手机号不能为空");
        } else if (!pwd.equals(pwdagain)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activityUtils.showDialog("忘记密码", "两次密码不一致，请重新输入");
                }
            });
        } else {
            //显示加载页面(不能再线程里执行)
            relLoading.setVisibility(View.VISIBLE);
            btnOk.setClickable(false);//不可点击
            new Thread(new Runnable() {
                @Override
                public void run() {
                    OkHttpUtils.post().url(HttpUtilsApi.httpUrl + HttpUtilsApi.forgetPwdUrl)
                            .addParams("phone", phone)
                            .addParams("msgcode", msgcode)
                            .addParams("pwd", pwd)
                            .build().execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            relLoading.setVisibility(View.GONE);//隐藏加载页面
                            btnOk.setClickable(true);//可点击
                            if (relLoading.getVisibility() == View.GONE) {
                                activityUtils.showDialog("忘记密码", "网络异常，请重试！");
                            }
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            Log.e(TAG, "onResponse: 忘记密码  修改  ：" + response);
                            Message msg = new Message();
                            msg.obj = response;
                            handlerForgetPwd.sendMessage(msg);
                        }
                    });
                }
            }).start();
        }

    }

    /**
     * 验证码获取时间
     *
     * @param time 验证码获取的总时间
     */
    private void createTimer(long time) {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
        mCountDownTimer = new CountDownTimer(time * 1000, 50) {
            @Override
            public void onTick(long millisUnitFinished) {
                long mTimeRemaining = ((millisUnitFinished / 1000) + 1);
                btn_code.setText(mTimeRemaining + "后重新获取");
                btn_code.setClickable(false);
                btn_code.setBackground(getResources().getDrawable(R.drawable.shape_color_pwd));
            }

            @Override
            public void onFinish() {
                //重新获取
                btn_code.setText(getResources().getString(R.string.restart_getcode));
                btn_code.setClickable(true);
                btn_code.setBackground(getResources().getDrawable(R.drawable.shape_color_pwd2));
            }
        };
        mCountDownTimer.start();
    }

    //给左上角加一个返回图标,需要重写菜单点击事件，否则点击无效
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        return super.onOptionsItemSelected(item);
    }


}
