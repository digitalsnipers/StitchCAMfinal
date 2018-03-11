package com.digitalsnipers.shameel.sample;


import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;




import com.bumptech.glide.Glide;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.DraweeView;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainCamActivity extends AppCompatActivity {

    Camera camera;
    FrameLayout frameLayout;
    DisplayCamera displayCamera;
    ImageButton shutterBtn;
    //initialising Fresco
    //Fresco.initialize(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_cam);

        //creating output folder if does not exist
        createOutputFolder();

        frameLayout = (FrameLayout)findViewById(R.id.frameLayout);


        //open the camera
        camera = Camera.open();

        displayCamera=new DisplayCamera(this, camera);

        frameLayout.addView(displayCamera);

        shutterBtn = (ImageButton) findViewById(R.id.shutterButton);
        shutterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(camera!=null) {
                    camera.takePicture(null, null, mPictureCallback);

                    //camera.stopPreview();

                    //dispImg();

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
    }

    @Override
    public void onResume()
    {
        super.onResume();
        //camera.startPreview();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
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

                        Toast.makeText(getApplicationContext(),fileName+"saved",Toast.LENGTH_SHORT).show();

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

        File imageFile = new File(outputFolder,"primary.jpg");
        fileName = imageFile.getAbsolutePath();
        return  imageFile;
    }

    ImageView imageView;
    private void dispImg() {
        //imageView.setVisibility(View.VISIBLE);
        if (fileName != null) {
            Uri imageUri = Uri.parse(fileName);
            DraweeView draweeView = (DraweeView) findViewById(R.id.sdvImage);
            draweeView.setImageURI(imageUri);
        } else {
            Toast.makeText(getApplicationContext(), fileName +"not exist", Toast.LENGTH_SHORT).show();
        }
    }




}
