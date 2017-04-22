package com.kalinasoft.yantrans;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;

/**
 * Activity with progressbar for loading languages list
 */
public class SplashActivity extends AppCompatActivity implements LangsLoader.Comm{

    LangsLoader loader;
    ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (YandexApi.getInstance().isLoaded())
            finishTask(0);
        ActionBar ab = getSupportActionBar();
        if(ab!=null)
            ab.hide();
        progress = (ProgressBar)findViewById(R.id.loading_progress);
        if (loader == null) {
            loader = new LangsLoader(this);
            loader.execute(this);
        }
        else
            loader.setDelegate(this);
    }

    @Override
    public void finishTask(int message) {
        if (message == 0){
            Intent intent = new Intent(this,NavigationActivity.class);
            startActivity(intent);
        }
        else{
            new AlertDialog.Builder(this)
                    .setTitle(android.R.string.dialog_alert_title)
                    .setMessage(message).show();
        }
    }

    @Override
    public void updateProgress(Integer value) {
        progress.setProgress(value);
    }
}
