package com.csefyp2016.gib3.ustsocialapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {
    private List<Request> mRequests;

    public RequestAdapter (Context context, List<Request> requests) {
        mRequests = requests;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = R.layout.friend_request_layout;

        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Request request = mRequests.get(position);
        holder.setmRequestContent(request.getFriendName());
        //holder.setmRequestContent();
    }

    @Override
    public int getItemCount() {
        return mRequests.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mRequestContent;

        public ViewHolder(View itemView) {
            super(itemView);

            mRequestContent = (TextView) itemView.findViewById(R.id.view_friendRequest_title);
        }

        public void setmRequestContent(String content) {
            if (null == mRequestContent) return;
            mRequestContent.setText(content);
        }

    }
}
