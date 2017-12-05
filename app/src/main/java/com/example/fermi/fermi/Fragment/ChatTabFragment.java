
package com.example.fermi.fermi.Fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.fermi.fermi.AssistantActivity1;
import com.example.fermi.fermi.Chat.ChatActivitycontact;
import com.example.fermi.fermi.R;
import com.example.fermi.fermi.adapter.ChatModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by system1 on 21-08-2017.
 */

public class ChatTabFragment extends Fragment {
    ListView allusers;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    ListingAdapter adapter;
    ArrayList<ChatModel> users = new ArrayList<>();
    FirebaseAuth auth;
    FloatingActionButton msdbtn;
    Timer myTimer;
    TimerTask doThis;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */

    /**
     * The {@link ViewPager} that will host the section contents.
     */

    private ViewPager mViewPager;

    /**
     * THANKS FOR WATCHING PART ONE OF THIS TUTORIAL.
     * THIS WILL BECOME A SERIES WHERE I'LL BE TRYING TO CLONE DIFFERENT
     * SOCIAL NETWORKS. IF YOU'D LIKE TO SEE ME CONTINUE THIS, LEAVE A LIKE
     * IF YOU HAVE ANY QUESTIONS LEAVE IT IN THE COMMENTS
     * AND IF YOU'D LIKE TO FOLLOW ALONG TO THIS TUTORIAL AND SEE MORE FROM ME
     * SUBSCRIBE!
     */

    public ChatTabFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_conversations, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        auth = FirebaseAuth.getInstance();
        allusers = (ListView) view.findViewById(R.id.massage_list);
        msdbtn=(FloatingActionButton)view.findViewById(R.id.conversation_fab);

        msdbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getContext(), AssistantActivity1.class);
                startActivity(myIntent);
            }
        });
        adapter = new ListingAdapter(getActivity(), users);
        allusers.setAdapter(adapter);

        myTimer = new Timer();
        int delay = 0;   // delay for 30 sec.
        int period = 5000;  // repeat every 60 sec.
        doThis = new TimerTask() {
            public void run() {
                getDataFromServer();
                Log.d("ekta12","repeated");
            }
        };

        myTimer.scheduleAtFixedRate(doThis, delay, period);
      /*  new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

            }
        }, 0, 2000);*/

        allusers.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                // selected item
                String selected = ((TextView) view.findViewById(R.id.name)).getText().toString();
                String uid = ((TextView) view.findViewById(R.id.uid_name)).getText().toString();
                String profile_pic=((TextView) view.findViewById(R.id.profile_url)).getText().toString();
                String email_id=((TextView) view.findViewById(R.id.email_id)).getText().toString();
                Intent myIntent = new Intent(getContext(), ChatActivitycontact.class);
                myIntent.putExtra("Username", selected );
                myIntent.putExtra("Uid", uid );
                myIntent.putExtra("image", profile_pic);
                myIntent.putExtra("Email", email_id);
                startActivity(myIntent);
                myTimer.cancel();
               // getActivity().finish();


            }
        });

        /*MainActivity.materialSearchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {
                adapter = new ListingAdapter(getActivity(), users);
                allusers.setAdapter(adapter);
            }
        });

        MainActivity. materialSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("getDataFro3", "getDataFromServer");
                if (newText !=null && !newText.isEmpty()){
                    ArrayList<ChatModel> users1 = new ArrayList<ChatModel>();
                    for (ChatModel item:users){
                        if (item.getPersonname().toLowerCase().contains(newText.toLowerCase()) || item.getPersonlastmessage().toLowerCase().contains(newText.toLowerCase())) {
                            users1.add(item);
                        }
                    }
                    adapter = new ListingAdapter(getActivity(), users1);
                    allusers.setAdapter(adapter);
                }
                else {
                    adapter = new ListingAdapter(getActivity(), users);
                    allusers.setAdapter(adapter);
                }
                return  true;
            }
        });*/
    }

    // getting the data from UserNode at Firebase and then adding the users in Arraylist and setting it to Listview
    public void getDataFromServer() {

        try {
            databaseReference.child("users").child( FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Conversation_person").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    users.clear();

                    try {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                                ChatModel user = postSnapShot.getValue(ChatModel.class);

                                    users.add(user);
                                    adapter.notifyDataSetChanged();
                                    Collections.sort(users, new Comparator<ChatModel>() {
                                        @Override
                                        public int compare(ChatModel u1, ChatModel u2) {

                                            String strLong2 = u1.getPersonname();
                                            String strLong3 = u2.getPersonname();
                                            String strLong = Long.toString(u1.getMessagetime());
                                            String strLong1 = Long.toString(u2.getMessagetime());

                                            Log.d("messagetimeand",strLong2+"  "+strLong3);
                                            return strLong1.compareTo(strLong);
                                        }
                                    });
                                    Log.d("compare","123");


                            }
                        }
                    }catch (Exception e){

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }catch (Exception e){

        }

    }

    private class ListingAdapter extends BaseAdapter {
        Context context;
        LayoutInflater layoutInflater;
        ArrayList<ChatModel> users;
        FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
        Uri uri = null;
        ListingAdapter.ViewHolder holder;

        public ListingAdapter(Context con, ArrayList<ChatModel> users) {
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
                convertView = layoutInflater.inflate(R.layout.chat_lv_item, null, false);
                holder = new ListingAdapter.ViewHolder();

                holder.time = (TextView) convertView.findViewById(R.id.time);
                holder.fullname = (TextView) convertView.findViewById(R.id.name);
                holder.lastmeg = (TextView) convertView.findViewById(R.id.number);
                holder.email_id = (TextView) convertView.findViewById(R.id.email_id);
                holder.profile=(CircleImageView)convertView.findViewById(R.id.image_view_contact_display);
                holder.uid_name=(TextView)convertView.findViewById(R.id.uid_name);
                holder.profileurl = (TextView) convertView.findViewById(R.id.profile_url);
                holder.you = (TextView) convertView.findViewById(R.id.you);
                holder.typing_text = (TextView) convertView.findViewById(R.id.typing_text);
                holder.message_image = (ImageView) convertView.findViewById(R.id.message_image);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            try{
                ChatModel user = users.get(position);
                holder.fullname.setText(user.getPersonname());
                holder.lastmeg.setText(user.getPersonlastmessage());
                holder.email_id.setText(user.getPersonemail());
                holder.uid_name.setText(user.getPersonudid());
                holder.profileurl.setText(user.getPersonprofile());
                holder.time.setText(DateFormat.format("HH:mm",user.getMessagetime()));

                Glide.with(getContext())
                        .load(user.getPersonprofile())
                        .placeholder(R.drawable.profile)
                        .into(holder.profile);

                String meg=user.getMsgDirection();
                String meg1=user.getChatmegalert();

                if (meg1.equals("yes")){
                    holder.message_image.setVisibility(View.VISIBLE);
                } else if (meg1.equals("No")) {
                    holder.message_image.setVisibility(View.GONE);
                   }

                if (meg.equals("1")){
                    holder.you.setVisibility(View.GONE);
                } else if (meg.equals("0")) {
                    holder.you.setVisibility(View.VISIBLE);
                }

                String  typing =user.getTyping_alert();

                // Toast.makeText(getApplication(),""+typing,Toast.LENGTH_SHORT).show();
                //prints "Do you have data? You'll love Firebase."
                if (typing==null) {
                    Log.d("ekta1", "" + typing);
                    holder.typing_text.setVisibility(View.GONE);
                }
                else if (typing.equals("yes")) {
                    Log.d("ekta12", "" + typing);
                    holder.typing_text.setVisibility(View.VISIBLE);
                }
                else {
                    Log.d("ekta123", "" + typing);
                    holder.typing_text.setVisibility(View.GONE);
                }

              /*  databaseReference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("typing_person").child(user.getPersonname()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String  typing = (String) dataSnapshot.child("typingAlert").getValue();

                                // Toast.makeText(getApplication(),""+typing,Toast.LENGTH_SHORT).show();
                                //prints "Do you have data? You'll love Firebase."
                                if (typing==null) {
                                    Log.d("ekta1", "" + typing);
                                    holder.typing_text.setVisibility(View.GONE);
                                }
                                else if (typing.equals("yes")) {
                                    Log.d("ekta12", "" + typing);
                                   holder.typing_text.setVisibility(View.VISIBLE);
                                }
                                else {
                                    Log.d("ekta123", "" + typing);
                                   holder.typing_text.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });*/


            }catch (Exception e){

            }
            //IndexOutOfBoundsException

            //  if (!user.getName().equals(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())){


            // }
            return convertView;
        }

        public class ViewHolder {
            TextView fullname, lastmeg,email_id,uid_name,profileurl,time,you,typing_text;
            CircleImageView profile;
            ImageView message_image;
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
}