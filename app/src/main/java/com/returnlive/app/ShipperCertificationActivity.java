package com.returnlive.app;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.returnlive.app.base.BaseActivity;
import com.returnlive.app.bean.User;
import com.returnlive.app.network.HttpUtilsApi;
import com.returnlive.app.network.ParserUser;
import com.returnlive.app.utils.ImageUtils;
import com.returnlive.app.utils.ResourceUtil;
import com.returnlive.app.utils.SharePreferencesUtils;
import com.returnlive.app.utils.SystemBarCompat;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ShipperCertificationActivity extends BaseActivity {

    @BindView(R.id.tv_yournameship)
    TextView tvYournameship;//姓名
    @BindView(R.id.tv_yourIDcardship)
    TextView tvYourIDcardship;//身份证号
    @BindView(R.id.img_portraitship)
    ImageView imgPortraitship;//头像照片
    @BindView(R.id.idCardship1)
    ImageView idCardship1;//身份证正面照
    @BindView(R.id.idCardship2)
    ImageView idCardship2;//身份证反面照
    @BindView(R.id.tv_company_name)
    TextView tvCompanyName;//公司名称
    @BindView(R.id.tv_company_address)
    TextView tvCompanyAddress;//公司地址
    @BindView(R.id.tv_yourposition)
    TextView tvYourposition;//职位
    @BindView(R.id.img_business_card)
    ImageView imgBusinessCard;//名片
    @BindView(R.id.img_door_head)
    ImageView imgDoorHead;//门头照
    @BindView(R.id.img_business_license)
    ImageView imgBusinessLicense;//营业执照
    private Unbinder unbinder;
    protected static final int CHOOSE_PICTURE = 0;
    protected static final int TAKE_PICTURE = 1;
    private static final int CROP_SMALL_PICTURE = 2;
    private static final int GETPICTURE = 3;
    protected static Uri tempUri;
    private int STATUS;//设置拍照的状态
    private int IMAGEVIEWSTATUS;//设置控件状态
    private int IDCARD_STATUS = 1;//设置身份证状态
    private boolean isSet = false;
    private boolean isSetBusinessCard = false;
    private boolean isSetDoorHead = false;
    private boolean isSetBusinessLicense = false;
    private String uid  = ParserUser.uid;
    private String mSession = ParserUser.mSesson;
    private static final String TAG = "ShipperCertificationActivity";
    private String url = HttpUtilsApi.httpUrl+HttpUtilsApi.shipperCertification+uid+"/"+ParserUser.zSesson+uid+"/"+mSession;
    public static String imagePath = "",imgPath = "",businessPath = "",doorPath = "",licensePath = "",cardPath1 = "",cardPath2 = "";
    public static Bitmap bitmap1,bitmap2;
    private ProgressDialog pro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipper_certification);
        unbinder = ButterKnife.bind(this);
        Log.e(TAG, "uid: =="+uid);
        Log.e(TAG, "mSession: =="+mSession);
        Log.e(TAG, "url: =="+url);
        Log.e(TAG, "imagePath: =="+imagePath);

        //设置标题栏和Toolbar颜色一致
        SystemBarCompat.setTranslucentStatusOnKitkat(this);
        SystemBarCompat.setupStatusBarColorAfterLollipop(this, ResourceUtil.getColor(R.color.snow, this));


    }

    @OnClick({R.id.img_backshipcer,R.id.lay_idship_certification, R.id.lay_yournameship, R.id.lay_yourIDcardship, R.id.lay_portraitship, R.id.tv_company_name, R.id.tv_company_address, R.id.lay_yourposition, R.id.lay_business_card, R.id.lay_door_head, R.id.lay_business_license, R.id.tv_contactship_customer, R.id.btn_shipsubmit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_backshipcer://返回键
                finish();
                break;
            case R.id.lay_yournameship://姓名
                pageJumpWithData(NameActivity.class, 14);
                break;
            case R.id.lay_yourIDcardship://身份证号
                pageJumpWithData(IdNumberActivity.class, 16);

                break;
            case R.id.lay_idship_certification://身份证照
                if (IDCARD_STATUS == 1) {
                    IdCardActivity.isLoading = false;
                    Intent intent = new Intent(this, IdCardActivity.class);
                    startActivityForResult(intent, 7);
                } else if (IDCARD_STATUS == 2) {
                    IdCardActivity.isLoading = true;
                     bitmap1 = ((BitmapDrawable) idCardship1.getDrawable()).getBitmap();
                     bitmap2 = ((BitmapDrawable) idCardship2.getDrawable()).getBitmap();
                    Intent intent = new Intent(this, IdCardActivity.class);
                    startActivityForResult(intent, 7);
                }

                break;
            case R.id.lay_portraitship://头像
                if (!isSet){
                    STATUS = 1;
                    IMAGEVIEWSTATUS = 1;
                    showChoosePicDialog(getResources().getString(R.string.head_portrait));
                }else {
                    sendBitmap(imgPortraitship, HeadPortraitActivity.class, "HeadPortrait", 523);
                }

                break;
            case R.id.tv_company_name://公司名称
                pageJumpWithData(CompanyNameActivity.class,20);
                break;
            case R.id.tv_company_address://公司地址
                pageJumpWithData(CompanyAddressActivity.class,22);

                break;
            case R.id.lay_yourposition://职位
                pageJumpWithData(PositionActivity.class,24);
                break;
            case R.id.lay_business_card://名片
                if (!isSetBusinessCard){
                    STATUS = 2;
                    IMAGEVIEWSTATUS = 2;
                    showChoosePicDialog(getResources().getString(R.string.business_card));
                }else {
                    sendBitmap(imgBusinessCard, BusinessCardActivity.class, "BusinessCard", 26);
                }

                break;
            case R.id.lay_door_head://门头照
                if (!isSetDoorHead){
                    STATUS = 3;
                    IMAGEVIEWSTATUS = 3;
                    showChoosePicDialog(getResources().getString(R.string.business_card));
                }else {
                    sendBitmap(imgDoorHead, DoorHeadActivity.class, "DoorHead", 28);
                }

                break;
            case R.id.lay_business_license://营业执照
                if (!isSetBusinessLicense){
                    STATUS = 4;
                    IMAGEVIEWSTATUS = 4;
                    showChoosePicDialog(getResources().getString(R.string.business_license));
                }else {
                    sendBitmap(imgBusinessLicense, BusinessLicenseActivity.class, "BusinessLicense", 30);
                }


                break;
            case R.id.tv_contactship_customer://联系客服
                /*Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:18795934365"));
                startActivity(intent);*/
                break;
            case R.id.btn_shipsubmit://提交
                if (tvYournameship.getText().toString().equals(getResources().getString(R.string.your_name))){
                    toastAction("姓名不能为空");
                    return;
                }else if (tvYourIDcardship.getText().toString().equals(getResources().getString(R.string.id_number))){
                    toastAction("身份证号码不能为空");
                    return;

                }else if (tvCompanyName.getText().toString().equals(getResources().getString(R.string.company_name))){
                    toastAction("公司名不能为空");
                    return;

                }else if (tvCompanyAddress.getText().toString().equals(getResources().getString(R.string.company_address))){
                    toastAction("公司地址不能为空");
                    return;

                }else if (tvYourposition.getText().toString().equals(getResources().getString(R.string.your_position))){
                    toastAction("职位不能为空");
                    return;
                }else if (imgPath.equals("")||imgPath==null){
                    toastAction("头像未设置");
                    return;
                }else if (businessPath.equals("")||businessPath==null){
                    toastAction("名片未设置");
                    return;
                }else if (doorPath.equals("")||doorPath==null){
                    toastAction("门头照未设置");
                    return;
                }else if (licensePath.equals("")||licensePath==null){
                    toastAction("营业执照未设置");
                    return;
                }else if (cardPath1.equals("")||cardPath1==null){
                    toastAction("身份证正面照未设置");
                    return;
                }else if (cardPath2.equals("")||cardPath2==null){
                    toastAction("身份证反面照未设置");
                    return;
                }else {
                    pro = new ProgressDialog(this);
                    pro.setProgressStyle(ProgressDialog.STYLE_SPINNER);

                    pro.setMessage("信息上传中...");
                    pro.setCanceledOnTouchOutside(false);
                    pro.setCancelable(false);
                    pro.setIndeterminateDrawable(getResources().getDrawable(R.drawable.pro_xuanzhuan));
                    pro.setIndeterminate(true);
                    pro.show();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            shipSubmit();

                        }
                    }).start();


                }
                break;
        }
    }



    private void toastAction(final String context) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ShipperCertificationActivity.this, context, Toast.LENGTH_SHORT).show();
            }
        });
    }


    //提交
    private void shipSubmit() {

        OkHttpClient mOkHttpClent = new OkHttpClient();
        File fileHead = new File(imgPath);//头像
        File fileBusiness = new File(businessPath);//名片
        File fileDoor = new File(doorPath);//门头照
        File fileLicense = new File(licensePath);//营业执照
        File fileIDCard1 = new File(cardPath1);//身份证正面照
        File fileIDCard2 = new File(cardPath2);//身份证反面照

        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);

        builder.addFormDataPart("name",tvYournameship.getText().toString());
        builder.addFormDataPart("card_id",tvYourIDcardship.getText().toString());
        builder.addFormDataPart("company_name",tvCompanyName.getText().toString());
        builder.addFormDataPart("company_add",tvCompanyAddress.getText().toString());
        builder.addFormDataPart("job",tvYourposition.getText().toString());

        builder.addFormDataPart("img", "HeadPortrait.png", RequestBody.create(MediaType.parse("image/png"), fileHead));
        builder.addFormDataPart("business", "BusinessCard.png", RequestBody.create(MediaType.parse("image/png"), fileBusiness));
        builder.addFormDataPart("door", "DoorHead.png", RequestBody.create(MediaType.parse("image/png"), fileDoor));
        builder.addFormDataPart("license", "BusinessLicense.png", RequestBody.create(MediaType.parse("image/png"), fileLicense));
        builder.addFormDataPart("card_img[]", "IdCard1.png", RequestBody.create(MediaType.parse("image/png"), fileIDCard1));
        builder.addFormDataPart("card_img[]", "IdCard2.png", RequestBody.create(MediaType.parse("image/png"), fileIDCard2));


        RequestBody requestBody = builder.build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Call call = mOkHttpClent.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: "+e);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ShipperCertificationActivity.this, "失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e(TAG, "成功"+response);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ShipperCertificationActivity.this, "成功", Toast.LENGTH_SHORT).show();
                        pro.dismiss();
                        finish();
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    protected void showChoosePicDialog(String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        String[] items = {"选择本地照片", "拍照"};
        builder.setNegativeButton("取消", null);
        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String pathName = null;
                switch (which) {
                    case CHOOSE_PICTURE: // 选择本地照片
                        Intent openAlbumIntent = new Intent(
                                Intent.ACTION_GET_CONTENT);
                        openAlbumIntent.setType("image/*");
                        startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
                        break;
                    case TAKE_PICTURE: // 拍照
                        Intent openCameraIntent = new Intent(
                                MediaStore.ACTION_IMAGE_CAPTURE);
                        if (STATUS==1){
                            pathName = "HeadPortrait.jpg";
                        }else if (STATUS==2){
                            pathName = "BusinessCard.jpg";
                        }else if (STATUS==3){
                            pathName = "DoorHead.jpg";
                        }else if (STATUS==4){
                            pathName = "BusinessLicense.jpg";
                        }
                        tempUri = Uri.fromFile(new File(Environment
                                .getExternalStorageDirectory()+"/"+pathName));
                        // 指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
                        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
                        startActivityForResult(openCameraIntent, TAKE_PICTURE);
                        break;
                }
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) { // 如果返回码是可以用的
            switch (requestCode) {
                case TAKE_PICTURE:
                    if (IMAGEVIEWSTATUS==1){
                        startPhotoZoom(tempUri); // 开始对图片进行裁剪处理
                    }else {
                        copyPhoto(tempUri);
                    }
                    break;
                case CHOOSE_PICTURE:
                    if (IMAGEVIEWSTATUS==1){
                        startPhotoZoom(data.getData()); // 开始对图片进行裁剪处理
                    }else {
                        copyPhoto(data.getData());
                    }
                    break;
                case CROP_SMALL_PICTURE:
                    if (IMAGEVIEWSTATUS == 1) {
                        if (data != null) {
                            setImageToView(data, imgPortraitship);
                            // 让刚才选择裁剪得到的图片显示在界面上
                            imgPath = imagePath;
                            Log.e(TAG, "imgPath:=== "+imgPath);
                            isSet = true;
                        }
                    }
                    break;

                case GETPICTURE:
                    if (IMAGEVIEWSTATUS == 2) {
                        if (data != null) {
                            setImageToView(data, imgBusinessCard); // 让刚才选择裁剪得到的图片显示在界面上
                            businessPath =  imagePath;
                            Log.e(TAG, "businessPath:== "+businessPath);
                            isSetBusinessCard = true;
                        }
                    }else if (IMAGEVIEWSTATUS == 3){
                        if (data != null) {
                            setImageToView(data, imgDoorHead); // 让刚才选择裁剪得到的图片显示在界面上
                            doorPath =imagePath ;
                            Log.e(TAG, "doorPath:== "+doorPath );
                            isSetDoorHead = true;
                        }
                    }else if (IMAGEVIEWSTATUS == 4){
                        if (data != null) {
                            setImageToView(data, imgBusinessLicense); // 让刚才选择裁剪得到的图片显示在界面上
                            licensePath=imagePath;
                            Log.e(TAG, "license: =="+licensePath);
                            isSetBusinessLicense = true;
                        }
                    }
                    break;
            }
        }else if (requestCode == 523 && resultCode == 520) {
            Bitmap bitmap = data.getParcelableExtra("Certification");
            imgPortraitship.setImageBitmap(bitmap);
        }else if (requestCode == 14 && resultCode == 15) {
            String name = data.getStringExtra("yourname");
            tvYournameship.setText(name);
        }else if (requestCode == 16 && resultCode == 17){
            String yourIdCard = data.getStringExtra("yourIdCard");
            tvYourIDcardship.setText(yourIdCard);
        }else if (requestCode == 7 && resultCode == 8) {
            idCardship1.setImageBitmap(IdCardActivity.bitmap1);
            idCardship2.setImageBitmap(IdCardActivity.bitmap2);
            IDCARD_STATUS = 2;
        }else if (requestCode == 20 && resultCode == 21){
            String companyName = data.getStringExtra("companyName");
            tvCompanyName.setText(companyName);
        }else if (requestCode == 22 && resultCode == 23){
            String companyAddress = data.getStringExtra("CompanyAddress");
            tvCompanyAddress.setText(companyAddress);
        }else if (requestCode == 24 && resultCode == 25){
            String yourPosition = data.getStringExtra("yourPosition");
            tvYourposition.setText(yourPosition);
        }else if (requestCode == 26 && resultCode == 27){
            Bitmap bitmap = data.getParcelableExtra("BusinessCardReturn");
            imgBusinessCard.setImageBitmap(bitmap);
        }else if (requestCode == 28 && resultCode == 29){
            Bitmap bitmap = data.getParcelableExtra("imgDoorHead");
            imgDoorHead.setImageBitmap(bitmap);
        }else if (requestCode == 30 && resultCode == 31){
            Bitmap bitmap = data.getParcelableExtra("imgBusinessLicense");
            imgBusinessLicense.setImageBitmap(bitmap);
        }
    }

    protected void setImageToView(Intent data,ImageView imageView) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            imageView.setImageBitmap(photo);
            uploadPic(photo);
        }
    }


    /**
     * 后续要改为上传到服务器
     * */
    private void uploadPic(Bitmap bitmap) {

         imagePath = ImageUtils.savePhoto(bitmap, Environment
                .getExternalStorageDirectory().getAbsolutePath(), String
                .valueOf(System.currentTimeMillis()));
        Log.e(TAG, "imagePath: "+imagePath);
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    protected void startPhotoZoom(Uri uri) {
        if (uri == null) {
            Log.i("tag", "The uri is not exist.");
        }
        tempUri = uri;
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop3", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX3", 1);
        intent.putExtra("aspectY3", 1);
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_SMALL_PICTURE);
    }



    protected void copyPhoto(Uri uri) {
        if (uri != null) {
            tempUri = uri;
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(uri, "image/*");
            // 设置裁剪
            intent.putExtra("crop2", "true");
            // aspectX aspectY 是宽高的比例
            intent.putExtra("aspectX2", 1);
            intent.putExtra("aspectY2", 1);
            intent.putExtra("return-data", true);
            startActivityForResult(intent, GETPICTURE);
        }
    }


    private void sendBitmap(ImageView imageview, Class<?> cls, String key, int requestCode) {
        Bitmap image = ((BitmapDrawable) imageview.getDrawable()).getBitmap();
        Intent intent = new Intent(ShipperCertificationActivity.this, cls);
        intent.putExtra(key, image);
        startActivityForResult(intent, requestCode);
    }


}
