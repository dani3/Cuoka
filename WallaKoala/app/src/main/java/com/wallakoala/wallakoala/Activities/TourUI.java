package com.wallakoala.wallakoala.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntro2Fragment;

/**
 * Pantalla con el tour que hace uso de la libreria AppIntro.
 * Created by Daniel Mancebo Aldea on 11/12/2016.
 */

public class TourUI extends AppIntro2
{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //addSlide(AppIntro2Fragment.newInstance("Bienvenido a Cuoka"
        //        , "Con Cuoka podrás ver todas tus tiendas preferidas y descubrir nuevas marcas", ));

        showSkipButton(true);
        setProgressButtonEnabled(true);

        setFadeAnimation();
        //setZoomAnimation();
        //setFlowAnimation();
        //setSlideOverAnimation();
        //setDepthAnimation();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment)
    {
        super.onSkipPressed(currentFragment);

        // Do something when users tap on Skip button.
    }

    @Override
    public void onDonePressed(Fragment currentFragment)
    {
        super.onDonePressed(currentFragment);

        // Do something when users tap on Done button.
    }
}
