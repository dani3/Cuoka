package com.wallakoala.wallakoala.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.wallakoala.wallakoala.Activities.ProductsUI;
import com.wallakoala.wallakoala.Beans.Product;
import com.wallakoala.wallakoala.R;

/**
 * @class Adapter para el grid de productos
 * Created by Daniel Mancebo Aldea on 10/11/2015.
 */

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductHolder>
{
    private Context mContext;
    private String[] titles;
    private String[] subtitles;
    private int[] images;
    private boolean[] fav;

    private Animation implode, explode;

    public static class ProductHolder extends RecyclerView.ViewHolder
    {
        private TextView title, subtitle;
        private ImageButton fav;
        private ImageView image;

        public ProductHolder( View itemView )
        {
            super( itemView );

            title    = ( TextView )itemView.findViewById( R.id.footer_title );
            subtitle = ( TextView )itemView.findViewById( R.id.footer_subtitle );
            image    = ( ImageView )itemView.findViewById( R.id.grid_image );
            fav      = ( ImageButton )itemView.findViewById( R.id.footer_fav_button );
        }

        public void bindProduct( Product product )
        {

        }
    }

    public ProductAdapter( Context context )
    {
        mContext = context;

        titles = new String[]{"ZARA", "BLANCO", "BERSHKA", "SPRINGFIELD","H&M", "ZARA", };
        subtitles = new String[]{"Pantalones", "Camisas", "Camisetas", "Faldas","Vestidos", "Abrigos", };
        fav = new boolean[]{ false, false, false, false, false, false };
        images = new int[]{ R.drawable.imagen1
                    , R.drawable.imagen2
                    , R.drawable.imagen3
                    , R.drawable.imagen4
                    , R.drawable.imagen5
                    , R.drawable.imagen6 };
    }

    @Override
    public ProductHolder onCreateViewHolder( ViewGroup viewGroup, int viewType )
    {
        View itemView = LayoutInflater.from( viewGroup.getContext() )
                                      .inflate( R.layout.product_item_grid, viewGroup, false );

        return new ProductHolder( itemView );
    }

    @Override
    public void onBindViewHolder( final ProductHolder productHolder, int pos )
    {
        final int i = pos;

        productHolder.title.setText(titles[pos]);
        productHolder.subtitle.setText(subtitles[pos]);
        productHolder.image.setImageResource(images[pos]);
        productHolder.fav.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                implode = AnimationUtils.loadAnimation(mContext, R.anim.implode);
                explode = AnimationUtils.loadAnimation(mContext, R.anim.explode);
                explode.setFillAfter(true);

                productHolder.fav.startAnimation(implode);
                implode.setAnimationListener(new Animation.AnimationListener()
                {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        if (!fav[i])
                            productHolder.fav.setBackgroundResource(R.drawable.ic_favorite_white);

                        else
                            productHolder.fav.setBackgroundResource(R.drawable.ic_favorite_border_white);

                        fav[i] = !fav[i];
                        productHolder.fav.startAnimation(explode);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });



            }
        });
    }

    @Override
    public int getItemCount()
    {
        return titles.length;
    }
}
