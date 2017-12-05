package com.example.fermi.fermi;

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

public class UpdateSubjectsActivity extends AppCompatActivity {

    FirebaseUser user;
    DatabaseReference mDatabase;
    GradientDrawable drawable;
    User mUser;
    boolean arts,maths,che,phy,health,fitness,bio,lastmess;
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

      //  yesButton.setChecked(true);
        final Button nextButton = (Button) findViewById(R.id.BtnSubjectsNext);

        rootView = (RelativeLayout) findViewById(R.id.rootGetSubjects);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar_get_subjects);

        rootView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);


        final CheckBox mathsCheckbox = (CheckBox) findViewById(R.id.mathsCheckbox);
        final CheckBox physicsCheckbox = (CheckBox) findViewById(R.id.physicsCheckbox);
        final CheckBox chemistryCheckbox = (CheckBox) findViewById(R.id.chemistryCheckbox);
        final CheckBox biologyCheckbox = (CheckBox) findViewById(R.id.biologyCheckbox);
        final CheckBox artCheckbox = (CheckBox) findViewById(R.id.artCheckbox);
        final CheckBox healthCheckbox = (CheckBox) findViewById(R.id.healthCheckbox);
        final CheckBox fitnessCheckbox = (CheckBox) findViewById(R.id.fitnessCheckbox);


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
                arts = (boolean) dataSnapshot.child("arts").getValue();
                bio = (boolean) dataSnapshot.child("biology").getValue();
                che = (boolean) dataSnapshot.child("chemistry").getValue();
                health = (boolean) dataSnapshot.child("health").getValue();
                fitness = (boolean) dataSnapshot.child("fitness").getValue();
                maths = (boolean) dataSnapshot.child("maths").getValue();
                phy = (boolean) dataSnapshot.child("physics").getValue();
                artCheckbox.setChecked(arts);
                mathsCheckbox.setChecked(maths);
                physicsCheckbox.setChecked(phy);
                chemistryCheckbox.setChecked(che);
                biologyCheckbox.setChecked(bio);
                healthCheckbox.setChecked(health);
                fitnessCheckbox.setChecked(fitness);
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
               // mUser.setHelper(true);

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

                mDatabase.child("users").child(user.getUid()).child("arts").setValue(false);
                mDatabase.child("users").child(user.getUid()).child("biology").setValue(false);
                mDatabase.child("users").child(user.getUid()).child("chemistry").setValue(false);
                mDatabase.child("users").child(user.getUid()).child("health").setValue(false);
                mDatabase.child("users").child(user.getUid()).child("fitness").setValue(false);
                mDatabase.child("users").child(user.getUid()).child("maths").setValue(false);
                mDatabase.child("users").child(user.getUid()).child("physics").setValue(false);

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
                    maths=true;
                 //   mUser.setMaths(true);
                } else {
                    counter--;
                    maths=false;
                 //   mUser.setMaths(false);
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
                    phy=true;
                  //  mUser.setPhysics(true);
                } else {
                    counter--;
                    phy=false;
                  //  mUser.setPhysics(false);
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
                    che=true;
                  //  mUser.setChemistry(true);
                } else {
                    counter--;
                    che=false;
                   // mUser.setChemistry(false);
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
                    bio=true;
                  //  mUser.setBiology(true);
                } else {
                    counter--;
                    bio=false;
                  //  mUser.setBiology(false);
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
                    arts=true;
                  //  mUser.setArts(true);
                } else {
                    counter--;
                    arts=false;
                   // mUser.setArts(false);
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
                    health=true;
                 //   mUser.setHealth(true);
                } else {
                    counter--;
                    health=false;
                  //  mUser.setHealth(false);
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
                    fitness=true;
                  //  mUser.setFitness(true);
                } else {
                    counter--;
                    fitness=false;
                   // mUser.setFitness(false);
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
                //mUser.setHasAnswered(true);
                //mDatabase.child("users").child(user.getUid()).child("Personal Information").setValue(mUser);
               // Toast.makeText(getApplicationContext(),"ghhhfhg  "+lastmess,Toast.LENGTH_LONG).show();
                mDatabase.child("users").child(user.getUid()).child("arts").setValue(arts);
                mDatabase.child("users").child(user.getUid()).child("biology").setValue(bio);
                mDatabase.child("users").child(user.getUid()).child("chemistry").setValue(che);
                mDatabase.child("users").child(user.getUid()).child("health").setValue(health);
                mDatabase.child("users").child(user.getUid()).child("fitness").setValue(fitness);
                mDatabase.child("users").child(user.getUid()).child("maths").setValue(maths);
                mDatabase.child("users").child(user.getUid()).child("physics").setValue(phy);
                UpdateSubjectsActivity.this.finish();
            }
        });

    }
}
