package com.wallakoala.wallakoala.Activities;

import android.animation.TimeInterpolator;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.TextView;

import com.wallakoala.wallakoala.Beans.User;
import com.wallakoala.wallakoala.Properties.Properties;
import com.wallakoala.wallakoala.R;
import com.wallakoala.wallakoala.Utils.SharedPreferencesManager;
import com.wallakoala.wallakoala.Utils.Utils;

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

    /* TextInputLayouts */
    private TextInputLayout mEmailInputLayout;
    private TextInputLayout mPasswordInputLayout;
    private TextInputLayout mAgeInputLayout;
    private TextInputLayout mPostalCodeInputLayout;
    private TextInputLayout mNameInputLayout;

    /* EditTexts */
    private EditText mEmailEdittext;
    private EditText mPasswordEdittext;
    private EditText mAgeEdittext;
    private EditText mPostalCodeEdittext;
    private EditText mNameEdittext;

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
        _initEditTexts();

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
     * Metodo que inicializa la Toolbar.
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

    /**
     * Metodo que inicializa los EditTexts.
     */
    private void _initEditTexts()
    {
        mEmailInputLayout      = (TextInputLayout)findViewById(R.id.profile_email_input);
        mPasswordInputLayout   = (TextInputLayout)findViewById(R.id.profile_password_input);
        mAgeInputLayout        = (TextInputLayout)findViewById(R.id.profile_age_input);
        mPostalCodeInputLayout = (TextInputLayout)findViewById(R.id.profile_cp_input);
        mNameInputLayout       = (TextInputLayout)findViewById(R.id.profile_name_input);

        mPasswordEdittext   = (EditText)findViewById(R.id.profile_password_edittext);
        mEmailEdittext      = (EditText)findViewById(R.id.profile_email_edittext);
        mAgeEdittext        = (EditText)findViewById(R.id.profile_age_edittext);
        mPostalCodeEdittext = (EditText)findViewById(R.id.profile_cp_edittext);
        mNameEdittext       = (EditText)findViewById(R.id.profile_name_edittext);

        mPasswordEdittext.addTextChangedListener(new MyTextWatcher(mPasswordEdittext));
        mEmailEdittext.addTextChangedListener(new MyTextWatcher(mEmailEdittext));
        mAgeEdittext.addTextChangedListener(new MyTextWatcher(mAgeEdittext));
        mPostalCodeEdittext.addTextChangedListener(new MyTextWatcher(mPostalCodeEdittext));
        mNameEdittext.addTextChangedListener(new MyTextWatcher(mNameEdittext));
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

    /**
     * Metodo que valida el email y muestra un error si es necesario.
     * @return true si el email es correcto.
     */
    private boolean _validateEmail()
    {
        String email = mEmailEdittext.getText().toString().trim();

        if (!email.isEmpty() && (!Utils.isValidEmail(email)))
        {
            mEmailInputLayout.setErrorEnabled(true);
            mEmailInputLayout.setError("Email incorrecto");

            _requestFocus(mEmailEdittext);

            return false;

        } else {
            mEmailInputLayout.setError(null);
            mEmailInputLayout.setErrorEnabled(false);
        }

        return true;
    }

    /**
     * Metodo que valida la contraseña y muestra un error si es necesario.
     * @return true si la contraseña es correcta.
     */
    private boolean _validatePassword()
    {
        String password = mPasswordEdittext.getText().toString();

        if (!password.isEmpty() && (!Utils.isValidPassword(password)))
        {
            mPasswordInputLayout.setErrorEnabled(true);
            mPasswordInputLayout.setError("Mínimo 6 caracteres");

            _requestFocus(mPasswordEdittext);

            return false;

        } else {
            mPasswordInputLayout.setError(null);
            mPasswordInputLayout.setErrorEnabled(false);
        }

        return true;
    }

    /**
     * Metodo que valida la edad y muestra un error si es necesario.
     * @return true si la edad es correcta.
     */
    private boolean _validateAge()
    {
        String age = mAgeEdittext.getText().toString();

        if (!age.isEmpty() && (!Utils.isValidAge(age)))
        {
            mAgeInputLayout.setErrorEnabled(true);
            mAgeInputLayout.setError("Edad incorrecta");

            _requestFocus(mAgeEdittext);

            return false;

        } else {
            mAgeInputLayout.setError(null);
            mAgeInputLayout.setErrorEnabled(false);
        }

        return true;
    }

    /**
     * Metodo que valida el CP y muestra un error si es necesario.
     * @return true si el CP es correcto.
     */
    private boolean _validatePostalCode()
    {
        String postalCode = mPostalCodeEdittext.getText().toString();

        if (!postalCode.isEmpty() && (!Utils.isValidPostalCode(postalCode)))
        {
            mPostalCodeInputLayout.setErrorEnabled(true);
            mPostalCodeInputLayout.setError("Código postal incorrecto");

            _requestFocus(mPostalCodeEdittext);

            return false;

        } else {
            mPostalCodeInputLayout.setError(null);
            mPostalCodeInputLayout.setErrorEnabled(false);
        }

        return true;
    }

    /**
     * Metodo que valida el nombre y muestra un error si es necesario.
     * @return true si el nombre es correcto.
     */
    private boolean _validateName()
    {
        String name = mNameEdittext.getText().toString();

        if (!name.isEmpty() && (!Utils.isValidName(name)))
        {
            mNameInputLayout.setErrorEnabled(true);
            mNameInputLayout.setError("Nombre incorrecto");

            _requestFocus(mNameEdittext);

            return false;

        } else {
            mNameInputLayout.setError(null);
            mNameInputLayout.setErrorEnabled(false);
        }

        return true;
    }

    /**
     * TextWatcher propio para validar todos los campos.
     */
    private class MyTextWatcher implements TextWatcher
    {
        private View view;

        private MyTextWatcher(View view)
        {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        public void afterTextChanged(Editable editable)
        {
            switch (view.getId())
            {
                case R.id.profile_email_edittext:
                    _validateEmail();
                    break;

                case R.id.profile_password_edittext:
                    _validatePassword();
                    break;

                case R.id.profile_age_edittext:
                    _validateAge();
                    break;

                case R.id.profile_cp_edittext:
                    _validatePostalCode();
                    break;
                case R.id.profile_name_edittext:
                    _validateName();
                    break;
            }
        }
    }

    /**
     * Metodo que pone el foco en la vista.
     * @param view: vista a la que se quiere poner el foco.
     */
    private void _requestFocus(final View view)
    {
        if (view.requestFocus())
        {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}
