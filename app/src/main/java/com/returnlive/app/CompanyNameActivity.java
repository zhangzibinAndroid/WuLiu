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

public class CompanyNameActivity extends BaseActivity {

    @BindView(R.id.edt_company_name)
    EditText edtCompanyName;
    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_name);
        unbinder =  ButterKnife.bind(this);
        //设置标题栏和Toolbar颜色一致
        SystemBarCompat.setTranslucentStatusOnKitkat(this);
        SystemBarCompat.setupStatusBarColorAfterLollipop(this, ResourceUtil.getColor(R.color.snow, this));

    }

    @OnClick({R.id.img_backcompany_name, R.id.tv_savescompany_name})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_backcompany_name:
                finish();
                break;
            case R.id.tv_savescompany_name:
                String companyName = edtCompanyName.getText().toString();
                if (!companyName.equals("")&&companyName!=null){
                    Intent intent = new Intent();
                    intent.putExtra("companyName",companyName);
                    setResult(21,intent);
                    finish();
                }else {
                    Toast.makeText(this, "公司名称不能为空", Toast.LENGTH_SHORT).show();
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
