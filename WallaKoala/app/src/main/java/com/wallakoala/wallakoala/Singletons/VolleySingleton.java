package com.wallakoala.wallakoala.Singletons;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.wallakoala.wallakoala.Properties.Properties;

/**
 * @class Singleton para manejar una instancia de Volley.
 * Created by Daniel Mancebo Aldea on 31/05/2016.
 */

public class VolleySingleton
{
    private static VolleySingleton mInstance;
    private static Context mContext;
    private RequestQueue mRequestQueue;

    private VolleySingleton(final Context context)
    {
        mContext = context;

        mRequestQueue = getRequestQueue();
    }

    public static synchronized VolleySingleton getInstance(Context context)
    {
        if (mInstance == null)
        {
            mInstance = new VolleySingleton(context);
        }

        return mInstance;
    }

    public RequestQueue getRequestQueue()
    {
        if (mRequestQueue == null)
        {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req)
    {
        req.setRetryPolicy(new DefaultRetryPolicy(
                Properties.REQUEST_TIMEOUT,
                0,
                0));

        getRequestQueue().add(req);
    }
}
