package com.returnlive.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.returnlive.app.utils.AndroidVersion;
import com.returnlive.app.utils.ResourceUtil;
import com.returnlive.app.utils.SystemBarCompat;
import com.returnlive.app.view.RoundImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.imid.swipebacklayout.lib.SwipeBackLayout;

public class GoodsDetailsActivity extends SwipeBackActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.ig_goddetail_back)
    TextView igBack;
    @BindView(R.id.img_share)
    ImageView imgShare;
    @BindView(R.id.tv_goods_details_start)
    TextView tvStart;
    @BindView(R.id.tv_goods_details_start_context)
    TextView tvStartDistance;//距离开始
    @BindView(R.id.tv_goods_details_end)
    TextView tvEnd;
    @BindView(R.id.btn_show_navigation)
    Button btnShowNavigation;//导航
    @BindView(R.id.tv_goods_details_end_context)
    TextView tvEndDistance;//距离目的地
    @BindView(R.id.tv_complaints)
    TextView tvDetailsComplaints;//投诉
    @BindView(R.id.tv_goods_details_time)
    TextView tvDetailsTime;//时间
    @BindView(R.id.tv_goods_details_goods)
    TextView tvDetailsGoodsType;//货品
    @BindView(R.id.tv_goods_details_specifications)
    TextView tvDetailsSpecifications;//规格
    @BindView(R.id.tv_goods_details_cartype)
    TextView tvDetailsCartype;//车型
    @BindView(R.id.tv_goods_details_message)
    TextView tvDetailsMessage;//留言
    @BindView(R.id.img_goods_details_icon)
    RoundImageView imgPersonalIcon;//头像
    @BindView(R.id.tv_goods_phone)
    TextView tvPersonalPhone;//电话
    @BindView(R.id.tv_goods_details_title)
    TextView tvPersonalDetails;//信用说明
    @BindView(R.id.img_goods_callphone)
    Button imgPersonalComents;//查看评价
    @BindView(R.id.btn_details_pay_deposit)
    Button btnPayDeposit;//支付定金
    @BindView(R.id.btn_details_Contact_owner)
    Button btnContactOwner;//联系货主

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_details);
        ButterKnife.bind(this);
        initView();
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
            ViewGroup viewGroup = (ViewGroup) findViewById(R.id.goods_details);
            SystemBarCompat.setupStatusBarView(this, viewGroup, ResourceUtil.getColor(R.color.snow, this));
        }

        //滑动的距离
        SwipeBackLayout swipeBackLayout = getSwipeBackLayout();
        swipeBackLayout.setEdgeSize(400);
       /* swipeBackLayout.addSwipeListener(new SwipeBackLayout.SwipeListener() {
            @Override
            public void onScrollStateChange(int state, float scrollPercent) {
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                long[] pattern = {
                        0, 10
                };
                vibrator.vibrate(pattern, 1);
            }

            @Override
            public void onEdgeTouch(int edgeFlag) {//刚滑动时

            }

            @Override
            public void onScrollOverThreshold() {//滑动一半时

            }
        });*/
    }

    @OnClick({R.id.ig_goddetail_back, R.id.img_share, R.id.btn_show_navigation, R.id.tv_complaints,R.id.img_goods_callphone, R.id.btn_details_pay_deposit, R.id.btn_details_Contact_owner})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ig_goddetail_back://返回
                finish();
                break;
            case R.id.img_share://分享
                break;
            case R.id.btn_show_navigation://导航
                break;
            case R.id.tv_complaints://投诉
                break;
            case R.id.img_goods_callphone://查看评价
                break;
            case R.id.btn_details_pay_deposit://支付定金
                startActivity(new Intent(GoodsDetailsActivity.this,GoodsPayActivity.class));
                break;
            case R.id.btn_details_Contact_owner://联系货主
                break;
        }
    }

}
