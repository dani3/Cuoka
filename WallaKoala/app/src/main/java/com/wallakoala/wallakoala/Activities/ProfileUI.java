package com.wallakoala.wallakoala.Activities;

import android.animation.TimeInterpolator;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.wallakoala.wallakoala.Singletons.RestClientSingleton;
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
    protected FloatingActionButton mDeleteFAB;

    /* AlertDialog */
    protected AlertDialog mDeleteAlertDialog;

    /* AsyincTask */
    protected AsyncTask mSendModificationToServer;
    protected AsyncTask mDeleteUser;

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

        mThumbnailTop    = bundle.getInt(Properties.PACKAGE + ".top");
        mThumbnailLeft   = bundle.getInt(Properties.PACKAGE + ".left");
        mThumbnailWidth  = bundle.getInt(Properties.PACKAGE + ".width");
        mThumbnailHeight = bundle.getInt(Properties.PACKAGE + ".height");
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
    @SuppressWarnings("deprecation")
    private void _initViews()
    {
        mCollapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.profile_collapsing_layout);
        mTopLevelLayout          = (CoordinatorLayout)findViewById(R.id.profile_coordinator);
        mProfileFAB              = (FloatingActionButton)findViewById(R.id.profile_floating_pic);
        mDeleteFAB               = (FloatingActionButton)findViewById(R.id.profile_delete);

        mFavoriteTextView = (TextView)findViewById(R.id.profile_favorites);
        mShopsTextView    = (TextView)findViewById(R.id.profile_shops);

        mFavoriteTextView.setText(Integer.toString(mUser.getFavoriteProducts().size()));
        mShopsTextView.setText(Integer.toString(mUser.getShops().size()));

        mDeleteFAB.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mDeleteAlertDialog = _createDeleteDialog();

                mDeleteAlertDialog.setOnShowListener(new DialogInterface.OnShowListener()
                {
                    @Override
                    public void onShow(DialogInterface dialog)
                    {
                        mDeleteAlertDialog.getButton(
                                AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorText));
                        mDeleteAlertDialog.getButton(
                                AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorAccent));
                    }
                });

                mDeleteAlertDialog.show();
            }
        });
    }

    /**
     * Metodo que crea un dialogo para confirmar el borrado de la cuenta.
     */
    private AlertDialog _createDeleteDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileUI.this, R.style.MyDialogTheme);

        builder.setTitle("");
        builder.setMessage("¿Seguro que quieres eliminar tu cuenta?");
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                mDeleteUser = new DeleteUser().execute();
            }
        });

        builder.setNegativeButton("Mejor no", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {}
        });

        return builder.create();
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

        mEmailInputLayout.setHint(mUser.getEmail());
        mAgeInputLayout.setHint(Integer.toString(mUser.getAge()));
        mPostalCodeInputLayout.setHint(Integer.toString(mUser.getPostalCode()));
        mNameInputLayout.setHint(mUser.getName());

        // Ponemos como hint los datos del usuario, pero si coge foco, se pone el hint por defecto.
        mEmailEdittext.setOnFocusChangeListener(new View.OnFocusChangeListener()
            {
               @Override
               public void onFocusChange(View v, boolean hasFocus)
               {
                   if (hasFocus)
                   {
                       mEmailInputLayout.setHint(getResources().getString(R.string.email_hint));

                   } else if ((mEmailEdittext.getText().toString().isEmpty())) {
                       mEmailInputLayout.setHint(mUser.getEmail());
                   }
               }
           });

        mAgeEdittext.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (hasFocus)
                {
                    mAgeInputLayout.setHint(getResources().getString(R.string.age_hint));

                } else if ((mAgeEdittext.getText().toString().isEmpty())) {
                    mAgeInputLayout.setHint(Integer.toString(mUser.getAge()));
                }
            }
        });

        mPostalCodeEdittext.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (hasFocus)
                {
                    mPostalCodeInputLayout.setHint(getResources().getString(R.string.postal_code_hint));

                } else if ((mPostalCodeEdittext.getText().toString().isEmpty())) {
                    mPostalCodeInputLayout.setHint(Integer.toString(mUser.getPostalCode()));
                }
            }
        });

        mNameEdittext.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (hasFocus)
                {
                    mNameInputLayout.setHint(getResources().getString(R.string.name_hint));

                } else if ((mNameEdittext.getText().toString().isEmpty())) {
                    mNameInputLayout.setHint(mUser.getName());
                }
            }
        });

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
            setResult(RESULT_OK);

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
        switch (item.getItemId())
        {
            case (android.R.id.home):
                onBackPressed();
                return true;

            case (R.id.menu_item_accept):
                if (_validateName() && _validateAge() && _validateEmail() && _validatePassword() && _validatePostalCode())
                {
                    mSendModificationToServer = new SendModificationToServer().execute();
                }

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Tarea en segundo plano que borra la cuenta del usuario.
     */
    private class DeleteUser extends AsyncTask<String, Void, Void>
    {
        ProgressDialog progressDialog;

        boolean correct;

        @Override
        protected void onPreExecute()
        {
            progressDialog = ProgressDialog.show(ProfileUI.this, "", "Borrando cuenta...", true);
        }

        @Override
        protected Void doInBackground(String... params)
        {
            correct = RestClientSingleton.deleteUser(ProfileUI.this);

            return null;
        }

        @Override
        protected void onPostExecute(Void unused)
        {
            progressDialog.dismiss();

            if (correct)
            {
                Intent intent = new Intent(ProfileUI.this, LoginUI.class);

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(intent);

            } else {
                Snackbar.make(mTopLevelLayout, "Ops, algo ha ido mal", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Reintentar", new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                mDeleteFAB.performClick();

                            }}).show();
            }
        }
    }

    /**
     * Tarea en segundo plano para enviar las modificaciones al servidor.
     */
    private class SendModificationToServer extends AsyncTask<String, Void, Void>
    {
        String name;
        String email;
        String password;
        short age;
        int postalCode;

        boolean correct;

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute()
        {
            progressDialog = ProgressDialog.show(ProfileUI.this, "", "Modificando tus datos...", true);

            name = mNameEdittext.getText().toString();
            email = mEmailEdittext.getText().toString();
            password = mPasswordEdittext.getText().toString();
            age = (mAgeEdittext.getText().toString().isEmpty())
                    ? -1 : Short.valueOf(mAgeEdittext.getText().toString());
            postalCode = (mPostalCodeEdittext.getText().toString().isEmpty())
                    ? -1 : Integer.valueOf(mPostalCodeEdittext.getText().toString());
        }

        @Override
        protected Void doInBackground(String... params)
        {
            // Se envian las modificaciones al servidor.
            correct = RestClientSingleton.sendUserModification(ProfileUI.this, name, email, password, age, postalCode);

            return null;
        }

        @Override
        protected void onPostExecute(Void unused)
        {
            // Cerramos el dialogo.
            progressDialog.dismiss();

            if (correct)
            {
                onBackPressed();

            } else {
                Snackbar.make(mTopLevelLayout, "Ops, algo ha ido mal", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Reintentar", new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                findViewById(R.id.menu_item_accept).performClick();

                            }}).show();
            }
        }
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
