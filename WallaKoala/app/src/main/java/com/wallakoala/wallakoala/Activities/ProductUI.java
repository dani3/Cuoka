package com.wallakoala.wallakoala.Activities;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.wallakoala.wallakoala.Adapters.ColorIconListAdapter;
import com.wallakoala.wallakoala.Adapters.ProductAdapter;
import com.wallakoala.wallakoala.Beans.ColorVariant;
import com.wallakoala.wallakoala.Beans.Product;
import com.wallakoala.wallakoala.R;
import com.wallakoala.wallakoala.Utils.RecyclerScrollDisabler;
import com.wallakoala.wallakoala.Utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;

/**
 * @class Pantalla de un producto.
 * Created by Daniel Mancebo Aldea on 23/01/2016.
 */

public class ProductUI extends AppCompatActivity
{
    /* Constants */
    private static final String TAG = "CUOKA";
    private static final String PACKAGE = "com.wallakoala.wallakoala";
    private static final TimeInterpolator sDecelerator = new DecelerateInterpolator();
    private static final int ANIM_DURATION = 500;
    private static boolean EXITING;

    /* Container Views */
    protected FrameLayout mTopLevelLayout;
    protected RecyclerView mImagesRecylcerView;
    protected CoordinatorLayout mCoordinatorLayout;
    protected LinearLayout mProductInfoLayout;
    protected ListView mColorIconListView;

    /* Adapter */
    protected ProductAdapter mImagesAdapter;
    protected ColorIconListAdapter mColorIconAdapter;

    /* LayoutManager */
    protected LinearLayoutManager mLinearLayoutManager;

    /* ScrollDisabler */
    protected RecyclerView.OnItemTouchListener mScrollDisabler;

    /* Views */
    protected ImageView mImageView;

    /* TextViews */
    protected TextView mProductNameTextView;
    protected TextView mProductPriceTextView;

    /* Floating Button */
    protected FloatingActionButton mFloatingActionButton;

    /* Animations */
    protected Animation mExplodeAnimation, mImplodeAnimation;
    protected Animation mScaleUp, mScaleDown;

    /* Data */
    protected Product mProduct;
    protected String mBitmapUri;
    protected BitmapDrawable mBitmapDrawable;
    protected ColorDrawable mBackground;

    /* Others */
    protected int mLeftDelta;
    protected int mTopDelta;
    protected float mWidthScale;
    protected float mHeightScale;
    protected int mThumbnailLeft;
    protected int mThumbnailTop;
    protected float mThumbnailWidth;
    protected float mThumbnailHeight;
    protected float mTopOffset;
    protected int mFloatingButtonX;
    protected int mFloatingButtonY;
    protected int mFloatingButtonTop;
    protected int mRadiusReveal;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Especificamos el layout 'product.xml'
        setContentView(R.layout.product);

        _initData();
        _initViews();
        _initIconListView();
        _initRecyclerView();
        _initAnimations();

        // Solo lo ejecutamos si venimos de la activity padre
        if (savedInstanceState == null)
        {
            // Listener global
            ViewTreeObserver observer = mImageView.getViewTreeObserver();
            observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
            {
                @Override
                public boolean onPreDraw()
                {
                    mImageView.getViewTreeObserver().removeOnPreDrawListener(this);

                    // Sacamos donde esta la imagen pequeña y lo que hay que desplazarse hacia ella
                    int[] screenLocation = new int[2];
                    mImageView.getLocationOnScreen(screenLocation);
                    mLeftDelta = mThumbnailLeft - screenLocation[0];
                    mTopDelta = mThumbnailTop - screenLocation[1];

                    // Factores de escala para saber cuanto hay que encojer o agrandar la imagen
                    mWidthScale = mThumbnailWidth / mImageView.getWidth();
                    mHeightScale = mThumbnailHeight / mImageView.getHeight();

                    runEnterAnimation();

                    return true;
                }
            });
        }
    }

    /**
     * Metodo que inicializa todos los datos.
     */
    protected void _initData()
    {
        EXITING = false;

        Bundle bundle = getIntent().getExtras();

        mThumbnailTop    = bundle.getInt(PACKAGE + ".top");
        mThumbnailLeft   = bundle.getInt(PACKAGE + ".left");
        mThumbnailWidth  = bundle.getInt(PACKAGE + ".width");
        mThumbnailHeight = bundle.getInt(PACKAGE + ".height");
        mBitmapUri       = bundle.getString(PACKAGE + ".bitmap");
        mProduct         = (Product)bundle.getSerializable(PACKAGE + ".Beans.Product");

        mTopOffset = 0.0f;
    }

    /**
     * Metodo que inicializa todas las vistas.
     */
    protected void _initViews()
    {
        mImageView            = (ImageView)findViewById(R.id.imageView);
        mTopLevelLayout       = (FrameLayout)findViewById(R.id.topLevelLayout);
        mFloatingActionButton = (FloatingActionButton)findViewById(R.id.floatingButton);
        mCoordinatorLayout    = (CoordinatorLayout)findViewById(R.id.product_coordinator_layout);
        mProductInfoLayout    = (LinearLayout)findViewById(R.id.product_info);
        mProductNameTextView  = (TextView)findViewById(R.id.product_info_name);
        mProductPriceTextView = (TextView)findViewById(R.id.product_info_price);

        /* Info del producto */
        mProductNameTextView.setText(mProduct.getName());
        mProductPriceTextView.setText(String.format("%.2f", mProduct.getPrice()) + "€");
        mProductInfoLayout.setVisibility(View.INVISIBLE);

        /* Floating Button */
        mFloatingActionButton.setVisibility(View.GONE);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
                int bottomHalf = screenHeight - (mFloatingButtonTop + (mFloatingActionButton.getHeight()/2));
                int offset = mProductInfoLayout.getHeight() - bottomHalf;

                // Deshabilitamos el scroll si se abre la pantalla de info
                if (mProductInfoLayout.getVisibility() == View.INVISIBLE)
                    mImagesRecylcerView.addOnItemTouchListener(mScrollDisabler);
                else
                    mImagesRecylcerView.removeOnItemTouchListener(mScrollDisabler);

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
                {
                    SupportAnimator animator =
                            ViewAnimationUtils.createCircularReveal(mProductInfoLayout
                                    , mFloatingButtonX
                                    , mFloatingButtonY
                                    , 0
                                    , mRadiusReveal);

                    if (mProductInfoLayout.getVisibility() == View.INVISIBLE)
                    {
                        mProductInfoLayout.setVisibility(View.VISIBLE);

                        animator.setDuration(200);
                        animator.setInterpolator(new AccelerateDecelerateInterpolator());

                        mFloatingActionButton.animate()
                                .translationYBy(-offset)
                                .setDuration(400)
                                .setStartDelay(75)
                                .setInterpolator(new OvershootInterpolator()).start();

                        animator.start();

                    } else {
                        SupportAnimator animator_reverse = animator.reverse();

                        animator_reverse.setDuration(200);
                        animator.setInterpolator(new AccelerateDecelerateInterpolator());
                        animator_reverse.addListener(new SupportAnimator.AnimatorListener() {
                            @Override
                            public void onAnimationStart() {
                            }

                            @Override
                            public void onAnimationEnd() {
                                mProductInfoLayout.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onAnimationCancel() {
                            }

                            @Override
                            public void onAnimationRepeat() {
                            }
                        });

                        mFloatingActionButton.animate()
                                .translationYBy(offset)
                                .setDuration(400)
                                .setStartDelay(75)
                                .setInterpolator(new OvershootInterpolator()).start();

                        animator_reverse.start();
                    }

                } else {
                    if (mProductInfoLayout.getVisibility() == View.INVISIBLE)
                    {
                        mProductInfoLayout.setVisibility(View.VISIBLE);

                        Animator animator =
                                android.view.ViewAnimationUtils.createCircularReveal(mProductInfoLayout
                                        , mFloatingButtonX
                                        , mFloatingButtonY
                                        , 0
                                        , mRadiusReveal);

                        animator.setDuration(200);
                        animator.setInterpolator(new AccelerateDecelerateInterpolator());

                        mFloatingActionButton.animate()
                                .translationYBy(-offset)
                                .setDuration(400)
                                .setStartDelay(75)
                                .setInterpolator(new OvershootInterpolator()).start();

                        animator.start();

                    } else {
                        Animator animator =
                                android.view.ViewAnimationUtils.createCircularReveal(mProductInfoLayout
                                        , mFloatingButtonX
                                        , mFloatingButtonY
                                        , mRadiusReveal
                                        , 0);

                        animator.setDuration(200);
                        animator.setInterpolator(new AccelerateDecelerateInterpolator());
                        animator.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                mProductInfoLayout.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {
                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {
                            }
                        });

                        mFloatingActionButton.animate()
                                .translationYBy(offset)
                                .setDuration(400)
                                .setStartDelay(75)
                                .setInterpolator(new OvershootInterpolator()).start();

                        animator.start();

                    }

                }

            }
        });

        /* Bitmap */
        File filePath = getFileStreamPath(mBitmapUri);
        mBitmapDrawable = (BitmapDrawable)Drawable.createFromPath(filePath.toString());
        mImageView.setImageDrawable(mBitmapDrawable);

        /* Background */
        mBackground = new ColorDrawable(Color.WHITE);
        mTopLevelLayout.setBackground(mBackground);
    }

    /**
     * Metodo que inicializa la ListView de iconos.
     */
    protected void _initIconListView()
    {
        mColorIconListView = (ListView)findViewById(R.id.product_info_list_colors);

        mColorIconAdapter = new ColorIconListAdapter(this, mProduct.getColors());
        mColorIconListView.setAdapter(mColorIconAdapter);
    }

    /**
     * Metodo que inicializa el RecyclerView.
     */
    protected void _initRecyclerView()
    {
        // Calculamos el aspect ratio de la imagen
        double ratio = (double)mBitmapDrawable.getIntrinsicHeight() / (double)mBitmapDrawable.getIntrinsicWidth();

        mImagesRecylcerView = (RecyclerView)findViewById(R.id.product_recycler_view);

        mScrollDisabler = new RecyclerScrollDisabler();
        mLinearLayoutManager = new LinearLayoutManager(this);
        mImagesAdapter = new ProductAdapter(this
                                , mProduct.getColors().get(0)
                                , ratio
                                , mProduct.getShop()
                                , mProduct.getSection()
                                , mImageView);

        mImagesRecylcerView.setLayoutManager(mLinearLayoutManager);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mImagesRecylcerView.setAdapter(mImagesAdapter);

        mImagesRecylcerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                mTopOffset += dy;
            }
        });
    }

    /**
     * Metodo que inicializa todas las animaciones.
     */
    protected void _initAnimations()
    {
        mScaleUp          = AnimationUtils.loadAnimation(this, R.anim.scale_up);
        mScaleDown        = AnimationUtils.loadAnimation(this, R.anim.scale_down);
        mExplodeAnimation = AnimationUtils.loadAnimation(this, R.anim.explode);
        mImplodeAnimation = AnimationUtils.loadAnimation(this, R.anim.implode);

        mImplodeAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mFloatingActionButton.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    /**
     * La animacion de entrada escala la imagen desde la pequeña hasta la posicion/tamaño de la grande.
     * En paralelo, el fondo se va oscureciendo.
     */
    private void runEnterAnimation()
    {
        final long duration = (int)(ANIM_DURATION * 0.5);

        mImageView.setPivotX(0);
        mImageView.setPivotY(0);
        mImageView.setScaleX(mWidthScale);
        mImageView.setScaleY(mHeightScale);
        mImageView.setTranslationX(mLeftDelta);
        mImageView.setTranslationY(mTopDelta);

        // Animacion de escalado y desplazamiento hasta el tamaño grande
        mImageView.animate().setDuration(duration)
                            .scaleX(1).scaleY(1)
                            .translationX(0).translationY(0)
                            .setInterpolator(sDecelerator)
                            .setListener(new Animator.AnimatorListener()
                            {
                                @Override
                                public void onAnimationStart(Animator animation) {}

                                @Override
                                public void onAnimationEnd(Animator animation)
                                {
                                    if (!EXITING)
                                    {
                                        mImagesRecylcerView.setVisibility(View.VISIBLE);

                                        // Hacemos aparecer el FloatingButton
                                        mFloatingActionButton.setVisibility(View.VISIBLE);
                                        mFloatingActionButton.startAnimation(mExplodeAnimation);

                                        mFloatingButtonX = (mFloatingActionButton.getLeft()
                                                                    + mFloatingActionButton.getRight())/2;

                                        mFloatingButtonY = ((int) mFloatingActionButton.getY()
                                                + mFloatingActionButton.getHeight())/2;

                                        int[] screenLocation = new int[2];
                                        mFloatingActionButton.getLocationOnScreen(screenLocation);
                                        mFloatingButtonTop = screenLocation[1];

                                        mRadiusReveal = Math.max(mProductInfoLayout.getWidth()
                                                            , mProductInfoLayout.getHeight());
                                    }
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {}

                                @Override
                                public void onAnimationRepeat(Animator animation) {}
                            });

        // Efecto fade para oscurecer la pantalla
        ObjectAnimator bgAnim = ObjectAnimator.ofInt(mBackground, "alpha", 0, 255);
        bgAnim.setDuration(duration);
        bgAnim.start();
    }

    /**
     * La animacion de salida es la misma animacion de entrada pero al reves
     * @param endAction: Accion que se ejecuta cuando termine la animacion.
     */
    private void runExitAnimation(final Runnable endAction)
    {
        final long duration = (int)(ANIM_DURATION * 0.6);

        EXITING = true;

        mImageView.setVisibility(View.VISIBLE);
        mImagesRecylcerView.setVisibility(View.GONE);

        // Si se ha producido un scroll en el recyclerView, desplazamos la imagen
        mImageView.setTranslationY(-mTopOffset);

        mImageView.animate().setDuration(duration)
                            .scaleX(mWidthScale).scaleY(mHeightScale)
                            .translationX(mLeftDelta).translationY(mTopDelta)
                            .withEndAction(endAction);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
        {
            SupportAnimator animator =
                    ViewAnimationUtils.createCircularReveal(mProductInfoLayout
                            , mFloatingButtonX
                            , mFloatingButtonY
                            , 0
                            , mRadiusReveal);

            SupportAnimator animator_reverse = animator.reverse();

            animator_reverse.setDuration(100);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator_reverse.addListener(new SupportAnimator.AnimatorListener() {
                @Override
                public void onAnimationStart() {}

                @Override
                public void onAnimationEnd() {
                    mProductInfoLayout.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationCancel() {}

                @Override
                public void onAnimationRepeat() {}
            });

            animator_reverse.start();

        } else {
            Animator animator =
                    android.view.ViewAnimationUtils.createCircularReveal(mProductInfoLayout
                            , mFloatingButtonX
                            , mFloatingButtonY
                            , mRadiusReveal
                            , 0);

            animator.setDuration(100);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {}

                @Override
                public void onAnimationEnd(Animator animation)
                {
                    mProductInfoLayout.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {}

                @Override
                public void onAnimationRepeat(Animator animation) {}
            });

            animator.start();
        }

        mFloatingActionButton.startAnimation(mImplodeAnimation);

        // Aclarar el fondo
        ObjectAnimator bgAnim = ObjectAnimator.ofInt(mBackground, "alpha", 0);
        bgAnim.setDuration(duration);
        bgAnim.start();
    }

    @Override
    public void onBackPressed()
    {
        if ( ! EXITING )
        {
            runExitAnimation(new Runnable()
            {
                public void run() {
                    finish();
                }
            });
        }
    }

    @Override
    public void finish()
    {
        super.finish();

        // Deshabilitamos las animaciones de Android
        overridePendingTransition(0, 0);
    }
}
