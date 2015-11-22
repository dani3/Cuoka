package com.wallakoala.wallakoala.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.wallakoala.wallakoala.R;

import java.util.HashMap;
import java.util.List;

/**
 * @class Adapter especifico para el expandable del drawer derecho
 * Created by Daniel Mancebo Aldea on 21/11/2015.
 */

public class ExpandableAdapter extends BaseExpandableListAdapter
{
    private Context mContext;
    private List<String> headerList;
    private HashMap<String, List<String>> childList;

    public ExpandableAdapter( Context context
                    , List<String> listDataHeader
                    , HashMap<String
                    , List<String>> listChildData )
    {
        mContext = context;
        headerList = listDataHeader;
        childList = listChildData;
    }

    @Override
    public Object getChild( int groupPosition, int childPosititon )
    {
        return this.childList.get( this.headerList.get( groupPosition ) )
                   .get( childPosititon );
    }

    @Override
    public long getChildId( int groupPosition, int childPosition )
    {
        return childPosition;
    }

    @Override
    public View getChildView( int groupPosition
                    , final int childPosition
                    , boolean isLastChild
                    , View convertView
                    , ViewGroup parent )
    {
        final String childText = ( String )getChild( groupPosition, childPosition );

        if ( convertView == null )
        {
            LayoutInflater infalInflater = ( LayoutInflater )this.mContext
                    .getSystemService( Context.LAYOUT_INFLATER_SERVICE );

            convertView = infalInflater.inflate( R.layout.right_navigation_drawer_item, null );
        }

        TextView txtListChild = ( TextView )convertView
                .findViewById( R.id.item_navigation_drawer );

        txtListChild.setText( childText );

        return convertView;
    }

    @Override
    public View getGroupView(int groupPosition
                    , boolean isExpanded
                    , View convertView
                    , ViewGroup parent)
    {
        String headerTitle = ( String )getGroup( groupPosition );

        if ( convertView == null )
        {
            LayoutInflater infalInflater = ( LayoutInflater )this.mContext
                    .getSystemService( Context.LAYOUT_INFLATER_SERVICE );

            convertView = infalInflater.inflate( R.layout.right_navigation_drawer_header, null );
        }

        TextView lblListHeader = ( TextView )convertView
                .findViewById( R.id.header_navigation_drawer );
        lblListHeader.setText( headerTitle );

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition)
    {
        return this.childList.get(this.headerList.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition)
    {
        return this.headerList.get(groupPosition);
    }

    @Override
    public int getGroupCount()
    {
        return this.headerList.size();
    }

    @Override
    public long getGroupId(int groupPosition)
    {
        return groupPosition;
    }

    @Override
    public boolean hasStableIds()
    {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return true;
    }
}
