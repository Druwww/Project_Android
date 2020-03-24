package fr.android.quentin.my_curling_app;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.os.Environment;
import android.provider.MediaStore;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import fr.android.quentin.my_curling_app.managerSQLI.FeedReaderDbHelper;

import static java.security.AccessController.getContext;


//Documentation : https://androidclarified.com/pick-image-gallery-camera-android/
public class add_match extends AppCompatActivity {

    private static final int GALLERY_REQUEST_CODE = 20;
    private static final int REQUEST_IMAGE_CAPTURE = 30;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private String currentPhotoPath;

    private ArrayList<Integer> myScores;

    private FeedReaderDbHelper myBDD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_match);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myScores = new ArrayList<Integer>();;
        currentPhotoPath = "";
        myBDD = new FeedReaderDbHelper(getApplicationContext());

        verifyStoragePermissions(this);
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

    public void pickFromGallery(View view) {
        //Create an Intent with action as ACTION_PICK
        Intent intent = new Intent(Intent.ACTION_PICK);
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.setType("image/*");
        //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        // Launching the Intent
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    private String saveImage(Bitmap finalBitmap, String image_name) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root);
        myDir.mkdirs();
        String fname = "Image-" + image_name + ".jpg";
        File file = new File((this
                .getApplicationContext().getFileStreamPath(fname)
                .getPath()));
        if (file.exists()) file.delete();
        try {

            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            return root + fname;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public void captureFromCamera(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Result code is RESULT_OK only if the user selects an Image
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            ImageView my_image = findViewById(R.id.match_picture);
            switch (requestCode) {
                case GALLERY_REQUEST_CODE:
                    //data.getData returns the content URI for the selected Image
                    Uri selectedImage = data.getData();
                    currentPhotoPath = selectedImage.toString();
                    my_image.setImageURI(selectedImage);
                    my_image.getLayoutParams().height = 500;
                    my_image.getLayoutParams().width = 500;
                    my_image.requestLayout();
                    break;

                case REQUEST_IMAGE_CAPTURE:
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    currentPhotoPath = "camera";
                    my_image.setImageBitmap(imageBitmap);
                    my_image.getLayoutParams().height = 500;
                    my_image.getLayoutParams().width = 500;
                    my_image.requestLayout();
                    break;
            }
        }
    }


    public void add_manche_score(View view) {
        EditText score_home = findViewById(R.id.match_score_home);
        EditText score_ext = findViewById(R.id.match_score_ext);

        if(score_home.getText().toString().matches("") || score_ext.getText().toString().matches("")){
            return;
        }

        LinearLayout layoutScore = findViewById(R.id.add_match_display_score);
        TextView newScore = new TextView(this);
        newScore.setText("Home : " + score_home.getText() + " - Ext : " + score_ext.getText());
        layoutScore.addView(newScore);

        myScores.add(Integer.parseInt(String.valueOf(score_home.getText())));
        myScores.add(Integer.parseInt(String.valueOf(score_ext.getText())));

        score_home.setText("");
        score_ext.setText("");
    }


    public void save_match(View view) throws ParseException {

        Snackbar errorPopUp;


        EditText myMatchName = findViewById(R.id.match_name);

        if(myMatchName.getText().toString().equals("")){
            errorPopUp = Snackbar.make(view, "Please enter a match Name", 3000);
            errorPopUp.show();
            return;
        }

        EditText myMatchDate = findViewById(R.id.match_date);

        if(myMatchName.getText().toString().equals("")){
            errorPopUp = Snackbar.make(view, "Please enter a match Date", 3000);
            errorPopUp.show();
            return;
        }

        EditText myMatchTime = findViewById(R.id.match_time);

        if(myMatchTime.getText().toString().equals("")){
            errorPopUp = Snackbar.make(view, "Please enter a match Time", 3000);
            errorPopUp.show();
            return;
        }

        byte[] img;

        if(currentPhotoPath.equals("")) {
            errorPopUp = Snackbar.make(view, "Please enter a match Picture", 3000);
            errorPopUp.show();
            return;
        }else{
            ImageView myImg = findViewById(R.id.match_picture);
            Bitmap b = ((BitmapDrawable)myImg.getDrawable()).getBitmap();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            img = bos.toByteArray();
        }

        //1 victory, 2 draw, 3 defeat
        int statusMatch = 0;
        RadioGroup myMatchStatus = findViewById(R.id.match_radio_group);

        RadioButton radioVictory = findViewById(R.id.match_victory);
        RadioButton radioDraw = findViewById(R.id.match_draw);
        RadioButton radioDefeat = findViewById(R.id.match_defeat);

        int selectedStatus = myMatchStatus.getCheckedRadioButtonId();

        if(selectedStatus == radioVictory.getId()){
            statusMatch = 1;
        }else if (selectedStatus == radioDraw.getId()){
            statusMatch = 2;
        }else if (selectedStatus == radioDefeat.getId()){
            statusMatch = 3;
        }else{
            //error no radio button select
            errorPopUp = Snackbar.make(view, "Please select a Match Status", 3000);
            errorPopUp.show();
            return;
        }

        SQLiteDatabase db = myBDD.getWritableDatabase();

// Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(managerSQLI.FeedEntry.COLUMN_NAME_MATCH_NAME, myMatchName.getText().toString());
        values.put(managerSQLI.FeedEntry.COLUMN_NAME_MATCH_DATE, myMatchDate.getText().toString());
        values.put(managerSQLI.FeedEntry.COLUMN_NAME_MATCH_TIME, myMatchTime.getText().toString());
        values.put(managerSQLI.FeedEntry.COLUMN_NAME_MATCH_STATUS, Integer.toString(statusMatch));
        values.put(managerSQLI.FeedEntry.COLUMN_NAME_MATCH_PICTURE, img);
        values.put(managerSQLI.FeedEntry.COLUMN_NAME_MATCH_SCORE, "test");


// Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(managerSQLI.FeedEntry.TABLE_NAME, null, values);
        errorPopUp = Snackbar.make(view, "Match Ajouté ! : " + newRowId, 3000);
        errorPopUp.show();
    }

}
