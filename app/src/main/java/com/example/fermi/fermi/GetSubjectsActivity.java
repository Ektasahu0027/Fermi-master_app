package com.example.fermi.fermi;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GetSubjectsActivity extends AppCompatActivity {

    FirebaseUser user;
    DatabaseReference mDatabase;
    GradientDrawable drawable;
    User mUser;
    RelativeLayout rootView;
    ProgressBar progressBar;
    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_subjects);

        final TextView askSubjectsText = (TextView) findViewById(R.id.askSubjectsText);
        final GridLayout gridLayout = (GridLayout) findViewById(R.id.subjectsGroup);
        RadioButton yesButton = (RadioButton) findViewById(R.id.yesRadioButton);
        RadioButton noButton = (RadioButton) findViewById(R.id.noRadioButton);
        final Button nextButton = (Button) findViewById(R.id.BtnSubjectsNext);

        rootView = (RelativeLayout) findViewById(R.id.rootGetSubjects);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar_get_subjects);

        rootView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);


        CheckBox mathsCheckbox = (CheckBox) findViewById(R.id.mathsCheckbox);
        CheckBox physicsCheckbox = (CheckBox) findViewById(R.id.physicsCheckbox);
        CheckBox chemistryCheckbox = (CheckBox) findViewById(R.id.chemistryCheckbox);
        CheckBox biologyCheckbox = (CheckBox) findViewById(R.id.biologyCheckbox);
        CheckBox artCheckbox = (CheckBox) findViewById(R.id.artCheckbox);
        CheckBox healthCheckbox = (CheckBox) findViewById(R.id.healthCheckbox);
        CheckBox fitnessCheckbox = (CheckBox) findViewById(R.id.fitnessCheckbox);


        nextButton.setEnabled(false);
        drawable = (GradientDrawable) nextButton.getBackground();
        drawable.setStroke(4, getResources().getColor(R.color.colorPrimaryLight));
        nextButton.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimaryLight, null));

        user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUser = dataSnapshot.getValue(User.class);
                progressBar.setVisibility(View.INVISIBLE);
                rootView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askSubjectsText.setVisibility(View.VISIBLE);
                gridLayout.setVisibility(View.VISIBLE);
                mUser.setHelper(true);

                if (counter == 0) {
                    nextButton.setEnabled(false);
                    drawable.setStroke(4, getResources().getColor(R.color.colorPrimaryLight));
                    nextButton.setBackground(getResources().getDrawable(R.drawable.round_shape_btn));
                    nextButton.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimaryLight, null));
                } else {
                    nextButton.setEnabled(true);
                    nextButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    nextButton.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null));
                }
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askSubjectsText.setVisibility(View.INVISIBLE);
                gridLayout.setVisibility(View.INVISIBLE);
                mUser = new User();
                mUser.setHelper(false);
                mUser.setHasAnswered(true);
                mUser.setName(user.getDisplayName());
                mUser.setEmail(user.getEmail());
                mUser.setProfile(user.getPhotoUrl().toString());
                mUser.setUdid(user.getUid());
                nextButton.setEnabled(true);
                nextButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                nextButton.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null));
            }
        });

        /* -----Checkboxes are made functional below----- */

        mathsCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    counter++;
                    mUser.setMaths(true);
                } else {
                    counter--;
                    mUser.setMaths(false);
                }
                if (counter == 0) {
                    nextButton.setEnabled(false);
                    drawable.setStroke(4, getResources().getColor(R.color.colorPrimaryLight));
                    nextButton.setBackground(getResources().getDrawable(R.drawable.round_shape_btn));
                    nextButton.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimaryLight, null));
                } else {
                    nextButton.setEnabled(true);
                    nextButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    nextButton.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null));
                }
            }
        });

        physicsCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    counter++;
                    mUser.setPhysics(true);
                } else {
                    counter--;
                    mUser.setPhysics(false);
                }
                if (counter == 0) {
                    nextButton.setEnabled(false);
                    drawable.setStroke(4, getResources().getColor(R.color.colorPrimaryLight));
                    nextButton.setBackground(getResources().getDrawable(R.drawable.round_shape_btn));
                    nextButton.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimaryLight, null));
                } else {
                    nextButton.setEnabled(true);
                    nextButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    nextButton.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null));
                }
            }
        });

        chemistryCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    counter++;
                    mUser.setChemistry(true);
                } else {
                    counter--;
                    mUser.setChemistry(false);
                }
                if (counter == 0) {
                    nextButton.setEnabled(false);
                    drawable.setStroke(4, getResources().getColor(R.color.colorPrimaryLight));
                    nextButton.setBackground(getResources().getDrawable(R.drawable.round_shape_btn));
                    nextButton.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimaryLight, null));
                } else {
                    nextButton.setEnabled(true);
                    nextButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    nextButton.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null));
                }
            }
        });

        biologyCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    counter++;
                    mUser.setBiology(true);
                } else {
                    counter--;
                    mUser.setBiology(false);
                }
                if (counter == 0) {
                    nextButton.setEnabled(false);
                    drawable.setStroke(4, getResources().getColor(R.color.colorPrimaryLight));
                    nextButton.setBackground(getResources().getDrawable(R.drawable.round_shape_btn));
                    nextButton.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimaryLight, null));
                } else {
                    nextButton.setEnabled(true);
                    nextButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    nextButton.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null));
                }
            }
        });

        artCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    counter++;
                    mUser.setArts(true);
                } else {
                    counter--;
                    mUser.setArts(false);
                }
                if (counter == 0) {
                    nextButton.setEnabled(false);
                    drawable.setStroke(4, getResources().getColor(R.color.colorPrimaryLight));
                    nextButton.setBackground(getResources().getDrawable(R.drawable.round_shape_btn));
                    nextButton.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimaryLight, null));
                } else {
                    nextButton.setEnabled(true);
                    nextButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    nextButton.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null));
                }
            }
        });

        healthCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    counter++;
                    mUser.setHealth(true);
                } else {
                    counter--;
                    mUser.setHealth(false);
                }
                if (counter == 0) {
                    nextButton.setEnabled(false);
                    drawable.setStroke(4, getResources().getColor(R.color.colorPrimaryLight));
                    nextButton.setBackground(getResources().getDrawable(R.drawable.round_shape_btn));
                    nextButton.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimaryLight, null));
                } else {
                    nextButton.setEnabled(true);
                    nextButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    nextButton.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null));
                }
            }
        });

        fitnessCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    counter++;
                    mUser.setFitness(true);
                } else {
                    counter--;
                    mUser.setFitness(false);
                }
                if (counter == 0) {
                    nextButton.setEnabled(false);
                    drawable.setStroke(4, getResources().getColor(R.color.colorPrimaryLight));
                    nextButton.setBackground(getResources().getDrawable(R.drawable.round_shape_btn));
                    nextButton.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimaryLight, null));
                } else {
                    nextButton.setEnabled(true);
                    nextButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    nextButton.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null));
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUser.setHasAnswered(true);
                mDatabase.child("users").child(user.getUid()).setValue(mUser);

                startActivity(new Intent(GetSubjectsActivity.this, MainActivity.class));
                GetSubjectsActivity.this.finish();
            }
        });

    }
}
