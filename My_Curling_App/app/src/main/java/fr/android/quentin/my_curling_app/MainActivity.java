package fr.android.quentin.my_curling_app;

import android.content.Intent;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_add) {
            Intent intent = new Intent(this, add_match.class);
            startActivity(intent);
            return true;
        }else if(id == R.id.menu_matchs){
            Intent intent = new Intent(this, view_matchs.class);
            startActivity(intent);
            return true;
        } else if(id == R.id.menu_stats){
            Intent intent = new Intent(this, view_stats.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void launchAddMatch(View view){
        //Creation du intent pour la nouvelle activité
        Intent intent = new Intent(this, add_match.class);
        //creation de la nouvelle activité
        startActivity(intent);
    }

    public void launchViewMatchs(View view){
        //Creation du intent pour la nouvelle activité
        Intent intent = new Intent(this, add_match.class);
        //creation de la nouvelle activité
        startActivity(intent);
    }

    public void launchViewStats(View view){
        //Creation du intent pour la nouvelle activité
        Intent intent = new Intent(this, view_stats.class);
        //creation de la nouvelle activité
        startActivity(intent);
    }
}
