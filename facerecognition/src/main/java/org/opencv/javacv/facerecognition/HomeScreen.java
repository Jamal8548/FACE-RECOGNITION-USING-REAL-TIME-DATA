package org.opencv.javacv.facerecognition;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class HomeScreen extends Activity {
    ImageView Registration,Recognition,History,BackupAndRestore,UpdateRecords,syncbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        syncbutton          =   (ImageView) findViewById(R.id.sync);
        Registration        =   (ImageView) findViewById(R.id.registrationimage)    ;
        Recognition         =   (ImageView) findViewById(R.id.recognitionimage)     ;
        History             =   (ImageView) findViewById(R.id.historyimage)         ;
        BackupAndRestore    =   (ImageView) findViewById(R.id.backupimage)          ;
        UpdateRecords       =   (ImageView) findViewById(R.id.updateimage)          ;

        Registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent   =   new Intent(HomeScreen.this,RegistrationImage.class);
                startActivity(intent);
            }
        });
        Recognition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent   =   new Intent(HomeScreen.this, org.opencv.javacv.facerecognition.Recognition.class);
                startActivity(intent);
            }
        });
        History.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent   =   new Intent(HomeScreen.this,History.class);
                startActivity(intent);
            }
        });
        BackupAndRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent   =   new Intent(HomeScreen.this,BackupAndRestore.class);
                startActivity(intent);
            }
        });
        UpdateRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent   =   new Intent(HomeScreen.this,UpdateRecordSearch.class);
                startActivity(intent);
            }
        });
        syncbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeScreen.this,SyncImages.class);
                //intent.putExtra(SimpleIntentService.PARAM_IN_MSG, "Test");
                getApplicationContext().startService(intent);
            }
        });
    }

}
