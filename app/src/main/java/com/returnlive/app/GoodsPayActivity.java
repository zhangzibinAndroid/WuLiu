package com.returnlive.app;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.returnlive.app.utils.AndroidVersion;
import com.returnlive.app.utils.ResourceUtil;
import com.returnlive.app.utils.SystemBarCompat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.imid.swipebacklayout.lib.SwipeBackLayout;

public class GoodsPayActivity extends SwipeBackActivity {

    @BindView(R.id.ig_pay_back)
    TextView igBack;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.img_certfiction_icon)
    ImageView imgPayIcon;//头像
    @BindView(R.id.tv_pay_start)
    TextView tvPayStart;
    @BindView(R.id.tv_pay_end)
    TextView tvPayEnd;
    @BindView(R.id.tv_pay_name)
    TextView tvPayName;//姓名
    @BindView(R.id.tv_input_money)
    EditText tvInputMoney;//输入金额
    @BindView(R.id.tv_security_money)
    TextView tvSecurityMoney;//保障金额
    @BindView(R.id.tv_security)
    TextView tvSecurityDetails;//保障详情
    @BindView(R.id.check_box_security)
    CheckBox ckBoxSecurity;//保障选择
    @BindView(R.id.tv_pay_balance)
    TextView tvPayBalance;//可用余额
    @BindView(R.id.check_box_balance)
    CheckBox cBoxBalance;//余额选择
    @BindView(R.id.tv_pay_remaining)
    TextView tvPayRemaining;//支付

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_pay);
        ButterKnife.bind(this);
        initView();
        //设置弹出数字输入框
        tvInputMoney.setInputType(EditorInfo.TYPE_CLASS_PHONE);


    }
    private void initView() {
        //设置一下actionbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        //设置标题栏和Toolbar颜色一致
        SystemBarCompat.setTranslucentStatusOnKitkat(this);
        /**
         * 适用于4.4，在rootView中添加一个与StatusBar高度一样的View，用于对状态栏着色
         */
        if (AndroidVersion.atLeast(19)) {
            //用于添加着色View的根View
            ViewGroup viewGroup = (ViewGroup) findViewById(R.id.root_pay);
            SystemBarCompat.setupStatusBarView(this, viewGroup, ResourceUtil.getColor(R.color.tomato, this));
        }

        //设置状态栏字体的颜色
        SystemBarCompat.setFlymeStatusBarDarkIcon(this,true);
        //滑动的距离
        SwipeBackLayout swipeBackLayout = getSwipeBackLayout();
        swipeBackLayout.setEdgeSize(400);
       /* swipeBackLayout.addSwipeListener(new SwipeBackLayout.SwipeListener() {
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

    @OnClick({R.id.ig_pay_back,R.id.tv_pay_remaining})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ig_pay_back:
                finish();
                break;
            case R.id.tv_pay_remaining://支付
                break;
        }
    }

}
