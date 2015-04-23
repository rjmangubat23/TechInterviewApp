package com.techinterview.elementz.techinterviewapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.techinterview.elementz.helpers.UserListAdapter;
import com.techinterview.elementz.helpers.Users;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


    public class MainUserListActivity extends ActionBarActivity {

        private ListView mListViewUsers;
        private ArrayList<Users> mUserList;
        private ProgressDialog progressBar;

        private Users user;
        protected final int RESPONSE_OK= 200; //response from server when data is received

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user_list);

        initComponents();
        mListViewUsers.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long id)
            {

                // pass user object thru intent from clicked row position
                user = (Users)adapter.getItemAtPosition(position);
                Toast.makeText(MainUserListActivity.this, "Welcome, "+ user.getFirstName() + "!", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(MainUserListActivity.this, ViewUserActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });

    }

        @Override
        protected void onStart() {
            super.onStart();
            //Temporary static JSON host holder from webservice
            new UsersAsyncTask().execute("https://api.myjson.com/bins/4gcg5");

        }

        public void initComponents(){

        //add list view
        mListViewUsers = (ListView)findViewById(R.id.lvUsers);
        mUserList = new ArrayList<>();

        getSupportActionBar().setTitle("Main Screen");
        progressBar = new ProgressDialog(this);

        progressBar.setIndeterminate(true);
        progressBar.setMessage("please wait..");
        progressBar.setCancelable(true);
        progressBar.setCanceledOnTouchOutside(true);



    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_user_list, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //Add button in Actionbar for adding user
        if (id == R.id.action_add) {
            Intent intent = new Intent(this, AddUserActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public class UsersAsyncTask extends AsyncTask<String, Void, Boolean>{

        boolean result;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.show();
            mListViewUsers.invalidateViews();

        }

        @Override
        protected Boolean doInBackground(String... params) {

            try {
                HttpClient mClient = new DefaultHttpClient();
                HttpGet mGet = new HttpGet(params[0]);
                HttpResponse httpResponse = mClient.execute(mGet);
                int status = httpResponse.getStatusLine().getStatusCode();

                if(status == RESPONSE_OK){
                    BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().
                            getContent(), "UTF-8"));
                    String json = reader.readLine();

                    JSONObject jObj = new JSONObject(json);
                    JSONArray jArray = jObj.getJSONArray("users");

                    for(int i= 0; i<jArray.length(); i++){
                        user = new Users();
                        JSONObject jUserObject = jArray.getJSONObject(i);


                        user.setFirstName(jUserObject.getString("firstname"));
                        user.setLastName(jUserObject.getString("lastname"));
                        user.setBirthday(jUserObject.getString("birthday"));
                        user.setSex(jUserObject.getString("sex"));
                        user.setImage(jUserObject.getString("image"));

                        mUserList.add(user);

                    }
                    result = true;
                }
                else{
                    result = false;
                }

            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            progressBar.dismiss();

            if(!result){
                Toast.makeText(MainUserListActivity.this, "Ooops, Something went wrong!", Toast.LENGTH_LONG).show();
            }
            else{
                UserListAdapter mUserAdapter = new UserListAdapter(MainUserListActivity.this, mUserList);
                mListViewUsers.setAdapter(mUserAdapter);
                mUserAdapter.notifyDataSetChanged();


            }

        }
    }


}
