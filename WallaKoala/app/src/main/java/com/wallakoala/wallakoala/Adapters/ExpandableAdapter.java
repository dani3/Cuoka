package com.wallakoala.wallakoala.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wallakoala.wallakoala.R;
import com.wallakoala.wallakoala.Views.AnimatedExpandableListView;

import java.util.ArrayList;
import java.util.List;

/**
 * @class Adapter especifico para el expandable del drawer derecho
 * Created by Daniel Mancebo Aldea on 21/11/2015.
 */

public class ExpandableAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter
{
    private LayoutInflater inflater;
    private List<GroupItem> items;

    /*
     * Atributos de la cabecera
     */
    public static class GroupItem
    {
        public String header;
        public List<ChildItem> items = new ArrayList<>();
    }

    /*
     * Atributos de los items de cada cabecera
     */
    public static class ChildItem
    {
        public String title;
    }

    /*
     * Views que forman los items de cada cabecera
     */
    private static class ChildHolder
    {
        TextView title;
    }

    /*
     * Views que forman la cabecera
     */
    private static class GroupHolder
    {
        TextView title;
    }

    public ExpandableAdapter(Context context)
    {
        inflater = LayoutInflater.from(context);
    }

    public void setData(List<GroupItem> items)
    {
        this.items = items;
    }

    @Override
    public ChildItem getChild(int groupPosition, int childPosition)
    {
        return items.get(groupPosition).items.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        return childPosition;
    }

    @Override
    public View getRealChildView(int groupPosition
                    , int childPosition
                    , boolean isLastChild
                    , View convertView
                    , ViewGroup parent)
    {
        ChildHolder holder;
        ChildItem item = getChild(groupPosition, childPosition);

        if (convertView == null)
        {
            holder = new ChildHolder();
            convertView = inflater.inflate(R.layout.right_navigation_drawer_item, parent, false);
            holder.title = (TextView) convertView.findViewById(R.id.item_navigation_drawer);
            convertView.setTag(holder);

        } else
            holder = (ChildHolder) convertView.getTag();

        holder.title.setText(item.title);

        return convertView;
    }

    @Override
    public int getRealChildrenCount(int groupPosition)
    {
        return items.get(groupPosition).items.size();
    }

    @Override
    public GroupItem getGroup(int groupPosition)
    {
        return items.get(groupPosition);
    }

    @Override
    public int getGroupCount()
    {
        return items.size();
    }

    @Override
    public long getGroupId(int groupPosition)
    {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition
                    , boolean isExpanded
                    , View convertView
                    , ViewGroup parent)
    {
        GroupHolder holder;
        GroupItem item = getGroup(groupPosition);

        if (convertView == null)
        {
            holder = new GroupHolder();
            convertView = inflater.inflate(R.layout.right_navigation_drawer_header, parent, false);
            holder.title = (TextView) convertView.findViewById(R.id.header_navigation_drawer);
            convertView.setTag(holder);

        } else
            holder = (GroupHolder) convertView.getTag();

        holder.title.setText(item.header);

        return convertView;
    }

    @Override
    public boolean hasStableIds()
    {
        return true;
    }

    @Override
    public boolean isChildSelectable(int arg0, int arg1)
    {
        return true;
    }
}