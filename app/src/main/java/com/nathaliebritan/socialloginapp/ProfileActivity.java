package com.nathaliebritan.socialloginapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.facebook.login.LoginManager;
import com.facebook.login.widget.ProfilePictureView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class ProfileActivity extends AppCompatActivity {
    private Button btnLogOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        btnLogOut = (Button) findViewById(R.id.btn_log_out);

        if(MainActivity.mSharedPreferences.getString(MainActivity.SOCIAL_NET,"") == "Facebook") {
            ProfilePictureView mProfilePicture = (ProfilePictureView) findViewById(R.id.img_facebook_profile_picture);
            mProfilePicture.setProfileId(MainActivity.mSharedPreferences.getString(MainActivity.USER_ID, ""));
        }
        else{
            new LoadProfileImage().execute(MainActivity.mSharedPreferences.getString(MainActivity.PROFILE_PICTURE, ""));
        }

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = MainActivity.mSharedPreferences.edit();
                editor.putBoolean(MainActivity.IS_SIGNIN, false);
                editor.commit();
                LoginManager.getInstance().logOut();
                finish();
            }
        });
    }

    private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap bitmap = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            ImageView mProfilePicture = (ImageView) findViewById(R.id.img_google_profile_picture);
            mProfilePicture.setImageBitmap(result);
        }
    }
}
