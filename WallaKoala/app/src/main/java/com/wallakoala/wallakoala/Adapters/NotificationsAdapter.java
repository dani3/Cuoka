package com.wallakoala.wallakoala.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Adapter que muestra los distintos tipos de notificaciones.
 * Created by Dani on 25/11/2016.
 */

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationHolder>
{

    public class NotificationHolder extends RecyclerView.ViewHolder
    {

        public NotificationHolder(View itemView)
        {
            super(itemView);
        }
    }

    @Override
    public int getItemViewType(int position)
    {
        return 0;
    }

    @Override
    public NotificationHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return null;
    }

    @Override
    public void onBindViewHolder(NotificationHolder holder, int position)
    {

    }

    @Override
    public int getItemCount()
    {
        return 0;
    }
}
