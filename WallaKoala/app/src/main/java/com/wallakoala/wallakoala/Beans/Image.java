package com.wallakoala.wallakoala.Beans;

/**
 * @class Clase que representa una imagen de un color.
 * Created by Daniel Mancebo on 11/12/2015.
 */

public class Image
{
    private String path;
    
    public Image() {}

    public Image(  String path )
    {
		this.path = path;
	}

    public String getPath() { return path; }

    public void setPath( String path ) { this.path = path; }
}
