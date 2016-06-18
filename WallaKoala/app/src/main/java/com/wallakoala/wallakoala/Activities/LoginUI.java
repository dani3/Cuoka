package com.wallakoala.wallakoala.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.wallakoala.wallakoala.R;

/**
 * @class Pantalla para logearse de distinta formas.
 * Created by Daniel Mancebo on 18/06/2016.
 */

public class LoginUI extends AppCompatActivity
{
    /* Buttons */
    private Button mSignInButton;
    private Button mSingUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login);

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
                Intent intent = new Intent(LoginUI.this, SignUpUI.class);
                startActivity(intent);

                // Animacion de transicion para pasar de una activity a otra.
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
            }
        });
    }
}
