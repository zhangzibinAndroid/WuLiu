package com.returnlive.app.fragment.me;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.returnlive.app.CertificationActivity;
import com.returnlive.app.MainActivity;
import com.returnlive.app.R;
import com.returnlive.app.SettingActivity;
import com.returnlive.app.ShipperCertificationActivity;
import com.returnlive.app.base.BaseFragment;
import com.returnlive.app.utils.ResourceUtil;
import com.returnlive.app.utils.SaveUtils;
import com.returnlive.app.utils.SystemBarCompat;
import com.zhy.autolayout.AutoRelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by 王建法 on 2017/3/13.
 */

public class MeFragment extends BaseFragment {
    @BindView(R.id.tv_driver)
    TextView tvDriver;
    @BindView(R.id.tv_shipper)
    TextView tvShipper;
    @BindView(R.id.tv_balance)
    TextView tvBalance;
    @BindView(R.id.lay_mycertification)
    AutoRelativeLayout layMycertification;
    @BindView(R.id.lay_set)
    AutoRelativeLayout laySet;
    @BindView(R.id.tv_version)
    TextView tvVersion;
    @BindView(R.id.lay_carmasyer)
    AutoRelativeLayout layCarmasyer;
    @BindView(R.id.lay_shipper)
    AutoRelativeLayout layShipper;
    @BindView(R.id.tv_me_change)
    TextView tvChange;
    private View view;
    public static boolean isChecked = false;//默认司机版
    private MainActivity mainActivity;
    private SaveUtils saveUtils;
    private int change;//切换

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_me, container, false);
        ButterKnife.bind(this, view);

        //设置标题栏和Toolbar颜色一致
        SystemBarCompat.setTranslucentStatusOnKitkat(getActivity());
        SystemBarCompat.setupStatusBarColorAfterLollipop(getActivity(), ResourceUtil.getColor(R.color.colorme_bg, getActivity()));

        saveUtils = new SaveUtils(getActivity());
        mainActivity = (MainActivity) getActivity();
        isChecked = saveUtils.getStatus();
        initView();

        return view;
    }

    private void initView() {
        if (!isChecked) {
            change = 0;
            tvDriver.setTextColor(getResources().getColor(R.color.div_gray));
            tvShipper.setTextColor(getResources().getColor(R.color.gray));
            tvChange.setSelected(false);
            tvChange.setBackground(getResources().getDrawable(R.drawable.sel_me_checke_color));
            layShipper.setVisibility(View.GONE);
            layCarmasyer.setVisibility(View.VISIBLE);
            tvVersion.setText("当前版本：司机版");

        } else {
            change = 1;
            tvShipper.setTextColor(getResources().getColor(R.color.ivory));
            tvDriver.setTextColor(getResources().getColor(R.color.gray));
            tvChange.setSelected(false);
            tvChange.setBackground(getResources().getDrawable(R.drawable.sel_me_checke_color2));
            layShipper.setVisibility(View.VISIBLE);
            layCarmasyer.setVisibility(View.GONE);
            tvVersion.setText("当前版本：货主版");

        }
    }

    @OnClick({R.id.img_driver, R.id.img_shipper, R.id.tv_me_change, R.id.tv_clinchdeal, R.id.tv_alclinchdeal, R.id.tv_receivegoods, R.id.tv_alreadyfinish, R.id.lay_balance, R.id.lay_topup, R.id.lay_mycertification, R.id.lay_set})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_driver:
                if (isChecked) {
                    setShowVersionsDialogPlus("货主版","司机版",false);
                } else {
                    return;
                }

                break;
            case R.id.img_shipper:
                if (!isChecked) {
                    setShowVersionsDialogPlus("司机版","货主版",true);
                } else {
                    return;
                }

                break;
            case R.id.tv_me_change://切换
                tvChange.setSelected(true);
                if (change == 0) {//司机版
                    if (!isChecked) {
                        setShowVersionsDialogPlus("司机版","货主版",true);
                    }
                    change = 1;
                }
                if (change == 1) {//货主版
                    if (isChecked) {
                        setShowVersionsDialogPlus("货主版","司机版",false);
                    }
                    change = 0;
                }
                break;
            case R.id.tv_clinchdeal:
                break;
            case R.id.tv_alclinchdeal:
                break;
            case R.id.tv_receivegoods:
                break;
            case R.id.tv_alreadyfinish:
                break;
            case R.id.lay_balance:
                break;
            case R.id.lay_topup:
                break;
            case R.id.lay_mycertification:
                if (!isChecked) {
                    pageJump(CertificationActivity.class);//司机认证
                } else {
                    pageJump(ShipperCertificationActivity.class);//货主认证
                }

                break;
            case R.id.lay_set:
                pageJump(SettingActivity.class);
                break;
        }
    }

    /**
     * 司机版和货主版切换的对话框
     */
    private void setShowVersionsDialogPlus(final String msgNow, final String msgLater, final boolean isCheck) {
        //header头部
        View viewHeader = LayoutInflater.from(getActivity()).inflate(R.layout.header_version, null);
        TextView tvTitle= (TextView) viewHeader.findViewById(R.id.tv_heard_title);
        tvTitle.setText("当前版本："+msgNow);
        //context内容
        View viewContex = LayoutInflater.from(getActivity()).inflate(R.layout.item_version_me, null);
        TextView tvContext= (TextView) viewContex.findViewById(R.id.tv_heard_context);
        tvContext.setText("您确定要切换为："+msgLater+"？");
        //弹窗
        DialogPlus dialogPlus=DialogPlus.newDialog(getActivity())
                .setContentHolder(new ViewHolder(viewContex))
                .setHeader(viewHeader)
                .setFooter(R.layout.footer_version)//添加脚布局
                .setInAnimation(R.anim.dialog_enter_anim)//类似于IOS底部出现效果
                .setContentBackgroundResource(R.color.lavenderblush)//设置对话框背景颜色为透明（为了边角有圆角弧度）
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        switch (view.getId()) {
                            case R.id.tv_footer_no:
                                dialog.dismiss();
                                break;
                            case R.id.tv_footer_yes:
                                isChecked = isCheck;
                                initView();
                                mainActivity.setBottomTAG();
                                saveUtils.saveStatus(isChecked);
                                dialog.dismiss();
                                break;
                        }
                    }
                })
                .setGravity(Gravity.CENTER)
                .setExpanded(true)
                .setCancelable(true)
                .create();
        dialogPlus.show();
    }
    /**
     * 页面跳转
     */
    private void pageJump(Class<?> cls) {
        Intent intent = new Intent(getActivity(), cls);
        startActivity(intent);
    }


}
