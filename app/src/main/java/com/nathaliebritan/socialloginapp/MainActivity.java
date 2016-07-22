package com.nathaliebritan.socialloginapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;


public class MainActivity extends AppCompatActivity  implements GoogleApiClient.OnConnectionFailedListener {

    public static final String PROFILE_PICTURE = "IMAGE_USER";
    public static final String IS_SIGNIN = "SIGNIN_BOOLEAN";
    public static final String SOCIAL_NET = "SOCIAL_NET";
    public static final String USER_ID = "USER_ID";
    public static SharedPreferences mSharedPreferences;

    public static  LoginButton btnFacebookLogin;
    public static SignInButton btnGoogleSignin;

    private static final int REQ_SELECT_PHOTO = 1;

    private Button btnShare;
    private Intent Profileintent;
    private CallbackManager mCallbackManager;
    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInOptions mGoogleSignInOptions;
    private static final int REQUEST_CODE = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_main);

        btnFacebookLogin = (LoginButton) findViewById(R.id.btn_facebook_login);
        btnGoogleSignin = (SignInButton) findViewById(R.id.btn_google_login);
        btnShare = (Button) findViewById(R.id.btn_share);

        btnGoogleSignin.setOnClickListener(onClickListener);
        btnFacebookLogin.setOnClickListener(onClickListener);
        btnShare.setOnClickListener(onClickListener);

        mGoogleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();
        mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, mGoogleSignInOptions).build();

        btnFacebookLogin.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = loginResult.getAccessToken();
                Profile profile = Profile.getCurrentProfile();
                SharedPreferences.Editor editor = MainActivity.mSharedPreferences.edit();
                editor.putBoolean(MainActivity.IS_SIGNIN, true);
                editor.putString(USER_ID,profile.getId());
                editor.putString(MainActivity.PROFILE_PICTURE, "http://graph.facebook.com/" + profile.getId() + "/picture?type=large");
                editor.putString(MainActivity.SOCIAL_NET, "Facebook");
                editor.commit();

                Profileintent = new Intent (getApplicationContext(), ProfileActivity.class);
                startActivity(Profileintent);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {

            }
        });

        mSharedPreferences = getPreferences(MODE_PRIVATE);
        if (mSharedPreferences.getBoolean(MainActivity.IS_SIGNIN, false)) {
            Profileintent = new Intent (getApplicationContext(), ProfileActivity.class);
            startActivity(Profileintent);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    View.OnClickListener onClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_google_login:
                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                    startActivityForResult(signInIntent, REQUEST_CODE);
                    break;
                case R.id.btn_share:
                    Intent photoPicker = new Intent(Intent.ACTION_PICK);
                    photoPicker.setType("video/*, image/*");
                    startActivityForResult(photoPicker, REQ_SELECT_PHOTO);
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQ_SELECT_PHOTO) {
            if(resultCode == RESULT_OK) {
                Uri selectedImage = data.getData();

                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/png");
                share.putExtra(Intent.EXTRA_STREAM, selectedImage);
                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(share, "Share image File"));
            }
        }

        if (!MainActivity.mSharedPreferences.getBoolean(MainActivity.IS_SIGNIN, false)) {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }

        if (requestCode == REQUEST_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            GoogleSignInAccount account = result.getSignInAccount();
            if (account != null) {
                SharedPreferences.Editor editor = MainActivity.mSharedPreferences.edit();
                editor.putBoolean(MainActivity.IS_SIGNIN, true);
                editor.putString(MainActivity.PROFILE_PICTURE, account.getPhotoUrl().toString());
                editor.putString(USER_ID, account.getId());
                editor.putString(MainActivity.SOCIAL_NET,"Google");
                editor.commit();

                Profileintent = new Intent (getApplicationContext(), ProfileActivity.class);
                startActivity(Profileintent);

            }
        }
    }
}

