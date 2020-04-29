package com.example.bookx;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookx.Model.User;
import com.example.bookx.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Locale;

public class PreferencesPage extends AppCompatActivity {
    private static final String TAG = "***PREFERENCES***";

    // the following below declare the specific views in the activity
    private String userid ;
    private User user ;
    TextView tvPreferences;
    TextView tvLanguage;
    TextView tvAppearances;
    TextView tvNotifications;
    TextView tvAccount;
    Spinner spLanguage;
    Switch swAlerts;
    Switch swLocation;
    Locale myLocale;
    String currentLanguage = "en", currentLang;
    Button btnGoHome ;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    DatabaseReference reference ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences_page);

        // the code below sets the user value from the bundle
        Bundle extra = getIntent().getExtras() ;
        if(extra != null){
            this.userid = extra.getString("userid") ;
            this.user = (User) extra.get("user") ;
        }

        // this gets the reference of the database and gets the value of the show location
        // permission of user
        reference = FirebaseDatabase.getInstance().getReference("users").child(userid).child("showLocation") ;
        currentLanguage = getIntent().getStringExtra(currentLang);

        // We used SharedPreferences to set the language and notification data so it remembers the
        // users choices when they leave the activity and come back
        pref = getSharedPreferences("com.example.bookx.notification", Context.MODE_PRIVATE);
        editor = pref.edit();

        // this receives and sets the users current value for notification on or not. the value is
        // true and pushes it to the editor
        if(pref.getBoolean("notificationIsOn", false) != pref.getBoolean("notificationIsOn", true) ){
            editor.putBoolean("notificationIsOn", true);
            editor.apply();
        }

        tvPreferences = (TextView) findViewById(R.id.tvPreferences);
        tvLanguage = (TextView) findViewById(R.id.tvPreferences);
        tvAppearances = (TextView) findViewById(R.id.tvPreferences);
        tvNotifications = (TextView) findViewById(R.id.tvPreferences);
        tvAccount = (TextView) findViewById(R.id.tvPreferences);
        spLanguage = (Spinner) findViewById(R.id.spLanguage);
        swAlerts = (Switch) findViewById(R.id.swAlerts);
        swLocation = (Switch) findViewById(R.id.swLocation);
        btnGoHome = (Button) findViewById(R.id.btnGoHome);
        btnGoHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openHomepage() ;
                finish();
            }
        });

        // this value sets the value of the switch what is in the SharedPreferences
        if(pref.getBoolean("notificationIsOn", false)){
            swAlerts.setChecked(true);
        }

        // this is a value listener to receive from the database whether the user wanted the
        // location on or off
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean showLocation = dataSnapshot.getValue(Boolean.class) ;
                swLocation.setChecked(showLocation);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "failed to load show location data");
            }
        });

        // this code sets a listener for the language selection and uses switch-case statements to
        // change language by calling changeLanguage()
        spLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String lang = spLanguage.getSelectedItem().toString();
                switch (lang) {
                    case "Choose a Language":
                        break;
                    case "English":
                        changeLanguage("en");
                        Log.i("chosen: ", lang);
                        break;
                    case "Spanish":
                        changeLanguage("es");
                        Log.i("chosen: ", lang);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d(TAG, "nothing selected for language");
            }
        });

        // this sets the value in SHaredPreferences if the user turns on or off the notifications
        // switch and pushes the new value to the editor in the SharedPreferences
        swAlerts.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    editor.putBoolean("notificationIsOn", true);
                    editor.apply();
                }
                else{
                    editor.putBoolean("notificationIsOn", false);
                    editor.apply();
                }
                Log.d(TAG, "check status: " + pref.getBoolean("notificationIsOn", true) );
                Log.d(TAG, "check status: " + pref.getBoolean("notificationIsOn", false) );
            }
        });

        swLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                reference.setValue(isChecked) ;
            }
        });
    }

    // Changes language
    // Credit: https://stackoverflow.com/questions/2900023/change-app-language-programmatically-in-android
    // https://javapapers.com/android/android-app-with-multi-language-support/
    public void changeLanguage(String lang) {
        String newLang = lang;
        if (!newLang.equals(currentLanguage)) {
            myLocale = new Locale(newLang);
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;     // sets the configuration to new language for change
            res.updateConfiguration(conf, dm);  // updates the new configuration settings
            onConfigurationChanged(conf);
        } else {
            Toast.makeText(PreferencesPage.this, "Language selected!", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onPause(){
        super.onPause();
        onConfigurationChanged(getResources().getConfiguration());
    }

    @Override
    public void recreate() {
        if (android.os.Build.VERSION.SDK_INT >= 14) {
            super.recreate();
        } else {
            startActivity(getIntent());
            finish();
        }
    }

    // this sets the intent for the home page to store the user data and user id so
    // the activity info is stored
    void openHomepage(){
        Intent intent = new Intent(getApplicationContext(),HomePage.class) ;
        intent.putExtra("user",user) ;
        intent.putExtra("userid",userid) ;
        startActivity(intent);
    }

    // forbids back button
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}

