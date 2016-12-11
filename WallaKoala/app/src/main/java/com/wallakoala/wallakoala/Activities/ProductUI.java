package com.wallakoala.wallakoala.Activities;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableString;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.wallakoala.wallakoala.Adapters.ColorIconListAdapter;
import com.wallakoala.wallakoala.Adapters.ProductAdapter;
import com.wallakoala.wallakoala.Beans.Product;
import com.wallakoala.wallakoala.Properties.Properties;
import com.wallakoala.wallakoala.R;
import com.wallakoala.wallakoala.Singletons.RestClientSingleton;
import com.wallakoala.wallakoala.Utils.SharedPreferencesManager;
import com.wallakoala.wallakoala.Utils.Utils;
import com.wallakoala.wallakoala.Views.LikeButtonLargeView;

import java.io.File;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;

/**
 * Pantalla de un producto.
 * Created by Daniel Mancebo Aldea on 23/01/2016.
 */

public class ProductUI extends AppCompatActivity implements GestureDetector.OnGestureListener
                                                    , GestureDetector.OnDoubleTapListener
{
    /* Constants */
    private static final TimeInterpolator ACCELERATE_DECELERATE_INTERPOLATOR = new AccelerateDecelerateInterpolator();
    private static final int ANIM_DURATION = 250;
    private static boolean EXITING;
    private static boolean COLLAPSING;

    /* Context */
    private Context mContext;

    /* Container Views */
    private RecyclerView mImagesRecylcerView;
    private LinearLayout mProductInfoLayout;

    /* Adapter */
    private ProductAdapter mImagesAdapter;
    private ColorIconListAdapter mColorIconAdapter;

    /* GestureDetector */
    private GestureDetectorCompat mGestureDetector;

    /* Views */
    private ImageView mImageView;
    private LikeButtonLargeView mFavoriteImageButton;

    /* TextViews */
    private TextView mProductReferenceTextView;

    /* Floating Button */
    private FloatingActionButton mFloatingActionButtonPlus;

    /* Animations */
    private Animation mExplodeAnimation, mImplodeAnimation;

    /* Data */
    private Product mProduct;
    private String mBitmapUri;
    private ColorDrawable mBackground;
    private int mCurrentColor;

    /* SharedPreferences */
    private SharedPreferencesManager mSharedPreferencesManager;

    /* Others */
    private int mLeftDeltaImage, mLeftDeltaFav;
    private int mTopDeltaImage, mTopDeltaFav;
    private float mWidthScaleImage, mWidthScaleFav;
    private float mHeightScaleImage, mHeightScaleFav;
    private int mThumbnailLeft, mThumbnailLeftFav;
    private int mThumbnailTop, mThumbnailTopFav;
    private float mThumbnailWidth, mThumbnailWidthFav;
    private float mThumbnailHeight, mThumbnailHeightFav;
    private float mTopOffset;
    private int mFloatingButtonX;
    private int mFloatingButtonY;
    private int mFloatingButtonTop;
    private int mRadiusReveal;
    private double mRatio;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Especificamos el layout 'activity_product.xmloduct.xml'
        setContentView(R.layout.activity_product);

        _initData();
        _initViews();
        _initIconListView();
        _initRecyclerView();
        _initAnimations();
        _sendViewedProduct();
        _fetchImages();

        // Solo lo ejecutamos si venimos de la activity padre
        if (savedInstanceState == null)
        {
            // Listener global
            final ViewTreeObserver observer = mImageView.getViewTreeObserver();
            observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
            {
                @Override
                public boolean onPreDraw()
                {
                    mImageView.getViewTreeObserver().removeOnPreDrawListener(this);

                    // Sacamos donde esta la imagen pequeña y lo que hay que desplazarse hacia ella
                    int[] imageScreenLocation = new int[2];
                    mImageView.getLocationOnScreen(imageScreenLocation);
                    mLeftDeltaImage = mThumbnailLeft - imageScreenLocation[0];
                    mTopDeltaImage  = mThumbnailTop - imageScreenLocation[1];

                    // Factores de escala para saber cuanto hay que encojer o agrandar la imagen
                    mWidthScaleImage = mThumbnailWidth / mImageView.getWidth();
                    mHeightScaleImage = mThumbnailHeight / mImageView.getHeight();

                    // Lo mismo para el boton de favorito
                    if (mThumbnailWidthFav != 0)
                    {
                        int[] favScreenLocation = new int[2];
                        mFavoriteImageButton.getLocationOnScreen(favScreenLocation);
                        mLeftDeltaFav = mThumbnailLeftFav - favScreenLocation[0];
                        mTopDeltaFav  = mThumbnailTopFav - favScreenLocation[1];

                        mWidthScaleFav  = mThumbnailWidthFav / mFavoriteImageButton.getWidth();
                        mHeightScaleFav = mThumbnailHeightFav / mFavoriteImageButton.getHeight();
                    }

                    _runEnterAnimation();

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
        COLLAPSING = false;

        mSharedPreferencesManager = new SharedPreferencesManager(this);

        mContext = this;

        mGestureDetector = new GestureDetectorCompat(this, this);
        mGestureDetector.setOnDoubleTapListener(this);

        Bundle bundle = getIntent().getExtras();

        mThumbnailTopFav    = bundle.getInt(Properties.PACKAGE + ".topFav");
        mThumbnailLeftFav   = bundle.getInt(Properties.PACKAGE + ".leftFav");
        mThumbnailWidthFav  = bundle.getInt(Properties.PACKAGE + ".widthFav");
        mThumbnailHeightFav = bundle.getInt(Properties.PACKAGE + ".heightFav");
        mThumbnailTop       = bundle.getInt(Properties.PACKAGE + ".top");
        mThumbnailLeft      = bundle.getInt(Properties.PACKAGE + ".left");
        mThumbnailWidth     = bundle.getInt(Properties.PACKAGE + ".width");
        mThumbnailHeight    = bundle.getInt(Properties.PACKAGE + ".height");
        mBitmapUri          = bundle.getString(Properties.PACKAGE + ".bitmap");
        mProduct            = (Product)bundle.getSerializable(Properties.PACKAGE + ".Beans.Product");

        mTopOffset = 0.0f;

        mCurrentColor = 0;
    }

    /**
     * Metodo que inicializa todas las vistas.
     */
    @SuppressWarnings("deprecation")
    protected void _initViews()
    {
        FrameLayout frameLayout              = (FrameLayout) findViewById(R.id.topLevelLayout);
        TextView mProductNameTextView        = (TextView) findViewById(R.id.product_info_name);
        TextView mProductPriceTextView       = (TextView) findViewById(R.id.product_info_price);
        TextView mProductDescriptionTextView = (TextView) findViewById(R.id.product_info_description);
        TextView mProductShopTextView        = (TextView) findViewById(R.id.product_info_shop);

        mImageView                = (ImageView) findViewById(R.id.imageView);
        mFloatingActionButtonPlus = (FloatingActionButton) findViewById(R.id.floatingButton);
        mProductInfoLayout        = (LinearLayout) findViewById(R.id.product_info);
        mProductReferenceTextView = (TextView) findViewById(R.id.product_info_reference);
        mFavoriteImageButton      = (LikeButtonLargeView) findViewById(R.id.product_favorite);

        ImageButton shareImageButton = (ImageButton) findViewById(R.id.product_share);
        ImageButton redirectImageButton = (ImageButton) findViewById(R.id.product_redirect);

        // Inicializamos los listeners a todos los botones.
        redirectImageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                _redirectToShop();
            }
        });

        shareImageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                _shareProduct();
            }
        });

        // Inicializamos la info del producto
        boolean emptyDescription = (mProduct.getDescription() == null ||
                mProduct.getDescription().isEmpty() ||
                mProduct.getDescription().equalsIgnoreCase("null"));

        String name = "<b>" + mProduct.getName() + "</b>";
        String reference = "<b>Referencia: </b>" +  mProduct.getColors().get(0).getReference();
        String description = emptyDescription ? mProduct.getName().toLowerCase() : mProduct.getDescription();
        description = Character.toUpperCase(description.charAt(0)) + description.substring(1);
        description = "<b>Descripción: </b>" + description;
        SpannableString price = Utils.priceToString(mProduct.getPrice());

        mProductNameTextView.setText(Html.fromHtml(name));
        mProductShopTextView.setText(mProduct.getShop());
        mProductDescriptionTextView.setText(Html.fromHtml(description));
        mProductReferenceTextView.setText(Html.fromHtml(reference));
        mProductPriceTextView.setText(price);

        mProductInfoLayout.setVisibility(View.INVISIBLE);

        mFavoriteImageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!mFavoriteImageButton.ANIMATING)
                {
                    RestClientSingleton.sendFavoriteProduct(mContext, mProduct);

                    mFavoriteImageButton.startAnimation();
                }
            }
        });

        // Floating Button para expandir la info.
        mFloatingActionButtonPlus.setVisibility(View.GONE);
        mFloatingActionButtonPlus.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mProductInfoLayout.getVisibility() == View.INVISIBLE)
                {
                    _expandInfo();

                } else {
                    _collapseInfo();
                }
            }
        });

        // Cargamos el bitmap de la imagen en baja calidad.
        File filePath = getFileStreamPath(mBitmapUri);
        BitmapDrawable bitmapDrawable = (BitmapDrawable) Drawable.createFromPath(filePath.toString());
        mImageView.setImageDrawable(bitmapDrawable);

        // Calculamos el aspect ratio de la imagen.
        mRatio = (double) bitmapDrawable.getIntrinsicHeight() / (double) bitmapDrawable.getIntrinsicWidth();

        // Background.
        mBackground = new ColorDrawable(Color.WHITE);
        frameLayout.setBackground(mBackground);
    }

    /**
     * Metodo que inicializa la ListView de iconos.
     */
    @SuppressWarnings("deprecation")
    private void _initIconListView()
    {
        ListView mColorIconListView = (ListView) findViewById(R.id.product_info_list_colors);

        mColorIconAdapter = new ColorIconListAdapter(this
                                    , mProduct.getColors()
                                    , mProduct.getShop()
                                    , mProduct.getSection());

        mColorIconListView.setAdapter(mColorIconAdapter);

        // Listener para cambiar de color.
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

                    String reference = "<b>Referencia: </b>" + mProduct.getColors().get(mCurrentColor).getReference();
                    mProductReferenceTextView.setText(Html.fromHtml(reference));
                }

            }
        });
    }

    /**
     * Metodo que inicializa el RecyclerView.
     */
    @SuppressWarnings("deprecation")
    private void _initRecyclerView()
    {
        mImagesRecylcerView = (RecyclerView)findViewById(R.id.product_recycler_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mImagesAdapter = new ProductAdapter(this
                                , mProduct.getColors().get(mCurrentColor)
                                , mRatio
                                , mProduct.getShop()
                                , mProduct.getSection()
                                , mImageView);

        mImagesRecylcerView.setLayoutManager(linearLayoutManager);
        mImagesRecylcerView.setAdapter(mImagesAdapter);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mImagesRecylcerView.setOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {}

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                mTopOffset += dy;
            }
        });
    }

    /**
     * Metodo que inicializa todas las animaciones.
     */
    private void _initAnimations()
    {
        mExplodeAnimation = AnimationUtils.loadAnimation(this, R.anim.explode_animation);
        mImplodeAnimation = AnimationUtils.loadAnimation(this, R.anim.implode_animation);

        mImplodeAnimation.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation)
            {
                mFloatingActionButtonPlus.setVisibility(View.GONE);

                if (mThumbnailWidthFav == 0)
                {
                    mFavoriteImageButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }

    /**
     * Metodo que llama al servidor para indicar que se ha visto este producto.
     */
    private void _sendViewedProduct()
    {
        RestClientSingleton.sendViewedProduct(mContext, mProduct);
    }

    /**
     * Metodo que precarga las imagenes antes de que aparezcan por pantalla.
     */
    private void _fetchImages()
    {
        Utils.fetchImages(mContext, mProduct, mCurrentColor);
    }

    /**
     * Metodo que redirecciona a la web de la tienda.
     */
    private void _redirectToShop()
    {
        if (mProduct.getLink().startsWith("http") || mProduct.getLink().startsWith("https"))
        {
            Intent intent = new Intent(Intent.ACTION_VIEW);

            intent.setData(Uri.parse(mProduct.getLink()));

            startActivity(intent);
        }
    }

    /**
     * Metodo que comparte el producto.
     */
    private void _shareProduct()
    {
        Uri uri = mImagesAdapter.getFirstImageUri();

        if (mImagesAdapter != null && uri != null)
        {
            Intent shareIntent = new Intent();

            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Mira lo que he encontrado en Cuoka");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Probando");
            shareIntent.setType("*/*");

            startActivity(Intent.createChooser(shareIntent, "Compartir imagen"));
        }
    }

    /**
     * La animacion de entrada escala la imagen desde la pequeña hasta la posicion/tamaño de la grande.
     * En paralelo, el fondo se va oscureciendo.
     */
    private void _runEnterAnimation()
    {
        mImageView.setPivotX(0);
        mImageView.setPivotY(0);
        mImageView.setScaleX(mWidthScaleImage);
        mImageView.setScaleY(mHeightScaleImage);
        mImageView.setTranslationX(mLeftDeltaImage);
        mImageView.setTranslationY(mTopDeltaImage);

        mFavoriteImageButton.changeIcon(
                mSharedPreferencesManager.retreiveUser().getFavoriteProducts().contains(mProduct.getId()));

        if (mThumbnailWidthFav != 0)
        {
            mFavoriteImageButton.setPivotX(0);
            mFavoriteImageButton.setPivotY(0);
            mFavoriteImageButton.setScaleX(mWidthScaleFav);
            mFavoriteImageButton.setScaleY(mHeightScaleFav);
            mFavoriteImageButton.setTranslationX(mLeftDeltaFav);
            mFavoriteImageButton.setTranslationY(mTopDeltaFav);

            // Animacion de escalado y desplazamiento hasta el tamaño grande
            mFavoriteImageButton.animate().setDuration(ANIM_DURATION)
                    .scaleX(1).scaleY(1)
                    .translationX(0).translationY(0)
                    .setInterpolator(ACCELERATE_DECELERATE_INTERPOLATOR);
        } else {
            mFavoriteImageButton.setVisibility(View.GONE);
        }

        // Animacion de escalado y desplazamiento hasta el tamaño grande (HARDWARE_LAYERED)
        mImageView.animate().setDuration(ANIM_DURATION)
                            .withLayer()
                            .scaleX(1).scaleY(1)
                            .translationX(0).translationY(0)
                            .setInterpolator(ACCELERATE_DECELERATE_INTERPOLATOR)
                            .setStartDelay(75)
                            .setListener(new Animator.AnimatorListener()
                            {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                }

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
                                                + mFloatingActionButtonPlus.getRight()) / 2;

                                        mFloatingButtonY = ((int) mFloatingActionButtonPlus.getY()
                                                + mFloatingActionButtonPlus.getHeight()) / 2;

                                        int[] screenLocation = new int[2];
                                        mFloatingActionButtonPlus.getLocationOnScreen(screenLocation);
                                        mFloatingButtonTop = screenLocation[1];

                                        mRadiusReveal = Math.max(mProductInfoLayout.getWidth()
                                                            , mProductInfoLayout.getHeight());

                                        // Hacemos aparecer el el boton de favorito
                                        if (mThumbnailWidthFav == 0)
                                        {
                                            mFavoriteImageButton.setVisibility(View.VISIBLE);
                                            mFavoriteImageButton.startAnimation(mExplodeAnimation);
                                        }
                                    }
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {}

                                @Override
                                public void onAnimationRepeat(Animator animation) {}
                            });

        // Efecto fade para oscurecer la pantalla
        ObjectAnimator bgAnim = ObjectAnimator.ofInt(mBackground, "alpha", 0, 255);
        bgAnim.setDuration(ANIM_DURATION);
        bgAnim.start();
    }

    /**
     * La animacion de salida es la misma animacion de entrada pero al reves
     * @param endAction: Accion que se ejecuta cuando termine la animacion.
     */
    private void _runExitAnimation(final Runnable endAction)
    {
        EXITING = true;

        mImageView.setVisibility(View.VISIBLE);
        mImagesRecylcerView.setVisibility(View.GONE);

        // Si se ha producido un scroll en el recyclerView, desplazamos la imagen
        mImageView.setTranslationY(-mTopOffset);

        // Animacion de escalado y desplazamiento hasta la imagen original (HARDWARE_LAYERED)
        mImageView.animate().setDuration(ANIM_DURATION)
                            .withLayer()
                            .setStartDelay(0)
                            .scaleX(mWidthScaleImage).scaleY(mHeightScaleImage)
                            .translationX(mLeftDeltaImage).translationY(mTopDeltaImage)
                            .withEndAction(endAction);

        if (mThumbnailWidthFav != 0)
        {
            mFavoriteImageButton.animate()
                                .setDuration(ANIM_DURATION)
                                .setStartDelay(75)
                                .scaleX(mWidthScaleFav).scaleY(mHeightScaleFav)
                                .translationX(mLeftDeltaFav).translationY(mTopDeltaFav)
                                .withEndAction(endAction);
        }

        mFavoriteImageButton.changeIcon(
                mSharedPreferencesManager.retreiveUser().getFavoriteProducts().contains(mProduct.getId()));

        if (mProductInfoLayout.getVisibility() == View.VISIBLE)
        {
            _collapseInfo();
        }

        mFloatingActionButtonPlus.startAnimation(mImplodeAnimation);

        if (mThumbnailWidthFav == 0)
        {
            mFavoriteImageButton.startAnimation(mImplodeAnimation);
        }

        // Aclarar el fondo
        ObjectAnimator bgAnim = ObjectAnimator.ofInt(mBackground, "alpha", 0);
        bgAnim.setDuration(ANIM_DURATION);
        bgAnim.start();
    }

    @Override
    public void onBackPressed()
    {
        if (!EXITING)
        {
            mContext.deleteFile(mBitmapUri);

            _runExitAnimation(new Runnable()
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

        overridePendingTransition(0, 0);
    }

    /**
     * Metodo que expande la ventana de informacion.
     */
    @SuppressWarnings("deprecation")
    private void _expandInfo()
    {
        // Calculamos cuanto hay que desplazar el FAB hasta el borde del layout de info.
        int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        int bottomHalf = screenHeight - (mFloatingButtonTop + (mFloatingActionButtonPlus.getHeight() / 2));
        int offset = mProductInfoLayout.getHeight() - bottomHalf;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            mFloatingActionButtonPlus.setImageDrawable(
                    getResources().getDrawable(R.drawable.ic_remove_white
                            , mContext.getTheme()));
        } else {
            mFloatingActionButtonPlus.setImageDrawable(
                    getResources().getDrawable(R.drawable.ic_remove_white));
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
        {
            SupportAnimator animator =
                    ViewAnimationUtils.createCircularReveal(mProductInfoLayout
                            , mFloatingButtonX
                            , mFloatingButtonY
                            , 0
                            , mRadiusReveal);

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
        }
    }

    /**
     * Metodo que hace desaparecer la ventana de informacion.
     */
    @SuppressWarnings("deprecation")
    private void _collapseInfo()
    {
        COLLAPSING = true;

        // Calculamos cuanto hay que desplazar el FAB hasta el borde del layout de info.
        int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        int bottomHalf = screenHeight - (mFloatingButtonTop + (mFloatingActionButtonPlus.getHeight() / 2));
        int offset = mProductInfoLayout.getHeight() - bottomHalf;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            mFloatingActionButtonPlus.setImageDrawable(
                    getResources().getDrawable(R.drawable.ic_add_white
                            , mContext.getTheme()));
        } else {
            mFloatingActionButtonPlus.setImageDrawable(
                    getResources().getDrawable(R.drawable.ic_add_white));
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
        {
            SupportAnimator animator =
                    ViewAnimationUtils.createCircularReveal(mProductInfoLayout
                            , mFloatingButtonX
                            , mFloatingButtonY
                            , 0
                            , mRadiusReveal);

            SupportAnimator animator_reverse = animator.reverse();

            animator_reverse.setDuration(200);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator_reverse.addListener(new SupportAnimator.AnimatorListener()
            {
                @Override
                public void onAnimationStart() {}

                @Override
                public void onAnimationEnd()
                {
                    mProductInfoLayout.setVisibility(View.INVISIBLE);

                    COLLAPSING = false;
                }

                @Override
                public void onAnimationCancel() {}

                @Override
                public void onAnimationRepeat() {}
            });

            mFloatingActionButtonPlus.animate()
                                     .translationYBy(offset)
                                     .setDuration(400)
                                     .setStartDelay(75)
                                     .setInterpolator(new OvershootInterpolator()).start();

            animator_reverse.start();

        } else {
            Animator animator =
                    android.view.ViewAnimationUtils.createCircularReveal(mProductInfoLayout
                            , mFloatingButtonX
                            , mFloatingButtonY
                            , mRadiusReveal
                            , 0);

            animator.setDuration(200);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.addListener(new Animator.AnimatorListener()
            {
                @Override
                public void onAnimationStart(Animator animation) {}

                @Override
                public void onAnimationEnd(Animator animation)
                {
                    COLLAPSING = false;
                    mProductInfoLayout.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {}

                @Override
                public void onAnimationRepeat(Animator animation) {}
            });

            mFloatingActionButtonPlus.animate()
                                     .translationYBy(offset)
                                     .setDuration(400)
                                     .setStartDelay(75)
                                     .setInterpolator(new OvershootInterpolator()).start();

            animator.start();
        }
    }

    /**
     * IMPORTANTE, los onTouch events los captura el recycler para el scroll.
     * Es imprescindible sobreescribir este metodo para que la Activity intercepte el evento.
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event)
    {
        mGestureDetector.onTouchEvent(event);

        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onSingleTapUp(MotionEvent event)
    {
        int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        int productInfoHeight = mProductInfoLayout.getHeight();

        // Si la info esta visible, ha pulsado encima de esta y no se esta cerrando
        if ((mProductInfoLayout.getVisibility() == View.VISIBLE) &&
            (screenHeight - productInfoHeight > event.getY()) &&
            (!COLLAPSING))
        {
            Log.d(Properties.TAG, "OnSingleTap: Collapsing info");

            _collapseInfo();
        }

        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) { return false; }

    @Override
    public boolean onDoubleTap(MotionEvent event) { return false; }

    @Override
    public boolean onDown(MotionEvent event) { return true; }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) { return false; }

    @Override
    public void onLongPress(MotionEvent event) {}

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) { return false; }

    @Override
    public void onShowPress(MotionEvent event) {}

    @Override
    public boolean onDoubleTapEvent(MotionEvent event) { return false; }
}
