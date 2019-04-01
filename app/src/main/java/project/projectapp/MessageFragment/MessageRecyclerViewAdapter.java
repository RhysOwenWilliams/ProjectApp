package project.projectapp.MessageFragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import project.projectapp.R;

public class MessageRecyclerViewAdapter extends RecyclerView.Adapter<MessageRecyclerViewAdapter.ViewHolder>{

    private ArrayList<String> messageBody, messageDate, messageTime, messageSender, messageSenderId;

    private Context context;

    private FirebaseAuth firebaseAuth;

    public MessageRecyclerViewAdapter(Context context, ArrayList<String> messageBody,
                                      ArrayList<String> messageDate, ArrayList<String> messageTime,
                                      ArrayList<String> messageSender,
                                      ArrayList<String> messageSenderId){
        this.context = context;
        this.messageBody = messageBody;
        this.messageDate = messageDate;
        this.messageTime = messageTime;
        this.messageSender = messageSender;
        this.messageSenderId = messageSenderId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_message_list, parent,false);
        ViewHolder holder = new ViewHolder(view);

        firebaseAuth = FirebaseAuth.getInstance();
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.message.setText(messageBody.get(position));
        holder.userMessage.setText(messageBody.get(position));

        holder.date.setText(messageDate.get(position));
        holder.userDate.setText(messageDate.get(position));

        holder.time.setText(messageTime.get(position));
        holder.userTime.setText(messageTime.get(position));

        holder.username.setText(messageSender.get(position));
        holder.userUsername.setText(messageSender.get(position));

        if(firebaseAuth.getUid().equals(messageSenderId.get(position))){
            holder.isUser.setVisibility(View.VISIBLE);
        } else {
            holder.notUser.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return messageBody.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder{

        LinearLayout notUser;
        LinearLayout isUser;
        TextView message;
        TextView userMessage;
        TextView date;
        TextView userDate;
        TextView time;
        TextView userTime;
        TextView username;
        TextView userUsername;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            notUser = itemView.findViewById(R.id.message_list_not_user);
            isUser = itemView.findViewById(R.id.message_list_user);
            message = itemView.findViewById(R.id.message_list_not_user_body);
            userMessage = itemView.findViewById(R.id.message_list_user_body);
            date = itemView.findViewById(R.id.message_list_not_user_date);
            userDate = itemView.findViewById(R.id.message_list_user_date);
            time = itemView.findViewById(R.id.message_list_not_user_time);
            userTime = itemView.findViewById(R.id.message_list_user_time);
            username = itemView.findViewById(R.id.message_list_not_user_username);
            userUsername = itemView.findViewById(R.id.message_list_user_username);
        }
    }
}
