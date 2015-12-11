package com.wallakoala.wallakoala.Beans;

/**
 * @class Clase que representa una talla de un producto.
 * Created by Daniel Mancebo on 11/12/2015.
 */

public class Size
{
	private String size; 
    private boolean stock;
    
    public Size() {}
    
    public Size( String size
            , boolean stock )
    {
        this.size = size;
        this.stock = stock;
    }
    
    public String getSize()  { return this.size; }
    public boolean isStock() { return this.stock; }
    
    public void setSize( String size )    { this.size = size; }
    public void setStock( boolean stock ) { this.stock = stock; }
}
