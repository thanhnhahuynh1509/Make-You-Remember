package com.huynhthanhnha.makeyouremember.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.material.textfield.TextInputLayout;
import com.huynhthanhnha.makeyouremember.R;
import com.huynhthanhnha.makeyouremember.utils.LanguageUtils;

import java.util.Locale;

public class SettingActivity extends AppCompat {

    private AutoCompleteTextView inputLanguages;
    private AutoCompleteTextView btnCheckRemember;
    private AutoCompleteTextView btnFavorite;
    private AutoCompleteTextView btnTrash;
    private AutoCompleteTextView btnAbout;
    private ImageView btnBack;

    private LanguageUtils languageUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        languageUtils = new LanguageUtils(this);
        inputLanguages = findViewById(R.id.inputLanguages);
        btnBack = findViewById(R.id.btnBack);
        btnCheckRemember = findViewById(R.id.btnCheckRemember);
        btnFavorite = findViewById(R.id.btnFavorite);
        btnTrash = findViewById(R.id.btnTrash);
        btnAbout = findViewById(R.id.btnAbout);

        btnBack.setOnClickListener(v -> {
            onBackPressed();
            finish();
        });

        btnCheckRemember.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), OptionalActivity.class);
            intent.putExtra("option", "remembered");
            startActivity(intent);
            finish();
        });

        btnFavorite.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), OptionalActivity.class);
            intent.putExtra("option", "favorite");
            startActivity(intent);
            finish();
        });

        btnTrash.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), OptionalActivity.class);
            intent.putExtra("option", "trash");
            startActivity(intent);
            finish();
        });

        btnAbout.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), AboutMeActivity.class);
            startActivity(intent);
            finish();
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        String[] languages = getResources().getStringArray(R.array.languages);
        ArrayAdapter languagesAdapter = new ArrayAdapter(getApplicationContext(), R.layout.dropdown_item_language, languages);
        if(languageUtils.getLang().equals("en")) {
            inputLanguages.setText(languages[0]);
        } else {
            inputLanguages.setText(languages[1]);
        }
        inputLanguages.setAdapter(languagesAdapter);
        inputLanguages.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String language = s.toString();
                if(language.equals("English")) {
                    languageUtils.updateResources("en");
                } else {
                    languageUtils.updateResources("vi");
                }
//                recreate();
                finish();
            }
        });

    }

}