package org.opencv.javacv.facerecognition;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;

import org.opencv.android.JavaCameraView;

public class CameraView extends JavaCameraView {

    private static final String TAG = "FRURTM::CameraView";
    public CameraView(Context context , AttributeSet attrs )
    {
        super( context , attrs );
    }

    public void setCamFront()
    {
        disconnectCamera();
        setCameraIndex( org.opencv.android.CameraBridgeViewBase.CAMERA_ID_FRONT );
        connectCamera ( getWidth() , getHeight() );
    }

    public void setCamBack()
    {
        disconnectCamera();
        setCameraIndex( org.opencv.android.CameraBridgeViewBase.CAMERA_ID_BACK );
        connectCamera ( getWidth() , getHeight() );
    }

    public int numberCameras()
    {
        return	Camera.getNumberOfCameras();
    }
}
