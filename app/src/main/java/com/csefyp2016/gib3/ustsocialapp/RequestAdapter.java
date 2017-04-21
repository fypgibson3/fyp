package com.csefyp2016.gib3.ustsocialapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {

    private List<Request> mRequests;

    private static final String acceptURL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/acceptFdRequest.php";
    private static final String declineURL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/declineFdRequest.php";
    private static final String getFdIdListURL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/getFriendIdList.php";
    private static final String getFdDisplayNameListURL = "http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com/getFriendDisplayNameList.php";
    private RequestQueue requestQueue;
    private StringRequest request;

    private static final String loginPreference = "LoginPreference";
    private static final String friendListPreference = "FriendList";
    private String id;
    private SharedPreferences sharedPreferencesLogin;
    private SharedPreferences sharedPreferencesFriend;

    public RequestAdapter (Context context, List<Request> requests) {
        requestQueue = Volley.newRequestQueue(context);

        sharedPreferencesLogin = context.getSharedPreferences(loginPreference, Context.MODE_PRIVATE);
        id = sharedPreferencesLogin.getString("ID", null);

        sharedPreferencesFriend = context.getSharedPreferences(friendListPreference, Context.MODE_PRIVATE);

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
        holder.setmRequestAcceptListener(request.getFriendId(), request.getFriendName(), position);
        holder.setmRequestDeclineListener(request.getFriendId(), request.getFriendName(), position);
    }

    @Override
    public int getItemCount() {
        return mRequests.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mRequestContent;
        private Button mRequestAccept;
        private Button mRequestDecline;

        public ViewHolder(View itemView) {
            super(itemView);

            mRequestContent = (TextView) itemView.findViewById(R.id.view_friendRequest_title);
            mRequestAccept = (Button) itemView.findViewById(R.id.b_friendRequest_accpet);
            mRequestDecline = (Button) itemView.findViewById(R.id.b_friendRequest_decline);
        }

        public void setmRequestContent(String content) {
            if (null == mRequestContent) return;
            mRequestContent.setText(content);
        }

        public void setmRequestAcceptListener(final String fdId, final String fdName, final int position) {
            mRequestAccept.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    request = new StringRequest(com.android.volley.Request.Method.POST, acceptURL, new Response.Listener<String>() {

                        @Override
                        // Response to request result
                        public void onResponse(String response) {
                            if (response.contains("Success")) {
                                removeFromSharedPreference("friendRequestList_ID", fdId);
                                setFriendListIdPreference();
                                mRequests.remove(getAdapterPosition());
                                notifyItemRemoved(getAdapterPosition());
                                notifyItemRangeChanged(getAdapterPosition(), mRequests.size());

                                String name = fdName.split(" ")[0];
                                Toast.makeText(view.getContext(), "You and " + name + " are friends now!", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                System.out.println("Accept failed");
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }) {
                        @Override
                        // Post request parameters
                        protected Map<String, String> getParams() throws AuthFailureError {
                            HashMap<String, String> hashMap = new HashMap<String, String>();
                            hashMap.put("id", id);
                            hashMap.put("fdId", fdId);
                            return hashMap;
                        }
                    };
                    // Put the request to the queue
                    requestQueue.add(request);
                }
            });
        }

        public void setmRequestDeclineListener(final String fdId, final String fdName, final int position) {
            mRequestDecline.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    request = new StringRequest(com.android.volley.Request.Method.POST, declineURL, new Response.Listener<String>() {

                        @Override
                        // Response to request result
                        public void onResponse(String response) {
                            if (response.contains("Success")) {
                                removeFromSharedPreference("friendRequestList_ID", fdId);
                                mRequests.remove(getAdapterPosition());
                                notifyItemRemoved(getAdapterPosition());
                                notifyItemRangeChanged(getAdapterPosition(), mRequests.size());

                                String name = fdName.split(" ")[0];
                                Toast.makeText(view.getContext(), "You have declined the friend request from " + name, Toast.LENGTH_SHORT).show();
                            }
                            else {
                                System.out.println("Decline failed");
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }) {
                        @Override
                        // Post request parameters
                        protected Map<String, String> getParams() throws AuthFailureError {
                            HashMap<String, String> hashMap = new HashMap<String, String>();
                            hashMap.put("id", id);
                            hashMap.put("fdId", fdId);
                            return hashMap;
                        }
                    };
                    // Put the request to the queue
                    requestQueue.add(request);
                }
            });
        }

        private void removeFromSharedPreference(String preference, String content) {
            SharedPreferences.Editor editor = sharedPreferencesFriend.edit();
            String list = sharedPreferencesFriend.getString(preference, null);
            if (list != null) {
                String newList = "";
                String[] arrayList = list.split(",");
                for (int i = 0; i < arrayList.length; i++) {
                    if (!arrayList[i].equals(content)) {
                        if (i == 0 || newList == "")
                            newList = arrayList[i];
                        else
                            newList = newList + "," + arrayList[i];
                    }
                }

                System.out.println(newList);

                if (newList == "") {
                    editor.remove(preference);
                }
                else {
                    editor.putString(preference, newList);
                }
                editor.commit();
            }
        }

        private void setFriendListIdPreference () {
            request = new StringRequest(com.android.volley.Request.Method.POST, getFdIdListURL, new Response.Listener<String>() {

                @Override
                // Response to request result
                public void onResponse(String response) {
                    if (response.contains("Success")) {

                        //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //
                        System.out.println(response);
                        //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //

                        String fdList = response.split(":")[1];
                        //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //
                        System.out.println(fdList);
                        //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //

                        SharedPreferences.Editor fdIdListEditor = sharedPreferencesFriend.edit();
                        fdIdListEditor.putString("FDLIST_ID", fdList);
                        fdIdListEditor.commit();

                        setFriendListDisplayNamePreference(fdList);
                    } else {
                        SharedPreferences.Editor editor = sharedPreferencesFriend.edit();
                        editor.putString("FDLIST_ID", null);
                        editor.putString("FDLIST_DISPLAYNAME", null);
                        editor.commit();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {
                @Override
                // Post request parameters
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> hashMap = new HashMap<String, String>();
                    hashMap.put("id", id);
                    return hashMap;
                }
            };
            // Put the request to the queue
            requestQueue.add(request);
        }

        private void setFriendListDisplayNamePreference(final String idList) {
            request = new StringRequest(com.android.volley.Request.Method.POST, getFdDisplayNameListURL, new Response.Listener<String>() {

                @Override
                // Response to request result
                public void onResponse(String response) {
                    if (response.contains("Success")) {
                        //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //
                        System.out.println(response);
                        //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //

                        String fdList = response.split(":")[1];
                        //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //
                        System.out.println(fdList);
                        //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //

                        SharedPreferences.Editor fdNameListEditor = sharedPreferencesFriend.edit();
                        fdNameListEditor.putString("FDLIST_DISPLAYNAME", fdList);
                        fdNameListEditor.commit();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {
                @Override
                // Post request parameters
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> hashMap = new HashMap<String, String>();
                    hashMap.put("list", idList);
                    return hashMap;
                }
            };
            // Put the request to the queue
            requestQueue.add(request);
        }


    }
}
