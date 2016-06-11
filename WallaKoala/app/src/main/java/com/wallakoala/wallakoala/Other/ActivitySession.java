package com.wallakoala.wallakoala.Other;

import android.util.Log;

import com.wallakoala.wallakoala.Beans.Product;
import com.wallakoala.wallakoala.Properties.Properties;

import java.util.ArrayList;
import java.util.List;

/**
 * @class Clase que guardara toda la actividad del usuario en esta sesion.
 * Created by Daniel Mancebo Aldea on 05/06/2016.
 */

public class ActivitySession
{
    private static List<Product> productsClickedList;
    private static List<Product> productsFavdList;
    private static List<Product> productsWishdList;
    private static List<Product> productsSharedList;
    private static List<Product> productsVisitedList;

    static
    {
        productsClickedList = new ArrayList<>();
        productsFavdList    = new ArrayList<>();
        productsWishdList   = new ArrayList<>();
        productsSharedList  = new ArrayList<>();
        productsVisitedList = new ArrayList<>();
    }

    public static List<Product> getProductsClickedList() { return productsClickedList; }
    public static List<Product> getProductsFavdList()    { return productsFavdList; }
    public static List<Product> getProductsWishdList()   { return productsWishdList; }
    public static List<Product> getProductsSharedList()  { return productsSharedList; }
    public static List<Product> getProductsVisitedList() { return productsVisitedList; }

    public static void addProductToProductsClickedList(Product product) { productsClickedList.add(product); }
    public static void addProductToProductsFavdList(Product product)    { productsFavdList.add(product); }
    public static void addProductToProductsWishdList(Product product)   { productsWishdList.add(product); }
    public static void addProductToProductsSharedList(Product product)  { productsSharedList.add(product); }
    public static void addProductToProductsVisitedList(Product product) { productsVisitedList.add(product); }
}
