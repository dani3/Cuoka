package com.wallakoala.wallakoala.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    /* Context */
    private Context mContext;

    /* Inflater */
    private LayoutInflater mLayoutInflater;

    /* Data */
    private List<ColorVariant> mColorList;

    /**
     * Constructor del adapter.
     * @param context: contexto de la aplicacion.
     * @param colorVariants: lista de ColorVariants.
     */
    public ColorIconListAdapter(Context context, List<ColorVariant> colorVariants)
    {
        this.mContext = context;
        mColorList = colorVariants;
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
        // Creamos el inflater si no esta creado
        if (mLayoutInflater == null)
            mLayoutInflater = (LayoutInflater)mContext
                                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Inflamos el item
        if (convertView == null)
            convertView = mLayoutInflater.inflate(R.layout.color_icon, null);

        final CircleImageView circleImageView = (CircleImageView)convertView.findViewById(R.id.color_icon);

        final Target target = new Target()
        {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
            {
                circleImageView.setImageBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {}

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {}

        };

        final String url = Utils.fixUrl(SERVER_URL + mColorList.get(position).getColorPath()
                                                               .replaceAll("/var/www/html", ""));

        Log.d(TAG, url);

        Picasso.with(mContext)
               .load(url)
               .into(target);

        return convertView;
    }
}
