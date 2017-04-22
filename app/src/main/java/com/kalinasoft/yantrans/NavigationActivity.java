package com.kalinasoft.yantrans;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
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

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction transaction;
            switch (item.getItemId()) {
                case R.id.navigation_main:
                    transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.content,fragTrans);
                    transaction.addToBackStack(null);
                    nav_stack.push(item.getItemId());
                    transaction.commit();
                    //TODO:change fragment to translate fragment
                    break;
                case R.id.navigation_history:
                    transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.content,fragHis);
                    transaction.addToBackStack(null);
                    nav_stack.push(item.getItemId());
                    transaction.commit();

                    //TODO:change fragment to history fragment
                    break;
                case R.id.navigation_fav:
                    transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.content,fragFav);
                    transaction.addToBackStack(null);
                    nav_stack.push(item.getItemId());
                    transaction.commit();

                    //TODO:change fragment to history fragment
                    break;
                default:
                    return false;

            }
            updateNavigationBarState(item.getItemId());
            return true;
        }


    };

    @Override
    public void onBackPressed() {
        if(!nav_stack.isEmpty()) {
            updateNavigationBarState(nav_stack.pop());
            super.onBackPressed();
        }
        else
            finish();
    }

    private void updateNavigationBarState(int actionId){
        BottomNavigationView bnw = (BottomNavigationView) findViewById(R.id.navigation);
        Menu menu = bnw.getMenu();

        for (int i = 0, size = menu.size(); i < size; i++) {
            MenuItem item = menu.getItem(i);
            item.setChecked(item.getItemId() == actionId);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        if (savedInstanceState == null) {
            fragTrans = TranslateFragment.newInstance();
            fragHis = HistoryFragment.newInstance();
            fragFav = FavsFragment.newInstance();
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

        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content,fragTrans);
        transaction.commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (fragTrans.isAdded())
            getSupportFragmentManager().putFragment(outState,TranslateFragment.class.getName(),fragTrans);
        if (fragHis.isAdded())
            getSupportFragmentManager().putFragment(outState,HistoryFragment.class.getName(),fragHis);
        if (fragFav.isAdded())
            getSupportFragmentManager().putFragment(outState,FavsFragment.class.getName(),fragFav);
        super.onSaveInstanceState(outState);
    }

}
