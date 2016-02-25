package com.wallakoala.wallakoala.Activities;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Dani on 25/02/2016.
 */

public class SearchActivity extends AppCompatActivity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        handleIntent(getIntent());
    }

    public void onNewIntent(Intent intent)
    {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent)
    {
        if (Intent.ACTION_SEARCH.equals(intent.getAction()))
        {
            String query =
                    intent.getStringExtra(SearchManager.QUERY);

            doSearch(query);
        }
    }

    private void doSearch(String queryStr)
    {

    }
}
