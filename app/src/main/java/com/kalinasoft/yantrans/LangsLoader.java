package com.kalinasoft.yantrans;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;

import java.io.IOException;

/**
 * Created by Andrey Kalikin on 17.04.2017.
 */

class LangsLoader extends AsyncTask<Context,Integer,Integer> {

    private Comm delegate = null;

    private Integer progress = 0;

    private int errorMessage = 0;

    private void incProgress(){
        Integer chip = 50;
        progress+= chip;
        publishProgress(progress);
    }

    interface Comm{
        void finishTask(int error);
        void updateProgress(Integer value);
    }

    @Override
    protected Integer doInBackground(Context... contexts) {
        YandexApi ya = YandexApi.getInstance();
        try {
            ya.trustYandex(contexts[0]);
            ya.langsGetEnglish();
            incProgress();
            ya.langsSync();
            incProgress();
            ya.langsSort();

        } catch (IOException | JSONException e) {
            if (e.getClass() == java.io.FileNotFoundException.class)
                errorMessage = R.string.error_wrong_key;
            if (e.getClass() == java.net.UnknownHostException.class)
                errorMessage = R.string.error_wrong_iternets;
            e.printStackTrace();
        }
        //waiting for message to go
        try {
            while (delegate == null)
                Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return errorMessage;
    }

    @Override
    protected void onPostExecute(Integer i) {
        super.onPostExecute(i);
        delegate.finishTask(i);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        delegate.updateProgress(values[0]);
        super.onProgressUpdate(values);
    }

    LangsLoader(Comm delegate){
        super();
        setDelegate(delegate);
    }

    void setDelegate(Comm delegate) {
        this.delegate = delegate;
    }
}
