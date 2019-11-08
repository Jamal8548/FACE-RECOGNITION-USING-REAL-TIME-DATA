package org.opencv.javacv.facerecognition;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FirstInstallation extends Activity {

    ProgressBar pb                  ;
    ImageView   imageview           ;
    int         counter     = 0     ;
    String      myJSON , substr     ;
    JSONArray peoples       = null  ;
    ArrayList<String> CNICLIST = new ArrayList<String>();
    ArrayList<HashMap<String, String>> personList = new ArrayList<HashMap<String, String>>();

    public static SharedPreferences pref                        ;
    private static final String TAG_RESULTS = "result"          ;
    private static final String TAG_ADMIN   = "Admin_ID"        ;
    private static final String TAG_PASS    = "Admin_Password"  ;
    private static final String TAG_ROLE    = "Admin_Role"      ;
    private static final String TAG_CNIC    = "CNIC"            ;
    private static final String TAG_URL     = "https://real-time-face-recognition.000webhostapp.com/Mobile/upload/";

    boolean internet_connection()
    {
        ConnectivityManager cm    = ( ConnectivityManager )this.getSystemService( Context.CONNECTIVITY_SERVICE );
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return  isConnected;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_installation);

        pb           = (ProgressBar)findViewById( R.id.progressBar );
        File folder  = new File( Environment.getExternalStorageDirectory() + "/facerecogOCV");

        if (!folder.exists()) {
            folder.mkdirs();
        }

        pref         = getSharedPreferences( "FirstInstall" , Context.MODE_PRIVATE );
        if( pref.getBoolean( "activity_executed" , false ) )
        {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            finish();
        }
        else
        {
            if( !internet_connection() )
            {
                Toast.makeText( this , "No Internet Connection" , Toast.LENGTH_SHORT) . show();
            }
            else
            {
                Downloadadmins();
                SharedPreferences.Editor ed = pref.edit();
                ed.putBoolean( "activity_executed" , true );
                ed.putInt( "counter" , counter );
                ed.apply();
                Intent intent = new Intent( this , Login.class );
                startActivity( intent );
                finish();
            }
        }
    }

    public void saveImage(Context context, Bitmap b, String imageName)
    {
        ContextWrapper cw    = new ContextWrapper( getApplicationContext());
        File mypath          = new File( Environment.getExternalStorageDirectory() , "facerecogOCV/" + imageName);
        FileOutputStream fos = null;
        try
        {
            fos = new FileOutputStream( mypath );
            b.compress( Bitmap.CompressFormat.JPEG , 100 , fos );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        finally
        {
            if ( fos != null )
            {
                try
                {
                    fos.close();
                }
                catch ( IOException e )
                {
                    e.printStackTrace();
                }
            }
        }
    }

    private class DownloadImage extends AsyncTask<String, Void, Bitmap>
    {
        private String TAG = "DownloadImage";
        private Bitmap downloadImageBitmap( String sUrl )
        {
            Bitmap bitmap = null;
            try
            {
                InputStream inputStream = new URL( sUrl ).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
            }
            catch ( Exception e )
            {
                Log.d( TAG , "Exception 1, Something went wrong!" );
                e.printStackTrace();
            }
            substr = sUrl.substring(67);
            substr = substr.substring(0, substr.length() - 4);
            return bitmap;
        }

        @Override
        protected Bitmap doInBackground(String... params)
        {
            return downloadImageBitmap(params[0]);
        }

        protected void onPostExecute(Bitmap result)
        {
            saveImage( getApplicationContext() , result , substr + "-" + counter + ".jpg" );
            counter++;
            updatelistfile( substr );
            substr = null;
        }
    }

    public void updatelistfile( String labeltosave )
    {
        String previousdata = "";
        String namesfileaddress = Environment.getExternalStorageDirectory() + "/facerecogOCV/staff.txt";
        try
        {
            FileInputStream fstream         = new FileInputStream(namesfileaddress);
            BufferedReader br               = new BufferedReader(new InputStreamReader(fstream));
            InputStreamReader isr           = new InputStreamReader(fstream);
            BufferedReader bufferedReader   = new BufferedReader(isr);
            StringBuilder sb                = new StringBuilder();
            String line;
            while ( (line = bufferedReader.readLine() ) != null )
            {
                sb.append( line );
            }
            previousdata = sb.toString();
            //Toast.makeText(this, previousdata, Toast.LENGTH_SHORT).show();
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }

        int lastindex = 0, nextindex    ;
        if ( previousdata.length() == 0 )
        {
            nextindex = 1;
        }
        else {
            Pattern p = Pattern.compile(".*,\\s*(.*)");
            Matcher m = p.matcher( previousdata );
            m.find();
            String temporarylastindex = "" + m.group( 1 );
            Toast.makeText( this , temporarylastindex, Toast.LENGTH_SHORT).show();
            try
            {
                lastindex = Integer.parseInt(temporarylastindex);
            }
            catch ( NumberFormatException nfe )
            {
                System.out.println( "Could not parse " + nfe );
            }
            Toast.makeText( this , "" + lastindex, Toast.LENGTH_SHORT).show();
            nextindex = lastindex + 1;

        }
        try
        {
            File file         = new File(namesfileaddress)  ;
            FileWriter fw     = new FileWriter(file, true)  ;
            BufferedWriter bw = new BufferedWriter(fw)      ;
            PrintWriter pw    = new PrintWriter(bw)         ;
            pw.println( labeltosave + "," + nextindex )     ;
            pw.close()                                      ;
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }

    private void Downloadadmins()
    {
        getData();
    }

    private void getData()
    {
        class GetDataJSON extends AsyncTask<String, Void, String>
        {
            @Override
            protected String doInBackground(String... params)
            {
                InputStream inputStream = null;
                String result           = null;
                try
                {
                    URL url = new URL("http://real-time-face-recognition.000webhostapp.com/Mobile/FI.php");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");

                    StringBuilder sb        = new StringBuilder();
                    inputStream             = new BufferedInputStream(con.getInputStream());
                    BufferedReader reader   = new BufferedReader(new InputStreamReader(inputStream));
                    String line             = null;

                    while (( line = reader.readLine()) != null )
                    {
                        sb.append(line).append("\n");
                    }
                    result = sb.toString();
                }
                catch ( Exception e )
                {
                    e.printStackTrace();
                }
                finally
                {
                    try
                    {
                        if (inputStream != null)
                        {
                            inputStream.close();
                        }
                    }
                    catch (Exception squish)
                    {
                        squish.printStackTrace();
                    }
                }
                return result;
            }

            @Override
            protected void onPostExecute(String result)
            {
                myJSON = result;
                Save();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute();
    }

    private void Save()
    {
        try {
            JSONObject jsonObj = new JSONObject(myJSON)             ;
            peoples            = jsonObj.getJSONArray(TAG_RESULTS)  ;
            int k              = 0                                  ;
            int temp2          = peoples.length()                   ;
            //Toast.makeText(this, temp2 + "", Toast.LENGTH_SHORT).show();
            for ( int i = 0; i < temp2; i++)
            {
                JSONObject c    = peoples.getJSONObject(i)  ;
                String hisid    = c.getString(TAG_ADMIN)    ;
                String pass     = c.getString(TAG_PASS)     ;
                String role     = c.getString(TAG_ROLE)     ;
                String cnic     = c.getString("CNIC")       ;

                HashMap<String, String> persons = new HashMap<>();
                persons.put( TAG_ADMIN, hisid );
                persons.put( TAG_PASS , pass  );
                persons.put( TAG_ROLE , role  );
                persons.put( TAG_CNIC , cnic  );
                personList.add( persons );
                CNICLIST.add( cnic );
            }

        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        int temp22 = 100/CNICLIST.size();
        int temp33 = 0                  ;
        for (int i = 0; i < CNICLIST.size(); i++)
        {
            temp33      = temp33 + temp22 ;
            String temp = "Downloading image:" + i + " of " + CNICLIST.size();
            //Toast.makeText(this, temp, Toast.LENGTH_SHORT).show();
            String admin = CNICLIST.get(i);
            //Toast.makeText(this, admin, Toast.LENGTH_SHORT).show();
            String picaddress = TAG_URL + admin + ".png";
            //Toast.makeText(this, picaddress, Toast.LENGTH_SHORT).show();
            new FirstInstallation.DownloadImage().execute(picaddress);
            pb.setProgress(temp33);
        }
    }
}