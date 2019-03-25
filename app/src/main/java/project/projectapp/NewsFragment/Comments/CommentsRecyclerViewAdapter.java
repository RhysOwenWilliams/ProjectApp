package project.projectapp.NewsFragment.Comments;

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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import project.projectapp.R;

public class CommentsRecyclerViewAdapter extends RecyclerView.Adapter<CommentsRecyclerViewAdapter.ViewHolder>{

    private ArrayList<String> usernames, userProfileImages, userComments, commentDates, commentTimes;

    private Context context;

    public CommentsRecyclerViewAdapter(Context context, ArrayList<String> usernames,
                                       ArrayList<String> userProfileImages,
                                       ArrayList<String> userComments, ArrayList<String> commentDates,
                                       ArrayList<String> commentTimes){
        this.context = context;
        this.usernames = usernames;
        this.userProfileImages = userProfileImages;
        this.userComments = userComments;
        this.commentDates = commentDates;
        this.commentTimes = commentTimes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_comment_list, parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.username.setText(usernames.get(position));
        Glide.with(context)
                .load(userProfileImages.get(position))
                .into(holder.profileImage);
        holder.comment.setText(userComments.get(position));
        holder.timeAndDate.setText(" - " + commentTimes.get(position)
                + " " + commentDates.get(position));
    }

    @Override
    public int getItemCount() {
        return usernames.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout parentLayout;
        TextView username;
        CircleImageView profileImage;
        TextView comment;
        TextView timeAndDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            parentLayout = itemView.findViewById(R.id.comments_list_parent);
            username = itemView.findViewById(R.id.comments_poster_username);
            profileImage = itemView.findViewById(R.id.comments_poster_profile_image);
            comment = itemView.findViewById(R.id.comments_poster_content);
            timeAndDate = itemView.findViewById(R.id.comments_date_time);
        }
    }
}
