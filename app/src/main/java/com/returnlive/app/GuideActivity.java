package com.returnlive.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.returnlive.app.base.BaseActivity;
import com.returnlive.app.utils.SystemBarCompat;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class GuideActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    @BindView(R.id.wel_img)
    SimpleDraweeView welImg;
    @BindView(R.id.welcome_viewpager)
    ViewPager viewPager;
    @BindView(R.id.img_guild)
    ImageView imgGuild;
    private SharedPreferences sharedPreferences;
    private int i;
    private PagerAdapter adapter;
    private List<ImageView> imageList;
    private int[] ims;
    private Unbinder unbinder;


    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    imgGuild.setVisibility(View.VISIBLE);

                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_guide);
        unbinder =  ButterKnife.bind(this);
        //设置标题栏和Toolbar颜色一致
        // 19
        SystemBarCompat.setTranslucentStatusOnKitkat(this);
        //after
        SystemBarCompat.setTranslucentNavigationAfterKitkat(this);
        SystemBarCompat.setupStatusBarColorAfterLollipop(this, Color.TRANSPARENT);

        sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        //进行获取判断值
        i = sharedPreferences.getInt("welcome", 0);
        init();

    }

    private void init() {

        //判断
        if (i == 0) {
            //把那一张图给gone掉
            welImg.setVisibility(View.GONE);
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putInt("welcome", 1);
            edit.commit();
            initData();
            initCtrl();
            viewPager.addOnPageChangeListener(this);
        } else {
            pageJump(LoginActivity.class);
            finish();
        }

    }

    //进行viewpager的适配
    private void initCtrl() {
        adapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(imageList.get(position));
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(imageList.get(position));
                return imageList.get(position);
            }
        };
        viewPager.setAdapter(adapter);

    }

    //添加图片到list里面
    private void initData() {
        imageList = new ArrayList<>();
        ims = new int[]{R.drawable.guidepage1, R.drawable.guidepage2, R.drawable.guidepage3};
        for (int i = 0; i < ims.length; i++) {
            SimpleDraweeView imageView = new SimpleDraweeView(this);
            imageView.setImageURI(Uri.parse("res:///" + ims[i]));
            //设置图片的比例   （图片，设置比例，与XY相符）
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageList.add(imageView);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (position==2){
            imgGuild.setVisibility(View.VISIBLE);
        }else {
            imgGuild.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPageSelected(int position) {
        if (position < 2) {
            imgGuild.setVisibility(View.GONE);
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Message msg = Message.obtain();
                    msg.what = 1;
                    handler.sendMessage(msg);
                }
            }).start();
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @OnClick(R.id.img_guild)
    public void onClick() {
        pageJump(LoginActivity.class);
        finish();
    }
}
