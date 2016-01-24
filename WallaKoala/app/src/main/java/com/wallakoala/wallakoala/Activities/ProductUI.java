package com.wallakoala.wallakoala.Activities;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.wallakoala.wallakoala.Adapters.ProductsGridAdapter;
import com.wallakoala.wallakoala.Beans.ColorVariant;
import com.wallakoala.wallakoala.R;

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
    protected int mOriginalOrientation;
    protected float mWidthScale;
    protected float mHeightScale;
    protected BitmapDrawable mBitmapDrawable;
    protected ColorDrawable mBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Especificamos el layout 'product.xml'
        setContentView(R.layout.product);

        mImageView = (ImageView)findViewById(R.id.imageView);
        mTopLevelLayout = (FrameLayout)findViewById(R.id.topLevelLayout);

        Bundle bundle = getIntent().getExtras();

        final int thumbnailTop = bundle.getInt(PACKAGE + ".top");
        final int thumbnailLeft = bundle.getInt(PACKAGE + ".left");
        final int thumbnailWidth = bundle.getInt(PACKAGE + ".width");
        final int thumbnailHeight = bundle.getInt(PACKAGE + ".height");
        mOriginalOrientation = bundle.getInt(PACKAGE + ".orientation");
        mColor = (ColorVariant) bundle.getSerializable(PACKAGE + ".Beans.ColorVariant");

        mBitmapDrawable = new BitmapDrawable(getResources()
                                    , BitmapFactory.decodeResource(getResources(), R.drawable.producto));
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

                    // Figure out where the thumbnail and full size versions are, relative
                    // to the screen and each other
                    int[] screenLocation = new int[2];
                    mImageView.getLocationOnScreen(screenLocation);
                    mLeftDelta = thumbnailLeft - screenLocation[0];
                    mTopDelta = thumbnailTop - screenLocation[1];

                    // Scale factors to make the large version the same size as the thumbnail
                    mWidthScale = (float) thumbnailWidth / mImageView.getWidth();
                    mHeightScale = (float) thumbnailHeight / mImageView.getHeight();

                    runEnterAnimation();

                    return true;
                }
            });
        }
    }

    /**
     * The enter animation scales the picture in from its previous thumbnail
     * size/location. In parallel, the background of the activity is fading in.
     */
    public void runEnterAnimation()
    {
        final long duration = ANIM_DURATION * 2;

        // Set starting values for properties we're going to animate. These
        // values scale and position the full size version down to the thumbnail
        // size/location, from which we'll animate it back up
        mImageView.setPivotX(0);
        mImageView.setPivotY(0);
        mImageView.setScaleX(mWidthScale);
        mImageView.setScaleY(mHeightScale);
        mImageView.setTranslationX(mLeftDelta);
        mImageView.setTranslationY(mTopDelta);

        // Animate scale and translation to go from thumbnail to full size
        mImageView.animate().setDuration(duration).
                scaleX(1).scaleY(1).
                translationX(0).translationY(0).
                setInterpolator(sDecelerator).
                withEndAction(new Runnable() {
                    public void run() {
                        // Animate the description in after the image animation
                        // is done. Slide and fade the text in from underneath
                        // the picture.
                        //mTextView.setTranslationY(-mTextView.getHeight());
                        //mTextView.animate().setDuration(duration/2).
                        //translationY(0).alpha(1).
                        //setInterpolator(sDecelerator);
                    }
                });

        // Fade in the black background
        ObjectAnimator bgAnim = ObjectAnimator.ofInt(mBackground, "alpha", 0, 255);
        bgAnim.setDuration(duration);
        bgAnim.start();
    }

    /**
     * The exit animation is basically a reverse of the enter animation, except that if
     * the orientation has changed we simply scale the picture back into the center of
     * the screen.
     *
     * @param endAction This action gets run after the animation completes (this is
     * when we actually switch activities)
     */
    public void runExitAnimation(final Runnable endAction)
    {
        final long duration = (long) (ANIM_DURATION * 2);

        // No need to set initial values for the reverse animation; the image is at the
        // starting size/location that we want to start from. Just animate to the
        // thumbnail size/location that we retrieved earlier

        final boolean fadeOut;
        if (getResources().getConfiguration().orientation != mOriginalOrientation)
        {
            mImageView.setPivotX(mImageView.getWidth() / 2);
            mImageView.setPivotY(mImageView.getHeight() / 2);

            mLeftDelta = 0;
            mTopDelta = 0;

            fadeOut = true;

        } else {
            fadeOut = false;
        }

        mImageView.animate().setDuration(duration).
                scaleX(mWidthScale).scaleY(mHeightScale).
                translationX(mLeftDelta).translationY(mTopDelta).
                withEndAction(endAction);

        if (fadeOut)
            mImageView.animate().alpha(0);

        // Fade out background
        ObjectAnimator bgAnim = ObjectAnimator.ofInt(mBackground, "alpha", 0);
        bgAnim.setDuration(duration);
        bgAnim.start();
    }

    /**
     * Overriding this method allows us to run our exit animation first, then exiting
     * the activity when it is complete.
     */
    @Override
    public void onBackPressed()
    {
        runExitAnimation(new Runnable()
        {
            public void run()
            {
                finish();
            }
        });
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(0, 0);
    }
}
