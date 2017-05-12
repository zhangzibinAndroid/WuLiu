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

public class HelpCenterActivity extends BaseActivity {

    @BindView(R.id.tv_helptitle)
    TextView tvHelptitle;
    @BindView(R.id.tv_helpcontext)
    TextView tvHelpcontext;

    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helpcenter);
        unbinder = ButterKnife.bind(this);
        //设置标题栏和Toolbar颜色一致
        SystemBarCompat.setTranslucentStatusOnKitkat(this);
        SystemBarCompat.setupStatusBarColorAfterLollipop(this, ResourceUtil.getColor(R.color.snow, this));

        initView();

    }

    private void initView() {
        tvHelptitle.setText("标题");
        tvHelpcontext.setText("\u3000\u3000 +首行缩进");
    }

    @OnClick(R.id.img_backhelp)
    public void onClick() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
