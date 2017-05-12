package com.returnlive.app;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.returnlive.app.base.BaseActivity;
import com.returnlive.app.bean.User;
import com.returnlive.app.network.ErrorJson;
import com.returnlive.app.network.HttpUtilsApi;
import com.returnlive.app.network.ParserUser;
import com.returnlive.app.utils.ActivityUtils;
import com.returnlive.app.utils.ErrorUtils;
import com.returnlive.app.utils.SharePreferencesUtils;
import com.returnlive.app.utils.SystemBarCompat;
import com.zhy.autolayout.AutoRelativeLayout;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.Call;

import static com.returnlive.app.R.id.tv_registered;

public class LoginActivity extends BaseActivity {
    @BindView(R.id.edt_username)
    EditText edtUsername;
    @BindView(R.id.edt_password)
    EditText edtPassword;
    @BindView(R.id.img_loging_delect1)
    ImageView userDelect;
    @BindView(R.id.img_loging_password_delect)
    ImageView passwordDelect;
    @BindView(R.id.rel_login)
    AutoRelativeLayout relLogin;//登录加载布局
    @BindView(R.id.btn_login)
    Button btnLogin;//登录
    private long mTimeRemaining;
    private CountDownTimer mCountDownTimer;
    private static final long COUNTER_TIME = 120;
    private Unbinder unbinder;
    private AlertDialog.Builder dialog;
    private static final String TAG = "TAG";
    private AlertDialog dia;
    private boolean isRegister = false;

    private String phone;
    private String pwd;
    private View viewDilog;
    private ActivityUtils activityUtils;
    private AutoRelativeLayout relResgister;
    private Button btn_reg;

    private String message;
    /**
     * 验证码
     */
    Handler handlerVerificationCode = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //获取请求返回码
            ErrorJson errorJsonn = ParserUser.parserStateCode((String) msg.obj);
            String state = errorJsonn.getState();
            String errorCode = errorJsonn.getCode();
            if (state.equals(ErrorUtils.success)) {
                createTimer(COUNTER_TIME, viewDilog);//验证码倒计时时间方法
                activityUtils.showToast("短信获取成功");
            } else {
                switch (errorCode) {
                    case ErrorUtils.nameEmpity:
                        activityUtils.showToast("手机号不能为空");
                        break;
                    case ErrorUtils.phoneError:
                        activityUtils.showToast("手机号输入有误");
                        break;
                    case ErrorUtils.cendCodeUrlError:
                        activityUtils.showToast("短信接口错误");
                        break;
                    case ErrorUtils.cendCodeStart:
                        activityUtils.showToast("短信已发送");
                        break;
                    case ErrorUtils.cendCodeRceiveError:
                        activityUtils.showToast("短信码获取失败");
                        break;
                }
            }
        }
    };

    /**
     * 注册
     */
    Handler handlerRegister = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String response = (String) msg.obj;
            ErrorJson errorJsonn = ParserUser.parserStateCode(response);
            String state = errorJsonn.getState();
            String errorCode = errorJsonn.getCode();
            if (state.equals(ErrorUtils.success)) {
                activityUtils.showToast("注册成功");

                if (!isRegister) {
                    dia.dismiss();
                }
                isRegister = false;
            } else {
                relResgister.setVisibility(View.GONE);
                btn_reg.setClickable(true);

                switch (errorCode) {
                    case ErrorUtils.nameEmpity:
                        message = "用户名不能为空";
                        break;
                    case ErrorUtils.phoneError:
                        message = "手机号格式错误";
                        break;
                    case ErrorUtils.nameAlreadySaves:
                        message = "手机号码已注册";
                        break;
                    case ErrorUtils.pewEmpity:
                        message = "密码不能为空";
                        break;
                    case ErrorUtils.pewError:
                        message = "密码必须在6-30位字母或数字";
                        break;
                    case ErrorUtils.cendCodeError:
                        message = "验证码错误";
                        break;
                }
                if (relResgister.getVisibility() == View.GONE) {
                    activityUtils.showToast("网络异常，请重试！");
                }
            }
        }
    };
    /**
     * 登录
     */
    Handler handlerLogin = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String response = (String) msg.obj;
            Log.d("TAG", "response: =="+response);
            if (response.contains("success")) {
                User user = ParserUser.parserUser(response);
                String state = user.getState();
                String id = user.getId();
                ParserUser.uid = id;
                try {
                    ParserUser.mSesson = ParserUser.parserSesson(response);
                } catch (JSONException e) {
                    Log.d(TAG, "handleMessage: "+e);
                }
                Log.e(TAG, "mSesson: ========"+ParserUser.mSesson);
                String sessionId = ParserUser.mSesson;
                if (state.equals(ErrorUtils.success)) {
                    //将用户信息保存到本地配置里
                    SharePreferencesUtils.setUser(user);
                    pageJump(MainActivity.class);
                    finish();
                    activityUtils.showToast("登录成功");
                }
            } else if (response.contains("error")) {
                relLogin.setVisibility(View.GONE);
                btnLogin.setClickable(true);//不可点击
                ErrorJson errorJsonn = ParserUser.parserStateCode(response);
                String errorCode = errorJsonn.getCode();
                switch (errorCode) {
                    case ErrorUtils.nameEmpity:
                        message = "用户名不能为空";
                        break;
                    case ErrorUtils.phoneNotRegistered:
                        message = "用户名未注册";
                        break;
                    case ErrorUtils.pewEmpity:
                        message = "密码不能为空";
                        break;
                    case ErrorUtils.pewError:
                        message = "密码必须在6-30位字母或数字";
                        break;
                    case ErrorUtils.pwdError:
                        message = "密码错误";
                        break;
                    case ErrorUtils.loginTimeout:
                        message = "登录超时";
                        break;
                }
                if (relLogin.getVisibility() == View.GONE) {
                    activityUtils.showDialog("忘记密码", message);
                }
            }
        }
    };
    private EditText et_phone;
    private EditText et_password;
    private EditText et_password_again;
    private EditText et_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        unbinder = ButterKnife.bind(this);
        activityUtils = new ActivityUtils(this);
        //设置标题栏和Toolbar颜色一致
        //19
        SystemBarCompat.setTranslucentStatusOnKitkat(this);
        //after
        SystemBarCompat.setTranslucentNavigationAfterKitkat(this);
        SystemBarCompat.setupStatusBarColorAfterLollipop(this, Color.TRANSPARENT);
        initView();
    }

    private void initView() {
        edtUsername.setHintTextColor(Color.argb(125, 255, 255, 255));
        edtPassword.setHintTextColor(Color.argb(125, 255, 255, 255));
        edtUsername.setTextColor(Color.argb(125, 255, 255, 255));
        edtPassword.setTextColor(Color.argb(125, 255, 255, 255));
        //设置控件获取焦点监听
        edtUsername.addTextChangedListener(textWatcher);
        edtPassword.addTextChangedListener(textWatcher);

    }

    /**
     * “登录”控件获取焦点监听
     */
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            /**
             * "登录"设置控件“删除”按钮是否显示(控件是否为空)
             */

            if (edtUsername.length() == 0) {
                userDelect.setVisibility(View.GONE);
            } else {
                userDelect.setVisibility(View.VISIBLE);
                userDelect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        edtUsername.setText("");
                    }
                });
            }
            if (edtPassword.length() == 0) {
                passwordDelect.setVisibility(View.GONE);
            } else {
                passwordDelect.setVisibility(View.VISIBLE);
                passwordDelect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        edtPassword.setText("");
                    }
                });
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    /**
     * "注册"控件获取焦点监听
     */
    private TextWatcher textWatcherRegister = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            /**
             *“注册”的控件设置改变背景
             */
            if (dia.isShowing()) {
                if (et_phone.length() == 0 || et_password.length() == 0 || et_password_again.length() == 0 || et_code.length() == 0) {
//                    btn_reg.setBackgroundResource(R.drawable.shape_color_sh2);
                    btn_reg.setClickable(false);
                } else {
//                    btn_reg.setBackgroundResource(R.drawable.ripple_bg_register);
                    btn_reg.setClickable(true);
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    @OnClick({R.id.btn_login, R.id.tv_forgetpwds, tv_registered})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login://登陆
                setLogin();
                break;
            case R.id.tv_forgetpwds:
                pageJump(ForgetPassWordActivity.class);

                break;
            case tv_registered://注册
                showRegisterDialog();
                break;
        }
    }

    /**
     * "注册"对话框
     */
    private void showRegisterDialog() {
        dialog = new AlertDialog.Builder(this);
        viewDilog = View.inflate(this, R.layout.dialog_register, null);
        Button btn_dismiss = (Button) viewDilog.findViewById(R.id.btn_dismiss);
        btn_reg = (Button) viewDilog.findViewById(R.id.btn_reg);
        //电话号码
        et_phone = (EditText) viewDilog.findViewById(R.id.et_phone);
        //首次输入密码
        et_password = (EditText) viewDilog.findViewById(R.id.et_password);
        //再次输入密码
        et_password_again = (EditText) viewDilog.findViewById(R.id.et_password_again);
        //验证码
        et_code = (EditText) viewDilog.findViewById(R.id.et_code);
        Button btn_code = (Button) viewDilog.findViewById(R.id.btn_code);//获取验证码按钮
        //加载布局
        relResgister = (AutoRelativeLayout) viewDilog.findViewById(R.id.rel_register);
        dialog.setView(viewDilog);

        //设置控件获取焦点监听
        et_phone.addTextChangedListener(textWatcherRegister);
        et_password.addTextChangedListener(textWatcherRegister);
        et_password_again.addTextChangedListener(textWatcherRegister);
        et_code.addTextChangedListener(textWatcherRegister);
//        btn_reg.setBackgroundResource(R.drawable.shape_color_sh2);
        btn_reg.setClickable(false);
        dia = dialog.show();
        btn_dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dia.dismiss();
            }
        });
        //验证码
        btn_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String phone = et_phone.getText().toString().trim();
                if (phone == null || "".equals(phone)) {
                    activityUtils.showToast("手机号不能为空");
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Log.e(TAG, "run: " + phone);
                            OkHttpUtils.post().url(HttpUtilsApi.httpUrl + HttpUtilsApi.sendCodeUrl)
                                    .addParams("phone", phone)
                                    .build().execute(new StringCallback() {
                                @Override
                                public void onError(Call call, Exception e, int id) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            activityUtils.showToast("网络异常，请重试");
                                        }
                                    });
                                }

                                @Override
                                public void onResponse(final String response, int id) {
                                    Log.e(TAG, "验证码获取: " + response + " -----" + id);
                                    Message msg = new Message();
                                    msg.obj = response;
                                    handlerVerificationCode.sendMessage(msg);
                                }
                            });

                        }
                    }).start();
                }
            }
        });
        //注册
        btn_reg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                phone = et_phone.getText().toString().trim();
                pwd = et_password.getText().toString().trim();
                String pwdagain = et_password_again.getText().toString().trim();
                final String msgcode = et_code.getText().toString().trim();
                if (phone == null || "".equals(phone)) {
                    activityUtils.showToast("手机号不能为空");
                } else if (!pwd.equals(pwdagain)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            activityUtils.showToast("两次密码不一致，请重新输入");
                        }
                    });
                } else {
                    relResgister.setVisibility(View.VISIBLE);
                    btn_reg.setClickable(false);//不可点击
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            OkHttpUtils.post().url(HttpUtilsApi.httpUrl + HttpUtilsApi.registerUrl)
                                    .addParams("phone", phone)
                                    .addParams("pwd", pwd)
                                    .addParams("msgcode", msgcode)
                                    .build()
                                    .execute(new StringCallback() {
                                        @Override
                                        public void onError(Call call, final Exception e, int id) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    relResgister.setVisibility(View.GONE);
                                                    btn_reg.setClickable(true);
                                                    if (relResgister.getVisibility() == View.GONE) {
                                                        activityUtils.showToast("网络异常，请重试！");
                                                    }
                                                }
                                            });
                                        }

                                        @Override
                                        public void onResponse(String response, int id) {
                                            Log.e(TAG, "注册: " + response + " -----" + id);
                                            Message msg = new Message();
                                            msg.obj = response;
                                            handlerRegister.sendMessage(msg);

                                        }
                                    });
                        }
                    }).start();
                }
            }
        });
    }

    /**
     * 登陆
     */
    private void setLogin() {
        final String userName = edtUsername.getText().toString().trim();
        final String passWord = edtPassword.getText().toString().trim();
        if (userName.length() == 0) {
            activityUtils.showToast("用户名不能为空");
        } else if (passWord.length() == 0) {
            activityUtils.showToast("密码不能为空");
        } else {
            relLogin.setVisibility(View.VISIBLE);
            btnLogin.setClickable(false);//不可点击
            new Thread(new Runnable() {
                @Override
                public void run() {
                    OkHttpUtils.post().url(HttpUtilsApi.httpUrl + HttpUtilsApi.loginUrl)
                            .addParams("phone", userName)
                            .addParams("pwd", passWord)
                            .build().execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    relLogin.setVisibility(View.GONE);
                                    btnLogin.setClickable(true);//不可点击
                                    if (relLogin.getVisibility() == View.GONE) {
                                        activityUtils.showDialog("忘记密码", "网络异常，请重试！");
                                    }
                                }
                            });
                        }

                        @Override
                        public void onResponse(final String response, int id) {
                            Log.e(TAG, "登入: " + response + " -----" + id);

                            Message msg = new Message();
                            msg.obj = response;
                            handlerLogin.sendMessage(msg);
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
     * @param view 获取的布局控件
     */
    private void createTimer(long time, View view) {
        final Button btn_code = (Button) view.findViewById(R.id.btn_code);
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
        mCountDownTimer = new CountDownTimer(time * 1000, 50) {
            @Override
            public void onTick(long millisUnitFinished) {
                mTimeRemaining = ((millisUnitFinished / 1000) + 1);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
