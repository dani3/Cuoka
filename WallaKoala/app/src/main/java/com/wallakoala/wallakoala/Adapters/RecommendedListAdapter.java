package com.wallakoala.wallakoala.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.wallakoala.wallakoala.Beans.Product;
import com.wallakoala.wallakoala.Properties.Properties;
import com.wallakoala.wallakoala.R;
import com.wallakoala.wallakoala.Utils.Utils;

import java.util.List;

/**
 * @class Adapter para el grid de productos recomendados.
 * Created by Daniel Mancebo on 25/06/2016.
 */

public class RecommendedListAdapter extends RecyclerView.Adapter<RecommendedListAdapter.ProductHolder>
{
    /* Context */
    private static Context mContext;

    /* Container Views */
    private static FrameLayout mFrameLayout;

    /* Data */
    private static List<Product> mProductList;
    private static double[] mProductBitmapArray;

    /**
     * ViewHolder del producto con todos los componentes graficos necesarios
     */
    public static class ProductHolder extends RecyclerView.ViewHolder
    {
        private boolean ERROR;
        private boolean LOADED;

        private Product mProduct;
        private Target mTarget;
        private Bitmap mBitmap;

        private ImageView mProductImageView;

        private TextView mNameTextView;

        public ProductHolder(View itemView)
        {
            super(itemView);

            mProductImageView = (ImageView)itemView.findViewById(R.id.recommended_image);
            mNameTextView     = (TextView)itemView.findViewById(R.id.recommended_name);
        }

        /**
         * Metodo que inicializa las vistas con los datos del producto recibido, se llama cada vez que se visualiza el item.
         * @param product: producto con el que se inicializa un item.
         * @param pos: posicion del producto en la lista.
         */
        public void bindProduct(Product product, int pos)
        {
            final int position = pos;

            mProductImageView.setImageBitmap(null);

            /* Inicializamos los TextViews */
            String name = product.getName();
            mNameTextView.setText(name);

            /* Cargamos la imagen usando Picasso */
            mTarget = new Target()
            {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
                {
                    LOADED = true;

                    // Reestablecemos la opacidad y el valor de la altura a WRAP_CONTENT, eliminamos el color de fondo.
                    mProductImageView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    mProductImageView.setBackgroundColor(-1);
                    mProductImageView.setAlpha(1.0f);

                    // Si la imagen es nueva, calculamos el aspect ratio y lo almacenamos el el array en la pos correspondiente.
                    if (mProductBitmapArray[position] == 0.0f)
                        mProductBitmapArray[position] = (double)bitmap.getHeight() / (double)bitmap.getWidth();

                    // Guardamos el bitmap, para asi pasarlo a ProductUI.
                    mBitmap = bitmap;

                    // Por ultimo, cargamos el Bitmap en la ImageView
                    mProductImageView.setImageBitmap(bitmap);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable)
                {
                    ERROR = true;

                    mProductImageView.setBackgroundColor(mContext.getResources()
                                                            .getColor(android.R.color.holo_red_dark));
                    mProductImageView.setAlpha(0.2f);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable)
                {
                    // Debido a que los ViewHolder se reciclan, eliminamos el Bitmap antiguo
                    mProductImageView.setImageBitmap(null);

                    // Si esta imagen ya se ha cargado, establecemos la altura de la ImageView
                    // usando el aspect ratio almacenado en el array, si no, se carga un valor cualquiera
                    if (mProductBitmapArray[position] != 0.0f)
                        mProductImageView.getLayoutParams().height = (int)(mProductImageView.getWidth()
                                * mProductBitmapArray[position]);
                    else
                        mProductImageView.getLayoutParams().height = 350;

                    // Establecemos un color de fondo y un 10% de opacidad.
                    mProductImageView.setBackgroundColor(mContext.getResources().getColor(R.color.colorText));
                    mProductImageView.setAlpha(0.1f);
                }
            };

            String imageFile = product.getShop() + "_"
                    + product.getSection() + "_"
                    + product.getColors().get(0).getReference() + "_"
                    + product.getColors().get(0).getColorName() + "_0_Small.jpg";

            String url = Utils.fixUrl(Properties.SERVER_URL + Properties.IMAGES_PATH + product.getShop() + "/" + imageFile);

            Picasso.with(mContext)
                    .load(url)
                    .into(mTarget);

            mProduct = product;
        }

    }/* [END] ViewHolder */

    /**
     * Constructor del Adapter
     * @param context: contexto (ProductsUI)
     * @param productList: lista de productos
     * @param total: total de productos, necesario para inicializar el array de ratios
     * @param frameLayout: layout necesario para animar la SnackBar
     */
    public RecommendedListAdapter(Context context, List<Product> productList, int total, FrameLayout frameLayout)
    {
        mContext = context;
        mProductList = productList;

        mFrameLayout = frameLayout;

        mProductBitmapArray = new double[total];
        for (int i = 0; i < total; i++)
            mProductBitmapArray[i] = 0.0f;
    }

    @Override
    public ProductHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
    {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.product_recommended
                        , viewGroup
                        , false );

        return new ProductHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ProductHolder productHolder, int pos)
    {
        productHolder.bindProduct(mProductList.get(pos), pos);
    }

    @Override
    public int getItemCount()
    {
        return mProductList.size();
    }
}
