package com.wallakoala.wallakoala.Activities;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.wallakoala.wallakoala.R;

/**
 * @class Pantalla de login que premite registrarse con Facebook, Google+, Instagram y un registro propio.
 * Created by Daniel Mancebo on 09/11/2015.
 */

public class SignUpUI extends AppCompatActivity
{
    private TextInputLayout emailInputLayout, passwordInputLAyout;
    private EditText emailEdittext, passwordEdittext;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // Ocultamos el titulo de la app (debe ejecutarse antes del super.onCreate y de cargar el layout)
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.sign_up);

        emailEdittext = (EditText)findViewById(R.id.email_edittext);
        emailInputLayout = (TextInputLayout)findViewById(R.id.email_input_layout_name);
        passwordEdittext = (EditText)findViewById(R.id.password_edittext);
        passwordInputLAyout = (TextInputLayout)findViewById(R.id.password_input_layout_name);

        //emailEdittext.addTextChangedListener(new MyTextWatcher(emailEdittext));
        //passwordEdittext.addTextChangedListener(new MyTextWatcher(passwordEdittext));
    }

    private boolean validateEmail()
    {
        String email = emailEdittext.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            emailInputLayout.setError("Email incorrecto");
            requestFocus(emailEdittext);
            return false;
        } else {
            emailInputLayout.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword()
    {
        if (passwordEdittext.getText().toString().trim().isEmpty()) {
            passwordInputLAyout.setError("Contraseña incorrecta");
            requestFocus(passwordEdittext);
            return false;
        } else {
            passwordInputLAyout.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private class MyTextWatcher implements TextWatcher
    {
        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                /*case R.id.input_name:
                    validateName();
                    break;*/
                case R.id.email_edittext:
                    validateEmail();
                    break;
                case R.id.password_edittext:
                    validatePassword();
                    break;
            }
        }
    }
}
