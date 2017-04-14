package com.csefyp2016.gib3.ustsocialapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class IndividualChat extends AppCompatActivity {

    private final static String TAG = "InstantChatRoom";
    private static final String profilePreference = "ProfilePreference";
    private static final String loginPreference = "LoginPreference";

    private Socket mSocket;
    private List<Message> mMessages = new ArrayList<>();
    private RecyclerView.Adapter mAdapter;
    private RecyclerView mMessagesView;
    private EditText mInputMessageView;
    private String id;
    private String mUsername = "user name";
    private String mTheFriendId;
    private String mTheFriendName;

    private SharedPreferences sharedPreferences;

    private DBHandler dbHandler;
    private String lastLogIndex;

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }
        mTheFriendId = extras.getString("the_friend_id");
        mTheFriendName = extras.getString("the_friend_name");

        sharedPreferences = getSharedPreferences(loginPreference, Context.MODE_PRIVATE);
        id = sharedPreferences.getString("ID", null);

        sharedPreferences = getSharedPreferences(profilePreference, Context.MODE_PRIVATE);
        mUsername = sharedPreferences.getString("DISPLAYNAME", null);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.title_activity_individual_chat_room));
        toolbar.setSubtitle(mTheFriendName);
        setSupportActionBar(toolbar);

        mAdapter = new MessageAdapter(this, mMessages);
        mMessagesView = (RecyclerView) findViewById(R.id.messages_chat);
        mMessagesView.setLayoutManager(new LinearLayoutManager(this));
        mMessagesView.setAdapter(mAdapter);

        mInputMessageView = (EditText) findViewById(R.id.message_chat_input);
        mInputMessageView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int id, KeyEvent event) {
                if (id == R.id.send || id == EditorInfo.IME_NULL) {
                    attemptSend();
                    return true;
                }
                return false;
            }
        });

        dbHandler = new DBHandler(this, getTheIndividualTableName(id, mTheFriendId));
        List<Message> previousMessage = dbHandler.getMessage();
        if (previousMessage != null) {
            Message message = previousMessage.get(0);
            lastLogIndex = message.getMessage();
            for (int i = 1; i < previousMessage.size(); i++) {
                message = previousMessage.get(i);
                addMessage(message.getUsername(), message.getMessage());
            }
        }

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                swipeRefresh();
            }
        });

        ImageButton sendButton = (ImageButton) findViewById(R.id.b_chat_send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSend();
            }
        });

        MyApplication app = (MyApplication) getApplication();
        mSocket = app.getSocket();
        mSocket.on(Socket.EVENT_CONNECT, onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.on("new message", onNewMessage);
        mSocket.on("user joined", onUserJoined);
        mSocket.on("user left", onUserLeft);
        mSocket.connect();
    }

    private void swipeRefresh() {
        if (lastLogIndex != "1") {
            List<Message> messagesNew = new ArrayList<>();
            List<Message> previousMessage = dbHandler.getMessage(lastLogIndex);
            if (previousMessage != null) {
                Message message = previousMessage.get(0);
                lastLogIndex = message.getMessage();
                //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //
                System.out.println("Last index is: " + lastLogIndex);
                //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //
                for (int i = 1; i < previousMessage.size(); i++) {
                    message = previousMessage.get(i);
                    messagesNew.add(new Message.Builder(0).username(message.getUsername()).message(message.getMessage()).build());
                }
            }
            messagesNew.addAll(mMessages);
            //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //
            System.out.println("Total number of Messages: " + messagesNew.size());
            //  --------------------------------------------------------------- Debug , To be deleted  --------------------------------------------------------------- //
            mMessages = messagesNew;
            mAdapter = new MessageAdapter(this, mMessages);
            mMessagesView.setAdapter(mAdapter);
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();

        mSocket.off(Socket.EVENT_CONNECT, onConnect);
        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.off("new message", onNewMessage);
        mSocket.off("user joined", onUserJoined);
        mSocket.off("user left", onUserLeft);
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (null != mUsername) {
                        mSocket.emit("add user", getTheIndividualRoomName(id, mTheFriendId), mUsername);
                    }
//                        Toast.makeText(this,
//                                R.string.connect, Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "diconnected");
//                    Toast.makeText(this,
//                            R.string.disconnect, Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    String message;
                    try {
                        username = data.getString("username");
                        message = data.getString("message");
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                        return;
                    }
                    addMessage(username, message);
                    dbHandler.addMessage(username, message);
                }
            });
        }
    };

    private Emitter.Listener onUserJoined = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    int numUsers;
                    try {
                        username = data.getString("username");
                        numUsers = data.getInt("numUsers");
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                        return;
                    }

                    //addLog(getResources().getString(R.string.message_user_joined, username));
                    //addParticipantsLog(numUsers);
                }
            });
        }
    };

    private Emitter.Listener onUserLeft = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    int numUsers;
                    try {
                        username = data.getString("username");
                        numUsers = data.getInt("numUsers");
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                        return;
                    }
                    //addLog(getResources().getString(R.string.message_user_left, username));
                    //addParticipantsLog(numUsers);
                }
            });
        }
    };

    private void attemptSend() {
        if (null == mUsername) return;
        if (!mSocket.connected()) return;

        String message = mInputMessageView.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            mInputMessageView.requestFocus();
            return;
        }

        mInputMessageView.setText("");
        addMessage(mUsername, message);
        dbHandler.addMessage(mUsername, message);

        // perform the sending message attempt.
        mSocket.emit("new message", message);
    }

    private void addMessage(String username, String message) {
        mMessages.add(new Message.Builder(Message.TYPE_MESSAGE)
                .username(username).message(message).build());
        mAdapter.notifyItemInserted(mMessages.size() - 1);
        scrollToBottom();
    }

    private void addLog(String message) {
        mMessages.add(new Message.Builder(Message.TYPE_LOG)
                .message(message).build());
        mAdapter.notifyItemInserted(mMessages.size() - 1);
        scrollToBottom();
    }

    private void addParticipantsLog(int numUsers) {
        addLog(getResources().getQuantityString(R.plurals.message_participants, numUsers, numUsers));
    }

    private void scrollToBottom() {
        mMessagesView.scrollToPosition(mAdapter.getItemCount() - 1);
    }

    private String getTheIndividualRoomName(String name, String name2) {
        List<String> nameList = new ArrayList<>();
        nameList.add(name);
        nameList.add(name2);
        Collections.sort(nameList);
        return nameList.get(0) + "+" + nameList.get(1);
    }

    private String getTheIndividualTableName(String name, String name2) {
        List<String> nameList = new ArrayList<>();
        nameList.add(name);
        nameList.add(name2);
        Collections.sort(nameList);
        return nameList.get(0) + "00" + nameList.get(1);
    }



}
