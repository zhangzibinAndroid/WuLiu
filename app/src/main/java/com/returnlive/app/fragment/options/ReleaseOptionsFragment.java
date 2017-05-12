package com.returnlive.app.fragment.options;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.lib.WheelView;
import com.bigkoo.pickerview.listener.CustomListener;
import com.google.gson.Gson;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.returnlive.app.R;
import com.returnlive.app.bean.GetJsonDataUtil;
import com.returnlive.app.bean.JsonBean;
import com.returnlive.app.utils.ResourceUtil;
import com.returnlive.app.utils.SystemBarCompat;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by 王建法 on 2017/3/13.
 */

public class ReleaseOptionsFragment extends Fragment {
    ImageView imgstart, imgend, im_car, im_fc;
    View view;
    EditText et_cfd;
    EditText et_mdd;
    EditText et_lx;
    EditText et_fcsj;
    public static boolean isRequestt = false;
    @BindView(R.id.release_options_toolbar)
    Toolbar toolbar;
    private TimePickerView timePickerView;//时间选择器
    private ArrayList<JsonBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();
    private Thread thread;
    private static final int MSG_LOAD_DATA = 0x0001;
    private static final int MSG_LOAD_FAILED = 0x0003;
    private ImageView iv_car;
    private boolean isLoaded = false;
    private int select;//0 起始地  1 终点地
    private OptionsPickerView pvOptions;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_release_options, container, false);
        ButterKnife.bind(this, view);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("");
        //设置标题栏和Toolbar颜色一致
        SystemBarCompat.setTranslucentStatusOnKitkat(getActivity());
        SystemBarCompat.setupStatusBarColorAfterLollipop(getActivity(), ResourceUtil.getColor(R.color.snow, getActivity()));

        //起始位置
        imgstart = (ImageView) view.findViewById(R.id.imgstart);
        et_cfd = (EditText) view.findViewById(R.id.et_start);
        //到达位置
        imgend = (ImageView) view.findViewById(R.id.imgend);
        et_mdd = (EditText) view.findViewById(R.id.et_mudi);
        //发布时间
        im_fc = (ImageView) view.findViewById(R.id.Im_fc);
        et_fcsj = (EditText) view.findViewById(R.id.et_fcsj);

        //装车类型
        im_car = (ImageView) view.findViewById(R.id.car);
        et_lx = (EditText) view.findViewById(R.id.et_lx);

        im_fc.setOnClickListener(onClickListener);
        imgstart.setOnClickListener(onClickListener);
        imgend.setOnClickListener(onClickListener);
        im_car.setOnClickListener(onClickListener);

        initCustomTimePicker();
        initJsonData();

        mHandler.sendEmptyMessage(MSG_LOAD_DATA);

        return view;
    }

    RadioGroup radioGroup;
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imgstart://起始
                    if (isLoaded) {
                        select = 0;
                        ShowPickerView();// 弹出选择器
                    } else {
                        Toast.makeText(getActivity(), "数据暂未解析成功，请等待!", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.imgend://终点
                    if (isLoaded) {
                        select = 1;
                        ShowPickerView();// 弹出选择器
                    } else {
                        Toast.makeText(getActivity(), "数据暂未解析成功，请等待!", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.Im_fc://发布时间
                    if (timePickerView != null) {
                        timePickerView.show();
                    }
                    break;
                case R.id.car://车辆类型
                    setShowDialogPlus();
                    break;
            }
        }
    };

    /**
     * 对话框显示:弹出对话框不会有黑屏现象（AlertDialog会出现）
     */
    private void setShowDialogPlus() {
        //Header头部
        View vHeader = LayoutInflater.from(getActivity()).inflate(R.layout.header, null);
        TextView tvheader = (TextView) vHeader.findViewById(R.id.tv_header_title);
        tvheader.setText("车辆类型");
        //内容布局
        final View viewContext = LayoutInflater.from(getActivity()).inflate(R.layout.item_options_car_type, null);
        radioGroup = (RadioGroup) viewContext.findViewById(R.id.rb_tracking_mode);
        final DialogPlus dialog = DialogPlus.newDialog(getActivity())
                .setContentHolder(new ViewHolder(viewContext))
                .setHeader(vHeader)//添加头布局
                .setFooter(R.layout.footer_version)//添加脚布局
                .setInAnimation(R.anim.slide_bottom_to_top)
                .setOutAnimation(R.anim.slide_out_bottom)
                .setContentBackgroundResource(R.color.lavenderblush)//设置对话框背景颜色为透明（为了边角有圆角弧度）
                .setGravity(Gravity.CENTER)
                .setExpanded(true)
                .setCancelable(true)
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        switch (view.getId()) {
                            case R.id.tv_footer_no:
                                dialog.dismiss();
                                break;
                            case R.id.tv_footer_yes:
                                //获取选中项的ID
                                int radioButtonId = radioGroup.getCheckedRadioButtonId();
                                switch (radioButtonId) {
                                    case R.id.rb_big_car:
                                    case R.id.rb_car:
                                    case R.id.rb_small_car:
                                        //获取控件的内容
                                        RadioButton rb = (RadioButton) viewContext.findViewById(radioButtonId);
                                        //设置控件
                                        et_lx.setText("" + rb.getText().toString());
                                        break;
                                }
                                dialog.dismiss();
                                break;
                        }
                    }
                })
                .create();
        dialog.show();
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_LOAD_DATA:
                    if (thread == null) {//如果已创建就不再重新创建子线程了
                        //Toast.makeText(BeanJsonDataActivity.this,"开始解析数据",Toast.LENGTH_SHORT).show();
                        thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // 写子线程中的操作,解析省市区数据
                                initJsonData();
                            }
                        });
                        thread.start();
                    }
                    break;

                case MSG_LOAD_FAILED:
                    Toast.makeText(getActivity(), "解析数据失败", Toast.LENGTH_SHORT).show();
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
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
                String time = format.format(date);
                et_fcsj.setText(time);
            }
        })
                .setType(TimePickerView.Type.ALL)
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
     * 显示城市弹窗选择器
     */
    private void ShowPickerView() {// 弹出选择器
        pvOptions = new OptionsPickerView.Builder(getActivity(), new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String tx1 = options1Items.get(options1).getPickerViewText();
                String tx2 = options1Items.get(options1).getPickerViewText() +
                        options2Items.get(options1).get(options2);
                String tx2s = options1Items.get(options1).getPickerViewText() +
                        options3Items.get(options1).get(options2).get(options3);

                String tx3 = options1Items.get(options1).getPickerViewText() +
                        options2Items.get(options1).get(options2) +
                        options3Items.get(options1).get(options2).get(options3);
                /**
                 * 为了判断为“不限”情况下的解决
                 */
                if (select == 0) {
                    if (options2Items.get(options1).get(options2).equals("不限---")) {
                        //判断市=不限，设置控件省
                        et_cfd.setText(tx1);
                    } else if (options3Items.get(options1).get(options2).get(options3).equals("不限---")) {
                        //判断区=不限，设置控件省、市
                        et_cfd.setText(tx2);
                        if (options1Items.get(options1).getPickerViewText().equals(options2Items.get(options1).get(options2))) {
                            //判断市=不限并且省=市，设置控件省
                            et_cfd.setText(tx1);
                        }
                    } else if (options1Items.get(options1).getPickerViewText().equals(options2Items.get(options1).get(options2))) {
                        //判断省=市，设置控件省、区
                        et_cfd.setText(tx2s);
                    } else {
                        //其它情况，设置控件省、市、区
                        et_cfd.setText(tx3);
                    }
                }
                if (select == 1) {
                    if (options2Items.get(options1).get(options2).equals("不限---")) {
                        //判断市=不限，设置控件省
                        et_mdd.setText(tx1);
                    } else if (options3Items.get(options1).get(options2).get(options3).equals("不限---")) {
                        et_mdd.setText(tx2);
                        if (options1Items.get(options1).getPickerViewText().equals(options2Items.get(options1).get(options2))) {
                            et_mdd.setText(tx1);
                        }
                    } else if (options1Items.get(options1).getPickerViewText().equals(options2Items.get(options1).get(options2))) {
                        et_mdd.setText(tx2s);
                    } else {
                        et_mdd.setText(tx3);
                    }

                }
            }
        })

                /**自定义三级联动地区布局填充
                 * 如果想使用默认的直接去掉.setLayoutRes()即可
                 * */
                .setLayoutRes(R.layout.pickerview_custom_options, new CustomListener() {
                    @Override
                    public void customLayout(View v) {
                        final TextView tvSubmit = (TextView) v.findViewById(R.id.tv_finish);
                        ImageView ivCancel = (ImageView) v.findViewById(R.id.iv_cancel);
                        tvSubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvOptions.returnData();
                            }
                        });
                        ivCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvOptions.dismiss();
                            }
                        });

                    }
                })
                .setTitleText("城市选择")
                .setTextColorCenter(Color.parseColor("#AE3340")) //设置选中项文字颜色(必须是16进制)
                .setContentTextSize(20)
                .setTextColorOut(Color.parseColor("#FFD574"))//设置选中项以外的颜色#64AE4A
                .setLineSpacingMultiplier(2.4f)//设置两横线之间的间隔倍数
                .setDividerColor(Color.parseColor("#E68CBA"))//设置分割线的颜色
                .setBgColor(Color.parseColor("#373250"))//设置背景颜色(必须是16进制)
                .isDialog(true)//设置在屏幕上显示的位置  默认底部
                .build();

        pvOptions.setPicker(options1Items, options2Items, options3Items);//三级选择器

        pvOptions.show();
    }

    /**
     * 解析数据
     * 注意：assets 目录下的Json文件仅供参考，实际使用可自行替换文件
     * 关键逻辑在于循环体
     */
    private void initJsonData() {
        String JsonData = new GetJsonDataUtil().getJson(getActivity(), "province.json");//获取assets目录下的json文件数据

        ArrayList<JsonBean> jsonBean = parseData(JsonData);//用Gson 转成实体

        /**
         * 添加省份数据
         *
         * 注意：如果是添加的JavaBean实体，则实体类需要实现 IPickerViewData 接口，
         * PickerView会通过getPickerViewText方法获取字符串显示出来。
         */
        options1Items = jsonBean;

        for (int i = 0; i < jsonBean.size(); i++) {//遍历省份
            ArrayList<String> CityList = new ArrayList<>();//该省的城市列表（第二级）
            ArrayList<ArrayList<String>> Province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）

            for (int c = 0; c < jsonBean.get(i).getCityList().size(); c++) {//遍历该省份的所有城市
                String CityName = jsonBean.get(i).getCityList().get(c).getName();
                CityList.add(CityName);//添加城市

                ArrayList<String> City_AreaList = new ArrayList<>();//该城市的所有地区列表

                //如果无地区数据，建议添加空数据，防止数据为null 导致三个选项长度不匹配造成崩溃
                if (jsonBean.get(i).getCityList().get(c).getArea().size() == 0) {
                    City_AreaList.add("");
                }
                for (int d = 0; d < jsonBean.get(i).getCityList().get(c).getArea().size(); d++) {//该城市对应地区所有数据
                    String AreaName = jsonBean.get(i).getCityList().get(c).getArea().get(d);

                    City_AreaList.add(AreaName);//添加该城市所有地区数据
                }
                Province_AreaList.add(City_AreaList);//添加该省所有地区数据
            }

            // 添加城市数据
            options2Items.add(CityList);
            //添加地区数据
            options3Items.add(Province_AreaList);
            isLoaded = true;
        }

    }

    /**
     * Gson 解析
     *
     * @param result 集合
     * @return
     */
    public ArrayList<JsonBean> parseData(String result) {
        ArrayList<JsonBean> detail = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(result);
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                JsonBean entity = gson.fromJson(data.optJSONObject(i).toString(), JsonBean.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(MSG_LOAD_FAILED);
        }
        return detail;
    }
}
