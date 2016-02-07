package com.zhangtao.takephotoandsysalbumdemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    Button takePhoto, choosePhoto;
    ImageView picture;

    //拍摄的照片的Uri
    private Uri imageUri;
    //裁剪后照片的Uri
    private Uri CutSaveImage;
    //拍照完成后需要裁剪的请求码
    public static final int TAKE_PHOTO = 1;
    //裁剪完成后需要设置给Imageview的请求码
    public static final int CROP_PHOTO = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //加载View
        initViews();
        //绑定监听
        initEvents();
    }

    private void initEvents() {
        //拍照按钮的监听
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //这里涉及到文件的储存github里有另外的demo
                //拍照和裁剪后分别要保存的文件路径和文件名称
                File outputImage = new File(Environment.getExternalStorageDirectory(), "myself.jpg");
               // File saveOutPut = new File(Environment.getExternalStorageDirectory(), "save.jpg");
                try {
                    //如果这个文件存在,则删除.
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                    //创建
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //将图片文件解析成URI
                imageUri = Uri.fromFile(outputImage);
               // CutSaveImage = Uri.fromFile(saveOutPut);
                //Log.d("My", saveOutPut.getAbsolutePath());
                //打开系统的拍照程序
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                //指定这张图片的输出地址
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                //打开拍照程序,完成后回回调onActivityResult,请求码为TAKE_PHOTO
                startActivityForResult(intent, TAKE_PHOTO);
            }
        });
        choosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra("crop", true);
                intent.putExtra("scale", true);
                //这里的Action是用来打开相册的
                intent.setAction(Intent.ACTION_GET_CONTENT);
                //或者是Intent intent=new Intent("android.intent.action.GET_CONTENT");
                startActivityForResult(intent, CROP_PHOTO);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //拍照完成后回调
        switch (requestCode) {
            //如果这个请求码为TAKE_PHOTO,那么代码需要裁剪
            case TAKE_PHOTO:
                //Log.d("My", "RESULT_OK" + RESULT_OK + ",resultCode:" + resultCode + ",两者相等?" + (resultCode == RESULT_OK));
                if (resultCode == RESULT_OK) {
                    //如果拍照成功那么resultCode就等于Result_ok
                    //这里的Action是用来对打开照片进行裁剪页面
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(imageUri, "image/*");
                    intent.putExtra("scale", true);
                    intent.putExtra("crop", "true");
//                    intent.putExtra("aspectX", aspectX);
//                    intent.putExtra("aspectY", aspectY);
//                    intent.putExtra("outputX", outputX);
//                    intent.putExtra("outputY", outputY);
                    //intent.putExtra("noFaceDetection", true);
                   // intent.putExtra("return-data", false);
                    //指定裁剪后的输出位置
                 //   intent.putExtra(MediaStore.EXTRA_OUTPUT, CutSaveImage);
                    //裁剪完成后会继续回调这个onActivityResult那么判断,请求码为CROP_PHOTO
                    //就是说要设置了.
                    startActivityForResult(intent, CROP_PHOTO);
                }
                break;
            case CROP_PHOTO:
                // Log.d("My", "RESULT_OK" + RESULT_OK + ",resultCode:" + resultCode);
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap = BitmapFactory
                                .decodeStream(getContentResolver().openInputStream(data.getData()));
                        //  Log.d("My", "开始设置图片");
                        picture.setImageBitmap(bitmap);
                        // Log.d("My", "图片设置完成");
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;

            default:
                break;
        }
    }

    private void initViews() {
        takePhoto = (Button) findViewById(R.id.take_photo);
        picture = (ImageView) findViewById(R.id.picture);
        choosePhoto = (Button) findViewById(R.id.choose_photo);
    }
}
