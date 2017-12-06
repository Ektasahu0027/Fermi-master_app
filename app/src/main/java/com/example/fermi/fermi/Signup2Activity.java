package com.example.fermi.fermi;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Signup2Activity extends AppCompatActivity {

    EditText Password, FullName;
    Button Nextbtn;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String email,downloadUrl;
    DatabaseReference mDatabase;
    GradientDrawable drawable;
    private ProgressDialog progressDialog;
    AlertDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup2);

        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        Password = (EditText) findViewById(R.id.passwordInSignup2);
        FullName = (EditText) findViewById(R.id.nameInSignup2);
        Nextbtn = (Button) findViewById(R.id.nextButtonSignup2);


        Intent intent = getIntent();
        email = intent.getStringExtra("Email");
        downloadUrl = intent.getStringExtra("Profile");
      //  Toast.makeText(getApplicationContext(), " id is " + downloadUrl, Toast.LENGTH_LONG).show();

        progressDialog = new ProgressDialog(this);

        alertDialog = new AlertDialog.Builder(
                Signup2Activity.this).create();
        //Toast.makeText(getApplicationContext(), "Email id is " + email, Toast.LENGTH_LONG).show();
        Nextbtn.setEnabled(false);
        drawable = (GradientDrawable) Nextbtn.getBackground();
        drawable.setStroke(4, getResources().getColor(R.color.colorPrimaryLight));

        FullName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().equals("")) {
                    Nextbtn.setEnabled(false);
                    drawable.setStroke(4, getResources().getColor(R.color.colorPrimaryLight));
                    Nextbtn.setBackground(getResources().getDrawable(R.drawable.round_shape_btn));
                    Nextbtn.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimaryLight, null));
                } else {
                    Nextbtn.setEnabled(true);
                    Nextbtn.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    //drawable.setStroke(4, getResources().getColor(R.color.colorPrimaryDark));
                    Nextbtn.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        Password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().equals("")) {
                    Nextbtn.setEnabled(false);
                    drawable.setStroke(4, getResources().getColor(R.color.colorPrimaryLight));
                    Nextbtn.setBackground(getResources().getDrawable(R.drawable.round_shape_btn));
                    Nextbtn.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimaryLight, null));
                } else {
                    Nextbtn.setEnabled(true);
                    Nextbtn.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    //drawable.setStroke(4, getResources().getColor(R.color.colorPrimaryDark));
                    Nextbtn.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        Nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((Password.getText().length() < 6)) {
                    Password.setError("Password must be at least characters long");
                } else if (FullName.getText().toString().trim().equals("")) {
                    FullName.setError("Cannot be empty");
                } else {
                    progressDialog.setMessage("Registering Please Wait...");
                    progressDialog.show();

                    firebaseAuth.createUserWithEmailAndPassword(email, Password.getText().toString().trim())
                            .addOnCompleteListener(Signup2Activity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    //checking if success
                                    if(task.isSuccessful()){
                                        final User user =  new User();
                                        user.setName(FullName.getText().toString());
                                        user.setEmail(email);
                                        user.setUdid(firebaseAuth.getCurrentUser().getUid());
                                        user.setProfile(downloadUrl);

                                        UserProfileChangeRequest profileUpdates;

                                        if (downloadUrl != null && !downloadUrl.isEmpty()) {
                                            profileUpdates = new UserProfileChangeRequest.Builder()
                                                    .setDisplayName(user.getName().trim())
                                                    .setPhotoUri(Uri.parse(downloadUrl))
                                                    .build();
                                        }
                                        else {
                                            profileUpdates = new UserProfileChangeRequest.Builder()
                                                    .setDisplayName(user.getName().trim())
                                                    .build();
                                        }

                                        FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                mDatabase.child("users").child(firebaseAuth.getCurrentUser().getUid()).setValue(user);
                                                //  mDatabase.child("Message Number").setValue(new MessageNumber(0));
                                                progressDialog.dismiss();
                                                startActivity(new Intent(Signup2Activity.this, GetProfileActivity.class));
                                                Signup2Activity.this.finish();
                                            }
                                        });
                                    }else{

                                        String message = "The email address is already in use by another account.";

                                        if (task.getException().getMessage().equals(message)) {

                                            AlertDialog.Builder alert = new AlertDialog.Builder(Signup2Activity.this);
                                            alert.setTitle("Sign up");
                                            alert.setMessage(message);
                                            alert.setPositiveButton("OK", null);
                                            alert.show();

                                        } else {
                                            AlertDialog.Builder alert = new AlertDialog.Builder(Signup2Activity.this);
                                            alert.setTitle("Sign up");
                                            alert.setMessage("Email is invalid or password contains less than 6 symbols");
                                            alert.setPositiveButton("OK", null);
                                            alert.show();
                                        }
                                    }
                                    progressDialog.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof FirebaseNetworkException) {
                            AlertDialog.Builder alert = new AlertDialog.Builder(Signup2Activity.this);
                            alert.setTitle("Sign up");
                            alert.setMessage("Please Check Your Connection");
                            alert.setPositiveButton("OK", null);
                            alert.show();
                            //  Toast.makeText(LoginActivity.this, "Please Check Your Connection", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                }
            }
        });
    }

  /*  public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            alertDialog.dismiss();
            *//*Toast.makeText(MainActivity.this,"Internet connection available  ",
                    Toast.LENGTH_SHORT).show();*//*
            return true;
        }
        ;
       *//* Toast.makeText(MainActivity.this,"No Internet connection available  ",
                Toast.LENGTH_SHORT).show();*//*

        alertDialog.setTitle("Network");

        // Setting Dialog Message
        alertDialog.setMessage("No network connection available");

        // Setting OK Button
        alertDialog.setButton("Retry", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });

        // Showing Alert Message
        alertDialog.show();
        return false;
    }
*/
}
