package com.example.fermi.fermi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.fermi.fermi.Chat.ChatActivitySearch;
import com.example.fermi.fermi.Fragment.ChatTabFragment;
import com.example.fermi.fermi.Fragment.ContactTabFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG ="ekta" ;
    CircleImageView photo;
    ListView allusers;
    static public RelativeLayout inviteview,nouser,usernotavalible;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private DrawerLayout mDrawerLayout;
    AlertDialog alertDialog;
    ProgressBar progressBar;
    private ActionBarDrawerToggle mDrawerToggle;
    static public MaterialSearchView materialSearchView;
    FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    ListingAdapter adapter;
    ArrayList<User> users = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        alertDialog = new AlertDialog.Builder(
                MainActivity.this).create();
        //isStoragePermissionGranted();
        photo = (CircleImageView) findViewById(R.id.profilePhoto);
        allusers = (ListView) findViewById(R.id.listView);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        inviteview = (RelativeLayout) findViewById(R.id.contact_search);
        nouser = (RelativeLayout) findViewById(R.id.no_search_user);
        progressBar=(ProgressBar)findViewById(R.id.progress_bar);
        usernotavalible = (RelativeLayout) findViewById(R.id.user_not_avalible);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Fermi");

       // getDataFromServer();

        materialSearchView=(MaterialSearchView)findViewById(R.id.search_view);


        allusers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                // selected item
                String selected = ((TextView) view.findViewById(R.id.name)).getText().toString();
                String uid = ((TextView) view.findViewById(R.id.uid_name)).getText().toString();
                String profile_pic=((TextView) view.findViewById(R.id.profile_url)).getText().toString();
                String email_id=((TextView) view.findViewById(R.id.number)).getText().toString();

                Intent myIntent = new Intent(getApplicationContext(), ChatActivitySearch.class);
                myIntent.putExtra("Username", selected );
                myIntent.putExtra("Uid", uid );
                myIntent.putExtra("image", profile_pic);
                myIntent.putExtra("Email", email_id);
                //myIntent.putExtra("Profile", (Parcelable) profile);
                startActivity(myIntent);
            }
        });

        materialSearchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                inviteview.setVisibility(View.VISIBLE);
                nouser.setVisibility(View.VISIBLE);
                usernotavalible.setVisibility(View.GONE);
            }

            @Override
            public void onSearchViewClosed() {
                inviteview.setVisibility(View.GONE);
               /* adapter = new ListingAdapter(getApplicationContext(), users);
                allusers.setAdapter(adapter);*/
            }
        });

        materialSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(final String query) {

                databaseReference.child("users").orderByChild("name").equalTo(query).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.getValue()==null){
                            databaseReference.child("users").orderByChild("email").equalTo(query).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    SearchTask st = new SearchTask();
                                    st.execute(dataSnapshot);

                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                  
                                }
                            });

                        }
                        else {
                            SearchTask st = new SearchTask();
                            st.execute(dataSnapshot);

                        }
                      
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }

                });
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                progressBar.setVisibility(View.VISIBLE);
              /*  progressBar.setVisibility(View.VISIBLE);
                databaseReference.child("users").orderByChild("name").equalTo(newText).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        SearchTask st = new SearchTask();
                        st.execute(dataSnapshot);

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // hideProgressDialog();
                      //  progressBar.setVisibility(View.GONE);
                    }
                });*/

                return true;

           
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        final ActionBar actionBar = getSupportActionBar();

        TextView signOut = (TextView) findViewById(R.id.signOut);
        LinearLayout settings = (LinearLayout) findViewById(R.id.settings_layout);
        LinearLayout profile = (LinearLayout) findViewById(R.id.profielview);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ProfileView.class));
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        });

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name) {

                public void onDrawerClosed(View view) {
                    supportInvalidateOptionsMenu();
                    //drawerOpened = false;
                }

                public void onDrawerOpened(View drawerView) {
                    supportInvalidateOptionsMenu();
                    //drawerOpened = true;
                }
            };
            mDrawerToggle.setDrawerIndicatorEnabled(false);
            mDrawerToggle.setHomeAsUpIndicator(R.drawable.menu);
            mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDrawerLayout.openDrawer(Gravity.LEFT);
                }
            });
            mDrawerToggle.syncState();

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if(user != null && !user.getDisplayName().equals("") && user.getPhotoUrl() != null) {
                Log.i("MAINACTIVITY", "Setting name and photo");
                TextView name = (TextView) findViewById(R.id.profileName);
                name.setText(user.getDisplayName());
                DownloadImagesTask task = new DownloadImagesTask();
                task.execute(user.getPhotoUrl().toString());

            } else {
                TextView name = (TextView) findViewById(R.id.profileName);
                name.setText(user.getDisplayName());
                photo.setImageResource(R.drawable.fermi);
            }
        }
    }
   /* public void getDataFromServer() {
        //showProgressDialog();
        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                users.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                        User user = postSnapShot.getValue(User.class);
                        try {
                            if (!user.getName().equals(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())){
                                users.add(user);
                                adapter.notifyDataSetChanged();
                                Collections.sort(users, new Comparator<User>() {
                                    @Override
                                    public int compare(User u1, User u2) {
                                        Log.d("compare",u1.getName()+""+u2.getName());
                                        return u1.getName().compareToIgnoreCase(u2.getName());
                                    }
                                });
                                Log.d("compare","123");
                            }
                            else {
                            }
                        }
                        catch (Exception e){
                        }
                    }
                }
                // hideProgressDialog();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // hideProgressDialog();
            }
        });
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_CONTACTS)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {
                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS, android.Manifest.permission.INTERNET}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }
*/
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
        }
        /*for (int i=0;i< permissions.length ;i++)
        {
            Log.d("Permission",permissions[i]+" result is:"+grantResults[i]);
        }*/
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item,menu);
        MenuItem item = menu.findItem(R.id.search_btn);
        materialSearchView.setMenuItem(item);
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ChatTabFragment(), "Chat");
        adapter.addFragment(new ContactTabFragment(), "Contacts");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public class DownloadImagesTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {
            return download_Image(urls[0]);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            photo.setImageBitmap(result);        }

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
    private class ListingAdapter extends BaseAdapter {
        Context context;
        LayoutInflater layoutInflater;
        ArrayList<User> users;
        FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
        Uri uri = null;
        ViewHolder holder;

        public ListingAdapter(Context con, ArrayList<User> users) {
            context = con;
            layoutInflater = LayoutInflater.from(context);
            this.users = users;
        }

        @Override
        public int getCount() {
            return users.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.contact_lv_item_cfragment1, null, false);
                holder = new ViewHolder();

                holder.fullname = (TextView) convertView.findViewById(R.id.name);
                holder.email = (TextView) convertView.findViewById(R.id.number);
                holder.profile=(CircleImageView)convertView.findViewById(R.id.image_view_contact_display);
                holder.uid_name=(TextView)convertView.findViewById(R.id.uid_name);
                holder.profileurl = (TextView) convertView.findViewById(R.id.profile_url);
                holder.invite=(TextView)convertView.findViewById(R.id.invitaion);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            try{
                User user = users.get(position);  //IndexOutOfBoundsException

                // if (!user.getName().equals(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())){
                holder.fullname.setText(user.getName());
                holder.email.setText(user.getEmail());
                holder.uid_name.setText(user.getUdid());
                holder.profileurl.setText(user.getProfile());
                Glide.with(getApplicationContext())
                        .load(user.getProfile())
                        .placeholder(R.drawable.profile)
                        .into(holder.profile);

            }catch (Exception e){

            }

            FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Person").child(holder.fullname.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String  lastmessa = (String) dataSnapshot.child("status").getValue();
                    Log.d("name",""+lastmessa);

                    Log.d("last",""+lastmessa);
                    try {
                        if (lastmessa == null){
                            holder.invite.setText("Invite");
                        }
                        else if (lastmessa.equals("Friend")){
                            holder.invite.setText("Friend");
                        }
                        else  if (lastmessa.equals("Invite")){
                            holder.invite.setText("REQUESTED");
                        }
                    }catch (Exception e){
                        Log.d("last4",""+lastmessa);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            return convertView;
        }

        public class ViewHolder {
            TextView fullname, email,uid_name,profileurl,invite;
            CircleImageView profile;
        }

        @Override
        public Object getItem(int position) {
            return users.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

    }

    private class SearchTask extends AsyncTask<DataSnapshot, Integer, Void> {
        ArrayList<User> users1 = new ArrayList<User>();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            alertDialog.setMessage("please wait!!!");
            alertDialog.show();
        }

        @Override
        protected Void doInBackground(DataSnapshot... params) {


            if (params[0].exists()) {
                for (DataSnapshot postSnapShot : params[0].getChildren()) {
                    User user = postSnapShot.getValue(User.class);
                    try {
                        if (!user.getName().equals(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())) {
                            users1.add(user);

                            Collections.sort(users1, new Comparator<User>() {
                                @Override
                                public int compare(User u1, User u2) {
                                    Log.d("compare", u1.getName() + "" + u2.getName());
                                    return u1.getName().compareToIgnoreCase(u2.getName());
                                }
                            });
                            Log.d("compare", "123");
                        } else {
                        }
                    } catch (Exception e) {
                    }
                }
            }
            // hideProgressDialog();




            return null;

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            alertDialog.dismiss();
            adapter = new ListingAdapter(getApplicationContext(), users1);
            allusers.setAdapter(adapter);
            allusers.setEmptyView(findViewById(R.id.user_not_avalible));
            nouser.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);

        }

    }
}
