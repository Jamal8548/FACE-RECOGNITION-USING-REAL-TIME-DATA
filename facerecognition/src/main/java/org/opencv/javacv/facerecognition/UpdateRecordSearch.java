package org.opencv.javacv.facerecognition;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UpdateRecordSearch extends Activity {

    public static final String MY_URL = "http://real-time-face-recognition.000webhostapp.com/Mobile/SeacrhRecord.php?CNIC=";
    public static final String KEY_FIRSTNAME    = "FirstName"               ;
    public static final String KEY_LASTNAME     = "LastName"                ;
    public static final String KEY_CNIC         = "CNIC"                    ;
    public static final String KEY_GENDER       = "Gender"                  ;
    public static final String KEY_DEPARTMENT   = "Department"              ;
    public static final String KEY_STREET       = "Street"                  ;
    public static final String KEY_CITY         = "City"                    ;
    public static final String KEY_STATE        = "State"                   ;
    public static final String KEY_ZIP          = "Zip"                     ;
    public static final String KEY_COUNTRY      = "Country"                 ;
    public static final String KEY_CONTACT      = "Contact"                 ;
    public static final String KEY_DATEOFBIRTH  = "DateOfBirth"             ;
    public static final String KEY_JOININGDATE  = "JoiningDate"             ;
    public static final String KEY_EMAIL        = "Email"                   ;
    public static final String KEY_ROLE         = "Role_in_Organization"    ;
    public static final String JSON_ARRAY       = "result"                  ;

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
    String hRole        =   null    ;

    private EditText SearchBox;
    private Button SearchButton;
    private TextView Result;
    private ProgressDialog loading;
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
        setContentView(R.layout.activity_update_record_search);
        SearchBox   =   (EditText) findViewById(R.id.searchbox);
        SearchButton=   (Button)   findViewById(R.id.Search);
        Result      =   (TextView) findViewById(R.id.textView2);
        SearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!internet_connection())
                {
                    Toast.makeText(UpdateRecordSearch.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    getData();
                }
            }
        });
    }
    private void getData() {
        String id = SearchBox.getText().toString().trim();
        if (id.equals("")) {
            Toast.makeText(this, "Enter a Valid CNIC", Toast.LENGTH_LONG).show();
            return;
        }
        loading = ProgressDialog.show(this,"Please wait...","Fetching...",false,false);

        String url = MY_URL+SearchBox.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loading.dismiss();
                showJSON(response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        Toast.makeText(UpdateRecordSearch.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void showJSON(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(JSON_ARRAY);

            JSONObject data = result.getJSONObject(0);
            //Result.setText(data.getString("Last_Name"));
            hFirstName      = data.getString("First_Name")  ;
            hLastName       = data.getString("Last_Name")   ;
            hCNIC           = data.getString("CNIC")        ;
            hGender         = data.getString("Gender")      ;
            hDepartment     = data.getString("Department")  ;
            hStreet         = data.getString("Street")      ;
            hCity           = data.getString("City")        ;
            hState          = data.getString("State")       ;
            hZip            = data.getString("Postal_Code") ;
            hCountry        = data.getString("Country")     ;
            hContact        = data.getString("Phone")       ;
            hDateOfBirth    = data.getString("DOB")         ;
            hJoiningDate    = data.getString("Joining_Date");
            hEmail          = data.getString("Email")       ;
            hRole           = data.getString("Role_in_Organization");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (hCNIC == null) {
            Result.setText("Record not Found");
        } else {
            Display();
            //Result.setText("" + hFirstName + "\n" + hLastName + "\n" + hCNIC + "\n" + hGender + "\n" + hDepartment + "\n" + hStreet + "\n" + hCity + "\n" + hState + "\n" + hZip + "\n" + hCountry + "\n" + hContact + "\n" + hDateOfBirth + "\n" + hJoiningDate + "\n" + hEmail);
        }
    }

    private void Display() {
        Intent viewrecord = new Intent(this , UpdateRecordForm.class);
        viewrecord.putExtra("aFirstName"    ,   hFirstName)     ;
        viewrecord.putExtra("aLastName"     ,   hLastName)      ;
        viewrecord.putExtra("aCNIC"         ,   hCNIC)          ;
        viewrecord.putExtra("aGender"       ,   hGender)        ;
        viewrecord.putExtra("aDepartment"   ,   hDepartment)    ;
        viewrecord.putExtra("aStreet"       ,   hStreet)        ;
        viewrecord.putExtra("aCity"         ,   hCity)          ;
        viewrecord.putExtra("aState"        ,   hState)         ;
        viewrecord.putExtra("aZip"          ,   hZip)           ;
        viewrecord.putExtra("aCountry"      ,   hCountry)       ;
        viewrecord.putExtra("aContact"      ,   hContact)       ;
        viewrecord.putExtra("aDateofBirth"  ,   hDateOfBirth)   ;
        viewrecord.putExtra("aJoiningDate"  ,   hJoiningDate)   ;
        viewrecord.putExtra("aEmail"        ,   hEmail)         ;
        viewrecord.putExtra("aRole"         ,   hRole)          ;
        startActivity(viewrecord);
    }


}