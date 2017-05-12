package com.returnlive.app;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.returnlive.app.utils.ImageUtils;
import com.returnlive.app.utils.ResourceUtil;
import com.returnlive.app.utils.SystemBarCompat;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class IdCardActivity extends AppCompatActivity {
    private Unbinder unbinder;
    protected static final int CHOOSE_PICTURE = 0;
    protected static final int TAKE_PICTURE = 1;
    private static final int CROP_SMALL_PICTURE = 2;
    protected static Uri tempUri;

    @BindView(R.id.img_backIdCard)
    ImageView imgBackIdCard;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    @BindView(R.id.portrait_toolbar)
    Toolbar portraitToolbar;
    @BindView(R.id.img_IdCard1)
    ImageView imgIdCard1;
    @BindView(R.id.img_IdCard2)
    ImageView imgIdCard2;

    private int IdCard_Status;
    public static boolean isLoading = false;
    public static Bitmap bitmap1,bitmap2;
    private String imagePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_id_card);
        unbinder = ButterKnife.bind(this);
        //设置标题栏和Toolbar颜色一致
        SystemBarCompat.setTranslucentStatusOnKitkat(this);
        SystemBarCompat.setupStatusBarColorAfterLollipop(this, ResourceUtil.getColor(R.color.snow, this));

        if (isLoading){
            initView();
        }

    }

    private void initView() {
        imgIdCard1.setImageBitmap(ShipperCertificationActivity.bitmap1);
        imgIdCard2.setImageBitmap(ShipperCertificationActivity.bitmap2);
    }

    @OnClick({R.id.img_backIdCard, R.id.img_IdCard1, R.id.img_IdCard2,R.id.btn_finish})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_backIdCard:
                finish();
                break;
            case R.id.btn_finish:
                finishBitmap(imgIdCard1,imgIdCard2,8);
                break;
            case R.id.img_IdCard1:
                IdCard_Status = 1;
                showChoosePicDialog("身份证正面照");
                break;
            case R.id.img_IdCard2:
                IdCard_Status = 2;
                showChoosePicDialog("身份证反面照");
                break;
        }
    }

    /**
     * 完成之后Bitmap传值
     * */
    private void finishBitmap(ImageView imgIdCard1,ImageView imgIdCard2,int resultCode){
         bitmap1 = ((BitmapDrawable)imgIdCard1.getDrawable()).getBitmap();
         bitmap2 = ((BitmapDrawable)imgIdCard2.getDrawable()).getBitmap();
        Intent intent = new Intent();
        setResult(resultCode, intent);
        finish();
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

                        if (IdCard_Status == 1) {
                            pathName = "IdCard1.jpg";
                        } else if (IdCard_Status == 2) {
                            pathName = "IdCard2.jpg";
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
                    startPhotoZoom(tempUri); // 开始对图片进行裁剪处理
                    break;
                case CHOOSE_PICTURE:
                    startPhotoZoom(data.getData()); // 开始对图片进行裁剪处理
                    break;
                case CROP_SMALL_PICTURE:
                    if (IdCard_Status==1){
                        if (data != null) {
                            setImageToView(data,imgIdCard1); // 让刚才选择裁剪得到的图片显示在界面上
                            ShipperCertificationActivity.cardPath1 = imagePath;
                        }
                    }else if (IdCard_Status==2){
                        if (data != null) {
                            setImageToView(data,imgIdCard2); // 让刚才选择裁剪得到的图片显示在界面上
                            ShipperCertificationActivity.cardPath2 = imagePath;

                        }
                    }


                    break;
            }
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
        // 上传至服务器
        // ... 可以在这里把Bitmap转换成file，然后得到file的url，做文件上传操作
        // 注意这里得到的图片已经是圆形图片了
        // bitmap是没有做个圆形处理的，但已经被裁剪了

         imagePath = ImageUtils.savePhoto(bitmap, Environment
                .getExternalStorageDirectory().getAbsolutePath(), String
                .valueOf(System.currentTimeMillis()));
        Log.e("ShipperCertificationActivity", "imagePath:== "+imagePath );
        if(imagePath != null){
            // 拿着imagePath上传了
            // ...
        }
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
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_SMALL_PICTURE);
    }

}
