package com.wallakoala.wallakoala.Activities;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.view.animation.Transformation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wallakoala.wallakoala.R;

/**
 * @class Activity con la pantalla de filtros.
 * Created by Daniel Mancebo Aldea on 11/02/2016.
 */

public class FilterUI extends AppCompatActivity implements View.OnClickListener
{
    /* Constants */
    protected static final String TAG = "CUOKA";
    protected static final float ALPHA_ACTIVE_FILTER = 0.8f;
    protected static final float ALPHA_INACTIVE_FILTER = 0.2f;

    /* Floating Button */
    protected FloatingActionButton mFloatingActionButton;

    /* Toolbar */
    protected Toolbar mToolbar;

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

    /* Data */
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

        _initData();
        _initToolbar();
        _initAnimations();
        _initViews();
        _initFilterItemViews();
        _initFilterMenuViews();
        _initNewnessMenuViewFilter();
    }

    /**
     * Inicializacion de los distintos datos y ED's.
     */
    protected void _initData()
    {
        SHOP_FILTER_ACTIVE    = false;
        SECTION_FILTER_ACTIVE = false;
        PRICE_FILTER_ACTIVE   = false;
        COLOR_FILTER_ACTIVE   = false;
        NEWNESS_FILTER_ACTIVE = false;
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

        mFilterShopImageView.setAlpha(ALPHA_INACTIVE_FILTER);
        mFilterSectionImageView.setAlpha(ALPHA_INACTIVE_FILTER);
        mFilterPriceImageView.setAlpha(ALPHA_INACTIVE_FILTER);
        mFilterColorImageView.setAlpha(ALPHA_INACTIVE_FILTER);
        mFilterNewnessImageView.setAlpha(ALPHA_INACTIVE_FILTER);

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

        if (mFilterShopMenuLayout.getParent() != null)
            ((ViewGroup)mFilterShopMenuLayout.getParent()).removeView(mFilterShopMenuLayout);

        if (mFilterSectionMenuLayout.getParent() != null)
            ((ViewGroup)mFilterSectionMenuLayout.getParent()).removeView(mFilterSectionMenuLayout);

        if (mFilterPriceMenuLayout.getParent() != null)
            ((ViewGroup)mFilterPriceMenuLayout.getParent()).removeView(mFilterPriceMenuLayout);

        if (mFilterColorMenuLayout.getParent() != null)
            ((ViewGroup)mFilterColorMenuLayout.getParent()).removeView(mFilterColorMenuLayout);

        if (mFilterNewnessMenuLayout.getParent() != null)
            ((ViewGroup)mFilterNewnessMenuLayout.getParent()).removeView(mFilterNewnessMenuLayout);
    }

    protected void _initNewnessMenuViewFilter()
    {
        mNewnessAllRadioButton = (AppCompatRadioButton)findViewById(R.id.newness_all_radio_button);
        mNewnessNewRadioButton = (AppCompatRadioButton)findViewById(R.id.newness_new_radio_button);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
        {
            ColorStateList colorStateList = new ColorStateList(
                    new int[][] {new int[]{-android.R.attr.state_checked}, new int[]{android.R.attr.state_checked}},
                    new int[]{R.color.colorAccent, R.color.colorAccent}
            );

            mNewnessAllRadioButton.setSupportButtonTintList(colorStateList);
            mNewnessNewRadioButton.setSupportButtonTintList(colorStateList);
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
