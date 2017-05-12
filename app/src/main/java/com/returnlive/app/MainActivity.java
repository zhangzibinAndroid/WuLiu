package com.returnlive.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.returnlive.app.fragment.goods.CarGoodsFragment;
import com.returnlive.app.fragment.goods.GoodsFragment;
import com.returnlive.app.fragment.me.MeFragment;
import com.returnlive.app.fragment.options.ReleaseOptionsFragment;
import com.returnlive.app.fragment.options.ReleaseOptionsOnwerFragment;
import com.returnlive.app.fragment.route.RouteDriverFragment;
import com.returnlive.app.fragment.route.RouteOnwerFragment;
import com.returnlive.app.utils.SaveUtils;

import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by 王建法 on 2017/3/13.
 * 让activity继承ParallaxActivityBase即可实现侧滑返回上一页。ParallaxActivityBase是继承AppCompatActivity的
 * 在styles中设置动画属性（系统自带的），可以不用，如果想自定义动画可以用来设置
 * setBackEnable(false);//设置销毁向右滑动返回上一页  在 setContentView(）方法前调用即可
 */
public class MainActivity extends AppCompatActivity {
    @BindViews({R.id.tv_main_goods, R.id.tv_main_route, R.id.tv_main_release_options, R.id.tv_main_me})
    TextView[] tv;

    private GoodsFragment goodsFragment;
    private RouteDriverFragment routeDriverFragment;
    private ReleaseOptionsFragment releaseOptionsFragment;
    private MeFragment meFragment;
    private CarGoodsFragment carGoodsFragment;
    private RouteOnwerFragment routeOnwerFragment;
    private ReleaseOptionsOnwerFragment releaseOptionsOnwerFragment;
    private long exitTime = 0;//点击2次返回，退出程序
    private boolean isFrist = false;
    private Unbinder unbinder;
    private SaveUtils saveUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //解决：当在控件edittext或textView中输入内容弹出软键盘时，解决底部导航栏不会被顶到上面
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);
        saveUtils = new SaveUtils(this);
        initFragment();
        MeFragment.isChecked = saveUtils.getStatus();
        setBottomTAG();


        //设置默认主页,此处顺序不能换
        tv[0].setSelected(true);
        if (tv[0].getText().equals(getResources().getString(R.string.supply_goods))) {
            tv[0].setTextColor(getResources().getColor(R.color.div_gray));
            setReplaceFragment(goodsFragment);
        } else {
            //显示车源的Fragment
            tv[0].setTextColor(getResources().getColor(R.color.ivory));
            setReplaceFragment(carGoodsFragment);
        }
    }

    //初始化fragment
    private void initFragment() {
        goodsFragment = new GoodsFragment();
        routeDriverFragment = new RouteDriverFragment();
        releaseOptionsFragment = new ReleaseOptionsFragment();
        meFragment = new MeFragment();
        carGoodsFragment = new CarGoodsFragment();
        routeOnwerFragment = new RouteOnwerFragment();
        releaseOptionsOnwerFragment = new ReleaseOptionsOnwerFragment();
    }

    public void setBottomTAG() {
        for (int i = 0; i < tv.length; i++) {
            tv[i].setSelected(false);
            if (!MeFragment.isChecked) {
                tv[i].setTextColor(getResources().getColor(R.color.dark_bg));
            } else if (MeFragment.isChecked) {
                tv[i].setTextColor(getResources().getColor(R.color.popup_right_bg));
            }
        }
        if (!MeFragment.isChecked) {
            tv[0].setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.sel_main_goods), null, null);
            tv[0].setText(getResources().getString(R.string.supply_goods));//货源
            tv[1].setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.sel_main_route), null, null);
            tv[1].setText(getResources().getString(R.string.show_route));//路线
            tv[2].setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.sel_main_options), null, null);
            tv[2].setText(getResources().getString(R.string.show_caroptions));//发布车源
            tv[3].setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.sel_main_me), null, null);
            tv[3].setText(getResources().getString(R.string.show_me));//我的
        } else if (MeFragment.isChecked) {
            tv[0].setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.sel_main_goods_onwer), null, null);
            tv[0].setText(getResources().getString(R.string.caroptions));//车源
            tv[1].setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.sel_main_route_onwer), null, null);
            tv[1].setText(getResources().getString(R.string.show_route));//路线
            tv[2].setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.sel_main_options_onwer), null, null);
            tv[2].setText(getResources().getString(R.string.show_supply_goods));//发布货源
            tv[3].setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.sel_main_me_onwer), null, null);
            tv[3].setText(getResources().getString(R.string.show_me));//我的
        }
        if (!isFrist) {
            isFrist = true;
            return;
        } else {
            tv[3].setSelected(true);
            if (!MeFragment.isChecked) {
                tv[3].setTextColor(getResources().getColor(R.color.div_gray));
            } else if (MeFragment.isChecked) {
                tv[3].setTextColor(getResources().getColor(R.color.ivory));
            }
            setReplaceFragment(meFragment);
        }
    }

    /**
     * 底板栏点击动画设置
     */
    @OnClick({R.id.tv_main_goods, R.id.tv_main_route, R.id.tv_main_release_options, R.id.tv_main_me})
    public void onClick(View view) {
        //设置未点击状态为正常颜色效果
        for (int i = 0; i < tv.length; i++) {
            tv[i].setSelected(false);
            if (!MeFragment.isChecked) {
                tv[i].setTextColor(getResources().getColor(R.color.dark_bg));
            } else if (MeFragment.isChecked) {
                tv[i].setTextColor(getResources().getColor(R.color.popup_right_bg));
            }
        }
        switch (view.getId()) {
            case R.id.tv_main_goods:
                tv[0].setSelected(true);
                if (tv[0].getText().equals(getResources().getString(R.string.supply_goods))) {
                    //货源
                    tv[0].setTextColor(getResources().getColor(R.color.div_gray));
                    setReplaceFragment(goodsFragment);
                } else {
                    //显示车源的Fragment
                    tv[0].setTextColor(getResources().getColor(R.color.ivory));
                    setReplaceFragment(carGoodsFragment);
                }
                break;
            case R.id.tv_main_route:
                tv[1].setSelected(true);
                if (!MeFragment.isChecked) {
                    tv[1].setTextColor(getResources().getColor(R.color.div_gray));
                    setReplaceFragment(routeDriverFragment);//司机版路线
                } else if (MeFragment.isChecked) {
                    tv[1].setTextColor(getResources().getColor(R.color.ivory));
                    setReplaceFragment(routeOnwerFragment);//货主版路线
                }
                break;
            case R.id.tv_main_release_options:
                tv[2].setSelected(true);
                if (tv[2].getText().equals(getResources().getString(R.string.show_caroptions))) {
                    //发布车源
                    tv[2].setTextColor(getResources().getColor(R.color.div_gray));
                    setReplaceFragment(releaseOptionsFragment);
                } else {
                    //显示发布货源的Fragment
                    tv[2].setTextColor(getResources().getColor(R.color.ivory));
                    setReplaceFragment(releaseOptionsOnwerFragment);
                }
                break;
            case R.id.tv_main_me://我的
                tv[3].setSelected(true);
                if (!MeFragment.isChecked) {
                    //发布车源
                    tv[3].setTextColor(getResources().getColor(R.color.div_gray));
                } else if (MeFragment.isChecked) {
                    //显示发布货源的Fragment
                    tv[3].setTextColor(getResources().getColor(R.color.ivory));
                }
                setReplaceFragment(meFragment);
                break;
        }
    }


    //点击两次退出
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {//两秒内再次点击返回则退出
                Toast.makeText(getApplicationContext(), "再按一次退出程序",
                        Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 设置替换的Fragment
     */
    private void setReplaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment, fragment).commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
