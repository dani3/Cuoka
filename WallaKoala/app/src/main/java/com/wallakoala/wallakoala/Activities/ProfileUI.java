package com.wallakoala.wallakoala.Activities;

import android.animation.TimeInterpolator;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import com.wallakoala.wallakoala.Beans.User;
import com.wallakoala.wallakoala.Properties.Properties;
import com.wallakoala.wallakoala.R;
import com.wallakoala.wallakoala.Utils.SharedPreferencesManager;

/**
 * Pantalla del perfil del usuario.
 * Created by Daniel Mancebo Aldea on 19/10/2016.
 */

public class ProfileUI extends AppCompatActivity
{
    /* Constants */
    protected static final TimeInterpolator ACCELERATE_DECELERATE_INTERPOLATOR = new AccelerateDecelerateInterpolator();
    protected static final int ANIM_DURATION = 250;
    protected static boolean EXITING;

    /* Data */
    protected int mThumbnailTop;
    protected int mThumbnailLeft;
    protected int mThumbnailWidth;
    protected int mThumbnailHeight;
    protected int mLeftDeltaImage;
    protected int mTopDeltaImage;

    /* User */
    protected User mUser;

    /* SharedPreferences */
    protected SharedPreferencesManager mSharedPreferencesManager;

    protected float mWidthScaleImage;
    protected float mHeightScaleImage;

    /* Container Layouts */
    protected CoordinatorLayout mTopLevelLayout;
    protected CollapsingToolbarLayout mCollapsingToolbarLayout;

    /* TextViews */
    protected TextView mFavoriteTextView;
    protected TextView mShopsTextView;

    /* Floating Action Button */
    protected FloatingActionButton mProfileFAB;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        _initToolbar();
        _initData();
        _initViews();

        // Solo lo ejecutamos si venimos de la activity padre
        if (savedInstanceState == null)
        {
            // Listener global
            final ViewTreeObserver observer = mProfileFAB.getViewTreeObserver();
            observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
            {
                @Override
                public boolean onPreDraw()
                {
                    mProfileFAB.getViewTreeObserver().removeOnPreDrawListener(this);

                    // Sacamos donde esta la imagen pequeña y lo que hay que desplazarse hacia ella
                    int[] imageScreenLocation = new int[2];
                    mProfileFAB.getLocationOnScreen(imageScreenLocation);
                    mLeftDeltaImage = mThumbnailLeft - imageScreenLocation[0];
                    mTopDeltaImage  = mThumbnailTop - imageScreenLocation[1];

                    // Factores de escala para saber cuanto hay que encojer o agrandar la imagen
                    mWidthScaleImage = (float)mThumbnailWidth / (float)mProfileFAB.getWidth();
                    mHeightScaleImage = (float)mThumbnailHeight / (float)mProfileFAB.getHeight();

                    _runEnterAnimation();

                    return true;
                }
            });
        }
    }

    /**
     * Metodo para inicializar los datos.
     */
    private void _initData()
    {
        EXITING = false;

        mSharedPreferencesManager = new SharedPreferencesManager(this);

        mUser = mSharedPreferencesManager.retreiveUser();

        Bundle bundle = getIntent().getExtras();

        mThumbnailTop       = bundle.getInt(Properties.PACKAGE + ".top");
        mThumbnailLeft      = bundle.getInt(Properties.PACKAGE + ".left");
        mThumbnailWidth     = bundle.getInt(Properties.PACKAGE + ".width");
        mThumbnailHeight    = bundle.getInt(Properties.PACKAGE + ".height");
    }

    /**
     * Metodo que inicializa la Toolbar
     */
    private void _initToolbar()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.profile_toolbar);
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
     * Metodo que inicializa las vistas.
     */
    private void _initViews()
    {
        mCollapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.profile_collapsing_layout);
        mTopLevelLayout          = (CoordinatorLayout)findViewById(R.id.profile_coordinator);
        mProfileFAB              = (FloatingActionButton)findViewById(R.id.profile_floating_pic);

        mFavoriteTextView = (TextView)findViewById(R.id.profile_favorites);
        mShopsTextView    = (TextView)findViewById(R.id.profile_shops);

        mFavoriteTextView.setText(Integer.toString(mUser.getFavoriteProducts().size()));
        mShopsTextView.setText(Integer.toString(mUser.getShops().size()));
    }

    @Override
    public void onBackPressed()
    {
        if (!EXITING)
        {
            if (mProfileFAB.getVisibility() == View.VISIBLE)
            {
                _runExitAnimation(new Runnable() {
                    public void run() {
                        finish();
                    }
                });

            } else {
                finish();
            }
        }
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(0, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.toolbar_menu_profile, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            onBackPressed();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * La animacion de entrada escala la imagen desde la pequeña hasta la posicion/tamaño de la grande.
     * En paralelo, el fondo se va oscureciendo.
     */
    private void _runEnterAnimation()
    {
        mTopLevelLayout.setAlpha(0.0f);

        mProfileFAB.setPivotX(0);
        mProfileFAB.setPivotY(0);
        mProfileFAB.setScaleX(mWidthScaleImage);
        mProfileFAB.setScaleY(mHeightScaleImage);
        mProfileFAB.setTranslationX(mLeftDeltaImage);
        mProfileFAB.setTranslationY(mTopDeltaImage);

        // Animacion de escalado y desplazamiento hasta el tamaño grande
        mProfileFAB.animate()
                   .setDuration(ANIM_DURATION)
                   .scaleX(1).scaleY(1)
                   .translationX(0).translationY(0)
                   .setInterpolator(ACCELERATE_DECELERATE_INTERPOLATOR);

        // Efecto fade para oscurecer la pantalla
        mTopLevelLayout.animate().alpha(1.0f).setDuration(ANIM_DURATION).start();
    }

    /**
     * La animacion de salida es la misma animacion de entrada pero al reves
     * @param endAction: Accion que se ejecuta cuando termine la animacion.
     */
    private void _runExitAnimation(final Runnable endAction)
    {
        EXITING = true;

        // En caso de que haya cambiado de posicion, se recalcula el desplazamiento
        int[] currentLocation = new int[2];
        mProfileFAB.getLocationOnScreen(currentLocation);
        mTopDeltaImage = mThumbnailTop - currentLocation[1];

        mProfileFAB.animate()
                   .setDuration(ANIM_DURATION)
                   .setStartDelay(0)
                   .scaleX(mWidthScaleImage).scaleY(mHeightScaleImage)
                   .translationX(mLeftDeltaImage).translationY(mTopDeltaImage)
                   .withEndAction(endAction);

        // Aclarar el fondo
        mTopLevelLayout.animate().alpha(0.0f).setDuration(ANIM_DURATION).start();
    }
}