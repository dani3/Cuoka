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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wallakoala.wallakoala.Properties.Properties;
import com.wallakoala.wallakoala.R;
import com.wallakoala.wallakoala.Utils.SharedPreferencesManager;
import com.wallakoala.wallakoala.Utils.Utils;
import com.wallakoala.wallakoala.Views.RangeSeekBar;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
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
    protected static final float ALPHA_ACTIVE_FILTER = 1.0f;
    protected static final float ALPHA_INACTIVE_FILTER = 0.2f;
    protected static String SECTION_FILTER_MAN_1;
    protected static String SECTION_FILTER_MAN_2;
    protected static String SECTION_FILTER_MAN_3;
    protected static String SECTION_FILTER_MAN_4;
    protected static String SECTION_FILTER_MAN_5;
    protected static String SECTION_FILTER_MAN_6;
    protected static String SECTION_FILTER_MAN_7;
    protected static String SECTION_FILTER_MAN_8;
    protected static String SECTION_FILTER_MAN_9;
    protected static String SECTION_FILTER_MAN_10;
    protected static String SECTION_FILTER_MAN_11;
    protected static String SECTION_FILTER_MAN_12;
    protected static String SECTION_FILTER_WOMAN_1;
    protected static String SECTION_FILTER_WOMAN_2;
    protected static String SECTION_FILTER_WOMAN_3;
    protected static String SECTION_FILTER_WOMAN_4;
    protected static String SECTION_FILTER_WOMAN_5;
    protected static String SECTION_FILTER_WOMAN_6;
    protected static String SECTION_FILTER_WOMAN_7;
    protected static String SECTION_FILTER_WOMAN_8;
    protected static String SECTION_FILTER_WOMAN_9;
    protected static String SECTION_FILTER_WOMAN_10;
    protected static String SECTION_FILTER_WOMAN_11;
    protected static String SECTION_FILTER_WOMAN_12;
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

    /* Menu */
    protected Menu mMenu;

    /* Container Views */
    protected ViewGroup mItemsMenuViewGroup;
    protected CoordinatorLayout mCoordinatorLayout;
    protected RelativeLayout mFilterShopItemLayout;
    protected RelativeLayout mFilterSectionItemLayout;
    protected RelativeLayout mFilterPriceItemLayout;
    protected RelativeLayout mFilterColorItemLayout;
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

    /* ImageViews */
    protected ImageView mFilterShopImageView;
    protected ImageView mFilterSectionImageView;
    protected ImageView mFilterPriceImageView;
    protected ImageView mFilterColorImageView;
    protected ImageView mFilterNewnessImageView;

    /* TextViews */
    protected TextView mToolbarTextView;
    protected TextView mFilterShopTextView;
    protected TextView mFilterSectionTextView;
    protected TextView mFilterColorTextView;
    protected TextView mFilterPriceTextView;
    protected TextView mFilterNewnessTextView;

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
    protected AppCompatCheckBox mShopZaraCheckBox;

    protected List<AppCompatCheckBox> mColorCheckBoxesList;
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

    protected List<AppCompatCheckBox> mSectionCheckBoxesList;
    protected AppCompatCheckBox mSection1CheckBox;
    protected AppCompatCheckBox mSection2CheckBox;
    protected AppCompatCheckBox mSection3CheckBox;
    protected AppCompatCheckBox mSection4CheckBox;
    protected AppCompatCheckBox mSection5CheckBox;
    protected AppCompatCheckBox mSection6CheckBox;
    protected AppCompatCheckBox mSection7CheckBox;
    protected AppCompatCheckBox mSection8CheckBox;
    protected AppCompatCheckBox mSection9CheckBox;
    protected AppCompatCheckBox mSection10CheckBox;
    protected AppCompatCheckBox mSection11CheckBox;
    protected AppCompatCheckBox mSection12CheckBox;

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

        setContentView(R.layout.activity_filter);

        if (savedInstanceState == null)
        {
            Map<String, Object> mFilterMap = new HashMap<>();

            Intent intent = getIntent();

            // Filtro de novedades (false si no esta activado)
            mFilterMap.put("newness", intent.getBooleanExtra(Properties.PACKAGE + ".newness", false));
            // Filtro de secciones (null si no esta activado)
            mFilterMap.put("sections", intent.getSerializableExtra(Properties.PACKAGE + ".sections"));
            // Filtro de colores (null si no esta activado)
            mFilterMap.put("colors", intent.getSerializableExtra(Properties.PACKAGE + ".colors"));
            // Filtro de tiendas (null si no esta activado)
            mFilterMap.put("shops", intent.getSerializableExtra(Properties.PACKAGE + ".shops"));
            // Filtro de precio (minimo) (-1 si no esta activado)
            mFilterMap.put("minPrice", intent.getIntExtra(Properties.PACKAGE + ".minPrice", -1));
            // Filtro de precio (maximo) (-1 si no esta activado)
            mFilterMap.put("maxPrice", intent.getIntExtra(Properties.PACKAGE + ".maxPrice", -1));

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
        SHOP_FILTER_ACTIVE    = (mFilterShops != null);
        SECTION_FILTER_ACTIVE = (mFilterSections != null);
        COLOR_FILTER_ACTIVE   = (mFilterColors != null);
        PRICE_FILTER_ACTIVE   = (mFilterMinPrice != -1) || (mFilterMaxPrice != -1);
        NEWNESS_FILTER_ACTIVE = true;

        mAllCheckBoxesList     = new ArrayList<>();
        mMyCheckBoxesList      = new ArrayList<>();
        mColorCheckBoxesList   = new ArrayList<>();
        mSectionCheckBoxesList = new ArrayList<>();

        mShopsList = new ArrayList<>();
        mSharedPreferences = new SharedPreferencesManager(this);
        for (String shop : mSharedPreferences.retreiveUser().getShops())
            mShopsList.add(shop);

        SECTION_FILTER_MAN_1 = getResources().getString(R.string.filter_section_abrigos);
        SECTION_FILTER_MAN_2 = getResources().getString(R.string.filter_section_americanas);
        SECTION_FILTER_MAN_3 = getResources().getString(R.string.filter_section_camisas);
        SECTION_FILTER_MAN_4 = getResources().getString(R.string.filter_section_camisetas);
        SECTION_FILTER_MAN_5 = getResources().getString(R.string.filter_section_jerseis);
        SECTION_FILTER_MAN_6 = getResources().getString(R.string.filter_section_pantalones);
        SECTION_FILTER_MAN_7 = getResources().getString(R.string.filter_section_polos);
        SECTION_FILTER_MAN_8 = getResources().getString(R.string.filter_section_shorts);
        SECTION_FILTER_MAN_9 = getResources().getString(R.string.filter_section_sport);
        SECTION_FILTER_MAN_10 = getResources().getString(R.string.filter_section_sudaderas);
        SECTION_FILTER_MAN_11 = getResources().getString(R.string.filter_section_trajes);
        SECTION_FILTER_MAN_12 = getResources().getString(R.string.filter_section_zapatos);

        SECTION_FILTER_WOMAN_1 = getResources().getString(R.string.filter_section_abrigos);
        SECTION_FILTER_WOMAN_2 = getResources().getString(R.string.filter_section_americanas);
        SECTION_FILTER_WOMAN_3 = getResources().getString(R.string.filter_section_camisas);
        SECTION_FILTER_WOMAN_4 = getResources().getString(R.string.filter_section_camisetas);
        SECTION_FILTER_WOMAN_5 = getResources().getString(R.string.filter_section_faldas);
        SECTION_FILTER_WOMAN_6 = getResources().getString(R.string.filter_section_jerseis);
        SECTION_FILTER_WOMAN_7 = getResources().getString(R.string.filter_section_pantalones);
        SECTION_FILTER_WOMAN_8 = getResources().getString(R.string.filter_section_monos);
        SECTION_FILTER_WOMAN_9 = getResources().getString(R.string.filter_section_shorts);
        SECTION_FILTER_WOMAN_10 = getResources().getString(R.string.filter_section_sport);
        SECTION_FILTER_WOMAN_11 = getResources().getString(R.string.filter_section_vestidos);
        SECTION_FILTER_WOMAN_12 = getResources().getString(R.string.filter_section_zapatos);
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
        mCoordinatorLayout    = (CoordinatorLayout)findViewById(R.id.filter_coordinator_layout);
        mItemsMenuViewGroup   = (ViewGroup)findViewById(R.id.menu_items);
        mFloatingActionButton = (FloatingActionButton)findViewById(R.id.floatingButton);

        // Hacemos aparecer el FloatingButton
        mFloatingActionButton.setVisibility(View.VISIBLE);
        mFloatingActionButton.startAnimation(mExplode);

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean OK = true;

                if (!COLOR_FILTER_ACTIVE &&
                    !SHOP_FILTER_ACTIVE &&
                    !SECTION_FILTER_ACTIVE &&
                    !PRICE_FILTER_ACTIVE &&
                    !NEWNESS_FILTER_ACTIVE)
                {
                    mSnackbar = Snackbar.make(mCoordinatorLayout
                            , "No se ha establecido ningún filtro"
                            , Snackbar.LENGTH_SHORT);

                    mSnackbar.show();

                } else {
                    Intent intent = new Intent();

                    ArrayList<String> shopsList = null;
                    if (SHOP_FILTER_ACTIVE) {
                        boolean none = true;

                        shopsList = new ArrayList<>();
                        for (AppCompatCheckBox checkBox : mAllCheckBoxesList) {
                            if (checkBox.isChecked()) {
                                none = false;

                                shopsList.add(checkBox.getText().toString());
                            }
                        }

                        if (none) {
                            OK = false;

                            mFilterShopMenuLayout.startAnimation(
                                    AnimationUtils.loadAnimation(FilterUI.this, R.anim.shake_animation));
                        }
                    }

                    ArrayList<String> colorsList = null;
                    if (COLOR_FILTER_ACTIVE) {
                        boolean none = true;

                        colorsList = new ArrayList<>();
                        for (AppCompatCheckBox checkBox : mColorCheckBoxesList) {
                            if (checkBox.isChecked()) {
                                none = false;

                                colorsList.add(checkBox.getText().toString());
                            }
                        }

                        if (none) {
                            OK = false;

                            mFilterColorMenuLayout.startAnimation(
                                    AnimationUtils.loadAnimation(FilterUI.this, R.anim.shake_animation));
                        }
                    }

                    ArrayList<String> sectionsList = null;
                    if (SECTION_FILTER_ACTIVE) {
                        boolean none = true;

                        sectionsList = new ArrayList<>();
                        for (AppCompatCheckBox checkBox : mSectionCheckBoxesList) {
                            if (checkBox.isChecked()) {
                                none = false;

                                sectionsList.add(checkBox.getText().toString());
                            }
                        }

                        if (none) {
                            OK = false;

                            mFilterSectionMenuLayout.startAnimation(
                                    AnimationUtils.loadAnimation(FilterUI.this, R.anim.shake_animation));
                        }
                    }

                    int from = -1;
                    int to = -1;
                    if (PRICE_FILTER_ACTIVE) {
                        int lengthFrom = mPriceFromEditText.getText().length();
                        int lengthTo = mPriceToEditText.getText().length();

                        from = (lengthFrom == 0) ? from : Integer.valueOf(mPriceFromEditText.getText().toString());
                        to = (lengthTo == 0) ? to : Integer.valueOf(mPriceToEditText.getText().toString());

                        if (lengthFrom == 0 && lengthTo == 0) {
                            OK = false;

                            mFilterPriceMenuLayout.startAnimation(
                                    AnimationUtils.loadAnimation(FilterUI.this, R.anim.shake_animation));
                        }
                    }

                    if (OK) {
                        boolean newness = mNewnessNewRadioButton.isChecked();

                        intent.putExtra(Properties.PACKAGE + ".shops", shopsList);
                        intent.putExtra(Properties.PACKAGE + ".colors", colorsList);
                        intent.putExtra(Properties.PACKAGE + ".sections", sectionsList);
                        intent.putExtra(Properties.PACKAGE + ".minPrice", from);
                        intent.putExtra(Properties.PACKAGE + ".maxPrice", to);
                        intent.putExtra(Properties.PACKAGE + ".newness", newness);

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
        mFilterShopItemLayout    = (RelativeLayout)findViewById(R.id.filter_shop);
        mFilterSectionItemLayout = (RelativeLayout)findViewById(R.id.filter_section);
        mFilterPriceItemLayout   = (RelativeLayout)findViewById(R.id.filter_price);
        mFilterColorItemLayout   = (RelativeLayout)findViewById(R.id.filter_color);

        mFilterShopImageView    = (ImageView)findViewById(R.id.filter_image_shop);
        mFilterSectionImageView = (ImageView)findViewById(R.id.filter_image_section);
        mFilterPriceImageView   = (ImageView)findViewById(R.id.filter_image_price);
        mFilterColorImageView   = (ImageView)findViewById(R.id.filter_image_color);
        mFilterNewnessImageView = (ImageView)findViewById(R.id.filter_image_newness);

        mFilterShopImageView.setAlpha((mFilterShops == null || mFilterShops.isEmpty()) ? ALPHA_INACTIVE_FILTER : ALPHA_ACTIVE_FILTER);
        mFilterSectionImageView.setAlpha((mFilterSections == null || mFilterSections.isEmpty()) ? ALPHA_INACTIVE_FILTER : ALPHA_ACTIVE_FILTER);
        mFilterPriceImageView.setAlpha(((mFilterMinPrice == -1) && (mFilterMaxPrice == -1)) ? ALPHA_INACTIVE_FILTER : ALPHA_ACTIVE_FILTER);
        mFilterColorImageView.setAlpha((mFilterColors == null || mFilterColors.isEmpty()) ? ALPHA_INACTIVE_FILTER : ALPHA_ACTIVE_FILTER);
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

        mFilterShopRemove    = (ImageButton)findViewById(R.id.filter_item_shop_clear);
        mFilterSectionRemove = (ImageButton)findViewById(R.id.filter_item_section_clear);
        mFilterPriceRemove   = (ImageButton)findViewById(R.id.filter_item_price_clear);
        mFilterColorRemove   = (ImageButton)findViewById(R.id.filter_item_color_clear);

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

            mFilterColorTextView.setTextColor(getResources().getColor(R.color.colorMediumText));

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
    protected void _initFilterSection()
    {
        mSection1CheckBox  = (AppCompatCheckBox)findViewById(R.id.filter_section_1);
        mSection2CheckBox  = (AppCompatCheckBox)findViewById(R.id.filter_section_2);
        mSection3CheckBox  = (AppCompatCheckBox)findViewById(R.id.filter_section_3);
        mSection4CheckBox  = (AppCompatCheckBox)findViewById(R.id.filter_section_4);
        mSection5CheckBox  = (AppCompatCheckBox)findViewById(R.id.filter_section_5);
        mSection6CheckBox  = (AppCompatCheckBox)findViewById(R.id.filter_section_6);
        mSection7CheckBox  = (AppCompatCheckBox)findViewById(R.id.filter_section_7);
        mSection8CheckBox  = (AppCompatCheckBox)findViewById(R.id.filter_section_8);
        mSection9CheckBox  = (AppCompatCheckBox)findViewById(R.id.filter_section_9);
        mSection10CheckBox = (AppCompatCheckBox)findViewById(R.id.filter_section_10);
        mSection11CheckBox = (AppCompatCheckBox)findViewById(R.id.filter_section_11);
        mSection12CheckBox = (AppCompatCheckBox)findViewById(R.id.filter_section_12);

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

            mFilterSectionTextView.setTextColor(getResources().getColor(R.color.colorMediumText));

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
    protected void _initFilterNewness()
    {
        mFilterNewnessTextView = (TextView)findViewById(R.id.filter_text_newness);

        mFilterNewnessTextView.setTextColor(getResources().getColor(R.color.colorMediumText));

        if (NEWNESS_FILTER_ACTIVE)
        {
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
    @SuppressWarnings("unchecked")
    protected void _initFilterPrice()
    {
        mFilterPriceTextView = (TextView)findViewById(R.id.filter_text_price);

        mRangeSeekBar = (RangeSeekBar)findViewById(R.id.filter_price_range_seek_bar);
        mRangeSeekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Object minValue, Object maxValue) {
                int from = (int) bar.getSelectedMinValue();
                int to = (int) bar.getSelectedMaxValue();

                mPriceFromEditText.setText((from == 0) ? "" : Integer.toString(from));
                mPriceToEditText.setText((to == 100) ? "" : Integer.toString(to));
            }
        });

        mPriceFromEditText = (EditText)findViewById(R.id.filter_price_from);
        mPriceToEditText   = (EditText)findViewById(R.id.filter_price_to);

        mPriceFromEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (!v.getText().toString().isEmpty()) {
                    int from = Integer.valueOf(mPriceFromEditText.getText().toString());
                    int to = 999;

                    if (mPriceToEditText.getText() != null && !mPriceToEditText.getText().toString().isEmpty())
                        to = Integer.valueOf(mPriceToEditText.getText().toString());

                    Log.d(Properties.TAG, Integer.toString(from) + "|" + Integer.toString(to));

                    if (from > to) {
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

        mPriceToEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (!v.getText().toString().isEmpty()) {
                    int from = -1;
                    int to = Integer.valueOf(mPriceToEditText.getText().toString());

                    if (mPriceFromEditText.getText() != null && !mPriceFromEditText.getText().toString().isEmpty())
                        from = Integer.valueOf(mPriceFromEditText.getText().toString());

                    if (from > to) {
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

            mFilterPriceTextView.setTextColor(getResources().getColor(R.color.colorMediumText));

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
        mShopZaraCheckBox           = (AppCompatCheckBox)findViewById(R.id.check_filter_shop_zara);

        mFilterShopTextView = (TextView)findViewById(R.id.filter_text_shop);

        // Metemos en una lista todos los CheckBoxes de todas las tiendas
        mAllCheckBoxesList.add(mShopBlancoCheckBox);
        mAllCheckBoxesList.add(mShopSpringfieldCheckBox);
        mAllCheckBoxesList.add(mShopPedroDelHierroCheckBox);
        mAllCheckBoxesList.add(mShopHyMCheckBox);
        mAllCheckBoxesList.add(mShopZaraCheckBox);

        // Metemos en una lista todos los CheckBoxes de mis tiendas
        for (String shop : mShopsList)
        {
            for (AppCompatCheckBox checkBox : mAllCheckBoxesList)
            {
                if (checkBox.getText().equals(shop))
                {
                    mMyCheckBoxesList.add(checkBox);
                }
            }
        }

        mShopAllCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Si se desmarca directamente (no se ha desmarcado al marcar el de mShopMyCheckBox)
                if (!isChecked && !mShopMyCheckBox.isChecked()) {
                    for (AppCompatCheckBox checkBox : mAllCheckBoxesList)
                        checkBox.setChecked(false);
                }

                // Si se marca, se marcan todas las tiendas y se desmarca mShopMyCheckBox
                if (isChecked) {
                    mShopMyCheckBox.setChecked(false);

                    for (AppCompatCheckBox checkBox : mAllCheckBoxesList)
                        checkBox.setChecked(true);
                }
            }
        });

        mShopMyCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Si se desmarca directamente (no se ha desmarcado al marcar el de mShopAllCheckBox)
                if (!isChecked && !mShopAllCheckBox.isChecked()) {
                    boolean allChecked = true;
                    for (AppCompatCheckBox checkBox : mMyCheckBoxesList)
                        if (!checkBox.isChecked())
                            allChecked = false;

                    if (allChecked)
                        for (AppCompatCheckBox checkBox : mMyCheckBoxesList)
                            checkBox.setChecked(false);
                }

                // Si se marca, desmarco el resto de tiendas y mShopAllCheckBox
                if (isChecked) {
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
            mFilterShopImageView.setScaleX(1.1f);
            mFilterShopImageView.setScaleY(1.1f);
            mFilterShopImageView.setAlpha(ALPHA_ACTIVE_FILTER);

            mFilterShopTextView.setTextColor(getResources().getColor(R.color.colorMediumText));

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

    /**
     * Metodo que resetea el filtro de tiendas.
     */
    protected void _resetFilterShop()
    {
        for (AppCompatCheckBox checkBox : mAllCheckBoxesList)
        {
            checkBox.setChecked(false);
        }

        mShopMyCheckBox.setChecked(false);
        mShopAllCheckBox.setChecked(false);
        mShopMyCheckBox.setChecked(true);
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
    protected void _resetFilter()
    {
        mSnackbar = Snackbar.make(mCoordinatorLayout, "Filtros restablecidos", Snackbar.LENGTH_SHORT);

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

                mFilterShopTextView.setTextColor(getResources().getColor(R.color.colorMediumText));

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

                mFilterSectionTextView.setTextColor(getResources().getColor(R.color.colorMediumText));

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

                mFilterPriceTextView.setTextColor(getResources().getColor(R.color.colorMediumText));

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

                mFilterColorTextView.setTextColor(getResources().getColor(R.color.colorMediumText));

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

    } /* [END OnClick] */

    @Override
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

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                if (Utils.isQueryOk(query))
                {
                    Intent intent = new Intent();

                    ArrayList<String> aux = null;

                    intent.putExtra(Properties.PACKAGE + ".shops", aux);
                    intent.putExtra(Properties.PACKAGE + ".colors", aux);
                    intent.putExtra(Properties.PACKAGE + ".sections", aux);
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
                    new GetSuggestionsFromServer().execute(newText);
                }

                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            onBackPressed();

            return true;
        }

        if (item.getItemId() == R.id.menu_item_options)
        {
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
    private class GetSuggestionsFromServer extends AsyncTask<String, Void, Void>
    {
        List<String> suggestions;

        @Override
        protected Void doInBackground(String... params)
        {
            BufferedReader reader = null;
            URL url = null;

            try {
                String fixedURL = Utils.fixUrl(Properties.SERVER_URL + ":" + Properties.SERVER_SPRING_PORT
                        + "/suggest/" + params[0]);

                Log.d(Properties.TAG, "Conectando con: " + fixedURL
                        + " para buscar '" + params[0] + "'");

                url = new URL(fixedURL);

                if (url != null)
                {
                    URLConnection conn = url.openConnection();

                    // Obtenemos la respuesta del servidor
                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line = "";

                    // Leemos la respuesta
                    while ((line = reader.readLine()) != null)
                        sb.append(line + "");

                    // Devolvemos la respuesta
                    String content =  sb.toString();

                    Log.d(Properties.TAG, content);

                    JSONArray jsonArray = new JSONArray(content);
                    suggestions = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        suggestions.add(jsonArray.getString(i));
                    }
                }

            } catch (Exception e) {
                Log.d(Properties.TAG, "Error conectando realizando busqueda");

            } finally {
                try {
                    if (reader != null)
                        reader.close();

                } catch (IOException e) {
                    Log.d(Properties.TAG, "Error cerrando conexion con el servidor");

                }

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused)
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

            final SearchView search = (SearchView)mMenu.findItem(R.id.menu_item_search).getActionView();

            search.setSuggestionsAdapter(new SuggestionAdapter(FilterUI.this, cursor, suggestions));
            search.getSuggestionsAdapter().notifyDataSetChanged();
        }

    } /* [END getSuggestionsFromServer] */

    /**
     * Metodo llamado cuando se hace click en el texto de la sugerencia.
     * @param view
     */
    public void onClickText(View view)
    {
        TextView textView = (TextView)view;

        if (Utils.isQueryOk(textView.getText().toString()))
        {
            Intent intent = new Intent();

            ArrayList<String> aux = null;

            intent.putExtra(Properties.PACKAGE + ".shops", aux);
            intent.putExtra(Properties.PACKAGE + ".colors", aux);
            intent.putExtra(Properties.PACKAGE + ".sections", aux);
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
     * @param view
     */
    public void onClickButton(View view)
    {
        ImageButton imageButton = (ImageButton)view.findViewById(R.id.suggestion_include);

        final SearchView search = (SearchView)mMenu.findItem(R.id.menu_item_search).getActionView();

        search.setQuery(imageButton.getContentDescription().toString(), false);
    }

    /**
     * Adapter para las sugerencias.
     */
    public class SuggestionAdapter extends CursorAdapter
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
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = inflater.inflate(R.layout.suggestion_item, parent, false);

            text = (TextView)view.findViewById(R.id.suggestion_item);
            imageButton = (ImageButton)view.findViewById(R.id.suggestion_include);

            return view;
        }

        @Override
        public int getCount()
        {
            return items.size();
        }

    } /* [END SuggestionAdapter] */

}
