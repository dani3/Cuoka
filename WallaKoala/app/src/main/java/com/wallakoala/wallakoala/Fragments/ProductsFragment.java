package com.wallakoala.wallakoala.Fragments;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.RequestFuture;
import com.wallakoala.wallakoala.Activities.MainScreenUI;
import com.wallakoala.wallakoala.Adapters.ProductsGridAdapter;
import com.wallakoala.wallakoala.Beans.ColorVariant;
import com.wallakoala.wallakoala.Beans.Product;
import com.wallakoala.wallakoala.Beans.User;
import com.wallakoala.wallakoala.Properties.Properties;
import com.wallakoala.wallakoala.R;
import com.wallakoala.wallakoala.Singletons.RestClientSingleton;
import com.wallakoala.wallakoala.Singletons.TypeFaceSingleton;
import com.wallakoala.wallakoala.Singletons.VolleySingleton;
import com.wallakoala.wallakoala.Utils.CustomRequest;
import com.wallakoala.wallakoala.Utils.ExceptionPrinter;
import com.wallakoala.wallakoala.Utils.SharedPreferencesManager;
import com.wallakoala.wallakoala.Utils.Utils;
import com.wallakoala.wallakoala.Views.StaggeredRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Fragmento con la pestaña de Novedades.
 * Created by Daniel Mancebo Aldea on 29/05/2016.
 */

public class ProductsFragment extends Fragment
{
    /* Constants */
    protected static final String ALL = "All";
    protected static final int NUM_PRODUCTS_DISPLAYED = 100;
    protected static final int MIN_PRODUCTS = 8;
    protected static final int MAX_OFFSET = 20;
    protected static boolean MAN;
    protected static boolean FIRST_CONNECTION;
    protected static int NUMBER_OF_CORES;
    protected static int DAYS_WITH_NOTHING;
    protected static int DAYS_OFFSET;
    protected static String SEARCH_QUERY;
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

    /* Data */
    protected List<ConcurrentMap<String, List<Product>>> mProductsListMap;
    protected Map<String, Object> mFilterMap;
    protected Deque<Product> mProductsCandidatesDeque;
    protected List<Product> mProductsDisplayedList;
    protected List<String> mShopsList;

    /* Container Views */
    protected StaggeredRecyclerView mProductsRecyclerView;

    /* Layouts */
    protected FrameLayout mFrameLayout;

    /* LayoutManagers */
    protected StaggeredGridLayoutManager mStaggeredGridLayoutManager;

    /* Views */
    protected TextView mNoDataTextView;

    protected View mLoadingView;
    protected View mLoadingServerView;
    protected View mNoShopsView;

    /* Buttons */
    protected Button mAddShopsButton;

    /* Adapters */
    protected ProductsGridAdapter mProductAdapter;

    /* Animations */
    protected Animation mMoveAndFadeAnimation;
    protected Animation mShowFromDown, mHideFromUp;

    /* Snackbar */
    protected Snackbar mSnackbar;

    /* SharedPreferences */
    protected SharedPreferencesManager mSharedPreferences;

    /* AsynTasks */
    protected AsyncTask mConnectToServer, mRetreiveProductsTask;

    /* User */
    protected User mUser;

    /* Others */
    protected STATE mState;
    protected int mProductsInsertedPreviously, start, count;
    protected long mBackPressed;

    /* Constructor por defecto NECESARIO */
    public ProductsFragment() {}

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

        return inflater.inflate(R.layout.fragment_products, container, false);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        // FrameLayout
        mFrameLayout = (FrameLayout) getView().findViewById(R.id.frame);

        // RecyclerView
        mProductsRecyclerView = (StaggeredRecyclerView) getView().findViewById(R.id.grid_recycler);

        // LoaderView
        mLoadingView       = getView().findViewById(R.id.avloadingIndicatorView);
        mLoadingServerView = getView().findViewById(R.id.loading);
        mLoadingServerView.setVisibility(View.GONE);

        // TextView que muestran que no hay productos disponibles
        mNoDataTextView = (TextView) getView().findViewById(R.id.nodata_textview);

        // No shops
        mNoShopsView    = getView().findViewById(R.id.no_shops);
        mAddShopsButton = (Button) getView().findViewById(R.id.add_shops_button);

        mAddShopsButton.setTypeface(TypeFaceSingleton.getTypeFace(getActivity(), "Existence-StencilLight.otf"));

        // Listener para abrir el dialogo para anadir tiendas.
        mAddShopsButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((MainScreenUI)getActivity()).openActivityShops();
            }
        });

        // Si el usuario no tiene tiendas.
        if (mUser.getShops().isEmpty())
        {
            Log.d(Properties.TAG, "[PRODUCTS_FRAGMENT] El usuario no tiene ninguna tienda");

            mLoadingView.setVisibility(View.GONE);
            mLoadingServerView.setVisibility(View.GONE);

            // IMPORTANTE quitar el RecyclerView de los productos.
            mProductsRecyclerView.setVisibility(View.GONE);

        } else {
            Log.d(Properties.TAG, "[PRODUCTS_FRAGMENT] El usuario tiene " + mUser.getShops().size() + " tiendas");

            mNoShopsView.setVisibility(View.GONE);

            mConnectToServer = new ConnectToServer().execute();
        }
    }

    /**
     * Inicializacion de las distintias estructuras de datos.
     */
    private void _initData()
    {
        mSharedPreferences = new SharedPreferencesManager(getActivity());

        mProductsListMap         = new ArrayList<>();
        mFilterMap               = new HashMap<>();
        mProductsDisplayedList   = new ArrayList<>();
        mProductsCandidatesDeque = new ArrayDeque<>();
        mShopsList               = new ArrayList<>();

        for (String shop : mSharedPreferences.retrieveUser().getShops())
        {
            mShopsList.add(shop);
        }

        start = count = 0;
        mBackPressed = 0;

        SEARCH_QUERY = null;

        DAYS_OFFSET = 0;
        DAYS_WITH_NOTHING = 0;
        MAN = mSharedPreferences.retrieveUser().getMan();

        NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

        FIRST_CONNECTION = true;

        mUser = mSharedPreferences.retrieveUser();

        Log.d(Properties.TAG, "[PRODUCTS_FRAGMENT] Numero de procesadores: " + NUMBER_OF_CORES);
    }

    /**
     * Metodo que reinicializa ciertas variables.
     */
    private void _reinitializeData()
    {
        mProductsListMap         = new ArrayList<>();
        mProductsDisplayedList   = new ArrayList<>();
        mProductsCandidatesDeque = new ArrayDeque<>();

        start = count = 0;
        mBackPressed = 0;

        DAYS_OFFSET = 0;
        DAYS_WITH_NOTHING = 0;

        FIRST_CONNECTION = true;
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

        mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mProductAdapter = new ProductsGridAdapter(getActivity()
                                    , mProductsDisplayedList
                                    , mFrameLayout);

        mProductsRecyclerView.setHasFixedSize(true);
        mProductsRecyclerView.setItemViewCacheSize(Properties.CACHED_PRODUCTS_MIN);
        mProductsRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);
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
                    int[] lastItemsPosition = new int[2];
                    mStaggeredGridLayoutManager.findLastCompletelyVisibleItemPositions(lastItemsPosition);

                    // Si los ultimos visibles son los ultimos productos
                    if ((lastItemsPosition[0] >= mProductsDisplayedList.size() - 2) ||
                        (lastItemsPosition[1] >= mProductsDisplayedList.size() - 1))
                    {
                        // Si la cola de candidatos no esta lista, es que todavia quedan productos
                        if (!mProductsCandidatesDeque.isEmpty())
                        {
                            Log.d(Properties.TAG, "[PRODUCTS_FRAGMENT] Se ha ha hecho SCROLL -> todavía quedan productos candidatos");

                            // Sacamos los siguientes productos
                            _getNextProductsToBeDisplayed();

                            // Sacamos el indice del primer producto a insertar
                            start = mProductsDisplayedList.size() - mProductsInsertedPreviously;
                            count = mProductsInsertedPreviously;

                            // Actualizamos la lista de productos del adapter
                            mProductAdapter.updateProductList(mProductsDisplayedList);

                            // Notificamos el cambio
                            mProductAdapter.notifyItemRangeInserted(start, count);

                        } else {
                            // Siempre que no se este cargando, o bien no estemos en los filtros
                            if ((mState != STATE.LOADING) && (DAYS_OFFSET >= 0))
                            {
                                Log.d(Properties.TAG, "[PRODUCTS_FRAGMENT] Se ha hecho SCROLL -> No quedan candidatos");

                                DAYS_OFFSET++;

                                if (DAYS_WITH_NOTHING < MAX_OFFSET)
                                {
                                    Log.d(Properties.TAG, "[PRODUCTS_FRAGMENT] Se traen más productos");

                                    mConnectToServer = new ConnectToServer().execute();

                                } else {
                                    Log.d(Properties.TAG
                                            , "[PRODUCTS_FRAGMENT] Se ha superado el máximo de dias, no se traen más productos");

                                    Snackbar.make(mFrameLayout, "No hay mas novedades", Snackbar.LENGTH_SHORT).show();

                                    mLoadingServerView.setVisibility(View.GONE);
                                    mLoadingView.setVisibility(View.GONE);
                                }
                            }
                        }
                    }
                }
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            mProductsRecyclerView.scheduleLayoutAnimation();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        // Si venimos de un producto, tenemos que restaurar el footer.
        if ((mProductAdapter != null) && (mProductAdapter.productClicked()))
        {
            Log.d(Properties.TAG, "Volviendo de ProductUI");
            mProductAdapter.restore();
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        // Cancelamos cualquier conexion que se este haciendo.
        if (mConnectToServer != null)
            if (!mConnectToServer.isCancelled())
                mConnectToServer.cancel(true);

        if (mRetreiveProductsTask != null)
            if (!mRetreiveProductsTask.isCancelled())
                mRetreiveProductsTask.cancel(true);
    }

    /**
     * Task que convierte una array de JSON en una lista de productos. Devuelve true cuando ha terminado.
     */
    private class ConversionTask implements Callable<Boolean>
    {
        private JSONArray mJsonArray;

        public ConversionTask(JSONArray jsonArray)
        {
            mJsonArray = jsonArray;
        }

        @Override
        public Boolean call()
        {
            try
            {
                convertJSONtoProduct(mJsonArray);

            } catch (Exception e) {
                ExceptionPrinter.printException("PRODUCTS_FRAGMENT", e);

                return false;
            }

            return true;
        }
    } /* [END ConversionTask] */

    /**
     * Metodo que convierte la lista de JSON en productos y los inserta en las distintas ED's.
     * @param jsonArray: lista de JSON a convertir.
     * @throws JSONException
     */
    private void convertJSONtoProduct(final JSONArray jsonArray) throws JSONException
    {
        List<Product> productsList = new ArrayList<>();
        List<JSONObject> jsonList = new ArrayList<>();
        String key = null;

        for (int j = 0; j < jsonArray.length(); j++)
        {
            JSONObject js = jsonArray.getJSONObject(j);

            jsonList.add(js);
        }

        for(JSONObject jsonObject : jsonList)
        {
            double price       = jsonObject.getDouble("1");
            String name        = jsonObject.getString("2");
            String shop = key  = jsonObject.getString("3");
            String section     = jsonObject.getString("4");
            String link        = jsonObject.getString("5");
            String description = jsonObject.getString("7");
            long id            = jsonObject.getLong("8");
            float aspectRatio  = (float) jsonObject.getDouble("9");
            double discount    = jsonObject.getDouble("11");

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

            Product product = new Product(id, name, shop
                                    , section, price, discount
                                    , aspectRatio, link
                                    , description, colors);

            if (product.isOkay())
            {
                productsList.add(product);
            }
        }

        mProductsListMap.get(mProductsListMap.size()-1).put(key, productsList);
    }

    /**
     * Tarea en segundo plano que descargara la lista de JSON del servidor en paralelo.
     * Si falla alguna conexion no pasa nada ya que se ignora, sin embargo, si fallan
     * todas las conexiones, se muestra la SnackBar para reintentar.
     */
    private class ConnectToServer extends AsyncTask<String, Void, Void>
    {
        private List<JSONArray> content = new ArrayList<>();
        private String error = null;

        @Override
        protected void onPreExecute()
        {
            if (mState != STATE.LOADING)
            {
                _loading(true, true);
            }

        } // onPreExecute

        @Override
        protected Void doInBackground(String... unused)
        {
            Log.d(Properties.TAG, "[PRODUCTS_FRAGMENT] Se traen los productos de hace " + DAYS_OFFSET + " dias");
            content = RestClientSingleton.retrieveProducts(getActivity(), DAYS_OFFSET, mShopsList);

            if (content == null)
            {
                error = "Se ha producido un error obteniendo productos";
                Log.e(Properties.TAG, "[PRODUCTS_FRAGMENT] " + error);
            }

            return null;

        } // doInBackground

        @Override
        @SuppressWarnings("unchecked")
        protected void onPostExecute(Void unused)
        {
            if (error != null)
            {
                _loading(false, false);

                _errorConnectingToServer(false);

            } else {
                new MultithreadConversion().execute(content);
            }

        } // onPostExecute

    } /* [END ConnectToServer] */

    /**
     * Tarea en segundo plano que contacta con el servidor para traer nuevos productos
     * que cumplan los filtros establecidos.
     */
    private class RetreiveProductsTask extends AsyncTask<String, Void, Void>
    {
        private List<JSONArray> content;
        private String error = null;

        @Override
        protected void onPreExecute()
        {
            if (mState != STATE.LOADING)
            {
                _loading(true, true);
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        protected Void doInBackground(String... params)
        {
            try
            {
                List<String> aux = (ArrayList<String>) mFilterMap.get("shops");
                List<String> shopsList = (aux == null) ? mShopsList : aux;

                if (SEARCH_QUERY == null)
                {
                    content = RestClientSingleton.sendFilterRequest(getContext(), shopsList, mFilterMap);

                } else {
                    content = RestClientSingleton.sendSearchRequest(getContext(), SEARCH_QUERY);
                }

                if (content == null)
                {
                    error = "Se ha producido un error obteniendo productos";
                    Log.e(Properties.TAG, "[PRODUCTS_FRAGMENT] " + error);
                }

            } catch (Exception ex)  {
                ExceptionPrinter.printException("PRODUCTS_FRAGMENT", ex);
            }

            return null;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void onPostExecute(Void unused)
        {
            if (error != null)
            {
                _loading(false, false);

                _errorConnectingToServer(true);

            } else {
                if (!content.isEmpty())
                {
                    new MultithreadConversion().execute(content);
                }
            }
        }

    } /* [END RetreiveProductsTask] */

    /**
     * Tarea en segundo plano que convertira concurrentemente el array de JSONs.
     */
    private class MultithreadConversion extends AsyncTask<List<JSONArray>, Void, Void>
    {
        private ThreadPoolExecutor executor;
        private CompletionService<Boolean> completionService;

        private String error = null;

        @Override
        protected void onPreExecute()
        {
            Log.d(Properties.TAG, "[PRODUCTS_FRAGMENT] Se crea un executor de " + (NUMBER_OF_CORES * 4) + " núcleos");
            // Creamos un executor, con cuatro veces mas de threads que nucleos fisicos.
            executor = new ThreadPoolExecutor(NUMBER_OF_CORES * 4
                    , NUMBER_OF_CORES * 4
                    , 60L
                    , TimeUnit.SECONDS
                    , new LinkedBlockingQueue<Runnable>());

            completionService = new ExecutorCompletionService<>(executor);
        }

        @Override
        @SuppressWarnings("unchecked")
        protected Void doInBackground(List<JSONArray>... params)
        {
            List<JSONArray> content = params[0];

            try
            {
                mProductsListMap.add(new ConcurrentHashMap<String, List<Product>>());

                // Creamos un callable por cada tienda
                for (int i = 0; i < content.size(); i++)
                {
                    Log.d(Properties.TAG, "[PRODUCTS_FRAGMENT] Se crea un ConversionTask para parsear el primer JSONArray ("
                            + (content.get(i).toString().getBytes().length / 1000) + "kB)");

                    ConversionTask task = new ConversionTask(content.get(i));

                    completionService.submit(task);
                }

                // Nos quedamos esperando a que terminen los threads
                for (int i = 0; i < content.size(); i++)
                {
                    completionService.take();
                    Log.d(Properties.TAG, "[PRODUCTS_FRAGMENT] Productos parseados correctamente");
                }

                Log.d(Properties.TAG, "[PRODUCTS_FRAGMENT] Todos los productos parseados correctamente");

                // Liberamos el executor ya que no hara falta.
                executor.shutdown();

                // Una vez cargados los productos, actualizamos la cola de candidatos...
                _updateCandidates();
                // ... y actualizamos la lista de los que se van a mostrar
                _getNextProductsToBeDisplayed();

                // Si no es la primera conexion
                if (!FIRST_CONNECTION)
                {
                    Log.d(Properties.TAG
                            , "[PRODUCTS_FRAGMENT] NO es primera conexión, se actualiza el adapter con los productos nuevos");

                    // Sacamos el indice del primer producto a insertar
                    start = mProductsDisplayedList.size() - mProductsInsertedPreviously;
                    count = mProductsInsertedPreviously;

                    // Actualizamos la lista de productos del adapter
                    mProductAdapter.updateProductList(mProductsDisplayedList);

                    // Notificamos el cambio
                    mProductAdapter.notifyItemRangeInserted(start, count);
                }

            } catch (Exception e) {
                ExceptionPrinter.printException("PRODUCTS_FRAGMENT", e);

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

                _errorConnectingToServer(false);

            } else {
                // Si lo ultimo que hemos traido esta vacio, o no se llega al minimo y NO estamos en los filtros
                if ((mProductsListMap.get(mProductsListMap.size()-1).isEmpty()) && (DAYS_OFFSET >= 0))
                {
                    Log.d(Properties.TAG, "[PRODUCTS_FRAGMENT] No se ha traído ningún producto");

                    DAYS_WITH_NOTHING++;
                    DAYS_OFFSET++;

                    if (DAYS_WITH_NOTHING < MAX_OFFSET)
                    {
                        Log.d(Properties.TAG, "[PRODUCTS_FRAGMENT] Se vuelve a conectar con el servidor para traer más productos");

                        mConnectToServer = new ConnectToServer().execute();

                    } else {
                        Log.d(Properties.TAG, "[PRODUCTS_FRAGMENT] Se ha superado el máximo de dias, no se traen más productos");

                        Snackbar.make(mFrameLayout, "No hay mas novedades", Snackbar.LENGTH_LONG).show();

                        mState = STATE.NORMAL;

                        Log.d(Properties.TAG, "[PRODUCTS_FRAGMENT] Estado: " + mState.toString());

                        mLoadingServerView.setVisibility(View.GONE);
                        mLoadingView.setVisibility(View.GONE);
                    }

                } else if (mProductsCandidatesDeque.isEmpty() && mProductsDisplayedList.size() < MIN_PRODUCTS && (DAYS_OFFSET >= 0)) {

                    Log.d(Properties.TAG, "[PRODUCTS_FRAGMENT] No se han traído suficientes productos");

                    DAYS_WITH_NOTHING++;
                    DAYS_OFFSET++;

                    if (DAYS_WITH_NOTHING < MAX_OFFSET)
                    {
                        Log.d(Properties.TAG, "[PRODUCTS_FRAGMENT] Se vuelve a conectar con el servidor para traer más productos");

                        mConnectToServer = new ConnectToServer().execute();

                    } else {
                        Log.d(Properties.TAG, "[PRODUCTS_FRAGMENT] Se ha superado el máximo de dias, no se traen más productos");

                        Snackbar.make(mFrameLayout, "No hay mas novedades", Snackbar.LENGTH_LONG).show();

                        mState = STATE.NORMAL;

                        Log.d(Properties.TAG, "[PRODUCTS_FRAGMENT] Estado: " + mState.toString());

                        mLoadingServerView.setVisibility(View.GONE);
                        mLoadingView.setVisibility(View.GONE);
                    }

                } else {
                    // Si estamos en los filtros y no se ha recuperado nada
                    if ((mProductsListMap.get(mProductsListMap.size()-1).isEmpty() && (DAYS_OFFSET == -1)))
                    {
                        Log.d(Properties.TAG, "[PRODUCTS_FRAGMENT] El filtro no ha devuelto ningún producto");

                        _noData(true);

                    } else {
                        DAYS_WITH_NOTHING = 0;

                        // Se han cargado los productos correctamente
                        _loading(false, true);
                    }
                }
            }
        }

    } /* [END MultithreadConversion] */

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

                    mState = STATE.NORMAL;

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

                    mState = STATE.NORMAL;
                }

            } else {
                mLoadingView.setVisibility(View.GONE);
                mLoadingServerView.setVisibility(View.GONE);
            }

        } else if (FIRST_CONNECTION) {
            _noData(false);

            mNoShopsView.setVisibility(View.GONE);

            // Pantalla de carga cuando es la primera conexion
            mLoadingView.setVisibility(View.VISIBLE);

            mState = STATE.LOADING;

        } else {
            // Icono de carga
            mLoadingServerView.startAnimation(mShowFromDown);
            mLoadingServerView.setVisibility(View.VISIBLE);

            mState = STATE.LOADING;
        }

        Log.d(Properties.TAG, "[PRODUCTS_FRAGMENT] Estado: " + mState.toString());
    }

    /**
     * Metodo que muestra un mensaje cuando no hay ningun producto que mostrar.
     * @param noData: true indica que no hay ningun producto que mostrar.
     */
    private void _noData(boolean noData)
    {
        if (!noData)
        {
            mNoDataTextView.setVisibility(View.GONE);

            mState = STATE.NORMAL;

        } else {
            mNoDataTextView.setVisibility(View.VISIBLE);
            mLoadingView.setVisibility(View.GONE);

            mState = STATE.NODATA;
        }

        Log.d(Properties.TAG, "[PRODUCTS_FRAGMENT] Estado:" + mState.toString());
    }

    /**
     * Metodo que muestra un mensaje cuando se ha producido un error al conectar con el server.
     * @param isFiltering: true si el error se ha producido con los filtros.
     */
    private void _errorConnectingToServer(boolean isFiltering)
    {
        if (!isFiltering)
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

        } else {
            mSnackbar = Snackbar.make(mFrameLayout
                            , getResources().getString( R.string.error_message )
                            , Snackbar.LENGTH_INDEFINITE ).setAction("Reintentar", new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    mRetreiveProductsTask = new RetreiveProductsTask().execute();
                }
            });
        }

        mSnackbar.show();

        mState = STATE.ERROR;

        Log.d(Properties.TAG, "[PRODUCTS_FRAGMENT] Estado:" + mState.toString());
    }

    /**
     * Metodo que actualiza la cola de candidatos, realiza una lectura del mapa de productos como un RoundRobin.
     */
    private void _updateCandidates()
    {
        Log.d(Properties.TAG, "[PRODUCTS_FRAGMENT] Se ordenan los productos y se sacan los productos candidatos");

        // Mapa de indices para trackear por donde nos hemos quedado en la iteracion anterior.
        final Map<String, Integer> indexMap = new HashMap<>();

        boolean finished = false;
        boolean turn = true;

        // Inicializar mapa de indices con todos a 0.
        for (String key : mProductsListMap.get(mProductsListMap.size()-1).keySet())
        {
            indexMap.put(key, 0);
        }

        Iterator<String> iterator = mProductsListMap.get(mProductsListMap.size()-1).keySet().iterator();

        // Mientras queden productos pendientes.
        while (!finished)
        {
            String key = iterator.next();

            // Sacamos el indice de donde nos quedamos y la lista de productos.
            int index = indexMap.get(key);
            List<Product> list = mProductsListMap.get(mProductsListMap.size()-1).get(key);

            // Mientras queden productos y no encontremos un producto mostrable.
            while ((index < list.size()) && (turn))
            {
                mProductsCandidatesDeque.addLast(list.get(index++));

                turn = false;
            } // while #2

            // Actualizamos el mapa de indices.
            indexMap.put(key, index);

            turn = true;

            // Si se ha terminado el recorrido, lo iniciamos de nuevo.
            if (!iterator.hasNext())
                iterator = mProductsListMap.get(mProductsListMap.size()-1).keySet().iterator();

            finished = _checkIfFinished(indexMap);

        } // while #1

        Log.d(Properties.TAG, "[PRODUCTS_FRAGMENT] Lista de candidatos actualizada: " + mProductsCandidatesDeque.size());
    }

    /**
     * Metodo que inserta los productos en la cola ordenados.
     */
    private void _getNextProductsToBeDisplayed()
    {
        Log.d(Properties.TAG, "[PRODUCTS_FRAGMENT] Se sacan los productos que se van a mostrar");

        mProductsInsertedPreviously = NUM_PRODUCTS_DISPLAYED;

        // Si no hay suficientes productos en la cola.
        if (NUM_PRODUCTS_DISPLAYED > mProductsCandidatesDeque.size())
        {
            mProductsInsertedPreviously = mProductsCandidatesDeque.size();
        }

        for (int i = 0; i < mProductsInsertedPreviously; i++)
        {
            mProductsDisplayedList.add(mProductsCandidatesDeque.getFirst());

            mProductsCandidatesDeque.removeFirst();
        }

        Log.d(Properties.TAG, "[PRODUCTS_FRAGMENT] Lista de candidatos actualizada: " + mProductsCandidatesDeque.size());
        Log.d(Properties.TAG, "[PRODUCTS_FRAGMENT] Lista de productos mostrados actualizada: " + mProductsDisplayedList.size());
    }

    /**
     * Metodo que comprueba si se han recorrido todos los productos del mapa de productos.
     * @param indexMap: Mapa de indices donde guardamos el indice de la ultima iteracion.
     * @return true si se han recorrido todos los productos.
     */
    private boolean _checkIfFinished(final Map<String, Integer> indexMap)
    {
        boolean finished = true;
        Iterator<String> iterator = indexMap.keySet().iterator();
        while ((iterator.hasNext()) && (finished))
        {
            String key = iterator.next();

            finished = (mProductsListMap.get(mProductsListMap.size()-1).get(key).size() == indexMap.get(key));
        }

        return finished;
    }

    /**
     * Metodo que indica si es posible filtrar.
     * @return true si se puede ir a la pantalla de filtros.
     */
    public boolean canFilter()
    {
        Log.d(Properties.TAG, "[PRODUCTS_FRAGMENT] Se comprueba si se puede filtrar");

        return ((mShopsList != null) && (!mShopsList.isEmpty()) &&
                (mState != STATE.LOADING) && (mLoadingView.getVisibility() == View.GONE));
    }

    /**
     * Metodo que devuelve el estado actual de los filtros.
     * @return mapa de filtros.
     */
    public Map<String, Object> getFilterMap()
    {
        return mFilterMap;
    }

    /**
     * Metodo que realiza el proceso de filtrado
     * @param filterMap nuevo estado de los filtros.
     */
    @SuppressWarnings("unchecked")
    public void processFilter(Map<String, Object> filterMap)
    {
        SEARCH_QUERY = null;

        mFilterMap = filterMap;

        List<String> shopsList    = (ArrayList<String>) mFilterMap.get("shops");
        List<String> sectionsList = (ArrayList<String>) mFilterMap.get("sections");
        List<String> colorsList   = (ArrayList<String>) mFilterMap.get("colors");
        boolean newness           = (boolean) mFilterMap.get("newness");
        int from                  = (int) mFilterMap.get("minPrice");
        int to                    = (int) mFilterMap.get("maxPrice");

        if (shopsList != null && shopsList.size() == mShopsList.size() && shopsList.containsAll(mShopsList))
        {
            shopsList = null;
        }

        _reinitializeData();

        if (mProductsRecyclerView != null)
        {
            mProductsRecyclerView.setVisibility(View.GONE);
        }

        // Se comprueba si los filtros son los por defecto, si es asi, se realiza una peticion normal.
        if ((shopsList == null) &&
            (colorsList == null) &&
            (sectionsList == null) &&
            (!newness) &&
            (from < 0) &&
            (to < 0))
        {
            Log.d(Properties.TAG, "[PRODUCTS_FRAGMENT] Se han introducido los filtros por defecto");
            Log.d(Properties.TAG, "[PRODUCTS_FRAGMENT] Se vuelve al funcionamiento normal");

            // Reiniciamos el mapa de filtros.
            mFilterMap = new HashMap<>();

            mConnectToServer = new ConnectToServer().execute();

        } else {
            // Lo ponemos a -1 para detectar cuando estamos en los filtros.
            DAYS_OFFSET = -1;

            // Si ha marcado todas las tiendas, tenemos que traerlas antes.
            if ((shopsList != null) && (shopsList.get(0).equals(ALL)))
            {
                Log.d(Properties.TAG, "[PRODUCTS_FRAGMENT] Se han marcado todas las tiendas");

                final String fixedURL = Utils.fixUrl(
                        Properties.SERVER_URL + ":" + Properties.SERVER_SPRING_PORT + "/shops/" + mUser.getMan());

                Log.d(Properties.TAG, "[PRODUCTS_FRAGMENT] Conectando con: " + fixedURL + " para traer la lista de tiendas");

                // Creamos la peticion para obtener la lista de tiendas.
                final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET
                        , fixedURL
                        , null
                        , new Response.Listener<JSONArray>()
                        {
                            @Override
                            public void onResponse(JSONArray response)
                            {
                                Log.d(Properties.TAG, "[PRODUCTS_FRAGMENT] Se recibe la respuesta con las siguientes tiendas: ");

                                // Sacamos la lista de tiendas
                                List<String> shops = new ArrayList<>();
                                for (int i = 0; i < response.length(); i++)
                                {
                                    try
                                    {
                                        shops.add(response.getJSONObject(i).getString("name"));
                                        Log.d(Properties.TAG, "[PRODUCTS_FRAGMENT] - " + shops.get(i));

                                    } catch (JSONException e) {
                                        ExceptionPrinter.printException("PRODUCTS_FRAGMENT", e);
                                    }
                                }

                                mFilterMap.put("shops", shops);

                                _loading(true, true);

                                mRetreiveProductsTask = new RetreiveProductsTask().execute();
                            }
                        }
                        , new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error)
                            {
                                _loading(false, false);

                                Snackbar.make(mFrameLayout, getResources().getString(R.string.error_message), Snackbar.LENGTH_INDEFINITE)
                                        .setAction("Reintentar", new View.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(View v)
                                            {
                                                processFilter(mFilterMap);
                                            }
                                        }).show();
                            }
                        });

                VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonArrayRequest);
                Log.d(Properties.TAG, "[PRODUCTS_FRAGMENT] Petición creada y enviada");

            } else {
                mRetreiveProductsTask = new RetreiveProductsTask().execute();
            }
        }
    }

    /**
     * Metodo que realiza el proceso de busqueda.
     * @param query cadena con la busqueda.
     */
    public void processSearch(String query)
    {
        // Reiniciamos ciertos parametros.
        _reinitializeData();

        SEARCH_QUERY = query;

        // Reiniciamos el mapa de filtros.
        mFilterMap = new HashMap<>();

        // Ocultamos el RecyclerView
        if (mProductsRecyclerView != null)
        {
            mProductsRecyclerView.setVisibility(View.GONE);
        }

        // Lo ponemos a -1 para detectar cuando estamos en los filtros
        DAYS_OFFSET = -1;

        mRetreiveProductsTask = new RetreiveProductsTask().execute();
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
     * Metodo que devuelve si es hombre o mujer.
     * @return true si es hombre.
     */
    public boolean getMan()
    {
        return MAN;
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

        // Si el usuario no tiene tiendas.
        if (mUser.getShops().isEmpty())
        {
            mNoShopsView.setVisibility(View.VISIBLE);

            mLoadingView.setVisibility(View.GONE);
            mLoadingServerView.setVisibility(View.GONE);

        } else {
            mNoShopsView.setVisibility(View.GONE);

            mConnectToServer = new ConnectToServer().execute();
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
