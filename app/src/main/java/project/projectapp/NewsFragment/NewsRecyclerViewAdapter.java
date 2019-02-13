package project.projectapp.NewsFragment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import project.projectapp.R;

public class NewsRecyclerViewAdapter extends RecyclerView.Adapter<NewsRecyclerViewAdapter.ViewHolder>{

    private ArrayList<String> images, usernames, dates, times, titles, contents;

    private Context context;

    public NewsRecyclerViewAdapter(Context thisContext, ArrayList<String> image,
                                   ArrayList<String> username, ArrayList<String> date,
                                   ArrayList<String> time, ArrayList<String> title,
                                   ArrayList<String> content){
        context = thisContext;
        images = image;
        usernames = username;
        dates = date;
        times = time;
        titles = title;
        contents = content;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_news_list_item, parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            parentLayout = itemView.findViewById(R.id.news_article_parent);
            profileImage = itemView.findViewById(R.id.news_article_layout_profile_image);
            profileUsername = itemView.findViewById(R.id.news_article_layout_poster);
            postDate = itemView.findViewById(R.id.news_article_layout_date);
            postTime = itemView.findViewById(R.id.news_article_layout_time);
            postTitle = itemView.findViewById(R.id.news_article_layout_title);
            postContent = itemView.findViewById(R.id.news_article_layout_content);
        }
    }
}
