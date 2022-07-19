package com.huynhthanhnha.makeyouremember.activities;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.huynhthanhnha.makeyouremember.utils.LanguageUtils;

public class AppCompat extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LanguageUtils languageUtils = new LanguageUtils(this);
        languageUtils.updateResources(languageUtils.getLang());
    }
}
