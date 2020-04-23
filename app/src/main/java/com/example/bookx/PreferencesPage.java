package com.example.bookx;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.example.bookx.R;

import java.util.Locale;

public class PreferencesPage extends AppCompatActivity {

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
    Button btnGoHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences_page);
        currentLanguage = getIntent().getStringExtra(currentLang);

        tvPreferences = (TextView) findViewById(R.id.tvPreferences);
        tvLanguage = (TextView) findViewById(R.id.tvPreferences);
        tvAppearances = (TextView) findViewById(R.id.tvPreferences);
        tvNotifications = (TextView) findViewById(R.id.tvPreferences);
        tvAccount = (TextView) findViewById(R.id.tvPreferences);
        spLanguage = (Spinner) findViewById(R.id.spLanguage);
        //changeLanguage("Spanish");
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
                    //changeLanguage(lang);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        swAlerts = (Switch) findViewById(R.id.swAlerts);
        swLocation = (Switch) findViewById(R.id.swLocation);
        btnGoHome = (Button) findViewById(R.id.btnGoHome);
        btnGoHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openHomePage();
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
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
            Intent refresh = new Intent(this, PreferencesPage.class);
            refresh.putExtra(currentLang, newLang);
            startActivity(refresh);
            //recreate();
        } else {
            Toast.makeText(PreferencesPage.this, "Language selected!", Toast.LENGTH_SHORT).show();
        }

    }

    public void openHomePage() {
        Intent intent = new Intent(this, HomePage.class);
        startActivity(intent);
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
}

