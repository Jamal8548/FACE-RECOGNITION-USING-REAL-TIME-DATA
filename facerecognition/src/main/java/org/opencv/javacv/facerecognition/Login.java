package org.opencv.javacv.facerecognition;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static android.os.Environment.getExternalStorageDirectory;

//import java.io.FileNotFoundException;
//import org.opencv.contrib.FaceRecognizer;

public class Login extends Activity implements CvCameraViewListener2 {

    Bitmap           mBitmap;
    StaffList        labelsFile             ;
    Handler          mHandler               ;
    EditText         text                   ;
    TextView         textresult             ;
    ImageButton      imCamera               ;
    PersonRecognizer fr                     ;

    int countImages         =   0                       ;
    int[] labels            =   new int[(int)MAXIMG]    ;
    String mPath            =   ""                      ;
    ArrayList<Mat> alimgs   =   new ArrayList<Mat>()    ;
    com.googlecode.javacv.cpp.opencv_contrib.FaceRecognizer faceRecognizer  ;

    private Mat                    mRgba                ;
    private Mat                    mGray                ;
    private File                   mCascadeFile         ;
    private String[]               mDetectorName        ;
    private MenuItem               nBackCam             ;
    private MenuItem               mFrontCam            ;
    private ImageView              Iv                   ;
    private CameraView mOpenCvCameraView    ;
    private CascadeClassifier      mJavaDetector        ;

    static final long MAXIMG                    =   10  ;
    public static final int TRAINING            =   0   ;
    public static final int SEARCHING           =   1   ;
    public static final int JAVA_DETECTOR       =   0   ;

    private static final String TAG                 =   "FRURTM::Login"             ;
    private static final int backCam                =   2                           ;
    private static final int frontCam               =   1                           ;
    private static final Scalar FACE_RECT_COLOR     =   new Scalar(0, 255, 0, 255)  ;

    private int mLikely             =    999            ;
    private int faceState           =    SEARCHING      ;
    private int mChooseCamera       =    backCam        ;
    private int mDetectorType       = JAVA_DETECTOR     ;
    private int mAbsoluteFaceSize   = 0                 ;
    private float mRelativeFaceSize = 0.2f              ;


    static
    {
        OpenCVLoader.initDebug();
        System.loadLibrary("opencv_java");
    }

    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this)
    {
        @Override
        public void onManagerConnected(int status)
        {
            switch (status)
            {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    fr       = new PersonRecognizer(mPath)  ;
                    String s = getResources().getString(R.string.Straininig)                 ;
                    fr.load()                               ;

                    try
                    {
                        InputStream is      = getResources().openRawResource(R.raw.lbpcascade_frontalface);
                        File cascadeDir     = getDir("cascade", Context.MODE_PRIVATE);
                        mCascadeFile        = new File(cascadeDir, "lbpcascade.xml");
                        FileOutputStream os = new FileOutputStream(mCascadeFile);
                        byte[] buffer       = new byte[4096];

                        int bytesRead;
                        while ( ( bytesRead = is.read(buffer )) != -1 )
                        {
                            os.write( buffer , 0 , bytesRead );
                        }
                        is.close();
                        os.close();

                        mJavaDetector = new CascadeClassifier( mCascadeFile.getAbsolutePath() );
                        if ( mJavaDetector.empty() )
                        {
                            Log.e( TAG , "Failed to load cascade classifier");
                            mJavaDetector = null;
                        }
                        else
                        {
                            Log.i(TAG, "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());
                        }
                        cascadeDir.delete();
                    }
                    catch ( IOException e )
                    {
                        e.printStackTrace();
                        Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
                    }
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public Login()
    {
        mDetectorName = new String[2];
        mDetectorName[JAVA_DETECTOR] = "Java";
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_login);

        mOpenCvCameraView = (CameraView) findViewById(R.id.tutorial3_activity_java_surface_view);
        mOpenCvCameraView . setCvCameraViewListener(this);
        mOpenCvCameraView . setCamFront();

        mPath       =   getExternalStorageDirectory()+"/facerecogOCV/";
        labelsFile  =   new StaffList(mPath);

        Iv          =   (ImageView) findViewById(R.id.imageView1);
        textresult  =   (TextView)  findViewById(R.id.textView1) ;

        mHandler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                String nameofadmin = msg.obj.toString();
                //Toast.makeText(Login.this, nameofadmin, Toast.LENGTH_SHORT).show();

                if( mLikely <60 && nameofadmin != "Unknown" && nameofadmin != "IMG" )
                {
                    Toast.makeText( Login.this , "Welcome "+nameofadmin , Toast.LENGTH_SHORT ). show();
                    Intent login = new Intent( Login.this , HomeScreen.class );
                    startActivity(login);
                    finish();
                }
                else
                {
                    textresult.setText("NO STAFF MEMBER FOUND");
                }
            }
        };

        boolean success = ( new File ( mPath ) ).mkdirs();
        if ( !success )
        {
            Log.e("Error","Error creating directory");
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if ( mOpenCvCameraView != null )
        {
            mOpenCvCameraView.disableView();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        mLoaderCallback.onManagerConnected( LoaderCallbackInterface . SUCCESS );
    }

    public void onDestroy()
    {
        super.onDestroy();
        mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted( int width , int height )
    {
        mGray = new Mat();
        mRgba = new Mat();
    }

    public void onCameraViewStopped()
    {
        mGray.release();
        mRgba.release();
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame)
    {
        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();

        if ( mAbsoluteFaceSize == 0)
        {
            int height = mGray.rows();
            if (Math.round(height * mRelativeFaceSize) > 0)
            {
                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
            }
        }

        MatOfRect faces = new MatOfRect();
        if ( mDetectorType == JAVA_DETECTOR )
        {
            if (mJavaDetector != null)
            {
                mJavaDetector.detectMultiScale( mGray , faces , 1.1 , 2 , 2 ,
                        new Size( mAbsoluteFaceSize , mAbsoluteFaceSize ), new Size());
            }
        }
        else
        {
            Log.e( TAG , "Detection method is not selected!" );
        }

        Rect[] facesArray = faces.toArray();
        if (( facesArray.length == 1 ) && ( faceState == TRAINING )&&( countImages <MAXIMG ) && ( !text.getText().toString().isEmpty() ))
        {
            Mat  m  =   new Mat()           ;
            Rect r  =   facesArray[0]       ;
            m       =   mRgba.submat( r )   ;
            mBitmap = Bitmap.createBitmap(m.width(),m.height(), Bitmap.Config.ARGB_8888);

            Utils.matToBitmap( m , mBitmap );

            Message msg         =   new Message() ;
            String textTochange =   "IMG"         ;
            msg.obj             =   textTochange  ;
            mHandler.sendMessage(msg)             ;
            if ( countImages < MAXIMG )
            {
                fr.add( m, text.getText().toString() );
                countImages++;
            }
        }
        else
        if ( ( facesArray.length>0 ) && ( faceState==SEARCHING ))
        {
            Mat m=new Mat();
            m=mGray.submat(facesArray[0]);
            mBitmap = Bitmap.createBitmap( m.width() , m.height() , Bitmap.Config.ARGB_8888 );

            Utils.matToBitmap ( m, mBitmap );
            Message msg         = new Message();
            String textTochange = "IMG";
            msg.obj             = textTochange;

            mHandler.sendMessage(msg)           ;
            textTochange    = fr.predict( m )   ;
            mLikely         = fr.getProb()      ;
            msg             = new Message()     ;
            msg.obj         = textTochange      ;
            mHandler.sendMessage(msg)           ;
        }
        for (int i = 0; i < facesArray.length; i++)
        {
            Core.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), FACE_RECT_COLOR, 3);
        }
        return mRgba;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        Log.i( TAG , "called onCreateOptionsMenu" );
        if ( mOpenCvCameraView.numberCameras() > 1 )
        {
            nBackCam  = menu.add(getResources().getString(R.string.SFrontCamera));
            mFrontCam = menu.add(getResources().getString(R.string.SBackCamera)) ;
        }
        else
        {
            imCamera.setVisibility( View.INVISIBLE );
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Log.i(TAG, "called onOptionsItemSelected; selected item: " + item);

        nBackCam.setChecked(false);
        mFrontCam.setChecked(false);
        if ( item == nBackCam )
        {
            mOpenCvCameraView.setCamFront();
            mChooseCamera = frontCam;
        }
        else if( item == mFrontCam )
        {
            mChooseCamera = backCam;
            mOpenCvCameraView.setCamBack();
        }
        item.setChecked(true);
        return true;
    }
}
