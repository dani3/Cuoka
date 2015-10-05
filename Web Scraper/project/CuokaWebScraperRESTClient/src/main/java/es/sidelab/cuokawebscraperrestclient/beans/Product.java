package es.sidelab.cuokawebscraperrestclient.beans;

/**
 * @class Clase que representa una prenda.
 * @author Daniel Mancebo Aldea
 */
public class Product 
{
    private double _price;
    private String _name;
    private String _path;

    public Product( double price, String name, String path ) 
    {
        this._price = price;
        this._name = name;
        this._path = path;
    }   
}
