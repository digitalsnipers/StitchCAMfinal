package com.digitalsnipers.shameel.sample;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.facebook.drawee.view.DraweeView;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Executors;

import static android.R.attr.countDown;
import static android.R.attr.data;

public class MainCamActivity extends AppCompatActivity {

    Camera camera;
    FrameLayout frameLayout;
    DisplayCamera displayCamera;
    ImageButton shutterBtn;
    ImageButton okBtn;
    ImageButton cancelBtn;
    ImageButton cropBtn;
    RelativeLayout imagePreview;
    ImageView imageStatus;
    int i=0,count=4,k=10;
    String no;
    //initialising Fresco
    //Fresco.initialize(this);

    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);
        View decorView = getWindow().getDecorView();
        if(hasFocus)
        {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_cam);

        startBgThread();

        //creating output folder if does not exist
        createOutputFolder();

        frameLayout = (FrameLayout)findViewById(R.id.frameLayout);
        imagePreview= (RelativeLayout)findViewById(R.id.previewLayout);
        imagePreview.setVisibility(View.GONE);
        imageStatus= (ImageView)findViewById(R.id.imageStatus);
        imageStatus.setVisibility(View.GONE );


        //open the camera
        camera = Camera.open();

        displayCamera=new DisplayCamera(this, camera);

        frameLayout.addView(displayCamera);

        shutterBtn = (ImageButton) findViewById(R.id.shutterButton);
        shutterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(camera!=null)
                {
                    camera.takePicture(null, null, mPictureCallback);

                }
            }
        });





    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (camera!= null)
            camera.stopPreview();
        stopBgThread();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        startBgThread();
        //camera.startPreview();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        stopBgThread();
        if (camera != null)
            camera.stopPreview();
            camera.release();
    }

    File picture_file;
    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            try{
                picture_file = getOutputMediaFile();

                if(picture_file==null)
                {
                    return;
                }
                else{
                    try{
                        FileOutputStream fos = new FileOutputStream(picture_file);
                        fos.write(data);
                        fos.close();

                        camera.startPreview();

                        Toast.makeText(getApplicationContext(),no+".jpg saved",Toast.LENGTH_SHORT).show();

                        dispImg();

                    }catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    };

    private File outputFolder;
    private void createOutputFolder(){
        File location = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        outputFolder = new File(location, "StitchCAM");
        if(!outputFolder.exists())
        {
            outputFolder.mkdirs();
        }
    }

    private String fileName;
    private File getOutputMediaFile() throws IOException {
        no=Integer.toString(i);
        File imageFile = new File(outputFolder,no+".jpg");
        fileName = imageFile.getAbsolutePath();
        return  imageFile;
    }


    private void dispImg() {

        imagePreview.setVisibility(View.VISIBLE);
        if (fileName != null) {
            Uri imageUri = Uri.parse(fileName);
            DraweeView draweeView = (DraweeView) findViewById(R.id.sdvImage);
            draweeView.setImageURI(imageUri);

            //OK Button
            okBtn = (ImageButton) findViewById(R.id.okButton);
            okBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imagePreview.setVisibility(View.GONE);

                    i++;
                    if(count==9)
                    {
                        loadGui9();
                    }
                    else
                    {
                            loadGui4();
                    }
                    //croping for primary image
                    if(i==1)
                    {
                        mBgHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                cropImg();
                            }
                        });
                        //cropImg();
                        cropBtn.setVisibility(View.GONE);
                    }
                }

            });

            //cropvaluebttn
            cropBtn = (ImageButton) findViewById(R.id.cropBtn);
            cropBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(count==4)
                    {
                        count=9;
                        cropBtn.setImageResource(R.mipmap.ic_9x);
                    }
                    else
                    {
                        count=4;
                        cropBtn.setImageResource(R.mipmap.ic_4x);
                    }
                }

            });


            //cancel Button
            cancelBtn = (ImageButton) findViewById(R.id.cancelButton);
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imagePreview.setVisibility(View.GONE);
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), fileName +"not exist", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadGui4(){

        //image status icon
        switch(i){

            case 1:{imageStatus.setVisibility(View.VISIBLE);
                imageStatus.setImageResource(R.mipmap.ic_sts14);
                break;}
            case 2:{imageStatus.setImageResource(R.mipmap.ic_sts24);
                break;}
            case 3:{imageStatus.setImageResource(R.mipmap.ic_sts34);
                break;}
            case 4:{imageStatus.setImageResource(R.mipmap.ic_sts44);
                break;}
            case 5:{
                String scount=Integer.toString(4);
                Intent intent= new Intent("com.digitalsnipers.shameel.sample.SrvrActivity");
                intent.putExtra("COUNT", scount);
                //intent.putExtra("OPFOLDER", scount);
                startActivity(intent);
            }

            default:break;
        }
    }
    private void loadGui9(){

        //image status icon
        switch(i){

            case 1:{imageStatus.setVisibility(View.VISIBLE);
                imageStatus.setImageResource(R.mipmap.ic_sts19);
                break;}
            case 2:{imageStatus.setImageResource(R.mipmap.ic_sts29);
                break;}
            case 3:{imageStatus.setImageResource(R.mipmap.ic_sts39);
                break;}
            case 4:{imageStatus.setImageResource(R.mipmap.ic_sts49);
                break;}
            case 5:{imageStatus.setImageResource(R.mipmap.ic_sts59);
                break;}
            case 6:{imageStatus.setImageResource(R.mipmap.ic_sts69);
                break;}
            case 7:{imageStatus.setImageResource(R.mipmap.ic_sts79);
                break;}
            case 8:{imageStatus.setImageResource(R.mipmap.ic_sts89);
                break;}
            case 9:{imageStatus.setImageResource(R.mipmap.ic_sts99);
                break;}
            case 10:{
                String scount=Integer.toString(9);
                Intent intent= new Intent("com.digitalsnipers.shameel.sample.SrvrActivity");
                intent.putExtra("COUNT", scount);
               // intent.putExtra("OPFOLDER", scount);
                startActivity(intent);
            }
            default:break;
        }
    }

    String sFileName;
    private void cropImg()
    {
        int h =displayCamera.getH();
        int w =displayCamera.getW();
        Toast.makeText(getApplicationContext(),"current resolution\n"+Integer.toString(h)+"*"+Integer.toString(w),Toast.LENGTH_SHORT).show();



        Uri imageUri = Uri.parse(fileName);


        //try{
            //Bitmap bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
        //Bitmap bmp = null;

            //bmp = getThumbnail(imageUri);
            Bitmap bmp = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(fileName), 1600, 900);
            if(bmp==null)
            {Toast.makeText(getApplicationContext(),"bmp is null",Toast.LENGTH_SHORT).show();}



        Rect rect = new Rect(-50, -25, 150, 75);
            assert(rect.left < rect.right && rect.top < rect.bottom);
            Bitmap resultBmp = Bitmap.createBitmap(rect.right-rect.left, rect.bottom-rect.top, Bitmap.Config.ARGB_8888);
            new Canvas(resultBmp).drawBitmap(bmp , -rect.left, -rect.top, null);

            //saving
            File imageFile = new File(outputFolder,/*Integer.toString(k)+*/"10.jpg");
            sFileName = imageFile.getAbsolutePath();

            if(imageFile==null)
            {
                return;
            }
            else{
                try{
                    FileOutputStream fos = new FileOutputStream(imageFile);
                    fos.write(data);
                    fos.close();
                }catch (IOException e)
                {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"image not croped",Toast.LENGTH_SHORT).show();
                }
            }

        //}catch (IOException e){
            Toast.makeText(getApplicationContext(),"image not created",Toast.LENGTH_SHORT).show();
            //e.printStackTrace();

        //}

    }

    //Background handler
    private HandlerThread mBgHandlerThread;
    private Handler mBgHandler;

    private void startBgThread(){
        mBgHandlerThread = new HandlerThread("StitchCAM");
        mBgHandlerThread.start();
        mBgHandler = new Handler(mBgHandlerThread.getLooper());
    }

    private void stopBgThread(){
        mBgHandlerThread.quitSafely();
        try {
            mBgHandlerThread.join();
            mBgHandlerThread = null;
            mBgHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }




}
