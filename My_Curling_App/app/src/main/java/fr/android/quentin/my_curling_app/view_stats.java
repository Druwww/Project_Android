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
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
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

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//Source des graph : https://github.com/jjoe64/GraphView

public class view_stats extends AppCompatActivity {

    GraphView graphMyScore;
    GraphView graphMyStatus;

    private managerSQLI.FeedReaderDbHelper myBDD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_stats);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        graphMyScore = (GraphView) findViewById(R.id.graphScore);
        graphMyStatus = (GraphView) findViewById(R.id.graphMatchStatus);


        // activate horizontal zooming and scrolling
        graphMyScore.getViewport().setScalable(true);
        // activate horizontal scrolling
        graphMyScore.getViewport().setScrollable(true);
        // activate horizontal and vertical zooming and scrolling

        myBDD = new managerSQLI.FeedReaderDbHelper(getApplicationContext());
        try {
            display_stats();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public void display_stats() throws IOException {
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

        int numberVictory = 0;
        int numberDraw = 0;
        int numberDefeat = 0;

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


            if(matchStatus.equals("2")){
                numberVictory++;
            }else if(matchStatus.equals("1")){
                numberDraw++;
            }else{
                numberDefeat++;
            }

            byte[] scores = cursor.getBlob(
                    cursor.getColumnIndexOrThrow(managerSQLI.FeedEntry.COLUMN_NAME_MATCH_SCORE));

            ArrayList<String> intScores = new ArrayList<>();

            ByteArrayInputStream bais = new ByteArrayInputStream(scores);
            DataInputStream in = new DataInputStream(bais);
            //true = pair
            boolean i = true;
            while (in.available() > 0) {
                String element = in.readUTF();
                intScores.add(element);
                if(i){
                    allMyScore.add(Integer.parseInt(element));

                }else{
                    allOutScore.add(Integer.parseInt(element));
                }
                i = !i;
            }

        }
        cursor.close();

        try {

            //Graph score
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

            //Graph status
            BarGraphSeries<DataPoint> seriesStatus = new BarGraphSeries<>(new DataPoint[] {
                    new DataPoint(1, numberVictory),
                    new DataPoint(2, numberDraw),
                    new DataPoint(3, numberDefeat)
            });
            graphMyStatus.addSeries(seriesStatus);
            seriesStatus.setValueDependentColor(new ValueDependentColor<DataPoint>() {
                @Override
                public int get(DataPoint data) {
                    return Color.rgb((int) data.getX()*255/4, (int) Math.abs(data.getY()*255/6), 100);
                }
            });
            seriesStatus.setSpacing(50);
            seriesStatus.setDrawValuesOnTop(true);
            seriesStatus.setValuesOnTopColor(Color.BLUE);

        } catch (IllegalArgumentException e) {
            //
        }

    }
}
