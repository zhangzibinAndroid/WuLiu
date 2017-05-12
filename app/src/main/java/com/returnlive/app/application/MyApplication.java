package com.returnlive.app.application;

import android.app.Application;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.returnlive.app.utils.SharePreferencesUtils;
import com.zhy.autolayout.config.AutoLayoutConifg;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by 王建法 on 2017/3/30.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();


        //OkHttpUtils 实例化
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                .build();
        OkHttpUtils.initClient(okHttpClient);

        //屏幕适配（需要在项目的AndroidManifest中注明你的设计稿的尺寸）
        AutoLayoutConifg.getInstance().useDeviceSize();

        //ImageLoader图片加载
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .build();
        ImageLoader.getInstance().init(config);

        //初始化本地配置
        SharePreferencesUtils.init(this);
    }
}
