package com.wallakoala.wallakoala.Beans;

import java.util.Set;

/**
 * @class Clase que representa la actividad del usuario en la aplicacion.
 * Created by Daniel Mancebo on 26/06/2016.
 */

public class UserActivity
{
    private Set<Long> favoriteProducts;
    private Set<Long> viewedProducts;
    private Set<Long> sharedProducts;
    private Set<Long> visitedProducts;
    private Set<Long> addedToCartProducts;

    public UserActivity() {}

    public Set<Long> getFavoriteProducts()    { return favoriteProducts; }
    public Set<Long> getViewedProducts()      { return viewedProducts; }
    public Set<Long> getSharedProducts()      { return sharedProducts; }
    public Set<Long> getVisitedProducts()     { return visitedProducts; }
    public Set<Long> getAddedToCartProducts() { return addedToCartProducts; }

    public void setFavoriteProducts(Set<Long> favoriteProducts)       { this.favoriteProducts = favoriteProducts; }
    public void setViewedProducts(Set<Long> viewedProducts)           { this.viewedProducts = viewedProducts; }
    public void setSharedProducts(Set<Long> sharedProducts)           { this.sharedProducts = sharedProducts; }
    public void setVisitedProducts(Set<Long> visitedProducts)         { this.visitedProducts = visitedProducts; }
    public void setAddedToCartProducts(Set<Long> addedToCartProducts) { this.addedToCartProducts = addedToCartProducts; }

    public void addToFavoriteProducts( Long idProduct )    { this.favoriteProducts.add( idProduct ); }
    public void addToViewedProducts( Long idProduct )      { this.viewedProducts.add( idProduct ); }
    public void addToSharedProducts( Long idProduct )      { this.sharedProducts.add( idProduct ); }
    public void addToVisitedProducts( Long idProduct )     { this.visitedProducts.add( idProduct ); }
    public void addToAddedToCartProducts( Long idProduct ) { this.addedToCartProducts.add( idProduct ); }
}
