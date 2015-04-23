package com.techinterview.elementz.techinterviewapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.techinterview.elementz.helpers.Users;
import com.techinterview.elementz.helpers.Utility;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.InputStream;



public class ViewUserActivity extends ActionBarActivity {

    private ImageView ivViewUser;
    private TextView tvViewUserName,
                       tvViewUserBday,
                       tvViewUserAge,
                       tvViewUserNextBday,
                       tvViewUserSex;
    protected Intent userIntent;
    protected ProgressDialog progressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user);

        //Initialize all components
        initComponents();
        //set data from row item user to display in activity
        setViewUsers();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_user, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //noinspection SimplifiableIfStatement
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initComponents(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("View Screen");

        ivViewUser = (ImageView)findViewById(R.id.ivViewUser);
        tvViewUserName = (TextView)findViewById(R.id.tvViewUserName);
        tvViewUserBday = (TextView)findViewById(R.id.tvViewUserBday);
        tvViewUserAge = (TextView)findViewById(R.id.tvViewUserAge);
        tvViewUserNextBday = (TextView)findViewById(R.id.tvViewUserNextBday);
        tvViewUserSex = (TextView)findViewById(R.id.tvViewUserSex);

        progressBar = new ProgressDialog(this);

        progressBar.setIndeterminate(true);
        progressBar.setMessage("Loading picture...");
        progressBar.setCancelable(true);
        progressBar.setCanceledOnTouchOutside(true);
    }

    private void setViewUsers(){

        userIntent = getIntent();
        Users selectedUser = (Users) userIntent.getSerializableExtra("user");
        String birthday = selectedUser.getBirthday(); //for getting age and countdown to next Bday

        new DownloadImageTask(ivViewUser).execute(selectedUser.getImage());

        tvViewUserName.setText("Name: "+ selectedUser.getLastName() + ", " + selectedUser.getFirstName());
        tvViewUserBday.setText("Birthday: "+ birthday);
        tvViewUserSex.setText("Sex: "+ selectedUser.getSex());

        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("MMMM d, yyyy");
        LocalDate date = dateTimeFormatter.parseLocalDate(birthday);
        tvViewUserAge.setText("Age: "+ Utility.getAge(date) + " yrs. old");
        tvViewUserNextBday.setText("Days Till Next Bday: " + Utility.getNextBirthday(date) + " days");

    }


    public void onClick(View v){
        switch(v.getId()){
            case R.id.btnDelete:
                //Add asynctask to delete from webservice API here
                messageDialog(this, null, "Are you sure you want to delete User?");

                break;
        }
    }

    private void messageDialog(Activity activity, String title, String message){
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setPositiveButton("No", null);
        dialog.setNegativeButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                onBackPressed();
                Toast.makeText(ViewUserActivity.this, "User deleted!", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.create().show();


    }
    //AsyncTask to download link given from JSON for Image
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.show();
        }


            public DownloadImageTask(ImageView bmImage) {

            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String url_display = urls[0];
            Bitmap mIcon = null;
            try {
                InputStream in = new java.net.URL(url_display).openStream();
                mIcon = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon;
        }

        protected void onPostExecute(Bitmap result) {

            bmImage.setImageBitmap(result);
            progressBar.dismiss();
        }
    }
}
