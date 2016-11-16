package com.wallakoala.wallakoala.Activities;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.wallakoala.wallakoala.Beans.Feedback;
import com.wallakoala.wallakoala.R;
import com.wallakoala.wallakoala.Singletons.RestClientSingleton;

/**
 * Pantalla para enviar la opinion del usuario.
 * Created by Daniel Mancebo Aldea on 09/11/2016.
 */

public class FeedbackUI extends AppCompatActivity implements View.OnClickListener
{
    /* Data */
    private short mFeedback;

    /* Drawable */
    private Drawable mEmptyStarDrawable;
    private Drawable mFilledStarDrawable;

    /* ImageButton */
    private ImageButton mOneStarImageButton;
    private ImageButton mTwoStarImageButton;
    private ImageButton mThreeStarImageButton;
    private ImageButton mFourStarImageButton;
    private ImageButton mFiveStarImageButton;

    /* AlertDialog */
    private AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_feedback);

        mFeedback = 0;

        _initViews();
        _initToolbar();
    }

    @SuppressWarnings("deprecation")
    private void _initViews()
    {
        mEmptyStarDrawable  = getResources().getDrawable(R.drawable.ic_star_empty);
        mFilledStarDrawable = getResources().getDrawable(R.drawable.ic_star_filled);

        final EditText feedbackEditText = (EditText) findViewById(R.id.feedback_suggestion);

        mOneStarImageButton   = (ImageButton) findViewById(R.id.one_star);
        mTwoStarImageButton   = (ImageButton) findViewById(R.id.two_star);
        mThreeStarImageButton = (ImageButton) findViewById(R.id.three_star);
        mFourStarImageButton  = (ImageButton) findViewById(R.id.four_star);
        mFiveStarImageButton  = (ImageButton) findViewById(R.id.five_star);

        FloatingActionButton acceptFAB = (FloatingActionButton) findViewById(R.id.feedback_accept);

        mOneStarImageButton.setOnClickListener(this);
        mTwoStarImageButton.setOnClickListener(this);
        mThreeStarImageButton.setOnClickListener(this);
        mFourStarImageButton.setOnClickListener(this);
        mFiveStarImageButton.setOnClickListener(this);

        acceptFAB.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mFeedback != 0)
                {
                    String feedbackText = feedbackEditText.getText().toString();

                    Feedback feedback = new Feedback(mFeedback, feedbackText);

                    RestClientSingleton.sendFeedback(FeedbackUI.this, feedback);

                    mDialog = _createDeleteDialog();

                    mDialog.show();

                    mDialog.setOnShowListener(new DialogInterface.OnShowListener()
                    {
                        @Override
                        public void onShow(DialogInterface dialog)
                        {
                            mDialog.getButton(
                                    AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorAccent));
                        }
                    });

                } else {
                    Snackbar.make(
                            findViewById(R.id.feedback_coordinator), "La valoración es obligatoria", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Inicializacion de la Toolbar.
     */
    private void _initToolbar()
    {
        Toolbar toolbar  = (Toolbar) findViewById(R.id.feedback_appbar);
        TextView mToolbarTextView = (TextView) findViewById(R.id.toolbar_textview);

        mToolbarTextView.setText(getResources().getString(R.string.toolbar_feedback));

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
     * Metodo que crea un dialogo para confirmar el borrado de la cuenta.
     */
    private AlertDialog _createDeleteDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(FeedbackUI.this, R.style.MyDialogTheme);

        builder.setTitle("");
        builder.setMessage("¡Gracias!");
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

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.one_star:
                if (mFeedback != 1)
                {
                    mFeedback = 1;

                    mOneStarImageButton.setBackground(mFilledStarDrawable);
                    mTwoStarImageButton.setBackground(mEmptyStarDrawable);
                    mThreeStarImageButton.setBackground(mEmptyStarDrawable);
                    mFourStarImageButton.setBackground(mEmptyStarDrawable);
                    mFiveStarImageButton.setBackground(mEmptyStarDrawable);
                }

                break;

            case R.id.two_star:
                if (mFeedback != 2)
                {
                    mFeedback = 2;

                    mOneStarImageButton.setBackground(mFilledStarDrawable);
                    mTwoStarImageButton.setBackground(mFilledStarDrawable);
                    mThreeStarImageButton.setBackground(mEmptyStarDrawable);
                    mFourStarImageButton.setBackground(mEmptyStarDrawable);
                    mFiveStarImageButton.setBackground(mEmptyStarDrawable);
                }

                break;

            case R.id.three_star:
                if (mFeedback != 3)
                {
                    mFeedback = 3;

                    mOneStarImageButton.setBackground(mFilledStarDrawable);
                    mTwoStarImageButton.setBackground(mFilledStarDrawable);
                    mThreeStarImageButton.setBackground(mFilledStarDrawable);
                    mFourStarImageButton.setBackground(mEmptyStarDrawable);
                    mFiveStarImageButton.setBackground(mEmptyStarDrawable);
                }

                break;

            case R.id.four_star:
                if (mFeedback != 4)
                {
                    mFeedback = 4;

                    mOneStarImageButton.setBackground(mFilledStarDrawable);
                    mTwoStarImageButton.setBackground(mFilledStarDrawable);
                    mThreeStarImageButton.setBackground(mFilledStarDrawable);
                    mFourStarImageButton.setBackground(mFilledStarDrawable);
                    mFiveStarImageButton.setBackground(mEmptyStarDrawable);
                }

                break;

            case R.id.five_star:
                if (mFeedback != 5)
                {
                    mFeedback = 5;

                    mOneStarImageButton.setBackground(mFilledStarDrawable);
                    mTwoStarImageButton.setBackground(mFilledStarDrawable);
                    mThreeStarImageButton.setBackground(mFilledStarDrawable);
                    mFourStarImageButton.setBackground(mFilledStarDrawable);
                    mFiveStarImageButton.setBackground(mEmptyStarDrawable);
                }

                break;
        }
    }
}
