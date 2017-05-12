package com.returnlive.app.fragment.goods;


import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.lib.WheelView;
import com.bigkoo.pickerview.listener.CustomListener;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.returnlive.app.R;
import com.returnlive.app.adapter.CarGoodsAdapter;
import com.returnlive.app.adapter.GoodsAdapter;
import com.returnlive.app.utils.ResourceUtil;
import com.returnlive.app.utils.SystemBarCompat;
import com.returnlive.app.view.PCDPopUpWindow;
import com.zhy.autolayout.AutoRelativeLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zzb on 2017/4/6.
 */

public class CarGoodsFragment extends Fragment {
    private View view;
    @BindView(R.id.goods_car_toolbar)
    Toolbar toolbar;
    @BindViews({R.id.toobar_goods_car_title_address, R.id.toobar_goods_car_title_nearby})
    TextView[] tvToobar;
    @BindView(R.id.recylerView_goods_car)
    RecyclerView recylerView;
    @BindView(R.id.layout_goods_car_more)
    AutoRelativeLayout lyMore;//更多
    @BindView(R.id.ly_goods_car_cartime)
    AutoRelativeLayout lyCartime;
    @BindView(R.id.tv_goods_car_car_time)
    TextView tvCarTime;//装车时间
    @BindView(R.id.ly_goods_car_start)
    AutoRelativeLayout lyStart;
    @BindView(R.id.tv_goods_car_start_email)
    TextView tvStart;//出发地
    @BindView(R.id.ly_goods_car_end)
    AutoRelativeLayout lyEnd;
    @BindView(R.id.tv_goods_car_end_email)
    TextView tvEnd;//目的地
    @BindView(R.id.toobar_goods_car_title_address)
    TextView toobarGoodsTitleAddress;
    @BindView(R.id.toobar_goods_car_title_nearby)
    TextView toobarGoodsTitleNearby;

    private TimePickerView timePickerView;//时间选择器
    private List<String> list;
    private CarGoodsAdapter carGoodsAdapter;
    private PopupWindow popMoreWindow;//用来显示popupwindow的parent
    private PopupWindow pcdPopCityWindow;//用来显示城市联动
    private File file;
    private int index;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_cargoods, null);
        ButterKnife.bind(this, view);

        initRecyclerViewAdapter();//初始化RecyclerView
        initDB();//初始化数据库
        //设置一下actionbar
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("");

        //设置标题栏和Toolbar颜色一致
        SystemBarCompat.setTranslucentStatusOnKitkat(getActivity());
        SystemBarCompat.setupStatusBarColorAfterLollipop(getActivity(), ResourceUtil.getColor(R.color.lemonchiffon, getActivity()));

        //设置标题选择状态和字体颜色更改
        tvToobar[0].setSelected(true);

        lyStart.setOnClickListener(clickListener);
        lyEnd.setOnClickListener(clickListener);
        lyCartime.setOnClickListener(clickListener);
        lyMore.setOnClickListener(clickListener);

        showPopMoreWindow();//用来显示“更多”的parent
        initCustomTimePicker();//用来显示“时间”
        return view;
    }

    //RecyclerView初始化设置
    private void initRecyclerViewAdapter() {

        list = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            list.add(i + "");
        }
        carGoodsAdapter = new CarGoodsAdapter(getActivity(), list);
        recylerView.setAdapter(carGoodsAdapter);
        carGoodsAdapter.notifyDataSetChanged();

        recylerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        //设置监听
        carGoodsAdapter.setonNewItemClickListener(newItemClickListener);
    }

    //item监听
    private CarGoodsAdapter.onNewItemClickListener newItemClickListener = new CarGoodsAdapter.onNewItemClickListener() {

        @Override
        public void onNewItemClick(View view, int postion) {
            //实现效果
            Toast.makeText(getActivity(), "点击了" + postion, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void OnNewItemLongClick(View view, int postion) {
            //实现效果
            Toast.makeText(getActivity(), "长按了" + postion, Toast.LENGTH_SHORT).show();
        }
    };

    //按钮监听
    @OnClick({R.id.toobar_goods_car_title_address, R.id.toobar_goods_car_title_nearby})
    public void onClick(View view) {
        //设置未点击状态为正常颜色效果
        for (int i = 0; i < tvToobar.length; i++) {
            tvToobar[i].setSelected(false);
        }
        switch (view.getId()) {
            case R.id.toobar_goods_car_title_address://标题地址
                //设置标题选择状态和字体颜色更改
                tvToobar[0].setSelected(true);
                break;
            case R.id.toobar_goods_car_title_nearby://标题附近
                //设置标题选择状态和字体颜色更改
                tvToobar[1].setSelected(true);
                break;
        }
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ly_goods_car_start://出发地
                    index = 0;
                    showPopCityWindow();
                    break;
                case R.id.ly_goods_car_end://目的地
                    index = 1;
                    showPopCityWindow();
                    break;
                case R.id.ly_goods_car_cartime://装车时间
                    if (timePickerView != null) {
                        timePickerView.show();
                    }
                    break;
                case R.id.layout_goods_car_more://更多
                        //点击弹出pop对话框(用来显示popupwindow的parent  ID，以该位置为参照物设置具体位置)
                    popMoreWindow.showAsDropDown(lyMore, 0, 5);
                    setPopWindowbackgroundAlpha(0.5f);//设置背景变暗
                    break;

            }
        }
    };

    /**
     * 显示“时间”的初始化
     */
    private void initCustomTimePicker() {
        Calendar selectedDate = Calendar.getInstance();//系统当前时间
        Calendar startDate = Calendar.getInstance();//开始时间
        Calendar endDate = Calendar.getInstance();//结束时间
        startDate.set(2000, 1, 23);
        endDate.set(2038, 1, 28);
        timePickerView = new TimePickerView.Builder(getActivity(), new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                //可根据需要自行截取数据显示在控件上  yyyy-MM-dd HH:mm:ss  或yyyy-MM-dd
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                String time = format.format(date);
                tvCarTime.setText(time);
            }
        })
                .setType(TimePickerView.Type.YEAR_MONTH_DAY)
                .setDate(selectedDate)
                .setRangDate(startDate, endDate)
                .setTextColorCenter(Color.parseColor("#FF4081"))//设置选中文字的颜色#64AE4A
                .setTextColorOut(Color.parseColor("#81D741"))//设置选中项以外的颜色#64AE4A
                .setLineSpacingMultiplier(2.4f)//设置两横线之间的间隔倍数
                .setDividerColor(Color.parseColor("#24E1E4"))//设置分割线的颜色
                .setDividerType(WheelView.DividerType.WRAP)//设置分割线的类型
                .setBgColor(Color.parseColor("#083731"))//背景颜色(必须是16进制) Night mode#2AA2BC
                .gravity(Gravity.CENTER)//设置控件显示位置 default is center*/
                .isDialog(true)//设置显示位置
                .setLayoutRes(R.layout.pickerview_custom_time, new CustomListener() {
                    @Override
                    public void customLayout(View v) {
                        ImageView imgCancel = (ImageView) v.findViewById(R.id.iv_cancel);
                        final TextView tvSubmit = (TextView) v.findViewById(R.id.tv_finish);
                        tvSubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                timePickerView.returnData();
                            }
                        });
                        imgCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                timePickerView.dismiss();
                            }
                        });
                    }
                })
                .build();
    }


    /**
     * 显示"更多"对话框
     */
    private void showPopMoreWindow() {
        View popview = LayoutInflater.from(getActivity()).inflate(R.layout.goods_scrollview_more, null);
        //WRAP_CONTENT:是.xml中的布局宽、高，wrap_content包裹
        popMoreWindow = new PopupWindow(popview, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popMoreWindow.setBackgroundDrawable(new BitmapDrawable());
        popMoreWindow.getContentView().setFocusable(true);//为是否可以获得焦点
        //设置可触摸PopupWindow之外的地方关闭
        popMoreWindow.setOutsideTouchable(true);
        setPopWindowbackgroundAlpha(1f);
        //点击其它地方对话框关闭的时候，将背景还原
        popMoreWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                setPopWindowbackgroundAlpha(1f);
            }
        });

        popMoreWindow.setAnimationStyle(R.style.dialogWindowAnim_style);//设置动画
        // TODO: 2017/3/28 控件点击待完成
    }

    /**
     * 显示三级联动的PopUpWindow城市联动对话框
     */
    private void showPopCityWindow() {
        pcdPopCityWindow = new PCDPopUpWindow(getActivity(), file, checkChangeListener);
        // 窗口显示的位置
        pcdPopCityWindow.showAsDropDown(lyStart);
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
    public void setPopWindowbackgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams params = getActivity().getWindow().getAttributes();
        params.alpha = bgAlpha;
        getActivity().getWindow().setAttributes(params);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }
    /**
     * 以下是选择PCDPopUpWindow的item的回调
     *
     * @param text
     */
    private PCDPopUpWindow.OnCheckChangeListener checkChangeListener = new PCDPopUpWindow.OnCheckChangeListener() {
        @Override
        public void setCityText(String text) {//城市

        }

        @Override
        public void setProvinceText(String text) {//省
            if (index == 0) {
                tvStart.setText(text);
            } else if (index == 1) {
                tvEnd.setText(text);
            }
        }

        @Override
        public void setDistrictText(String text) {//县、区
            if (index == 0) {
                tvStart.setText(text);
            } else if (index == 1) {
                tvEnd.setText(text);
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
        //将city.db添加到本地文件夹
        file = new File(getActivity().getFilesDir(), "city.db");
        if (!file.exists()) {
            new Thread() {
                public void run() {
                    InputStream is = null;
                    FileOutputStream fos = null;
                    try {
                        is = getActivity().getAssets().open("city.db");
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
