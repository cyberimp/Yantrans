package com.kalinasoft.yantrans;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by Andrey Kalikin on 17.04.2017.
 */

class HistoryArray {
    private ArrayList<HistoryItem> content;

    private static HistoryArray instance=new HistoryArray();

    static HistoryArray getInstance() {
        return instance;
    }

    void loadArray(Context context){
        FileInputStream fIn;
        ObjectInputStream osw;
        try {
            fIn = context.openFileInput("history.dat");
            osw = new ObjectInputStream(fIn);
            //noinspection unchecked
            content = (ArrayList<HistoryItem>) osw.readObject();
            osw.close();
            fIn.close();
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            content = new ArrayList<>();
            e.printStackTrace();
        }
    }


    void saveArray(Context context){
        FileOutputStream fOut = null;
        ObjectOutputStream osw = null;

        try{
            fOut = context.openFileOutput("history.dat", Context.MODE_PRIVATE);
            osw = new ObjectOutputStream(fOut);

            osw.writeObject(content);
            osw.flush();
            //Toast.makeText(context, "Settings saved",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            // Toast.makeText(context, "Settings not saved",Toast.LENGTH_SHORT).show();
        }
        finally {
            try {
                if(osw!=null)
                    osw.close();
                if (fOut != null)
                    fOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private HistoryArray(){
        content = new ArrayList<>();
    }

//    //factory method for creating testing history
//    public static HistoryArray getDummy(){
//        HistoryArray dummy = new HistoryArray();
//        dummy.add(new HistoryItem("streamline","оптимизация","EN","RU"));
//        dummy.add(new HistoryItem("enigma","загадка","EN","RU"));
//        dummy.add(new HistoryItem("streamlined","обтекаемый","EN","RU"));
//        dummy.add(new HistoryItem("Привет","Hi","RU","EN"));
//        dummy.add(new HistoryItem("распространитель","distributor","RU","EN"));
//        dummy.add(new HistoryItem("purveyor","поставщик","EN","RU"));
//        dummy.add(new HistoryItem("purveyors","заготовителей","EN","RU"));
//        return dummy;
//    }

    int getCount(boolean favsOnly){
        if (!favsOnly)
            return content.size();
        else
        {
            int i = 0;
            for (HistoryItem hi:content) {
                if (hi.isChecked())
                    i++;
            }
            return i;
        }
    }

    void add(HistoryItem item){
        content.add(item);
    }

    HistoryItem getItem(int pos, boolean favsOnly){
        if (!favsOnly)
            return content.get(pos);
        else
        {
            int i = 0;
            for (HistoryItem hi:content) {
                if (hi.isChecked()) {
                    if (i==pos)
                        return hi;
                    i++;
                }
            }
            return null;
        }

    }

}
