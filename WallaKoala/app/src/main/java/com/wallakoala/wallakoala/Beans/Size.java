package com.wallakoala.wallakoala.Beans;

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
