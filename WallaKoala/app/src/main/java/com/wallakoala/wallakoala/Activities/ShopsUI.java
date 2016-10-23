package com.wallakoala.wallakoala.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import com.wallakoala.wallakoala.R;

/**
 * Pantalla que muestra las tiendas del usuario y las disponibles.
 * Created by Daniel Mancebo Aldea on 23/10/2016.
 */

public class ShopsUI extends AppCompatActivity
{
    /* RecyclerView */
    protected RecyclerView mShopsRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_shops);

        _initRecycerlView();
    }

    private void _initRecycerlView()
    {
        mShopsRecyclerView = (RecyclerView)findViewById(R.id.my_shops_recyclerview);
    }
}
