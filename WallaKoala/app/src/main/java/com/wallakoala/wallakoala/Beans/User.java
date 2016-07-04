package com.wallakoala.wallakoala.Beans;

/**
 * @class Clase que representa un usuario.
 * Created by Daniel Mancebo Aldea on 04/07/2016.
 */
public class User
{
    private String email;
    private String password;
    private int age;
    private boolean man;
    private int postalCode;

    public User() {}

    public User(String email
            , int age
            , boolean man
            , String password
            , int postalCode)
    {
        this.email = email;
        this.age = age;
        this.man = man;
        this.password = password;
        this.postalCode = postalCode;
    }

    public String getEmail()    { return this.email; }
    public String getPassword() { return this.password; }
    public int getAge()         { return this.age; }
    public boolean getMan()     { return this.man; }
    public int getPostalCode()  { return this.postalCode; }

    public void setEmail(String email)        { this.email = email; }
    public void setPassword(String password)  { this.password = password; }
    public void setAge(int age)               { this.age = age; }
    public void setMan(boolean man)           { this.man = man; }
    public void setPostalCode(int postalCode) { this.postalCode = postalCode; }
}
