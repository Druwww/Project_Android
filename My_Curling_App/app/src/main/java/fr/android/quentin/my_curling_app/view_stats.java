package fr.android.quentin.my_curling_app;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

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

public class view_stats extends AppCompatActivity {

    GraphView graphMyScore;

    private managerSQLI.FeedReaderDbHelper myBDD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_stats);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        graphMyScore = (GraphView) findViewById(R.id.graphScore);


        // activate horizontal zooming and scrolling
        graphMyScore.getViewport().setScalable(true);
        // activate horizontal scrolling
        graphMyScore.getViewport().setScrollable(true);
        // activate horizontal and vertical zooming and scrolling
        graphMyScore.getViewport().setScalableY(true);
        // activate vertical scrolling
        graphMyScore.getViewport().setScrollableY(true);
        //graphMyScore.setVisibility(View.VISIBLE);

        myBDD = new managerSQLI.FeedReaderDbHelper(getApplicationContext());
        display_stats();
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
        } else if (id == R.id.menu_matchs) {
            Intent intent = new Intent(this, view_matchs.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_stats) {
            Intent intent = new Intent(this, view_stats.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void display_stats() {
        SQLiteDatabase db = myBDD.getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                BaseColumns._ID,
                managerSQLI.FeedEntry.COLUMN_NAME_MATCH_NAME,
                managerSQLI.FeedEntry.COLUMN_NAME_MATCH_DATE,
                managerSQLI.FeedEntry.COLUMN_NAME_MATCH_TIME,
                managerSQLI.FeedEntry.COLUMN_NAME_MATCH_STATUS,
                managerSQLI.FeedEntry.COLUMN_NAME_MATCH_SCORE,
                managerSQLI.FeedEntry.COLUMN_NAME_MATCH_POSITION
        };

// Filter results WHERE "title" = 'My Title'
        String selection = "";
        String[] selectionArgs = {};

// How you want the results sorted in the resulting Cursor
        String sortOrder =
                managerSQLI.FeedEntry.COLUMN_NAME_MATCH_DATE + " DESC";

        Cursor cursor = db.query(
                managerSQLI.FeedEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );

        List itemIds = new ArrayList<>();
        ArrayList<Integer> allMyScore = new ArrayList<Integer>();
        ArrayList<Integer> allOutScore = new ArrayList<Integer>();

        while (cursor.moveToNext()) {
            long itemId = cursor.getLong(
                    cursor.getColumnIndexOrThrow(managerSQLI.FeedEntry._ID));

            String matchName = cursor.getString(
                    cursor.getColumnIndexOrThrow(managerSQLI.FeedEntry.COLUMN_NAME_MATCH_NAME));

            String matchDate = cursor.getString(
                    cursor.getColumnIndexOrThrow(managerSQLI.FeedEntry.COLUMN_NAME_MATCH_DATE));

            String matchTime = cursor.getString(
                    cursor.getColumnIndexOrThrow(managerSQLI.FeedEntry.COLUMN_NAME_MATCH_TIME));

            String position = cursor.getString(
                    cursor.getColumnIndexOrThrow(managerSQLI.FeedEntry.COLUMN_NAME_MATCH_POSITION));


            String matchStatus = cursor.getString(
                    cursor.getColumnIndexOrThrow(managerSQLI.FeedEntry.COLUMN_NAME_MATCH_STATUS));

            /*
            if(matchStatus.equals("1")){
                finalTextMatch += "\nResultat : Victoire";
            }else if(matchStatus.equals("2")){
                finalTextMatch += "\nResultat : Nul";
            }else{
                finalTextMatch += "\nResultat : Defaite";
            }*/

            byte[] scores = cursor.getBlob(
                    cursor.getColumnIndexOrThrow(managerSQLI.FeedEntry.COLUMN_NAME_MATCH_SCORE));

            ArrayList<Integer> intScores = new ArrayList<Integer>();
            int b = 0;
            for (int i = 0; i < scores.length; i += 4) {
                byte[] lotOfBytes = new byte[4];
                intScores.add(scores[i] * 1000 + scores[i + 1] * 100 + scores[i + 2] * 10 + scores[i + 3]);
                b++;
            }

            for (int i = 0; i < intScores.size() / 2; i += 2) {
                allMyScore.add(intScores.get(i));
                allOutScore.add(intScores.get(i+1));
            }

        }
        cursor.close();

        try {

            DataPoint[] scoreLocal;
            DataPoint[] scoreExt;
            scoreLocal = new DataPoint[allMyScore.size()];
            scoreExt = new DataPoint[allOutScore.size()];

            for (int i = 0; i < allMyScore.size(); i++) {
                scoreLocal[i] = new DataPoint(i + 1, allMyScore.get(i));
                scoreExt[i] = new DataPoint(i+1, allOutScore.get(i));
            }

            LineGraphSeries<DataPoint> seriesHome = new LineGraphSeries<>(scoreLocal);
            seriesHome.setColor(Color.GREEN);
            LineGraphSeries<DataPoint> seriesOut = new LineGraphSeries<>(scoreExt);
            seriesOut.setColor(Color.RED);
            graphMyScore.addSeries(seriesHome);
            graphMyScore.addSeries(seriesOut);

            seriesHome.setTitle("Home");
            seriesOut.setTitle("Ext");
            graphMyScore.getLegendRenderer().setVisible(true);
            graphMyScore.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);


        } catch (IllegalArgumentException e) {
            //
        }

    }
}
