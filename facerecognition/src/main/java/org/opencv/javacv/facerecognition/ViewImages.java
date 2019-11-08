package org.opencv.javacv.facerecognition;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
//import android.provider.MediaStore.Files;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

public class ViewImages extends Activity implements
        AdapterView.OnItemSelectedListener, ViewSwitcher.ViewFactory {

    Button      buttonDel;
    PersonList thelabels;
    Bitmap      bmlist[];
    String      namelist[];
    Gallery     g;
    TextView    name;
    ImageButton buttonBack;
    int count    = 0  ;
    String mPath = "" ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_view_images);

        name       = (TextView)      findViewById( R.id.textView1 )    ;
        buttonBack = (ImageButton)   findViewById( R.id.imageButton1 ) ;
        mSwitcher  = (ImageSwitcher) findViewById( R.id.switcher )     ;
        mSwitcher  . setFactory(this);
        mSwitcher  . setInAnimation (AnimationUtils.loadAnimation(this, android.R.anim.fade_in ));
        mSwitcher  . setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));

        Bundle bundle   = getIntent().getExtras()   ;
        mPath           = bundle.getString("path")  ;
        thelabels       = new PersonList(mPath)         ;
        thelabels       . Read()                    ;
        count           = 0                         ;

        int max = thelabels.max() ;
        for( int i=0 ; i<=max ;i ++ )
        {
            if ( thelabels . get(i) != "")
            {
                count++;
            }
        }

        bmlist      =   new Bitmap[count];
        namelist    =   new String[count];
        count       =   0;
        for ( int i=0 ; i<=max ; i++ )
        {
            if ( thelabels . get(i) != "")
            {
                File root                = new File( mPath )    ;
                final String fname       = thelabels.get( i )   ;
                FilenameFilter pngFilter = new FilenameFilter()
                {
                    public boolean accept( File dir , String name )
                    {
                        return name.toLowerCase().startsWith( fname.toLowerCase() + "-" );
                    }
                };
                File[] imageFiles = root.listFiles( pngFilter );
                if ( imageFiles.length > 0 )
                {
                    InputStream is;
                    try
                    {
                        is              =   new FileInputStream( imageFiles[0] )  ;
                        bmlist[count]   =   BitmapFactory.decodeStream( is )      ;
                        namelist[count] =   thelabels.get (i )                    ;

                    }
                    catch ( FileNotFoundException e )
                    {
                        Log.e ( "File erro" , e.getMessage() + " " + e.getCause() );
                        e.printStackTrace();
                    }
                }
                count++;
            }
        }

        g   = (Gallery) findViewById( R.id.gallery1 ) ;
        g   . setAdapter( new ImageAdapter( this ) )  ;
        g   . setOnItemSelectedListener( this )       ;

        buttonBack.setOnClickListener( new View.OnClickListener()
        {
            public void onClick(View v)
            {
                finish();
            }
        });
    }


    public void onItemSelected( AdapterView<?> parent , View v , int position , long id )
    {
        mSwitcher.setImageDrawable( new BitmapDrawable ( getResources() , bmlist[position]));
        name.setText( namelist[position] );
    }

    public void onNothingSelected(AdapterView<?> parent) {

    }

    public View makeView()
    {
        ImageView i = new ImageView(this);
        i.setBackgroundColor(0xFF000000);
        i.setScaleType(ImageView.ScaleType.FIT_CENTER);
        i.setLayoutParams(new ImageSwitcher.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        return i;
    }

    private ImageSwitcher mSwitcher;
    public class ImageAdapter extends BaseAdapter
    {
        public ImageAdapter( Context c )
        {
            mContext = c ;
        }

        public int getCount()
        {
            return count;
        }

        public Object getItem( int position )
        {
            return bmlist[position];
        }

        public long getItemId( int position )
        {
            return position;
        }

        public View getView( int position , View convertView , ViewGroup parent)
        {
            ImageView i = new ImageView(mContext);
            i.setImageBitmap(bmlist[position]);
            i.setAdjustViewBounds(true);
            i.setLayoutParams(new Gallery.LayoutParams( LayoutParams.WRAP_CONTENT , LayoutParams.WRAP_CONTENT));
            return i;
        }
        private Context mContext;
    }
}