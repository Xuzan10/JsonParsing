package com.sujanlama.yipl_android_list_me.RecylerViewAdapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sujanlama.yipl_android_list_me.R;
import com.sujanlama.yipl_android_list_me.helper.CommentHelper;
import com.sujanlama.yipl_android_list_me.helper.PostHelper;

import java.util.ArrayList;


/**
 * Created by DEll on 11/26/2017.
 */

public class ViewCommentAdapter extends RecyclerView.Adapter<ViewCommentAdapter.CommentViewHolder> {

    private ArrayList<CommentHelper> list;
    private Context context;


    public ViewCommentAdapter(ArrayList<CommentHelper> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item,parent,false);
        return new CommentViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {

        CommentHelper helper = list.get(position);
        holder.id.setText(helper.getId() + "   ");
        holder.name.setText(helper.getName());
        holder.email.setText(helper.getEmail());
        holder.body.setText(helper.getBody());




    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    class CommentViewHolder extends RecyclerView.ViewHolder{

        public TextView id,name,email,body;

        public CommentViewHolder(View v) {
            super(v);

            id=v.findViewById(R.id.id);
            name = v.findViewById(R.id.name);
            email=v.findViewById(R.id.email);
            body=v.findViewById(R.id.body);



        }


    }
}
