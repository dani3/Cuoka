package com.cuoka.cuoka.Fragments;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.cuoka.cuoka.Activities.MainScreenUI;
import com.cuoka.cuoka.Adapters.RecommendedListAdapter;
import com.cuoka.cuoka.Beans.Product;
import com.cuoka.cuoka.Beans.User;
import com.cuoka.cuoka.Properties.Properties;
import com.cuoka.cuoka.R;
import com.cuoka.cuoka.Singletons.RestClientSingleton;
import com.cuoka.cuoka.Singletons.TypeFaceSingleton;
import com.cuoka.cuoka.Utils.ExceptionPrinter;
import com.cuoka.cuoka.Utils.JSONParser;
import com.cuoka.cuoka.Utils.SharedPreferencesManager;
import com.cuoka.cuoka.Views.StaggeredRecyclerView;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragmento con la pestaña de Descubre.
 * Created by Daniel Mancebo Aldea on 29/05/2016.
 */

public class RecommendedFragment extends Fragment
{
    /* Flags */
    private static boolean SELECTED;

    /* Container Views */
    private StaggeredRecyclerView mProductsRecyclerView;

    /* Views */
    private View mLoadingView;
    private View mNoStylesView;
    private View mNoDataView;

    /* Layouts */
    private FrameLayout mFrameLayout;

    /* Snackbar */
    private Snackbar mSnackbar;

    /* Animations */
    private Animation mMoveAndFadeAnimation;

    /* Data */
    private User mUser;
    private Properties.STATE mState;
    private List<DescubreShop> mShopList;

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
    @SuppressWarnings("ConstantConditions, deprecation")
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        // FrameLayout
        mFrameLayout = (FrameLayout) getView().findViewById(R.id.recommended_frame);

        // LoaderView
        mLoadingView = getView().findViewById(R.id.recommended_avloadingIndicatorView);

        // RecyclerView
        mProductsRecyclerView = (StaggeredRecyclerView) getView().findViewById(R.id.recommended_grid_recycler);

        // No data TextView
        mNoDataView = getView().findViewById(R.id.norecommended_textview);

        // No styles
        mNoStylesView = getView().findViewById(R.id.no_styles);
        Button addStylesButton = (Button) getView().findViewById(R.id.add_styles_button);

        addStylesButton.setTypeface(TypeFaceSingleton.getTypeFace(getActivity(), "Existence-StencilLight.otf"));

        // Listener para abrir el dialogo para anadir tiendas.
        addStylesButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((MainScreenUI)getActivity()).openActivityStyles();
            }
        });

        View noStylesImage = getView().findViewById(R.id.no_styles_image);
        noStylesImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainScreenUI)getActivity()).openActivityStyles();
            }
        });

        // Si el usuario no tiene ningun estilo.
        if (mUser.getStyles().isEmpty())
        {
            Log.d(Properties.TAG, "[RECOMMENDED_FRAGMENT] El usuario no tiene ningun estilo seleccionado");

            mLoadingView.setVisibility(View.GONE);

            // IMPORTANTE quitar el RecyclerView de los productos.
            mProductsRecyclerView.setVisibility(View.GONE);

        } else {
            Log.d(Properties.TAG, "[RECOMMENDED_FRAGMENT] El usuario tiene " + mUser.getStyles().size() + " estilos");
            for (String style : mUser.getStyles())
            {
                Log.d(Properties.TAG, "[RECOMMENDED_FRAGMENT] - " + style);
            }

            mNoStylesView.setVisibility(View.GONE);
        }
    }

    /**
     * Inicializacion de las distintias estructuras de datos.
     */
    protected void _initData()
    {
        SharedPreferencesManager sharedPreferences = new SharedPreferencesManager(getActivity());

        mUser = sharedPreferences.retrieveUser();

        mShopList = new ArrayList<>();

        SELECTED = false;
    }

    /**
     * Metodo que inicializa las animaciones.
     */
    private void _initAnimations()
    {
        mMoveAndFadeAnimation = AnimationUtils.loadAnimation(getActivity()
                , R.anim.translate_and_fade_animation);
    }

    /**
     * Inicializacion y configuracion del recyclerView.
     */
    @SuppressWarnings("deprecation")
    private void _initRecyclerView()
    {
        mProductsRecyclerView.setVisibility(View.VISIBLE);

        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        DescubreAdapter shopAdapter = new DescubreAdapter(getActivity(), mShopList);

        mProductsRecyclerView.setItemViewCacheSize(Properties.CACHED_PRODUCTS_MAX);
        mProductsRecyclerView.setLayoutManager(gridLayoutManager);
        mProductsRecyclerView.setAdapter(shopAdapter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            mProductsRecyclerView.scheduleLayoutAnimation();
        }
    }

    /**
     * Tarea en segundo plano que se conecta al servidor para traer las recomendaciones
     */
    private class RetrieveRecommendationsTask extends AsyncTask<String, Void, Void>
    {
        private JSONArray content = null;
        private boolean error = false;
        private boolean empty = false;

        @Override
        protected void onPreExecute()
        {
            if (mState != Properties.STATE.LOADING)
            {
                _loading(true, true);
            }
        }

        @Override
        protected Void doInBackground(String... unused)
        {
            content = RestClientSingleton.retrieveDescubreShops(getActivity());

            error = (content == null);

            return null;
        }

        @Override
        protected void onPostExecute(Void unused)
        {
            if (error)
            {
                _loading(false, false);
                _errorConnectingToServer();

            } else {
                empty = (content.length() == 0);

                if (!empty)
                {
                    new JSONConversion().execute(content);

                } else {
                    mLoadingView.setVisibility(View.GONE);
                    mNoDataView.setVisibility(View.VISIBLE);

                    mState = Properties.STATE.NODATA;
                }
            }
        }

    } /* [END RetrieveRecommendationsTask] */

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
                mShopList = JSONParser.convertJSONsToDescubreShops(content);

            } catch (Exception e) {
                ExceptionPrinter.printException("RECOMMENDED_FRAGMENT", e);

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

                mState = Properties.STATE.OK;

            } else {
                mLoadingView.setVisibility(View.GONE);
            }

        } else {
            // Pantalla de carga cuando es la primera conexion
            mLoadingView.setVisibility(View.VISIBLE);

            mNoDataView.setVisibility(View.GONE);

            mState = Properties.STATE.LOADING;
        }

        Log.d(Properties.TAG, "[RECOMMENDED_FRAGMENT] Estado = " + mState.toString());
    }

    /**
     * Metodo que muestra un mensaje cuando se ha producido un error al conectar con el server.
     */
    @SuppressWarnings("deprecation")
    private void _errorConnectingToServer()
    {
        mSnackbar = Snackbar.make(mFrameLayout
                    , getResources().getString(R.string.error_message)
                    , Snackbar.LENGTH_INDEFINITE ).setAction("Reintentar", new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            new RetrieveRecommendationsTask().execute();
                        }
                    });

        mSnackbar.getView().setBackgroundColor(getResources().getColor(android.R.color.white));
        mSnackbar.setActionTextColor(getResources().getColor(R.color.colorAccent));
        ((TextView)mSnackbar.getView().findViewById(android.support.design.R.id.snackbar_text))
                .setTextColor(getResources().getColor(R.color.colorText));

        mSnackbar.show();

        mState = Properties.STATE.ERROR;

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

    /**
     * Metodo que oculta la snackbar si esta visible.
     */
    public void hideSnackbar()
    {
        if (mSnackbar != null && mSnackbar.isShown())
        {
            mSnackbar.dismiss();
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

        // Si el usuario no tiene estilos.
        if (mUser.getStyles().isEmpty())
        {
            mNoStylesView.setVisibility(View.VISIBLE);

            mLoadingView.setVisibility(View.GONE);

            mNoDataView.setVisibility(View.GONE);

        } else {
            mNoStylesView.setVisibility(View.GONE);

            new RetrieveRecommendationsTask().execute();
        }
    }

    /**
     * Metodo que carga las tiendas por primera vez.
     */
    public void loadShops()
    {
        if (!SELECTED)
        {
            SELECTED = true;

            if (!mUser.getStyles().isEmpty())
            {
                new RetrieveRecommendationsTask().execute();
            }
        }
    }
}
