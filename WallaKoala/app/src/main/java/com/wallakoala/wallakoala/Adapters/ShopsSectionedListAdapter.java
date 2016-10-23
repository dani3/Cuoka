package com.wallakoala.wallakoala.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.sectionedrecyclerview.SectionedRecyclerViewAdapter;

/**
 * Adapter con para mostrar la lista de tiendas con secciones.
 * Created by Daniel Mancebo Aldea on 23/10/2016.
 */

public class ShopsSectionedListAdapter extends SectionedRecyclerViewAdapter<ShopsSectionedListAdapter.ShopHolder>
{
    /* [BEGIN ViewHolder] */
    public static class ShopHolder extends RecyclerView.ViewHolder
    {
        public ShopHolder(View itemView)
        {
            super(itemView);
        }

    } /* [END ViewHolder] */

    public ShopsSectionedListAdapter()
    {

    }

    @Override
    public int getSectionCount()
    {
        return 0;
    }

    @Override
    public int getItemCount(int section)
    {
        return 0;
    }

    @Override
    public void onBindHeaderViewHolder(ShopHolder holder, int section)
    {

    }

    @Override
    public void onBindViewHolder(ShopHolder holder, int section, int relativePosition, int absolutePosition)
    {

    }

    @Override
    public ShopHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return null;
    }
}
