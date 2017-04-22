package com.kalinasoft.yantrans;

import android.os.AsyncTask;

/**
 * Created by Andrey Kalikin on 22.04.2017.
 * Translation task, that can be detached and attached to fragment
 */

@SuppressWarnings("DefaultFileTemplate")
class TranslateTask extends AsyncTask<String,Integer,String> {

    interface Comm{
        void finishTask(String text);
    }

    private Comm delegate;

    void setDelegate(Comm delegate) {
        this.delegate = delegate;
    }

    @Override
    protected String doInBackground(String... params) {
        String result = YandexApi.getInstance().doTranslate(params[0]);
        try {
            while (delegate == null)
                Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (delegate!=null)
            delegate.finishTask(s);
    }
}
