package com.kalinasoft.yantrans;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

/**
 * Created by Andrey Kalikin on 13.04.2017.
 * singleton for YandexAPI interface
 */


class YandexApi {
    private static final String key = "trnsl.1.1.20170413T042055Z.79c0858788638d3e.9c7cdeb56f775dfe48cfb1e34d8e6451c4de0978";

    //wrong key for testing exceptions
    //private static final String key = "trnsl.1.1.20170413T042055Z.79c08587888d3e.9c7cdeb56f775dfe48cfb1e34d8e6451c4de0978";
    private List<String> langs;
    private List<String> langs_short;

//    private Map<String,String> langs;

//    private Context context;
    private SSLContext trust_context;
    private String lang_code="en";

    private int lang_from=0;
    private int lang_to=1;

    private boolean loaded = false;



    private static final String TAG = "YandexAPI";

    /**
     * Trust Yandex certificate on old phones
     */
    //added in KitKat, so ignore this warning
    @SuppressWarnings("TryFinallyCanBeTryWithResources")
    void trustYandex(Context context) throws IOException {
        CertificateFactory cf;
        InputStream caInput= context.getResources().openRawResource(R.raw.certum);
        try{
            cf = CertificateFactory.getInstance("X.509");
            Certificate ca;
            ca = cf.generateCertificate(caInput);
            System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());

            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            // Create an SSLContext that uses our TrustManager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);
            trust_context = sslContext;
        } catch (CertificateException | IOException | NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            e.printStackTrace();
        } finally {
        caInput.close();
    }

    }

    private String makeServiceCall(String reqUrl, String text) throws IOException {
        String response;
        URL url = new URL(reqUrl);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setSSLSocketFactory(trust_context.getSocketFactory());

        if (text.equals(""))
            conn.setRequestMethod("GET");
        else{
            String outtext = "text="+ URLEncoder.encode(text,"UTF-8");
            Log.d(TAG, "makeServiceCall: "+outtext);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setFixedLengthStreamingMode(outtext.getBytes().length);
            OutputStream out = conn.getOutputStream();
            out.write(outtext.getBytes());
            out.flush();
            out.close();
        }


        // read the response
        InputStream in = new BufferedInputStream(conn.getInputStream());
        response = convertStreamToString(in);
        Log.d(TAG, response);
        return response;
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    private String commandLangs() throws IOException {
        String YandexURI = "https://translate.yandex.net/api/v1.5/tr.json/";
        YandexURI+="getLangs";
        YandexURI+="?key=";
        YandexURI+=key;
        YandexURI+="&ui=";
        YandexURI+= lang_code;

        return makeServiceCall(YandexURI,"");
    }

    private String commandTranslate(String text) throws IOException {
        if (text.trim().equals(""))
            throw new IOException();
        String YandexURI = "https://translate.yandex.net/api/v1.5/tr.json/";
        YandexURI+="translate";
        YandexURI+="?key=";
        YandexURI+=key;
        YandexURI+="&lang=";
        YandexURI+=langs_short.get(lang_from)+"-"+langs_short.get(lang_to);


        return makeServiceCall(YandexURI,text);
    }



//    public void setContext(Context context) throws IOException {
//        this.context = context;
//        trustYandex();
//    }

    private static YandexApi instance = new YandexApi();

    static YandexApi getInstance() {
        return instance;
    }

    List<String> getLanguages(){
        return langs;
    }

    /**
     * Get languages list and checks system language against that list
     * @throws IOException when key is wrong or no internet
     * @throws JSONException when data is not a JSON
     */
    void langsGetEnglish() throws IOException, JSONException {
        lang_code = "en";
        String json = commandLangs();
        Log.d(TAG, json);
        String check_code = Locale.getDefault().getLanguage();
        Log.d(TAG, json);
        JSONObject response = new JSONObject(json);
        JSONObject array = response.getJSONObject("langs");
        Iterator<String> i = array.keys();
        while(i.hasNext()){
            String key = i.next();
  //          langs_short.add(key);
            if (check_code.equals(key))
                lang_code = check_code;
            //langs.add(array.getString(key));
//                Log.d(TAG, key);
        }
    }

    /**
     * saves languages list translated to gui language
     * @throws IOException when key is wrong or no internet
     * @throws JSONException when data is not a JSON
     */

    void langsSync() throws IOException, JSONException {
        langs_short = new ArrayList<>();
        langs = new ArrayList<>();
        String json = commandLangs();
        JSONObject response = new JSONObject(json);
        JSONObject array = response.getJSONObject("langs");
        Iterator<String> i = array.keys();
        while(i.hasNext()){
            String key = i.next();
            langs_short.add(key);
            langs.add(array.getString(key));
            Log.d(TAG, array.getString(key));
        }
    }


    /**
     * Translates text
     * @param text text to translate
     * @return translation
     */
    String doTranslate(String text)  {
        String translation = "";
        try {
            String json = commandTranslate(text);
            JSONObject response = new JSONObject(json);
            JSONArray array = response.getJSONArray("text");
            translation = array.getString(0);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return translation;
    }


    /**
     * sorts language list by alphabet, and sets GUI lang first, english/russian second/third
     */
    void langsSort(){
        HashMap<String,String> map = new HashMap<>();
        ArrayList<String> new_langs = new ArrayList<>();
        ArrayList<String> new_short = new ArrayList<>();
        for (int i = 0; i < langs.size(); i++) {
            map.put(langs_short.get(i), langs.get(i));
        }
        new_short.add(lang_code);
        new_langs.add(map.get(lang_code));
        map.remove(lang_code);
        if (!new_short.get(0).equals("en")){
            new_short.add("en");
            new_langs.add(map.get("en"));
            map.remove("en");
        }
        if (!new_short.get(0).equals("ru")){
            new_short.add("ru");
            new_langs.add(map.get("ru"));
            map.remove("ru");
        }

        ArrayList<String> sorted_map = new ArrayList<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            sorted_map.add(entry.getValue());
        }
        Collections.sort(sorted_map);
        for (String s:sorted_map) {
            Log.d(TAG,s);
            new_langs.add(s);
            String key = "";
            for (Map.Entry<String, String> entry : map.entrySet())
            {
                if (entry.getValue().equals(s))
                    key = entry.getKey();
            }
            new_short.add(key);
            map.remove(key);
        }

        Log.d(TAG, "langsSort: "+new_langs.toString());
        langs = new_langs;
        langs_short = new_short;
        loaded = true;
    }

//    /**
//     * Returns code for language
//     * @param name name of language in UI language
//     * @return code of language
//     */
//    String getCode (String name){
//        int i = langs.indexOf(name);
//        return langs_short.get(i);
//    }

    void setLang_from(int number){
        lang_from = number;
    }

    void setLang_to(int number){
        lang_to = number;
    }

    int getLang_from() {
        return lang_from;
    }

    int getLang_to() {
        return lang_to;
    }

    boolean isLoaded() {
        return loaded;
    }

    String getLangShort(int i){
        return langs_short.get(i);
    }
}
