package project.projectapp.NewsFragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import java.util.Collections;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import project.projectapp.NewsFragment.Comments.CommentsActivity;
import project.projectapp.R;

public class NewsRecyclerViewAdapter extends RecyclerView.Adapter<NewsRecyclerViewAdapter.ViewHolder>{

    private ArrayList<String> images, usernames, dates, times, titles, contents, postIds,
            commentCounts;
    private Map<String, Integer> postIdsAndCommentCount;

    private Context context;

    public NewsRecyclerViewAdapter(Context thisContext, ArrayList<String> image,
                                   ArrayList<String> username, ArrayList<String> date,
                                   ArrayList<String> time, ArrayList<String> title,
                                   ArrayList<String> content, ArrayList<String> postId,
                                   Map<String, Integer> commentCount){
        context = thisContext;
        images = image;
        usernames = username;
        dates = date;
        times = time;
        titles = title;
        contents = content;
        postIds = postId;
        postIdsAndCommentCount = commentCount;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_news_list_item, parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    private void toComments(String id){
        Intent comments = new Intent(context, CommentsActivity.class);
        comments.putExtra("callingPost", id);
        context.startActivity(comments);
        ((Activity) context).overridePendingTransition(R.anim.slide_in_from_bottom,
                R.anim.slide_out_from_bottom);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Glide.with(context)
                .load(images.get(position))
                .into(holder.profileImage);

        holder.profileUsername.setText(usernames.get(position));
        holder.postDate.setText(dates.get(position));
        holder.postTime.setText(times.get(position));
        holder.postTitle.setText(titles.get(position));
        holder.postContent.setText(contents.get(position));
        holder.postContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Used to open and close the text
                if(holder.postContent.getMaxLines() == 4){
                    holder.postContent.setMaxLines(Integer.MAX_VALUE);
                } else {
                    holder.postContent.setMaxLines(4);
                }
            }
        });

        holder.comments.setText("COMMENTS ("
                + postIdsAndCommentCount.get(postIds.get(position)).toString()
                + ")");

        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toComments(postIds.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return usernames.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder{

        RelativeLayout parentLayout;
        CircleImageView profileImage;
        TextView profileUsername;
        TextView postDate;
        TextView postTime;
        TextView postTitle;
        TextView postContent;
        Button comments;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            parentLayout = itemView.findViewById(R.id.news_article_parent);
            profileImage = itemView.findViewById(R.id.news_article_layout_profile_image);
            profileUsername = itemView.findViewById(R.id.news_article_layout_poster);
            postDate = itemView.findViewById(R.id.news_article_layout_date);
            postTime = itemView.findViewById(R.id.news_article_layout_time);
            postTitle = itemView.findViewById(R.id.news_article_layout_title);
            postContent = itemView.findViewById(R.id.news_article_layout_content);
            comments = itemView.findViewById(R.id.news_article_comments);
        }
    }
}
