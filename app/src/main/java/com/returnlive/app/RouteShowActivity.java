package com.returnlive.app;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.returnlive.app.utils.AndroidVersion;
import com.returnlive.app.utils.ResourceUtil;
import com.returnlive.app.utils.SystemBarCompat;
import com.returnlive.app.view.PCDPopUpWindow;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.imid.swipebacklayout.lib.SwipeBackLayout;

import static android.R.attr.duration;

public class RouteShowActivity extends com.returnlive.app.SwipeBackActivity {
    @BindView(R.id.ig_back)
    TextView imgBack;
    @BindView(R.id.route_show_toolbar)
    Toolbar routeToolbar;
    @BindView(R.id.edt_route_start)
    Button edtRouteStart;
    @BindView(R.id.edt_route_end)
    Button edtRouteEnd;
    @BindView(R.id.btn_route_no)
    Button btnNo;
    @BindView(R.id.btn_route_yes)
    Button btnYes;

    private PopupWindow pcdPopCityWindow;
    private File file;
    private int index;
    private String startName;
    private String endName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_show);
        ButterKnife.bind(this);
        initDB();

        initToolBarTouchBack();//方法

        //获取EditText控件随时监听动态
        edtRouteStart.addTextChangedListener(textWatcher);
        edtRouteEnd.addTextChangedListener(textWatcher);
    }

    /**
     * 获取EditText控件随时监听动态
     */
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            //设置“完成”背景颜色(控件是否为空)
            if (edtRouteStart.length() == 0 || edtRouteEnd.length() == 0) {
                btnYes.setBackgroundResource(R.color.color_no);
            } else {
                btnYes.setBackgroundResource(R.color.border);
            }
        }
    };

    /**
     * 状态栏颜色和滑动返回
     */
    private void initToolBarTouchBack(){
        //设置一下actionbar
        setSupportActionBar(routeToolbar);
        getSupportActionBar().setTitle("");
        //设置标题栏和Toolbar颜色一致
        SystemBarCompat.setTranslucentStatusOnKitkat(this);
        /**
         * 适用于4.4，在rootView中添加一个与StatusBar高度一样的View，用于对状态栏着色
         */
        if (AndroidVersion.atLeast(19)) {
            ViewGroup viewGroup = (ViewGroup) findViewById(R.id.root_route);
            SystemBarCompat.setupStatusBarView(this, viewGroup, ResourceUtil.getColor(R.color.snow, this));
        }
        //滑动的距离
        SwipeBackLayout swipeBackLayout = getSwipeBackLayout();
        swipeBackLayout.setEdgeSize(400);
        /*swipeBackLayout.addSwipeListener(new SwipeBackLayout.SwipeListener() {
            @Override
            public void onScrollStateChange(int state, float scrollPercent) {
            }

            @Override
            public void onEdgeTouch(int edgeFlag) {//刚滑动时
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                long[] pattern = {
                        0, 20
                };
                vibrator.vibrate(pattern, 1);
            }

            @Override
            public void onScrollOverThreshold() {//滑动一半时

            }
        });*/
    }
    @OnClick({R.id.ig_back, R.id.edt_route_start, R.id.edt_route_end, R.id.btn_route_no, R.id.btn_route_yes})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ig_back:
                finish();
                break;
            case R.id.edt_route_start://起始地
                index = 0;
                showPopUpWindow();
                pcdPopCityWindow.showAsDropDown(edtRouteStart); // 窗口显示的位置
                break;
            case R.id.edt_route_end://到达地
                index = 1;
                showPopUpWindow();
                pcdPopCityWindow.showAsDropDown(edtRouteEnd); // 窗口显示的位置
                break;
            case R.id.btn_route_no://清空
                edtRouteStart.setText("");
                edtRouteEnd.setText("");
                break;
            case R.id.btn_route_yes://完成
                startName = edtRouteStart.getText().toString().trim();
                endName = edtRouteEnd.getText().toString().trim();
                if (startName.equals("") || endName.equals("")) {
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra("startName", startName);
                intent.putExtra("endName", endName);
                setResult(2, intent);
                finish();
                //跳转动画
                overridePendingTransition(R.anim.alpha1,R.anim.scale2);

                break;
        }
    }

    /**
     * 显示三级联动的PopUpWindow
     */
    private void showPopUpWindow() {
        pcdPopCityWindow = new PCDPopUpWindow(this, file, checkChangeListener);
        setPopWindowbackgroundAlpha(0.5f);//设置展示对话框背景变暗
        //设置关闭对话框背景还原
        pcdPopCityWindow.setOutsideTouchable(true);
        pcdPopCityWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setPopWindowbackgroundAlpha(1f);
            }
        });
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void setPopWindowbackgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.alpha = bgAlpha;
        getWindow().setAttributes(params);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    /**
     * 以下是选择item的回调
     *
     * @param text
     */
    PCDPopUpWindow.OnCheckChangeListener checkChangeListener = new PCDPopUpWindow.OnCheckChangeListener() {
        @Override
        public void setCityText(String text) {//城市


                if (index == 0) {
                    edtRouteStart.setText(text);
                } else if (index == 1) {
                    edtRouteEnd.setText(text);
                }

        }

        @Override
        public void setProvinceText(String text) {//省
            if (index == 0) {
                edtRouteStart.setText(text);
            } else if (index == 1) {
                edtRouteEnd.setText(text);
            }
        }

        @Override
        public void setDistrictText(String text) {//县、区
            if (index == 0) {
                edtRouteStart.setText(text);
            } else if (index == 1) {
                edtRouteEnd.setText(text);
            }
            if (PCDPopUpWindow.NUM == 3) {//当等于3时，点击区、县时关闭对话框
                pcdPopCityWindow.dismiss();
            }
        }
    };

    /**
     * 初始化数据库
     * 第一次运行时把数据库文件copy到file目录
     */
    private void initDB() {
        file = new File(getFilesDir(), "city.db");
        if (!file.exists()) {
            new Thread() {

                public void run() {
                    InputStream is = null;
                    FileOutputStream fos = null;
                    try {
                        is = getAssets().open("city.db");
                        fos = new FileOutputStream(file);

                        int len;
                        byte[] buffer = new byte[1024];
                        while ((len = is.read(buffer)) != -1) {
                            fos.write(buffer, 0, len);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {

                        try {
                            if (is != null) {

                                is.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();

                        }
                        try {
                            if (fos != null) {
                                fos.close();

                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }

            }.start();
        }

    }

}
