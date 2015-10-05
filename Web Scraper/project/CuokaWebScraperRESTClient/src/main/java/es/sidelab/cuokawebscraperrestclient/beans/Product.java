package es.sidelab.cuokawebscraperrestclient.beans;

/**
 * @class Clase que representa una prenda.
 * @author Daniel Mancebo Aldea
 */
public class Product 
{
    private double price;
    private String name;
    private String path;

    public Product( double price, String name, String path ) 
    {
        this.price = price;
        this.name = name;
        this.path = path;
    }   
}
