package com.sujanlama.yipl_android_list_me.RecylerViewAdapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sujanlama.yipl_android_list_me.R;
import com.sujanlama.yipl_android_list_me.helper.PostHelper;

import java.util.ArrayList;



/**
 * Created by DEll on 11/26/2017.
 */

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private ArrayList<PostHelper> list;
    private Context context;


    public PostAdapter(ArrayList<PostHelper> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item,parent,false);
        return new PostViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {

        PostHelper helper = list.get(position);
        holder.id.setText(helper.getId() + "   ");
        holder.title.setText(helper.getTitle());
        holder.body.setText(helper.getBody());
        holder.userid.setText("Post by User Id "+helper.getUserId());




    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    class PostViewHolder extends RecyclerView.ViewHolder{

        public TextView id,title,body,userid;

        public PostViewHolder(View v) {
            super(v);
            id = v.findViewById(R.id.id);
            title = v.findViewById(R.id.title);
            body = v.findViewById(R.id.body);
            userid = v.findViewById(R.id.userid);


        }


    }
}
