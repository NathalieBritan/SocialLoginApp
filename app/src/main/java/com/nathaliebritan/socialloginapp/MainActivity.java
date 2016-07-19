package com.nathaliebritan.socialloginapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;


public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    private LoginButton btnFacebookLogin;
    private SignInButton btnGoogleSignin;
    private CallbackManager mFacebookCallbackManager;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        setContentView(R.layout.activity_main);
        mFacebookCallbackManager = CallbackManager.Factory.create();
        mGoogleApiClient = new GoogleApiClient.Builder(this).build();
        btnFacebookLogin = (LoginButton) findViewById(R.id.btnFacebookLogin);
        btnGoogleSignin = (SignInButton) findViewById(R.id.btnGoogleLogin);
        btnGoogleSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });
        btnFacebookLogin.registerCallback(mFacebookCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        getFacebookProfilePicture(loginResult.getAccessToken().getUserId());
                    }

                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d("Error", error.getMessage());
                    }
                });
    }

    public void getFacebookProfilePicture(String userID){
        ProfilePictureView profilePictureView;
        profilePictureView = (ProfilePictureView) findViewById(R.id.viewProfilePicture);
        profilePictureView.setProfileId(userID);
        profilePictureView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()) {
                final GoogleApiClient client = mGoogleApiClient;
            }
            else {
                mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    private void signInWithGoogle() {
        if(mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();
        final Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

}
