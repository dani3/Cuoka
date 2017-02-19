package com.wallakoala.wallakoala.Fragments;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.wallakoala.wallakoala.Adapters.RecommendedListAdapter;
import com.wallakoala.wallakoala.Beans.Product;
import com.wallakoala.wallakoala.Beans.User;
import com.wallakoala.wallakoala.Properties.Properties;
import com.wallakoala.wallakoala.R;
import com.wallakoala.wallakoala.Singletons.RestClientSingleton;
import com.wallakoala.wallakoala.Utils.ExceptionPrinter;
import com.wallakoala.wallakoala.Utils.JSONParser;
import com.wallakoala.wallakoala.Utils.SharedPreferencesManager;
import com.wallakoala.wallakoala.Views.StaggeredRecyclerView;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragmento con la pestaña de Descubre.
 * Created by Daniel Mancebo Aldea on 29/05/2016.
 */

public class RecommendedFragment extends Fragment
{
    /* Constants */
    private static boolean HAS_BEEN_SELECTED;

    /* Container Views */
    private StaggeredRecyclerView mProductsRecyclerView;

    /* Views */
    private View mLoadingView;

    /* Layouts */
    private FrameLayout mFrameLayout;

    /* Adapters */
    private RecommendedListAdapter mProductAdapter;

    /* Animations */
    private Animation mMoveAndFadeAnimation;

    /* Data */
    private User mUser;
    private Properties.STATE mState;
    private List<Product> mProductList;

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

        // Si el usuario no tiene ninguna tienda,
        if (mUser.getShops().isEmpty())
        {
            mLoadingView.setVisibility(View.GONE);

            // IMPORTANTE quitar el RecyclerView de los productos.
            mProductsRecyclerView.setVisibility(View.GONE);
        }
    }

    /**
     * Metodo que crea un dialogo informativo.
     * @return AlertDialog.
     */
    private AlertDialog _createDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme);

        builder.setTitle("No has seleccionado ninguna tienda");
        builder.setMessage("Selecciona tus tiendas para poder mostrarte nuestras recomendaciones.");
        builder.setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });

        return builder.create();
    }

    /**
     * Inicializacion de las distintias estructuras de datos.
     */
    protected void _initData()
    {
        SharedPreferencesManager sharedPreferences = new SharedPreferencesManager(getActivity());

        mUser = sharedPreferences.retrieveUser();

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
    }

    /**
     * Inicializacion y configuracion del recyclerView.
     */
    @SuppressWarnings("deprecation")
    private void _initRecyclerView()
    {
        mProductsRecyclerView.setVisibility(View.VISIBLE);

        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mProductAdapter = new RecommendedListAdapter(getActivity()
                , mProductList
                , mFrameLayout);

        mProductsRecyclerView.setItemViewCacheSize(Properties.CACHED_PRODUCTS_MAX);
        mProductsRecyclerView.setLayoutManager(gridLayoutManager);
        mProductsRecyclerView.setAdapter(mProductAdapter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            mProductsRecyclerView.scheduleLayoutAnimation();
        }
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
    private class RetrieveRecommendationsTask extends AsyncTask<String, Void, Void>
    {
        private JSONArray content = null;
        private String error = null;

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
            content = RestClientSingleton.retrieveRecommendedProducts(getActivity());

            if (content == null)
            {
                error = "Error obteniendo productos recomendados";

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
                new JSONConversion().execute(content);
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
                mProductList = JSONParser.convertJSONsToProducts(content);

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
        Snackbar snackbar = Snackbar.make(mFrameLayout
                    , getResources().getString(R.string.error_message)
                    , Snackbar.LENGTH_INDEFINITE ).setAction("Reintentar", new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            new RetrieveRecommendationsTask().execute();
                        }
                    });

        snackbar.getView().setBackgroundColor(getResources().getColor(android.R.color.white));
        snackbar.setActionTextColor(getResources().getColor(R.color.colorAccent));
        ((TextView)snackbar.getView().findViewById(android.support.design.R.id.snackbar_text))
                .setTextColor(getResources().getColor(R.color.colorText));

        snackbar.show();

        mState = Properties.STATE.ERROR;

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

            new RetrieveRecommendationsTask().execute();

        } else if (mUser.getShops().isEmpty()) {
            final AlertDialog dialog = _createDialog();

            dialog.setOnShowListener(new DialogInterface.OnShowListener()
            {
                @Override
                @SuppressWarnings("deprecation")
                public void onShow(DialogInterface dialogInterface)
                {
                    dialog.getButton(
                            AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorAccent));
                }
            });

            dialog.show();
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
