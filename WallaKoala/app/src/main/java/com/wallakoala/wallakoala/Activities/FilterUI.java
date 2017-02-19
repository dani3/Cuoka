package com.wallakoala.wallakoala.Activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.wallakoala.wallakoala.Properties.Properties;
import com.wallakoala.wallakoala.R;
import com.wallakoala.wallakoala.Singletons.RestClientSingleton;
import com.wallakoala.wallakoala.Singletons.TypeFaceSingleton;
import com.wallakoala.wallakoala.Utils.ExceptionPrinter;
import com.wallakoala.wallakoala.Utils.SharedPreferencesManager;
import com.wallakoala.wallakoala.Utils.Utils;
import com.wallakoala.wallakoala.Views.RangeSeekBar;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Activity con la pantalla de filtros.
 * Created by Daniel Mancebo Aldea on 11/02/2016.
 */

public class FilterUI extends AppCompatActivity implements View.OnClickListener
{
    /* Constants */
    private static final float ALPHA_ACTIVE_FILTER = 1.0f;
    private static final float ALPHA_INACTIVE_FILTER = 0.2f;
    private static final String ALL = "All";

    private String SECTION_FILTER_MAN_1;
    private String SECTION_FILTER_MAN_2;
    private String SECTION_FILTER_MAN_3;
    private String SECTION_FILTER_MAN_4;
    private String SECTION_FILTER_MAN_5;
    private String SECTION_FILTER_MAN_6;
    private String SECTION_FILTER_MAN_7;
    private String SECTION_FILTER_MAN_8;
    private String SECTION_FILTER_MAN_9;
    private String SECTION_FILTER_MAN_10;
    private String SECTION_FILTER_MAN_11;
    private String SECTION_FILTER_MAN_12;
    private String SECTION_FILTER_WOMAN_1;
    private String SECTION_FILTER_WOMAN_2;
    private String SECTION_FILTER_WOMAN_3;
    private String SECTION_FILTER_WOMAN_4;
    private String SECTION_FILTER_WOMAN_5;
    private String SECTION_FILTER_WOMAN_6;
    private String SECTION_FILTER_WOMAN_7;
    private String SECTION_FILTER_WOMAN_8;
    private String SECTION_FILTER_WOMAN_9;
    private String SECTION_FILTER_WOMAN_10;
    private String SECTION_FILTER_WOMAN_11;
    private String SECTION_FILTER_WOMAN_12;
    private boolean MAN;

    /* Snackbar */
    private Snackbar mSnackbar;

    /* Animations */
    private Animation mExplode;

    /* Menu */
    private Menu mMenu;

    /* Container Views */
    private ViewGroup mItemsMenuViewGroup;
    private CoordinatorLayout mCoordinatorLayout;
    private View mFilterShopMenuLayout;
    private View mFilterSectionMenuLayout;
    private View mFilterPriceMenuLayout;
    private View mFilterColorMenuLayout;
    private View mFilterNewnessMenuLayout;

    /* ImageViews */
    private ImageView mFilterShopImageView;
    private ImageView mFilterSectionImageView;
    private ImageView mFilterPriceImageView;
    private ImageView mFilterColorImageView;
    private ImageView mFilterNewnessImageView;

    /* TextViews */
    private TextView mFilterShopTextView;
    private TextView mFilterSectionTextView;
    private TextView mFilterColorTextView;
    private TextView mFilterPriceTextView;

    /* RadioButtons */
    private AppCompatRadioButton mNewnessAllRadioButton;
    private AppCompatRadioButton mNewnessNewRadioButton;

    /* CheckBoxes */
    private List<AppCompatCheckBox> mShopsCheckBoxesList;
    private List<AppCompatCheckBox> mColorCheckBoxesList;
    private AppCompatCheckBox mAllShopsCheckBox;

    private List<AppCompatCheckBox> mSectionCheckBoxesList;

    /* RangeSeekBar */
    private RangeSeekBar mRangeSeekBar;

    /* EditTexts */
    private EditText mPriceFromEditText;
    private EditText mPriceToEditText;

    /* Data */
    private List<String> mShopsList;
    private List<String> mFilterShops;
    private List<String> mFilterColors;
    private List<String> mFilterSections;

    private int mFilterMinPrice;
    private int mFilterMaxPrice;

    private boolean mFilterNewness;
    private boolean SHOP_FILTER_ACTIVE;
    private boolean SECTION_FILTER_ACTIVE;
    private boolean PRICE_FILTER_ACTIVE;
    private boolean COLOR_FILTER_ACTIVE;
    private boolean NEWNESS_FILTER_ACTIVE;

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_filter);

        if (savedInstanceState == null)
        {
            Intent intent = getIntent();

            MAN = intent.getBooleanExtra(Properties.PACKAGE + ".man", false);

            mFilterNewness  = intent.getBooleanExtra(Properties.PACKAGE + ".newness", false);
            mFilterMinPrice = intent.getIntExtra(Properties.PACKAGE + ".minPrice", -1);
            mFilterMaxPrice = intent.getIntExtra(Properties.PACKAGE + ".maxPrice", -1);
            mFilterSections = (ArrayList<String>)intent.getSerializableExtra(Properties.PACKAGE + ".sections");
            mFilterColors   = (ArrayList<String>)intent.getSerializableExtra(Properties.PACKAGE + ".colors");
            mFilterShops    = (ArrayList<String>)intent.getSerializableExtra(Properties.PACKAGE + ".shops");
        }

        _initData();
        _initToolbar();
        _initAnimations();
        _initViews();
        _initFilterItemViews();
        _initFilterMenuViews();
    }

    /**
     * Inicializacion de los distintos datos y ED's.
     */
    protected void _initData()
    {
        SHOP_FILTER_ACTIVE = (mFilterShops != null);
        SECTION_FILTER_ACTIVE = (mFilterSections != null);
        COLOR_FILTER_ACTIVE = (mFilterColors != null);
        PRICE_FILTER_ACTIVE = (mFilterMinPrice != -1) || (mFilterMaxPrice != -1);
        NEWNESS_FILTER_ACTIVE = true;

        mColorCheckBoxesList   = new ArrayList<>();
        mSectionCheckBoxesList = new ArrayList<>();

        mShopsList = new ArrayList<>();
        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(this);
        for (String shop : sharedPreferencesManager.retrieveUser().getShops())
        {
            mShopsList.add(shop);
        }

        SECTION_FILTER_MAN_1  = getResources().getString(R.string.filter_section_abrigos);
        SECTION_FILTER_MAN_2  = getResources().getString(R.string.filter_section_americanas);
        SECTION_FILTER_MAN_3  = getResources().getString(R.string.filter_section_camisas);
        SECTION_FILTER_MAN_4  = getResources().getString(R.string.filter_section_camisetas);
        SECTION_FILTER_MAN_5  = getResources().getString(R.string.filter_section_jerseis);
        SECTION_FILTER_MAN_6  = getResources().getString(R.string.filter_section_pantalones);
        SECTION_FILTER_MAN_7  = getResources().getString(R.string.filter_section_polos);
        SECTION_FILTER_MAN_8  = getResources().getString(R.string.filter_section_shorts);
        SECTION_FILTER_MAN_9  = getResources().getString(R.string.filter_section_sport);
        SECTION_FILTER_MAN_10 = getResources().getString(R.string.filter_section_sudaderas);
        SECTION_FILTER_MAN_11 = getResources().getString(R.string.filter_section_trajes);
        SECTION_FILTER_MAN_12 = getResources().getString(R.string.filter_section_zapatos);

        SECTION_FILTER_WOMAN_1  = getResources().getString(R.string.filter_section_abrigos);
        SECTION_FILTER_WOMAN_2  = getResources().getString(R.string.filter_section_americanas);
        SECTION_FILTER_WOMAN_3  = getResources().getString(R.string.filter_section_camisas);
        SECTION_FILTER_WOMAN_4  = getResources().getString(R.string.filter_section_camisetas);
        SECTION_FILTER_WOMAN_5  = getResources().getString(R.string.filter_section_faldas);
        SECTION_FILTER_WOMAN_6  = getResources().getString(R.string.filter_section_jerseis);
        SECTION_FILTER_WOMAN_7  = getResources().getString(R.string.filter_section_pantalones);
        SECTION_FILTER_WOMAN_8  = getResources().getString(R.string.filter_section_monos);
        SECTION_FILTER_WOMAN_9  = getResources().getString(R.string.filter_section_shorts);
        SECTION_FILTER_WOMAN_10 = getResources().getString(R.string.filter_section_sport);
        SECTION_FILTER_WOMAN_11 = getResources().getString(R.string.filter_section_vestidos);
        SECTION_FILTER_WOMAN_12 = getResources().getString(R.string.filter_section_zapatos);
    }

    /**
     * Inicializacion de la Toolbar.
     */
    protected void _initToolbar()
    {
        Toolbar toolbar  = (Toolbar) findViewById(R.id.filter_appbar);
        TextView mToolbarTextView = (TextView) findViewById(R.id.toolbar_textview);

        mToolbarTextView.setText(getResources().getString(R.string.toolbar_filter));

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        }
    }

    /**
     * Inicializacion de animaciones.
     */
    protected void _initAnimations()
    {
        mExplode = AnimationUtils.loadAnimation(this, R.anim.explode_animation);
        mExplode.setStartOffset(300);
    }

    /**
     * Inicializacion de vistas generales.
     */
    protected void _initViews()
    {
        mCoordinatorLayout  = (CoordinatorLayout)findViewById(R.id.filter_coordinator_layout);
        mItemsMenuViewGroup = (ViewGroup)findViewById(R.id.menu_items);

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingButton);

        // Hacemos aparecer el FloatingButton
        floatingActionButton.setVisibility(View.VISIBLE);
        floatingActionButton.startAnimation(mExplode);

        // [BEGIN] Listener FAB OK
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Log.d(Properties.TAG, "[FILTER_UI] Se hace CLICK -> Aceptar");
                boolean OK = true;

                if (!COLOR_FILTER_ACTIVE &&
                    !SHOP_FILTER_ACTIVE &&
                    !SECTION_FILTER_ACTIVE &&
                    !PRICE_FILTER_ACTIVE &&
                    !NEWNESS_FILTER_ACTIVE)
                {
                    Log.d(Properties.TAG, "[FILTER_UI] No hay ningún filtro establecido");

                    mSnackbar = Snackbar.make(mCoordinatorLayout
                            , "No se ha establecido ningún filtro"
                            , Snackbar.LENGTH_SHORT);

                    mSnackbar.getView().setBackgroundColor(getResources().getColor(android.R.color.white));
                    mSnackbar.setActionTextColor(getResources().getColor(R.color.colorAccent));
                    ((TextView)mSnackbar.getView().findViewById(android.support.design.R.id.snackbar_text))
                            .setTextColor(getResources().getColor(R.color.colorText));

                    mSnackbar.show();

                } else {
                    Log.d(Properties.TAG, "[FILTER_UI] Se buscan productos que cumplan los siguientes filtros:");

                    Intent intent = new Intent();

                    ArrayList<String> shopsList = null;
                    if (SHOP_FILTER_ACTIVE)
                    {
                        Log.d(Properties.TAG, "[FILTER_UI] - De las siguientes tiendas:");

                        boolean none = true;

                        shopsList = new ArrayList<>();

                        // Si esta marcado, metemos en la lista el tag ALL.
                        if (!mAllShopsCheckBox.isChecked())
                        {
                            for (AppCompatCheckBox checkBox : mShopsCheckBoxesList)
                            {
                                if (checkBox.isChecked())
                                {
                                    Log.d(Properties.TAG, "[FILTER_UI]  - " + checkBox.getText().toString());

                                    none = false;

                                    shopsList.add(checkBox.getText().toString());
                                }
                            }

                        } else {
                            Log.d(Properties.TAG, "[FILTER_UI]  - Todas ");

                            none = false;

                            shopsList.add(ALL);
                        }

                        if (none)
                        {
                            Log.d(Properties.TAG, "[FILTER_UI]  - No se ha marcado ninguna tienda");

                            OK = false;

                            YoYo.with(Techniques.Shake)
                                .duration(400)
                                .playOn(mFilterShopMenuLayout);
                        }
                    }

                    ArrayList<String> colorsList = null;
                    if (COLOR_FILTER_ACTIVE)
                    {
                        Log.d(Properties.TAG, "[FILTER_UI] De los siguientes colores:");

                        boolean none = true;

                        colorsList = new ArrayList<>();
                        for (AppCompatCheckBox checkBox : mColorCheckBoxesList)
                        {
                            if (checkBox.isChecked())
                            {
                                Log.d(Properties.TAG, "[FILTER_UI]  - " + checkBox.getText().toString());

                                none = false;

                                colorsList.add(checkBox.getText().toString());
                            }
                        }

                        if (none)
                        {
                            Log.d(Properties.TAG, "[FILTER_UI]  - No se ha marcado ningún color");

                            OK = false;

                            YoYo.with(Techniques.Shake)
                                .duration(400)
                                .playOn(mFilterColorMenuLayout);
                        }
                    }

                    ArrayList<String> sectionsList = null;
                    if (SECTION_FILTER_ACTIVE)
                    {
                        Log.d(Properties.TAG, "[FILTER_UI] De las siguientes secciones:");

                        boolean none = true;

                        sectionsList = new ArrayList<>();
                        for (AppCompatCheckBox checkBox : mSectionCheckBoxesList)
                        {
                            if (checkBox.isChecked())
                            {
                                Log.d(Properties.TAG, "[FILTER_UI]  - " + checkBox.getText().toString());

                                none = false;

                                sectionsList.add(checkBox.getText().toString());
                            }
                        }

                        if (none)
                        {
                            Log.d(Properties.TAG, "[FILTER_UI]  - No se ha marcado ninguna sección");

                            OK = false;

                            YoYo.with(Techniques.Shake)
                                .duration(400)
                                .playOn(mFilterSectionMenuLayout);
                        }
                    }

                    int from = -1;
                    int to = -1;
                    if (PRICE_FILTER_ACTIVE)
                    {
                        int lengthFrom = mPriceFromEditText.getText().length();
                        int lengthTo = mPriceToEditText.getText().length();

                        from = (lengthFrom == 0) ? from : Integer.valueOf(mPriceFromEditText.getText().toString());
                        to = (lengthTo == 0) ? to : Integer.valueOf(mPriceToEditText.getText().toString());

                        if (lengthFrom == 0 && lengthTo == 0)
                        {
                            OK = false;

                            YoYo.with(Techniques.Shake)
                                .duration(400)
                                .playOn(mFilterPriceMenuLayout);
                        } else {
                            Log.d(Properties.TAG, "[FILTER_UI] - A partir de " + from + "€ hasta " + to + "€");
                        }
                    }

                    if (OK)
                    {
                        final boolean newness = mNewnessNewRadioButton.isChecked();

                        Log.d(Properties.TAG, "[FILTER_UI] " + ((newness) ? "Sólo novedades" : "Todos los productos"));

                        Log.d(Properties.TAG, "[FILTER_UI] Todos los filtros introducidos son correctos");
                        Log.d(Properties.TAG, "[FILTER_UI] Se vuelve a la Activity -> MainScreenUI");

                        intent.putExtra(Properties.PACKAGE + ".shops", shopsList);
                        intent.putExtra(Properties.PACKAGE + ".colors", colorsList);
                        intent.putExtra(Properties.PACKAGE + ".sections", sectionsList);
                        intent.putExtra(Properties.PACKAGE + ".minPrice", from);
                        intent.putExtra(Properties.PACKAGE + ".maxPrice", to);
                        intent.putExtra(Properties.PACKAGE + ".newness", newness);
                        intent.putExtra(Properties.PACKAGE + ".search", (String)null);

                        setResult(RESULT_OK, intent);

                        finish();
                    }
                }
            }
        });
    }

    /**
     * Inicializacion de los elementos de la lista de filtros.
     */
    protected void _initFilterItemViews()
    {
        RelativeLayout mFilterShopItemLayout    = (RelativeLayout) findViewById(R.id.filter_shop);
        RelativeLayout mFilterSectionItemLayout = (RelativeLayout) findViewById(R.id.filter_section);
        RelativeLayout mFilterPriceItemLayout   = (RelativeLayout) findViewById(R.id.filter_price);
        RelativeLayout mFilterColorItemLayout   = (RelativeLayout) findViewById(R.id.filter_color);

        mFilterShopImageView    = (ImageView)findViewById(R.id.filter_image_shop);
        mFilterSectionImageView = (ImageView)findViewById(R.id.filter_image_section);
        mFilterPriceImageView   = (ImageView)findViewById(R.id.filter_image_price);
        mFilterColorImageView   = (ImageView)findViewById(R.id.filter_image_color);
        mFilterNewnessImageView = (ImageView)findViewById(R.id.filter_image_newness);

        mFilterShopImageView.setAlpha((mFilterShops == null || mFilterShops.isEmpty())
                ? ALPHA_INACTIVE_FILTER : ALPHA_ACTIVE_FILTER);
        mFilterSectionImageView.setAlpha((mFilterSections == null || mFilterSections.isEmpty())
                ? ALPHA_INACTIVE_FILTER : ALPHA_ACTIVE_FILTER);
        mFilterPriceImageView.setAlpha(((mFilterMinPrice == -1) && (mFilterMaxPrice == -1))
                ? ALPHA_INACTIVE_FILTER : ALPHA_ACTIVE_FILTER);
        mFilterColorImageView.setAlpha((mFilterColors == null || mFilterColors.isEmpty())
                ? ALPHA_INACTIVE_FILTER : ALPHA_ACTIVE_FILTER);
        mFilterNewnessImageView.setAlpha(ALPHA_ACTIVE_FILTER);

        mFilterShopItemLayout.setOnClickListener(this);
        mFilterSectionItemLayout.setOnClickListener(this);
        mFilterPriceItemLayout.setOnClickListener(this);
        mFilterColorItemLayout.setOnClickListener(this);
    }

    /**
     * Inicializacion de los menus de cada filtro.
     */
    protected void _initFilterMenuViews()
    {
        mFilterShopMenuLayout    = findViewById(R.id.filter_item_shop_menu);
        mFilterSectionMenuLayout = findViewById(R.id.filter_item_section_menu);
        mFilterPriceMenuLayout   = findViewById(R.id.filter_item_price_menu);
        mFilterColorMenuLayout   = findViewById(R.id.filter_item_color_menu);
        mFilterNewnessMenuLayout = findViewById(R.id.filter_item_newness_menu);

        ImageButton mFilterShopRemove    = (ImageButton) findViewById(R.id.filter_item_shop_clear);
        ImageButton mFilterSectionRemove = (ImageButton) findViewById(R.id.filter_item_section_clear);
        ImageButton mFilterPriceRemove   = (ImageButton) findViewById(R.id.filter_item_price_clear);
        ImageButton mFilterColorRemove   = (ImageButton) findViewById(R.id.filter_item_color_clear);

        mFilterShopRemove.setOnClickListener(this);
        mFilterSectionRemove.setOnClickListener(this);
        mFilterPriceRemove.setOnClickListener(this);
        mFilterColorRemove.setOnClickListener(this);

        _initFilterColor();
        _initFilterSection();
        _initFilterNewness();
        _initFilterPrice();
        _initFilterShop();
    }

    /**
     * Metodo para inicializar el menu de colores.
     */
    @SuppressWarnings("deprecation")
    protected void _initFilterColor()
    {
        AppCompatCheckBox mColorYellowCheckBox = (AppCompatCheckBox) findViewById(R.id.filter_color_yellow);
        AppCompatCheckBox mColorBlueCheckBox   = (AppCompatCheckBox) findViewById(R.id.filter_color_blue);
        AppCompatCheckBox mColorBeigeCheckBox  = (AppCompatCheckBox) findViewById(R.id.filter_color_beige);
        AppCompatCheckBox mColorWhiteCheckBox  = (AppCompatCheckBox) findViewById(R.id.filter_color_white);
        AppCompatCheckBox mColorGreyCheckBox   = (AppCompatCheckBox) findViewById(R.id.filter_color_grey);
        AppCompatCheckBox mColorBrownCheckBox  = (AppCompatCheckBox) findViewById(R.id.filter_color_brown);
        AppCompatCheckBox mColorPurpleCheckBox = (AppCompatCheckBox) findViewById(R.id.filter_color_purple);
        AppCompatCheckBox mColorBlackCheckBox  = (AppCompatCheckBox) findViewById(R.id.filter_color_black);
        AppCompatCheckBox mColorRedCheckBox    = (AppCompatCheckBox) findViewById(R.id.filter_color_red);
        AppCompatCheckBox mColorPinkCheckBox   = (AppCompatCheckBox) findViewById(R.id.filter_color_pink);
        AppCompatCheckBox mColorGreenCheckBox  = (AppCompatCheckBox) findViewById(R.id.filter_color_green);

        mColorCheckBoxesList.add(mColorYellowCheckBox); mColorCheckBoxesList.add(mColorBlueCheckBox);
        mColorCheckBoxesList.add(mColorBeigeCheckBox); mColorCheckBoxesList.add(mColorWhiteCheckBox);
        mColorCheckBoxesList.add(mColorGreyCheckBox); mColorCheckBoxesList.add(mColorBrownCheckBox);
        mColorCheckBoxesList.add(mColorPurpleCheckBox); mColorCheckBoxesList.add(mColorBlackCheckBox);
        mColorCheckBoxesList.add(mColorRedCheckBox); mColorCheckBoxesList.add(mColorPinkCheckBox);
        mColorCheckBoxesList.add(mColorGreenCheckBox);

        mFilterColorTextView = (TextView)findViewById(R.id.filter_text_color);

        ((ViewGroup)mFilterColorMenuLayout.getParent()).removeView(mFilterColorMenuLayout);

        if (COLOR_FILTER_ACTIVE)
        {
            mFilterColorImageView.setScaleX(1.1f);
            mFilterColorImageView.setScaleY(1.1f);
            mFilterColorImageView.setAlpha(ALPHA_ACTIVE_FILTER);

            mFilterColorTextView.setTextColor(getResources().getColor(R.color.colorText));

            mItemsMenuViewGroup.addView(mFilterColorMenuLayout, 0);

            for (String color : mFilterColors)
            {
                for (AppCompatCheckBox checkBox : mColorCheckBoxesList)
                {
                    if (checkBox.getText().equals(color))
                    {
                        checkBox.setChecked(true);
                    }
                }
            }
        }
    }

    /**
     * Metodo para inicializar el menu de secciones.
     */
    @SuppressWarnings("deprecation")
    protected void _initFilterSection()
    {
        AppCompatCheckBox mSection1CheckBox  = (AppCompatCheckBox) findViewById(R.id.filter_section_1);
        AppCompatCheckBox mSection2CheckBox  = (AppCompatCheckBox) findViewById(R.id.filter_section_2);
        AppCompatCheckBox mSection3CheckBox  = (AppCompatCheckBox) findViewById(R.id.filter_section_3);
        AppCompatCheckBox mSection4CheckBox  = (AppCompatCheckBox) findViewById(R.id.filter_section_4);
        AppCompatCheckBox mSection5CheckBox  = (AppCompatCheckBox) findViewById(R.id.filter_section_5);
        AppCompatCheckBox mSection6CheckBox  = (AppCompatCheckBox) findViewById(R.id.filter_section_6);
        AppCompatCheckBox mSection7CheckBox  = (AppCompatCheckBox) findViewById(R.id.filter_section_7);
        AppCompatCheckBox mSection8CheckBox  = (AppCompatCheckBox) findViewById(R.id.filter_section_8);
        AppCompatCheckBox mSection9CheckBox  = (AppCompatCheckBox) findViewById(R.id.filter_section_9);
        AppCompatCheckBox mSection10CheckBox = (AppCompatCheckBox) findViewById(R.id.filter_section_10);
        AppCompatCheckBox mSection11CheckBox = (AppCompatCheckBox) findViewById(R.id.filter_section_11);
        AppCompatCheckBox mSection12CheckBox = (AppCompatCheckBox) findViewById(R.id.filter_section_12);

        mSectionCheckBoxesList.add(mSection1CheckBox); mSectionCheckBoxesList.add(mSection2CheckBox);
        mSectionCheckBoxesList.add(mSection3CheckBox); mSectionCheckBoxesList.add(mSection4CheckBox);
        mSectionCheckBoxesList.add(mSection5CheckBox); mSectionCheckBoxesList.add(mSection6CheckBox);
        mSectionCheckBoxesList.add(mSection7CheckBox); mSectionCheckBoxesList.add(mSection8CheckBox);
        mSectionCheckBoxesList.add(mSection9CheckBox); mSectionCheckBoxesList.add(mSection10CheckBox);
        mSectionCheckBoxesList.add(mSection11CheckBox); mSectionCheckBoxesList.add(mSection12CheckBox);

        mFilterSectionTextView = (TextView)findViewById(R.id.filter_text_section);

        ((ViewGroup)mFilterSectionMenuLayout.getParent()).removeView(mFilterSectionMenuLayout);

        if (MAN)
        {
            mSectionCheckBoxesList.get(0).setText(SECTION_FILTER_MAN_1);
            mSectionCheckBoxesList.get(1).setText(SECTION_FILTER_MAN_2);
            mSectionCheckBoxesList.get(2).setText(SECTION_FILTER_MAN_3);
            mSectionCheckBoxesList.get(3).setText(SECTION_FILTER_MAN_4);
            mSectionCheckBoxesList.get(4).setText(SECTION_FILTER_MAN_5);
            mSectionCheckBoxesList.get(5).setText(SECTION_FILTER_MAN_6);
            mSectionCheckBoxesList.get(6).setText(SECTION_FILTER_MAN_7);
            mSectionCheckBoxesList.get(7).setText(SECTION_FILTER_MAN_8);
            mSectionCheckBoxesList.get(8).setText(SECTION_FILTER_MAN_9);
            mSectionCheckBoxesList.get(9).setText(SECTION_FILTER_MAN_10);
            mSectionCheckBoxesList.get(10).setText(SECTION_FILTER_MAN_11);
            mSectionCheckBoxesList.get(11).setText(SECTION_FILTER_MAN_12);

        } else {
            mSectionCheckBoxesList.get(0).setText(SECTION_FILTER_WOMAN_1);
            mSectionCheckBoxesList.get(1).setText(SECTION_FILTER_WOMAN_2);
            mSectionCheckBoxesList.get(2).setText(SECTION_FILTER_WOMAN_3);
            mSectionCheckBoxesList.get(3).setText(SECTION_FILTER_WOMAN_4);
            mSectionCheckBoxesList.get(4).setText(SECTION_FILTER_WOMAN_5);
            mSectionCheckBoxesList.get(5).setText(SECTION_FILTER_WOMAN_6);
            mSectionCheckBoxesList.get(6).setText(SECTION_FILTER_WOMAN_7);
            mSectionCheckBoxesList.get(7).setText(SECTION_FILTER_WOMAN_8);
            mSectionCheckBoxesList.get(8).setText(SECTION_FILTER_WOMAN_9);
            mSectionCheckBoxesList.get(9).setText(SECTION_FILTER_WOMAN_10);
            mSectionCheckBoxesList.get(10).setText(SECTION_FILTER_WOMAN_11);
            mSectionCheckBoxesList.get(11).setText(SECTION_FILTER_WOMAN_12);
        }

        if (SECTION_FILTER_ACTIVE)
        {
            mFilterSectionImageView.setScaleX(1.1f);
            mFilterSectionImageView.setScaleY(1.1f);
            mFilterSectionImageView.setAlpha(ALPHA_ACTIVE_FILTER);

            mFilterSectionTextView.setTextColor(getResources().getColor(R.color.colorText));

            for (String section : mFilterSections)
            {
                for (AppCompatCheckBox checkBox : mSectionCheckBoxesList)
                {
                    if (checkBox.getText().toString().equals(section))
                    {
                        checkBox.setChecked(true);
                    }
                }
            }

            mItemsMenuViewGroup.addView(mFilterSectionMenuLayout, 0);
        }
    }

    /**
     * Metodo para inicializar el menu de novedades.
     */
    @SuppressWarnings("deprecation")
    protected void _initFilterNewness()
    {
        TextView mFilterNewnessTextView = (TextView) findViewById(R.id.filter_text_newness);

        mFilterNewnessTextView.setTextColor(getResources().getColor(R.color.colorText));

        if (NEWNESS_FILTER_ACTIVE)
        {
            ((ViewGroup)mFilterNewnessMenuLayout.getParent()).removeView(mFilterNewnessMenuLayout);

            mItemsMenuViewGroup.addView(mFilterNewnessMenuLayout, 0);

            mFilterNewnessImageView.setScaleX(1.1f);
            mFilterNewnessImageView.setScaleY(1.1f);
            mFilterNewnessImageView.setAlpha(ALPHA_ACTIVE_FILTER);

            mNewnessAllRadioButton = (AppCompatRadioButton)findViewById(R.id.newness_all_radio_button);
            mNewnessNewRadioButton = (AppCompatRadioButton)findViewById(R.id.newness_new_radio_button);

            mNewnessAllRadioButton.setTypeface(TypeFaceSingleton.getTypeFace(this, "Existence-StencilLight.otf"));
            mNewnessNewRadioButton.setTypeface(TypeFaceSingleton.getTypeFace(this, "Existence-StencilLight.otf"));

            mNewnessNewRadioButton.setChecked(false);
            mNewnessNewRadioButton.setChecked(false);

            if (mFilterNewness)
                mNewnessNewRadioButton.setChecked(true);
            else
                mNewnessAllRadioButton.setChecked(true);
        }
    }

    /**
     * Metodo para inicializa el menu de precios.
     */
    @SuppressWarnings("unchecked, deprecaction")
    protected void _initFilterPrice()
    {
        mFilterPriceTextView = (TextView)findViewById(R.id.filter_text_price);

        mRangeSeekBar = (RangeSeekBar)findViewById(R.id.filter_price_range_seek_bar);
        mRangeSeekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener()
        {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Object minValue, Object maxValue)
            {
                int from = (int) bar.getSelectedMinValue();
                int to = (int) bar.getSelectedMaxValue();

                mPriceFromEditText.setText((from == 0) ? "" : Integer.toString(from));
                mPriceToEditText.setText((to == 100) ? "" : Integer.toString(to));
            }
        });

        mPriceFromEditText = (EditText)findViewById(R.id.filter_price_from);
        mPriceToEditText   = (EditText)findViewById(R.id.filter_price_to);

        mPriceFromEditText.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (!v.getText().toString().isEmpty())
                {
                    int from = Integer.valueOf(mPriceFromEditText.getText().toString());
                    int to = 999;

                    if (mPriceToEditText.getText() != null && !mPriceToEditText.getText().toString().isEmpty())
                        to = Integer.valueOf(mPriceToEditText.getText().toString());

                    Log.d(Properties.TAG, Integer.toString(from) + "|" + Integer.toString(to));

                    if (from > to)
                    {
                        mRangeSeekBar.setSelectedMaxValue(from);
                        mPriceToEditText.setText(Integer.toString(from));
                    }

                    mRangeSeekBar.setSelectedMinValue(from);

                } else {
                    mRangeSeekBar.setSelectedMinValue(mRangeSeekBar.getAbsoluteMinValue());
                }

                return false;
            }
        });

        mPriceToEditText.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (!v.getText().toString().isEmpty())
                {
                    int from = -1;
                    int to = Integer.valueOf(mPriceToEditText.getText().toString());

                    if (mPriceFromEditText.getText() != null && !mPriceFromEditText.getText().toString().isEmpty())
                        from = Integer.valueOf(mPriceFromEditText.getText().toString());

                    if (from > to)
                    {
                        mRangeSeekBar.setSelectedMinValue(to);
                        mPriceFromEditText.setText(Integer.toString(to));
                    }

                    mRangeSeekBar.setSelectedMaxValue(to);

                } else {
                    mRangeSeekBar.setSelectedMaxValue(mRangeSeekBar.getAbsoluteMaxValue());
                }

                return false;
            }
        });

        ((ViewGroup)mFilterPriceMenuLayout.getParent()).removeView(mFilterPriceMenuLayout);
        if (PRICE_FILTER_ACTIVE)
        {
            mFilterPriceImageView.setScaleX(1.1f);
            mFilterPriceImageView.setScaleY(1.1f);
            mFilterPriceImageView.setAlpha(ALPHA_ACTIVE_FILTER);

            mFilterPriceTextView.setTextColor(getResources().getColor(R.color.colorText));

            if (mFilterMinPrice > 0)
            {
                mPriceFromEditText.setText(Integer.toString(mFilterMinPrice));
                mRangeSeekBar.setSelectedMinValue(mFilterMinPrice);
            }

            if (mFilterMaxPrice > 0)
            {
                mPriceToEditText.setText(Integer.toString(mFilterMaxPrice));
                mRangeSeekBar.setSelectedMaxValue(mFilterMaxPrice);
            }

            mItemsMenuViewGroup.addView(mFilterPriceMenuLayout, 0);
        }
    }

    /**
     * Metodo para inicializar el menu de tiendas.
     */
    @SuppressWarnings("deprecation, InflateParams" )
    protected void _initFilterShop()
    {
        mShopsCheckBoxesList = new ArrayList<>();

        mFilterShopTextView = (TextView)findViewById(R.id.filter_text_shop);

        mAllShopsCheckBox = (AppCompatCheckBox)findViewById(R.id.filterAllShops);
        mAllShopsCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked)
            {
                for (AppCompatCheckBox checkBox : mShopsCheckBoxesList)
                {
                    checkBox.setEnabled(!isChecked);

                    checkBox.animate()
                            .alpha((isChecked) ? ALPHA_INACTIVE_FILTER : ALPHA_ACTIVE_FILTER)
                            .setDuration(100);
                }
            }
        });

        LinearLayout mRightShopList = (LinearLayout) findViewById(R.id.rightShopList);
        LinearLayout mLeftShopList  = (LinearLayout) findViewById(R.id.leftShopList);

        // Ordenamos las tiendas por orden alfabetico.
        Collections.sort(mShopsList);
        for (int i = 0; i < mShopsList.size(); i++)
        {
            // Inflamos el layout con el checkbox.
            final View view = LayoutInflater.from(this).inflate(R.layout.shop_checkbox_item, null);

            // Obtenemos el checkbox y le cambiamos el texto.
            final AppCompatCheckBox shopCheckbox = (AppCompatCheckBox) view.findViewById(R.id.shop_checkbox);
            shopCheckbox.setText(mShopsList.get(i));

            // Lo guardamos en una lista.
            mShopsCheckBoxesList.add(shopCheckbox);

            // Los pares a la izquierda, los impares a la derecha.
            if ((i % 2) == 0)
            {
                mLeftShopList.addView(view);

            } else {
                mRightShopList.addView(view);
            }
        }

        if (SHOP_FILTER_ACTIVE)
        {
            mFilterShopImageView.setScaleX(1.1f);
            mFilterShopImageView.setScaleY(1.1f);
            mFilterShopImageView.setAlpha(ALPHA_ACTIVE_FILTER);

            mFilterShopTextView.setTextColor(getResources().getColor(R.color.colorText));

            ((ViewGroup)mFilterShopMenuLayout.getParent()).removeView(mFilterShopMenuLayout);

            mItemsMenuViewGroup.addView(mFilterShopMenuLayout, 0);

            // Marcamos los que vengan en el mapa de filtros, si viene solo ALL, marcamos el check de mAllShops
            if ((!mFilterShops.isEmpty()) && !(mFilterShops.get(0).equals(ALL)))
            {
                for (String shop : mFilterShops)
                {
                    for (AppCompatCheckBox checkBox : mShopsCheckBoxesList)
                    {
                        if (shop.equals(checkBox.getText().toString()))
                        {
                            checkBox.setChecked(true);
                        }
                    }
                }

            } else {
                mAllShopsCheckBox.setChecked(true);
            }

        } else {
            for (AppCompatCheckBox checkBox : mShopsCheckBoxesList)
            {
                checkBox.setChecked(true);
            }

            ((ViewGroup)mFilterShopMenuLayout.getParent()).removeView(mFilterShopMenuLayout);
        }
    }

    /**
     * Metodo que resetea el filtro de tiendas.
     */
    protected void _resetFilterShop()
    {
        for (AppCompatCheckBox checkBox : mShopsCheckBoxesList)
        {
            checkBox.setChecked(false);
        }
    }

    /**
     * Metodo que resetea el filtro de secciones.
     */
    protected void _resetFilterSection()
    {
        for (AppCompatCheckBox checkBox : mSectionCheckBoxesList)
        {
            checkBox.setChecked(false);
        }
    }

    /**
     * Metodo que resetea el filtro de colores.
     */
    protected void _resetFilterColor()
    {
        for (AppCompatCheckBox checkBox : mColorCheckBoxesList)
        {
            checkBox.setChecked(false);
        }
    }

    /**
     * Metodo que resetea el filtro de precios.
     */
    @SuppressWarnings("unchecked")
    protected void _resetFilterPrice()
    {
        mRangeSeekBar.setSelectedMaxValue(mRangeSeekBar.getAbsoluteMaxValue());
        mRangeSeekBar.setSelectedMinValue(mRangeSeekBar.getAbsoluteMinValue());

        mPriceFromEditText.setText("");
        mPriceToEditText.setText("");
    }

    /**
     * Metodo que resetea el filtro de novedades.
     */
    protected void _resetFilterNewness()
    {
        mNewnessNewRadioButton.setChecked(false);
        mNewnessAllRadioButton.setChecked(true);
    }

    /**
     * Metodo que resetea todos los filtros.
     */
    @SuppressWarnings("deprecation")
    protected void _resetFilter()
    {
        Log.d(Properties.TAG, "[FILTER_UI] Se resetean los filtros");

        mSnackbar = Snackbar.make(mCoordinatorLayout, "Filtros restablecidos", Snackbar.LENGTH_SHORT);

        mSnackbar.getView().setBackgroundColor(getResources().getColor(android.R.color.white));
        mSnackbar.setActionTextColor(getResources().getColor(R.color.colorAccent));
        ((TextView)mSnackbar.getView().findViewById(android.support.design.R.id.snackbar_text))
                .setTextColor(getResources().getColor(R.color.colorText));

        mSnackbar.show();

        _resetFilterShop();
        if (SHOP_FILTER_ACTIVE)
        {
            SHOP_FILTER_ACTIVE = false;

            mFilterShopImageView.animate().setDuration(250)
                    .scaleXBy(-0.1f)
                    .scaleYBy(-0.1f)
                    .alpha(ALPHA_INACTIVE_FILTER)
                    .setInterpolator(new OvershootInterpolator());

            mFilterShopTextView.setTextColor(getResources().getColor(R.color.colorLightText));

            mItemsMenuViewGroup.removeView(mFilterShopMenuLayout);
        }

        _resetFilterSection();
        if (SECTION_FILTER_ACTIVE)
        {
            SECTION_FILTER_ACTIVE = false;

            mFilterSectionImageView.animate().setDuration(250)
                    .scaleXBy(-0.1f)
                    .scaleYBy(-0.1f)
                    .alpha(ALPHA_INACTIVE_FILTER)
                    .setInterpolator(new OvershootInterpolator());

            mFilterSectionTextView.setTextColor(getResources().getColor(R.color.colorLightText));

            mItemsMenuViewGroup.removeView(mFilterSectionMenuLayout);
        }

        _resetFilterColor();
        if (COLOR_FILTER_ACTIVE)
        {
            COLOR_FILTER_ACTIVE = false;

            mFilterColorImageView.animate().setDuration(250)
                    .scaleXBy(-0.1f)
                    .scaleYBy(-0.1f)
                    .alpha(ALPHA_INACTIVE_FILTER)
                    .setInterpolator(new OvershootInterpolator());

            mFilterColorTextView.setTextColor(getResources().getColor(R.color.colorLightText));

            mItemsMenuViewGroup.removeView(mFilterColorMenuLayout);
        }

        _resetFilterPrice();
        if (PRICE_FILTER_ACTIVE)
        {
            PRICE_FILTER_ACTIVE = false;

            mFilterPriceImageView.animate().setDuration(250)
                    .scaleXBy(-0.1f)
                    .scaleYBy(-0.1f)
                    .alpha(ALPHA_INACTIVE_FILTER)
                    .setInterpolator(new OvershootInterpolator());

            mFilterPriceTextView.setTextColor(getResources().getColor(R.color.colorLightText));

            mItemsMenuViewGroup.removeView(mFilterPriceMenuLayout);
        }

        _resetFilterNewness();
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onClick(final View view)
    {
        /* [BEGIN Listener en los filtros] */
        if (view.getId() == R.id.filter_shop)
        {
            if (!SHOP_FILTER_ACTIVE)
            {
                SHOP_FILTER_ACTIVE = true;

                mFilterShopImageView.animate().setDuration(250)
                                              .scaleXBy(0.1f)
                                              .scaleYBy(0.1f)
                                              .alpha(ALPHA_ACTIVE_FILTER)
                                              .setInterpolator(new OvershootInterpolator());

                mFilterShopTextView.setTextColor(getResources().getColor(R.color.colorText));

                mItemsMenuViewGroup.addView(mFilterShopMenuLayout, 0);

            } else {
                SHOP_FILTER_ACTIVE = false;

                mFilterShopImageView.animate().setDuration(250)
                                              .scaleXBy(-0.1f)
                                              .scaleYBy(-0.1f)
                                              .alpha(ALPHA_INACTIVE_FILTER)
                                              .setInterpolator(new OvershootInterpolator());

                mFilterShopTextView.setTextColor(getResources().getColor(R.color.colorLightText));

                mItemsMenuViewGroup.removeView(mFilterShopMenuLayout);

                mSnackbar = Snackbar.make(mCoordinatorLayout, "Filtro eliminado", Snackbar.LENGTH_SHORT);

                mSnackbar.getView().setBackgroundColor(getResources().getColor(android.R.color.white));
                mSnackbar.setActionTextColor(getResources().getColor(R.color.colorAccent));
                ((TextView)mSnackbar.getView().findViewById(android.support.design.R.id.snackbar_text))
                        .setTextColor(getResources().getColor(R.color.colorText));

                mSnackbar.show();
            }
        }

        if (view.getId() == R.id.filter_section)
        {
            if (!SECTION_FILTER_ACTIVE)
            {
                SECTION_FILTER_ACTIVE = true;

                mFilterSectionImageView.animate().setDuration(250)
                                                 .scaleXBy(0.1f)
                                                 .scaleYBy(0.1f)
                                                 .alpha(ALPHA_ACTIVE_FILTER)
                                                 .setInterpolator(new OvershootInterpolator());

                mFilterSectionTextView.setTextColor(getResources().getColor(R.color.colorText));

                mItemsMenuViewGroup.addView(mFilterSectionMenuLayout, 0);

            } else {
                SECTION_FILTER_ACTIVE = false;

                mFilterSectionImageView.animate().setDuration(250)
                                                 .scaleXBy(-0.1f)
                                                 .scaleYBy(-0.1f)
                                                 .alpha(ALPHA_INACTIVE_FILTER)
                                                 .setInterpolator(new OvershootInterpolator());

                mFilterSectionTextView.setTextColor(getResources().getColor(R.color.colorLightText));

                mItemsMenuViewGroup.removeView(mFilterSectionMenuLayout);

                mSnackbar = Snackbar.make(mCoordinatorLayout, "Filtro eliminado", Snackbar.LENGTH_SHORT);

                mSnackbar.getView().setBackgroundColor(getResources().getColor(android.R.color.white));
                mSnackbar.setActionTextColor(getResources().getColor(R.color.colorAccent));
                ((TextView)mSnackbar.getView().findViewById(android.support.design.R.id.snackbar_text))
                        .setTextColor(getResources().getColor(R.color.colorText));

                mSnackbar.show();
            }
        }

        if (view.getId() == R.id.filter_price)
        {
            if (!PRICE_FILTER_ACTIVE)
            {
                PRICE_FILTER_ACTIVE = true;

                mFilterPriceImageView.animate().setDuration(250)
                                               .scaleXBy(0.1f)
                                               .scaleYBy(0.1f)
                                               .alpha(ALPHA_ACTIVE_FILTER)
                                               .setInterpolator(new OvershootInterpolator());

                mFilterPriceTextView.setTextColor(getResources().getColor(R.color.colorText));

                mItemsMenuViewGroup.addView(mFilterPriceMenuLayout, 0);

            } else {
                PRICE_FILTER_ACTIVE = false;

                mFilterPriceImageView.animate().setDuration(250)
                                               .scaleXBy(-0.1f)
                                               .scaleYBy(-0.1f)
                                               .alpha(ALPHA_INACTIVE_FILTER)
                                               .setInterpolator(new OvershootInterpolator());

                mFilterPriceTextView.setTextColor(getResources().getColor(R.color.colorLightText));

                mItemsMenuViewGroup.removeView(mFilterPriceMenuLayout);

                mSnackbar = Snackbar.make(mCoordinatorLayout, "Filtro eliminado", Snackbar.LENGTH_SHORT);

                mSnackbar.getView().setBackgroundColor(getResources().getColor(android.R.color.white));
                mSnackbar.setActionTextColor(getResources().getColor(R.color.colorAccent));
                ((TextView)mSnackbar.getView().findViewById(android.support.design.R.id.snackbar_text))
                        .setTextColor(getResources().getColor(R.color.colorText));

                mSnackbar.show();
            }
        }

        if (view.getId() == R.id.filter_color)
        {
            if (!COLOR_FILTER_ACTIVE)
            {
                COLOR_FILTER_ACTIVE = true;

                mFilterColorImageView.animate().setDuration(250)
                                               .scaleXBy(0.1f)
                                               .scaleYBy(0.1f)
                                               .alpha(ALPHA_ACTIVE_FILTER)
                                               .setInterpolator(new OvershootInterpolator());

                mFilterColorTextView.setTextColor(getResources().getColor(R.color.colorText));

                mItemsMenuViewGroup.addView(mFilterColorMenuLayout, 0);

            } else {
                COLOR_FILTER_ACTIVE = false;

                mFilterColorImageView.animate().setDuration(250)
                                               .scaleXBy(-0.1f)
                                               .scaleYBy(-0.1f)
                                               .alpha(ALPHA_INACTIVE_FILTER)
                                               .setInterpolator(new OvershootInterpolator());

                mFilterColorTextView.setTextColor(getResources().getColor(R.color.colorLightText));

                mItemsMenuViewGroup.removeView(mFilterColorMenuLayout);

                mSnackbar = Snackbar.make(mCoordinatorLayout, "Filtro eliminado", Snackbar.LENGTH_SHORT);

                mSnackbar.getView().setBackgroundColor(getResources().getColor(android.R.color.white));
                mSnackbar.setActionTextColor(getResources().getColor(R.color.colorAccent));
                ((TextView)mSnackbar.getView().findViewById(android.support.design.R.id.snackbar_text))
                        .setTextColor(getResources().getColor(R.color.colorText));

                mSnackbar.show();
            }
        }

        /* [BEGIN Listeners en los botones para cerrar un filtro] */
        if (view.getId() == R.id.filter_item_shop_clear)
        {
            SHOP_FILTER_ACTIVE = false;

            mFilterShopImageView.animate().setDuration(250)
                                          .scaleXBy(-0.1f)
                                          .scaleYBy(-0.1f)
                                          .alpha(ALPHA_INACTIVE_FILTER)
                                          .setInterpolator(new OvershootInterpolator());

            mItemsMenuViewGroup.removeView(mFilterShopMenuLayout);

            mSnackbar = Snackbar.make(mCoordinatorLayout, "Filtro eliminado", Snackbar.LENGTH_SHORT);

            mSnackbar.getView().setBackgroundColor(getResources().getColor(android.R.color.white));
            mSnackbar.setActionTextColor(getResources().getColor(R.color.colorAccent));
            ((TextView)mSnackbar.getView().findViewById(android.support.design.R.id.snackbar_text))
                    .setTextColor(getResources().getColor(R.color.colorText));

            mSnackbar.show();
        }

        if (view.getId() == R.id.filter_item_section_clear)
        {
            SECTION_FILTER_ACTIVE = false;

            mFilterSectionImageView.animate().setDuration(250)
                                             .scaleXBy(-0.1f)
                                             .scaleYBy(-0.1f)
                                             .alpha(ALPHA_INACTIVE_FILTER)
                                             .setInterpolator(new OvershootInterpolator());

            mItemsMenuViewGroup.removeView(mFilterSectionMenuLayout);

            mSnackbar = Snackbar.make(mCoordinatorLayout, "Filtro eliminado", Snackbar.LENGTH_SHORT);

            mSnackbar.getView().setBackgroundColor(getResources().getColor(android.R.color.white));
            mSnackbar.setActionTextColor(getResources().getColor(R.color.colorAccent));
            ((TextView)mSnackbar.getView().findViewById(android.support.design.R.id.snackbar_text))
                    .setTextColor(getResources().getColor(R.color.colorText));

            mSnackbar.show();
        }

        if (view.getId() == R.id.filter_item_price_clear)
        {
            PRICE_FILTER_ACTIVE = false;

            mFilterPriceImageView.animate().setDuration(250)
                                           .scaleXBy(-0.1f)
                                           .scaleYBy(-0.1f)
                                           .alpha(ALPHA_INACTIVE_FILTER)
                                           .setInterpolator(new OvershootInterpolator());

            mItemsMenuViewGroup.removeView(mFilterPriceMenuLayout);

            mSnackbar = Snackbar.make(mCoordinatorLayout, "Filtro eliminado", Snackbar.LENGTH_SHORT);

            mSnackbar.getView().setBackgroundColor(getResources().getColor(android.R.color.white));
            mSnackbar.setActionTextColor(getResources().getColor(R.color.colorAccent));
            ((TextView)mSnackbar.getView().findViewById(android.support.design.R.id.snackbar_text))
                    .setTextColor(getResources().getColor(R.color.colorText));

            mSnackbar.show();
        }

        if (view.getId() == R.id.filter_item_color_clear)
        {
            COLOR_FILTER_ACTIVE = false;

            mFilterColorImageView.animate().setDuration(250)
                                           .scaleXBy(-0.1f)
                                           .scaleYBy(-0.1f)
                                           .alpha(ALPHA_INACTIVE_FILTER)
                                           .setInterpolator(new OvershootInterpolator());

            mItemsMenuViewGroup.removeView(mFilterColorMenuLayout);

            mSnackbar = Snackbar.make(mCoordinatorLayout, "Filtro eliminado", Snackbar.LENGTH_SHORT);

            mSnackbar.getView().setBackgroundColor(getResources().getColor(android.R.color.white));
            mSnackbar.setActionTextColor(getResources().getColor(R.color.colorAccent));
            ((TextView)mSnackbar.getView().findViewById(android.support.design.R.id.snackbar_text))
                    .setTextColor(getResources().getColor(R.color.colorText));

            mSnackbar.show();
        }

    } /* [END OnClick] */

    @Override
    @SuppressWarnings("deprecation")
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.toolbar_menu_filter, menu);

        mMenu = menu;

        // Associate searchable configuration with the SearchView
        final SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView =
                (SearchView) menu.findItem(R.id.menu_item_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        searchView.setQueryHint(getResources().getString(R.string.search_hint));

        ((EditText)searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text))
                .setTextColor(getResources().getColor(R.color.colorText));

        ((EditText)searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text))
                .setHintTextColor(getResources().getColor(R.color.colorText));

        ((EditText)searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text)).setTypeface(
                TypeFaceSingleton.getTypeFace(this, "Existence-StencilLight.otf"));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                if (Utils.isQueryOk(query))
                {
                    Log.d(Properties.TAG, "[FILTER_UI] Se realiza la búsqueda de: " + query);

                    Intent intent = new Intent();

                    intent.putExtra(Properties.PACKAGE + ".shops", (ArrayList<String>)null);
                    intent.putExtra(Properties.PACKAGE + ".colors", (ArrayList<String>)null);
                    intent.putExtra(Properties.PACKAGE + ".sections", (ArrayList<String>)null);
                    intent.putExtra(Properties.PACKAGE + ".minPrice", -1);
                    intent.putExtra(Properties.PACKAGE + ".maxPrice", -1);
                    intent.putExtra(Properties.PACKAGE + ".newness", false);
                    intent.putExtra(Properties.PACKAGE + ".search", query);

                    setResult(RESULT_OK, intent);

                    finish();
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                if (newText.length() > 1)
                {
                    new GetSuggestionsTask(FilterUI.this, mMenu).execute(newText);
                }

                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            Log.d(Properties.TAG, "[FILTER_UI] Se pulsa el botón Atrás");

            onBackPressed();

            return true;
        }

        if (item.getItemId() == R.id.menu_item_options)
        {
            Log.d(Properties.TAG, "[FILTER_UI] Se hace CLICK -> Restablecer filtros");

            _resetFilter();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        setResult(RESULT_CANCELED);

        super.onBackPressed();
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.left_in_animation, R.anim.left_out_animation);
    }

    /**
     * Tarea en segundo plano para traer las sugerencias del servidor.
     */
    private static class GetSuggestionsTask extends AsyncTask<String, Void, Void>
    {
        private WeakReference<FilterUI> context;
        private WeakReference<Menu> menu;

        private List<String> suggestions = new ArrayList<>();

        public GetSuggestionsTask(FilterUI context, Menu menu)
        {
            this.context = new WeakReference<>(context);
            this.menu = new WeakReference<>(menu);
        }

        @Override
        protected Void doInBackground(String... params)
        {
            JSONArray jsonArray = RestClientSingleton.retrieveSuggestions(context.get(), params[0]);

            if (jsonArray != null)
            {
                try
                {
                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        Log.d(Properties.TAG, "[FILTER_UI] Sugerencia #" + i + ": " + jsonArray.getString(i));
                        suggestions.add(jsonArray.getString(i));
                    }

                } catch (JSONException e) {
                    ExceptionPrinter.printException("FILTER_UI", e);
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused)
        {
            if (context.get() != null)
            {
                String[] columns = new String[] { "_id", "text" };
                Object[] temp = new Object[] { 0, "default" };

                MatrixCursor cursor = new MatrixCursor(columns);

                for(int i = 0; i < suggestions.size(); i++)
                {
                    temp[0] = i;
                    temp[1] = suggestions.get(i);

                    cursor.addRow(temp);
                }

                final SearchView search = (SearchView) menu.get().findItem(R.id.menu_item_search).getActionView();

                search.setSuggestionsAdapter(new SuggestionAdapter(context.get(), cursor, suggestions));
                search.getSuggestionsAdapter().notifyDataSetChanged();
            }
        }

    } /* [END getSuggestionsFromServer] */

    /**
     * Metodo llamado cuando se hace click en el texto de la sugerencia.
     * @param view: texto en el que se hace click.
     */
    public void onClickText(View view)
    {
        TextView textView = (TextView) view;

        if (Utils.isQueryOk(textView.getText().toString()))
        {
            Log.d(Properties.TAG, "[FILTER_UI] Se hace CLICK -> Sugerencia");
            Log.d(Properties.TAG, "[FILTER_UI] Se realiza la búsqueda de: " + textView.getText().toString());

            Intent intent = new Intent();

            intent.putExtra(Properties.PACKAGE + ".shops", (ArrayList<String>)null);
            intent.putExtra(Properties.PACKAGE + ".colors", (ArrayList<String>)null);
            intent.putExtra(Properties.PACKAGE + ".sections", (ArrayList<String>)null);
            intent.putExtra(Properties.PACKAGE + ".minPrice", -1);
            intent.putExtra(Properties.PACKAGE + ".maxPrice", -1);
            intent.putExtra(Properties.PACKAGE + ".newness", false);
            intent.putExtra(Properties.PACKAGE + ".search", textView.getText().toString());

            setResult(RESULT_OK, intent);

            finish();
        }
    }

    /**
     * Metodo llamado cuando se hace click en el boton de la sugerencia.
     * @param view: sugerencia clickada.
     */
    public void onClickButton(View view)
    {
        final ImageButton imageButton = (ImageButton)view.findViewById(R.id.suggestion_include);

        final SearchView search = (SearchView)mMenu.findItem(R.id.menu_item_search).getActionView();

        search.setQuery(imageButton.getContentDescription().toString(), false);
    }

    /**
     * Adapter para las sugerencias.
     */
    public static class SuggestionAdapter extends CursorAdapter
    {
        private List<String> items;
        private TextView text;
        private ImageButton imageButton;

        public SuggestionAdapter(Context context, Cursor cursor, List<String> items)
        {
            super(context, cursor, false);

            this.items = items;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor)
        {
            text.setText(items.get(cursor.getPosition()));
            imageButton.setContentDescription(text.getText());
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent)
        {
            final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            final View view = inflater.inflate(R.layout.suggestion_item, parent, false);

            text        = (TextView) view.findViewById(R.id.suggestion_item);
            imageButton = (ImageButton) view.findViewById(R.id.suggestion_include);

            return view;
        }

        @Override
        public int getCount()
        {
            return items.size();
        }

    } /* [END SuggestionAdapter] */
}
