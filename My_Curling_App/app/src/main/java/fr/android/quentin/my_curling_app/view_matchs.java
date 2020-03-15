package fr.android.quentin.my_curling_app;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.BaseColumns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class view_matchs extends AppCompatActivity {

    private managerSQLI.FeedReaderDbHelper myBDD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_matchs);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myBDD = new managerSQLI.FeedReaderDbHelper(getApplicationContext());
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

    public void launchDisplayMatchs(View view){
        SQLiteDatabase db = myBDD.getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                BaseColumns._ID,
                managerSQLI.FeedEntry.COLUMN_NAME_MATCH_NAME,
                managerSQLI.FeedEntry.COLUMN_NAME_MATCH_DATE,
                managerSQLI.FeedEntry.COLUMN_NAME_MATCH_TIME,
                managerSQLI.FeedEntry.COLUMN_NAME_MATCH_STATUS,
                managerSQLI.FeedEntry.COLUMN_NAME_MATCH_PICTURE,
                managerSQLI.FeedEntry.COLUMN_NAME_MATCH_SCORE
        };

// Filter results WHERE "title" = 'My Title'
        String selection ="";
        String[] selectionArgs = {};

// How you want the results sorted in the resulting Cursor
        String sortOrder =
                managerSQLI.FeedEntry.COLUMN_NAME_MATCH_NAME + " DESC";

        Cursor cursor = db.query(
                managerSQLI.FeedEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );

        LinearLayout layoutMatchs = (LinearLayout)findViewById(R.id.list_matchs);
        layoutMatchs.removeAllViews();

        List itemIds = new ArrayList<>();
        while(cursor.moveToNext()) {
            long itemId = cursor.getLong(
                    cursor.getColumnIndexOrThrow(managerSQLI.FeedEntry._ID));
            String matchName = cursor.getString(
                    cursor.getColumnIndexOrThrow(managerSQLI.FeedEntry.COLUMN_NAME_MATCH_NAME));

            String matchDate = cursor.getString(
                    cursor.getColumnIndexOrThrow(managerSQLI.FeedEntry.COLUMN_NAME_MATCH_DATE));

            String matchTime = cursor.getString(
                    cursor.getColumnIndexOrThrow(managerSQLI.FeedEntry.COLUMN_NAME_MATCH_TIME));

            String finalTextMatch = "Name : " + matchName + "\nDate : " + matchDate + "\nHeure : " + matchTime;

            String matchStatus = cursor.getString(
                    cursor.getColumnIndexOrThrow(managerSQLI.FeedEntry.COLUMN_NAME_MATCH_STATUS));

            if(matchStatus.equals("1")){
                finalTextMatch += "\nResultat : Victoire";
            }else if(matchStatus.equals("2")){
                finalTextMatch += "\nResultat : Nul";
            }else{
                finalTextMatch += "\nResultat : Defaite";
            }

            String matchPhoto = cursor.getString(
                    cursor.getColumnIndexOrThrow(managerSQLI.FeedEntry.COLUMN_NAME_MATCH_PICTURE));

            //finalTextMatch += "\nPhoto : " + matchPhoto + "\n\n\n";

            ImageView my_image = new ImageView(this);
            my_image.setImageURI(Uri.parse(matchPhoto));

            TextView newScore = new TextView(this);
            newScore.setText(finalTextMatch);
            layoutMatchs.addView(newScore);
            layoutMatchs.addView(my_image);
            my_image.getLayoutParams().height = 500;
            my_image.getLayoutParams().width = 500;
            //my_image.requestLayout();
        }
        cursor.close();
    }

    public void erraseDataBase(View view) {

        SQLiteDatabase db = myBDD.getReadableDatabase();

        int deletedRows = db.delete(managerSQLI.FeedEntry.TABLE_NAME, null, null);
        launchDisplayMatchs(view);

    }
}
