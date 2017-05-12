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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.OptionsPickerView;
import com.returnlive.app.base.BaseActivity;
import com.returnlive.app.utils.ImageUtils;
import com.returnlive.app.utils.ResourceUtil;
import com.returnlive.app.utils.SystemBarCompat;
import com.zhy.autolayout.AutoRelativeLayout;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.R.attr.x;


/**
 * @author 张梓彬
 *         司机认证页面
 */
public class CertificationActivity extends BaseActivity {

    @BindView(R.id.certification_toolbar)
    Toolbar toolbar;
    @BindView(R.id.img_portrait)
    ImageView imgPortrait;
    @BindView(R.id.idCard1)
    ImageView idCard1;
    @BindView(R.id.idCard2)
    ImageView idCard2;
    @BindView(R.id.lay_identity_certification)
    AutoRelativeLayout layIdentityCertification;
    @BindView(R.id.lay_drive_license)
    AutoRelativeLayout layDriveLicense;
    @BindView(R.id.lay_driving_license)
    AutoRelativeLayout layDrivingLicense;
    @BindView(R.id.lay_headstock_according)
    AutoRelativeLayout layHeadstockAccording;
    @BindView(R.id.img_driverPic)
    ImageView imgDriverPic;
    @BindView(R.id.img_drivingPic)
    ImageView imgDrivingPic;
    @BindView(R.id.img_carPic)
    ImageView imgCarPic;
    @BindView(R.id.tv_yourCardId)
    TextView tvYourCardId;
    @BindView(R.id.tv_yourCarStatus)
    TextView tvYourCarStatus;
    @BindView(R.id.tv_yourcarWeight)
    TextView tvYourcarWeight;
    @BindView(R.id.tv_yourname)
    TextView tvYourname;
    @BindView(R.id.tv_yourIDcard)
    TextView tvYourIDcard;

    private Unbinder unbinder;
    protected static final int CHOOSE_PICTURE = 0;
    protected static final int TAKE_PICTURE = 1;
    private static final int CROP_SMALL_PICTURE = 2;
    private static final int GETPICTURE = 3;
    protected static Uri tempUri;

    private boolean isSet = false;
    private boolean isSetDriver = false;
    private boolean isSetDriving = false;
    private boolean isSetCar = false;
    private int STATUS;//设置拍照的状态
    private int IMAGEVIEWSTATUS;//设置控件状态
    private int IDCARD_STATUS = 1;//设置身份证状态

    private OptionsPickerView<String> mOpv;
    private ArrayList<String> mListProvince = new ArrayList<String>();
    private ArrayList mm = new ArrayList<>();
    private String[] city = new String[]{"京", "津", "沪", "渝", "冀", "豫", "云", "辽", "黑", "湘", "皖", "鲁", "新", "苏", "浙", "赣", "鄂", "桂", "甘", "晋", "蒙", "陕", "吉", "闽", "贵", "粤", "青", "藏", "川", "宁", "琼"};
    private static final String TAG = "TAG";
    private String[] models = {"平板", "高栏", "厢式", "保温", "冷藏", "集装箱", "面包车", "危险品", "其他"};
    private String[] carLength = {"4.2米", "5.0米", "6.2米", "6.8米", "7.2米", "7.7米", "7.8米", "8.2米", "8.7米", "9.6米", "12.5米",
            "13.0米", "15.0米", "16.0米", "17.5米", "自定义"};
    private String carModels, carSize;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certification);
        unbinder = ButterKnife.bind(this);
        //设置标题栏和Toolbar颜色一致
        SystemBarCompat.setTranslucentStatusOnKitkat(this);
        SystemBarCompat.setupStatusBarColorAfterLollipop(this, ResourceUtil.getColor(R.color.snow, this));

        initView();
    }

    /**
     * 初始化配置
     */
    private void initView() {
        toolbar.setTitleTextColor(getResources().getColor(R.color.write));
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        for (int i = 0; i < city.length; i++) {
            mListProvince.add(city[i]);
        }
        String a[] = new String[26];
        char num[] = new char[26];
        char words = 'A';
        for (int i = 0; i < 26; i++) {
            num[i] = (char) (words + i);
            a[i] = String.valueOf(num[i]);
        }
        for (int i = 0; i < a.length; i++) {
            mm.add(a[i]);
        }

        mOpv = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                String tx = mListProvince.get(options1) + mm.get(options2);
                Intent intent = new Intent(CertificationActivity.this, CarIdActivity.class);
                intent.putExtra("cityselect", tx);
                startActivityForResult(intent, 12);
            }
        })
                .setTitleText("选择车牌号")
                .setContentTextSize(20)//设置滚轮文字大小
                .setSelectOptions(0,0)//默认选中项
                .build();

        mOpv.setNPicker(mListProvince, mm,null);


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
                        if (STATUS == 1) {
                            pathName = "HeadPortrait.jpg";
                        } else if (STATUS == 2) {
                            pathName = "IdCard.jpg";
                        } else if (STATUS == 3) {
                            pathName = "Driver.jpg";
                        } else if (STATUS == 4) {
                            pathName = "Driving.jpg";
                        } else if (STATUS == 5) {
                            pathName = "HeadCar.jpg";
                        }
                        tempUri = Uri.fromFile(new File(Environment
                                .getExternalStorageDirectory() + "/" + pathName));
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
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @OnClick({R.id.lay_portrait, R.id.lay_identity_certification, R.id.lay_drive_license, R.id.lay_driving_license, R.id.lay_headstock_according, R.id.img_backcer, R.id.lay_yourname, R.id.lay_yourIDcard, R.id.lay_yourCardId, R.id.lay_yourCarStatus, R.id.lay_yourcarWeight})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lay_yourname:
                pageJumpWithData(NameActivity.class, 14);
                break;
            case R.id.lay_yourIDcard:
                pageJumpWithData(IdNumberActivity.class, 16);

                break;
            case R.id.lay_yourCardId:
                mOpv.show();
                break;
            case R.id.lay_yourCarStatus:
                showDialoge();
                break;
            case R.id.lay_yourcarWeight:
                showDialogWeight();
                break;
            case R.id.lay_portrait://头像
                if (!isSet) {
                    STATUS = 1;
                    IMAGEVIEWSTATUS = 1;
                    showChoosePicDialog(getResources().getString(R.string.head_portrait));
                } else {
                    sendBitmap(imgPortrait, HeadPortraitActivity.class, "HeadPortrait", 523);
                }
                break;
            case R.id.lay_identity_certification://身份证
                if (IDCARD_STATUS == 1) {
                    IdCardActivity.isLoading = false;
                    Intent intent = new Intent(this, IdCardActivity.class);
                    startActivityForResult(intent, 7);
                } else if (IDCARD_STATUS == 2) {
                    IdCardActivity.isLoading = true;
                    Bitmap bitmap1 = ((BitmapDrawable) idCard1.getDrawable()).getBitmap();
                    Bitmap bitmap2 = ((BitmapDrawable) idCard2.getDrawable()).getBitmap();
                    Intent intent = new Intent(this, IdCardActivity.class);
                    intent.putExtra("caridCard1", bitmap1);
                    intent.putExtra("caridCard2", bitmap2);
                    startActivityForResult(intent, 7);
                }
                break;
            case R.id.lay_drive_license://驾驶证
                if (!isSetDriver) {
                    STATUS = 3;
                    IMAGEVIEWSTATUS = 3;
                    showChoosePicDialog(getResources().getString(R.string.drive_license));
                } else {
                    sendBitmap(imgDriverPic, DriverActivity.class, "DriverPic", 8);
                }

                break;
            case R.id.lay_driving_license://行驶证
                if (!isSetDriving) {
                    STATUS = 4;
                    IMAGEVIEWSTATUS = 4;
                    showChoosePicDialog(getResources().getString(R.string.driving_license));
                } else {
                    sendBitmap(imgDrivingPic, DrivingActivity.class, "DrivingPic", 9);
                }
                break;
            case R.id.lay_headstock_according://车头照
                if (!isSetCar) {
                    STATUS = 5;
                    IMAGEVIEWSTATUS = 5;
                    showChoosePicDialog(getResources().getString(R.string.headstock_according));
                } else {
                    sendBitmap(imgCarPic, CarHeadActivity.class, "CarHead", 10);
                }
                break;

            case R.id.img_backcer:
                finish();
                break;
        }
    }

    private void showDialoge() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("选择车型");
        dialog.setItems(models, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                carModels = models[which];
                dialog.dismiss();
                showDialogeLength();

            }
        });
        dialog.show();
    }


    private void showDialogeLength() {
        AlertDialog.Builder dialog2 = new AlertDialog.Builder(this);
        dialog2.setTitle("选择长度");
        dialog2.setItems(carLength, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                carSize = carLength[which];
                if (carLength[which].equals("自定义")) {
                    showDialoge3();
                } else {
                    tvYourCarStatus.setText(carModels + "  " + carSize);
                }

            }
        });
        dialog2.show();
    }

    private void showDialoge3() {
        AlertDialog.Builder dialog3 = new AlertDialog.Builder(this);
        View view = View.inflate(this, R.layout.dialoglayout, null);
        final EditText edt_printName = (EditText) view.findViewById(R.id.edt_print);
        dialog3.setView(view);
        dialog3.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String length = edt_printName.getText().toString();
                if (!length.equals("") && length != null) {
                    tvYourCarStatus.setText(carModels + "  " + length + "米");
                } else {
                    Toast.makeText(CertificationActivity.this, "您还没有输入长度", Toast.LENGTH_SHORT).show();
                }


            }
        });
        dialog3.setNeutralButton("取消", null);
        dialog3.show();
    }

    private void showDialogWeight() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        View view = View.inflate(this, R.layout.dialoglayout, null);
        final EditText edt_weight = (EditText) view.findViewById(R.id.edt_print);
        edt_weight.setHint("请输入您的车辆载重");
        dialog.setView(view);
        dialog.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String length = edt_weight.getText().toString();
                if (!length.equals("") && length != null) {
                    tvYourcarWeight.setText(length + ".0吨");
                } else {
                    Toast.makeText(CertificationActivity.this, "您还没有输入您的车辆载重", Toast.LENGTH_SHORT).show();
                }


            }
        });
        dialog.setNeutralButton("取消", null);
        dialog.show();


    }


    private void sendBitmap(ImageView imageview, Class<?> cls, String key, int requestCode) {
        Bitmap image = ((BitmapDrawable) imageview.getDrawable()).getBitmap();
        Intent intent = new Intent(CertificationActivity.this, cls);
        intent.putExtra(key, image);
        startActivityForResult(intent, requestCode);
    }


    protected void setImageToView(Intent data, ImageView imageView) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            imageView.setImageBitmap(photo);
            uploadPic(photo);
        }
    }


    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    protected void startPhotoZoom(Uri uri) {
        if (uri != null) {
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


    /**
     * 后续要改为上传到服务器
     */
    private void uploadPic(Bitmap bitmap) {
        // 上传至服务器
        // ... 可以在这里把Bitmap转换成file，然后得到file的url，做文件上传操作
        // 注意这里得到的图片已经是圆形图片了
        // bitmap是没有做个圆形处理的，但已经被裁剪了

        String imagePath = ImageUtils.savePhoto(bitmap, Environment
                .getExternalStorageDirectory().getAbsolutePath(), String
                .valueOf(System.currentTimeMillis()));
        if (imagePath != null) {
            // 拿着imagePath上传了
            // ...
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            // 如果返回码是可以用的
            switch (requestCode) {
                case TAKE_PICTURE:
                    if (IMAGEVIEWSTATUS == 1) {
                        startPhotoZoom(tempUri); // 开始对图片进行裁剪处理
                    } else {
                        copyPhoto(tempUri);
                    }
                    break;
                case CHOOSE_PICTURE:
                    if (IMAGEVIEWSTATUS == 1) {
                        startPhotoZoom(data.getData()); // 开始对图片进行裁剪处理
                    } else {
                        copyPhoto(data.getData());
                    }
                    break;
                case CROP_SMALL_PICTURE:
                    if (IMAGEVIEWSTATUS == 1) {
                        if (data != null) {
                            setImageToView(data, imgPortrait); // 让刚才选择裁剪得到的图片显示在界面上
                            isSet = true;
                        }
                    }
                    break;
                case GETPICTURE:
                    if (IMAGEVIEWSTATUS == 3) {
                        if (data != null) {
                            setImageToView(data, imgDriverPic);
                            isSetDriver = true;
                        }
                    } else if (IMAGEVIEWSTATUS == 4) {
                        if (data != null) {
                            setImageToView(data, imgDrivingPic);
                            isSetDriving = true;
                        }
                    } else if (IMAGEVIEWSTATUS == 5) {
                        if (data != null) {
                            setImageToView(data, imgCarPic);
                            isSetCar = true;
                        }
                    }
                    break;
            }
        } else if (requestCode == 523 && resultCode == 520) {
            Bitmap bitmap = data.getParcelableExtra("Certification");
            imgPortrait.setImageBitmap(bitmap);
        } else if (requestCode == 7 && resultCode == 8) {
            Bitmap bitmap1 = data.getParcelableExtra("IdCard1");
            idCard1.setImageBitmap(bitmap1);
            Bitmap bitmap2 = data.getParcelableExtra("IdCard2");
            idCard2.setImageBitmap(bitmap2);
            IDCARD_STATUS = 2;
        } else if (requestCode == 8 && resultCode == 9) {
            Bitmap bitmap = data.getParcelableExtra("FromDriverPic");
            imgDriverPic.setImageBitmap(bitmap);
        } else if (requestCode == 9 && resultCode == 10) {
            Bitmap bitmap = data.getParcelableExtra("FromDrivingPic");
            imgDrivingPic.setImageBitmap(bitmap);
        } else if (requestCode == 10 && resultCode == 11) {
            Bitmap bitmap = data.getParcelableExtra("FromCarhead");
            imgCarPic.setImageBitmap(bitmap);
        } else if (requestCode == 12 && resultCode == 13) {
            String licensePlateNumber = data.getStringExtra("licensePlateNumber");
            Log.d("TAG", "onActivityResult: " + licensePlateNumber);
            tvYourCardId.setText(licensePlateNumber);
        } else if (requestCode == 14 && resultCode == 15) {
            String name = data.getStringExtra("yourname");
            tvYourname.setText(name);
        } else if (requestCode == 16 && resultCode == 17){
            String yourIdCard = data.getStringExtra("yourIdCard");
            tvYourIDcard.setText(yourIdCard);
        }
    }


}
