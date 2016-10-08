package com.wallakoala.wallakoala.Fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.RequestFuture;
import com.wallakoala.wallakoala.Adapters.RecommendedListAdapter;
import com.wallakoala.wallakoala.Beans.ColorVariant;
import com.wallakoala.wallakoala.Beans.Product;
import com.wallakoala.wallakoala.Beans.User;
import com.wallakoala.wallakoala.Properties.Properties;
import com.wallakoala.wallakoala.R;
import com.wallakoala.wallakoala.Singletons.VolleySingleton;
import com.wallakoala.wallakoala.Utils.SharedPreferencesManager;
import com.wallakoala.wallakoala.Utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Fragmento con la pestaña de Descubre.
 * Created by Daniel Mancebo Aldea on 29/05/2016.
 */

public class RecommendedFragment extends Fragment
{
    /* Constants */
    protected static int NUMBER_OF_CORES;
    protected static int NUMBER_OF_PRODUCTS = 20;
    protected static boolean FIRST_CONNECTION;

    /* Container Views */
    protected RecyclerView mProductsRecyclerView;

    /* Views */
    protected View mLoadingView;
    protected View mLoadingServerView;

    /* Layouts */
    protected FrameLayout mFrameLayout;

    /* LayoutManagers */
    protected GridLayoutManager mGridLayoutManager;

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
    protected int mOffset;

    /* Constructor por defecto NECESARIO */
    public RecommendedFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        _initData();
        _initAnimations();
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
        mLoadingView       = getView().findViewById(R.id.recommended_avloadingIndicatorView);
        mLoadingServerView = getView().findViewById(R.id.recommended_loading);
        mLoadingServerView.setVisibility(View.GONE);

        // RecyclerView
        mProductsRecyclerView = (RecyclerView)getView().findViewById(R.id.recommended_grid_recycler);

        // Si el usuario no tiene ninguna tienda,
        if (mUser.getShops().isEmpty())
        {
            mLoadingView.setVisibility(View.GONE);
            mLoadingServerView.setVisibility(View.GONE);

            // IMPORTANTE quitar el RecyclerView de los productos.
            mProductsRecyclerView.setVisibility(View.GONE);

            /* CREAR UNA ANIMACION DE UNA FLECHA */

        } else {
            mConnectToServer = new ConnectToServer().execute();
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

        mOffset = 0;

        FIRST_CONNECTION = true;

        NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

        Log.d(Properties.TAG, "Numero de procesadores: " + NUMBER_OF_CORES);
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

        mGridLayoutManager = new GridLayoutManager(getActivity(), 1);
        mProductAdapter = new RecommendedListAdapter(getActivity()
                , mProductList
                , mFrameLayout);

        mProductsRecyclerView.setLayoutManager(mGridLayoutManager);
        mProductsRecyclerView.setAdapter(mProductAdapter);
        mProductsRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener()
        {
            boolean scrollingUp;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {}

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                scrollingUp = dy > 0;

                if (scrollingUp)
                {
                    // Detectamos cuando llegamos abajo para cargar nuevos productos
                    int lastItemPosition = mGridLayoutManager.findLastCompletelyVisibleItemPosition();

                    // Si los ultimos visibles son los ultimos productos
                    if (lastItemPosition >= mProductList.size() - 1)
                    {
                        // Siempre que no se este cargando
                        if ((mState != ProductsFragment.STATE.LOADING))
                        {
                            mConnectToServer = new ConnectToServer().execute();
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onResume()
    {
        super.onResume();

        // Si no venimos del onCreate (ON_CREATE_FLAG = FALSE) significa que venimos de
        // la pantalla de un producto o de los filtros.
        // Si venimos de un producto, tenemos que restaurar el footer.
        if ((mProductAdapter != null) && (mProductAdapter.productClicked()))
        {
            Log.d(Properties.TAG, "Volviendo de ProductUI");
            mProductAdapter.restore();
        }
    }

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
            try
            {
                RequestFuture<JSONArray> future = RequestFuture.newFuture();

                final String fixedURL = Utils.fixUrl(Properties.SERVER_URL + ":" + Properties.SERVER_SPRING_PORT
                        + "/recommended/" + mUser.getId() + "/" + (mOffset * NUMBER_OF_PRODUCTS));

                Log.d(Properties.TAG, "Conectando con: " + fixedURL + " para traer los productos recomendados");

                // Creamos una peticion
                final JsonArrayRequest jsonObjReq = new JsonArrayRequest(Request.Method.GET
                                                                    , fixedURL
                                                                    , null
                                                                    , future
                                                                    , future);

                // La mandamos a la cola de peticiones
                VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjReq);

                if (isCancelled())
                {
                    return null;
                }

                try
                {
                    content = future.get(20, TimeUnit.SECONDS);

                } catch (InterruptedException e) {
                    error = "Thread interrumpido";
                    Log.d(Properties.TAG, error);
                }

                if (isCancelled())
                {
                    return null;
                }

                // Si content es vacio, es que han fallado todas las conexiones.
                if (content == null)
                {
                    error = "Imposible conectar con el servidor";
                    Log.d(Properties.TAG, error);
                }

            } catch (Exception e) {
                error = e.getMessage();
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
            int start;

            try
            {
                Log.d(Properties.TAG, "Tamano en bytes: " + (content.toString().getBytes().length / 1000) + "kB");

                _convertJSONtoProduct(content);

                // Si no es la primera conexion
                if (!FIRST_CONNECTION)
                {
                    start = mOffset * NUMBER_OF_PRODUCTS;

                    // Actualizamos la lista de productos del adapter
                    mProductAdapter.updateProductList(mProductList);

                    // Notificamos el cambio
                    mProductAdapter.notifyItemRangeInserted(start, NUMBER_OF_PRODUCTS);
                }

            } catch (Exception e) {
                error = e.getMessage();
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

                mOffset++;
            }
        }
    }

    /**
     * Metodo que convierte la lista de JSON en productos y los inserta en las distintas ED's.
     * @param jsonArray: lista de JSON a convertir.
     * @throws JSONException
     */
    private void _convertJSONtoProduct(final JSONArray jsonArray) throws JSONException
    {
        List<JSONObject> jsonList = new ArrayList<>();

        for (int j = 0; j < jsonArray.length(); j++)
        {
            JSONObject js = jsonArray.getJSONObject(j);

            jsonList.add(js);
        }

        for(JSONObject jsonObject : jsonList)
        {
            double price       = jsonObject.getDouble("1");
            String name        = jsonObject.getString("2");
            String shop        = jsonObject.getString("3");
            String section     = jsonObject.getString("4");
            String link        = jsonObject.getString("5");
            String description = jsonObject.getString("7");
            long id            = jsonObject.getLong("8");
            float aspectRation = (float) jsonObject.getDouble("9");

            JSONArray jsColors = jsonObject.getJSONArray("6");
            List<ColorVariant> colors = new ArrayList<>();
            for(int i = 0; i < jsColors.length(); i++)
            {
                JSONObject jsColor = jsColors.getJSONObject(i);

                String reference = jsColor.getString("1");
                String colorName = jsColor.getString("2");
                String colorPath = jsColor.getString("4");
                short numerOfImages = (short)jsColor.getInt("3");

                colors.add(new ColorVariant(reference, colorName, colorPath, numerOfImages));
            }

            Product product = new Product(id
                                    , name
                                    , shop
                                    , section
                                    , price
                                    , aspectRation
                                    , link
                                    , description
                                    , colors);

            if (product.isOkay())
            {
                mProductList.add(product);
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
                // Si no es la primera conexion no hace falta inicializar el RecyclerView
                if (FIRST_CONNECTION)
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

                    FIRST_CONNECTION = false;

                } else {
                    mHideFromUp.setAnimationListener(new Animation.AnimationListener()
                    {
                        @Override
                        public void onAnimationStart(Animation animation) {}

                        @Override
                        public void onAnimationEnd(Animation animation)
                        {
                            mLoadingServerView.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {}
                    });

                    mLoadingServerView.startAnimation(mHideFromUp);

                    mState = ProductsFragment.STATE.NORMAL;
                }

            } else {
                mLoadingView.setVisibility(View.GONE);
                mLoadingServerView.setVisibility(View.GONE);
            }

        } else if (FIRST_CONNECTION) {
            // Pantalla de carga cuando es la primera conexion
            mLoadingView.setVisibility(View.VISIBLE);

            mState = ProductsFragment.STATE.LOADING;

        } else {
            // Icono de carga
            mLoadingServerView.startAnimation(mShowFromDown);
            mLoadingServerView.setVisibility(View.VISIBLE);

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
}
