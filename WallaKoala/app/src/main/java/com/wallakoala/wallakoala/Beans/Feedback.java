package com.wallakoala.wallakoala.Beans;

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

    public void setStars(short stars)
    {
        this.stars = stars;
    }

    public String getOpinion()
    {
        return opinion;
    }

    public void setOpinion(String opinion)
    {
        this.opinion = opinion;
    }
}
