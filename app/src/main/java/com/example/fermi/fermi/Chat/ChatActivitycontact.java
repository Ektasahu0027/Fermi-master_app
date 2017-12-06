package com.example.fermi.fermi.Chat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.fermi.fermi.R;
import com.example.fermi.fermi.adapter.ChatMessage;
import com.example.fermi.fermi.adapter.ChatModel;
import com.example.fermi.fermi.adapter.ContactModel;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by znt on 9/12/17.
 */

public class ChatActivitycontact extends AppCompatActivity {
    private FirebaseListAdapter<ChatMessage> adapter;
    DatabaseReference mDatabase;
    Timer myTimer1;
    ListingAdapter message_adapter;
    ListingAdapter message_adapter1;
    ArrayList<ChatMessage> messageview = new ArrayList<>();
    Date today = new Date();
    String loginperson_name, login_email, login_udid, login_profile;
    String username, uid, profile, email;
    ImageButton sendbutton;
    EditText message;
    ListView messagelist;

    Button wave_btn, wave_btn_accept;
    RelativeLayout requrest_lay, send_lay, main_request_lay, requrest_lay2;
    TextView waiting_text, send_text, typing_text;
    String lastmessa = "Send";
    CircleImageView chatprofile;
    ProgressBar progressBar;
    AlertDialog alertDialog;
    String avaliblemeg = "";
    private RecyclerView mMessagesList;
    private SwipeRefreshLayout mRefreshLayout;
    private static final int TOTAL_ITEMS_TO_LOAD = 10;
    private int mCurrentPage = 1;

    private LinearLayoutManager mLinearLayout;
    private MessageAdapter mAdapter;
    // Storage Firebase



    //New Solution
    private int itemPos = 0;

    private String mLastKey = "";
    private String mPrevKey = "";
    private final List<ChatMessage> messagesList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //FirebaseDatabase.getInstance().setLogLevel(Logger.Level.DEBUG);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);

        initComponents();

        Intent intent = getIntent();
        username = intent.getStringExtra("Username");
        uid = intent.getStringExtra("Uid");
        profile = intent.getStringExtra("image");
        email = intent.getStringExtra("Email");

        send_text.setText("Invitation sent to " + username);
        waiting_text.setText("Wating for " + username + "...");

        //for pagination
        //message_adapter = new ListingAdapter(getApplicationContext(), messageview);
        //messagelist.setAdapter(message_adapter);

        mMessagesList = (RecyclerView) findViewById(R.id.messages_list);
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.message_swipe_layout);
        mLinearLayout = new LinearLayoutManager(this);
        mMessagesList.setHasFixedSize(true);
        mMessagesList.setLayoutManager(mLinearLayout);
        mAdapter = new MessageAdapter(messagesList);
        mMessagesList.setAdapter(mAdapter);
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.message_swipe_layout);



        final Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        toolbar.setTitle(username);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatActivitycontact.this.finish();

            }
        });

        Glide.with(getApplicationContext())
                .load(profile)
                .placeholder(R.drawable.profile)
                .into(chatprofile);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
        } else {
            mDatabase = FirebaseDatabase.getInstance().getReference();
            loginperson_name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
            login_email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            login_udid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            login_profile = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString();

            //getDataFromServer();
            loadMessages();
            invitaionchaeck();

        }
        mDatabase.child("users").child(login_udid).child("Conversation_person").child(uid).child("chatmegalert").setValue("No");

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                mCurrentPage++;

                itemPos = 0;

                loadMoreMessages();


            }
        });

        initListeners();


    }

    private void initComponents() {
        sendbutton = (ImageButton) findViewById(R.id.iv_sendMessage);
        message = (EditText) findViewById(R.id.edit_new_text);
        chatprofile = (CircleImageView) findViewById(R.id.tv_close);
        requrest_lay = (RelativeLayout) findViewById(R.id.request_layout1);
        requrest_lay2 = (RelativeLayout) findViewById(R.id.request_layout2);
        send_lay = (RelativeLayout) findViewById(R.id.send_layout1);
        send_text = (TextView) findViewById(R.id.text_invitation1);
        typing_text = (TextView) findViewById(R.id.typing_text);
        waiting_text = (TextView) findViewById(R.id.waiting_text);
        main_request_lay = (RelativeLayout) findViewById(R.id.main_request_layout);
        wave_btn = (Button) findViewById(R.id.wave_btn1);
        wave_btn_accept = (Button) findViewById(R.id.wave_btn2);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar_send_message);
        messagelist = (ListView) findViewById(R.id.lv_list);
        alertDialog = new AlertDialog.Builder(
                ChatActivitycontact.this).create();

    }

    private void initListeners() {

        message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().equals("")) {
                    sendbutton.setEnabled(false);
                    sendbutton.setBackground(getResources().getDrawable(R.drawable.ic_send_24dp));


                } else {
                    sendbutton.setEnabled(true);
                    sendbutton.setBackground(getResources().getDrawable(R.drawable.send_message));


                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        wave_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new WaveSendTask().execute();
            }
        });

        sendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                avaliblemeg = message.getText().toString();
                ChatMessage chatMessage = new ChatMessage();
                // Here we set the film name and star name attribute for a film in one loop
                chatMessage.setMessageText(avaliblemeg);
                chatMessage.setMsgDirection("0");

                // Pass the Film object to the array of Film objects
                //messagesList.add(chatMessage);
                //mAdapter.notifyDataSetChanged();

                long time = today.getTime();
                mDatabase.child("users").child(login_udid).child("Conversation_person").child(uid).setValue(new ChatModel(username, profile, uid, email, "No", avaliblemeg, "0", new Date().getTime()));
                mDatabase.child("users").child(uid).child("Conversation_person").child(login_udid).setValue(new ChatModel(loginperson_name, login_profile, login_udid, login_email, "yes", avaliblemeg, "1", new Date().getTime()));
                mDatabase.child("users").child(login_udid).child("Chat").child(uid).child("ChatList").push().setValue(chatMessage);
                mDatabase.child("users").child(uid).child("Chat").child(login_udid).child("ChatList").push().setValue(new ChatMessage(avaliblemeg, "1"));
                message.setText("");
            }
        });

        wave_btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new WaveSendAcceptTask().execute();
            }
        });
    }



    public void getDataFromServer() {

        mDatabase.child("users").child(login_udid).child("Chat").child(uid).child("ChatList").limitToLast(20).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot datasnapshot) {
                AsyncTask at = new T2();
                at.execute(datasnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private class T2 extends AsyncTask<Object, Integer, Void> {
        private boolean changeUI;

        @Override
        protected Void doInBackground(Object... datasnapshots) {
            changeUI = false;
            if (datasnapshots[0] != null) {
                DataSnapshot datasnapshot = (DataSnapshot) datasnapshots[0];
                if (datasnapshot.exists()) {
                    messageview.clear();
                    for (DataSnapshot postSnapShot : datasnapshot.getChildren()) {
                        ChatMessage user = postSnapShot.getValue(ChatMessage.class);
                        messageview.add(user);
                        changeUI = true;
                    }

                }

            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            if (changeUI) {
                message_adapter.notifyDataSetChanged();
            }
        }


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);


        }

    }

    private void invitaionchaeck() {

        mDatabase.child("users").child(login_udid).child("Person").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                lastmessa = (String) dataSnapshot.child("status").getValue();
                //   Toast.makeText(getApplicationContext(),"message "+lastmessa,Toast.LENGTH_SHORT).show();
                Log.d("last", "" + lastmessa);
                try {

                    if (lastmessa == null) {
                        requrest_lay.setVisibility(View.VISIBLE);
                        send_lay.setVisibility(View.GONE);
                        requrest_lay2.setVisibility(View.GONE);
                        Log.d("last3", "" + lastmessa);
                    } else if (lastmessa.equals("Request")) {
                        requrest_lay.setVisibility(View.GONE);
                        send_lay.setVisibility(View.GONE);
                        requrest_lay2.setVisibility(View.VISIBLE);
                        Log.d("last1", "" + lastmessa);
                    } else if (lastmessa.equals("Invite")) {
                        requrest_lay.setVisibility(View.GONE);
                        send_lay.setVisibility(View.VISIBLE);
                        requrest_lay2.setVisibility(View.GONE);
                        Log.d("last1", "" + lastmessa);
                    } else if (lastmessa.equals("Friend")) {
                        requrest_lay.setVisibility(View.GONE);
                        send_lay.setVisibility(View.GONE);
                        requrest_lay2.setVisibility(View.GONE);
                        Log.d("last2", "" + lastmessa);
                    }

                } catch (Exception e) {
                    Log.d("last4", "" + lastmessa);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private class WaveSendTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            requrest_lay.setVisibility(View.GONE);
            send_lay.setVisibility(View.VISIBLE);
            alertDialog.setMessage("please wait!!!");
            alertDialog.show();
            Log.d("chatActivity", "Inside onPreExecute() of ChatFragment");
        }

        @Override
        protected Void doInBackground(Void... params) {

            ContactModel user = new ContactModel();
            user.setPersonname(username);
            user.setPersonprofile(profile);
            user.setPersonemail(email);
            user.setPersonudid(uid);
            user.setStatus("Invite");

            ContactModel user1 = new ContactModel();
            user1.setPersonname(loginperson_name);
            user1.setPersonprofile(login_profile);
            user1.setPersonudid(login_udid);
            user1.setPersonemail(login_email);
            user1.setStatus("Request");

            mDatabase.child("users").child(login_udid).child("Person").child(uid).setValue(user);
            mDatabase.child("users").child(uid).child("Person").child(login_udid).setValue(user1);
            Log.d("chatActivity", "Inside doInBackground() of ChatFragment");
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            alertDialog.dismiss();
            Log.d("chatActivity", "inside onPOstExecute() of ChatFragment");
        }

    }

    private class WaveSendAcceptTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            alertDialog.setMessage("please wait!!!");
            alertDialog.show();
            requrest_lay2.setVisibility(View.GONE);
            Log.d("chatActivity", "Inside onPreExecute() of ChatFragment");

        }

        @Override
        protected Void doInBackground(Void... params) {

            ContactModel user = new ContactModel();
            user.setPersonname(username);
            user.setPersonprofile(profile);
            user.setPersonemail(email);
            user.setPersonudid(uid);
            user.setStatus("Friend");

            ContactModel user1 = new ContactModel();
            user1.setPersonname(loginperson_name);
            user1.setPersonprofile(login_profile);
            user1.setPersonudid(login_udid);
            user1.setPersonemail(login_email);
            user1.setStatus("Friend");

            mDatabase.child("users").child(login_udid).child("Person").child(uid).setValue(user);
            mDatabase.child("users").child(uid).child("Person").child(login_udid).setValue(user1);

            Log.d("chatActivity", "Inside doInBackground() of ChatFragment");
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // progressBar.setVisibility(View.GONE);
            alertDialog.dismiss();
            Log.d("chatActivity", "inside onPOstExecute() of ChatFragment");
        }

    }

    private class ListingAdapter extends BaseAdapter {
        Context context;
        LayoutInflater layoutInflater;
        ArrayList<ChatMessage> chatmesssages;
        FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
        Uri uri = null;
        ViewHolder holder;

        public ListingAdapter(Context con, ArrayList<ChatMessage> chatmsg) {
            context = con;
            layoutInflater = LayoutInflater.from(context);
            this.chatmesssages = chatmsg;
        }

        @Override
        public int getCount() {
            return chatmesssages.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.item_chat_adapter, null, false);
                holder = new ViewHolder();

                holder.recevier = (LinearLayout) convertView.findViewById(R.id.receiver);
                holder.sender = (LinearLayout) convertView.findViewById(R.id.sender);
                holder.senderimage = (CircleImageView) convertView.findViewById(R.id.profile_view);

                holder.tvmessage = (TextView) convertView.findViewById(R.id.messageTextView);
                holder.tvmessage1 = (TextView) convertView.findViewById(R.id.messageTextView1);
                holder.time = (TextView) convertView.findViewById(R.id.txt_msg_time);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            try {
                ChatMessage user = chatmesssages.get(position);


                holder.time.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", user.getMessageTime()));

                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) holder.tvmessage.getLayoutParams();

                if (user.getMsgDirection().equals("1")) { //direction=0  should be displayed as messages sent from the mobile app.
                    lp.gravity = Gravity.LEFT;
                    holder.recevier.setVisibility(View.GONE);
                    holder.sender.setVisibility(View.VISIBLE);
                    holder.sender.setGravity(Gravity.LEFT);
                    Glide.with(getApplicationContext())
                            .load(profile)
                            .placeholder(R.drawable.profile)
                            .into(holder.senderimage);
                    holder.sender.setLayoutParams(lp);
                    holder.tvmessage.setText(user.getMessageText());
                    holder.tvmessage.setTextColor(getResources().getColor(R.color.reciver_text_msg));
                    holder.tvmessage.setBackground(getResources().getDrawable(R.drawable.textview_border));
                    //    holder.txt_message_time.setTextColor(mContext.getResources().getColor(R.color.error_text));
                } else if (user.getMsgDirection().equals("0")) { //direction=1  should be displayed as messages received at the mobile app
                    lp.gravity = Gravity.RIGHT;
                    holder.sender.setVisibility(View.GONE);
                    holder.recevier.setVisibility(View.VISIBLE);
                    holder.recevier.setGravity(Gravity.RIGHT);
                    holder.recevier.setLayoutParams(lp);
                    holder.tvmessage1.setText(user.getMessageText());
                    holder.tvmessage1.setTextColor(getResources().getColor(R.color.sender_text_msg));
                    holder.tvmessage1.setBackground(getResources().getDrawable(R.drawable.textview_bordersender));
                }
            } catch (Exception e) {

            }

            return convertView;
        }

        public class ViewHolder {

            TextView tvmessage, tvmessage1, time;
            LinearLayout recevier, sender;
            CircleImageView senderimage;

        }

        @Override
        public Object getItem(int position) {
            return chatmesssages.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

    }


    private void loadMoreMessages() {

        DatabaseReference messageRef =  mDatabase.child("users").child(login_udid).child("Chat").child(uid).child("ChatList");

        Query messageQuery = messageRef.orderByKey().endAt(mLastKey).limitToLast(10);


        messageQuery.addChildEventListener(new ChildEventListener() {


            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                ChatMessage message = dataSnapshot.getValue(ChatMessage.class);
                String messageKey = dataSnapshot.getKey();

                if(!mPrevKey.equals(messageKey)){

                    messagesList.add(itemPos++, message);

                } else {

                    mPrevKey = mLastKey;

                }


                if(itemPos == 1) {

                    mLastKey = messageKey;

                }


                Log.d("TOTALKEYS", "Last Key : " + mLastKey + " | Prev Key : " + mPrevKey + " | Message Key : " + messageKey);

                mAdapter.notifyDataSetChanged();

                mRefreshLayout.setRefreshing(false);

                mLinearLayout.scrollToPositionWithOffset(10, 0);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }



    private void loadMessages() {

        DatabaseReference messageRef =  mDatabase.child("users").child(login_udid).child("Chat").child(uid).child("ChatList");

        Query messageQuery = messageRef.limitToLast(mCurrentPage * TOTAL_ITEMS_TO_LOAD);


        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                ChatMessage message = dataSnapshot.getValue(ChatMessage.class);

                itemPos++;

                if(itemPos == 1){

                    String messageKey = dataSnapshot.getKey();

                    mLastKey = messageKey;
                    mPrevKey = messageKey;

                }

                messagesList.add(message);
                mAdapter.notifyDataSetChanged();

                mMessagesList.scrollToPosition(messagesList.size() - 1);

                mRefreshLayout.setRefreshing(false);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //  mDatabase.child("users").child(uid).child("Person").child(login_udid).child("typing_alert").setValue("no");
        mDatabase.child("users").child(uid).child("Conversation_person").child(login_udid).child("typing_alert").setValue("no");
        finish();
    }
    public class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvmessage, tvmessage1, time;
        LinearLayout recevier, sender;
        CircleImageView senderimage;

        public MessageViewHolder(View view) {
            super(view);
            recevier = (LinearLayout) view.findViewById(R.id.receiver);
            sender = (LinearLayout) view.findViewById(R.id.sender);
            senderimage = (CircleImageView) view.findViewById(R.id.profile_view);

            tvmessage = (TextView) view.findViewById(R.id.messageTextView);
            tvmessage1 = (TextView) view.findViewById(R.id.messageTextView1);
            time = (TextView) view.findViewById(R.id.txt_msg_time);

        }
    }
    public class MessageAdapter extends RecyclerView.Adapter<ChatActivitycontact.MessageViewHolder>{

        private List<ChatMessage> mMessageList;


        public MessageAdapter(List<ChatMessage> mMessageList) {

            this.mMessageList = mMessageList;

        }
        @Override
        public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_adapter ,parent, false);

            return new MessageViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final MessageViewHolder holder, int i) {

            ChatMessage c = mMessageList.get(i);




            ChatMessage user = mMessageList.get(i);


            holder.time.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", user.getMessageTime()));
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) holder.tvmessage.getLayoutParams();

            if (user.getMsgDirection().equals("1")) { //direction=0  should be displayed as messages sent from the mobile app.
                lp.gravity = Gravity.LEFT;
                holder.recevier.setVisibility(View.GONE);
                holder.sender.setVisibility(View.VISIBLE);
                holder.sender.setGravity(Gravity.LEFT);
                Glide.with(getApplicationContext())
                        .load(profile)
                        .placeholder(R.drawable.profile)
                        .into(holder.senderimage);
                holder.sender.setLayoutParams(lp);
                holder.tvmessage.setText(user.getMessageText());
                holder.tvmessage.setTextColor(getResources().getColor(R.color.reciver_text_msg));
                holder.tvmessage.setBackground(getResources().getDrawable(R.drawable.textview_border));
                //    holder.txt_message_time.setTextColor(mContext.getResources().getColor(R.color.error_text));
            } else if (user.getMsgDirection().equals("0")) { //direction=1  should be displayed as messages received at the mobile app
                lp.gravity = Gravity.RIGHT;
                holder.sender.setVisibility(View.GONE);
                holder.recevier.setVisibility(View.VISIBLE);
                holder.recevier.setGravity(Gravity.RIGHT);
                holder.recevier.setLayoutParams(lp);
                holder.tvmessage1.setText(user.getMessageText());
                holder.tvmessage1.setTextColor(getResources().getColor(R.color.sender_text_msg));
                holder.tvmessage1.setBackground(getResources().getDrawable(R.drawable.textview_bordersender));
            }
        }

        @Override
        public int getItemCount() {
            return mMessageList.size();
        }



    }

}

/*
package com.example.fermi.fermi.Chat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.fermi.fermi.R;
import com.example.fermi.fermi.adapter.ChatMessage;
import com.example.fermi.fermi.adapter.ChatModel;
import com.example.fermi.fermi.adapter.ContactModel;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

*/
/**
 * Created by znt on 9/12/17.
 *//*


public class ChatActivitycontact extends AppCompatActivity {

    private FirebaseListAdapter<ChatMessage> adapter;
    DatabaseReference mDatabase;
    ListingAdapter message_adapter;
    ArrayList<ChatMessage> messageview = new ArrayList<>();
    String loginperson_name,login_email,login_udid,login_profile;
    String username,uid,profile,email;
    ImageButton sendbutton;
    EditText message;
    ListView messagelist;
    Button wave_btn,wave_btn_accept;
    RelativeLayout requrest_lay,send_lay,main_request_lay,requrest_lay2;
    TextView waiting_text,send_text,typing_text;
    String lastmessa="Send";
    CircleImageView chatprofile;
    ProgressBar progressBar;
    AlertDialog alertDialog;
    String avaliblemeg="";
    int add=0,count=1;
    public Handler mhandler;
    public View ftview;
    public boolean isLoading=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);

        initComponents();

        final Intent intent = getIntent();
        username = intent.getStringExtra("Username");
        uid = intent.getStringExtra("Uid");
        profile = intent.getStringExtra("image");
        email = intent.getStringExtra("Email");

        send_text.setText("Invitation sent to "+username);
        waiting_text.setText("Wating for "+username+"...");

        message_adapter = new ListingAdapter(getApplicationContext(), messageview);
        messagelist.setAdapter(message_adapter);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        toolbar.setTitle(username);
        LayoutInflater l1= (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ftview=l1.inflate(R.layout.header_view,null);
        mhandler= new MyHandler();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  mDatabase.child("users").child(uid).child("Person").child(loginperson_name).child("typing_alert").setValue("no");
              //  mDatabase.child("users").child(uid).child("Conversation_person").child(login_udid).child("typing_alert").setValue("no");
             //   myTimer.cancel();
                //myTimer1.cancel();
                ChatActivitycontact.this.finish();
             */
/*   Intent intent1= new Intent(ChatActivitycontact.this, MainActivity.class);
                startActivity(intent1);*//*




            }
        });

        Glide.with(getApplicationContext())
                .load(profile)
                .placeholder(R.drawable.profile)
                .into(chatprofile);

        if (FirebaseAuth.getInstance().getCurrentUser() == null)
        {
        }
        else {
            mDatabase = FirebaseDatabase.getInstance().getReference();
            loginperson_name=  FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
            login_email=  FirebaseAuth.getInstance().getCurrentUser().getEmail();
            login_udid=  FirebaseAuth.getInstance().getCurrentUser().getUid();
            login_profile=  FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString();
            new GetDataFromServerTask().execute();
           // displayChatMessage();
            invitaionchaeck();
        }

        mDatabase.child("users").child(login_udid).child("Conversation_person").child(uid).child("chatmegalert").setValue("No");

        initListeners();


       */
/* final Handler ha=new Handler();
        ha.postDelayed(new Runnable() {

            @Override
            public void run() {

                new TypingStatusTask().execute();
                ha.postDelayed(this, 10000);
            }
        }, 10000);*//*


      */
/*  new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {



            }
        }, 0, 3000);*//*


    }

    private void initComponents() {

        sendbutton=(ImageButton)findViewById(R.id.iv_sendMessage);
        message=(EditText)findViewById(R.id.edit_new_text);
        chatprofile = (CircleImageView)findViewById(R.id.tv_close);
        requrest_lay=(RelativeLayout)findViewById(R.id.request_layout1);
        requrest_lay2=(RelativeLayout)findViewById(R.id.request_layout2);
        send_lay=(RelativeLayout)findViewById(R.id.send_layout1);
        send_text=(TextView)findViewById(R.id.text_invitation1);
        typing_text=(TextView)findViewById(R.id.typing_text);
        waiting_text=(TextView)findViewById(R.id.waiting_text);
        main_request_lay=(RelativeLayout)findViewById(R.id.main_request_layout);
        wave_btn=(Button)findViewById(R.id.wave_btn1);
        wave_btn_accept = (Button) findViewById(R.id.wave_btn2);
        progressBar=(ProgressBar)findViewById(R.id.progress_bar_send_message);
        messagelist=(ListView)findViewById(R.id.lv_list);
        alertDialog = new AlertDialog.Builder(
                ChatActivitycontact.this).create();

    }

    private void initListeners() {
      */
/*  messagelist.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int mLastFirstVisibleItem;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    //    Toast.makeText(getApplicationContext(),"SCROLLING DOWN "+lastmessa,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

             *//*
*/
/*   final int lastItem = firstVisibleItem - visibleItemCount;
                if(lastItem == totalItemCount){
                    add=add+5;

                    getDataFromServer();
                }*//*
*/
/*


                int lastIndexInScreen = visibleItemCount + firstVisibleItem;



                if (lastIndexInScreen>= totalItemCount && 	!isLoading) {

                    // It is time to load more items
                    Toast.makeText(getApplicationContext(),""+lastIndexInScreen, Toast.LENGTH_SHORT).show();
                    isLoading = true;

                    add=add+5;
                  new GetDataFromServerTask().execute();

                }



                *//*
*/
/*if(mLastFirstVisibleItem == 0){
                    add=add+5;
                    mLastFirstVisibleItem=1;
                    new GetDataFromServerTask().execute();
                    //  messagelist.smoothScrollToPosition(10);
                    //  Toast.makeText(getApplicationContext(),mLastFirstVisibleItem+"SCROLLING DOWN "+firstVisibleItem,Toast.LENGTH_SHORT).show();
                }
                mLastFirstVisibleItem=firstVisibleItem;*//*
*/
/*

            }

        });
*//*

        messagelist.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int mLastFirstVisibleItem;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
              //  Toast.makeText(getApplicationContext(),"message "+messagelist.getCount(), Toast.LENGTH_SHORT).show();
               //Toast.makeText(getApplicationContext(),isLoading+" "+view.getFirstVisiblePosition()+" "+view.getLastVisiblePosition()+" "+(totalItemCount-1)+" "+messagelist.getCount() ,Toast.LENGTH_SHORT).show();
               Log.d("value",isLoading+" "+view.getFirstVisiblePosition()+" "+view.getLastVisiblePosition()+" "+(totalItemCount-1)+" "+messagelist.getCount());
               */
/*  if (view.getLastVisiblePosition() == totalItemCount-1 && messagelist.getCount() >=10 && isLoading==false){
                    isLoading=true;
                     add=0;
                    Thread thread =new ThreadGetmoreData();
                    thread.start();
                 // Toast.makeText(getApplicationContext(),"message "+messagelist.getCount(),Toast.LENGTH_SHORT).show();
                }
*//*


                if(view.getFirstVisiblePosition() == 1 && messagelist.getCount() >=5 && isLoading==false && count==1)
                {   Log.d("value123",""+(add+10));
                  //  messageview.clear();
                    count=2;
                  //  message_adapter.notifyDataSetChanged();
                    add=add+5;
                    isLoading=true;
                    Thread thread =new ThreadGetmoreData();
                    thread.start();

                   // Toast.makeText(getApplicationContext(),"message "+messagelist.getCount(),Toast.LENGTH_SHORT).show();
                    */
/*  int index = messagelist.getFirstVisiblePosition();
                    Log.i("SCROLLING UP","TRUE");
                    add=add+10;
                    mLastFirstVisibleItem=1;
                    new GetDataFromServerTask().execute();
                    messagelist.smoothScrollToPosition(index);*//*

                }
                else {
                    count=1;
                }
               // mLastFirstVisibleItem=firstVisibleItem;

            }
        });
        message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().equals("")) {
                    sendbutton.setEnabled(false);
                    sendbutton.setBackground(getResources().getDrawable(R.drawable.ic_send_24dp));



                       //mDatabase.child("users").child(uid).child("Conversation_person").child(login_udid).child("typing_alert").setValue("no");


                  } else {
                    sendbutton.setEnabled(true);
                    sendbutton.setBackground(getResources().getDrawable(R.drawable.send_message));

                         //   mDatabase.child("users").child(uid).child("Conversation_person").child(login_udid).child("typing_alert").setValue("yes");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        wave_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new WaveSendTask().execute();
            }
        });

        sendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                avaliblemeg = message.getText().toString();
                // ChatMessage name=  new ChatMessage(avaliblemeg,"0");


                ChatMessage film = new ChatMessage();
                // Here we set the film name and star name attribute for a film in one loop
                film.setMessageText(avaliblemeg);
                film.setMsgDirection("0");

                // Pass the Film object to the array of Film objects
                messageview.add(film);
                message_adapter.notifyDataSetChanged();
                message.setText("");
                new SendMessageTask().execute();
            }
        });

        wave_btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new WaveSendAcceptTask().execute();
            }
        });
    }

  */
/*  private void displayChatMessage() {

        messagelist=(ListView)findViewById(R.id.lv_list);
        adapter = new FirebaseListAdapter<ChatMessage>(this,ChatMessage.class,R.layout.item_chat_adapter,mDatabase.child("users").child(login_udid).child("Chat").child(uid).child("ChatList")) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {

                TextView tvmessage,tvmessage1,time;
                LinearLayout recevier,sender;
                CircleImageView senderimage;

                recevier = (LinearLayout) v.findViewById(R.id.receiver);
                sender = (LinearLayout) v.findViewById(R.id.sender);
                senderimage = (CircleImageView) v.findViewById(R.id.profile_view);

                tvmessage=(TextView)v.findViewById(R.id.messageTextView);
                tvmessage1=(TextView)v.findViewById(R.id.messageTextView1);
                time=(TextView)v.findViewById(R.id.txt_msg_time);

                //message.setText(model.getMessageText());
                time.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",model.getMessageTime()));

                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) tvmessage.getLayoutParams();

                if(model.getMsgDirection().equals("1")){ //direction=0  should be displayed as messages sent from the mobile app.
                    lp.gravity = Gravity.LEFT;
                    recevier.setVisibility(View.GONE);
                    sender.setVisibility(View.VISIBLE);
                    sender.setGravity(Gravity.LEFT);
                    Glide.with(getApplicationContext())
                            .load(profile)
                            .placeholder(R.drawable.profile)
                            .into(senderimage);
                    sender.setLayoutParams(lp);
                    tvmessage.setText(model.getMessageText());
                    tvmessage.setTextColor(getResources().getColor(R.color.reciver_text_msg));
                    tvmessage.setBackground(getResources().getDrawable(R.drawable.textview_border));
                }else if(model.getMsgDirection().equals("0")){ //direction=1  should be displayed as messages received at the mobile app
                    lp.gravity = Gravity.RIGHT;
                    sender.setVisibility(View.GONE);
                    recevier.setVisibility(View.VISIBLE);
                    recevier.setGravity(Gravity.RIGHT);
                    recevier.setLayoutParams(lp);
                    tvmessage1.setText(model.getMessageText());
                    tvmessage1.setTextColor(getResources().getColor(R.color.sender_text_msg));
                    tvmessage1.setBackground(getResources().getDrawable(R.drawable.textview_bordersender));
                }
            }
        };

        messagelist.setAdapter(adapter);

    }*//*

    private void invitaionchaeck(){

        mDatabase.child("users").child(login_udid).child("Person").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                lastmessa = (String) dataSnapshot.child("status").getValue();
                //   Toast.makeText(getApplicationContext(),"message "+lastmessa,Toast.LENGTH_SHORT).show();
                Log.d("last", "" + lastmessa);
                try {

                    if (lastmessa == null) {
                        requrest_lay.setVisibility(View.VISIBLE);
                        send_lay.setVisibility(View.GONE);
                        requrest_lay2.setVisibility(View.GONE);
                        Log.d("last3", "" + lastmessa);
                    } else if (lastmessa.equals("Request")) {
                        requrest_lay.setVisibility(View.GONE);
                        send_lay.setVisibility(View.GONE);
                        requrest_lay2.setVisibility(View.VISIBLE);
                        Log.d("last1", "" + lastmessa);
                    } else if (lastmessa.equals("Invite")) {
                        requrest_lay.setVisibility(View.GONE);
                        send_lay.setVisibility(View.VISIBLE);
                        requrest_lay2.setVisibility(View.GONE);
                        Log.d("last1", "" + lastmessa);
                    } else if (lastmessa.equals("Friend")) {
                        requrest_lay.setVisibility(View.GONE);
                        send_lay.setVisibility(View.GONE);
                        requrest_lay2.setVisibility(View.GONE);
                        Log.d("last2", "" + lastmessa);
                    }

                } catch (Exception e) {
                    Log.d("last4", "" + lastmessa);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
  */
/*  private class TypingStatusTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {

            mDatabase.child("users").child(login_udid).child("Conversation_person").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String  typing = (String) dataSnapshot.child("typing_alert").getValue();

                    // Toast.makeText(getApplication(),""+typing,Toast.LENGTH_SHORT).show();
                    //prints "Do you have data? You'll love Firebase."

                    if (typing==null) {
                        //Log.d("ekta1", "" + typing);
                        typing_text.setVisibility(View.GONE);
                    }
                    else if (typing.equals("yes")) {
                        //  Log.d("ekta12", "" + typing);
                        typing_text.setVisibility(View.VISIBLE);
                    }
                    else {
                        //  Log.d("ekta123", "" + typing);
                        typing_text.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }

    }*//*

  public class MyHandler extends Handler {
      @Override
      public void handleMessage(Message msg) {
          switch (msg.what){
              case 0:
                  messagelist.addHeaderView(ftview);
                  break;
              case 1:

                 // message_adapter.addListItemToAdapter((ArrayList < ChatMessage>) msg.obj);
                  messagelist.removeHeaderView(ftview);
                  isLoading=false;
                  break;
              default:break;

          }
      }
  }
private ArrayList<ChatMessage> getmoreData(){
    final ArrayList<ChatMessage> fistlist=new ArrayList<>();
    Log.d("value1",""+(add+10));
    try {
        mDatabase.child("users").child(login_udid).child("Chat").child(uid).child("ChatList").limitToLast(add+10).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                 messageview.clear();

                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                        ChatMessage user = postSnapShot.getValue(ChatMessage.class);

                        messageview.add(user);
                       message_adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }catch (Exception e){

    }

    return messageview;

}
  public class ThreadGetmoreData extends Thread{
      @Override
      public void run() {
         mhandler.sendEmptyMessage(0);
          Log.d("value12",""+(add+10));
          ArrayList<ChatMessage> Fstresult= getmoreData();

          try {
              Thread.sleep(3000);
          }
          catch (InterruptedException e){
              e.printStackTrace();
          }
          Message message = mhandler.obtainMessage(1,Fstresult);
          mhandler.sendMessage(message);
      }
    }

    private class GetDataFromServerTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                mDatabase.child("users").child(login_udid).child("Chat").child(uid).child("ChatList").limitToLast(15).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        messageview.clear();

                        if (dataSnapshot.exists()) {
                            for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                                ChatMessage user = postSnapShot.getValue(ChatMessage.class);

                                messageview.add(user);
                                message_adapter.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }catch (Exception e){

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }

    }
    private class WaveSendTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //  progressBar.setVisibility(View.VISIBLE);
            requrest_lay.setVisibility(View.GONE);
            send_lay.setVisibility(View.VISIBLE);
            alertDialog.setMessage("please wait!!!");
            alertDialog.show();
            Log.d("chatActivity", "Inside onPreExecute() of ChatFragment");
        }

        @Override
        protected Void doInBackground(Void... params) {

            ContactModel user = new ContactModel();
            user.setPersonname(username);
            user.setPersonprofile(profile);
            user.setPersonemail(email);
            user.setPersonudid(uid);
            user.setStatus("Invite");

            ContactModel user1 = new ContactModel();
            user1.setPersonname(loginperson_name);
            user1.setPersonprofile(login_profile);
            user1.setPersonudid(login_udid);
            user1.setPersonemail(login_email);
            user1.setStatus("Request");

            mDatabase.child("users").child(login_udid).child("Person").child(uid).setValue(user);
            mDatabase.child("users").child(uid).child("Person").child(login_udid).setValue(user1);
            Log.d("chatActivity", "Inside doInBackground() of ChatFragment");
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // progressBar.setVisibility(View.GONE);
            alertDialog.dismiss();
            Log.d("chatActivity", "inside onPOstExecute() of ChatFragment");
        }

    }

    private class SendMessageTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected Void doInBackground(Void... params) {

            mDatabase.child("users").child(login_udid).child("Conversation_person").child(uid).setValue(new ChatModel(username,profile, uid, email,"No",avaliblemeg,"0",new Date().getTime()));
            mDatabase.child("users").child(uid).child("Conversation_person").child(login_udid).setValue(new ChatModel(loginperson_name,login_profile,login_udid,login_email,"yes",avaliblemeg,"1",new Date().getTime()));
            mDatabase.child("users").child(login_udid).child("Chat").child(uid).child("ChatList").push().setValue(new ChatMessage(avaliblemeg,"0"));
            mDatabase.child("users").child(uid).child("Chat").child(login_udid).child("ChatList").push().setValue(new ChatMessage(avaliblemeg,"1"));

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

        }

    }
    private class WaveSendAcceptTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            alertDialog.setMessage("please wait!!!");
            alertDialog.show();
            requrest_lay2.setVisibility(View.GONE);
            Log.d("chatActivity", "Inside onPreExecute() of ChatFragment");

        }

        @Override
        protected Void doInBackground(Void... params) {

            ContactModel user = new ContactModel();
            user.setPersonname(username);
            user.setPersonprofile(profile);
            user.setPersonemail(email);
            user.setPersonudid(uid);
            user.setStatus("Friend");

            ContactModel user1 = new ContactModel();
            user1.setPersonname(loginperson_name);
            user1.setPersonprofile(login_profile);
            user1.setPersonudid(login_udid);
            user1.setPersonemail(login_email);
            user1.setStatus("Friend");

            mDatabase.child("users").child(login_udid).child("Person").child(uid).setValue(user);
            mDatabase.child("users").child(uid).child("Person").child(login_udid).setValue(user1);

            Log.d("chatActivity", "Inside doInBackground() of ChatFragment");
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // progressBar.setVisibility(View.GONE);
            alertDialog.dismiss();
            Log.d("chatActivity", "inside onPOstExecute() of ChatFragment");
        }

    }

    private class ListingAdapter extends BaseAdapter {
        Context context;
        LayoutInflater layoutInflater;
        ArrayList<ChatMessage> users;
        FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
        Uri uri = null;
        ViewHolder holder;

        public ListingAdapter(Context con, ArrayList<ChatMessage> users) {
            context = con;
            layoutInflater = LayoutInflater.from(context);
            this.users = users;
        }

        public void addListItemToAdapter(List<ChatMessage> list){
            users.addAll(list);
            this.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return users.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.item_chat_adapter, null, false);
                holder = new ViewHolder();

                holder.recevier = (LinearLayout) convertView.findViewById(R.id.receiver);
                holder.sender = (LinearLayout) convertView.findViewById(R.id.sender);
                holder.senderimage = (CircleImageView) convertView.findViewById(R.id.profile_view);

                holder. tvmessage=(TextView)convertView.findViewById(R.id.messageTextView);
                holder. tvmessage1=(TextView)convertView.findViewById(R.id.messageTextView1);
                holder.time=(TextView)convertView.findViewById(R.id.txt_msg_time);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            try {
                ChatMessage user = users.get(position);

                holder.time.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", user.getMessageTime()));

                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) holder.tvmessage.getLayoutParams();

                if (user.getMsgDirection().equals("1")) { //direction=0  should be displayed as messages sent from the mobile app.
                    lp.gravity = Gravity.LEFT;
                    holder.recevier.setVisibility(View.GONE);
                    holder.sender.setVisibility(View.VISIBLE);
                    holder.sender.setGravity(Gravity.LEFT);
                    Glide.with(getApplicationContext())
                            .load(profile)
                            .placeholder(R.drawable.profile)
                            .into(holder.senderimage);
                    holder.sender.setLayoutParams(lp);
                    holder.tvmessage.setText(user.getMessageText());
                    holder.tvmessage.setTextColor(getResources().getColor(R.color.reciver_text_msg));
                    holder.tvmessage.setBackground(getResources().getDrawable(R.drawable.textview_border));
                    //    holder.txt_message_time.setTextColor(mContext.getResources().getColor(R.color.error_text));
                } else if (user.getMsgDirection().equals("0")) { //direction=1  should be displayed as messages received at the mobile app
                    lp.gravity = Gravity.RIGHT;
                    holder.sender.setVisibility(View.GONE);
                    holder.recevier.setVisibility(View.VISIBLE);
                    holder.recevier.setGravity(Gravity.RIGHT);
                    holder.recevier.setLayoutParams(lp);
                    holder.tvmessage1.setText(user.getMessageText());
                    holder.tvmessage1.setTextColor(getResources().getColor(R.color.sender_text_msg));
                    holder.tvmessage1.setBackground(getResources().getDrawable(R.drawable.textview_bordersender));
                }
            }catch (Exception e){

            }

            return convertView;
        }

        public class ViewHolder {

            TextView tvmessage,tvmessage1,time;
            LinearLayout recevier,sender;
            CircleImageView senderimage;

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
      //  mDatabase.child("users").child(uid).child("Person").child(login_udid).child("typing_alert").setValue("no");
     //   mDatabase.child("users").child(uid).child("Conversation_person").child(login_udid).child("typing_alert").setValue("no");
        //myTimer.cancel();
       // myTimer1.cancel();
        finish();
       */
/* Intent intent1= new Intent(ChatActivitycontact.this, MainActivity.class);
        startActivity(intent1);*//*

    }
}
*/
