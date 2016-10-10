package com.wallakoala.wallakoala.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;
import com.wallakoala.wallakoala.Beans.ColorVariant;
import com.wallakoala.wallakoala.Properties.Properties;
import com.wallakoala.wallakoala.R;
import com.wallakoala.wallakoala.Utils.Utils;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.wallakoala.wallakoala.Properties.Properties.TAG;

/**
 * Adapter para mostrar la lista de iconos en un producto recomendado.
 * Created by Daniel Mancebo Aldea on 10/10/2016.
 */

public class RecommendedColorIconListAdapter extends RecyclerView.Adapter<RecommendedColorIconListAdapter.IconHolder>
{
    /* Context */
    private static Context mContext;

    /* Data */
    private List<ColorVariant> mColorVariants;
    private String mShop, mSection;

    /* [BEGIN] ViewHolder */
    public static class IconHolder extends RecyclerView.ViewHolder
    {
        private CircleImageView mIconView;

        public IconHolder(View itemView)
        {
            super(itemView);

            mIconView = (CircleImageView)itemView.findViewById(R.id.recommended_color_icon);
        }

        /**
         * Metodo llamado cuando se va a mostrar el item.
         * @param colorVariant: color variant.
         */
        @SuppressWarnings("deprecation")
        public void bindIcon(ColorVariant colorVariant, String shop, String section)
        {
            // Path != 0 -> Color predefinido
            String url;
            if (colorVariant.getColorPath().equals("0"))
            {
                String imageFile = shop + "_" + section + "_"
                        + colorVariant.getReference() + "_"
                        + colorVariant.getColorName().replaceAll(" ", "_") + "_ICON.jpg";

                url = Utils.fixUrl(Properties.SERVER_URL + Properties.ICONS_PATH + shop + "/" + imageFile);

            } else {
                String imageFile = colorVariant.getColorPath();

                url = Utils.fixUrl(Properties.SERVER_URL + Properties.PREDEFINED_ICONS_PATH + imageFile + "_ICON.jpg");
            }

            Log.d(TAG, url);

            Picasso.with(mContext)
                   .load(url)
                   .into(mIconView);
        }

    } /* [END] ViewHolder */

    /**
     * Constructor del adapter
     * @param context: contexto (ProductUI)
     * @param colorVariants: lista de colores
     */
    public RecommendedColorIconListAdapter(Context context
                                , List<ColorVariant> colorVariants
                                , String shop
                                , String section)
    {
        mContext = context;
        mColorVariants = colorVariants;
        mShop = shop;
        mSection = section;
    }

    @Override
    public IconHolder onCreateViewHolder(final ViewGroup viewGroup, final int viewType)
    {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                                      .inflate(R.layout.aux_recommended_color_icon
                                            , viewGroup
                                            , false );

        return new IconHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final IconHolder shopHolder, final int pos)
    {
        shopHolder.bindIcon(mColorVariants.get(pos), mShop, mSection);
    }

    @Override
    public int getItemCount()
    {
        return mColorVariants.size();
    }
}
