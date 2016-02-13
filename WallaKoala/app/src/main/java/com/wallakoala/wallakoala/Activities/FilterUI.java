package com.wallakoala.wallakoala.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
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

    /* Toolbar */
    protected Toolbar mToolbar;

    /* Snackbar */
    protected Snackbar mSnackbar;

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

    /* TextViews */
    protected TextView mToolbarTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.filter);

        _initToolbar();
        _initViews();
        _initFilterItemViews();
        _initFilterMenuViews();
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
     * Inicializacion de vistas generales.
     */
    protected void _initViews()
    {
        mCoordinatorLayout  = (CoordinatorLayout)findViewById(R.id.filter_coordinator_layout);
        mItemsMenuViewGroup = (ViewGroup)findViewById(R.id.menu_items);
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

        mFilterShopMenuLayout.setVisibility(View.GONE);
        mFilterSectionMenuLayout.setVisibility(View.GONE);
        mFilterPriceMenuLayout.setVisibility(View.GONE);
        mFilterColorMenuLayout.setVisibility(View.GONE);
        mFilterNewnessMenuLayout.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.filter_shop)
        {
            if (mFilterShopMenuLayout.getVisibility() == View.GONE)
            {
                Log.d(TAG, "Click en filtro de tiendas");

                mFilterShopMenuLayout.setVisibility(View.VISIBLE);

                ((ViewGroup)mFilterShopMenuLayout.getParent()).removeView(mFilterShopMenuLayout);

                mItemsMenuViewGroup.addView(mFilterShopMenuLayout, 0);
            }
        }

        if (view.getId() == R.id.filter_section)
        {
            if (mFilterSectionMenuLayout.getVisibility() == View.GONE)
            {
                Log.d(TAG, "Click en filtro de secciones");

                mFilterSectionMenuLayout.setVisibility(View.VISIBLE);

                ((ViewGroup)mFilterSectionMenuLayout.getParent()).removeView(mFilterSectionMenuLayout);

                mItemsMenuViewGroup.addView(mFilterSectionMenuLayout, 0);
            }
        }

        if (view.getId() == R.id.filter_price)
        {
            if (mFilterPriceMenuLayout.getVisibility() == View.GONE)
            {
                Log.d(TAG, "Click en filtro de precios");

                mFilterPriceMenuLayout.setVisibility(View.VISIBLE);

                ((ViewGroup)mFilterPriceMenuLayout.getParent()).removeView(mFilterPriceMenuLayout);

                mItemsMenuViewGroup.addView(mFilterPriceMenuLayout, 0);
            }
        }

        if (view.getId() == R.id.filter_color)
        {
            if (mFilterColorMenuLayout.getVisibility() == View.GONE)
            {
                Log.d(TAG, "Click en filtro de colores");

                mFilterColorMenuLayout.setVisibility(View.VISIBLE);

                ((ViewGroup)mFilterColorMenuLayout.getParent()).removeView(mFilterColorMenuLayout);

                mItemsMenuViewGroup.addView(mFilterColorMenuLayout, 0);
            }
        }

        if (view.getId() == R.id.filter_newness)
        {
            if (mFilterNewnessMenuLayout.getVisibility() == View.GONE)
            {
                Log.d(TAG, "Click en filtro de novedades");

                mFilterNewnessMenuLayout.setVisibility(View.VISIBLE);

                ((ViewGroup)mFilterNewnessMenuLayout.getParent()).removeView(mFilterNewnessMenuLayout);

                mItemsMenuViewGroup.addView(mFilterNewnessMenuLayout, 0);
            }
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
