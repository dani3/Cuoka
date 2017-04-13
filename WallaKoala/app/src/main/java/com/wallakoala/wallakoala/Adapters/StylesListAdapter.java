package com.wallakoala.wallakoala.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.wallakoala.wallakoala.R;

import java.util.List;

/**
 * Adapter con para mostrar la lista de estilos.
 * Created by Daniel Mancebo Aldea on 09/04/2017.
 */

public class StylesListAdapter extends RecyclerView.Adapter<StylesListAdapter.StyleHolder>
{
    /* Constants */
    private static final int MALE_NUMBER_IMAGES = 6;
    private static final int FEMALE_NUMBER_IMAGES = 9;

    private final int[] maleDrawablesBw =
        new int[] { R.drawable.style_alternative_man_bw, R.drawable.style_classic_man_bw, R.drawable.style_hipster_man_bw, R.drawable.style_sporty_man_bw, R.drawable.style_trendy_man_bw, R.drawable.style_urban_man_bw };
    private final int[] maleDrawables =
            new int[] { R.drawable.style_alternative_man, R.drawable.style_classic_man, R.drawable.style_hipster_man, R.drawable.style_sporty_man, R.drawable.style_trendy_man, R.drawable.style_urban_man };
    private final int[] femaleDrawablesBw =
            new int[] { R.drawable.style_alternative_bw, R.drawable.style_boho_bw, R.drawable.style_classic_bw, R.drawable.style_girly_bw, R.drawable.style_preppy_bw, R.drawable.style_rocker_bw, R.drawable.style_sporty_bw, R.drawable.style_trendy_bw, R.drawable.style_vintage_bw };
    private final int[] femaleDrawables =
            new int[] { R.drawable.style_alternative, R.drawable.style_boho, R.drawable.style_classic, R.drawable.style_girly, R.drawable.style_preppy, R.drawable.style_rocker, R.drawable.style_sporty, R.drawable.style_trendy, R.drawable.style_vintage };

    private final String[] maleStyles =
            new String[] {"Alternative", "Classic", "Hipster", "Sporty", "Trendy", "Urban"};
    private final String[] femaleStyles =
            new String[] {"Alternative", "Boho", "Classic", "Girly", "Preppy", "Rocker", "Sporty", "Trendy", "Vintage"};

    /* Context */
    private Context mContext;

    /* Data */
    private boolean mIsMan;
    private List<String> mStylesList;

    /**
     * ViewHolder del estilo.
     */
    public class StyleHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private ImageView mImageView;
        private TextView mFooterTextView;
        private int mPosition;

        public StyleHolder(View itemView)
        {
            super(itemView);

            mImageView      = (ImageView) itemView.findViewById(R.id.style_image);
            mFooterTextView = (TextView) itemView.findViewById(R.id.footer_style);

            mImageView.setOnClickListener(this);
        }

        public void bindStyle(int pos)
        {
            mPosition = pos;

            if (mIsMan)
            {
                _loadImage(maleStyles[pos], maleDrawablesBw[pos], maleDrawables[pos]);

                mFooterTextView.setText(maleStyles[pos]);

            } else {
                _loadImage(femaleStyles[pos], femaleDrawablesBw[pos], femaleDrawables[pos]);

                mFooterTextView.setText(femaleStyles[pos]);
            }
        }

        /**
         * Metodo que inicializa la imagen con el estilo correspondiente.
         * @param style: estilo a cargar.
         * @param drawableBw: imagen en blanco y negro.
         * @param drawable: imagen a color.
         */
        @SuppressWarnings("deprecation")
        private void _loadImage(String style, int drawableBw, int drawable)
        {
            boolean selected = mStylesList.contains(style);

            mImageView.setImageDrawable(mContext.getResources().getDrawable((selected) ? drawable : drawableBw));
        }

        @Override
        public void onClick(View v)
        {
            String style = (mIsMan) ? maleStyles[mPosition] : femaleStyles[mPosition];
            boolean selected = mStylesList.contains(style);

            YoYo.with(Techniques.Pulse)
                .duration(250)
                .playOn(mImageView);

            // Si esta seleccionado es que se quiere eliminar el estilo.
            if (selected)
            {
                mStylesList.remove(style);

            } else {
                mStylesList.add(style);
            }

            if (mIsMan)
            {
                _loadImage(maleStyles[mPosition], maleDrawablesBw[mPosition], maleDrawables[mPosition]);
            } else {
                _loadImage(femaleStyles[mPosition], femaleDrawablesBw[mPosition], femaleDrawables[mPosition]);
            }
        }

    } /* [END ViewHolder] */

    public StylesListAdapter(Context context, boolean man, List<String> stylesList)
    {
        mContext = context;

        mIsMan = man;

        mStylesList = stylesList;
    }

    /**
     * Metodo que devuelve el estado de los estilos seleccionados.
     * @return lista de estados seleccionados.
     */
    public List<String> getStylesSelected()
    {
        return mStylesList;
    }

    @Override
    public StyleHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                                      .inflate(R.layout.style_item
                                            , parent
                                            , false );

        return new StyleHolder(itemView);
    }

    @Override
    public void onBindViewHolder(StyleHolder styleHolder, int pos)
    {
        styleHolder.bindStyle(pos);
    }

    @Override
    public int getItemCount()
    {
        return (mIsMan ? MALE_NUMBER_IMAGES : FEMALE_NUMBER_IMAGES);
    }
}
