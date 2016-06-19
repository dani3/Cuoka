package com.wallakoala.wallakoala.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.dd.CircularProgressButton;
import com.wallakoala.wallakoala.Properties.Properties;
import com.wallakoala.wallakoala.R;
import com.wallakoala.wallakoala.Singletons.VolleySingleton;
import com.wallakoala.wallakoala.Utils.SharedPreferencesManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @class Pantalla para logearse de distinta formas.
 * Created by Daniel Mancebo on 18/06/2016.
 */

public class LoginUI extends AppCompatActivity
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

    /* SharedPreferences */
    protected SharedPreferencesManager mSharedPreferencesManager;

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

    /* Morphing Button */
    private CircularProgressButton mRegisterCircularButton;

    /* ImageButtons */
    private ImageButton mMaleImageButton;
    private ImageButton mFemaleImageButton;

    /* Buttons */
    private Button mSignInButton;
    private Button mSingUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login);

        mSharedPreferencesManager = new SharedPreferencesManager(this);

        _initButtons();
    }

    private void _initButtons()
    {
        mSignInButton = (Button)findViewById(R.id.sign_in);
        mSingUpButton = (Button)findViewById(R.id.sign_up);

        mSingUpButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AlertDialog alertDialog = createDialogSignUp();

                alertDialog.show();
            }
        });
    }

    private AlertDialog createDialogSignUp()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();

        View v = inflater.inflate(R.layout.sign_up, null);

        builder.setView(v);

        _initEditTexts(v);
        _initImageButtons(v);
        _initCircularButton(v);

        return builder.create();
    }

    /**
     * Metodo que inicializa los EditTexts.
     */
    private void _initEditTexts(View parent)
    {
        mEmailInputLayout      = (TextInputLayout)parent.findViewById(R.id.email_input_layout);
        mPasswordInputLayout   = (TextInputLayout)parent.findViewById(R.id.password_input_layout);
        mAgeInputLayout        = (TextInputLayout)parent.findViewById(R.id.age_input_layout);
        mPostalCodeInputLayout = (TextInputLayout)parent.findViewById(R.id.postal_code_input_layout);

        mPasswordEdittext      = (EditText)parent.findViewById(R.id.password_edittext);
        mEmailEdittext         = (EditText)parent.findViewById(R.id.email_edittext);
        mAgeEdittext           = (EditText)parent.findViewById(R.id.age_edittext);
        mPostalCodeEdittext    = (EditText)parent.findViewById(R.id.postal_code_edittext);

        mPasswordEdittext.addTextChangedListener(new MyTextWatcher(mPasswordEdittext));
        mEmailEdittext.addTextChangedListener(new MyTextWatcher(mEmailEdittext));
        mAgeEdittext.addTextChangedListener(new MyTextWatcher(mAgeEdittext));
        mPostalCodeEdittext.addTextChangedListener(new MyTextWatcher(mPostalCodeEdittext));
    }

    /**
     * Metodo que inicializa los ImageButtons.
     */
    private void _initImageButtons(View parent)
    {
        MALE_SELECTED   = false;
        FEMALE_SELECTED = false;

        mMaleImageButton   = (ImageButton)parent.findViewById(R.id.male_icon);
        mFemaleImageButton = (ImageButton)parent.findViewById(R.id.female_icon);

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

    /**
     * Metodo que inicializa el CircularButton.
     */
    private void _initCircularButton(View parent)
    {
        mRegisterCircularButton = (CircularProgressButton)parent.findViewById(R.id.sign_up_accept);
        mRegisterCircularButton.setIndeterminateProgressMode(true);

        mRegisterCircularButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mRegisterCircularButton.getProgress() == 100)
                {
                    Intent intent = new Intent(LoginUI.this, MainScreenUI.class);

                    // Limpiamos la Activity Stack
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    startActivity(intent);

                    finish();
                }

                // Validamos los datos introducidos y comprobamos que no estemos ya cargando.
                if (mRegisterCircularButton.getProgress() != 50 && mRegisterCircularButton.getProgress() != 100 &&
                        _validateEmail() && _validatePassword() && _validateAge() && _validatePostalCode() && _isGenderSelected())
                {
                    try
                    {
                        // 0 < X < 100 -> Cargando
                        mRegisterCircularButton.setProgress(50);

                        final String fixedURL = Properties.SERVER_URL + ":" + Properties.SERVER_SPRING_PORT + "/users";

                        Log.d(Properties.TAG, "Conectando con: " + fixedURL + " para registrar un usuario");

                        // Creamos el JSON con los datos del usuario
                        final JSONObject jsonObject = new JSONObject();

                        jsonObject.put("age", Short.valueOf(mAgeEdittext.getText().toString()));
                        jsonObject.put("email", mEmailEdittext.getText().toString());
                        jsonObject.put("password", mPasswordEdittext.getText().toString());
                        jsonObject.put("man", (mMaleImageButton.getAlpha() == ACTIVE_ALPHA));
                        jsonObject.put("postalCode", Integer.valueOf(mPostalCodeEdittext.getText().toString()));

                        Log.d(Properties.TAG, "JSON con el usuario:\n    " + jsonObject.toString());

                        StringRequest stringRequest = new StringRequest(Request.Method.POST
                                , fixedURL
                                , new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response)
                            {
                                Log.d(Properties.TAG, "Respueste del servidor: " + response);

                                if (response.equals(Properties.REGISTRATION_OK))
                                {
                                    // Si ha ido bien, avanzamos a la siguiente pantalla.
                                    Log.d(Properties.TAG, "Usuario registrado correctamente");

                                    // X = 100 -> Complete
                                    mRegisterCircularButton.setProgress(100);

                                    // Actualizamos el fichero de SharedPreferences.
                                    _updateSharedPreferences();

                                } else if (response.equals(Properties.ALREADY_EXISTS)) {
                                    // X < 0 -> Error
                                    mRegisterCircularButton.setProgress(0);
                                }
                            }
                        }
                                , new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error)
                            {
                                mRegisterCircularButton.setProgress(-1);

                                Log.d(Properties.TAG, "Error registrando usuario: " + error.getMessage());

                                error.printStackTrace();
                            }
                        })
                        {
                            @Override
                            public byte[] getBody() throws AuthFailureError
                            {
                                return jsonObject.toString().getBytes();
                            }

                            @Override
                            public String getBodyContentType()
                            {
                                return "application/json";
                            }
                        };

                        // Enviamos la peticion.
                        VolleySingleton.getInstance(LoginUI.this).addToRequestQueue(stringRequest);

                    } catch (JSONException e) {
                        Log.d(Properties.TAG, "Error creando JSON (" + e.getMessage() + ")");
                    }
                }
            }
        });
    }

    /**
     * Metodo que actualiza el fichero de SharedPreferences.
     */
    private void _updateSharedPreferences()
    {
        boolean man     = (mMaleImageButton.getAlpha() == ACTIVE_ALPHA);
        int age         = Integer.valueOf(mAgeEdittext.getText().toString());
        int postalCode  = Integer.valueOf(mPostalCodeEdittext.getText().toString());
        String email    = mEmailEdittext.getText().toString();
        String password = mPasswordEdittext.getText().toString();

        mSharedPreferencesManager.insertMan(man);
        mSharedPreferencesManager.insertAge(age);
        mSharedPreferencesManager.insertPostalCode(postalCode);
        mSharedPreferencesManager.insertEmai(email);
        mSharedPreferencesManager.insertPassword(password);

        mSharedPreferencesManager.insertOwnRegister(true);
        mSharedPreferencesManager.insertLoggedIn(true);
    }

    /**
     * Metodo que comprueba si se ha seleccionado algun sexo.
     * @return true si se ha seleccionado algun sexo.
     */
    private boolean _isGenderSelected()
    {

        return true;
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
