package com.cuoka.cuoka.Beans;

/**
 * Clase que contiene la informacion de la tienda sugerida.
 * Created by Daniel Mancebo Aldea on 13/11/2016.
 */

public class ShopSuggested
{
    private String shop;
    private String link;

    public ShopSuggested(String shop, String link)
    {
        this.shop = shop;
        this.link = link;
    }

    public String getShop()
    {
        return shop;
    }

    public void setShop(String shop)
    {
        this.shop = shop;
    }

    public String getLink()
    {
        return link;
    }

    public void setLink(String link)
    {
        this.link = link;
    }
}
