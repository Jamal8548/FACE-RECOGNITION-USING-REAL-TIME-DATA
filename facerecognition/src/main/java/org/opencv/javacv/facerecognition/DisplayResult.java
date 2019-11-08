package org.opencv.javacv.facerecognition;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class DisplayResult extends AppCompatActivity {
    TextView FirstName;
    TextView LastName;
    TextView Gender;
    TextView CNIC;
    TextView Email;
    TextView Phone;
    TextView DOB;
    TextView Department;
    TextView JoiningDate;
    TextView Street;
    TextView City;
    TextView State;
    TextView Country;
    TextView ZIP;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_result);

        FirstName   = (TextView)  findViewById(R.id.firstname)  ;
        LastName    = (TextView)  findViewById(R.id.lastname)   ;
        Gender      = (TextView)  findViewById(R.id.gender)     ;
        CNIC        = (TextView)  findViewById(R.id.cnic)       ;
        Email       = (TextView)  findViewById(R.id.email)      ;
        Phone       = (TextView)  findViewById(R.id.phone)      ;
        DOB         = (TextView)  findViewById(R.id.dob)        ;
        Department  = (TextView)  findViewById(R.id.department) ;
        City        = (TextView)  findViewById(R.id.city)       ;
        Street      = (TextView)  findViewById(R.id.street)     ;
        State       = (TextView)  findViewById(R.id.state)      ;
        Country     = (TextView)  findViewById(R.id.country)    ;
        ZIP         = (TextView)  findViewById(R.id.zip)        ;
        JoiningDate = (TextView)  findViewById(R.id.joiningdate);

        Intent intent2 = getIntent();

        Email        .setText( intent2.getStringExtra("aEmail"))        ;
        FirstName    .setText( intent2.getStringExtra("aFirstName"))    ;
        LastName     .setText( intent2.getStringExtra("aLastName"))     ;
        CNIC         .setText( intent2.getStringExtra("aCNIC"))         ;
        Street       .setText( intent2.getStringExtra("aStreet"))       ;
        City         .setText( intent2.getStringExtra("aCity"))         ;
        State        .setText( intent2.getStringExtra("aState"))        ;
        ZIP          .setText( intent2.getStringExtra("aZip"))          ;
        Country      .setText( intent2.getStringExtra("aCountry"))      ;
        Phone        .setText( intent2.getStringExtra("aContact"))      ;
        DOB          .setText( intent2.getStringExtra("aDateofBirth"))  ;
        JoiningDate  .setText( intent2.getStringExtra("aJoiningDate"))  ;
    }
}
