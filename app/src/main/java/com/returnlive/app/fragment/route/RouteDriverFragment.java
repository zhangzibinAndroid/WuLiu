package com.returnlive.app.fragment.route;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jorge.circlelibrary.ImageCycleView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.returnlive.app.MainActivity;
import com.returnlive.app.QCodeActivity;
import com.returnlive.app.R;
import com.returnlive.app.RouteShowActivity;
import com.returnlive.app.adapter.RoutDriverAdapter;
import com.returnlive.app.bean.RoutDriver;
import com.returnlive.app.utils.ResourceUtil;
import com.returnlive.app.utils.SystemBarCompat;
import com.returnlive.app.view.RoundImageView;
import com.zhy.autolayout.AutoRelativeLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.R.attr.data;
import static android.R.id.message;

/**
 * Created by 王建法 on 2017/3/13.
 */

public class RouteDriverFragment extends Fragment {
    View view;
    @BindView(R.id.route_driver_toolbar)
    Toolbar toolbar;
    @BindView(R.id.rout_driver_recyler)
    RecyclerView recylerView;
    @BindView(R.id.tv_rout_add)
    AutoRelativeLayout tvRoutAdd;
    @BindView(R.id.tvimg_route_carout)
    RoundImageView imgCarout;
    @BindView(R.id.tvimg_route_qcoad)
    RoundImageView imgQcoad;
    @BindView(R.id.tv_route_unmber)
    TextView tvRouteUnmber;//路线数量

    private RoutDriverAdapter routAdapter;
    private List<RoutDriver> list = new ArrayList<>();

    /**
     * 轮播图
     */
    @BindView(R.id.cyaleview_route_driver)
    ImageCycleView imageCycleView;
    //装在数据的集合  文字描述
    ArrayList<String> imageDescList = new ArrayList<>();
    //装在数据的集合  图片地址
    ArrayList<String> urlList = new ArrayList<>();

    /**
     * 二维码
     */
    private static final int REQUEST_CODE_QRCODE_PERMISSIONS = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_route_driver_scrollview, container, false);
        ButterKnife.bind(this, view);
        initRecylerView();//初始化
        initAddDateImg();

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("");
        //设置标题栏和Toolbar颜色一致
        SystemBarCompat.setTranslucentStatusOnKitkat(getActivity());
        SystemBarCompat.setupStatusBarColorAfterLollipop(getActivity(), ResourceUtil.getColor(R.color.snow, getActivity()));

        return view;
    }

    private void initRecylerView() {
        recylerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recylerView.setItemAnimator(new DefaultItemAnimator());
        routAdapter = new RoutDriverAdapter(getActivity(), list);
        recylerView.setAdapter(routAdapter);
        //路线数量
        tvRouteUnmber.setText("订阅路线 (" + list.size() + ")");
        routAdapter.notifyItemInserted(list.size());
        routAdapter.setonNewItemClickListener(newItemClickListener);
    }

    /**
     * 初始化数据和集合图片
     */
    private void initAddDateImg() {
        urlList.clear();
        // 选择切换类型
        //CYCLE_VIEW_NORMAL  CYCLE_VIEW_THREE_SCALE   CYCLE_VIEW_ZOOM_IN   可以随意选择
        imageCycleView.setCycle_T(ImageCycleView.CYCLE_T.CYCLE_VIEW_THREE_SCALE);
        /**添加数据*/
        urlList.add("http://www.qsgct999.cn/d/file/jingmeitietu/2012-05-02/6933eea9f55b97f7e49ddb9049ceb753.jpg");
        urlList.add("http://www.qsgct999.cn/d/file/jingmeitietu/2012-05-02/56febf95fec224e5afb362833a8f903b.jpg");
        urlList.add("http://pic.58pic.com/58pic/13/16/62/96b58PIChRj_1024.jpg");
        urlList.add("http://img04.tooopen.com/images/20131115/sy_47419374241.jpg");
        urlList.add("http://www.daimg.com/uploads/allimg/110706/3-110F6213624317.jpg");

        /**添加文字*/
        imageDescList.add("1");
        imageDescList.add("2");
        imageDescList.add("3");
        imageDescList.add("4");
        imageDescList.add("5");

        int screenHeight = getScreenHeight(getActivity());
        initCarsuelView(imageDescList, urlList);//轮播图
    }

    /**
     * 初始化轮播图
     */
    public void initCarsuelView(ArrayList<String> imageDescList, ArrayList<String> urlList) {
        LinearLayout.LayoutParams cParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, getScreenHeight(getActivity()) * 3 / 10);
        imageCycleView.setLayoutParams(cParams);
        ImageCycleView.ImageCycleViewListener mAdCycleViewListener = new ImageCycleView.ImageCycleViewListener() {
            @Override
            public void onImageClick(int position, View imageView) {
                /**实现点击事件*/
                Toast.makeText(getActivity(), "position=" + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void displayImage(String imageURL, ImageView imageView) {
                /**在此方法中，显示图片，可以用自己的图片加载库，也可以用（Imageloader）*/
                ImageLoader.getInstance().displayImage(imageURL, imageView);
            }
        };
        /**设置数据*/
        imageCycleView.setImageResources(imageDescList, urlList, mAdCycleViewListener);
        // 是否隐藏底部
        imageCycleView.hideBottom(false);
        imageCycleView.startImageCycle();//循环播放间隔
        //imageCycleView.pushImageCycle();
    }

    /**
     * 得到屏幕的高度
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        if (null == context) {
            return 0;
        }
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getApplicationContext().getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    //item监听
    private RoutDriverAdapter.onNewItemClickListener newItemClickListener = new RoutDriverAdapter.onNewItemClickListener() {

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

    @OnClick({R.id.tv_rout_add, R.id.tvimg_route_carout, R.id.tvimg_route_qcoad})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_rout_add://添加路线
                Intent intent = new Intent(getActivity(), RouteShowActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.tvimg_route_carout://出车
                Toast.makeText(getActivity(), "点击： 出车", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tvimg_route_qcoad://二维码
                startActivity(new Intent(getActivity(), QCodeActivity.class));
                break;
        }
    }

    /**
     * Handler线程：为了观察主界面中“添加线路”出现的时间
     */
    private Handler handler = new Handler() ;
    Runnable runnable = new Runnable() {
        public void run() {
            try {
                //延时10s
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            RoutDriver routObj = new RoutDriver(startName, endName);
            routAdapter.addDate(list.size(), routObj);
            recylerView.setItemAnimator(new DefaultItemAnimator());
            //路线数量
            tvRouteUnmber.setText("订阅路线 (" + list.size() + ")");
            routAdapter.notifyItemInserted(list.size());
        }

    };
    /**
     * Intent意图返回值
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    private String startName;
    private String endName;
    @Override
    public void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 2) {
            startName = data.getStringExtra("startName");
            endName = data.getStringExtra("endName");
            //Handler线程
            handler.post(runnable);
            Log.e("tag", startName + "--" + endName);
        }
    }

    /**
     * 二维码callBack检测异常
     */
    private EasyPermissions.PermissionCallbacks permissionCallbacks = new EasyPermissions.PermissionCallbacks() {
        @Override
        public void onPermissionsGranted(int requestCode, List<String> perms) {

        }

        @Override
        public void onPermissionsDenied(int requestCode, List<String> perms) {

        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, permissionCallbacks);
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        requestCodeQRCodePermissions();//调用方法
    }

    @AfterPermissionGranted(REQUEST_CODE_QRCODE_PERMISSIONS)
    private void requestCodeQRCodePermissions() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!EasyPermissions.hasPermissions(getActivity(), perms)) {
            EasyPermissions.requestPermissions(this, "扫描二维码需要打开相机和散光灯的权限", REQUEST_CODE_QRCODE_PERMISSIONS, perms);
        }
    }
}
