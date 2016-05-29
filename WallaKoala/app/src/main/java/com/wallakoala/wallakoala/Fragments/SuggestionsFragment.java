package com.wallakoala.wallakoala.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wallakoala.wallakoala.R;

/**
 * @class
 * Created by Dani on 29/05/2016.
 */

public class SuggestionsFragment extends Fragment
{
    public SuggestionsFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.suggestions_tab, container, false);
    }
}
