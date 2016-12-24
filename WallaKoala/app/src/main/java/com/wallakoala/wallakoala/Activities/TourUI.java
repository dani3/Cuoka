package com.wallakoala.wallakoala.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntro2Fragment;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.wallakoala.wallakoala.R;

/**
 * Pantalla con el tour que hace uso de la libreria AppIntro.
 * Created by Daniel Mancebo Aldea on 11/12/2016.
 */

public class TourUI extends AppIntro
{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        addSlide(AppIntroFragment.newInstance("Tus tiendas"
                , "Selecciona tus tiendas favoritas para ver sus novedades de cada día."
                , R.drawable.tour_select_shops
                , getResources().getColor(R.color.tour_select_shops)));

        addSlide(AppIntroFragment.newInstance("Tus tiendas"
                , "Selecciona tus tiendas favoritas para ver sus novedades de cada día."
                , R.drawable.tour_select_shops
                , getResources().getColor(R.color.tour_select_shops)));

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
