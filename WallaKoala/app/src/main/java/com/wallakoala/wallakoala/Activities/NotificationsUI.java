package com.wallakoala.wallakoala.Activities;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.wallakoala.wallakoala.R;

/**
 * Pantalla donde se muestran las notificaciones.
 * Created by Daniel Mancebo on 25/11/2016.
 */

public class NotificationsUI extends AppCompatActivity
{
    /* ContainerViews */
    private CoordinatorLayout mCoordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_notifications);

        _initToolbar();
    }

    /**
     * Inicializacion de la Toolbar.
     */
    private void _initToolbar()
    {
        Toolbar toolbar = (Toolbar)findViewById(R.id.notifications_toolbar);
        TextView toolbarTextView = (TextView)findViewById(R.id.toolbar_textview);

        toolbarTextView.setText(getResources().getString(R.string.toolbar_notifications));

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        }
    }
}
