package com.wallakoala.wallakoala.Activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.wallakoala.wallakoala.Beans.ShopSuggested;
import com.wallakoala.wallakoala.R;
import com.wallakoala.wallakoala.Singletons.RestClientSingleton;
import com.wallakoala.wallakoala.Utils.Utils;

/**
 * Pantalla para enviar la sugerencia de una tienda.
 * Created by Daniel Mancebo Aldea on 09/11/2016.
 */

public class SuggestedUI extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_suggested);

        _initViews();
        _initToolbar();
    }

    /**
     * Metodo que inicializa las distintas vistas.
     */
    private void _initViews()
    {
        final EditText nameEditText = (EditText) findViewById(R.id.suggested_name);
        final EditText linkEditText = (EditText) findViewById(R.id.suggested_link);

        FloatingActionButton sendSuggestFAB = (FloatingActionButton) findViewById(R.id.suggested_accept);

        sendSuggestFAB.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String name = nameEditText.getText().toString();
                String link = linkEditText.getText().toString();

                if (name.isEmpty())
                {
                    Snackbar.make(
                            findViewById(R.id.suggested_coordinator), "El nombre es obligatorio", Snackbar.LENGTH_SHORT).show();
                } else if (!Utils.isAlphaNumeric(name)) {
                    Snackbar.make(
                            findViewById(R.id.suggested_coordinator), "El nombre es incorrecto", Snackbar.LENGTH_SHORT).show();
                } else {
                    ShopSuggested shopSuggested = new ShopSuggested(name, link);

                    RestClientSingleton.sendSuggestion(SuggestedUI.this, shopSuggested);

                    finish();
                }
            }
        });
    }

    /**
     * Inicializacion de la Toolbar.
     */
    private void _initToolbar()
    {
        Toolbar toolbar  = (Toolbar) findViewById(R.id.suggested_appbar);
        TextView mToolbarTextView = (TextView) findViewById(R.id.toolbar_textview);

        mToolbarTextView.setText(getResources().getString(R.string.toolbar_suggested));

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        }
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

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.left_in_animation, R.anim.left_out_animation);
    }
}
