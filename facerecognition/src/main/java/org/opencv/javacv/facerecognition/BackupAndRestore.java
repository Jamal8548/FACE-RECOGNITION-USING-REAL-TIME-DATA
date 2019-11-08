package org.opencv.javacv.facerecognition;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class BackupAndRestore extends Activity {
    Button Backup_Button,Restore_Button;
    private ProgressDialog loading;
    JSONArray peoples = null;
    String backupslist, nameoffile=null;
    String[]names;
    boolean internet_connection()
    {
        ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_and_restore);

        Backup_Button  = (Button) findViewById(R.id.backupbutton);
        Restore_Button = (Button) findViewById(R.id.restorebutton);

        Backup_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if (internet_connection())
            {
                BackupDatabase();
            }
            }
        });
        Restore_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if (internet_connection())
            {
                DisplayList();
            }
            }
        });
    }
    void BackupDatabase()
    {
        loading = ProgressDialog.show(this,"Please wait...","Backing up...",false,false);
        RequestQueue requestQueue = Volley.newRequestQueue(BackupAndRestore.this);
        StringRequest request = new StringRequest(Request.Method.POST, "http://real-time-face-recognition.000webhostapp.com/Mobile/CreateBackup.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                if (response.equals("success"))
                {
                    loading.dismiss();
                    Toast.makeText(BackupAndRestore.this, "Backup created successfully", Toast.LENGTH_LONG).show();
                }
                else
                {
                    loading.dismiss();
                    Toast.makeText(BackupAndRestore.this, "Backup Creation Failed"+response, Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(BackupAndRestore.this,error.toString(), Toast.LENGTH_LONG).show();
            }
        }
        ){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                return params;
            }
        };

        requestQueue.add(request);
    }

    void RestoreDatabase()
    {
        loading = ProgressDialog.show(this,"Please wait...","Restoring...",false,false);
        RequestQueue requestQueue = Volley.newRequestQueue(BackupAndRestore.this);
        StringRequest request = new StringRequest(Request.Method.POST, "http://real-time-face-recognition.000webhostapp.com/Mobile/RestoreBackup.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                if (response.equals("success"))
                {

                    loading.dismiss();
                    Toast.makeText(BackupAndRestore.this, "Backup Restored successfully", Toast.LENGTH_LONG).show();
                }
                else
                {
                    //                                                                             loading.dismiss();
                    Toast.makeText(BackupAndRestore.this, "Backup Restoration Failed"+response, Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(BackupAndRestore.this,error.toString(), Toast.LENGTH_LONG).show();
            }
        }
        ){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                String filename = "Backup-21-07-2017_10:24:23.txt";
                params.put("File",nameoffile);
                return params;
            }
        };

        requestQueue.add(request);
    }
    void DisplayList()
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
                    URL url = new URL("http://real-time-face-recognition.000webhostapp.com/Mobile/ListBackupFiles.php");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");

                    StringBuilder sb = new StringBuilder();
                    inputStream = new BufferedInputStream(con.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    int i=0;
                    String line = null;
                    while ((line = reader.readLine()) != null)
                    {
                        sb.append(line).append("\n");
                        //names[i] = sb.toString();
                        //Toast.makeText(BackupAndRestore.this, line , Toast.LENGTH_SHORT).show();
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
                backupslist=result;
                names = backupslist.split(" ");
                displaypopup();
//                for(int z=0;z<names.length;z++)
//                {
//                    Toast.makeText(BackupAndRestore.this, names[z], Toast.LENGTH_SHORT).show();
//                }
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute();
    }

    private void displaypopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select a backup file to restore");
        builder.setItems(names, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                // Do something with the selection
                nameoffile=names[item];
                Toast.makeText(BackupAndRestore.this, nameoffile, Toast.LENGTH_SHORT).show();
                RestoreDatabase();

                //mDoneButton.setText(items[item]);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
