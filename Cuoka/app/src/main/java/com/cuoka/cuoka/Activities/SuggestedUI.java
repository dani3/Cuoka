package com.cuoka.cuoka.Activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.cuoka.cuoka.Beans.ShopSuggested;
import com.cuoka.cuoka.R;
import com.cuoka.cuoka.Singletons.RestClientSingleton;
import com.cuoka.cuoka.Utils.Utils;

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
    @SuppressWarnings("deprecation")
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
                    Snackbar snackbar = Snackbar.make(
                            findViewById(R.id.suggested_coordinator), "El nombre es obligatorio", Snackbar.LENGTH_SHORT);

                    snackbar.getView().setBackgroundColor(getResources().getColor(android.R.color.white));
                    snackbar.setActionTextColor(getResources().getColor(R.color.colorAccent));
                    ((TextView)snackbar.getView().findViewById(android.support.design.R.id.snackbar_text))
                            .setTextColor(getResources().getColor(R.color.colorText));

                    snackbar.show();

                } else if (!Utils.isAlphaNumeric(name)) {
                    Snackbar snackbar = Snackbar.make(
                            findViewById(R.id.suggested_coordinator), "El nombre es incorrecto", Snackbar.LENGTH_SHORT);

                    snackbar.getView().setBackgroundColor(getResources().getColor(android.R.color.white));
                    snackbar.setActionTextColor(getResources().getColor(R.color.colorAccent));
                    ((TextView)snackbar.getView().findViewById(android.support.design.R.id.snackbar_text))
                            .setTextColor(getResources().getColor(R.color.colorText));

                    snackbar.show();

                } else {
                    ShopSuggested shopSuggested = new ShopSuggested(name, link);

                    RestClientSingleton.sendSuggestion(SuggestedUI.this, shopSuggested);

                    final AlertDialog alertDialog = _createDeleteDialog();

                    alertDialog.show();

                    alertDialog.setOnShowListener(new DialogInterface.OnShowListener()
                    {
                        @Override
                        public void onShow(DialogInterface dialog)
                        {
                            alertDialog.getButton(
                                    AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorAccent));
                        }
                    });
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

    /**
     * Metodo que crea un dialogo para mostrar un mensaje.
     */
    private AlertDialog _createDeleteDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(SuggestedUI.this, R.style.MyDialogTheme);

        builder.setTitle("");
        builder.setMessage(getResources().getString(R.string.suggested_message));

        builder.setPositiveButton("Volver", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                finish();
            }
        });

        return builder.create();
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
