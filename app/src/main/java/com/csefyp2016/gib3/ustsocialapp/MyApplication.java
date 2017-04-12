package com.csefyp2016.gib3.ustsocialapp;


import android.app.Application;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class MyApplication extends Application {

    private Socket mSocket;

    {
        try {
            mSocket = IO.socket("http://ec2-52-221-30-8.ap-southeast-1.compute.amazonaws.com:3120");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public Socket getSocket() {
        return mSocket;
    }

}
