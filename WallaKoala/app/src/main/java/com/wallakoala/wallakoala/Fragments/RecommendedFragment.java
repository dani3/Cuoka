package com.wallakoala.wallakoala.Fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.wallakoala.wallakoala.Adapters.RecommendedListAdapter;
import com.wallakoala.wallakoala.Beans.Product;
import com.wallakoala.wallakoala.Beans.User;
import com.wallakoala.wallakoala.Properties.Properties;
import com.wallakoala.wallakoala.R;
import com.wallakoala.wallakoala.Singletons.RestClientSingleton;
import com.wallakoala.wallakoala.Utils.JSONParser;
import com.wallakoala.wallakoala.Utils.SharedPreferencesManager;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import static com.wallakoala.wallakoala.Fragments.ProductsFragment.NUM_PRODUCTS_CACHED;

/**
 * Fragmento con la pestaña de Descubre.
 * Created by Daniel Mancebo Aldea on 29/05/2016.
 */

public class RecommendedFragment extends Fragment
{
    /* Constants */
    protected static boolean HAS_BEEN_SELECTED;

    /* Container Views */
    protected RecyclerView mProductsRecyclerView;

    /* Views */
    protected View mLoadingView;

    /* Layouts */
    protected FrameLayout mFrameLayout;

    /* LayoutManagers */
    protected StaggeredGridLayoutManager mGridLayoutManager;

    /* Adapters */
    protected RecommendedListAdapter mProductAdapter;

    /* SharedPreferenceManager */
    protected SharedPreferencesManager mSharedPreferences;

    /* Animations */
    protected Animation mMoveAndFadeAnimation;
    protected Animation mShowFromDown, mHideFromUp;

    /* Snackbar */
    protected Snackbar mSnackbar;

    /* AsynTasks */
    protected AsyncTask mConnectToServer;

    /* Data */
    protected User mUser;
    protected ProductsFragment.STATE mState;
    protected List<Product> mProductList;

    /* Constructor por defecto NECESARIO */
    public RecommendedFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        _initAnimations();
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

        // LoaderView
        mLoadingView = getView().findViewById(R.id.recommended_avloadingIndicatorView);

        // RecyclerView
        mProductsRecyclerView = (RecyclerView)getView().findViewById(R.id.recommended_grid_recycler);

        // Si el usuario no tiene ninguna tienda,
        if (mUser.getShops().isEmpty())
        {
            mLoadingView.setVisibility(View.GONE);

            // IMPORTANTE quitar el RecyclerView de los productos.
            mProductsRecyclerView.setVisibility(View.GONE);
        }
    }

    /**
     * Inicializacion de las distintias estructuras de datos.
     */
    protected void _initData()
    {
        mSharedPreferences = new SharedPreferencesManager(getActivity());

        mUser = mSharedPreferences.retreiveUser();

        mProductList = new ArrayList<>();

        HAS_BEEN_SELECTED = false;
    }

    /**
     * Metodo que inicializa las animaciones.
     */
    private void _initAnimations()
    {
        mMoveAndFadeAnimation = AnimationUtils.loadAnimation(getActivity()
                , R.anim.translate_and_fade_animation);

        mHideFromUp = AnimationUtils.loadAnimation(getActivity()
                , R.anim.hide_to_down_animation);

        mShowFromDown = AnimationUtils.loadAnimation(getActivity()
                , R.anim.show_from_down_animation);
    }

    /**
     * Inicializacion y configuracion del recyclerView.
     */
    @SuppressWarnings("deprecation")
    private void _initRecyclerView()
    {
        mProductsRecyclerView.setVisibility(View.VISIBLE);

        mGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mProductAdapter = new RecommendedListAdapter(getActivity()
                , mProductList
                , mFrameLayout);

        mProductsRecyclerView.setItemViewCacheSize(NUM_PRODUCTS_CACHED);
        mProductsRecyclerView.setLayoutManager(mGridLayoutManager);
        mProductsRecyclerView.setAdapter(mProductAdapter);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        // Si venimos de un producto, tenemos que actualizar los cambios (si los hay)
        if ((mProductAdapter != null) && (mProductAdapter.productClicked()))
        {
            Log.d(Properties.TAG, "Volviendo de ProductUI");
            mProductAdapter.restore();
        }
    }

    /**
     * Tarea en segundo plano que se conecta al servidor para traer las recomendaciones
     */
    private class ConnectToServer extends AsyncTask<String, Void, Void>
    {
        private JSONArray content = null;
        private String error = null;

        @Override
        protected void onPreExecute()
        {
            if (mState != ProductsFragment.STATE.LOADING)
            {
                _loading(true, true);
            }

        } // onPreExecute

        @Override
        protected Void doInBackground(String... unused)
        {
            content = RestClientSingleton.retrieveRecommendedProducts(getActivity());

            if (content == null)
            {
                error = "Error obteniendo productos recomendados";

                Log.d(Properties.TAG, error);
            }

            return null;

        } // doInBackground

        @Override
        protected void onPostExecute(Void unused)
        {
            if (error != null)
            {
                _loading(false, false);
                _errorConnectingToServer();

            } else {
                new JSONConversion().execute(content);
            }

        } // onPostExecute

    } /* [END ConnectToServer] */

    /**
     * Tarea en segundo plano que convertira el array de JSONs.
     */
    private class JSONConversion extends AsyncTask<JSONArray, Void, Void>
    {
        private String error = null;

        @Override
        protected Void doInBackground(JSONArray... params)
        {
            JSONArray content = params[0];

            try
            {
                Log.d(Properties.TAG, "Tamano en bytes: " + (content.toString().getBytes().length / 1000) + "kB");

                mProductList = JSONParser.convertJSONtoProduct(content);

            } catch (Exception e) {
                error = e.getMessage();

                Log.d(Properties.TAG, error);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused)
        {
            if (error != null)
            {
                _loading(false, false);

                _errorConnectingToServer();

            } else {
                // Se han cargado los productos correctamente
                _loading(false, true);
            }
        }
    }

    /**
     * Metodo que maneja la interfaz en funcion de si esta cargando o no los productos.
     * @param loading: true indica que se inicia la carga, false que ha terminado.
     */
    private void _loading(boolean loading, boolean ok)
    {
        // Si hemos terminado de cargar los productos
        if (!loading)
        {
            if (ok)
            {
                // Cuando termine la animacion de la view de carga, mostramos el RecyclerView
                mMoveAndFadeAnimation.setAnimationListener(new Animation.AnimationListener()
                {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation)
                    {
                        mLoadingView.setVisibility(View.GONE);

                        _initRecyclerView();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });

                mLoadingView.startAnimation(mMoveAndFadeAnimation);

                mState = ProductsFragment.STATE.NORMAL;

            } else {
                mLoadingView.setVisibility(View.GONE);
            }

        } else {
            // Pantalla de carga cuando es la primera conexion
            mLoadingView.setVisibility(View.VISIBLE);

            mState = ProductsFragment.STATE.LOADING;
        }

        Log.d(Properties.TAG, "Estado = " + mState.toString());
    }

    /**
     * Metodo que muestra un mensaje cuando se ha producido un error al conectar con el server.
     */
    private void _errorConnectingToServer()
    {
        mSnackbar = Snackbar.make(mFrameLayout
                        , getResources().getString(R.string.error_message)
                        , Snackbar.LENGTH_INDEFINITE ).setAction("Reintentar", new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View view)
                            {
                                mConnectToServer = new ConnectToServer().execute();
                            }
                        });

        mSnackbar.show();

        mState = ProductsFragment.STATE.ERROR;

        Log.d(Properties.TAG, "Estado = " + mState.toString());
    }

    /**
     * Metodo que llama al servidor para traer las recomendaciones, solo la primera vez que se selecciona.
     */
    public void select()
    {
        if ((!HAS_BEEN_SELECTED) && (mUser.getShops() != null) && (!mUser.getShops().isEmpty()))
        {
            HAS_BEEN_SELECTED = true;

            mConnectToServer = new ConnectToServer().execute();
        }
    }

    /**
     * Metodo que redimensiona el grid de productos.
     * @param reduction: porcentaje que se quiere reducir.
     */
    public void resizeGrid(final float reduction)
    {
        if (mProductsRecyclerView != null)
        {
            mProductsRecyclerView.animate()
                                 .setDuration(0)
                                 .scaleX(reduction)
                                 .scaleY(reduction);
        }
    }

    /**
     * Metodo que reinicia la pantalla ya que se han realizado cambios.
     */
    public void restart()
    {
        _initData();

        // Ocultamos el RecyclerView
        if (mProductsRecyclerView != null)
        {
            mProductsRecyclerView.setVisibility(View.GONE);
        }

        // Si el usuario no tiene ninguna tienda,
        if (mUser.getShops().isEmpty())
        {
            mLoadingView.setVisibility(View.GONE);
        }
    }

    /**
     * Metodo que para notificar que algo ha cambiado.
     */
    public void notifyDataSetChanged()
    {
        if (mProductAdapter != null)
        {
            mProductAdapter.notifyDataSetChanged();
        }
    }
}
