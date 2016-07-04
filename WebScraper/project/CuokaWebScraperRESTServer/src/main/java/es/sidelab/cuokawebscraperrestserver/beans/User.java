package es.sidelab.cuokawebscraperrestserver.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Calendar;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @class Clase que representa el registro de un usuario.
 * @author Daniel Mancebo Aldea
 */

@Entity
@Table( name = "USERS" )
public class User 
{
    @Id
    @GeneratedValue( strategy = GenerationType.AUTO )
    @Column( name = "ID" )
    private long id;
    
    @Column( name = "EMAIL" )
    private String email;
    
    @Column( name = "AGE" )
    private short age;
    
    @Column( name = "MAN" )
    private boolean man;
    
    @Column( name = "PASSWORD" )
    private String password;
    
    @Column( name = "POSTAL_CODE" )
    private int postalCode;
    
    @JsonIgnore
    @Column( name = "DATE" )
    private Calendar registrationDate;
    
    @JsonIgnore
    @ElementCollection
    @Column( name = "FAVORITE_PRODUCTS" )
    private Set<Long> favoriteProducts;
 
    @JsonIgnore
    @ElementCollection
    @Column( name = "VIEWED_PRODUCTS" )
    private Set<Long> viewedProducts;
    
    @JsonIgnore
    @ElementCollection
    @Column( name = "SHARED_PRODUCTS" )
    private Set<Long> sharedProducts;
    
    @JsonIgnore
    @ElementCollection
    @Column( name = "VISITED_PRODUCTS" )
    private Set<Long> visitedProducts;
    
    @JsonIgnore
    @ElementCollection
    @Column( name = "ADDED_TO_CART_PRODUCTS" )
    private Set<Long> addedToCartProducts;
    
    @JsonIgnore
    @ElementCollection
    @Column( name = "SHOPS" )
    private Set<String> shops;
    
    @JsonIgnore
    @Column( name = "INITIALIZATION_VECTOR" )
    private byte[] initializationVector;
    
    public User() {}
    
    public User( String email
            , short age
            , boolean man
            , String password
            , int postalCode
            , Calendar registrationDate )
    {
        this.email = email;
        this.age = age;
        this.man = man;
        this.password = password;
        this.postalCode = postalCode;
        this.registrationDate = registrationDate;
    }
    
    public String getEmail()    { return this.email; }
    public String getPassword() { return this.password; }
    public short getAge()       { return this.age; }
    public boolean getMan()     { return this.man; }
    public int getPostalCode()  { return this.postalCode; }
    
    @JsonIgnore
    public byte[] getInitializationVector() { return this.initializationVector; }  
    
    @JsonIgnore
    public Calendar getRegistrationDate() { return this.registrationDate; }
    
    @JsonProperty
    public Set<Long> getFavoriteProducts() { return favoriteProducts; }
    @JsonIgnore
    public Set<Long> getViewedProducts() { return viewedProducts; }
    @JsonIgnore
    public Set<Long> getSharedProducts() { return sharedProducts; }
    @JsonIgnore
    public Set<Long> getVisitedProducts() { return visitedProducts; }
    @JsonIgnore
    public Set<Long> getAddedToCartProducts() { return addedToCartProducts; }
    
    @JsonProperty
    public Set<String> getShops() { return shops; }
    
    @JsonIgnore
    public long getId() { return id; }
    
    @JsonIgnore
    public void setInitializationVector( byte[] initializationVector ) { this.initializationVector = initializationVector; }
    
    @JsonIgnore
    public void setRegistrationDate( Calendar registrationDate ) { this.registrationDate = registrationDate; } 
    
    @JsonIgnore
    public void setFavoriteProducts( Set<Long> favoriteProducts ) { this.favoriteProducts = favoriteProducts; }
    @JsonIgnore
    public void setViewedProducts( Set<Long> viewedProducts ) { this.viewedProducts = viewedProducts; }
    @JsonIgnore
    public void setSharedProducts( Set<Long> sharedProducts ) { this.sharedProducts = sharedProducts; }
    @JsonIgnore
    public void setVisitedProducts( Set<Long> visitedProducts ) { this.visitedProducts = visitedProducts; }
    @JsonIgnore
    public void setAddedToCartProducts( Set<Long> addedToCartProducts ) { this.addedToCartProducts = addedToCartProducts; }
    
    @JsonIgnore
    public void setShops( Set<String> shops ) { this.shops = shops; }
    
    public void setEmail( String email )        { this.email = email; }
    public void setPassword( String password )  { this.password = password; }
    public void setAge( short age )             { this.age = age; }
    public void setMan( boolean man )           { this.man = man; }
    public void setPostalCode( int postalCode ) { this.postalCode = postalCode; }
    
    public void addToFavoriteProducts( Long idProduct )    { this.favoriteProducts.add( idProduct ); }
    public void addToViewedProducts( Long idProduct )      { this.viewedProducts.add( idProduct ); }
    public void addToSharedProducts( Long idProduct )      { this.sharedProducts.add( idProduct ); }
    public void addToVisitedProducts( Long idProduct )     { this.visitedProducts.add( idProduct ); }
    public void addToAddedToCartProducts( Long idProduct ) { this.addedToCartProducts.add( idProduct ); }
}
