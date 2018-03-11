package com.digitalsnipers.shameel.sample;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

/**
 * Created by Shameel on 3/10/2018.
 */

public class DisplayCamera extends SurfaceView implements SurfaceHolder.Callback {

        Camera camera;
    SurfaceHolder holder;
    public DisplayCamera(Context context,Camera camera) {
        super(context);
        this.camera = camera;
        holder = getHolder();
        holder.addCallback(this);
    }

    //when surfaceholder class initiated it calls below functions
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Camera.Parameters params = camera.getParameters();

        //listing available resolutions
        List<Camera.Size> sizes = params.getSupportedPictureSizes();
        Camera.Size mSize = null;

        for(Camera.Size size : sizes)
        {
            mSize=size;
        }

        //change the orientation

        if(this.getResources().getConfiguration().orientation!= Configuration.ORIENTATION_LANDSCAPE)
        {
            params.set("orientation","portrait");
            camera.setDisplayOrientation(90);
            params.setRotation(90);
        }
        else
        {
            params.set("orientation","landscape");
            camera.setDisplayOrientation(0);
            params.setRotation(0);
        }

        //chosing resolution
        params.setPictureSize(mSize.width,mSize.height);

        //enabling continues autofocusing
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

        //set these parameters
        camera.setParameters(params);
        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        }catch (IOException e)
        {
            e.printStackTrace();
        }


    }
}

