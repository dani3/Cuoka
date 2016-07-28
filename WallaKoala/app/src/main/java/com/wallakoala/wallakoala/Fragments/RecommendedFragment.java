package com.wallakoala.wallakoala.Fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.wallakoala.wallakoala.Adapters.RecommendedListAdapter;
import com.wallakoala.wallakoala.Beans.ColorVariant;
import com.wallakoala.wallakoala.Beans.Product;
import com.wallakoala.wallakoala.Properties.Properties;
import com.wallakoala.wallakoala.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @class Fragmento con la pestaña de Descubre.
 * Created by Daniel Mancebo Aldea on 29/05/2016.
 */

public class RecommendedFragment extends Fragment
{
    /* Constants */
    protected static int NUMBER_OF_CORES;
    protected enum STATE
    {
        ERROR
                { @Override
                public String toString() { return "ERROR"; }
                },
        LOADING
                { @Override
                public String toString() { return "LOADING"; }
                },
        NODATA
                { @Override
                public String toString() { return "NO_DATA"; }
                },
        NORMAL
                { @Override
                public String toString() { return "NORMAL"; }
                },
    }

    /* Container Views */
    protected RecyclerView mProductsRecyclerView;

    /* Layouts */
    protected FrameLayout mFrameLayout;

    /* LayoutManagers */
    protected GridLayoutManager mGridLayoutManager;

    /* Adapters */
    protected RecommendedListAdapter mProductAdapter;

    /* AsynTasks */
    protected AsyncTask mConnectToServer;

    /* Others */
    protected STATE mState;

    /* Constructor por defecto NECESARIO */
    public RecommendedFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        _initData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.fragment_recommended, container, false);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        // FrameLayout
        mFrameLayout = (FrameLayout)getView().findViewById(R.id.recommended_frame);

        // RecyclerView
        mProductsRecyclerView = (RecyclerView)getView().findViewById(R.id.recommended_grid_recycler);

        // Nos conectamos al servidor para traer los ultimos productos.
        mConnectToServer = new ConnectToServer().execute();
    }

    /**
     * Inicializacion de las distintias estructuras de datos.
     */
    protected void _initData()
    {
        NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

        Log.d(Properties.TAG, "Numero de procesadores: " + NUMBER_OF_CORES);
    }

    private class ConnectToServer extends AsyncTask<String, Void, Void>
    {
        List<Product> mProductsDisplayedList = new ArrayList<>();

        @Override
        protected void onPreExecute()
        {

        }

        @Override
        protected Void doInBackground(String... unused)
        {
            ColorVariant cv = new ColorVariant("536200701", "NEGRO", "NEGRO", (short)6);
            List<ColorVariant> list = new ArrayList<>();
            list.add(cv);

            Product product = new Product(0
                                    , "TRENCH ENVOLVENTE ESPIGA"
                                    , "Blanco"
                                    , "Blazers"
                                    , 129.99f
                                    , "https://www.blanco.com//product/7942/new/clothing/outerwear/jackets/blazers/wrap-around-trench"
                                    , ""
                                    , list);

            mProductsDisplayedList.add(product);

            return null;
        }

        @Override
        protected void onPostExecute(Void unused)
        {
            mGridLayoutManager = new GridLayoutManager(getActivity(), 1);
            mProductAdapter = new RecommendedListAdapter(getActivity()
                    , mProductsDisplayedList
                    , 1
                    , mFrameLayout);

            mProductsRecyclerView.setHasFixedSize(true);
            mProductsRecyclerView.setLayoutManager(mGridLayoutManager);
            mProductsRecyclerView.setAdapter(mProductAdapter);
            mProductsRecyclerView.setVisibility(View.VISIBLE);
        }
    }
}
