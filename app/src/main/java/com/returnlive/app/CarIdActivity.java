package com.returnlive.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.returnlive.app.base.BaseActivity;
import com.returnlive.app.utils.ResourceUtil;
import com.returnlive.app.utils.SystemBarCompat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class CarIdActivity extends BaseActivity {
    private Unbinder unbinder;

    @BindView(R.id.edt_printName)
    EditText edtPrintName;
    private String citySelect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_id);
        unbinder = ButterKnife.bind(this);
        //设置标题栏和Toolbar颜色一致
        SystemBarCompat.setTranslucentStatusOnKitkat(this);
        SystemBarCompat.setupStatusBarColorAfterLollipop(this, ResourceUtil.getColor(R.color.snow, this));

        Intent intent = getIntent();
        citySelect = intent.getStringExtra("cityselect");

    }

    @OnClick({R.id.img_backcarid, R.id.tv_caridsaves})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_backcarid:
                finish();
                break;
            case R.id.tv_caridsaves:
                String number = edtPrintName.getText().toString();
                if (!number.equals("")&&number!=null){
                    String licensePlateNumber =citySelect+number;
                    Intent intent = new Intent();
                    intent.putExtra("licensePlateNumber",licensePlateNumber);
                    setResult(13,intent);
                    finish();
                }else {
                    Toast.makeText(this, "车牌号不能为空", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
