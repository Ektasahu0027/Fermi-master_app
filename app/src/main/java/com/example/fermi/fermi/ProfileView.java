package com.example.fermi.fermi;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProfileView extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {

    @Bind(R.id.toolbar_header_view)
    protected HeaderView toolbarHeaderView;

    @Bind(R.id.float_header_view)
    protected HeaderView floatHeaderView;

    @Bind(R.id.appbar)
    protected AppBarLayout appBarLayout;

    FirebaseUser user;

    @Bind(R.id.toolbar)
    protected Toolbar toolbar;

    private boolean isHideToolbarView = false;
     ImageView profileview;
    ImageView editbutton;
    String qual;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_view);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editbutton=(ImageView)findViewById(R.id.edit_button);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              ProfileView.this.finish();
            }
        });
        profileview=(ImageView)findViewById(R.id.profileName);
        user = FirebaseAuth.getInstance().getCurrentUser();

        View view = findViewById(R.id.information_layout);
        TextView email = (TextView) view.findViewById(R.id.profielemail);
        final TextView qualification = (TextView) view.findViewById(R.id.profile_qualification);
        email.setText(user.getEmail());

        FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
              // Qualification mUser = dataSnapshot.getValue(Qualification.class);
                try {
                    qual = (String) dataSnapshot.child("qualification").getValue();

                        if (qual.equals(""))
                        {

                        }
                        else
                            qualification.setText(qual);

                }
                catch (Exception e)
                {

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        editbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileView.this, UpdateProfileActivity.class));
                ProfileView.this.finish();
            }
        });

        if(user != null && !user.getDisplayName().equals("") && user.getPhotoUrl() != null) {
            Log.i("MAINACTIVITY", "Setting name and photo");
            initUi();
            DownloadImagesTask task = new DownloadImagesTask();
            task.execute(user.getPhotoUrl().toString());
        } else {
            initUi();
            profileview.setImageResource(R.drawable.fermi);
        }
    }
    private void initUi() {
        appBarLayout.addOnOffsetChangedListener(this);
        toolbarHeaderView.bindTo(user.getDisplayName());
        floatHeaderView.bindTo(user.getDisplayName());
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        if (percentage == 1f && isHideToolbarView) {
            toolbarHeaderView.setVisibility(View.VISIBLE);
            isHideToolbarView = !isHideToolbarView;

        } else if (percentage < 1f && !isHideToolbarView) {
            toolbarHeaderView.setVisibility(View.GONE);
            isHideToolbarView = !isHideToolbarView;
        }
    }

    public class DownloadImagesTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {
            return download_Image(urls[0]);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            profileview.setImageBitmap(result);        }

        private Bitmap download_Image(String url) {

            Bitmap bmp =null;
            try{
                URL ulrn = new URL(url);
                HttpURLConnection con = (HttpURLConnection)ulrn.openConnection();
                InputStream is = con.getInputStream();
                bmp = BitmapFactory.decodeStream(is);
                if (null != bmp)
                    return bmp;

            }catch(Exception e){}
            return bmp;
        }
    }
}
