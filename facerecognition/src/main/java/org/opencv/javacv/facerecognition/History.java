package org.opencv.javacv.facerecognition;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class History extends Activity {

    Button BID,BDate,BCNIC,BTime,SButton;
    EditText SBox;
    String myJSON;
    int a,b,c,d=0;
    private static final String TAG_RESULTS = "result"      ;
    private static final String TAG_ID      = "History_ID"  ;
    private static final String TAG_DATE    = "Date"        ;
    private static final String TAG_TIME    = "Time"        ;
    private static final String TAG_CNIC    = "CNIC"        ;

    JSONArray peoples = null;
    ArrayList<HashMap<String, String>> personList,personlistcopy;
    ListView list;

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
        setContentView(R.layout.activity_history);

        BID             =   (Button)    findViewById(R.id.id)           ;
        BDate           =   (Button)    findViewById(R.id.date)         ;
        BTime           =   (Button)    findViewById(R.id.time)         ;
        BCNIC           =   (Button)    findViewById(R.id.cnic)         ;
        SButton         =   (Button)    findViewById(R.id.searchbutton) ;
        list            =   (ListView)  findViewById(R.id.listview)     ;
        SBox            =   (EditText)  findViewById(R.id.searchbox)    ;
        personList      =   new ArrayList<HashMap<String, String>>()    ;
        personlistcopy  =   new ArrayList<HashMap<String, String>>()    ;

        if (internet_connection())
        {
            getData();
        }
        else
        {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

        SButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<HashMap<String,String>> personListTemp = new ArrayList<HashMap<String,String>>();
                String tempcnic =   SBox.getText().toString();
                personlistcopy  =   personList  ;
                Collections.sort(personlistcopy, new MapComparator(TAG_ID));
                Collections.reverse(personlistcopy);
                Collections.reverse(personlistcopy);
                for(HashMap<String, String> person : personlistcopy)
                {
                    if(tempcnic.equals(person.get("CNIC")))
                    {
                        //Toast.makeText(History.this, person.get(TAG_ID), Toast.LENGTH_SHORT).show();
                        personListTemp.add(person);
                    }
                }
                ListAdapter adaptersearch = new SimpleAdapter(
                        History.this, personListTemp, R.layout.list_item,
                        new String[]{TAG_ID,TAG_DATE,TAG_TIME,TAG_CNIC},
                        new int[]{R.id.id, R.id.date, R.id.time, R.id.cnic}
                );
                list.setAdapter(adaptersearch);
                if(personListTemp==null)
                {
                    Toast.makeText(History.this, "No Record Found", Toast.LENGTH_SHORT).show();
                }
            }
        });

        BID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(a==0)
                {
                    a=1;
                }
                if(a==2)
                {
                    BID   . setText("ID ▼");
                    BDate . setText("DATE") ;
                    BTime . setText("TIME") ;
                    BCNIC . setText("CNIC") ;
                    personlistcopy  =   personList  ;
                    Collections.sort(personList, new MapComparator(TAG_ID));
                    Collections.reverse(personList);
                    ListAdapter adapter2 = new SimpleAdapter(
                            History.this, personList, R.layout.list_item,
                            new String[]{TAG_ID,TAG_DATE,TAG_TIME,TAG_CNIC},
                            new int[]{R.id.id, R.id.date, R.id.time, R.id.cnic}
                    );
                    list.setAdapter(adapter2);
                    a=1;
                }
                else if(a==1)
                {
                    BID   . setText("ID ▲");
                    BDate . setText("DATE") ;
                    BTime . setText("TIME") ;
                    BCNIC . setText("CNIC") ;
                    personlistcopy  =   personList  ;
                    Collections.sort(personList, new MapComparator(TAG_ID));
                    ListAdapter adapter2 = new SimpleAdapter(
                            History.this, personList, R.layout.list_item,
                            new String[]{TAG_ID,TAG_DATE,TAG_TIME,TAG_CNIC},
                            new int[]{R.id.id, R.id.date, R.id.time, R.id.cnic}
                    );
                    list.setAdapter(adapter2);
                    a=2;
                }
            }
        });
        BDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(b==0){b=1;}
                if(b==2)
                {
                    BDate . setText("DATE ▼");
                    BID   . setText("ID")     ;
                    BTime . setText("TIME")   ;
                    BCNIC . setText("CNIC")   ;
                    personlistcopy  =   personList  ;
                    Collections.sort(personList, new MapComparator(TAG_DATE));
                    Collections.reverse(personList);
                    ListAdapter adapter2 = new SimpleAdapter(
                            History.this, personList, R.layout.list_item,
                            new String[]{TAG_ID,TAG_DATE,TAG_TIME,TAG_CNIC},
                            new int[]{R.id.id, R.id.date, R.id.time, R.id.cnic}
                    );
                    list.setAdapter(adapter2);
                    b=1;
                }
                else if(b==1)
                {
                    BDate . setText("DATE ▲");
                    BID   . setText("ID")     ;
                    BTime . setText("TIME")   ;
                    BCNIC . setText("CNIC")   ;
                    personlistcopy  =   personList  ;
                    Collections.sort(personList, new MapComparator(TAG_DATE));
                    ListAdapter adapter2 = new SimpleAdapter(
                            History.this, personList, R.layout.list_item,
                            new String[]{TAG_ID,TAG_DATE,TAG_TIME,TAG_CNIC},
                            new int[]{R.id.id, R.id.date, R.id.time, R.id.cnic}
                    );
                    list.setAdapter(adapter2);
                    b=2;
                }
            }
        });
        BTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(c==0){c=1;}
                if(c==2)
                {
                    BTime . setText("TIME ▼");
                    BDate . setText("DATE")   ;
                    BID   . setText("ID")     ;
                    BCNIC . setText("CNIC")   ;
                    personlistcopy  =   personList  ;
                    Collections.sort(personList, new MapComparator(TAG_TIME));
                    Collections.reverse(personList);
                    ListAdapter adapter2 = new SimpleAdapter(
                            History.this, personList, R.layout.list_item,
                            new String[]{TAG_ID,TAG_DATE,TAG_TIME,TAG_CNIC},
                            new int[]{R.id.id, R.id.date, R.id.time, R.id.cnic}
                    );
                    list.setAdapter(adapter2);
                    c=1;
                }
                else if(c==1)
                {
                    BTime . setText("TIME ▲");
                    BDate . setText("DATE")   ;
                    BID   . setText("ID")     ;
                    BCNIC . setText("CNIC")   ;
                    personlistcopy  =   personList  ;
                    Collections.sort(personList, new MapComparator(TAG_TIME));
                    ListAdapter adapter2 = new SimpleAdapter(
                            History.this, personList, R.layout.list_item,
                            new String[]{TAG_ID,TAG_DATE,TAG_TIME,TAG_CNIC},
                            new int[]{R.id.id, R.id.date, R.id.time, R.id.cnic}
                    );
                    list.setAdapter(adapter2);
                    c=2;
                }

            }
        });
        BCNIC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(d==0){d=1;}
                if(d==2)
                {
                    BCNIC . setText("CNIC ▼");
                    BDate . setText("DATE")   ;
                    BTime . setText("TIME")   ;
                    BID   . setText("ID")     ;
                    personlistcopy  =   personList  ;
                    Collections.sort(personList, new MapComparator(TAG_CNIC));
                    Collections.reverse(personList);
                    ListAdapter adapter2 = new SimpleAdapter(
                            History.this, personList, R.layout.list_item,
                            new String[]{TAG_ID,TAG_DATE,TAG_TIME,TAG_CNIC},
                            new int[]{R.id.id, R.id.date, R.id.time, R.id.cnic}
                    );
                    list.setAdapter(adapter2);
                    d=1;
                }
                else if(d==1)
                {
                    BCNIC . setText("CNIC ▲");
                    BDate . setText("DATE")   ;
                    BTime . setText("TIME")   ;
                    BID   . setText("ID")     ;
                    personlistcopy  =   personList  ;
                    Collections.sort(personList, new MapComparator(TAG_CNIC));
                    ListAdapter adapter2 = new SimpleAdapter(
                            History.this, personList, R.layout.list_item,
                            new String[]{TAG_ID,TAG_DATE,TAG_TIME,TAG_CNIC},
                            new int[]{R.id.id, R.id.date, R.id.time, R.id.cnic}
                    );
                    list.setAdapter(adapter2);
                    d=2;
                }
            }
        });
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
                    URL url = new URL("http://real-time-face-recognition.000webhostapp.com/Mobile/History.php");
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
            for(int i=0;i<peoples.length();i++)
            {
                JSONObject c    = peoples.getJSONObject(i)  ;
                String hisid    = c.getString(TAG_ID)       ;
                String date     = c.getString(TAG_DATE)     ;
                String time     = c.getString(TAG_TIME)     ;
                String cnic     = c.getString(TAG_CNIC)     ;
                HashMap<String,String> persons = new HashMap<>();

                //persons.put(TAG_ID,Integer.toString(++k));
                if(hisid=="1")
                {
                    hisid="01";
                }
                else if(hisid=="2")
                {
                    hisid="02";
                }
                else if(hisid=="3")
                {
                    hisid="03";
                }
                else if(hisid=="4")
                {
                    hisid="04";
                }
                else if(hisid=="5")
                {
                    hisid="05";
                }
                else if(hisid=="6")
                {
                    hisid="06";
                }
                else if(hisid=="7")
                {
                    hisid="07";
                }
                else if(hisid=="8")
                {
                    hisid="08";
                }
                else if(hisid=="9")
                {
                    hisid="09";
                }
                persons.put(TAG_ID,hisid);
                persons.put(TAG_DATE,date);
                persons.put(TAG_TIME,time);
                persons.put(TAG_CNIC,cnic);

                personList.add(persons);
            }

            ListAdapter adapter = new SimpleAdapter(
                    History.this, personList, R.layout.list_item,
                    new String[]{TAG_ID,TAG_DATE,TAG_TIME,TAG_CNIC},
                    new int[]{R.id.id, R.id.date, R.id.time, R.id.cnic}
            );

            list.setAdapter(adapter);
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
    }
    private class MapComparator implements Comparator<Map<String, String>>
    {
        private final String key;

        MapComparator(String key)
        {
            this.key = key;
        }

        public int compare(Map<String, String> first,
                           Map<String, String> second)
        {
            // TODO: Null checking, both for maps and values
            String firstValue = first.get(key);
            String secondValue = second.get(key);
            return firstValue.compareTo(secondValue);
        }
    }
}
