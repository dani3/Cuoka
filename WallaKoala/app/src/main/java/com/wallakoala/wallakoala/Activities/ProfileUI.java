package com.wallakoala.wallakoala.Activities;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.wallakoala.wallakoala.R;

/**
 * Pantalla del perfil del usuario.
 * Created by Daniel Mancebo Aldea on 19/10/2016.
 */

public class ProfileUI extends AppCompatActivity
{
    protected CollapsingToolbarLayout mCollapsingToolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.profile_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        mCollapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.profile_collapsing_layout);
    }
}
