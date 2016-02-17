package com.wallakoala.wallakoala.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wallakoala.wallakoala.R;
import com.wallakoala.wallakoala.Utils.SharedPreferencesManager;
import com.wallakoala.wallakoala.Views.RangeSeekBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @class Activity con la pantalla de filtros.
 * Created by Daniel Mancebo Aldea on 11/02/2016.
 */

public class FilterUI extends AppCompatActivity implements View.OnClickListener
{
    /* Constants */
    protected static final String TAG = "CUOKA";
    protected static final String PACKAGE = "com.wallakoala.wallakoala";
    protected static final float ALPHA_ACTIVE_FILTER = 0.8f;
    protected static final float ALPHA_INACTIVE_FILTER = 0.2f;
    protected static boolean MAN;

    /* Floating Button */
    protected FloatingActionButton mFloatingActionButton;

    /* Toolbar */
    protected Toolbar mToolbar;

    /* SharedPreferences */
    protected SharedPreferencesManager mSharedPreferences;

    /* Snackbar */
    protected Snackbar mSnackbar;

    /* Animations */
    protected Animation mExplode;

    /* Container Views */
    protected ViewGroup mItemsMenuViewGroup;
    protected CoordinatorLayout mCoordinatorLayout;
    protected RelativeLayout mFilterShopItemLayout;
    protected RelativeLayout mFilterSectionItemLayout;
    protected RelativeLayout mFilterPriceItemLayout;
    protected RelativeLayout mFilterColorItemLayout;
    protected RelativeLayout mFilterNewnessItemLayout;
    protected View mFilterShopMenuLayout;
    protected View mFilterSectionMenuLayout;
    protected View mFilterPriceMenuLayout;
    protected View mFilterColorMenuLayout;
    protected View mFilterNewnessMenuLayout;

    /* ImageButtons */
    protected ImageButton mFilterShopRemove;
    protected ImageButton mFilterSectionRemove;
    protected ImageButton mFilterPriceRemove;
    protected ImageButton mFilterColorRemove;
    protected ImageButton mFilterNewnessRemove;

    /* ImageViews */
    protected ImageView mFilterShopImageView;
    protected ImageView mFilterSectionImageView;
    protected ImageView mFilterPriceImageView;
    protected ImageView mFilterColorImageView;
    protected ImageView mFilterNewnessImageView;

    /* TextViews */
    protected TextView mToolbarTextView;

    /* RadioButtons */
    protected AppCompatRadioButton mNewnessAllRadioButton;
    protected AppCompatRadioButton mNewnessNewRadioButton;

    /* CheckBoxes */
    protected List<AppCompatCheckBox> mMyCheckBoxesList;
    protected List<AppCompatCheckBox> mAllCheckBoxesList;
    protected AppCompatCheckBox mShopAllCheckBox;
    protected AppCompatCheckBox mShopMyCheckBox;
    protected AppCompatCheckBox mShopBlancoCheckBox;
    protected AppCompatCheckBox mShopPedroDelHierroCheckBox;
    protected AppCompatCheckBox mShopSpringfieldCheckBox;
    protected AppCompatCheckBox mShopHyMCheckBox;

    protected AppCompatCheckBox mColorYellowCheckBox;
    protected AppCompatCheckBox mColorBlueCheckBox;
    protected AppCompatCheckBox mColorBeigeCheckBox;
    protected AppCompatCheckBox mColorWhiteCheckBox;
    protected AppCompatCheckBox mColorGreyCheckBox;
    protected AppCompatCheckBox mColorBrownCheckBox;
    protected AppCompatCheckBox mColorPurpleCheckBox;
    protected AppCompatCheckBox mColorBlackCheckBox;
    protected AppCompatCheckBox mColorRedCheckBox;
    protected AppCompatCheckBox mColorPinkCheckBox;
    protected AppCompatCheckBox mColorGreenCheckBox;

    /* RangeSeekBar */
    protected RangeSeekBar mRangeSeekBar;

    /* EditTexts */
    protected EditText mPriceFromEditText;
    protected EditText mPriceToEditText;

    /* Data */
    protected List<String> mShopsList;
    protected List<String> mFilterShops;
    protected List<String> mFilterColors;
    protected List<String> mFilterSections;
    protected Map<String, ?> mFilterMap;
    protected int mFilterMinPrice;
    protected int mFilterMaxPrice;
    protected boolean mFilterNewness;
    protected boolean SHOP_FILTER_ACTIVE;
    protected boolean SECTION_FILTER_ACTIVE;
    protected boolean PRICE_FILTER_ACTIVE;
    protected boolean COLOR_FILTER_ACTIVE;
    protected boolean NEWNESS_FILTER_ACTIVE;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.filter);

        if (savedInstanceState == null)
        {
            Map<String, Object> map = new HashMap<>();

            Intent intent = getIntent();

            // Filtro de novedades (false si no esta activado)
            map.put("newness", intent.getBooleanExtra(PACKAGE + ".newness", false));
            // Filtro de secciones (null si no esta activado)
            map.put("sections", intent.getSerializableExtra(PACKAGE + ".sections"));
            // Filtro de colores (null si no esta activado)
            map.put("colors", intent.getSerializableExtra(PACKAGE + ".colors"));
            // Filtro de tiendas (null si no esta activado)
            map.put("shops", intent.getSerializableExtra(PACKAGE + ".shops"));
            // Filtro de precio (minimo) (-1 si no esta activado)
            map.put("minPrice", intent.getIntExtra(PACKAGE + ".minPrice", -1));
            // Filtro de precio (maximo) (-1 si no esta activado)
            map.put("maxPrice", intent.getIntExtra(PACKAGE + ".maxPrice", -1));

            MAN = intent.getBooleanExtra(PACKAGE + ".man", false);

            mFilterNewness  = intent.getBooleanExtra(PACKAGE + ".newness", false);
            mFilterMinPrice = intent.getIntExtra(PACKAGE + ".minPrice", -1);
            mFilterMaxPrice = intent.getIntExtra(PACKAGE + ".maxPrice", -1);
            mFilterSections = (ArrayList<String>)intent.getSerializableExtra(PACKAGE + ".sections");
            mFilterColors   = (ArrayList<String>)intent.getSerializableExtra(PACKAGE + ".colors");
            mFilterShops    = (ArrayList<String>)intent.getSerializableExtra(PACKAGE + ".shops");

            mFilterMap = map;
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
        SHOP_FILTER_ACTIVE    = (mFilterShops != null);
        SECTION_FILTER_ACTIVE = (mFilterSections != null);
        PRICE_FILTER_ACTIVE   = (mFilterMinPrice != -1) || (mFilterMaxPrice != -1);
        COLOR_FILTER_ACTIVE   = (mFilterColors != null);
        NEWNESS_FILTER_ACTIVE = true;

        mAllCheckBoxesList = new ArrayList<>();
        mMyCheckBoxesList  = new ArrayList<>();

        mShopsList = new ArrayList<>();
        mSharedPreferences = new SharedPreferencesManager(this);
        for (String shop : mSharedPreferences.retreiveShops())
            mShopsList.add(shop);
    }

    /**
     * Inicializacion de la Toolbar.
     */
    protected void _initToolbar()
    {
        mToolbar = (Toolbar)findViewById(R.id.filter_appbar);
        mToolbarTextView = (TextView)findViewById(R.id.toolbar_textview);

        mToolbarTextView.setText(getResources().getString(R.string.toolbar_filter));

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    protected void _initAnimations()
    {
        mExplode = AnimationUtils.loadAnimation(this, R.anim.explode);
        mExplode.setStartOffset(300);
    }

    /**
     * Inicializacion de vistas generales.
     */
    protected void _initViews()
    {
        mCoordinatorLayout    = (CoordinatorLayout)findViewById(R.id.filter_coordinator_layout);
        mItemsMenuViewGroup   = (ViewGroup)findViewById(R.id.menu_items);
        mFloatingActionButton = (FloatingActionButton)findViewById(R.id.floatingButton);

        // Hacemos aparecer el FloatingButton
        mFloatingActionButton.setVisibility(View.VISIBLE);
        mFloatingActionButton.startAnimation(mExplode);
    }

    /**
     * Inicializacion de los elementos de la lista de filtros.
     */
    protected void _initFilterItemViews()
    {
        mFilterShopItemLayout    = (RelativeLayout)findViewById(R.id.filter_shop);
        mFilterSectionItemLayout = (RelativeLayout)findViewById(R.id.filter_section);
        mFilterPriceItemLayout   = (RelativeLayout)findViewById(R.id.filter_price);
        mFilterColorItemLayout   = (RelativeLayout)findViewById(R.id.filter_color);
        mFilterNewnessItemLayout = (RelativeLayout)findViewById(R.id.filter_newness);

        mFilterShopImageView    = (ImageView)findViewById(R.id.filter_image_shop);
        mFilterSectionImageView = (ImageView)findViewById(R.id.filter_image_section);
        mFilterPriceImageView   = (ImageView)findViewById(R.id.filter_image_price);
        mFilterColorImageView   = (ImageView)findViewById(R.id.filter_image_color);
        mFilterNewnessImageView = (ImageView)findViewById(R.id.filter_image_newness);

        mFilterShopImageView.setAlpha((mFilterShops == null) ? ALPHA_INACTIVE_FILTER : ALPHA_ACTIVE_FILTER);
        mFilterSectionImageView.setAlpha((mFilterSections == null) ? ALPHA_INACTIVE_FILTER : ALPHA_ACTIVE_FILTER);
        mFilterPriceImageView.setAlpha(((mFilterMinPrice == -1) && (mFilterMaxPrice == -1)) ? ALPHA_INACTIVE_FILTER : ALPHA_ACTIVE_FILTER);
        mFilterColorImageView.setAlpha((mFilterColors == null) ? ALPHA_INACTIVE_FILTER : ALPHA_ACTIVE_FILTER);
        mFilterNewnessImageView.setAlpha(ALPHA_ACTIVE_FILTER);

        mFilterShopItemLayout.setOnClickListener(this);
        mFilterSectionItemLayout.setOnClickListener(this);
        mFilterPriceItemLayout.setOnClickListener(this);
        mFilterColorItemLayout.setOnClickListener(this);
        mFilterNewnessItemLayout.setOnClickListener(this);
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

        mFilterShopRemove    = (ImageButton)findViewById(R.id.filter_item_shop_clear);
        mFilterSectionRemove = (ImageButton)findViewById(R.id.filter_item_section_clear);
        mFilterPriceRemove   = (ImageButton)findViewById(R.id.filter_item_price_clear);
        mFilterColorRemove   = (ImageButton)findViewById(R.id.filter_item_color_clear);
        mFilterNewnessRemove = (ImageButton)findViewById(R.id.filter_item_newness_clear);

        mFilterShopRemove.setOnClickListener(this);
        mFilterSectionRemove.setOnClickListener(this);
        mFilterPriceRemove.setOnClickListener(this);
        mFilterColorRemove.setOnClickListener(this);
        mFilterNewnessRemove.setOnClickListener(this);

        _initFilterColor();
        _initFilterSection();
        _initFilterNewness();
        _initFilterPrice();
        _initFilterShop();
    }

    /**
     * Metodo para inicializar el menu de colores.
     */
    protected void _initFilterColor()
    {
        mColorYellowCheckBox = (AppCompatCheckBox)findViewById(R.id.filter_color_yellow);
        mColorBlueCheckBox   = (AppCompatCheckBox)findViewById(R.id.filter_color_blue);
        mColorBeigeCheckBox  = (AppCompatCheckBox)findViewById(R.id.filter_color_beige);
        mColorWhiteCheckBox  = (AppCompatCheckBox)findViewById(R.id.filter_color_white);
        mColorGreyCheckBox   = (AppCompatCheckBox)findViewById(R.id.filter_color_grey);
        mColorBrownCheckBox  = (AppCompatCheckBox)findViewById(R.id.filter_color_brown);
        mColorPurpleCheckBox = (AppCompatCheckBox)findViewById(R.id.filter_color_purple);
        mColorBlackCheckBox  = (AppCompatCheckBox)findViewById(R.id.filter_color_black);
        mColorRedCheckBox    = (AppCompatCheckBox)findViewById(R.id.filter_color_red);
        mColorPinkCheckBox   = (AppCompatCheckBox)findViewById(R.id.filter_color_pink);
        mColorGreenCheckBox  = (AppCompatCheckBox)findViewById(R.id.filter_color_green);

        ((ViewGroup)mFilterColorMenuLayout.getParent()).removeView(mFilterColorMenuLayout);
        if (COLOR_FILTER_ACTIVE)
        {
            Log.d(TAG, "Filtro de colores ACTIVO");

            mFilterColorImageView.setScaleX(1.1f);
            mFilterColorImageView.setScaleY(1.1f);
            mFilterColorImageView.setAlpha(ALPHA_ACTIVE_FILTER);

            mItemsMenuViewGroup.addView(mFilterColorMenuLayout, 0);

            for (String color : mFilterColors)
            {
                switch (color)
                {
                    case "Amarillos": mColorYellowCheckBox.setChecked(true); break;
                    case "Azules": mColorBlueCheckBox.setChecked(true); break;
                    case "Beiges": mColorBeigeCheckBox.setChecked(true); break;
                    case "Blancos": mColorWhiteCheckBox.setChecked(true); break;
                    case "Grises": mColorGreyCheckBox.setChecked(true); break;
                    case "Marrones": mColorBrownCheckBox.setChecked(true); break;
                    case "Morados": mColorPurpleCheckBox.setChecked(true); break;
                    case "Negros": mColorBlackCheckBox.setChecked(true); break;
                    case "Rojos": mColorRedCheckBox.setChecked(true); break;
                    case "Rosas": mColorPinkCheckBox.setChecked(true); break;
                    case "Verdes": mColorGreenCheckBox.setChecked(true); break;
                }
            }
        }
    }

    /**
     * Metodo para inicializar el menu de secciones.
     */
    protected void _initFilterSection()
    {
        ((ViewGroup)mFilterSectionMenuLayout.getParent()).removeView(mFilterSectionMenuLayout);
        if (SECTION_FILTER_ACTIVE)
        {
            Log.d(TAG, "Filtro de secciones ACTIVO");

            mItemsMenuViewGroup.addView(mFilterSectionMenuLayout, 0);
        }
    }

    /**
     * Metodo para inicializar el menu de novedades.
     */
    protected void _initFilterNewness()
    {
        if (NEWNESS_FILTER_ACTIVE)
        {
            Log.d(TAG, "Filtro de novedades ACTIVO");
            if (mFilterNewness)
                Log.d(TAG, "  - Solo novedades");
            else
                Log.d(TAG, "  - Todos los products");

            ((ViewGroup)mFilterNewnessMenuLayout.getParent()).removeView(mFilterNewnessMenuLayout);

            mItemsMenuViewGroup.addView(mFilterNewnessMenuLayout, 0);

            mFilterNewnessImageView.setScaleX(1.1f);
            mFilterNewnessImageView.setScaleY(1.1f);
            mFilterNewnessImageView.setAlpha(ALPHA_ACTIVE_FILTER);

            mNewnessAllRadioButton = (AppCompatRadioButton)findViewById(R.id.newness_all_radio_button);
            mNewnessNewRadioButton = (AppCompatRadioButton)findViewById(R.id.newness_new_radio_button);

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
    protected void _initFilterPrice()
    {
        mRangeSeekBar = (RangeSeekBar)findViewById(R.id.filter_price_range_seek_bar);
        mRangeSeekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener()
        {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Object minValue, Object maxValue)
            {
                int from = (int)bar.getSelectedMinValue();
                int to = (int)bar.getSelectedMaxValue();

                mPriceFromEditText.setText((from == 0) ? "" : Integer.toString(from));
                mPriceToEditText.setText((to == 100) ? "" : Integer.toString(to));
            }
        });

        mPriceFromEditText = (EditText)findViewById(R.id.filter_price_from);
        mPriceToEditText   = (EditText)findViewById(R.id.filter_price_to);

        ((ViewGroup)mFilterPriceMenuLayout.getParent()).removeView(mFilterPriceMenuLayout);
        if (PRICE_FILTER_ACTIVE)
        {
            Log.d(TAG, "Filtro por precio ACTIVO");

            mFilterPriceImageView.setScaleX(1.1f);
            mFilterPriceImageView.setScaleY(1.1f);
            mFilterPriceImageView.setAlpha(ALPHA_ACTIVE_FILTER);

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
    protected void _initFilterShop()
    {
        mShopAllCheckBox            = (AppCompatCheckBox)findViewById(R.id.check_filter_shop_all);
        mShopMyCheckBox             = (AppCompatCheckBox)findViewById(R.id.check_filter_shop_my);
        mShopBlancoCheckBox         = (AppCompatCheckBox)findViewById(R.id.check_filter_shop_blanco);
        mShopPedroDelHierroCheckBox = (AppCompatCheckBox)findViewById(R.id.check_filter_shop_pedro_del_hierro);
        mShopSpringfieldCheckBox    = (AppCompatCheckBox)findViewById(R.id.check_filter_shop_springfield);
        mShopHyMCheckBox            = (AppCompatCheckBox)findViewById(R.id.check_filter_shop_hym);

        // Metemos en una lista todos los CheckBoxes de mis tiendas
        for (String shop : mShopsList)
        {
            switch (shop)
            {
                case "Blanco": mMyCheckBoxesList.add(mShopBlancoCheckBox); break;
                case "Springfield": mMyCheckBoxesList.add(mShopSpringfieldCheckBox); break;
                case "Pedro Del Hierro": mMyCheckBoxesList.add(mShopPedroDelHierroCheckBox); break;
                case "HyM": mMyCheckBoxesList.add(mShopHyMCheckBox); break;
            }
        }

        // Metemos en una lista todos los CheckBoxes de todas las tiendas
        mAllCheckBoxesList.add(mShopBlancoCheckBox);
        mAllCheckBoxesList.add(mShopSpringfieldCheckBox);
        mAllCheckBoxesList.add(mShopPedroDelHierroCheckBox);
        mAllCheckBoxesList.add(mShopHyMCheckBox);

        mShopAllCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                // Si se desmarca directamente (no se ha desmarcado al marcar el de mShopMyCheckBox)
                if (!isChecked && !mShopMyCheckBox.isChecked())
                {
                    for (AppCompatCheckBox checkBox : mAllCheckBoxesList)
                        checkBox.setChecked(false);
                }

                // Si se marca, se marcan todas las tiendas y se desmarca mShopMyCheckBox
                if (isChecked)
                {
                    mShopMyCheckBox.setChecked(false);

                    for (AppCompatCheckBox checkBox : mAllCheckBoxesList)
                        checkBox.setChecked(true);
                }
            }
        });

        mShopMyCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                // Si se desmarca directamente (no se ha desmarcado al marcar el de mShopAllCheckBox)
                if (!isChecked && !mShopAllCheckBox.isChecked())
                {
                    for (AppCompatCheckBox checkBox : mMyCheckBoxesList)
                        checkBox.setChecked(false);
                }

                // Si se marca, desmarco el resto de tiendas y mShopAllCheckBox
                if (isChecked)
                {
                    mShopAllCheckBox.setChecked(false);

                    for (AppCompatCheckBox checkBox : mAllCheckBoxesList)
                        checkBox.setChecked(false);

                    for (AppCompatCheckBox checkBox : mMyCheckBoxesList)
                        checkBox.setChecked(true);
                }
            }
        });

        if (SHOP_FILTER_ACTIVE)
        {
            Log.d(TAG, "Filtro de tiendas ACTIVO");

            mFilterShopImageView.setScaleX(1.1f);
            mFilterShopImageView.setScaleY(1.1f);
            mFilterShopImageView.setAlpha(ALPHA_ACTIVE_FILTER);

            ((ViewGroup)mFilterShopMenuLayout.getParent()).removeView(mFilterShopMenuLayout);

            mItemsMenuViewGroup.addView(mFilterShopMenuLayout, 0);

            for (String shop : mFilterShops)
            {
                for (AppCompatCheckBox checkBox : mAllCheckBoxesList)
                {
                    if (checkBox.getText().toString().equals(shop))
                    {
                        checkBox.setChecked(true);
                    }
                }
            }

        } else {

            // Marcamos el ChecBox, el listener lo tratara y marcara las tiendas.
            mShopMyCheckBox.setChecked(true);

            ((ViewGroup)mFilterShopMenuLayout.getParent()).removeView(mFilterShopMenuLayout);
        }
    }

    @Override
    public void onClick(View view)
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

                mItemsMenuViewGroup.addView(mFilterShopMenuLayout, 0);

            } else {
                SHOP_FILTER_ACTIVE = false;

                mFilterShopImageView.animate().setDuration(250)
                                              .scaleXBy(-0.1f)
                                              .scaleYBy(-0.1f)
                                              .alpha(ALPHA_INACTIVE_FILTER)
                                              .setInterpolator(new OvershootInterpolator());

                mItemsMenuViewGroup.removeView(mFilterShopMenuLayout);

                mSnackbar = Snackbar.make(mCoordinatorLayout, "Filtro eliminado", Snackbar.LENGTH_SHORT);
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

                mItemsMenuViewGroup.addView(mFilterSectionMenuLayout, 0);

            } else {
                SECTION_FILTER_ACTIVE = false;

                mFilterSectionImageView.animate().setDuration(250)
                        .scaleXBy(-0.1f)
                        .scaleYBy(-0.1f)
                        .alpha(ALPHA_INACTIVE_FILTER)
                        .setInterpolator(new OvershootInterpolator());

                mItemsMenuViewGroup.removeView(mFilterSectionMenuLayout);

                mSnackbar = Snackbar.make(mCoordinatorLayout, "Filtro eliminado", Snackbar.LENGTH_SHORT);
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

                mItemsMenuViewGroup.addView(mFilterPriceMenuLayout, 0);

            } else {
                PRICE_FILTER_ACTIVE = false;

                mFilterPriceImageView.animate().setDuration(250)
                        .scaleXBy(-0.1f)
                        .scaleYBy(-0.1f)
                        .alpha(ALPHA_INACTIVE_FILTER)
                        .setInterpolator(new OvershootInterpolator());

                mItemsMenuViewGroup.removeView(mFilterPriceMenuLayout);

                mSnackbar = Snackbar.make(mCoordinatorLayout, "Filtro eliminado", Snackbar.LENGTH_SHORT);
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

                mItemsMenuViewGroup.addView(mFilterColorMenuLayout, 0);

            } else {
                COLOR_FILTER_ACTIVE = false;

                mFilterColorImageView.animate().setDuration(250)
                        .scaleXBy(-0.1f)
                        .scaleYBy(-0.1f)
                        .alpha(ALPHA_INACTIVE_FILTER)
                        .setInterpolator(new OvershootInterpolator());

                mItemsMenuViewGroup.removeView(mFilterColorMenuLayout);

                mSnackbar = Snackbar.make(mCoordinatorLayout, "Filtro eliminado", Snackbar.LENGTH_SHORT);
                mSnackbar.show();
            }
        }

        if (view.getId() == R.id.filter_newness)
        {
            if (!NEWNESS_FILTER_ACTIVE)
            {
                NEWNESS_FILTER_ACTIVE = true;

                mFilterNewnessImageView.animate().setDuration(250)
                                                 .scaleXBy(0.1f)
                                                 .scaleYBy(0.1f)
                                                 .alpha(ALPHA_ACTIVE_FILTER)
                                                 .setInterpolator(new OvershootInterpolator());

                mItemsMenuViewGroup.addView(mFilterNewnessMenuLayout, 0);

            } else {
                NEWNESS_FILTER_ACTIVE = false;

                mFilterNewnessImageView.animate().setDuration(250)
                        .scaleXBy(-0.1f)
                        .scaleYBy(-0.1f)
                        .alpha(ALPHA_INACTIVE_FILTER)
                        .setInterpolator(new OvershootInterpolator());

                mItemsMenuViewGroup.removeView(mFilterNewnessMenuLayout);

                mSnackbar = Snackbar.make(mCoordinatorLayout, "Filtro eliminado", Snackbar.LENGTH_SHORT);
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
            mSnackbar.show();
        }

        if (view.getId() == R.id.filter_item_newness_clear)
        {
            NEWNESS_FILTER_ACTIVE = false;

            mFilterNewnessImageView.animate().setDuration(250)
                                             .scaleXBy(-0.1f)
                                             .scaleYBy(-0.1f)
                                             .alpha(ALPHA_INACTIVE_FILTER)
                                             .setInterpolator(new OvershootInterpolator());

            mItemsMenuViewGroup.removeView(mFilterNewnessMenuLayout);

            mSnackbar = Snackbar.make(mCoordinatorLayout, "Filtro eliminado", Snackbar.LENGTH_SHORT);
            mSnackbar.show();
        }

    } /* [END OnClick] */

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.toolbar_menu_filter, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            super.onBackPressed();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish(){
        super.finish();

        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

}
