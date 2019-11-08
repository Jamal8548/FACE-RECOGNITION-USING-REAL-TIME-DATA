package org.opencv.javacv.facerecognition;

import java.io.File;
//import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
//import org.opencv.contrib.FaceRecognizer;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.objdetect.CascadeClassifier;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import static android.os.Environment.getExternalStorageDirectory;

public class Recognition extends Activity implements CvCameraViewListener2 {

    public static final String MY_URL = "http://real-time-face-recognition.000webhostapp.com/Mobile/SeacrhRecord.php?CNIC=";
    public static final String KEY_FIRSTNAME    = "FirstName"   ;
    public static final String KEY_LASTNAME     = "LastName"    ;
    public static final String KEY_CNIC         = "CNIC"        ;
    public static final String KEY_GENDER       = "Gender"      ;
    public static final String KEY_DEPARTMENT   = "Department"  ;
    public static final String KEY_STREET       = "Street"      ;
    public static final String KEY_CITY         = "City"        ;
    public static final String KEY_STATE        = "State"       ;
    public static final String KEY_ZIP          = "Zip"         ;
    public static final String KEY_COUNTRY      = "Country"     ;
    public static final String KEY_CONTACT      = "Contact"     ;
    public static final String KEY_DATEOFBIRTH  = "DateOfBirth" ;
    public static final String KEY_JOININGDATE  = "JoiningDate" ;
    public static final String KEY_EMAIL        = "Email"       ;
    public static final String JSON_ARRAY       = "result"      ;

    String hFirstName   =   null    ;
    String hLastName    =   null    ;
    String hCNIC        =   null    ;
    String hGender      =   null    ;
    String hDepartment  =   null    ;
    String hStreet      =   null    ;
    String hCity        =   null    ;
    String hState       =   null    ;
    String hZip         =   null    ;
    String hCountry     =   null    ;
    String hContact     =   null    ;
    String hDateOfBirth =   null    ;
    String hJoiningDate =   null    ;
    String hEmail       =   null    ;

    Bitmap              mBitmap         ;
    PersonList labelsFile      ;
    Button              buttonCatalog   ;
    Handler             mHandler        ;
    TextView            textState       ;
    TextView            textresult      ;
    ImageButton         imCamera        ;
    ToggleButton        buttonSearch    ;
    PersonRecognizer    fr              ;
    com.googlecode.javacv.cpp.opencv_contrib.FaceRecognizer faceRecognizer ;

    String mPath            =   ""                      ;
    int[] labels            =   new int[ (int) MAXIMG]  ;
    int countImages         =   0                       ;
    ArrayList<Mat> alimgs   =   new ArrayList<Mat>()    ;

    private Mat                     mRgba               ;
    private Mat                     mGray               ;
    private File                    mCascadeFile        ;
    private MenuItem                nBackCam            ;
    private MenuItem                mFrontCam           ;
    private String[]                mDetectorName       ;
    private ImageView               Iv                  ;
    private CameraView mOpenCvCameraView   ;
    private ProgressDialog          loading             ;
    private CascadeClassifier       mJavaDetector       ;

    private int mLikely               =   999           ;
    private int faceState             =   IDLE          ;
    private int mDetectorType         =   JAVA_DETECTOR ;
    private int mChooseCamera         =   backCam       ;
    private int mAbsoluteFaceSize     =   0             ;
    private float mRelativeFaceSize   =   0.2f          ;

    public static final int IDLE                    =   2                               ;
    public static final int SEARCHING               =   1                               ;
    public static final int JAVA_DETECTOR           =   0                               ;

    private static final String TAG                 =   "FRURTM::RECOGNITION"           ;
    private static final int backCam                =   2                               ;
    private static final int frontCam               =   1                               ;
    private static final Scalar FACE_RECT_COLOR     =   new Scalar( 0 , 255 , 0 , 255 ) ;

    static final long MAXIMG = 10;
    static
    {
        OpenCVLoader.initDebug();
        System.loadLibrary( "opencv_java" );
    }

    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback( this )
    {
        @Override
        public void onManagerConnected( int status )
        {
            switch ( status )
            {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    fr       = new PersonRecognizer(mPath)  ;
                    String s = getResources().getString(R.string.Straininig)                 ;
                    fr.load();
                    try
                    {
                        InputStream is      = getResources().openRawResource( R.raw.lbpcascade_frontalface );
                        File cascadeDir     = getDir( "cascade" , Context.MODE_PRIVATE );
                        mCascadeFile        = new File( cascadeDir , "lbpcascade.xml"  );
                        FileOutputStream os = new FileOutputStream( mCascadeFile )      ;
                        byte[] buffer       = new byte[4096];
                        int bytesRead;
                        while( ( bytesRead = is.read( buffer ) ) != -1 )
                        {
                            os.write( buffer , 0 , bytesRead );
                        }
                        is.close() ;
                        os.close() ;

                        mJavaDetector = new CascadeClassifier( mCascadeFile.getAbsolutePath() );
                        if( mJavaDetector.empty() )
                        {
                            Log.e( TAG , "Failed to load cascade classifier" );
                            mJavaDetector = null ;
                        }
                        else
                        {
                            Log.i( TAG, "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());
                        }
                        cascadeDir.delete();
                    }
                    catch ( IOException e )
                    {
                        e.printStackTrace();
                        Log.e( TAG , "Failed to load cascade. Exception thrown: " + e );
                    }
                    mOpenCvCameraView.enableView();
                }break;
                default:
                {
                    super.onManagerConnected( status );
                }break;
            }
        }
    };

    public Recognition()
    {
        mDetectorName = new String[ 2 ]                     ;
        mDetectorName[ JAVA_DETECTOR ] = "Java"             ;
        Log.i( TAG, "Instantiated new " + this.getClass() ) ;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView( R.layout.activity_recognition);
        mOpenCvCameraView = (CameraView) findViewById ( R.id.tutorial3_activity_java_surface_view );
        mOpenCvCameraView.setCvCameraViewListener(this);

        mPath       = getExternalStorageDirectory() + "/facerecogOCV/" ;
        labelsFile  = new PersonList( mPath )                              ;
        Iv          = ( ImageView )findViewById ( R.id.imageView1 )    ;
        textresult  = ( TextView ) findViewById ( R.id.textView1  )    ;

        mHandler = new Handler()
        {
            @Override
            public void handleMessage( Message msg )
            {
                if (!( msg.obj == "IMG" ))
                {
                    if( mLikely < 80 )
                    {
                        textresult   . setText( msg.obj.toString() );
                        buttonSearch . setChecked(false)            ;
                        faceState    = IDLE                         ;
                        textState    . setText(getResources().getString(R.string.SIdle))              ;
                        getData ( msg.obj.toString() )              ;
                    }
                    else
                    {
                        textresult.setText( "Unknown" );
                    }
                }
            }
        };
        buttonCatalog   = (Button)       findViewById( R.id.buttonCat     );
        buttonSearch    = (ToggleButton) findViewById( R.id.buttonBuscar  );
        textState       = (TextView)     findViewById( R.id.textViewState );
        imCamera        = (ImageButton)  findViewById( R.id.imageButton1  );
        textresult      . setVisibility(View.INVISIBLE);

        buttonCatalog.setOnClickListener(new View.OnClickListener()
        {
            public void onClick( View view )
            {
                Intent i = new Intent( Recognition.this ,
                        ViewImages.class );
                i.putExtra( "path" , mPath );
                startActivity( i );
            };
        });

        imCamera.setOnClickListener( new View.OnClickListener()
        {
            public void onClick( View v )
            {
                if ( mChooseCamera == frontCam )
                {
                    mChooseCamera = backCam         ;
                    mOpenCvCameraView.setCamBack()  ;
                }
                else
                {
                    mChooseCamera = frontCam        ;
                    mOpenCvCameraView.setCamFront() ;
                }
            }
        });

        buttonSearch.setOnClickListener( new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if ( buttonSearch.isChecked() )
                {
                    if ( !fr.canPredict() )
                    {
                        buttonSearch.setChecked( false );
                        Toast.makeText( getApplicationContext() , getResources().getString( R.string.SCanntoPredic ) , Toast.LENGTH_LONG) . show() ;
                        return;
                    }
                    textState.setText( getResources().getString(R.string.SSearching) );
                    faceState = SEARCHING;
                    textresult.setVisibility( View.VISIBLE );
                }
                else
                {
                    faceState = IDLE;
                    textState.setText( getResources().getString(R.string.SIdle) );
                    textresult.setVisibility( View.INVISIBLE );
                }
            }
        });

        boolean success = (new File( mPath )).mkdirs() ;
        if ( !success )
        {
            Log.e( "Error" , "Error creating directory" );
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
        mLoaderCallback.onManagerConnected( LoaderCallbackInterface.SUCCESS );
    }

    public void onDestroy()
    {
        super.onDestroy()               ;
        mOpenCvCameraView.disableView() ;
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

    public Mat onCameraFrame( CvCameraViewFrame inputFrame )
    {
        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();

        if ( mAbsoluteFaceSize == 0 )
        {
            int height = mGray.rows();
            if ( Math.round( height * mRelativeFaceSize ) > 0 )
            {
                mAbsoluteFaceSize = Math.round( height * mRelativeFaceSize );
            }
        }

        MatOfRect faces = new MatOfRect();
        if ( mDetectorType == JAVA_DETECTOR )
        {
            if ( mJavaDetector != null )
            {
                mJavaDetector.detectMultiScale( mGray , faces, 1.1 , 2 , 2 ,
                        new Size( mAbsoluteFaceSize , mAbsoluteFaceSize ) , new Size() ) ;
            }
        }
        else
        {
            Log.e( TAG , "Detection method is not selected!" ) ;
        }

        Rect[] facesArray = faces.toArray() ;
        if ( ( facesArray.length>0 ) && ( faceState==SEARCHING ) )
        {
            Mat m   = new Mat();
            m       = mGray.submat( facesArray[0] );
            mBitmap = Bitmap.createBitmap( m.width() , m.height() , Bitmap.Config.ARGB_8888 );

            Utils.matToBitmap( m , mBitmap )    ;
            Message msg         = new Message() ;
            String textTochange = "IMG"         ;
            msg.obj             = textTochange  ;
            mHandler.sendMessage( msg )         ;

            textTochange =  fr.predict( m ) ;
            mLikely      =  fr.getProb()    ;
            msg          =  new Message()   ;
            msg.obj      =  textTochange    ;
            mHandler.sendMessage( msg )     ;
        }
        for ( int i = 0 ; i < facesArray.length ; i++ )
        {
            Core.rectangle( mRgba , facesArray[i].tl() , facesArray[i].br() , FACE_RECT_COLOR , 3 );
        }
        return mRgba;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        Log.i(TAG, "called onCreateOptionsMenu");
        if (mOpenCvCameraView.numberCameras()>1)
        {
            nBackCam  = menu.add( getResources().getString(R.string.SFrontCamera) );
            mFrontCam = menu.add( getResources().getString(R.string.SBackCamera)  );
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
        Log.i( TAG , "called onOptionsItemSelected; selected item: " + item );
        nBackCam .setChecked( false );
        mFrontCam.setChecked( false );
        if ( item == nBackCam )
        {
            mOpenCvCameraView.setCamFront() ;
            mChooseCamera = frontCam        ;
        }
        else if ( item == mFrontCam )
        {
            mChooseCamera = backCam         ;
            mOpenCvCameraView.setCamBack()  ;
        }
        item.setChecked(true);
        return true;
    }

    private void getData(String id)
    {
        if ( id.equals( "" ) )
        {
            Toast.makeText( this , "No Record Found" , Toast.LENGTH_LONG) . show();
            return;
        }
        loading     = ProgressDialog.show(this,"Please wait...","Fetching "+id+" Record",false,false);
        String url  = MY_URL+id;
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>()
        {
            @Override
            public void onResponse( String response )
            {
                loading.dismiss()   ;
                showJSON( response );
            }
        },new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                loading.dismiss();
                String message = null;
                if ( error instanceof NetworkError )
                {
                    message = "Cannot connect to Internet...Please check your connection!";
                }
                else if ( error instanceof ServerError )
                {
                    message = "The server could not be found. Please try again after some time!!";
                }
                else if ( error instanceof AuthFailureError )
                {
                    message = "Cannot connect to Internet...Please check your connection!";
                }
                else if ( error instanceof ParseError )
                {
                    message = "Parsing error! Please try again after some time!!";
                }
                else if ( error instanceof NoConnectionError )
                {
                    message = "Cannot connect to Internet...Please check your connection!";
                }
                else if ( error instanceof TimeoutError )
                {
                    message = "Connection TimeOut! Please check your internet connection.";
                }
                Toast.makeText( Recognition.this , message , Toast.LENGTH_LONG) . show() ;
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue( this );
        requestQueue.add( stringRequest );
    }

    private void showJSON( String response )
    {
        try
        {
            JSONObject jsonObject = new JSONObject(response)            ;
            JSONArray result      = jsonObject.getJSONArray(JSON_ARRAY) ;

            JSONObject data = result.getJSONObject(0);
            hFirstName      = data.getString( "First_Name"      );
            hLastName       = data.getString( "Last_Name"       );
            hCNIC           = data.getString( "CNIC"            );
            hGender         = data.getString( "Gender"          );
            hDepartment     = data.getString( "Department"      );
            hStreet         = data.getString( "Street"          );
            hCity           = data.getString( "City"            );
            hState          = data.getString( "State"           );
            hZip            = data.getString( "Postal_Code"     );
            hCountry        = data.getString( "Country"         );
            hContact        = data.getString( "Phone"           );
            hDateOfBirth    = data.getString( "DOB"             );
            hJoiningDate    = data.getString( "Joining_Date"    );
            hEmail          = data.getString( "Email"           );
        }
        catch ( JSONException e )
        {
            e.printStackTrace();
        }
        if ( hCNIC == null )
        {
            textresult.setText( "Record not Found" );
        }
        else
        {
            Display();
        }
    }

    private void UpdateHistory(String cnic)
    {
        final String tempcnic2    = cnic;
        RequestQueue requestQueue = Volley.newRequestQueue( Recognition.this );
        StringRequest request     = new StringRequest( Request.Method.POST , "http://real-time-face-recognition.000webhostapp.com/Mobile/UpdateHistory.php" , new Response.Listener<String>()
        {
            @Override
            public void onResponse( String response )
            {
                if ( response.equals ( "success" ) )
                {
                    Toast.makeText( Recognition.this , "History Updation Successfull" , Toast.LENGTH_LONG ) . show();
                }
                else
                {
                    Toast.makeText( Recognition.this , "Updation Failed" + response , Toast.LENGTH_LONG) . show() ;
                }
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse( VolleyError error )
            {
                String message = null;
                if ( error instanceof NetworkError )
                {
                    message = "Cannot connect to Internet...Please check your connection!";
                }
                else if ( error instanceof ServerError )
                {
                    message = "The server could not be found. Please try again after some time!!";
                }
                else if ( error instanceof AuthFailureError )
                {
                    message = "Cannot connect to Internet...Please check your connection!";
                }
                else if ( error instanceof ParseError )
                {
                    message = "Parsing error! Please try again after some time!!";
                }
                else if ( error instanceof NoConnectionError )
                {
                    message = "Cannot connect to Internet...Please check your connection!";
                }
                else if ( error instanceof TimeoutError )
                {
                    message = "Connection TimeOut! Please check your internet connection.";
                }
                Toast.makeText( Recognition.this , message , Toast.LENGTH_LONG ) . show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<>();

                Calendar cal            =   Calendar.getInstance( TimeZone.getTimeZone( "GMT+5:00" ) )  ;
                Date currentLocalTime   =   cal.getTime();
                DateFormat date         =   new SimpleDateFormat( "HH:mm:ss" );

                date.setTimeZone( TimeZone.getTimeZone( "GMT+5:00" ) )                                  ;

                String hTime            =   date.format(currentLocalTime)                               ;
                String hDate            =   new SimpleDateFormat("yyyy-MM-dd").format(new Date())       ;
                String hCNIC            =   tempcnic2                                                   ;

                params.put  ( "gTime"   ,   hTime   );
                params.put  ( "gDate"   ,   hDate   );
                params.put  ( "gCNIC"   ,   hCNIC   );

                return params;
            }
        };
        requestQueue.add(request);
    }

    private void Display()
    {
        UpdateHistory( hCNIC );
        Intent viewrecord = new Intent( this , DisplayResult.class )  ;
        viewrecord.putExtra( "aFirstName"    ,   hFirstName )         ;
        viewrecord.putExtra( "aLastName"     ,   hLastName )          ;
        viewrecord.putExtra( "aCNIC"         ,   hCNIC )              ;
        viewrecord.putExtra( "aGender"       ,   hGender )            ;
        viewrecord.putExtra( "aDepartment"   ,   hDepartment )        ;
        viewrecord.putExtra( "aStreet"       ,   hStreet )            ;
        viewrecord.putExtra( "aCity"         ,   hCity )              ;
        viewrecord.putExtra( "aState"        ,   hState )             ;
        viewrecord.putExtra( "aZip"          ,   hZip )               ;
        viewrecord.putExtra( "aCountry"      ,   hCountry )           ;
        viewrecord.putExtra( "aContact"      ,   hContact )           ;
        viewrecord.putExtra( "aDateofBirth"  ,   hDateOfBirth )       ;
        viewrecord.putExtra( "aJoiningDate"  ,   hJoiningDate )       ;
        viewrecord.putExtra( "aEmail"        ,   hEmail )             ;
        startActivity( viewrecord );
    }
}
