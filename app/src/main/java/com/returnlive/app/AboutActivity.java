package com.returnlive.app;

import android.os.Bundle;
import android.widget.TextView;
import com.returnlive.app.base.BaseActivity;
import com.returnlive.app.utils.ResourceUtil;
import com.returnlive.app.utils.SystemBarCompat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class AboutActivity extends BaseActivity {
    private Unbinder unbinder;

    @BindView(R.id.tv_aboutcontext)
    TextView tvAboutcontext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        unbinder=ButterKnife.bind(this);
        //设置标题栏和Toolbar颜色一致
        SystemBarCompat.setTranslucentStatusOnKitkat(this);
        SystemBarCompat.setupStatusBarColorAfterLollipop(this, ResourceUtil.getColor(R.color.snow, this));

        initView();
    }

    private void initView() {
        tvAboutcontext.setText("\u3000\u3000+内容");
    }

    @OnClick(R.id.img_backabout)
    public void onClick() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
