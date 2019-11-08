package org.opencv.javacv.facerecognition;

import android.app.IntentService;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
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

public class SyncImages extends IntentService {

    String myJSON;
    private static final String TAG = SyncImages.class.getName();
    private static final String TAG_RESULTS = "result";
    private static final String TAG_NAME = "Image_Name";
    private static final String TAG_ADDRESS = "Image_Address";
    private static final String TAG_CNIC = "CNIC";
    ImageView preiew;
    JSONArray peoples = null;
    ArrayList<HashMap<String, String>> personList =   new ArrayList<HashMap<String, String>>()    ;
    ListView list;
    String picname,piccnic,picaddress,substr;
    boolean internet_connection()
    {
        ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public SyncImages() {
        super("SyncImages");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        if (!internet_connection())
        {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
        else
        {
            getData();
        }
    }
    public void saveImage(Context context, Bitmap b, String imageName) {
//        FileOutputStream foStream;
//        try {
//            foStream = context.openFileOutput(imageName, Context.MODE_PRIVATE);
//            b.compress(Bitmap.CompressFormat.PNG, 100, foStream);
//            foStream.close();
//        } catch (Exception e) {
//            Log.d("saveImage", "Exception 2, Something went wrong!");
//            e.printStackTrace();
//        }
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        //File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        //String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
        //String directory = Environment.getExternalStorageDirectory(), "facerecogOCV";
        // Create imageDir
        File mypath=new File(Environment.getExternalStorageDirectory(),"facerecogOCV/"+imageName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            b.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                assert fos != null;
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {
        private String TAG = "DownloadImage";
        private Bitmap downloadImageBitmap(String sUrl) {
            Bitmap bitmap = null;
            try {
                InputStream inputStream = new URL(sUrl).openStream();   // Download Image from URL
                bitmap = BitmapFactory.decodeStream(inputStream);       // Decode Bitmap
                inputStream.close();
            } catch (Exception e) {
                Log.d(TAG, "Exception 1, Something went wrong!");
                e.printStackTrace();
            }
            substr =  sUrl.substring(66);
            substr = substr.substring(0, substr.length() - 4);
//            Toast.makeText(SyncPictures.this, substr, Toast.LENGTH_SHORT).show();
            return bitmap;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            return downloadImageBitmap(params[0]);
        }

        protected void onPostExecute(Bitmap result) {
//            preiew.setImageBitmap(result);
            saveImage(getApplicationContext(), result, substr+"-0.jpg");
            updatelistfile(substr);
            substr=null;
        }
    }

    public void updatelistfile(String labeltosave)
    {
        String previousdata="";
        String namesfileaddress = Environment.getExternalStorageDirectory()+"/facerecogOCV/faces.txt";
        try {

            FileInputStream fstream = new FileInputStream(namesfileaddress);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

            InputStreamReader isr = new InputStreamReader(fstream);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            previousdata = sb.toString();
            Toast.makeText(this, previousdata, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        int lastindex=0,nextindex;
        if(previousdata.length()==0)
        {
            nextindex=1;
        }
        else {
            //char templastindex = previousdata.charAt(previousdata.length() - 1);
            Pattern p = Pattern.compile(".*,\\s*(.*)");
            Matcher m = p.matcher(previousdata);
            m.find();
            String temporarylastindex = "" + m.group(1);
            Toast.makeText(this, temporarylastindex, Toast.LENGTH_SHORT).show();
            //String temporarylastindex = "" + templastindex;
            //lastindex= Character.getNumericValue(temporarylastindex.charAt(0));
            try {
                lastindex = Integer.parseInt(temporarylastindex);
            } catch(NumberFormatException nfe)
            {
                System.out.println("Could not parse " + nfe);
            }
            Toast.makeText(this, "" + lastindex, Toast.LENGTH_SHORT).show();
            nextindex = lastindex+1;

        }


        try {
            File file = new File(namesfileaddress);
            FileWriter fw = new FileWriter(file,true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);
            //pw.println();
            pw.println(labeltosave+","+nextindex);
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void getData()
    {
        class GetDataJSON extends AsyncTask<String, Void, String>
        {
            @Override
            protected String doInBackground(String... params)
            {
                InputStream inputStream = null;
                String result = null;
                try
                {
                    URL url = new URL("http://real-time-face-recognition.000webhostapp.com/Mobile/FaceRecord.php");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");

                    StringBuilder sb = new StringBuilder();
                    inputStream = new BufferedInputStream(con.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line = null;
                    while ((line = reader.readLine()) != null)
                    {
                        sb.append(line).append("\n");
                    }
                    result = sb.toString();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    try
                    {
                        if(inputStream != null)
                            inputStream.close();
                    }
                    catch(Exception squish)
                    {
                        squish.printStackTrace();
                    }
                }
                return result;
            }
            @Override
            protected void onPostExecute(String result)
            {
                myJSON=result;
                showList();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute();
    }

    private void showList()
    {
        try
        {
            JSONObject jsonObj  = new JSONObject(myJSON);
            peoples             = jsonObj.getJSONArray(TAG_RESULTS);
            int k=0;
            Toast.makeText(this, "Length of json result is:"+peoples.length(), Toast.LENGTH_SHORT).show();
            for(int i=0;i<peoples.length();i++)
            {
                JSONObject c    = peoples.getJSONObject(i)  ;
                String name2     = c.getString(TAG_NAME)     ;
                String address2  = c.getString(TAG_ADDRESS)  ;
                String cnic2     = c.getString(TAG_CNIC)     ;
                HashMap<String,String> persons2 = new HashMap<>();
                //Toast.makeText(this, cnic2, Toast.LENGTH_SHORT).show();
                //persons.put(TAG_ID,Integer.toString(++k));

                persons2.put(TAG_NAME,name2);
                persons2.put(TAG_ADDRESS,address2);
                persons2.put(TAG_CNIC,cnic2);

                personList.add(persons2);
                Toast.makeText(this, "Length of personlist now :"+personList.size(), Toast.LENGTH_SHORT).show();
            }
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }

        for(int i=0; i<personList.size(); i++)
        {
            picname=null;
            picaddress=null;
            piccnic=null;
            HashMap<String,String> persons1 = personList.get(i);
            String picname = persons1.get(TAG_NAME);
            String picaddress = persons1.get(TAG_ADDRESS);
            String piccnic = persons1.get(TAG_CNIC);
            Toast.makeText(this, picaddress, Toast.LENGTH_SHORT).show();
            //preiew.setImageBitmap(new DownloadImage().execute(picaddress));
            new DownloadImage().execute(picaddress);
        }
    }

}
