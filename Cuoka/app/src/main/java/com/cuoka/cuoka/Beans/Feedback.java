package com.cuoka.cuoka.Beans;

/**
 * Clase que contiene la informacion de una sugerencia.
 * Created by Daniel Mancebo Aldea on 13/11/2016.
 */

public class Feedback
{
    private short stars;
    private String opinion;

    public Feedback(short stars, String opinion)
    {
        this.stars = stars;
        this.opinion = opinion;
    }

    public short getStars()
    {
        return stars;
    }

    public String getOpinion()
    {
        return opinion;
    }
}
