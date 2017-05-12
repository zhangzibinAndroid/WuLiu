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

public class IdNumberActivity extends BaseActivity {
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
        setContentView(R.layout.activity_id_number);
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
                String yourIdCard =  edtPrintName.getText().toString();
                Intent intent = new Intent();
                intent.putExtra("yourIdCard",yourIdCard);
                setResult(17,intent);
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
