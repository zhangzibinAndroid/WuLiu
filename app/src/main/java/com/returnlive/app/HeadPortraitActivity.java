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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.returnlive.app.base.BaseActivity;
import com.returnlive.app.utils.ImageUtils;
import com.returnlive.app.utils.ResourceUtil;
import com.returnlive.app.utils.SystemBarCompat;

import java.io.File;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author 张梓彬
 * Created by zzb on 2017/3/23.
 * 后续改为从服务器获取照片
 */

public class HeadPortraitActivity extends BaseActivity {
    private Unbinder unbinder;
    protected static final int CHOOSE_PICTURE = 0;
    protected static final int TAKE_PICTURE = 1;
    private static final int CROP_SMALL_PICTURE = 2;
    protected static Uri tempUri;

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_replace)
    TextView tvReplace;
    @BindView(R.id.portrait_toolbar)
    Toolbar toolbar;
    @BindView(R.id.img_head_portrait)
    ImageView imgHeadPortrait;
    @BindView(R.id.img_backhead)
    ImageView imgBackhead;
    private Bitmap bitmap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.portrait_layout);
        unbinder=ButterKnife.bind(this);
        //设置标题栏和Toolbar颜色一致
        SystemBarCompat.setTranslucentStatusOnKitkat(this);
        SystemBarCompat.setupStatusBarColorAfterLollipop(this, ResourceUtil.getColor(R.color.snow, this));

        initView();
        Intent intent = getIntent();
        if(intent!=null)
        {
            bitmap=intent.getParcelableExtra("HeadPortrait");
            imgHeadPortrait.setImageBitmap(bitmap);
        }
    }

    /**
     * 初始化配置
     */
    private void initView() {
        toolbar.setTitleTextColor(getResources().getColor(R.color.write));
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
    }

    @OnClick({R.id.img_head_portrait, R.id.img_backhead,R.id.tv_replace})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_head_portrait:
                break;
            case R.id.img_backhead:
                finishBitmap();
                break;
            case R.id.tv_replace:
                showChoosePicDialog(getResources().getString(R.string.head_portrait));
                break;
        }
    }

    private void finishBitmap(){
        Bitmap bitmap = ((BitmapDrawable)imgHeadPortrait.getDrawable()).getBitmap();
        Intent intent = new Intent();
        intent.putExtra("Certification",bitmap);
        setResult(520, intent);
        finish();
    }


    /**
     * 手机自带返回键关闭页面传值
     * */
    @Override
    public void onBackPressed() {
        finishBitmap();
    }

    /**
     * 显示修改头像的对话框
     */
    protected void showChoosePicDialog(String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        String[] items = {"选择本地照片", "拍照"};
        builder.setNegativeButton("取消", null);
        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
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
                        tempUri = Uri.fromFile(new File(Environment
                                .getExternalStorageDirectory()+"/HeadPortrait.jpg"));
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
                    if (data != null) {
                        setImageToView(data); // 让刚才选择裁剪得到的图片显示在界面上
                    }
                    break;
            }
        }
    }

    protected void setImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            imgHeadPortrait.setImageBitmap(photo);
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

        String imagePath = ImageUtils.savePhoto(bitmap, Environment
                .getExternalStorageDirectory().getAbsolutePath(), String
                .valueOf(System.currentTimeMillis()));
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
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_SMALL_PICTURE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
