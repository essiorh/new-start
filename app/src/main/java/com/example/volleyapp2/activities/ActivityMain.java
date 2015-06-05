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


public class ActivityMain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popitka);
        Intent intent=new Intent(this,MyService.class);
        startService(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_popitka, menu);
        return true;
    }

    /**
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            String SAVED_FIRST_NEW = "saved_first_new";
            SharedPreferences sharedPreferences;
            sharedPreferences = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(SAVED_FIRST_NEW, "ololo");
            editor.commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
