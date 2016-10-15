package com.wallakoala.wallakoala.Beans;

import java.util.Set;

/**
 * Clase que representa un usuario.
 * Created by Daniel Mancebo Aldea on 04/07/2016.
 */

public class User
{
    private long id;
    private String email;
    private String password;
    private int age;
    private int postalCode;
    private boolean man;
    private Set<String> shops;
    private Set<Long> favoriteProducts;

    public User() {}

    public long getId()         { return this.id; }
    public String getEmail()    { return this.email; }
    public String getPassword() { return this.password; }
    public int getAge()         { return this.age; }
    public boolean getMan()     { return this.man; }
    public int getPostalCode()  { return this.postalCode; }

    public Set<Long> getFavoriteProducts() { return this.favoriteProducts; }
    public Set<String> getShops()          { return this.shops; }

    public void setId(long id)                { this.id = id; }
    public void setEmail(String email)        { this.email = email; }
    public void setPassword(String password)  { this.password = password; }
    public void setAge(int age)               { this.age = age; }
    public void setMan(boolean man)           { this.man = man; }
    public void setPostalCode(int postalCode) { this.postalCode = postalCode; }

    public void setFavoriteProducts(Set<Long> favoriteProducts) { this.favoriteProducts = favoriteProducts; }
    public void setShops(Set<String> shops)                     { this.shops = shops; }
}
