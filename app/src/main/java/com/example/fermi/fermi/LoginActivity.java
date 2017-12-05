package com.example.fermi.fermi;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;
    GradientDrawable drawable;
    DatabaseReference mDatabase;
    ProgressBar progressBar;
    LinearLayout rootView;
    FirebaseUser user;
    AlertDialog alertDialog;
    private EditText emailInput, passwordInput;
    Button fbButton;
    private Button loginBtn;
    private TextView signupText1, signupText2, resetPassword;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //PrintHashKey();
        progressBar = (ProgressBar) findViewById(R.id.loginProgressBar);
        rootView = (LinearLayout) findViewById(R.id.rootLogin);

//        Log.d("SIGNUP ACTIVITY 2", "createUserWithEmail:success"+mDatabase.child("users").child(auth.getCurrentUser().getUid()));
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.fermi.fermi",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
               // Toast.makeText(getApplicationContext(), Base64.encodeToString(md.digest(), Base64.DEFAULT),Toast.LENGTH_LONG).show();
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        mDatabase = FirebaseDatabase.getInstance().getReference();
        alertDialog = new AlertDialog.Builder(
                LoginActivity.this).create();
        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        try{
            if (user != null && !user.getDisplayName().equals("")) {

                rootView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);

                mDatabase.child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User mUser = dataSnapshot.getValue(User.class);
                        if (mUser.isHasAnswered()) {
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            LoginActivity.this.finish();
                        } else {
                            startActivity(new Intent(LoginActivity.this, GetSubjectsActivity.class));
                            LoginActivity.this.finish();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }catch (Exception e){

        }


        emailInput = (EditText) findViewById(R.id.emailInput);
        passwordInput = (EditText) findViewById(R.id.passwordInput);
        fbButton = (Button) findViewById(R.id.fbLogin);
        loginBtn = (Button) findViewById(R.id.loginbtn);
        signupText1 = (TextView) findViewById(R.id.signupText1);
        signupText2 = (TextView) findViewById(R.id.signupText2);
        resetPassword = (TextView) findViewById(R.id.resetPassword);

        loginBtn.setEnabled(false);
        drawable = (GradientDrawable) loginBtn.getBackground();
        drawable.setStroke(4, getResources().getColor(R.color.colorPrimaryLight));
        loginBtn.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimaryLight, null));

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAllowNewEmailAccounts(false)
                                .setAvailableProviders(
                                        Arrays.asList(
                                                new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build()
                                        ))
                                .build(),
                        999);
            }
        });

        emailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().equals("")) {
                    loginBtn.setEnabled(false);
                    drawable.setStroke(4, getResources().getColor(R.color.colorPrimaryLight));
                    loginBtn.setBackground(getResources().getDrawable(R.drawable.round_shape_btn));
                    loginBtn.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimaryLight, null));
                } else {
                    loginBtn.setEnabled(true);
                    loginBtn.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    //drawable.setStroke(4, getResources().getColor(R.color.colorPrimaryDark));
                    loginBtn.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        passwordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().equals("")) {
                    if(!emailInput.getText().toString().trim().equals("")) {
                        loginBtn.setEnabled(true);
                        loginBtn.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                        //drawable.setStroke(4, getResources().getColor(R.color.colorPrimaryDark));
                        loginBtn.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null));
                    }
                } else {
                    loginBtn.setEnabled(false);
                    drawable.setStroke(4, getResources().getColor(R.color.colorPrimaryLight));
                    loginBtn.setBackground(getResources().getDrawable(R.drawable.round_shape_btn));
                    loginBtn.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimaryLight, null));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        fbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(
                                        Arrays.asList(
                                                new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()
                                        ))
                                .build(),
                        RC_SIGN_IN);
            }
        });


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (emailInput.getText().toString().trim().equals("")) {
                    emailInput.setError("Cannot be empty");
                } else if (passwordInput.getText().toString().trim().equals("")) {
                    passwordInput.setError("Cannot be empty");
                } else {
                    alertDialog.setMessage("please wait!!!");
                    alertDialog.show();

                    try{
                        auth.signInWithEmailAndPassword(emailInput.getText().toString().trim(), passwordInput.getText().toString().trim())
                                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(!task.isSuccessful()) {
                                            alertDialog.dismiss();
                                            if(passwordInput.getText().toString().trim().length() < 6) {
                                                passwordInput.setError(getString(R.string.minimum_password));
                                            } else {
                                                // Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                            }
                                        } else {
                                            alertDialog.dismiss();
                                            mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    User mUser = dataSnapshot.getValue(User.class);
                                                    rootView.setVisibility(View.GONE);
                                                    progressBar.setVisibility(View.VISIBLE);
                                                    if (mUser.isHasAnswered()) {
                                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                                    } else {
                                                        startActivity(new Intent(LoginActivity.this, GetSubjectsActivity.class));
                                                    }
                                                    LoginActivity.this.finish();
                                                }
                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                if( e instanceof FirebaseAuthInvalidUserException){
                                    AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
                                    alert.setTitle("Sign up");
                                    alert.setMessage("This User Not Found , Create A New Account");
                                    alert.setPositiveButton("OK", null);
                                    alert.show();

                                }
                                if( e instanceof FirebaseAuthInvalidCredentialsException){
                                    AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
                                    alert.setTitle("Login");
                                    alert.setMessage( "The Password Is Invalid, Please Try Valid Password");
                                    alert.setPositiveButton("OK", null);
                                    alert.show();

                                }
                                if(e instanceof FirebaseNetworkException){

                                    AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
                                    alert.setTitle("Login");
                                    alert.setMessage( "Please Check Your Connection");
                                    alert.setPositiveButton("OK", null);
                                    alert.show();
                                }
                            }
                        });
                    }catch (Exception e){
                        alertDialog.setMessage(e.getMessage());
                        alertDialog.show();
                    }


                }
            }
        });

        signupText1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
                finish();
            }
        });
        signupText2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
                finish();
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            // Successfully signed in
            if (resultCode == ResultCodes.OK) {
                rootView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);

                mDatabase.child("users").child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User mUser = dataSnapshot.getValue(User.class);
                        try {
                            if (mUser.isHasAnswered()) {
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                LoginActivity.this.finish();
                            } else {
                                startActivity(new Intent(LoginActivity.this, GetSubjectsActivity.class));
                                LoginActivity.this.finish();
                            }
                        }
                        catch (Exception e){
                            AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
                            alert.setTitle("Login");
                            alert.setMessage("This User Not Found , Create A New Account");
                            alert.setPositiveButton("OK", null);
                            alert.show();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                return;
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    Log.e("Login","Login canceled by User");
                    return;
                }
                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Log.e("Login","No Internet Connection");
                    return;
                }
                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Log.e("Login","Unknown Error");
                    return;
                }
            }
            Log.e("Login","Unknown sign in response");
        }
    }

  /*  private void PrintHashKey() {

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.fermi.fermi",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }*/

}