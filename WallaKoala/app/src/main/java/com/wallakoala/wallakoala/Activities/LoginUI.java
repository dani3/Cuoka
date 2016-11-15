package com.wallakoala.wallakoala.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.dd.CircularProgressButton;
import com.wallakoala.wallakoala.Beans.User;
import com.wallakoala.wallakoala.Properties.Properties;
import com.wallakoala.wallakoala.R;
import com.wallakoala.wallakoala.Singletons.TypeFaceSingleton;
import com.wallakoala.wallakoala.Singletons.VolleySingleton;
import com.wallakoala.wallakoala.Utils.SharedPreferencesManager;
import com.wallakoala.wallakoala.Utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;

/**
 * Pantalla para logearse de distinta formas.
 * Created by Daniel Mancebo on 18/06/2016.
 */

public class LoginUI extends AppCompatActivity
{
    /* Constants */
    private static final float INACTIVE_ALPHA = 0.5f;
    private static final float ACTIVE_ALPHA = 1.0f;
    private static boolean MALE_SELECTED;
    private static boolean FEMALE_SELECTED;

    /* SharedPreferences */
    private SharedPreferencesManager mSharedPreferencesManager;

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

    /* Buttons */
    private CircularProgressButton mRegisterCircularButton;
    private ImageButton mMaleImageButton;
    private ImageButton mFemaleImageButton;

    /* CheckBoxes */
    private AppCompatCheckBox mRememberMeCheckBox;

    /* AlertDialog */
    private AlertDialog mAlertDialog;

    /* AlertDialog View */
    private View mAlertDialogView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        mSharedPreferencesManager = new SharedPreferencesManager(this);

        _initButtons();
        _animateBackground();
    }

    /**
     * Metodo que aplica una animacion al fondo.
     */
    private void _animateBackground()
    {
        Animation zoomInAnimation = AnimationUtils.loadAnimation(this, R.anim.zoom_in_animation);

        View backgroundImage = findViewById(R.id.login_background);

        backgroundImage.startAnimation(zoomInAnimation);
    }

    /**
     * Metodo para inicializar todos los botones.
     */
    private void _initButtons()
    {
        final Button mSignInButton = (Button) findViewById(R.id.sign_in);
        final Button mSingUpButton = (Button) findViewById(R.id.sign_up);

        mSignInButton.setTypeface(TypeFaceSingleton.getTypeFace(this, "Existence-StencilLight.otf"));
        mSingUpButton.setTypeface(TypeFaceSingleton.getTypeFace(this, "Existence-StencilLight.otf"));

        mSingUpButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mAlertDialog = createDialogSignUp();

                mAlertDialog.show();
            }
        });

        mSignInButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mAlertDialog = createDialogSignIn();

                mAlertDialog.show();
            }
        });
    }

    /**
     * Metodo que crea un dialogo para registrarse.
     * @return AlertDialog
     */
    @SuppressLint("InflateParams")
    private AlertDialog createDialogSignUp()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();

        mAlertDialogView = inflater.inflate(R.layout.dialog_sign_up, null);

        builder.setView(mAlertDialogView);

        _initSignUpEditTexts(mAlertDialogView);
        _initSignUpImageButtons(mAlertDialogView);
        _initSignUpMorphingButton(mAlertDialogView);

        return builder.create();
    }

    /**
     * Metodo que crea un dialogo para iniciar sesion.
     * @return AlertDialog
     */
    @SuppressLint("InflateParams")
    private AlertDialog createDialogSignIn()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();

        mAlertDialogView = inflater.inflate(R.layout.dialog_sign_in, null);

        builder.setView(mAlertDialogView);

        _initSignInButtons(mAlertDialogView);
        _initSignInEditTexts(mAlertDialogView);
        _initSignInViews(mAlertDialogView);

        return builder.create();
    }

    /**
     * Metodo que inicializa los botones de la ventana de inicio de sesion.
     * @param parent: vista padre para inflar los controles.
     */
    private void _initSignInButtons(View parent)
    {
        final Button mCreateAccountButton         = (Button) parent.findViewById(R.id.create_account);
        final CircularProgressButton mEnterButton = (CircularProgressButton) parent.findViewById(R.id.enter);

        mEnterButton.setIndeterminateProgressMode(true);

        mCreateAccountButton.setTypeface(TypeFaceSingleton.getTypeFace(this, "Existence-StencilLight.otf"));
        mEnterButton.setTypeface(TypeFaceSingleton.getTypeFace(this, "Existence-StencilLight.otf"));

        mCreateAccountButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Se permite cambiar de pantalla siempre que no se este cargando.
                if (mEnterButton.getProgress() == 0)
                {
                    mAlertDialog.dismiss();

                    mAlertDialog = createDialogSignUp();

                    mAlertDialog.show();
                }
            }
        });

        mEnterButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (_validateEmail() && _validatePassword() && (mEnterButton.getProgress() == 0))
                {
                    // Guardamos el estado de la pantalla por si acaso el usuario lo cambia mientras se loguea.
                    final boolean rememberMe = mRememberMeCheckBox.isChecked();
                    final String email = mEmailEdittext.getText().toString();
                    final String password = mPasswordEdittext.getText().toString();

                    mEnterButton.setProgress(50);

                    final String fixedURL = Utils.fixUrl(Properties.SERVER_URL + ":" + Properties.SERVER_SPRING_PORT
                            + "/users" + "/" + email + "/" + password);

                    Log.d(Properties.TAG, "Conectando con: " + fixedURL + " para logear un usuario");

                    StringRequest stringRequest = new StringRequest(Request.Method.GET
                            , fixedURL
                            , new Response.Listener<String>()
                            {
                                @Override
                                public void onResponse(String response)
                                {
                                    Log.d(Properties.TAG, "Respuesta del servidor: " + response);

                                    if (response.equals(Properties.INCORRECT_LOGIN))
                                    {
                                        mEnterButton.setProgress(0);

                                        Snackbar.make(mAlertDialogView
                                                , "Email y/o contraseña incorectos"
                                                , Snackbar.LENGTH_LONG).show();

                                    } else {
                                        final long id = Long.valueOf(response);

                                        Log.d(Properties.TAG, "Usuario logueado correctamente (ID: " + id + ")");

                                        _getUserInfo(id, mEnterButton, rememberMe);
                                    }

                                }
                            }
                            , new Response.ErrorListener()
                            {
                                @Override
                                public void onErrorResponse(VolleyError error)
                                {
                                    mEnterButton.setProgress(0);

                                    Log.d(Properties.TAG, "Error logeando usuario: " + error.getMessage());

                                    error.printStackTrace();

                                    Snackbar.make(mAlertDialogView, "Ops! Algo ha ido mal", Snackbar.LENGTH_INDEFINITE)
                                            .setAction("Reintentar", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v)
                                                {
                                                    mEnterButton.setProgress(0);
                                                    mEnterButton.performClick();
                                                }
                                            }).show();
                                }
                            });

                    VolleySingleton.getInstance(LoginUI.this).addToRequestQueue(stringRequest);
                }
            }
        });
    }

    /**
     * Metodo que inicializa los EditTexts de la ventana de inicio de sesion.
     * @param parent: vista padre para inflar los controles.
     */
    private void _initSignInEditTexts(View parent)
    {
        mEmailInputLayout    = (TextInputLayout)parent.findViewById(R.id.email_input_layout);
        mPasswordInputLayout = (TextInputLayout)parent.findViewById(R.id.password_input_layout);

        mEmailInputLayout.setTypeface(TypeFaceSingleton.getTypeFace(this, "Existence-StencilLight.otf"));
        mPasswordInputLayout.setTypeface(TypeFaceSingleton.getTypeFace(this, "Existence-StencilLight.otf"));

        mPasswordEdittext = (EditText)parent.findViewById(R.id.password_edittext);
        mEmailEdittext    = (EditText)parent.findViewById(R.id.email_edittext);

        mPasswordEdittext.addTextChangedListener(new MyTextWatcher(mPasswordEdittext));
        mEmailEdittext.addTextChangedListener(new MyTextWatcher(mEmailEdittext));
    }

    /**
     * Metodo que inicializa el resto de controles de la ventana de registro.
     * @param parent: vista padre para inflar los controles.
     */
    private void _initSignInViews(View parent)
    {
        mRememberMeCheckBox = (AppCompatCheckBox)parent.findViewById(R.id.remember_me_checkbox);
        mRememberMeCheckBox.setChecked(true);

        mRememberMeCheckBox.setTypeface(TypeFaceSingleton.getTypeFace(this, "Existence-StencilLight.otf"));

        TextView mPasswordForgottenTextView = (TextView)parent.findViewById(R.id.password_forgotten);
        mPasswordForgottenTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

            }
        });
    }

    /**
     * Metodo que inicializa los EditTexts de la ventana de registro.
     * @param parent: vista padre para inflar los controles.
     */
    private void _initSignUpEditTexts(View parent)
    {
        mEmailInputLayout      = (TextInputLayout)parent.findViewById(R.id.email_input_layout);
        mPasswordInputLayout   = (TextInputLayout)parent.findViewById(R.id.password_input_layout);
        mAgeInputLayout        = (TextInputLayout)parent.findViewById(R.id.age_input_layout);
        mPostalCodeInputLayout = (TextInputLayout)parent.findViewById(R.id.postal_code_input_layout);
        mNameInputLayout       = (TextInputLayout)parent.findViewById(R.id.name_input_layout);

        mEmailInputLayout.setTypeface(TypeFaceSingleton.getTypeFace(this, "Existence-StencilLight.otf"));
        mPasswordInputLayout.setTypeface(TypeFaceSingleton.getTypeFace(this, "Existence-StencilLight.otf"));
        mAgeInputLayout.setTypeface(TypeFaceSingleton.getTypeFace(this, "Existence-StencilLight.otf"));
        mPostalCodeInputLayout.setTypeface(TypeFaceSingleton.getTypeFace(this, "Existence-StencilLight.otf"));
        mNameInputLayout.setTypeface(TypeFaceSingleton.getTypeFace(this, "Existence-StencilLight.otf"));

        mPasswordEdittext   = (EditText)parent.findViewById(R.id.password_edittext);
        mEmailEdittext      = (EditText)parent.findViewById(R.id.email_edittext);
        mAgeEdittext        = (EditText)parent.findViewById(R.id.age_edittext);
        mPostalCodeEdittext = (EditText)parent.findViewById(R.id.postal_code_edittext);
        mNameEdittext       = (EditText)parent.findViewById(R.id.name_edittext);

        mPasswordEdittext.addTextChangedListener(new MyTextWatcher(mPasswordEdittext));
        mEmailEdittext.addTextChangedListener(new MyTextWatcher(mEmailEdittext));
        mAgeEdittext.addTextChangedListener(new MyTextWatcher(mAgeEdittext));
        mPostalCodeEdittext.addTextChangedListener(new MyTextWatcher(mPostalCodeEdittext));
        mNameEdittext.addTextChangedListener(new MyTextWatcher(mNameEdittext));
    }

    /**
     * Metodo que inicializa los ImageButtons de la ventana de registro.
     * @param parent: vista padre para inflar los controles.
     */
    private void _initSignUpImageButtons(View parent)
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
     * Metodo que inicializa el CircularButton de la ventana de registro.
     * @param parent: vista padre para inflar los controles.
     */
    private void _initSignUpMorphingButton(View parent)
    {
        mRegisterCircularButton = (CircularProgressButton)parent.findViewById(R.id.sign_up_accept);
        mRegisterCircularButton.setIndeterminateProgressMode(true);

        mRegisterCircularButton.setTypeface(TypeFaceSingleton.getTypeFace(this, "Existence-StencilLight.otf"));

        mRegisterCircularButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Si se hace click cuando se ha completado el registro se llama a la siguiente pantalla
                if (mRegisterCircularButton.getProgress() == 100)
                {
                    Intent intent = new Intent(LoginUI.this, MainScreenUI.class);

                    startActivity(intent);

                    finish();

                    // Animacion de transicion para pasar de una activity a otra.
                    overridePendingTransition(R.anim.right_in_animation, R.anim.right_out_animation);
                }

                // Validamos los datos introducidos y comprobamos que no estemos ya cargando.
                if (mRegisterCircularButton.getProgress() != 50 && mRegisterCircularButton.getProgress() != 100 &&
                    _validateEmail() && _validatePassword() && _validateAge() && _validatePostalCode() && _validateName() && _isGenderSelected())
                {
                    try
                    {
                        // 0 < X < 100 -> Cargando
                        mRegisterCircularButton.setProgress(50);

                        final String fixedURL = Utils.fixUrl(
                                Properties.SERVER_URL + ":" + Properties.SERVER_SPRING_PORT + "/users");

                        Log.d(Properties.TAG, "Conectando con: " + fixedURL + " para registrar un usuario");

                        // Creamos el JSON con los datos del usuario
                        final JSONObject jsonObject = new JSONObject();

                        jsonObject.put("name", mNameEdittext.getText().toString());
                        jsonObject.put("age", Short.valueOf(mAgeEdittext.getText().toString()));
                        jsonObject.put("email", mEmailEdittext.getText().toString());
                        jsonObject.put("password", mPasswordEdittext.getText().toString());
                        jsonObject.put("man", (mMaleImageButton.getAlpha() == ACTIVE_ALPHA));
                        jsonObject.put("postalCode", Integer.valueOf(mPostalCodeEdittext.getText().toString()));

                        Log.d(Properties.TAG, "JSON con el usuario:\n    " + jsonObject.toString());

                        StringRequest stringRequest = new StringRequest(Request.Method.POST
                                , fixedURL
                                , new Response.Listener<String>()
                                {
                                    @Override
                                    public void onResponse(String response)
                                    {
                                        Log.d(Properties.TAG, "Respuesta del servidor: " + response);

                                        if (response.equals(Properties.ALREADY_EXISTS))
                                        {
                                            // X = 0 -> Idle
                                            mRegisterCircularButton.setProgress(0);

                                            Snackbar.make(mAlertDialogView, "Email ya registrado", Snackbar.LENGTH_LONG).show();

                                        } else {
                                            // Si ha ido bien, actualizamos las preferencias.
                                            long id = Long.valueOf(response);

                                            Log.d(Properties.TAG, "Usuario registrado correctamente (ID: " + id + ")");

                                            // X = 100 -> Complete
                                            mRegisterCircularButton.setProgress(100);

                                            // Actualizamos el fichero de SharedPreferences.
                                            boolean man     = (mMaleImageButton.getAlpha() == ACTIVE_ALPHA);
                                            int age         = Integer.valueOf(mAgeEdittext.getText().toString());
                                            int postalCode  = Integer.valueOf(mPostalCodeEdittext.getText().toString());
                                            String email    = mEmailEdittext.getText().toString();
                                            String password = mPasswordEdittext.getText().toString();
                                            String name     = mNameEdittext.getText().toString();

                                            User user = new User();

                                            user.setName(name);
                                            user.setId(id);
                                            user.setPostalCode(postalCode);
                                            user.setMan(man);
                                            user.setPassword(password);
                                            user.setAge(age);
                                            user.setEmail(email);
                                            user.setFavoriteProducts(new HashSet<Long>());
                                            user.setShops(new HashSet<String>());

                                            mSharedPreferencesManager.insertUser(user);
                                            mSharedPreferencesManager.insertLoggedIn(true);
                                        }
                                    }
                                }
                                , new Response.ErrorListener()
                                {
                                    @Override
                                    public void onErrorResponse(VolleyError error)
                                    {
                                        // X = -1 -> Error
                                        mRegisterCircularButton.setProgress(-1);

                                        Log.d(Properties.TAG, "Error registrando usuario: " + error.getMessage());

                                        error.printStackTrace();

                                        Snackbar.make(mAlertDialogView, "Ops! Algo ha ido mal", Snackbar.LENGTH_INDEFINITE)
                                                .setAction("Reintentar", new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v)
                                                    {
                                                        mRegisterCircularButton.setProgress(0);
                                                        mRegisterCircularButton.performClick();
                                                    }
                                                }).show();
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
     * Metodo que obtiene la info del usuario.
     * @param id: id del usuario.
     * @param enterButton: boton para cambiar el estado.
     * @param rememberMe: check de recuerdame.
     */
    private void _getUserInfo(final long id, final CircularProgressButton enterButton, final boolean rememberMe)
    {
        final String fixedURL = Utils.fixUrl(
                Properties.SERVER_URL + ":" + Properties.SERVER_SPRING_PORT + "/users/" + id);

        Log.d(Properties.TAG, "Conectando con: " + fixedURL + " para obtener los datos del usuario");

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET
                , fixedURL
                , null
                , new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try
                        {
                            User user = Utils.getUserFromJSON(id, response);

                            mSharedPreferencesManager.insertUser(user);
                            mSharedPreferencesManager.insertLoggedIn(rememberMe);

                            // Avanzamos automaticamente a la siguiente pantalla.
                            Intent intent = new Intent(LoginUI.this, MainScreenUI.class);

                            startActivity(intent);

                            finish();

                            // Animacion de transicion para pasar de una activity a otra.
                            overridePendingTransition(R.anim.right_in_animation, R.anim.right_out_animation);

                        } catch (JSONException e) {
                            e.printStackTrace();

                            enterButton.setProgress(0);

                            Log.d(Properties.TAG, "Error logeando usuario: no se pudo parsear el JSON");

                            Snackbar.make(mAlertDialogView, "Ops! Algo ha ido mal", Snackbar.LENGTH_INDEFINITE)
                                    .setAction("Reintentar", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v)
                                        {
                                            enterButton.setProgress(0);
                                            enterButton.performClick();
                                        }
                                    }).show();
                        }
                    }
                }
                , new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        enterButton.setProgress(0);

                        Log.d(Properties.TAG, "VOLLEYERROR: Error logeando usuario: no se pudo sacar sus datos");

                        error.printStackTrace();

                        Snackbar.make(mAlertDialogView, "Ops! Algo ha ido mal", Snackbar.LENGTH_INDEFINITE)
                                .setAction("Reintentar", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v)
                                    {
                                        enterButton.setProgress(0);
                                        enterButton.performClick();
                                    }
                                }).show();
                    }
                });

        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    /**
     * Metodo que comprueba si se ha seleccionado algun sexo.
     * @return true si se ha seleccionado algun sexo.
     */
    private boolean _isGenderSelected()
    {
        boolean selected = (mMaleImageButton.getAlpha() == ACTIVE_ALPHA) || (mFemaleImageButton.getAlpha() == ACTIVE_ALPHA);

        if (!selected)
        {
            Snackbar.make(mAlertDialogView, "No has elegido tu sexo", Snackbar.LENGTH_LONG).show();
        }

        return selected;
    }

    /**
     * Metodo que valida el email y muestra un error si es necesario.
     * @return true si el email es correcto.
     */
    private boolean _validateEmail()
    {
        String email = mEmailEdittext.getText().toString().trim();

        if (!Utils.isValidEmail(email))
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

        if (!Utils.isValidPassword(password))
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

        if (!Utils.isValidAge(age))
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

        if (!Utils.isValidPostalCode(postalCode))
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

        if (!Utils.isValidName(name))
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
                case R.id.name_edittext:
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
