package com.example.volleyapp2.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.volleyapp2.R;
import com.example.volleyapp2.background.MyService;

/**
 * This is the Main Activity. It is needed to display a list of news
 * @see android.support.v7.app.AppCompatActivity
 * @author ilia
 */
public class ActivityMain extends AppCompatActivity {

    private static final String stringToTestNotification = "ololo";
    private static final String MY_PREF = "MyPref";
    private static final String SAVED_FIRST_NEW = "saved_first_new";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        Intent intent=new Intent(this,MyService.class);
        startService(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            SharedPreferences sharedPreferences;
            sharedPreferences = getSharedPreferences(MY_PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(SAVED_FIRST_NEW, stringToTestNotification);
            editor.commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
