package com.kalinasoft.yantrans;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Stack;

public class NavigationActivity extends AppCompatActivity {

    private Stack<Integer> nav_stack = new Stack<>();

    TranslateFragment fragTrans;
    HistoryFragment fragHis;
    FavsFragment fragFav;
    int selected;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_main:
                    switchFragment(fragTrans);
                    break;
                case R.id.navigation_history:
                    switchFragment(fragHis);
                    break;
                case R.id.navigation_fav:
                    switchFragment(fragFav);
                    break;
                default:
                    return false;

            }
            selected = item.getItemId();
            //           updateNavigationBarState(selected);
            return true;
        }


    };

    void switchFragment(Fragment newFragment) {
        FragmentTransaction transaction;
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content, newFragment);
        transaction.addToBackStack(null);
        nav_stack.push(selected);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if(!nav_stack.isEmpty()) {
            selected = nav_stack.pop();
            updateNavigationBarState(selected);
            super.onBackPressed();
        }
        else
            exitApp();
    }

    private void exitApp() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    private void updateNavigationBarState(int actionId){
        BottomNavigationView bnw = findViewById(R.id.navigation);
        Menu menu = bnw.getMenu();

        MenuItem item = menu.findItem(actionId);
        bnw.setOnNavigationItemSelectedListener(null);
        item.setChecked(true);
        bnw.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        if (savedInstanceState == null) {
            fragTrans = TranslateFragment.newInstance();
            fragHis = HistoryFragment.newInstance();
            fragFav = FavsFragment.newInstance();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content, fragTrans);
//            transaction.addToBackStack(null);
            selected = R.id.navigation_main;
//            nav_stack.push(selected);
            transaction.commit();
        }
        else
        {
            fragTrans = (TranslateFragment) getSupportFragmentManager().getFragment(
                    savedInstanceState,TranslateFragment.class.getName());
            fragHis = (HistoryFragment) getSupportFragmentManager().getFragment(
                    savedInstanceState,HistoryFragment.class.getName());
            fragFav = (FavsFragment) getSupportFragmentManager().getFragment(
                    savedInstanceState,FavsFragment.class.getName());


            if (fragTrans == null)
                fragTrans = TranslateFragment.newInstance();

            if (fragHis == null)
                fragHis = HistoryFragment.newInstance();

            if (fragFav == null)
                fragFav = FavsFragment.newInstance();

            selected = savedInstanceState.getInt("selected", R.id.navigation_main);
            setStack(savedInstanceState.getIntArray("navigation"));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (fragTrans.isAdded())
            getSupportFragmentManager().putFragment(outState,TranslateFragment.class.getName(),fragTrans);
        if (fragHis.isAdded())
            getSupportFragmentManager().putFragment(outState,HistoryFragment.class.getName(),fragHis);
        if (fragFav.isAdded())
            getSupportFragmentManager().putFragment(outState,FavsFragment.class.getName(),fragFav);

        outState.putInt("selected", selected);
        outState.putIntArray("navigation", getStack());
        super.onSaveInstanceState(outState);

    }

    int[] getStack() {
        int[] result = new int[nav_stack.size()];
        for (int i = 0; i < result.length; i++)
            result[i] = nav_stack.get(i);
        return result;
    }

    void setStack(int[] stack) {
        nav_stack.clear();
        for (int aStack : stack) {
            nav_stack.push(aStack);
        }
    }


}
