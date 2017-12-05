package com.example.fermi.fermi;

import android.app.AlertDialog;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by znt on 9/26/17.
 */

public class ChangePasswordActivity extends AppCompatActivity {
    EditText password, repassword;
    Button Nextbtn;
    FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    AlertDialog alertDialog;
    GradientDrawable drawable;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        Toolbar toolbar = (Toolbar) findViewById(R.id.Toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangePasswordActivity.this.finish();
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        user= firebaseAuth.getCurrentUser();
        password = (EditText) findViewById(R.id.password_reset);
        repassword = (EditText) findViewById(R.id.repassword);
        Nextbtn = (Button) findViewById(R.id.nextButtonSignup1);

        alertDialog = new AlertDialog.Builder(
                ChangePasswordActivity.this).create();

        Nextbtn.setEnabled(false);
        drawable = (GradientDrawable) Nextbtn.getBackground();
        drawable.setStroke(4, getResources().getColor(R.color.colorPrimaryLight));


        password.addTextChangedListener(new TextWatcher() {
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

        repassword.addTextChangedListener(new TextWatcher() {
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
               if(password.getText().toString().trim().equals(repassword.getText().toString().trim())){

                   if ((password.getText().length() < 6)){
                       Toast.makeText(getApplicationContext(),"Password must be at least six characters long",Toast.LENGTH_LONG).show();

                   } else if(user!=null){
                       alertDialog.setMessage("Changing password, please wait!!!");
                       alertDialog.show();

                       user.updatePassword(password.getText().toString().trim())
                               .addOnCompleteListener(new OnCompleteListener<Void>() {
                                   @Override
                                   public void onComplete(@NonNull Task<Void> task) {
                                     if (task.isSuccessful()){
                                         alertDialog.dismiss();
                                         Toast.makeText(getApplicationContext(),"Your password has been changed",Toast.LENGTH_LONG).show();
                                         /*firebaseAuth.signOut();
                                         finish();
                                         Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
                                         startActivity(intent);*/
                                     }
                                     else {
                                         alertDialog.dismiss();
                                         Log.d("Exception",""+task.getException().getMessage());
                                         Toast.makeText(getApplicationContext(),"Password could not be changed",Toast.LENGTH_LONG).show();
                                     }
                                   }
                               });
                   }
                }
               else
                    Toast.makeText(getApplication(),"Password don't match",Toast.LENGTH_LONG).show();

            }
        });

    }
}

