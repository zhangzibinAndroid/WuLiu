package com.returnlive.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.returnlive.app.base.BaseActivity;
import com.returnlive.app.utils.ResourceUtil;
import com.returnlive.app.utils.SystemBarCompat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class NameActivity extends BaseActivity {
    private Unbinder unbinder;

    @BindView(R.id.img_backname)
    ImageView imgBackname;
    @BindView(R.id.tv_saves)
    TextView tvSaves;
    @BindView(R.id.edt_printName)
    EditText edtPrintName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);
        unbinder = ButterKnife.bind(this);
        //设置标题栏和Toolbar颜色一致
        SystemBarCompat.setTranslucentStatusOnKitkat(this);
        SystemBarCompat.setupStatusBarColorAfterLollipop(this, ResourceUtil.getColor(R.color.snow, this));


        getWindow().getAttributes().softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE;//设置键盘弹出
    }

    @OnClick({R.id.img_backname, R.id.tv_saves})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_backname:
                finish();
                break;
            case R.id.tv_saves:
               String name =  edtPrintName.getText().toString();
                Intent intent = new Intent();
                intent.putExtra("yourname",name);
                setResult(15,intent);
                finish();

                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
