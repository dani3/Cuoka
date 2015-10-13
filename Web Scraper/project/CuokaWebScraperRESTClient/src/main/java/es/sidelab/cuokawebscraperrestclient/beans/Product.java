package es.sidelab.cuokawebscraperrestclient.beans;

/**
 * @class Clase que representa un producto.
 * @author Daniel Mancebo Aldea
 */

public class Product 
{
    private double price;
    private String name;
    private String path;
    
    public Product() {}

    public Product( double price, String name, String path ) 
    {
        this.price = price;
        this.name = name;
        this.path = path;
    }  
    
    public void setPrice( double price ) { this.price = price; }
    public void setName( String name ) { this.name = name; }
    public void setPath( String path ) { this.path = path; }
    
    public double getPrice() { return this.price; }
    public String getName() { return this.name; }
    public String getPath() { return this.path; }
}
