package com.wallakoala.wallakoala.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.BaseAdapter;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.wallakoala.wallakoala.Beans.ColorVariant;
import com.wallakoala.wallakoala.R;
import com.wallakoala.wallakoala.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @class: Adapter de la lista de iconos
 * Created by Daniel Mancebo Aldea on 04/02/2016.
 */

public class ColorIconListAdapter extends BaseAdapter
{
    /* Constants */
    private static final String TAG = "CUOKA";
    private static final String SERVER_URL = "http://cuoka-ws.cloudapp.net";
    private static final String PREDEFINED_ICONS_PATH = "/images/colors/";
    private static final String ICONS_PATH = "/images/products/";

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
    protected static class ColorIconHolder
    {
        CircleImageView mIconView;
        CircleImageView mSelectedView;
    }

    /**
     * Constructor del adapter.
     * @param context: contexto de la aplicacion.
     * @param colorVariants: lista de ColorVariants.
     */
    public ColorIconListAdapter(Context context, List<ColorVariant> colorVariants, String shop, String section)
    {
        this.mContext = context;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ColorIconHolder colorIconHolder = null;

        // Creamos el inflater si no esta creado
        if (mLayoutInflater == null)
            mLayoutInflater = (LayoutInflater)mContext
                                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Inflamos el item
        if (convertView == null)
        {
            colorIconHolder = new ColorIconHolder();

            convertView = mLayoutInflater.inflate(R.layout.color_icon, null);

            colorIconHolder.mIconView     = (CircleImageView)convertView.findViewById(R.id.color_icon);
            colorIconHolder.mSelectedView = (CircleImageView)convertView.findViewById(R.id.color_selected);

            convertView.setTag(colorIconHolder);

        } else {
            colorIconHolder = (ColorIconHolder)convertView.getTag();
        }

        // Path != 0 -> Color predefinido
        String url;
        if (mColorList.get(position).getColorPath().equals("0"))
        {
            String imageFile = mShop + "_"
                    + mSection + "_"
                    + mColorList.get(position).getReference() + "_"
                    + mColorList.get(position).getColorName().replaceAll(" ", "_") + "_ICON.jpg";

            url = Utils.fixUrl(SERVER_URL + ICONS_PATH + mShop + "/" + imageFile);

        } else {
            String imageFile = mColorList.get(position).getColorPath();

            url = Utils.fixUrl(SERVER_URL + PREDEFINED_ICONS_PATH + imageFile);
        }

        if (position == mIconSelected)
        {
            colorIconHolder.mSelectedView.setVisibility(View.VISIBLE);
            colorIconHolder.mSelectedView.startAnimation(AnimationUtils.loadAnimation(mContext
                                                                            , R.anim.explode));

        } else {
            colorIconHolder.mSelectedView.setVisibility(View.GONE);
        }


        Log.d(TAG, url);

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
