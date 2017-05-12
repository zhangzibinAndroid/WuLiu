package com.returnlive.app;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.returnlive.app.base.BaseActivity;
import com.returnlive.app.utils.ResourceUtil;
import com.returnlive.app.utils.SystemBarCompat;
import com.returnlive.app.versionUpdate.UpdateInfo;
import com.returnlive.app.versionUpdate.UpdateInfoService;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.Call;


public class SettingActivity extends BaseActivity {

    @BindView(R.id.set_toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_modify_login_password)
    TextView tvModifyLoginPassword;
    @BindView(R.id.tv_modify_pay_password)
    TextView tvModifyPayPassword;
    @BindView(R.id.chb_open_voice_broadcast_remind)
    CheckBox chbOpenVoiceBroadcastRemind;
    @BindView(R.id.lay_voice_broadcast_time)
    AutoLinearLayout layVoiceBroadcastTime;
    @BindView(R.id.lay_help)
    AutoLinearLayout layHelp;
    @BindView(R.id.lay_version)
    AutoLinearLayout layVersion;
    @BindView(R.id.tv_about)
    TextView tvAbout;
    @BindView(R.id.tv_exit)
    TextView tvExit;
    @BindView(R.id.tv_version)
    TextView tvVersion;
    private Unbinder unbinder;
    // 更新版本要用到的一些信息
    private UpdateInfo info;
    private ProgressDialog pBar;
    private boolean isLoading= false;//防止重复加载

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setting);
        unbinder = ButterKnife.bind(this);
        //设置标题栏和Toolbar颜色一致
        SystemBarCompat.setTranslucentStatusOnKitkat(this);
        SystemBarCompat.setupStatusBarColorAfterLollipop(this, ResourceUtil.getColor(R.color.pink, this));

        initView();
    }

    /**
     * 初始化配置
     */
    private void initView() {
        toolbar.setTitleTextColor(getResources().getColor(R.color.write));
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        //给左上角加一个返回图标,需要重写菜单点击事件，否则点击无效
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                UpdateInfoService updateInfoService = new UpdateInfoService(
                        SettingActivity.this);
                try {
                    info = updateInfoService.getUpDateInfo();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (isNeedUpdate()){
                                    tvVersion.setText("有新版本" + info.getVersion()+",请更新体验");
                                }else {
                                    tvVersion.setText(getResources().getString(R.string.version_newest));
                                }
                            }
                        });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //给左上角加一个返回图标,需要重写菜单点击事件，否则点击无效
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        isLoading = false;
    }

    @OnClick({R.id.tv_modify_login_password, R.id.tv_modify_pay_password, R.id.chb_open_voice_broadcast_remind, R.id.lay_voice_broadcast_time, R.id.lay_help, R.id.lay_version, R.id.tv_about, R.id.tv_exit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_modify_login_password://修改登录密码
                break;
            case R.id.tv_modify_pay_password://修改支付密码
                break;
            case R.id.chb_open_voice_broadcast_remind://开启语音播报提醒
                break;
            case R.id.lay_voice_broadcast_time://语音播放时间
                break;
            case R.id.lay_help://帮助中心
                pageJump(HelpCenterActivity.class);
                break;
            case R.id.lay_version://版本更新
                if (!isLoading){
                    versionUpdata();
                }

                break;
            case R.id.tv_about://关于
                pageJump(AboutActivity.class);
                break;
            case R.id.tv_exit://退出账号
                break;
        }
    }



    private void versionUpdata() {
        isLoading = true;
        new Thread() {
            public void run() {
                try {
                    UpdateInfoService updateInfoService = new UpdateInfoService(
                            SettingActivity.this);
                    info = updateInfoService.getUpDateInfo();
                    handler1.sendEmptyMessage(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            ;
        }.start();
    }

    @SuppressLint("HandlerLeak")
    private Handler handler1 = new Handler() {
        public void handleMessage(Message msg) {
            // 如果有更新就提示
            if (isNeedUpdate()) {
                showUpdateDialog();
            }
        }

        ;
    };


    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setTitle("有新版本" + info.getVersion());
        builder.setMessage(info.getDescription());
        builder.setCancelable(false);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED)) {
                    downFile(info.getUrl());
                } else {
                    Toast.makeText(SettingActivity.this, "SD卡不可用，请插入SD卡",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                isLoading = false;

            }

        });
        builder.create().show();
    }

    private boolean isNeedUpdate() {
        String v = info.getVersion(); // 最新版本的版本号
        if (v.equals(getVersion())) {
            return false;
        } else {
            return true;
        }
    }

    // 获取当前版本的版本号
    private String getVersion() {
        try {
            PackageManager packageManager = getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "版本号未知";

        }
    }

    void downFile(final String url) {
        pBar = new ProgressDialog(SettingActivity.this);    //进度条，在下载的时候实时更新进度，提高用户友好度
        pBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pBar.setTitle("正在下载");
        pBar.setMessage("请稍候...");
        pBar.setProgress(0);
        pBar.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpUtils.get().url(url).build().execute(new FileCallBack(Environment.getExternalStorageDirectory() + "/wuliu", "wuliu.apk") {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SettingActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(File response, int id) {
                        down();
                    }


                    @Override
                    public void inProgress(float progress, long total, int id) {
                        super.inProgress(progress, total, id);
                        int mtotal = (int) total;
                        pBar.setMax(mtotal);
                        pBar.setProgress((int) (mtotal * progress));
                    }
                });


            }
        }).start();
    }

    void down() {
        handler1.post(new Runnable() {
            public void run() {
                pBar.cancel();
                update();
            }
        });
    }

    void update() {
        isLoading = false;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(Environment
                        .getExternalStorageDirectory() + "/wuliu", "wuliu.apk")),
                "application/vnd.android.package-archive");
        startActivity(intent);
    }


}
