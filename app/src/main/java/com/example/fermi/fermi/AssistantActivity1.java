package com.example.fermi.fermi;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.fermi.fermi.Chat.ChatActivitySearch;
import com.example.fermi.fermi.adapter.CustomMessageAdapter;
import com.example.fermi.fermi.adapter.MessageModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AssistantActivity1 extends AppCompatActivity {


    private  ListView messagelist,messagelist1;
    FirebaseUser user;
    private CustomMessageAdapter custommessageAdapter;
    private ArrayList<MessageModel> messageModelArrayList;
    LinearLayout bottombuttons;
    int i=0;
    TextView empty_message;
    RecyclerView cardView;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    ListingAdapter adapter;
    HorizontalScrollView  horizontalScrollView;
    ArrayList<User> users = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assistant1);
        MessageModel messageModel = new MessageModel();
        user = FirebaseAuth.getInstance().getCurrentUser();
        bottombuttons=(LinearLayout)findViewById(R.id.mLlayoutBottomButtons);
       // yesnobutton=(LinearLayout)findViewById(R.id.mLlayoutBottomyesnobtn);
        empty_message=(TextView)findViewById(R.id.empty_message);
        horizontalScrollView=(HorizontalScrollView)findViewById(R.id.list_itemscroll) ;

        Toolbar toolbar = (Toolbar) findViewById(R.id.AssistantToolbar);
        toolbar.setTitle("Fermi Assistant");
        //  toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimaryDark));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavUtils.navigateUpFromSameTask(AssistantActivity1.this);
            }
        });

        messagelist =(ListView)findViewById(R.id.massage_list);


        cardView =(RecyclerView)findViewById(R.id.cardView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        cardView.setLayoutManager(layoutManager);

        /*messagelist1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                // selected item
                String selected = ((TextView) view.findViewById(R.id.name)).getText().toString();
                String uid = ((TextView) view.findViewById(R.id.uid_name)).getText().toString();
                String profile_pic=((TextView) view.findViewById(R.id.profile_url)).getText().toString();

                Intent myIntent = new Intent(getApplicationContext(), ChatActivitycontact.class);
                myIntent.putExtra("Username", selected );
                myIntent.putExtra("Uid", uid );
                myIntent.putExtra("image", profile_pic);
                //myIntent.putExtra("Profile", (Parcelable) profile);
                startActivity(myIntent);

            }
        });*/
        messageModelArrayList = new ArrayList<>();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                MessageModel messageModel = new MessageModel();
                messageModel.setMessage("Hi " + user.getDisplayName().split(" ")[0] + "! I'm Fermi assistant.");
                messageModel.setPosition("0");
              //  bottomTextView.setText("I can help you connect to the right person.\nCan you please tell me if your problem relates to any of the following?"););
                messageModelArrayList.add(messageModel);
                custommessageAdapter = new CustomMessageAdapter(AssistantActivity1.this,messageModelArrayList);
                messagelist.setAdapter(custommessageAdapter);
                secondmessage();
            }
        }, 2000);

    }

    private void secondmessage() {

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                MessageModel messageModel = new MessageModel();
                messageModel.setMessage("I can help you connect to the right person. Can you please tell me if your problem relates to any of the following?");
                messageModel.setPosition("0");
                messageModelArrayList.add(messageModel);
                custommessageAdapter = new CustomMessageAdapter(AssistantActivity1.this,messageModelArrayList);
                messagelist.setAdapter(custommessageAdapter);
               thiredmessage();
            }
        }, 2000);

    }

    private void thiredmessage() {

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                bottombuttons.setVisibility(View.VISIBLE);
            }
        }, 2000);
    }

    public void subjectChosenmeths(View view) {

        MessageModel messageModel = new MessageModel();
        messageModel.setMessage("Maths");
        messageModel.setPosition("1");
        messageModelArrayList.add(messageModel);
        custommessageAdapter = new CustomMessageAdapter(AssistantActivity1.this,messageModelArrayList);
        messagelist.setAdapter(custommessageAdapter);
        fourthmessage("Maths");

    }

    public void subjectChosenchemistry(View view) {

        MessageModel messageModel = new MessageModel();
        messageModel.setMessage("Chemistry");
        messageModel.setPosition("1");
        messageModelArrayList.add(messageModel);
        custommessageAdapter = new CustomMessageAdapter(AssistantActivity1.this,messageModelArrayList);
        messagelist.setAdapter(custommessageAdapter);
        fourthmessage("Chemistry");

    }

    public void subjectChosenHealth(View view) {

        MessageModel messageModel = new MessageModel();
        messageModel.setMessage("Health");
        messageModel.setPosition("1");
        messageModelArrayList.add(messageModel);
        custommessageAdapter = new CustomMessageAdapter(AssistantActivity1.this,messageModelArrayList);
        messagelist.setAdapter(custommessageAdapter);
        fourthmessage("Health");
    }

    public void subjectChosenArt(View view) {

        MessageModel messageModel = new MessageModel();
        messageModel.setMessage("Art");
        messageModel.setPosition("1");
        messageModelArrayList.add(messageModel);
        custommessageAdapter = new CustomMessageAdapter(AssistantActivity1.this,messageModelArrayList);
        messagelist.setAdapter(custommessageAdapter);
        fourthmessage("Art");
    }

    public void subjectChosenFitness(View view) {

        MessageModel messageModel = new MessageModel();
        messageModel.setMessage("Fitness");
        messageModel.setPosition("1");
        messageModelArrayList.add(messageModel);
        custommessageAdapter = new CustomMessageAdapter(AssistantActivity1.this,messageModelArrayList);
        messagelist.setAdapter(custommessageAdapter);
        fourthmessage("Fitness");
    }

    public void subjectChosenbiology(View view) {

        MessageModel messageModel = new MessageModel();
        messageModel.setMessage("Biology");
        messageModel.setPosition("1");
        messageModelArrayList.add(messageModel);
        custommessageAdapter = new CustomMessageAdapter(AssistantActivity1.this,messageModelArrayList);
        messagelist.setAdapter(custommessageAdapter);
        fourthmessage("Biology");

    }

    public void subjectChosenphysics(View view) {

        MessageModel messageModel = new MessageModel();
        messageModel.setMessage("Physics");
        messageModel.setPosition("1");
        messageModelArrayList.add(messageModel);
        custommessageAdapter = new CustomMessageAdapter(AssistantActivity1.this,messageModelArrayList);
        messagelist.setAdapter(custommessageAdapter);
        fourthmessage("Physics");

    }
    private void fourthmessage(final String name) {

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                bottombuttons.setVisibility(View.INVISIBLE);
                MessageModel messageModel = new MessageModel();
                messageModel.setMessage("Okay Let me connect with you someone...");
                messageModel.setPosition("0");
                messageModelArrayList.add(messageModel);
                custommessageAdapter = new CustomMessageAdapter(AssistantActivity1.this,messageModelArrayList);
                messagelist.setAdapter(custommessageAdapter);
                fifthmessage(name);
            }
        }, 2000);
    }

    private void fifthmessage(final String name) {

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
               // yesnobutton.setVisibility(View.VISIBLE);
                MessageModel messageModel = new MessageModel();
                messageModel.setMessage("I found these people for you. you can start conversation with anyone...");
                messageModel.setPosition("0");
                messageModelArrayList.add(messageModel);
                custommessageAdapter = new CustomMessageAdapter(AssistantActivity1.this,messageModelArrayList);
                messagelist.setAdapter(custommessageAdapter);
                measdsds(name);

            }
        }, 2000);

    }

    private void measdsds(String name) {

        adapter = new ListingAdapter(getApplication(), users);
        cardView.setAdapter(adapter);
        empty_message.setVisibility(View.GONE);
        cardView.setVisibility(View.VISIBLE);
        horizontalScrollView.setVisibility(View.INVISIBLE);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                    if (adapter.getItemCount() == 0){
            empty_message.setVisibility(View.VISIBLE);
            cardView.setVisibility(View.GONE);
        }
        else {
            cardView.setAdapter(adapter);
            empty_message.setVisibility(View.GONE);
            cardView.setVisibility(View.VISIBLE);
        }

            }
        }, 2000);

        Log.d("getDataFromServer", "getDataFromServer");
        getDataFromServer(name);
    }

    public void getDataFromServer(final String save) {
        //showProgressDialog();
        databaseReference.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                users.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                        User user = postSnapShot.getValue(User.class);

                        try {
                            if (!user.getName().equals(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())){

                                if (save.equals("Art")) {
                                    if (user.isArts()) {
                                        users.add(user);
                                         adapter.notifyDataSetChanged();
                                    }
                                }
                                if (save.equals("Maths")) {
                                    if (user.isMaths()) {
                                        users.add(user);
                                          adapter.notifyDataSetChanged();
                                    }
                                }
                                if (save.equals("Fitness")) {
                                    if (user.isFitness()) {
                                        users.add(user);
                                          adapter.notifyDataSetChanged();
                                    }
                                }
                                if (save.equals("Biology")) {
                                    if (user.isBiology()) {
                                        users.add(user);
                                         adapter.notifyDataSetChanged();
                                    }
                                }
                                if (save.equals("Health")) {
                                    if (user.isHealth()) {
                                        users.add(user);
                                          adapter.notifyDataSetChanged();
                                    }
                                }
                                if (save.equals("Chemistry")) {
                                    if (user.isChemistry()) {
                                        users.add(user);
                                          adapter.notifyDataSetChanged();
                                    }
                                }
                                if (save.equals("Physics")) {
                                    if (user.isPhysics()) {
                                        users.add(user);
                                         adapter.notifyDataSetChanged();
                                    }
                                }
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

    private class ListingAdapter extends RecyclerView.Adapter<ListingAdapter.ViewHolder> {
        Context context;
        LayoutInflater layoutInflater;
        ArrayList<User> users;
        ViewHolder holder;

        public ListingAdapter(Context con, ArrayList<User> users) {
            super();
            context = con;
            layoutInflater = LayoutInflater.from(context);
            this.users = users;
        }

        @Override
        public int getItemCount() {
            return users.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView fullname, email,uid_name,profileurl;
            CircleImageView profile;

            public ViewHolder(View convertView) {
                super(convertView);
                fullname = (TextView) convertView.findViewById(R.id.name);
                email = (TextView) convertView.findViewById(R.id.number);
                profile=(CircleImageView)convertView.findViewById(R.id.image_view_contact_display);
                uid_name=(TextView)convertView.findViewById(R.id.uid_name);
                profileurl = (TextView) convertView.findViewById(R.id.profile_url);
                convertView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {

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
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.contact_lv_item_forassistant, parent, false);
            holder = new ViewHolder(v);
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
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


        }

        @Override
        public long getItemId(int position) {
            return position;
        }


    }
 /*   private class ListingAdapter extends BaseAdapter {
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
                convertView = layoutInflater.inflate(R.layout.contact_lv_item, null, false);
                holder = new ViewHolder();

                holder.fullname = (TextView) convertView.findViewById(R.id.name);
                holder.email = (TextView) convertView.findViewById(R.id.number);
                holder.profile=(CircleImageView)convertView.findViewById(R.id.image_view_contact_display);
                holder.uid_name=(TextView)convertView.findViewById(R.id.uid_name);
                holder.profileurl = (TextView) convertView.findViewById(R.id.profile_url);

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

            //  }
          *//* else {
                holder.fullname.setVisibility(View.GONE);
                holder.email.setVisibility(View.GONE);
                holder.profile.setVisibility(View.GONE);
            }*//*
            return convertView;
        }

        public class ViewHolder {
            TextView fullname, email,uid_name,profileurl;
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

    }*/
}

