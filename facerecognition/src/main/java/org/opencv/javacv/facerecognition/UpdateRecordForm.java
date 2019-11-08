package org.opencv.javacv.facerecognition;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class UpdateRecordForm extends Activity {
    Intent intent2;
    private EditText FirstName;
    private EditText LastName;
    private EditText Cnic;
    private Spinner Gender;
    private Spinner Role;
    private Spinner Department;
    private EditText Street;
    private Spinner City;
    private Spinner State;
    private EditText Zip;
    private Spinner Country;
    private EditText Contact;
    private EditText DateofBirth;
    private EditText Dateofjoining;
    private EditText Email;
    private ImageView ProfilePicture;
    Button SelectButton,CaptureButton;
    String DateB,DateJ;
    Bitmap bitmap;
    Calendar myCalendar,myCalendar2;
    private static final int CAMERA_REQUEST = 1888, PICK_IMAGE_REQUEST=1;

    boolean internet_connection()
    {
        ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }


    private String blockCharacterSet = "!@#$%^&*()_+=-{}|\':/.1234567890";
    private InputFilter filter = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            for (int i = start; i < end; i++)
            {
                if (blockCharacterSet.contains(("" + source.charAt(i))))
                {
                    return "";
                }
            }
            return null;
        }
    };
    private String blockCharacterSet2 = "qwertyuiopasdfghjklzxcvbnm!@#$%^&*()_+=}|]';:?><,.";
    private InputFilter filter2 = new InputFilter()
    {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            for (int i = start; i < end; i++)
            {
                if (blockCharacterSet2.contains(("" + source.charAt(i))))
                {
                    return "";
                }
            }
            return null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_record_form);
        Email                   =       (EditText) findViewById(R.id.email)         ;
        FirstName               =       (EditText) findViewById(R.id.fname)         ;
        LastName                =       (EditText) findViewById(R.id.lname)         ;
        Cnic                    =       (EditText) findViewById(R.id.cnic)          ;
        Gender                  =       (Spinner)  findViewById(R.id.gender)        ;
        Department              =       (Spinner)  findViewById(R.id.department)    ;
        Role                    =       (Spinner)  findViewById(R.id.role)          ;
        Street                  =       (EditText) findViewById(R.id.street)        ;
        City                    =       (Spinner) findViewById(R.id.city)          ;
        State                   =       (Spinner) findViewById(R.id.state)         ;
        Zip                     =       (EditText) findViewById(R.id.zip)           ;
        Country                 =       (Spinner) findViewById(R.id.country)       ;
        Contact                 =       (EditText) findViewById(R.id.phone)         ;
        DateofBirth             =       (EditText) findViewById(R.id.dob)           ;
        Dateofjoining           =       (EditText) findViewById(R.id.joiningdate)   ;

        ProfilePicture          =       (ImageView)findViewById(R.id.profiledp)     ;
//        SelectButton            =       (Button)   findViewById(R.id.selectimage)   ;
//        CaptureButton           =       (Button)   findViewById(R.id.captureimage)  ;

//        SelectButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent();
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
//            }
//        });
//        CaptureButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(cameraIntent, CAMERA_REQUEST);
//            }
//        });


        List<String> list = new ArrayList<>();
        list.add("Select Gender");
        list.add("MALE");
        list.add("FEMALE");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Gender.setAdapter(dataAdapter);

        List<String> list2 = new ArrayList<>();
        list2.add("Select Department");
        list2.add("Accounts");
        list2.add("HR");
        list2.add("Other");
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, list2);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Department.setAdapter(dataAdapter2);

        List<String> list3 = new ArrayList<>();
        list3.add("Select Role");
        list3.add("Staff");
        list3.add("Target Person");
        ArrayAdapter<String> dataAdapter3 = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, list3);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Role.setAdapter(dataAdapter3);

        List<String> list4 = new ArrayList<>();
        list4.add("Select City");
        list4.add("Abbottabad");
        list4.add("Adezai");
        list4.add("Ahmed Nager Chatha");
        list4.add("Ahmedpur East");
        list4.add("Ali Bandar");
        list4.add("Ali Pur");
        list4.add("Amir Chah");
        list4.add("Arifwala");
        list4.add("Astor");
        list4.add("Attock");
        list4.add("Ayubia");
        list4.add("Baden");
        list4.add("Bagh");
        list4.add("Bagh");
        list4.add("Bahawalnagar");
        list4.add("Bahawalpur");
        list4.add("Bajaur");
        list4.add("Banda Daud Shah");
        list4.add("Bannu");
        list4.add("Baramula");
        list4.add("Basti Malook");
        list4.add("Batagram");
        list4.add("Bazdar");
        list4.add("Bela");
        list4.add("Bellpat");
        list4.add("Bhagalchur");
        list4.add("Bhaipheru");
        list4.add("Bhakkar");
        list4.add("Bhalwal");
        list4.add("Bhimber");
        list4.add("Birote");
        list4.add("Buner");
        list4.add("Burewala");
        list4.add("Burj");
        list4.add("Chachro");
        list4.add("Chagai");
        list4.add("Chah Sandan");
        list4.add("Chailianwala");
        list4.add("Chakdara");
        list4.add("Chakku");
        list4.add("Chakwal");
        list4.add("Chaman");
        list4.add("Charsadda");
        list4.add("Chhatr");
        list4.add("Chichawatni");
        list4.add("Chiniot");
        list4.add("Chitral");
        list4.add("Chowk Azam");
        list4.add("Chowk Sarwar Shaheed");
        list4.add("Dadu");
        list4.add("Dalbandin");
        list4.add("Dargai");
        list4.add("Darya Khan");
        list4.add("Darya Khan");
        list4.add("Daska");
        list4.add("Dera Bugti");
        list4.add("Dera Ghazi Khan");
        list4.add("Dera Ismail Khan");
        list4.add("Derawar Fort");
        list4.add("Dhana Sar");
        list4.add("Dhaular");
        list4.add("Digri");
        list4.add("Dina City");
        list4.add("Dinga");
        list4.add("Dipalpur");
        list4.add("Diplo");
        list4.add("Diwana");
        list4.add("Dokri");
        list4.add("Drasan");
        list4.add("Drosh");
        list4.add("Duki");
        list4.add("Dushi");
        list4.add("Duzab");
        list4.add("Faisalabad");
        list4.add("Fateh Jang");
        list4.add("Federally Administered Tribal Areas/FATA");
        list4.add("Gadar");
        list4.add("Gadra");
        list4.add("Gajar");
        list4.add("Gandava");
        list4.add("Garhi Khairo");
        list4.add("Garruck");
        list4.add("Ghakhar Mandi");
        list4.add("Ghanian");
        list4.add("Ghauspur");
        list4.add("Ghazluna");
        list4.add("Ghotki");
        list4.add("Gilgit");
        list4.add("Girdan");
        list4.add("Gujar Khan");
        list4.add("Gujranwala");
        list4.add("Gujrat");
        list4.add("Gulistan");
        list4.add("Gwadar");
        list4.add("Gwash");
        list4.add("Hab Chauki");
        list4.add("Hafizabad");
        list4.add("Hala");
        list4.add("Hameedabad");
        list4.add("Hangu");
        list4.add("Hangu");
        list4.add("Haripur");
        list4.add("Harnai");
        list4.add("Haroonabad");
        list4.add("Hasilpur");
        list4.add("Haveli Lakha");
        list4.add("Hinglaj");
        list4.add("Hoshab");
        list4.add("Hunza");
        list4.add("Hyderabad");
        list4.add("Islamkot");
        list4.add("Ispikan");
        list4.add("Jacobabad");
        list4.add("Jahania");
        list4.add("Jalla Araain");
        list4.add("Jamesabad");
        list4.add("Jampur");
        list4.add("Jamshoro");
        list4.add("Janghar");
        list4.add("Jati (Mughalbhin)");
        list4.add("Jauharabad");
        list4.add("Jhal");
        list4.add("Jhal Jhao");
        list4.add("Jhang");
        list4.add("Jhatpat");
        list4.add("Jhelum");
        list4.add("Jhudo");
        list4.add("Jiwani");
        list4.add("Jungshahi");
        list4.add("Kalabagh");
        list4.add("Kalam");
        list4.add("Kalandi");
        list4.add("Kalat");
        list4.add("Kamalia");
        list4.add("Kamararod");
        list4.add("Kamokey");
        list4.add("Kanak");
        list4.add("Kandi");
        list4.add("Kandiaro");
        list4.add("Kanpur");
        list4.add("Kapip");
        list4.add("Kappar");
        list4.add("Karachi");
        list4.add("Karak");
        list4.add("Karodi");
        list4.add("Karor Lal Esan");
        list4.add("Kashmor");
        list4.add("Kasur");
        list4.add("Katuri");
        list4.add("Keti Bandar");
        list4.add("Khairpur");
        list4.add("Khanaspur");
        list4.add("Khanewal");
        list4.add("Khanpur");
        list4.add("Kharan");
        list4.add("Kharian");
        list4.add("Khokhropur");
        list4.add("Khora");
        list4.add("Khushab");
        list4.add("Khuzdar");
        list4.add("Khyber");
        list4.add("Kikki");
        list4.add("Klupro");
        list4.add("Kohan");
        list4.add("Kohat");
        list4.add("Kohistan");
        list4.add("Kohlu");
        list4.add("Korak");
        list4.add("Korangi");
        list4.add("Kot Addu");
        list4.add("Kot Sarae");
        list4.add("Kotli");
        list4.add("Kotri");
        list4.add("Kurram");
        list4.add("Laar");
        list4.add("Lahore");
        list4.add("Lahri");
        list4.add("Lakki Marwat");
        list4.add("Lalamusa");
        list4.add("Larkana");
        list4.add("Lasbela");
        list4.add("Latamber");
        list4.add("Layyah");
        list4.add("Liari");
        list4.add("Lodhran");
        list4.add("Loralai");
        list4.add("Lower Dir");
        list4.add("Lund");
        list4.add("Mach");
        list4.add("Madyan");
        list4.add("Mailsi");
        list4.add("Makhdoom Aali");
        list4.add("Malakand");
        list4.add("Malakand");
        list4.add("Mamoori");
        list4.add("Mand");
        list4.add("Mandi Bahauddin");
        list4.add("Mandi Warburton");
        list4.add("Mangla");
        list4.add("Manguchar");
        list4.add("Mansehra");
        list4.add("Mardan");
        list4.add("Mashki Chah");
        list4.add("Maslti");
        list4.add("Mastuj");
        list4.add("Mastung");
        list4.add("Mathi");
        list4.add("Matiari");
        list4.add("Mehar");
        list4.add("Mekhtar");
        list4.add("Merui");
        list4.add("Mian Channu");
        list4.add("Mianez");
        list4.add("Mianwali");
        list4.add("Minawala");
        list4.add("Miram Shah");
        list4.add("Mirpur");
        list4.add("Mirpur Batoro");
        list4.add("Mirpur Khas");
        list4.add("Mirpur Sakro");
        list4.add("Mithani");
        list4.add("Mithi");
        list4.add("Mohmand");
        list4.add("Mongora");
        list4.add("Moro");
        list4.add("Multan");
        list4.add("Murgha Kibzai");
        list4.add("Muridke");
        list4.add("Murree");
        list4.add("Musa Khel Bazar");
        list4.add("Muzaffarabad");
        list4.add("Muzaffargarh");
        list4.add("Nagar");
        list4.add("Nagar Parkar");
        list4.add("Nagha Kalat");
        list4.add("Nal");
        list4.add("Naokot");
        list4.add("Narowal");
        list4.add("Naseerabad");
        list4.add("Naudero");
        list4.add("Nauroz Kalat");
        list4.add("Naushara");
        list4.add("Nawabshah");
        list4.add("Nazimabad");
        list4.add("North Waziristan");
        list4.add("Noushero Feroz");
        list4.add("Nowshera");
        list4.add("Nur Gamma");
        list4.add("Nushki");
        list4.add("Nuttal");
        list4.add("Okara");
        list4.add("Ormara");
        list4.add("Paharpur");
        list4.add("Pak Pattan");
        list4.add("Palantuk");
        list4.add("Panjgur");
        list4.add("Panjgur");
        list4.add("Pasni");
        list4.add("Pattoki");
        list4.add("Pendoo");
        list4.add("Peshawar");
        list4.add("Piharak");
        list4.add("Pirmahal");
        list4.add("Pishin");
        list4.add("Plandri");
        list4.add("Pokran");
        list4.add("Punch");
        list4.add("Qambar");
        list4.add("Qamruddin Karez");
        list4.add("Qazi Ahmad");
        list4.add("Qila Abdullah");
        list4.add("Qila Didar Singh");
        list4.add("Qila Ladgasht");
        list4.add("Qila Safed");
        list4.add("Qila Saifullah");
        list4.add("Quetta");
        list4.add("Rabwah");
        list4.add("Rahim Yar Khan");
        list4.add("Raiwind");
        list4.add("Rajan Pur");
        list4.add("Rajan Pur");
        list4.add("Rakhni");
        list4.add("Ranipur");
        list4.add("Ratodero");
        list4.add("Rawalakot");
        list4.add("Rawalpindi");
        list4.add("Renala Khurd");
        list4.add("Robat Thana");
        list4.add("Rodkhan");
        list4.add("Rohri");
        list4.add("Rohri");
        list4.add("Sadiqabad");
        list4.add("Safdar Abad – (Dhaban Singh)");
        list4.add("Sahiwal");
        list4.add("Saidu Sharif");
        list4.add("Saidu Sharif");
        list4.add("Saindak");
        list4.add("Sakesar");
        list4.add("Sakrand");
        list4.add("Samberial");
        list4.add("Sanghar");
        list4.add("Sangla Hill");
        list4.add("Sanjawi");
        list4.add("Sarai Alamgir");
        list4.add("Sargodha");
        list4.add("Saruna");
        list4.add("Shabaz Kalat");
        list4.add("Shadadkhot");
        list4.add("Shafqat Shaheed Chowk");
        list4.add("Shahbandar");
        list4.add("Shahdadpur");
        list4.add("Shahpur");
        list4.add("Shahpur Chakar");
        list4.add("Shakargarh");
        list4.add("Shandur");
        list4.add("Shangla");
        list4.add("Shangrila");
        list4.add("Sharam Jogizai");
        list4.add("Sheikhupura");
        list4.add("Shikarpur");
        list4.add("Shingar");
        list4.add("Shorap");
        list4.add("Sialkot");
        list4.add("Sibi");
        list4.add("Skardu");
        list4.add("Sohawa");
        list4.add("Sonmiani");
        list4.add("Sooianwala");
        list4.add("South Waziristan");
        list4.add("Spezand");
        list4.add("Spintangi");
        list4.add("Sui");
        list4.add("Sujawal");
        list4.add("Sukkur");
        list4.add("Sundar (city)");
        list4.add("Suntsar");
        list4.add("Surab");
        list4.add("Swabi");
        list4.add("Swat");
        list4.add("Takhtbai");
        list4.add("Talagang");
        list4.add("Tando Adam");
        list4.add("Tando Allahyar");
        list4.add("Tando Bago");
        list4.add("Tangi");
        list4.add("Tank");
        list4.add("Tar Ahamd Rind");
        list4.add("Tarbela");
        list4.add("Taxila");
        list4.add("Thall");
        list4.add("Thalo");
        list4.add("Thatta");
        list4.add("Toba Tek Singh");
        list4.add("Tordher");
        list4.add("Tujal");
        list4.add("Tump");
        list4.add("Turbat");
        list4.add("Umarao");
        list4.add("Umarkot");
        list4.add("Upper Dir");
        list4.add("Uthal");
        list4.add("Vehari");
        list4.add("Veirwaro");
        list4.add("Vitakri");
        list4.add("Wadh");
        list4.add("Wah Cantonment");
        list4.add("Wana");
        list4.add("Warah");
        list4.add("Washap");
        list4.add("Wasjuk");
        list4.add("Wazirabad");
        list4.add("Yakmach");
        list4.add("Zhob");
        list4.add("khuiratta");
        list4.add("pirMahal");
        ArrayAdapter<String> dataAdapter4 = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, list4);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        City.setAdapter(dataAdapter4);


        List<String> list5 = new ArrayList<>();
        list5.add("Select State");
        list5.add("Punjab");
        list5.add("Sindh");
        list5.add("KPK");
        list5.add("Balochistan");
        ArrayAdapter<String> dataAdapter5 = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, list5);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        State.setAdapter(dataAdapter5);


        List<String> list6 = new ArrayList<>();
        list6.add("Pakistan");
        ArrayAdapter<String> dataAdapter6 = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, list6);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Country.setAdapter(dataAdapter6);

        intent2 = getIntent();

        String mycnic = intent2.getStringExtra("aCNIC");
        String URL = "http://real-time-face-recognition.000webhostapp.com/Mobile/upload/"+mycnic+".png";
        new DownloadImage().execute(URL);

        Toast.makeText(this, intent2.getStringExtra("aFirstName"), Toast.LENGTH_SHORT).show();



//        String mycnic = intent2.getStringExtra("aCNIC");
//        String URL = "http://real-time-face-recognition.000webhostapp.com/uploads/"+mycnic+".png";
//        new DownloadImage().execute(URL);

        Email        .setText( intent2.getStringExtra("aEmail"))        ;
        FirstName    .setText( intent2.getStringExtra("aFirstName"))    ;
        LastName     .setText( intent2.getStringExtra("aLastName"))     ;
        Cnic         .setText( intent2.getStringExtra("aCNIC"))         ;
        Street       .setText( intent2.getStringExtra("aStreet"))       ;
        Zip          .setText( intent2.getStringExtra("aZip"))          ;
        Contact      .setText( intent2.getStringExtra("aContact"))      ;
        DateofBirth  .setText( intent2.getStringExtra("aDateofBirth"))  ;
        Dateofjoining.setText( intent2.getStringExtra("aJoiningDate"))  ;
        String tRole        = intent2.getStringExtra("aRole");
        String tGender      = intent2.getStringExtra("aGender");
        String tDepartment  = intent2.getStringExtra("aDepartment");
        String tCity        = intent2.getStringExtra("aCity");
        String tState       = intent2.getStringExtra("aState");
        String tCountry     = intent2.getStringExtra("aCountry");
        String tetemp = tRole+"  "+tGender+"  "+tDepartment+" "+tCity+"  "+tState+"  "+tCountry;
        Toast.makeText(this, tetemp, Toast.LENGTH_SHORT).show();
//        Department.setSelection(0);
//        if(tGender=="male"||tGender=="Male"||tGender=="MALE")
//        {
//            Gender.setSelection(1);
//        }
//        else if(tGender=="female"||tGender=="Female"||tGender=="FEMALE")
//        {
//            Gender.setSelection(2);
//        }
//        if(tRole=="Staff"||tGender=="staff"||tGender=="STAFF")
//        {
//            Role.setSelection(1);
//        }
//        else if(tGender=="Target Person"||tGender=="target person"||tGender=="TARGET PERSON")
//        {
//            Role.setSelection(2);
//        }
//        if(tDepartment=="accounts"||tDepartment=="Accounts"||tDepartment=="ACCOUNTS")
//        {
//            Department.setSelection(1);
//        }
//        else if(tDepartment=="HR"||tDepartment=="Hr"||tDepartment=="hr")
//        {
//            Department.setSelection(2);
//        }
//        else
//        {
//            Department.setSelection(3);
//        }

        if (!tCity.equals(null)) {
            int spinnerPosition = dataAdapter4.getPosition(tCity);
            City.setSelection(spinnerPosition);
        }
        if (!tState.equals(null)) {
            int spinnerPosition = dataAdapter5.getPosition(tState);
            State.setSelection(spinnerPosition);
        }
        if (!tCountry.equals(null)) {
            int spinnerPosition = dataAdapter6.getPosition(tCountry);
            Country.setSelection(spinnerPosition);
        }
        if (!tGender.equals(null)) {
            int spinnerPosition = dataAdapter.getPosition(tGender);
            Gender.setSelection(spinnerPosition);
        }
        if (!tDepartment.equals(null)) {
            int spinnerPosition = dataAdapter2.getPosition(tDepartment);
            Department.setSelection(spinnerPosition);
        }
        if (!tRole.equals(null)) {
            int spinnerPosition = dataAdapter3.getPosition(tRole);
            Role.setSelection(spinnerPosition);
        }


        myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
            {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                try
                {
                    updateBirthLabel();
                }
                catch (ParseException e)
                {
                    e.printStackTrace();
                }
            }
        };
        DateofBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(UpdateRecordForm.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        myCalendar2 = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date2 = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
            {
                myCalendar2.set(Calendar.YEAR, year);
                myCalendar2.set(Calendar.MONTH, monthOfYear);
                myCalendar2.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                try
                {
                    updateJoiningLabel();
                }
                catch (ParseException e)
                {
                    e.printStackTrace();
                }
            }
        };
        Dateofjoining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(UpdateRecordForm.this, date2, myCalendar2
                        .get(Calendar.YEAR), myCalendar2.get(Calendar.MONTH),
                        myCalendar2.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        FirstName.setFilters(new InputFilter[] { filter });
        LastName.setFilters(new InputFilter[] { filter });
        Street.setFilters(new InputFilter[] { filter });
        Zip.setFilters(new InputFilter[] { filter2 });
        Cnic.setFilters(new InputFilter[] { filter2 });
        Contact.setFilters(new InputFilter[] { filter2 });

        if (!internet_connection())
        {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
//            Uri uri = data.getData();
//            try {
//                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
//                // Log.d(TAG, String.valueOf(bitmap));
//                ProfilePicture.setImageBitmap(bitmap);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
//            bitmap = (Bitmap) data.getExtras().get("data");
//            ProfilePicture.setImageBitmap(bitmap);
//        }
    }

    private void updateBirthLabel() throws ParseException
    {
        String dbFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf2 = new SimpleDateFormat(dbFormat, Locale.US);
        DateB = sdf2.format(myCalendar.getTime());
        Toast.makeText(this, DateB , Toast.LENGTH_SHORT).show();

        String myFormat = "MMM d, yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        DateofBirth.setText(sdf.format(myCalendar.getTime()));
    }
    private void updateJoiningLabel() throws ParseException
    {
        String dbFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf2 = new SimpleDateFormat(dbFormat, Locale.US);
        DateJ = sdf2.format(myCalendar.getTime());
        Toast.makeText(this, DateJ , Toast.LENGTH_SHORT).show();

        String myFormat = "MMM d, yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        Dateofjoining.setText(sdf.format(myCalendar.getTime()));
    }

    public void Updation(View view)
    {
        if( FirstName.getText().toString().trim().equals(""))
        {
            FirstName.setError( "First name is required!" );
        }
        else if( LastName.getText().toString().trim().equals(""))
        {
            LastName.setError( "Last name is required!" );
        }
        else if( Gender.getSelectedItem().toString().trim().equals("Select Gender"))
        {
            Toast.makeText(this, "Gender is required.", Toast.LENGTH_SHORT).show();
        }
        else if( DateofBirth.getText().toString().trim().equals(""))
        {
            DateofBirth.setError( "Date of Birth is required!" );
        }
        else if( Contact.getText().toString().trim().equals(""))
        {
            Contact.setError( "Contact info is required!" );
        }
        else if( Email.getText().toString().trim().equals(""))
        {
            Email.setError( "Email address is required!" );
        }
        else if( Role.getSelectedItem().toString().trim().equals("Select Role"))
        {
            Toast.makeText(this, "Role is required!", Toast.LENGTH_SHORT).show();
        }
        else if( Department.getSelectedItem().toString().trim().equals("Select Department"))
        {
            Toast.makeText(this, "Department is required!", Toast.LENGTH_SHORT).show();
        }
        else if( Dateofjoining.getText().toString().trim().equals(""))
        {
            DateofBirth.setError( "Joining Date is required!" );
        }
        else if( Street.getText().toString().trim().equals(""))
        {
            Street.setError( "Street is required!" );
        }
        else if( City.getSelectedItem().toString().trim().equals("Select City"))
        {
            Toast.makeText(this, "City is required!", Toast.LENGTH_SHORT).show();
        }
        else if( State.getSelectedItem().toString().trim().equals(""))
        {
            Toast.makeText(this, "State is required!", Toast.LENGTH_SHORT).show();
        }
        else if( Country.getSelectedItem().toString().trim().equals(""))
        {
            Toast.makeText(this, "Country is required!", Toast.LENGTH_SHORT).show();
        }
        else if(Zip.getText().toString().trim().equals(""))
        {
            Zip.setError( "Zip Code is required!" );
        }
        else
        {
            if (!internet_connection())
            {
                Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
            else
            {
                //     final ProgressDialog loading = ProgressDialog.show(this,"Registering...","Please wait...",false,false);
                RequestQueue requestQueue = Volley.newRequestQueue(UpdateRecordForm.this);
                StringRequest request = new StringRequest(Request.Method.POST, "http://real-time-face-recognition.000webhostapp.com/Mobile/UpdateRecord.php", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("success")) {
                            Toast.makeText(UpdateRecordForm.this, "Updation Successful", Toast.LENGTH_LONG).show();
                            //                    Intent intent = new Intent(PersonRegistration.this,HomeScreen.class);
                            //                    startActivity(intent);

                            //Disimissing the progress dialog
                            //                  loading.dismiss();
                            //Showing toast message of the response
                            //                    Toast.makeText(PersonRegistration.this, response , Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(UpdateRecordForm.this, "Updation Failed:" + response, Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //           loading.dismiss();
                        String message = null;
                        if (error instanceof NetworkError) {
                            message = "Cannot connect to Internet...Please check your connection!";
                        } else if (error instanceof ServerError) {
                            message = "The server could not be found. Please try again after some time!!";
                        } else if (error instanceof AuthFailureError) {
                            message = "Cannot connect to Internet...Please check your connection!";
                        } else if (error instanceof ParseError) {
                            message = "Parsing error! Please try again after some time!!";
                        } else if (error instanceof NoConnectionError) {
                            message = "Cannot connect to Internet...Please check your connection!";
                        } else if (error instanceof TimeoutError) {
                            message = "Connection TimeOut! Please check your internet connection.";
                        }
                        Toast.makeText(UpdateRecordForm.this, "Error: "+message, Toast.LENGTH_LONG).show();
                    }
                }
                ) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        String hFirstName   = FirstName.getText().toString();
                        String hLastName    = LastName.getText().toString();
                        String hCNIC        = Cnic.getText().toString();
                        String hGender      = Gender.getSelectedItem().toString();
                        String hDepartment  = Department.getSelectedItem().toString();
                        String hRole        = Role.getSelectedItem().toString();
                        String hStreet      = Street.getText().toString();
                        String hCity        = City.getSelectedItem().toString();
                        String hState       = State.getSelectedItem().toString();
                        String hZip         = Zip.getText().toString();
                        String hCountry     = Country.getSelectedItem().toString();
                        String hContact     = Contact.getText().toString();
                        String hDateOfBirth = DateB;
                        String hJoiningDate = DateJ;
                        String hEmail       = Email.getText().toString();

                        params.put("gFirstName", hFirstName);
                        params.put("gLastName", hLastName);
                        params.put("gCNIC", hCNIC);
                        params.put("gGender", hGender);
                        params.put("gDepartment", hDepartment);
                        params.put("gRole", hRole);
                        params.put("gStreet", hStreet);
                        params.put("gCity", hCity);
                        params.put("gState", hState);
                        params.put("gZip", hZip);
                        params.put("gCountry", hCountry);
                        params.put("gContact", hContact);
                        params.put("gDateOfBirth", hDateOfBirth);
                        params.put("gJoiningDate", hJoiningDate);
                        params.put("gEmail", hEmail);

                        return params;
                    }
                };

                //        int socketTimeout = 30000; // 30 seconds. You can change it
                //        request.setRetryPolicy(new DefaultRetryPolicy(socketTimeout,
                //                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                //                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                //        AppController.getInstance().addToRequestQueue(request);
                requestQueue.add(request);
                //UploadImage();

            }
        }
    }

//    public void Updation(View view)
//    {
//        if( FirstName.getText().toString().trim().equals(""))
//        {
//            FirstName.setError( "First name is required!" );
//        }
//        else if( LastName.getText().toString().trim().equals(""))
//        {
//            LastName.setError( "Last name is required!" );
//        }
//        else if( Gender.getSelectedItem().toString().trim().equals("Select Gender"))
//        {
//            Toast.makeText(this, "Gender is required.", Toast.LENGTH_SHORT).show();
//        }
//        else if( DateofBirth.getText().toString().trim().equals(""))
//        {
//            DateofBirth.setError( "Date of Birth is required!" );
//        }
//        else if( Contact.getText().toString().trim().equals(""))
//        {
//            Contact.setError( "Contact info is required!" );
//        }
//        else if( Email.getText().toString().trim().equals(""))
//        {
//            Email.setError( "Email address is required!" );
//        }
//        else if( Department.getSelectedItem().toString().trim().equals("Select Department"))
//        {
//            Toast.makeText(this, "Department is required!", Toast.LENGTH_SHORT).show();
//        }
//        else if( Dateofjoining.getText().toString().trim().equals(""))
//        {
//            DateofBirth.setError( "Joining Date is required!" );
//        }
//        else if( Street.getText().toString().trim().equals(""))
//        {
//            Street.setError( "Street is required!" );
//        }
//        else if( City.getSelectedItem().toString().trim().equals("Select City"))
//        {
//            Toast.makeText(this, "City is required!", Toast.LENGTH_SHORT).show();
//        }
//        else if( State.getSelectedItem().toString().trim().equals("Select State"))
//        {
//            Toast.makeText(this, "State is required!", Toast.LENGTH_SHORT).show();
//        }
//        else if( Country.getSelectedItem().toString().trim().equals("Select Country"))
//        {
//            Toast.makeText(this, "Country is required!", Toast.LENGTH_SHORT).show();
//        }
//        else if( Role.getSelectedItem().toString().trim().equals("Select Role"))
//        {
//            Toast.makeText(this, "Role is required!", Toast.LENGTH_SHORT).show();
//        }
//        else if(Zip.getText().toString().trim().equals(""))
//        {
//            Zip.setError( "Zip Code is required!" );
//        }
//        else
//        {
//            //     final ProgressDialog loading = ProgressDialog.show(this,"Registering...","Please wait...",false,false);
//            RequestQueue requestQueue = Volley.newRequestQueue(UpdateRecordForm.this);
//            StringRequest request = new StringRequest(Request.Method.POST, "https://real-time-face-recognition.000webhostapp.com/Mobile/UpdateRecord.php", new Response.Listener<String>() {
//                @Override
//                public void onResponse(String response) {
//                    if (response.equals("success")) {
//                        Toast.makeText(UpdateRecordForm.this, "Registration Successful", Toast.LENGTH_LONG).show();
////                    Intent intent = new Intent(PersonRegistration.this,HomeScreen.class);
////                    startActivity(intent);
//
////Disimissing the progress dialog
////                  loading.dismiss();
////Showing toast message of the response
////                    Toast.makeText(PersonRegistration.this, response , Toast.LENGTH_LONG).show();
//
//                    }
//                    else {
//                        Toast.makeText(UpdateRecordForm.this, "Registration Failed:" + response, Toast.LENGTH_LONG).show();
//                    }
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    //           loading.dismiss();
//                    String message = null;
//                    if (error instanceof NetworkError) {
//                        message = "Cannot connect to Internet...Please check your connection!";
//                    } else if (error instanceof ServerError) {
//                        message = "The server could not be found. Please try again after some time!!";
//                    } else if (error instanceof AuthFailureError) {
//                        message = "Cannot connect to Internet...Please check your connection!";
//                    } else if (error instanceof ParseError) {
//                        message = "Parsing error! Please try again after some time!!";
//                    } else if (error instanceof NoConnectionError) {
//                        message = "Cannot connect to Internet...Please check your connection!";
//                    } else if (error instanceof TimeoutError) {
//                        message = "Connection TimeOut! Please check your internet connection.";
//                    }
//                    Toast.makeText(UpdateRecordForm.this, "Error: "+ message, Toast.LENGTH_LONG).show();
//                }
//            }
//            ) {
//                @Override
//                protected Map<String, String> getParams() throws AuthFailureError {
//                    Map<String, String> params = new HashMap<>();
//                    String hFirstName   = FirstName.getText().toString();
//                    String hLastName    = LastName.getText().toString();
//                    String hCNIC        = Cnic.getText().toString();
//                    String hGender      = Gender.getSelectedItem().toString();
//                    String hDepartment  = Department.getSelectedItem().toString();
//                    String hStreet      = Street.getText().toString();
//                    String hCity        = City.getSelectedItem().toString();
//                    String hState       = State.getSelectedItem().toString();
//                    String hZip         = Zip.getText().toString();
//                    String hCountry     = Country.getSelectedItem().toString();
//                    String hContact     = Contact.getText().toString();
//                    String hDateOfBirth = DateB;
//                    String hJoiningDate = DateJ;
//                    String hRole        = Role.getSelectedItem().toString();
//                    String hEmail       = Email.getText().toString();
//
//                    params.put("gFirstName", hFirstName);
//                    params.put("gLastName", hLastName);
//                    params.put("gCNIC", hCNIC);
//                    params.put("gGender", hGender);
//                    params.put("gDepartment", hDepartment);
//                    params.put("gStreet", hStreet);
//                    params.put("gCity", hCity);
//                    params.put("gState", hState);
//                    params.put("gZip", hZip);
//                    params.put("gCountry", hCountry);
//                    params.put("gRole", hRole);
//                    params.put("gContact", hContact);
//                    params.put("gDateOfBirth", hDateOfBirth);
//                    params.put("gJoiningDate", hJoiningDate);
//                    params.put("gEmail", hEmail);
//
//                    return params;
//                }
//            };
//
//            //        int socketTimeout = 30000; // 30 seconds. You can change it
//            //        request.setRetryPolicy(new DefaultRetryPolicy(socketTimeout,
//            //                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//            //                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//            //        AppController.getInstance().addToRequestQueue(request);
//            requestQueue.add(request);
//            //UploadImage();
//        }
//    }

    public void Deletion(View view){
        if (!internet_connection())
        {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
        else
        {
            RequestQueue requestQueue = Volley.newRequestQueue(UpdateRecordForm.this);
            StringRequest request = new StringRequest(Request.Method.POST, "http://real-time-face-recognition.000webhostapp.com/Mobile/DeleteRecord.php", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response.equals("success")) {
                        Toast.makeText(UpdateRecordForm.this, "Deleted Successfully", Toast.LENGTH_LONG).show();
//                    Intent intent = new Intent(UpdateRecordForm.this,HomeScreen.class);
//                    startActivity(intent);
                    } else {
                        Toast.makeText(UpdateRecordForm.this, "Deletion Failed"+response, Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String message = null;
                    if (error instanceof NetworkError) {
                        message = "Cannot connect to Internet...Please check your connection!";
                    } else if (error instanceof ServerError) {
                        message = "The server could not be found. Please try again after some time!!";
                    } else if (error instanceof AuthFailureError) {
                        message = "Cannot connect to Internet...Please check your connection!";
                    } else if (error instanceof ParseError) {
                        message = "Parsing error! Please try again after some time!!";
                    } else if (error instanceof NoConnectionError) {
                        message = "Cannot connect to Internet...Please check your connection!";
                    } else if (error instanceof TimeoutError) {
                        message = "Connection TimeOut! Please check your internet connection.";
                    }
                    Toast.makeText(UpdateRecordForm.this, message, Toast.LENGTH_LONG).show();
                    //Toast.makeText(UpdateRecordForm.this,error.toString(), Toast.LENGTH_LONG).show();
                }
            }
            ){
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    String hCNIC            =           Cnic          .   getText()         .   toString()    ;
                    params.put  ( "gCNIC"           ,   hCNIC          );
                    return params;
                }
            };
            requestQueue.add(request);
        }
    }

    private void UploadImage(){
        if (!internet_connection())
        {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
        else
        {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://real-time-face-recognition.000webhostapp.com/Mobile/PhotoUpdate.php", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    String s = response.trim();
                    if(s.equalsIgnoreCase("success")){
                        Toast.makeText(UpdateRecordForm.this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(UpdateRecordForm.this, "Image Uplaod Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String message = null;
                    if (error instanceof NetworkError) {
                        message = "Cannot connect to Internet...Please check your connection!";
                    } else if (error instanceof ServerError) {
                        message = "The server could not be found. Please try again after some time!!";
                    } else if (error instanceof AuthFailureError) {
                        message = "Cannot connect to Internet...Please check your connection!";
                    } else if (error instanceof ParseError) {
                        message = "Parsing error! Please try again after some time!!";
                    } else if (error instanceof NoConnectionError) {
                        message = "Cannot connect to Internet...Please check your connection!";
                    } else if (error instanceof TimeoutError) {
                        message = "Connection TimeOut! Please check your internet connection.";
                    }
                    Toast.makeText(UpdateRecordForm.this, message, Toast.LENGTH_LONG).show();
                    //Toast.makeText(UpdateRecordForm.this, error+"", Toast.LENGTH_SHORT).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    String ten = Cnic.getText().toString();
                    ProfilePicture.setDrawingCacheEnabled(true);
                    Bitmap bitmap2= ProfilePicture.getDrawingCache();
                    String image = getStringImage(bitmap2);
                    Map<String ,String> params = new HashMap<String,String>();
                    params.put("TEN",ten);
                    params.put("HINH",image);
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        }
    }

    public String getStringImage(Bitmap bm){
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100,ba);
        byte[] imagebyte = ba.toByteArray();
        String encode = Base64.encodeToString(imagebyte, Base64.DEFAULT);
        return encode;
    }

    // DownloadImage AsyncTask
    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(String... URL) {

            String imageURL = URL[0];

            Bitmap bitmap = null;
            try {
                // Download Image from URL
                InputStream input = new java.net.URL(imageURL).openStream();
                // Decode Bitmap
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // Set the bitmap into ImageView
            ProfilePicture.setImageBitmap(result);
            // Close progressdialog
            //mProgressDialog.dismiss();
        }
    }
}


