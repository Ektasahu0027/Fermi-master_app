package com.example.fermi.fermi;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

public class Signup1Activity extends AppCompatActivity {
    String emailtextshow,personPhotoUrl;
    private static final int RC_SIGN_IN = 1;
    GradientDrawable drawable;
    private static final String TAG = "Ekta";
    EditText emailText;
    private GoogleApiClient mGoogleApiClient;
    Button sign;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup1);

        sign=(Button) findViewById(R.id.nextButtonSignup1);
        emailText=(EditText)findViewById(R.id.emailSignup1);
        // Configure Google Sign In


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                      //  Toast.makeText(getApplicationContext(),"you got an error",Toast.LENGTH_LONG).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        signIn();
        sign.setEnabled(false);
        drawable = (GradientDrawable) sign.getBackground();
        drawable.setStroke(4, getResources().getColor(R.color.colorPrimaryLight));

        emailText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().equals("")) {
                    sign.setEnabled(false);
                    drawable.setStroke(4, getResources().getColor(R.color.colorPrimaryLight));
                    sign.setBackground(getResources().getDrawable(R.drawable.round_shape_btn));
                    sign.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimaryLight, null));
                } else {
                    sign.setEnabled(true);
                    sign.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    //drawable.setStroke(4, getResources().getColor(R.color.colorPrimaryDark));
                    sign.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        sign.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimaryLight, null));

        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mainemail= emailText.getText().toString().trim();
                Intent myIntent = new Intent(Signup1Activity.this, Signup2Activity.class);
                myIntent.putExtra("Email", mainemail );
                myIntent.putExtra("Profile", personPhotoUrl );
                startActivity(myIntent);
                Signup1Activity.this.finish();
            }
        });
    }
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                String personName = account.getDisplayName();
                personPhotoUrl = account.getPhotoUrl().toString();
                emailtextshow = account.getEmail();
                String familyName = account.getFamilyName();

                Log.e(TAG, "Name: " + personName +
                        ", email: " + emailtextshow +
                        ", Image: " + personPhotoUrl +
                        ", Family Name: " + familyName);
               emailText.setText(emailtextshow);
                emailText.setTextColor(Color.parseColor("#000000"));
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
               // Toast.makeText(getApplicationContext(),"update UI appropriately",Toast.LENGTH_LONG).show();
            }
        }
    }


}
