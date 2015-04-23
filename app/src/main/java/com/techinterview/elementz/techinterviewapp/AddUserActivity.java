package com.techinterview.elementz.techinterviewapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.techinterview.elementz.helpers.DatabaseHelper;
import com.techinterview.elementz.helpers.Users;
import com.techinterview.elementz.helpers.Utility;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.joda.time.LocalDate;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;


public class AddUserActivity extends ActionBarActivity {

    private static final int CAMERA_PIC_REQUEST = 1337;
    private EditText etAddLastName,
            etAddFirstName;
    private TextView tvAddBirthday,
            tvAddAge,
            tvAddNextBirthday;
    private RadioButton rbGenderFemale,
            rbGenderMale;
    private ImageView ivAddUser;
    private RadioGroup rgGender;
    private String radioValue;
    private DatabaseHelper DbHelper;
    private Bitmap addedImage;
    private Users user;

    protected static final int RESPONSE_OK= 200, ONE = 1; //response from server when data is received
    protected ProgressDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        initComponents();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Back button on Actionbar
        switch (item.getItemId()) {
            case android.R.id.home:
                if(!validate()) {
                    messageDialog(this, null, "Are you sure you do not want to add User?");
                }
                else{
                    onBackPressed();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void initComponents() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Screen");

        DbHelper = new DatabaseHelper(this);

        ivAddUser = (ImageView) findViewById(R.id.ivAddUser);

        etAddLastName = (EditText) findViewById(R.id.etAddLastName);
        etAddFirstName = (EditText) findViewById(R.id.etAddFirstName);

        tvAddBirthday = (TextView) findViewById(R.id.tvAddBirthday);
        tvAddAge = (TextView) findViewById(R.id.tvAddAge);
        tvAddNextBirthday = (TextView) findViewById(R.id.tvAddNextBirthday);

        rbGenderFemale = (RadioButton)findViewById(R.id.rbGenderFemale);
        rbGenderMale = (RadioButton)findViewById(R.id.rbGenderMale);
        rgGender =(RadioGroup)findViewById(R.id.rgGender);

        progressBar = new ProgressDialog(this);

        progressBar.setIndeterminate(true);
        progressBar.setMessage("please wait..");
        progressBar.setCancelable(true);
        progressBar.setCanceledOnTouchOutside(true);

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivAddUser:
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);

                break;
            case R.id.tvAddBirthday:
                DialogFragment datePickerFragment = new DatePickerFragment() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {

                        final Calendar c = Calendar.getInstance();
                        year = c.get(Calendar.YEAR);
                        month = c.get(Calendar.MONTH)+ ONE; // behavior of output of onDateSet for month starts from 0
                        day = c.get(Calendar.DAY_OF_MONTH);

                        LocalDate birthday = new LocalDate(year, month, day);
                        String formattedDate = birthday.toString("MMMM dd, yyyy");
                        tvAddBirthday.setText(formattedDate);
                        tvAddBirthday.setTypeface(Typeface.DEFAULT_BOLD);
                        tvAddAge.setText("Age: " + Utility.getAge(birthday) + " yrs. old");
                        tvAddNextBirthday.setText("Days till next Birthday: " +
                                Utility.getNextBirthday(birthday) + " days");
                    }
                };
                datePickerFragment.show(AddUserActivity.this.getSupportFragmentManager(), "datePicker");
                break;
            case R.id.rbGenderFemale | R.id.rbGenderMale:
                if(rgGender.getCheckedRadioButtonId()!=-ONE){
                    int id= rgGender.getCheckedRadioButtonId();
                    View radioButton = rgGender.findViewById(id);
                    int radioId = rgGender.indexOfChild(radioButton);
                    RadioButton btn = (RadioButton) rgGender.getChildAt(radioId);
                    radioValue = (String) btn.getText();
                }
                user.setSex(radioValue);
                Toast.makeText(this, "Sex: "+ radioValue, Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnAddUser:
                if(!validate()){
                    Toast.makeText(getBaseContext(), "Enter some data!", Toast.LENGTH_LONG).show();
                }
                else {
                    addImageToDB(addedImage);
                    //Note: "http://hmkcode.appspot.com/jsonservlet" is a test web service I found and used as a dummy in hmkcode.appspot.com
                    //JSON key values for this are: [{"name":null, "country":null, "twitter":null}] See line 245 start for dummy data
                    new HttpAsyncTask().execute("http://hmkcode.appspot.com/jsonservlet");
                    Toast.makeText(this, "User added to database!", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
                break;


        }

    }



    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_PIC_REQUEST) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            ivAddUser.setImageBitmap(image);
            addedImage = image;

        }
    }

    private void addImageToDB(Bitmap image) {
        if(image != null) {
            DbHelper.open();
            DbHelper.insertUserDetails(image);
            DbHelper.close();
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
            }
        });
        dialog.create().show();


    }





    public static String POST(String url, Users user) {
        InputStream inputStream;
        String result = "";
        try {

            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);
            String json;

            // build jsonObject
            JSONObject jsonObject = new JSONObject();

            //When webservice is available use this instead
/*          jsonObject.accumulate("firstname", user.getFirstName());
            jsonObject.accumulate("lasttname", user.getLastName());
            jsonObject.accumulate("birthday", user.getBirthday());
            jsonObject.accumulate("sex", user.getSex());*/

              /* This is only dummy data as reference from  "http://hmkcode.appspot.com/jsonservlet" dummy webservice i found
              * in the internet */
            jsonObject.accumulate("name", user.getFirstName());
            jsonObject.accumulate("country", user.getLastName());
            jsonObject.accumulate("twitter", user.getBirthday());

            // convert JSONObject to JSON to String
            json = jsonObject.toString();

            // set json to StringEntity
            StringEntity se = new StringEntity(json);
            // set httpPost Entity
            httpPost.setEntity(se);

            // Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);
            int status = httpResponse.getStatusLine().getStatusCode();
            if(status == RESPONSE_OK) {

                // receive response as inputStream
                inputStream = httpResponse.getEntity().getContent();

                // convert inputstream to string
                if (inputStream != null)
                    result = convertInputStreamToString(inputStream);
                else
                    result = "Did not work!";
            }
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            user = new Users();
            user.setFirstName(etAddFirstName.getText().toString());
            user.setLastName(etAddLastName.getText().toString());
            user.setBirthday(tvAddBirthday.getText().toString());

            return POST(urls[0],user);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "Data Sent!", Toast.LENGTH_LONG).show();
        }
    }


    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line;
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    private boolean validate(){
        if(etAddFirstName.getText().toString().trim().equals("")) {
            return false;
        }
        else if(etAddLastName.getText().toString().trim().equals("")) {
            return false;
        }
        else if(rbGenderMale.isSelected() || rbGenderFemale.isSelected()) {
            return false;
        }
        else if(tvAddBirthday.getText().toString().equals("Click this text to set")) {
            return false;
        }
        else if(addedImage == null){
            return false;
        }
        else{
            return true;
        }

    }
}
