package com.kalinasoft.yantrans;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;


/**
 * Fragment that translates phrases through YandexAPI
 */
public class TranslateFragment extends Fragment implements TranslateTask.Comm{

    @SuppressWarnings("unused")
    private static final String TAG = "TranslateFragment";


//    private OnFragmentInteractionListener mListener;

    public TranslateFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TranslateFragment.
     */
    public static TranslateFragment newInstance() {
        return new TranslateFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }


    }

    TranslateTask translateTask;
    TextView trans;
    ScrollView sv;

    Spinner from;
    Spinner to;

    EditText et;

    CheckBox star;

    boolean translating = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        HistoryArray.getInstance().loadArray(getContext());
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_translate, container, false);

        //

        ArrayAdapter<String> langList = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item,YandexApi.getInstance().getLanguages());

        sv = (ScrollView)root.findViewById(R.id.scroll);

        from = (Spinner)root.findViewById(R.id.left_lang);
        to = (Spinner)root.findViewById(R.id.right_lang);

        from.setAdapter(langList);

        int from_position = YandexApi.getInstance().getLang_from();
        from.setSelection(from_position);
        from.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                YandexApi.getInstance().setLang_from(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        to.setAdapter(langList);
        int to_position = YandexApi.getInstance().getLang_to();
        to.setSelection(to_position);
        to.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                YandexApi.getInstance().setLang_to(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        et = (EditText)root.findViewById(R.id.input_word);
  //      if (savedInstanceState!= null)
  //          et.setText(savedInstanceState.getCharSequence("word"));
        trans = (TextView)root.findViewById(R.id.translation);
  //      if (savedInstanceState!= null)
  //          trans.setText(savedInstanceState.getCharSequence("translation"));
        final TranslateFragment tf = this;

        if(translateTask!=null && translateTask.getStatus() == AsyncTask.Status.RUNNING)
            translateTask.setDelegate(this);

        et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE){
                    star.setChecked(false);
                    translating = true;
                    translateTask = new TranslateTask();
                    translateTask.setDelegate(tf);
                    translateTask.execute(et.getText().toString());
                    setTranslation(R.string.loading);
                  //  trans.setText(YandexApi.getInstance().doTranslate(et.getText().toString()));
                }
                return false;
            }
        });

        star = (CheckBox)root.findViewById(R.id.action_favorite);
        star.setChecked(false);
        star.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && !translating){
                    HistoryArray.getInstance().getItem(
                            HistoryArray.getInstance().getCount(false)-1,false).setChecked(true);
                    HistoryArray.getInstance().saveArray(getContext());
                }
            }
        });

        return root;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //this is saved in singleton
//        outState.putInt("from",from.getSelectedItemPosition());
//        outState.putInt("to",to.getSelectedItemPosition());

        //this is saved by magical icicles
//        outState.putString("word",et.getText().toString());
//        outState.putString("translation",trans.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        trans = null;
        sv = null;
        from = null;
        to = null;
        et = null;
        star = null;
        if (translateTask!=null)
            translateTask.setDelegate(null);
        super.onDestroyView();
    }

//    public void onTranslate() {
//        if (mListener != null) {
//            mListener.onFragmentInteraction();
//        }
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    @Override
    public void finishTask(String text) {
        if (text.equals(""))
            setTranslation(R.string.lorem_ipsum);
        else {
            setTranslation(text);
            HistoryArray.getInstance().add(
                    new HistoryItem(
                            et.getText().toString(),trans.getText().toString(),
                            YandexApi.getInstance().getLangShort(
                                    YandexApi.getInstance().getLang_from()).toUpperCase(),
                            YandexApi.getInstance().getLangShort(
                                    YandexApi.getInstance().getLang_to()).toUpperCase()
                    ));
            HistoryArray.getInstance().getItem(
                    HistoryArray.getInstance().getCount(false)-1,false).setChecked(star.isChecked());
            HistoryArray.getInstance().saveArray(getContext());
        }
        translating = false;
    }

    private void setTranslation(int resId) {
        scrollUp();
        trans.setText(resId);
    }

    public void scrollUp(){
        sv.fullScroll(-1);
    }

    private void setTranslation(String text){
        scrollUp();
        trans.setText(text);
    }

//    /**
//     * part of standard fragment template
//     */
//    interface OnFragmentInteractionListener {
//        void onFragmentInteraction();
//    }
}
