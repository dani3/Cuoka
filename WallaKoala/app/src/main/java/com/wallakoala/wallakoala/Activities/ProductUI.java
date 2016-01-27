package com.wallakoala.wallakoala.Activities;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.wallakoala.wallakoala.Beans.ColorVariant;
import com.wallakoala.wallakoala.R;

import java.io.IOException;

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
    private static final TimeInterpolator sAccelerator = new AccelerateInterpolator();
    private static final int ANIM_DURATION = 500;

    /* Container Views */
    protected FrameLayout mTopLevelLayout;

    /* Views */
    protected ImageView mImageView;

    /* Data */
    protected ColorVariant mColor;
    protected int mLeftDelta;
    protected int mTopDelta;
    protected String mBitmapUri;
    protected float mWidthScale;
    protected float mHeightScale;
    protected BitmapDrawable mBitmapDrawable;
    protected ColorDrawable mBackground;
    protected boolean mExiting;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Especificamos el layout 'product.xml'
        setContentView(R.layout.product);

        mImageView      = (ImageView)findViewById(R.id.imageView);
        mTopLevelLayout = (FrameLayout)findViewById(R.id.topLevelLayout);

        Bundle bundle = getIntent().getExtras();

        mExiting = false;

        final int thumbnailTop    = bundle.getInt(PACKAGE + ".top");
        final int thumbnailLeft   = bundle.getInt(PACKAGE + ".left");
        final int thumbnailWidth  = bundle.getInt(PACKAGE + ".width");
        final int thumbnailHeight = bundle.getInt(PACKAGE + ".height");
        mBitmapUri                = bundle.getString(PACKAGE + ".bitmap");
        mColor                    = (ColorVariant)bundle.getSerializable(PACKAGE + ".Beans.ColorVariant");

        try
        {
            mBitmapDrawable = new BitmapDrawable(getResources()
                                        , MediaStore.Images.Media.getBitmap(this.getContentResolver()
                                                                    , Uri.parse(mBitmapUri)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        mImageView.setImageDrawable(mBitmapDrawable);

        // Ponemos un fondo de pantalla para ir oscureciondola
        mBackground = new ColorDrawable(Color.BLACK);
        mTopLevelLayout.setBackground(mBackground);

        // Solo lo ejecutamos si venimos del activity padre
        if ( savedInstanceState == null )
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
                    mLeftDelta = thumbnailLeft - screenLocation[0];
                    mTopDelta = thumbnailTop - screenLocation[1];

                    // Factores de escala para saber cuanto hay que encojer o agrandar la imagen
                    mWidthScale = (float) thumbnailWidth / mImageView.getWidth();
                    mHeightScale = (float) thumbnailHeight / mImageView.getHeight();

                    runEnterAnimation();

                    return true;
                }
            });
        }
    }

    /**
     * La animacion de entrada escala la imagen desde la pequeña hasta la posicion/tamaño de la grande.
     * En paralelo, el fondo se va oscureciendo.
     */
    public void runEnterAnimation()
    {
        final long duration = ANIM_DURATION;

        // Establecemos las propiedades para las animaciones. Estos
        // valores escalan y desplazan la imagen grande a la posicion/tamaño de la pequeña.
        // Desde donde se va a iniciar la animacion.
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
                            .setInterpolator(sDecelerator);

        // Efecto fade para oscurecer la pantalla
        ObjectAnimator bgAnim = ObjectAnimator.ofInt(mBackground, "alpha", 0, 255);
        bgAnim.setDuration(duration);
        bgAnim.start();
    }

    /**
     * La animacion de salida es la misma animacion de entrada pero al reves
     *
     * @param endAction This action gets run after the animation completes (this is
     * when we actually switch activities)
     */
    public void runExitAnimation(final Runnable endAction)
    {
        mExiting = true;

        final long duration = ANIM_DURATION;

        mImageView.animate().setDuration(duration)
                            .scaleX(mWidthScale).scaleY(mHeightScale)
                            .translationX(mLeftDelta).translationY(mTopDelta)
                            .withEndAction(endAction);


        // Aclarar el fondo
        ObjectAnimator bgAnim = ObjectAnimator.ofInt(mBackground, "alpha", 0);
        bgAnim.setDuration(duration);
        bgAnim.start();
    }

    /**
     * Sobreescribir este metodo nos permite ejecutar la animacion de salida
     * y luego salir de la activity.
     */
    @Override
    public void onBackPressed()
    {
        if ( ! mExiting )
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
