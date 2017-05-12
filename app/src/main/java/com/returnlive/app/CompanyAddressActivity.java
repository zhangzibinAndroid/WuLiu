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

/**
 * @author 张梓彬
 * @// TODO: 2017/3/31 公司地址页面 
 * */
public class CompanyAddressActivity extends BaseActivity {

    @BindView(R.id.edt_company_address)
    EditText edtCompanyAddress;
    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_address);
        unbinder = ButterKnife.bind(this);
        //设置标题栏和Toolbar颜色一致
        SystemBarCompat.setTranslucentStatusOnKitkat(this);
        SystemBarCompat.setupStatusBarColorAfterLollipop(this, ResourceUtil.getColor(R.color.snow, this));

    }

    @OnClick({R.id.img_backcompany_address, R.id.tv_savescompany_address})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_backcompany_address:
                finish();
                break;
            case R.id.tv_savescompany_address:
                String address = edtCompanyAddress.getText().toString();
                if (!address.equals("")&&address!=null){
                    Intent intent = new Intent();
                    intent.putExtra("CompanyAddress",address);
                    setResult(23,intent);
                    finish();
                }else {
                    Toast.makeText(this, "公司地址不能为空", Toast.LENGTH_SHORT).show();
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
