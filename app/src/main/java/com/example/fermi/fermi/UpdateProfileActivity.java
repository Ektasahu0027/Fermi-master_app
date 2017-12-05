package com.example.fermi.fermi;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class UpdateProfileActivity extends AppCompatActivity {

    private static final int READ_REQUEST_CODE = 42;
    ImageView image;
    TextView text;
    EditText name,qualification;
    TextView next;
    FirebaseUser user;
    boolean isImageSet = false;
    Uri uri = null;
    ImageButton image1;
    Uri downloadUrl = null;
    ProgressBar progressBar;
    DatabaseReference mDatabase;
    User mUser;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        image = (ImageView) findViewById(R.id.addPhoto);
        image1=(ImageButton)findViewById(R.id.addPhoto1);
        text = (TextView) findViewById(R.id.addPhotoText);
        name = (EditText) findViewById(R.id.get_name_input);
        qualification = (EditText) findViewById(R.id.get_qualification);
        next = (TextView) findViewById(R.id.button_next);

        user = FirebaseAuth.getInstance().getCurrentUser();

        progressBar = (ProgressBar) findViewById(R.id.progress_bar_get_profile);

        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performFileSearch();
            }
        });
        if (user != null) {
            if (user.getPhotoUrl() == null) {
                Log.i("GetProfile", "Photo does not exists returned =" + user.getPhotoUrl());
                // Toast.makeText(this, "Select a photo", Toast.LENGTH_SHORT).show();
               /* image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        performFileSearch();
                    }
                });*/
            } else {
                Log.i("GetProfile", "Photo exists returned =" + user.getPhotoUrl());
                DownloadImagesTask2 task2 = new DownloadImagesTask2();
                uri = user.getPhotoUrl();
                task2.execute(user.getPhotoUrl().toString());
                isImageSet = true;
                text.setVisibility(View.GONE);
            }
            if (user.getDisplayName() != null) {
                name.setText(user.getDisplayName());
            }

            FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Qualification mUser = dataSnapshot.getValue(Qualification.class);
                    try {
                      String  qual = (String) dataSnapshot.child("qualification").getValue();

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
        }

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user != null) {
                   /* if (!isImageSet) {
                        Log.i("GetProfile", "Photo does not exists returned =" + user.getPhotoUrl());
                        Toast.makeText(GetProfileActivity.this, "Select a photo", Toast.LENGTH_SHORT).show();
                    } else*/
                    if (name.getText().toString().trim().equals("")) {
                        name.setError("Name cannot be empty");
                    }
                    else {
                        text.setText("Please wait, Updating Profile...");
                        progressBar.setVisibility(View.VISIBLE);
                        progressBar.setEnabled(true);
                        uploadProfileImage();
                    }
                }
            }
        });
    }

    public void uploadProfileImage() {
        Log.i("UPLOAD IMAGE", "UPLOADING IMAGE NOW");
        ContentResolver contentResolver = UpdateProfileActivity.this.getContentResolver();
        if (uri != null) {
            StorageMetadata storageMetadata = new StorageMetadata.Builder()
                    .setContentType(contentResolver.getType(uri))
                    .build();
        }
        try {
            if (uri != null && !uri.toString().contains("https://")) {
                UploadTask task = FirebaseStorage.getInstance().getReference()
                        .child("users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(UUID.randomUUID().toString())
                        .putStream(contentResolver.openInputStream(uri));
                task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        downloadUrl = taskSnapshot.getDownloadUrl();
                        Log.i("IMAGE UPLOADED", "IMAGE AT " + downloadUrl.toString());
                        if (!downloadUrl.toString().equals("")) {
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name.getText().toString().trim())
                                    .setPhotoUri(downloadUrl)
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                progressBar.setEnabled(false);
                                                progressBar.setVisibility(View.GONE);

                                                mDatabase.child("users").child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        mUser = dataSnapshot.getValue(User.class);
                                                        mDatabase.child("users").child(user.getUid()).child("profile").setValue(user.getPhotoUrl().toString());
                                                        mDatabase.child("users").child(user.getUid()).child("name").setValue(name.getText().toString().trim());
                                                        if (qualification.getText().toString().trim().equals("")) {
                                                            mDatabase.child("users").child(user.getUid()).child("Qualification").setValue("not avalible plz update");
                                                        }
                                                        else
                                                        mDatabase.child("users").child(user.getUid()).child("Qualification").setValue(qualification.getText().toString());
                                                   /*     if (mUser.isHasAnswered()) {
                                                            startActivity(new Intent(UpdateProfileActivity.this, MainActivity.class));
                                                        } else {
                                                            startActivity(new Intent(UpdateProfileActivity.this, GetSubjectsActivity.class));
                                                        }
                                                        Log.d("UpdateProfile", "User profile updated.");
                                                        UpdateProfileActivity.this.finish();*/
                                                        startActivity(new Intent(UpdateProfileActivity.this, ProfileView.class));
                                                        UpdateProfileActivity.this.finish();
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });
                                            }
                                        }
                                    });
                        }
                    }
                });
                task.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UpdateProfileActivity.this, "Image Upload Failed! Try Again Later", Toast.LENGTH_SHORT);
                    }
                });
            } else {
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(name.getText().toString().trim())
                        .build();

               // mUser.setProfile(downloadUrl.toString());
                user.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    progressBar.setEnabled(false);
                                    progressBar.setVisibility(View.GONE);

                                    mDatabase.child("users").child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            mUser = dataSnapshot.getValue(User.class);
                                            mDatabase.child("users").child(user.getUid()).child("name").setValue(name.getText().toString().trim());
                                            if (qualification.getText().toString().trim().equals("")) {
                                                mDatabase.child("users").child(user.getUid()).child("qualification").setValue("not avalible plz update");
                                            }
                                            else
                                                mDatabase.child("users").child(user.getUid()).child("qualification").setValue(qualification.getText().toString());
                                          /*  if (mUser.isHasAnswered()) {
                                                startActivity(new Intent(UpdateProfileActivity.this, MainActivity.class));
                                            } else {
                                                startActivity(new Intent(UpdateProfileActivity.this, GetSubjectsActivity.class));
                                            }
                                            Log.d("UpdateProfile", "User profile updated.");
                                            */
                                            startActivity(new Intent(UpdateProfileActivity.this, ProfileView.class));
                                            UpdateProfileActivity.this.finish();
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }
                        });
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Fires an intent to spin up the "file chooser" UI and select an image.
     */
    public void performFileSearch() {

        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.setType("image/*");

        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            if (resultData != null) {
                uri = resultData.getData();
                Log.i("ImageSet", "Uri: " + uri.toString());
                image.setImageURI(uri);
                text.setVisibility(View.VISIBLE);
                text.setText("This is how you will appear!");
                isImageSet = true;
            }
        }
    }

    public class DownloadImagesTask2 extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {
            return download_Image(urls[0]);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            image.setImageBitmap(result);
        }

        private Bitmap download_Image(String url) {

            Bitmap bmp = null;
            try {
                URL ulrn = new URL(url);
                HttpURLConnection con = (HttpURLConnection) ulrn.openConnection();
                InputStream is = con.getInputStream();
                bmp = BitmapFactory.decodeStream(is);
                if (null != bmp)
                    return bmp;

            } catch (Exception e) {
            }
            return bmp;
        }
    }
}