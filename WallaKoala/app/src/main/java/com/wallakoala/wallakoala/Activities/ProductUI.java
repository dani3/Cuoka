package com.wallakoala.wallakoala.Activities;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.res.Resources;
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
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.wallakoala.wallakoala.Adapters.ColorIconListAdapter;
import com.wallakoala.wallakoala.Adapters.ProductAdapter;
import com.wallakoala.wallakoala.Beans.Product;
import com.wallakoala.wallakoala.R;
import com.wallakoala.wallakoala.Utils.RecyclerScrollDisabler;

import java.io.File;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;

/**
 * @class Pantalla de un producto.
 * Created by Daniel Mancebo Aldea on 23/01/2016.
 */

public class ProductUI extends AppCompatActivity
{
    /* Constants */
    protected static final String TAG = "CUOKA";
    protected static final String PACKAGE = "com.wallakoala.wallakoala";
    protected static final TimeInterpolator sDecelerator = new DecelerateInterpolator();
    protected static final int ANIM_DURATION = 500;
    protected static boolean EXITING;

    /* Context */
    protected static Context mContext;

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
    protected TextView mProductReferenceTextView;
    protected TextView mProductDescriptionTextView;

    /* Floating Button */
    protected FloatingActionButton mFloatingActionButtonPlus;

    /* Animations */
    protected Animation mExplodeAnimation, mImplodeAnimation;
    protected Animation mScaleUp, mScaleDown;

    /* Data */
    protected Product mProduct;
    protected String mBitmapUri;
    protected BitmapDrawable mBitmapDrawable;
    protected ColorDrawable mBackground;
    protected int mCurrentColor;

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
    protected double mRatio;

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

        mContext = this;

        Bundle bundle = getIntent().getExtras();

        mThumbnailTop    = bundle.getInt(PACKAGE + ".top");
        mThumbnailLeft   = bundle.getInt(PACKAGE + ".left");
        mThumbnailWidth  = bundle.getInt(PACKAGE + ".width");
        mThumbnailHeight = bundle.getInt(PACKAGE + ".height");
        mBitmapUri       = bundle.getString(PACKAGE + ".bitmap");
        mProduct         = (Product)bundle.getSerializable(PACKAGE + ".Beans.Product");

        mTopOffset = 0.0f;

        mCurrentColor = 0;
    }

    /**
     * Metodo que inicializa todas las vistas.
     */
    protected void _initViews()
    {
        mImageView                  = (ImageView)findViewById(R.id.imageView);
        mTopLevelLayout             = (FrameLayout)findViewById(R.id.topLevelLayout);
        mFloatingActionButtonPlus   = (FloatingActionButton)findViewById(R.id.floatingButton);
        mCoordinatorLayout          = (CoordinatorLayout)findViewById(R.id.product_coordinator_layout);
        mProductInfoLayout          = (LinearLayout)findViewById(R.id.product_info);
        mProductNameTextView        = (TextView)findViewById(R.id.product_info_name);
        mProductPriceTextView       = (TextView)findViewById(R.id.product_info_price);
        mProductReferenceTextView   = (TextView)findViewById(R.id.product_info_reference);
        mProductDescriptionTextView = (TextView)findViewById(R.id.product_info_description);

        /* Inicializamos la info del producto */
        mProductNameTextView.setText(mProduct.getName());
        mProductPriceTextView.setText(String.format("%.2f", mProduct.getPrice()) + "€");
        mProductReferenceTextView.setText(mProduct.getColors().get(0).getReference());
        mProductDescriptionTextView.setText(mProduct.getDescription());
        mProductInfoLayout.setVisibility(View.INVISIBLE);

        /* Floating Button */
        mFloatingActionButtonPlus.setVisibility(View.GONE);
        mFloatingActionButtonPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Calculamos cuanto hay que desplazar el FAB hasta el borde del layout de info.
                int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
                int bottomHalf = screenHeight - (mFloatingButtonTop + (mFloatingActionButtonPlus.getHeight() / 2));
                int offset = mProductInfoLayout.getHeight() - bottomHalf;

                // Deshabilitamos el scroll si se abre la pantalla de info y cambiamos el icono del FAB
                if (mProductInfoLayout.getVisibility() == View.INVISIBLE) {
                    mImagesRecylcerView.addOnItemTouchListener(mScrollDisabler);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        mFloatingActionButtonPlus.setImageDrawable(
                                getResources().getDrawable(R.drawable.ic_remove_white
                                        , mContext.getTheme()));
                    else
                        mFloatingActionButtonPlus.setImageDrawable(
                                getResources().getDrawable(R.drawable.ic_remove_white));

                } else {
                    mImagesRecylcerView.removeOnItemTouchListener(mScrollDisabler);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        mFloatingActionButtonPlus.setImageDrawable(
                                getResources().getDrawable(R.drawable.ic_add_white
                                        , mContext.getTheme()));
                    else
                        mFloatingActionButtonPlus.setImageDrawable(
                                getResources().getDrawable(R.drawable.ic_add_white));
                }

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    SupportAnimator animator =
                            ViewAnimationUtils.createCircularReveal(mProductInfoLayout
                                    , mFloatingButtonX
                                    , mFloatingButtonY
                                    , 0
                                    , mRadiusReveal);

                    if (mProductInfoLayout.getVisibility() == View.INVISIBLE) {
                        mProductInfoLayout.setVisibility(View.VISIBLE);

                        animator.setDuration(200);
                        animator.setInterpolator(new AccelerateDecelerateInterpolator());

                        mFloatingActionButtonPlus.animate()
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

                        mFloatingActionButtonPlus.animate()
                                .translationYBy(offset)
                                .setDuration(400)
                                .setStartDelay(75)
                                .setInterpolator(new OvershootInterpolator()).start();

                        animator_reverse.start();
                    }

                } else {
                    if (mProductInfoLayout.getVisibility() == View.INVISIBLE) {
                        mProductInfoLayout.setVisibility(View.VISIBLE);

                        Animator animator =
                                android.view.ViewAnimationUtils.createCircularReveal(mProductInfoLayout
                                        , mFloatingButtonX
                                        , mFloatingButtonY
                                        , 0
                                        , mRadiusReveal);

                        animator.setDuration(200);
                        animator.setInterpolator(new AccelerateDecelerateInterpolator());

                        mFloatingActionButtonPlus.animate()
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

                        mFloatingActionButtonPlus.animate()
                                .translationYBy(offset)
                                .setDuration(400)
                                .setStartDelay(75)
                                .setInterpolator(new OvershootInterpolator()).start();

                        animator.start();

                    }

                }

            }
        });

        /* Cargamos el bitmap de la imagen en baja calidad */
        File filePath = getFileStreamPath(mBitmapUri);
        mBitmapDrawable = (BitmapDrawable)Drawable.createFromPath(filePath.toString());
        mImageView.setImageDrawable(mBitmapDrawable);

        /* Calculamos el aspect ratio de la imagen */
        mRatio = (double)mBitmapDrawable.getIntrinsicHeight() / (double)mBitmapDrawable.getIntrinsicWidth();

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

        mColorIconAdapter = new ColorIconListAdapter(this, mProduct.getColors(), mProduct.getShop(), mProduct.getSection());
        mColorIconListView.setAdapter(mColorIconAdapter);

        // Listener para cambiar de color
        mColorIconListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if (mCurrentColor != position)
                {
                    mColorIconAdapter.setSelected(position);
                    mColorIconAdapter.notifyDataSetChanged();

                    mImagesAdapter = new ProductAdapter(ProductUI.this
                            , mProduct.getColors().get(position)
                            , mRatio
                            , mProduct.getShop()
                            , mProduct.getSection()
                            , mImageView);

                    mImagesRecylcerView.setAdapter(mImagesAdapter);

                    mCurrentColor = position;

                    mProductReferenceTextView.setText(mProduct.getColors().get(mCurrentColor).getReference());
                }

            }
        });
    }

    /**
     * Metodo que inicializa el RecyclerView.
     */
    protected void _initRecyclerView()
    {
        mImagesRecylcerView = (RecyclerView)findViewById(R.id.product_recycler_view);

        mScrollDisabler = new RecyclerScrollDisabler();
        mLinearLayoutManager = new LinearLayoutManager(this);
        mImagesAdapter = new ProductAdapter(this
                                , mProduct.getColors().get(mCurrentColor)
                                , mRatio
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
                mFloatingActionButtonPlus.setVisibility(View.GONE);
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
                                        mFloatingActionButtonPlus.setVisibility(View.VISIBLE);
                                        mFloatingActionButtonPlus.startAnimation(mExplodeAnimation);

                                        // Una vez cargado, nos quedamos con las coordenadas del FAB
                                        mFloatingButtonX = (mFloatingActionButtonPlus.getLeft()
                                                                    + mFloatingActionButtonPlus.getRight())/2;

                                        mFloatingButtonY = ((int) mFloatingActionButtonPlus.getY()
                                                                    + mFloatingActionButtonPlus.getHeight())/2;

                                        int[] screenLocation = new int[2];
                                        mFloatingActionButtonPlus.getLocationOnScreen(screenLocation);
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

        mFloatingActionButtonPlus.startAnimation(mImplodeAnimation);

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
