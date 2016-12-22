package com.wallakoala.wallakoala.Beans;

import android.support.annotation.NonNull;

/**
 * Clase que representa una tienda.
 * Created by Daniel Mancebo Aldea on 24/10/2016.
 */

public class Shop implements Comparable<Shop>
{
    private String name;
    private int products;

    public Shop(String name, int products)
    {
        this.name = name;
        this.products = products;
    }

    public String getName() {
        return this.name;
    }
    public int getProducts() {
        return this.products;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(@NonNull Shop o)
    {
        return this.name.compareTo(o.name);
    }
}

