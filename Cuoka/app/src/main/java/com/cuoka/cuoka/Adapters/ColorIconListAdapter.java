package com.cuoka.cuoka.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;

import com.squareup.picasso.Picasso;
import com.cuoka.cuoka.Beans.ColorVariant;
import com.cuoka.cuoka.R;
import com.cuoka.cuoka.Utils.Utils;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Adapter de la lista de iconos
 * Created by Daniel Mancebo Aldea on 04/02/2016.
 */

public class ColorIconListAdapter extends BaseAdapter
{
    /* Context */
    private Context mContext;

    /* Inflater */
    private LayoutInflater mLayoutInflater;

    /* Data */
    private List<ColorVariant> mColorList;
    private String mShop;
    private String mSection;
    private int mIconSelected;

    /**
     * ViewHolder del icono.
     */
    private static class ColorIconHolder
    {
        private CircleImageView mIconView;
        private CircleImageView mSelectedView;
    }

    /**
     * Constructor del adapter.
     * @param context: contexto (ProductUI).
     * @param colorVariants: lista de ColorVariants.
     */
    public ColorIconListAdapter(final Context context
                    , List<ColorVariant> colorVariants
                    , String shop
                    , String section)
    {
        mContext = context;
        mColorList = colorVariants;
        mSection = section;
        mShop = shop;
        mIconSelected = 0;
    }

    @Override
    public int getCount()
    {
        return this.mColorList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mColorList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ColorIconHolder colorIconHolder;

        // Creamos el inflater si no esta creado
        if (mLayoutInflater == null)
        {
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        // Inflamos el item
        if (convertView == null)
        {
            colorIconHolder = new ColorIconHolder();

            convertView = mLayoutInflater.inflate(R.layout.aux_color_icon, null);

            colorIconHolder.mIconView     = (CircleImageView)convertView.findViewById(R.id.color_icon);
            colorIconHolder.mSelectedView = (CircleImageView)convertView.findViewById(R.id.color_selected);

            convertView.setTag(colorIconHolder);

        } else {
            colorIconHolder = (ColorIconHolder)convertView.getTag();
        }

        // Obtenemos la url del icono
        String url = Utils.getColorUrl(mColorList.get(position), mShop, mSection);

        if (position == mIconSelected)
        {
            colorIconHolder.mSelectedView.setVisibility(View.VISIBLE);
            colorIconHolder.mSelectedView.startAnimation(
                    AnimationUtils.loadAnimation(mContext, R.anim.explode_animation));

        } else {
            colorIconHolder.mSelectedView.setVisibility(View.INVISIBLE);
        }

        Picasso.with(mContext)
               .load(url)
               .into(colorIconHolder.mIconView);

        return convertView;
    }

    public void setSelected(int position)
    {
        mIconSelected = position;
    }
}
