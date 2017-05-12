package com.returnlive.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.returnlive.app.utils.AndroidVersion;
import com.returnlive.app.utils.ResourceUtil;
import com.returnlive.app.utils.SystemBarCompat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.QRCodeDecoder;
import cn.bingoogolapple.qrcode.zxing.ZXingView;
import me.imid.swipebacklayout.lib.SwipeBackLayout;

import static android.R.attr.data;

public class QCodeActivity extends SwipeBackActivity implements QRCodeView.Delegate{
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.choose_qrcde_from_gallery)
    TextView chooseQcodePhoto;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.scan_barcode)
    TextView tvScanCode;
    @BindView(R.id.close_flashlight)
    TextView tvlight;
    private static final int QRCODE_FROM_GALLERY = 666;//相册请求码

    private QRCodeView mQRCodeView;//二维码
    private int light=1;//0 关灯 1 开灯
    private boolean isQcode=false;//默认是条形码
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qcode);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        mQRCodeView = (ZXingView) findViewById(R.id.qc_code_zxingview);
        mQRCodeView.setDelegate(this);
        initQRCode();

    }
    /**
     * 初始化二维码扫一扫
     */
    private void initQRCode(){
        mQRCodeView.startSpot();//开始扫描
        mQRCodeView.startSpotAndShowRect();//开始识别显示扫描框
        mQRCodeView.showScanRect();//显示扫描框
        mQRCodeView.startCamera();//开始预览

        /**
         * 滑动返回初始化
         */
        SwipeBackLayout swipeBackLayout = getSwipeBackLayout();

        /*swipeBackLayout.addSwipeListener(new SwipeBackLayout.SwipeListener() {
            @Override
            public void onScrollStateChange(int state, float scrollPercent) {
            }

            @Override
            public void onEdgeTouch(int edgeFlag) {//刚滑动时
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                long[] pattern = {
                        0, 20
                };
                vibrator.vibrate(pattern, 1);
            }

            @Override
            public void onScrollOverThreshold() {//滑动一半时

            }
        });*/
    }
    /**
     *  扫描结果的处理监听
     */
    @Override
    public void onScanQRCodeSuccess(String result) {
        Toast.makeText(QCodeActivity.this, "结果： "+result, Toast.LENGTH_SHORT).show();
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
       // mQRCodeView.startSpot();
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Toast.makeText(QCodeActivity.this, "打开相机出错啦！！！", Toast.LENGTH_SHORT).show();
    }

    @OnClick({R.id.img_back, R.id.choose_qrcde_from_gallery, R.id.scan_barcode, R.id.close_flashlight})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back://返回
                finish();
                break;
            case R.id.choose_qrcde_from_gallery://相册
                startActivityForResult(BGAPhotoPickerActivity.newIntent(this, null, 1, null, false), QRCODE_FROM_GALLERY);
                break;
            case R.id.scan_barcode://二维码、条形码
                if (!isQcode){
                    mQRCodeView.changeToScanBarcodeStyle();//条形码
                    tvScanCode.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.ic_qc_code),null,null);
                    tvScanCode.setText("二维码");
                    isQcode=true;
                }else if (isQcode){
                    mQRCodeView.changeToScanQRCodeStyle();//二维码
                    tvScanCode.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.ic_qc_code2),null,null);
                    tvScanCode.setText("条形码");
                    isQcode=false;
                }
                break;
            case R.id.close_flashlight://灯
                switch (light){
                    case 0://关灯
                        mQRCodeView.closeFlashlight();
                        tvlight.setSelected(false);
                        tvlight.setText("关灯");
                        light=1;
                        break;
                    case 1:
                        mQRCodeView.openFlashlight();
                        tvlight.setSelected(true);
                        tvlight.setText("关灯");
                        light=0;
                        break;
                }
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mQRCodeView.showScanRect();

        if (resultCode == Activity.RESULT_OK && requestCode == QRCODE_FROM_GALLERY) {
            final String picturePath = BGAPhotoPickerActivity.getSelectedImages(data).get(0);

            /*
            *同步解析本地图片二维码。该方法是耗时操作，请在子线程中调用。
            * @param picturePath 要解析的二维码图片本地路径
            * @return 返回二维码图片里的内容 或 null*/
            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {

                    return  QRCodeDecoder.syncDecodeQRCode(picturePath);
                }

                @Override
                protected void onPostExecute(String result) {
                    if (TextUtils.isEmpty(result)) {
                        Toast.makeText(QCodeActivity.this, "未发现二维码", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(QCodeActivity.this, result, Toast.LENGTH_SHORT).show();
                    }
                }
            }.execute();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        mQRCodeView.startCamera();
      //mQRCodeView.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);

        mQRCodeView.showScanRect();
    }

    @Override
    protected void onStop() {
        mQRCodeView.stopCamera();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mQRCodeView.onDestroy();
        super.onDestroy();
        ButterKnife.bind(this).unbind();
    }


}
