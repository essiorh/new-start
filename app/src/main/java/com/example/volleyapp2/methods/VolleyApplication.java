package com.example.volleyapp2.methods;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * This method needs for connection to web sites
 * @see com.android.volley.RequestQueue
 * @see com.android.volley.toolbox.Volley;
 * @author ilia
 */
public class VolleyApplication extends Application {
    private static VolleyApplication sInstance;

    private RequestQueue mRequestQueue;

    @Override
    public void onCreate() {
        super.onCreate();

        mRequestQueue = Volley.newRequestQueue(this);

        sInstance = this;
    }

    public synchronized static VolleyApplication getInstance() {
        return sInstance;
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }
}
