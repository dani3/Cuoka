package com.wallakoala.wallakoala.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.wallakoala.wallakoala.R;

/**
 * Created by Dani on 11/02/2016.
 */

public class FilterUI extends AppCompatActivity
{
    protected Toolbar mToolbar;

    protected TextView mToolbarTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.filter);

        _initToolbar();
    }

    protected void _initToolbar()
    {
        mToolbar = (Toolbar)findViewById(R.id.filter_appbar);
        mToolbarTextView = (TextView)findViewById(R.id.toolbar_textview);

        mToolbarTextView.setText("Filtros");

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflamos la ActionBar
        getMenuInflater().inflate(R.menu.toolbar_menu_filter, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            super.onBackPressed();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish(){
        super.finish();

        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }
}
