package com.wallakoala.wallakoala.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.ImageButton;

import com.wallakoala.wallakoala.Properties.Properties;
import com.wallakoala.wallakoala.R;

/**
 * @class Pantalla de login que premite registrarse con Facebook, Google+, Instagram y un registro propio.
 * Created by Daniel Mancebo on 09/11/2015.
 */

public class SignUpUI extends AppCompatActivity
{
    /* Constants */
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int MIN_AGE = 10;
    private static final int MAX_AGE = 100;
    private static final int POSTAL_CODE_LENGHT = 5;
    private static final float INACTIVE_ALPHA = 0.5f;
    private static final float ACTIVE_ALPHA = 1.0f;
    private static boolean MALE_SELECTED;
    private static boolean FEMALE_SELECTED;
    private static boolean BACK_PRESSED;

    /* TextInputLayouts */
    private TextInputLayout mEmailInputLayout;
    private TextInputLayout mPasswordInputLayout;
    private TextInputLayout mAgeInputLayout;
    private TextInputLayout mPostalCodeInputLayout;

    /* EditTexts */
    private EditText mEmailEdittext;
    private EditText mPasswordEdittext;
    private EditText mAgeEdittext;
    private EditText mPostalCodeEdittext;

    /* ImageButtons */
    private ImageButton mMaleImageButton;
    private ImageButton mFemaleImageButton;

    /* FABs */
    private FloatingActionButton mAcceptFAB;
    private FloatingActionButton mBackFAB;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // Ocultamos el titulo de la app (debe ejecutarse antes del super.onCreate y de cargar el layout)
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.sign_up);

        _initEditTexts();
        _initFABs();
        _initImageButtons();
    }

    /**
     * Metodo que inicializa los EditTexts.
     */
    private void _initEditTexts()
    {
        mEmailInputLayout      = (TextInputLayout)findViewById(R.id.email_input_layout);
        mPasswordInputLayout = (TextInputLayout)findViewById(R.id.password_input_layout);
        mAgeInputLayout = (TextInputLayout)findViewById(R.id.age_input_layout);
        mPostalCodeInputLayout = (TextInputLayout)findViewById(R.id.postal_code_input_layout);

        mPasswordEdittext      = (EditText)findViewById(R.id.password_edittext);
        mEmailEdittext         = (EditText)findViewById(R.id.email_edittext);
        mAgeEdittext           = (EditText)findViewById(R.id.age_edittext);
        mPostalCodeEdittext    = (EditText)findViewById(R.id.postal_code_edittext);

        mPasswordEdittext.addTextChangedListener(new MyTextWatcher(mPasswordEdittext));
        mEmailEdittext.addTextChangedListener(new MyTextWatcher(mEmailEdittext));
        mAgeEdittext.addTextChangedListener(new MyTextWatcher(mAgeEdittext));
        mPostalCodeEdittext.addTextChangedListener(new MyTextWatcher(mPostalCodeEdittext));
    }

    /**
     * Metodo que inicializa los FABs.
     */
    private void _initFABs()
    {
        BACK_PRESSED = false;

        mAcceptFAB = (FloatingActionButton)findViewById(R.id.done_sign_up);
        mBackFAB   = (FloatingActionButton)findViewById(R.id.back_sign_up);

        mAcceptFAB.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //if (_validateEmail() && _validatePassword() && _validateAge() && _validatePostalCode())
                //{
                    Intent intent = new Intent(SignUpUI.this, MainScreenUI.class);
                    startActivity(intent);

                    // Animacion de transicion para pasar de una activity a otra.
                    overridePendingTransition(R.anim.right_in, R.anim.right_out);

                    finish();
                //}
            }
        });

        mBackFAB.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                BACK_PRESSED = true;

                finish();
            }
        });
    }

    /**
     * Metodo que inicializa los ImageButtons.
     */
    private void _initImageButtons()
    {
        MALE_SELECTED   = false;
        FEMALE_SELECTED = false;

        mMaleImageButton   = (ImageButton)findViewById(R.id.male_icon);
        mFemaleImageButton = (ImageButton)findViewById(R.id.female_icon);

        mMaleImageButton.setScaleX(0.75f);
        mMaleImageButton.setScaleY(0.75f);
        mMaleImageButton.setAlpha(INACTIVE_ALPHA);

        mFemaleImageButton.setScaleX(0.75f);
        mFemaleImageButton.setScaleY(0.75f);
        mFemaleImageButton.setAlpha(INACTIVE_ALPHA);

        mMaleImageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                MALE_SELECTED = !MALE_SELECTED;

                if (mMaleImageButton.getAlpha() == ACTIVE_ALPHA)
                {
                    mMaleImageButton.animate().setDuration(250)
                                              .scaleX(0.75f)
                                              .scaleY(0.75f)
                                              .alpha(INACTIVE_ALPHA)
                                              .setInterpolator(new OvershootInterpolator());

                } else {
                    mMaleImageButton.animate().setDuration(250)
                                              .scaleX(1.0f)
                                              .scaleY(1.0f)
                                              .alpha(ACTIVE_ALPHA)
                                              .setInterpolator(new OvershootInterpolator());

                    if (FEMALE_SELECTED)
                    {
                        FEMALE_SELECTED = !FEMALE_SELECTED;

                        mFemaleImageButton.animate().setDuration(250)
                                                    .scaleX(0.75f)
                                                    .scaleY(0.75f)
                                                    .alpha(INACTIVE_ALPHA)
                                                    .setInterpolator(new OvershootInterpolator());

                    }
                }
            }
        });

        mFemaleImageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                FEMALE_SELECTED = !FEMALE_SELECTED;

                if (mFemaleImageButton.getAlpha() == ACTIVE_ALPHA)
                {
                    mFemaleImageButton.animate().setDuration(250)
                                                .scaleX(0.75f)
                                                .scaleY(0.75f)
                                                .alpha(INACTIVE_ALPHA)
                                                .setInterpolator(new OvershootInterpolator());

                } else {
                    mFemaleImageButton.animate().setDuration(250)
                                                .scaleX(1.0f)
                                                .scaleY(1.0f)
                                                .alpha(ACTIVE_ALPHA)
                                                .setInterpolator(new OvershootInterpolator());

                    if (MALE_SELECTED)
                    {
                        MALE_SELECTED = !MALE_SELECTED;

                        mMaleImageButton.animate().setDuration(250)
                                                  .scaleX(0.75f)
                                                  .scaleY(0.75f)
                                                  .alpha(INACTIVE_ALPHA)
                                                  .setInterpolator(new OvershootInterpolator());

                    }
                }
            }
        });
    }

    @Override
    public void finish()
    {
        super.finish();

        if (BACK_PRESSED)
        {
            overridePendingTransition(R.anim.left_in, R.anim.left_out);
        }
    }

    /**
     * Metodo que valida el email y muestra un error si es necesario.
     * @return true si el email es correcto.
     */
    private boolean _validateEmail()
    {
        String email = mEmailEdittext.getText().toString().trim();

        if (!_isValidEmail(email))
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
        if (mPasswordEdittext.getText().toString().trim().isEmpty() ||
                mPasswordEdittext.getText().toString().trim().length() < MIN_PASSWORD_LENGTH)
        {
            mPasswordInputLayout.setErrorEnabled(true);
            mPasswordInputLayout.setError("Contraseña incorrecta (6 caracteres min)");

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

        if (age.isEmpty() || !_isValidAge(age))
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

        if (!_isValidPostalCode(postalCode))
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
     * Metodo que comprueba si el email es correcto.
     * @param email: email a comprobar.
     * @return true si el email es correcto.
     */
    private static boolean _isValidEmail(String email)
    {
        return (!TextUtils.isEmpty(email) &&
                            android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    /**
     * Metodo que comprueba si la edad es correcta.
     * @param age: edad a comprobar.
     * @return true si la edad es correcta.
     */
    private static boolean _isValidAge(String age)
    {
        return (Integer.valueOf(age) >= MIN_AGE && Integer.valueOf(age) <= MAX_AGE);
    }

    /**
     * Metodo que comprueba si el CP es correcto.
     * @param postalCode: CP a comprobar.
     * @return true si el CP es correcto.
     */
    private static boolean _isValidPostalCode(String postalCode)
    {
        return (postalCode.length() == POSTAL_CODE_LENGHT);
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
                case R.id.email_edittext:
                    _validateEmail();
                    break;

                case R.id.password_edittext:
                    _validatePassword();
                    break;

                case R.id.age_edittext:
                    _validateAge();
                    break;

                case R.id.postal_code_edittext:
                    _validatePostalCode();
                    break;
            }
        }
    }

    /**
     * Metodo que pone el foco en la vista.
     * @param view: vista a la que se quiere poner el foco.
     */
    private void _requestFocus(View view)
    {
        if (view.requestFocus())
        {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}
