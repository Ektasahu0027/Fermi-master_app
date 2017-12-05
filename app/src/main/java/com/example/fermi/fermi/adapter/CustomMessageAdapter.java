package com.example.fermi.fermi.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.fermi.fermi.R;

import java.util.ArrayList;

/**
 * Created by Parsania Hardik on 11-May-17.
 */
public class CustomMessageAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<MessageModel> messageModelArrayList;


    public CustomMessageAdapter(Context context, ArrayList<MessageModel> messageModelArrayList) {

        this.context = context;
        this.messageModelArrayList = messageModelArrayList;

    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }
    @Override
    public int getItemViewType(int position) {

        return position;
    }

    @Override
    public int getCount() {
        return messageModelArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return messageModelArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.message_layout, null, true);
            holder.recevier = (LinearLayout) convertView.findViewById(R.id.receiver);
            holder.sender = (LinearLayout) convertView.findViewById(R.id.sender);
            holder.tvmessage = (TextView) convertView.findViewById(R.id.messageTextView);
            holder.tvmessage1 = (TextView) convertView.findViewById(R.id.messageTextView1);

            convertView.setTag(holder);
        }else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = (ViewHolder)convertView.getTag();
        }

        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) holder.tvmessage.getLayoutParams();

            if(messageModelArrayList.get(position).getPosition().equals("0")){ //direction=0  should be displayed as messages sent from the mobile app.
                lp.gravity = Gravity.LEFT;
                holder.recevier.setVisibility(View.GONE);
                holder.sender.setVisibility(View.VISIBLE);
                holder.sender.setGravity(Gravity.LEFT);
                holder.sender.setLayoutParams(lp);
                holder.tvmessage.setText(messageModelArrayList.get(position).getMessage());
                holder.tvmessage.setTextColor(context.getResources().getColor(R.color.reciver_text_msg));
                holder.tvmessage.setBackground(context.getResources().getDrawable(R.drawable.textview_border));
            //    holder.txt_message_time.setTextColor(mContext.getResources().getColor(R.color.error_text));
            }else if(messageModelArrayList.get(position).getPosition().equals("1")){ //direction=1  should be displayed as messages received at the mobile app
                lp.gravity = Gravity.RIGHT;
                holder.sender.setVisibility(View.GONE);
                holder.recevier.setVisibility(View.VISIBLE);
                holder.recevier.setGravity(Gravity.RIGHT);
                holder.recevier.setLayoutParams(lp);
                holder.tvmessage1.setText(messageModelArrayList.get(position).getMessage());
                holder.tvmessage1.setTextColor(context.getResources().getColor(R.color.sender_text_msg));
                holder.tvmessage1.setBackground(context.getResources().getDrawable(R.drawable.textview_bordersender));
            }
          //  holder.txt_message_time.setVisibility(View.VISIBLE);
         //   holder.txt_message_time.setText(message.getMsgTime());

      /* if(i==0)
        holder.tvmessage.setText(messageModelArrayList.get(position).getMessage());
        if(i==1)
            holder.tvmessage1.setText(messageModelArrayList.get(position).getMessage());
*/

        return convertView;
    }

    private class ViewHolder {

        protected TextView tvmessage,tvmessage1;
        LinearLayout sender,recevier;


    }
}
