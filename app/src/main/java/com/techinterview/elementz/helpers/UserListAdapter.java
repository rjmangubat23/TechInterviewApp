package com.techinterview.elementz.helpers;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.techinterview.elementz.techinterviewapp.R;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Array Adapter for Users to populate List View
 */
public class UserListAdapter extends ArrayAdapter<Users>{

    protected ArrayList<Users> mArrayListUsers;
    protected Activity context;

    public UserListAdapter(Activity context, ArrayList<Users> objects) {
        super(context, R.layout.row_user, objects);
        mArrayListUsers = objects;
        this.context = context;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater mLayoutInflater;
        ViewHolder mHolder;
        View rowView = convertView;


        if(rowView == null){
           // mLayoutInflater = LayoutInflater.from(context);
            mLayoutInflater = context.getLayoutInflater();
            rowView = mLayoutInflater.inflate(R.layout.row_user, parent, false);

            // configure view holder
            mHolder = new ViewHolder();

            mHolder.ivImage = (ImageView) rowView.findViewById(R.id.ivImage);
            mHolder.tvName = (TextView) rowView.findViewById(R.id.tvName);
            mHolder.tvBirthday = (TextView) rowView.findViewById(R.id.tvBirthday);
            mHolder.tvSex = (TextView) rowView.findViewById(R.id.tvSex);

            rowView.setTag(mHolder);
        }else {
            mHolder = (ViewHolder) rowView.getTag();
        }
            //To display items onto list
            new DownloadImageTask(mHolder.ivImage).execute(mArrayListUsers.get(position).getImage());
            mHolder.tvName.setText(mArrayListUsers.get(position).getLastName() + ", " + mArrayListUsers.get(position).getFirstName());
            mHolder.tvBirthday.setText(mArrayListUsers.get(position).getBirthday());
            mHolder.tvSex.setText(mArrayListUsers.get(position).getSex());

        return rowView;
    }

     static class ViewHolder{
        public ImageView ivImage;
        public TextView tvName;
        public TextView tvBirthday;
        public TextView tvSex;

    }

    //AsyncTask to download link given from JSON for Image
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

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
        }
    }
}
