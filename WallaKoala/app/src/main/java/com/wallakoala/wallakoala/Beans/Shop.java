package com.wallakoala.wallakoala.Beans;

import java.util.Calendar;

/**
 * Clase que representa una tienda.
 * Created by Daniel Mancebo Aldea on 24/10/2016.
 */

public class Shop
{
    private String name;
    private boolean man;
    private boolean woman;
    private int products;

    public Shop(String name, boolean man, boolean woman, int products)
    {
        this.name = name;
        this.man = man;
        this.woman = woman;
        this.products = products;
    }

    public String getName() {
        return this.name;
    }
    public boolean getMan() {
        return this.man;
    }
    public boolean getWoman() {
        return this.woman;
    }
    public int getProducts() {
        return this.products;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setMan(boolean man) {
        this.man = man;
    }
    public void setWoman(boolean woman) {
        this.woman = woman;
    }
    public void setProducts(int products) {
        this.products = products;
    }
}

