package com.wallakoala.wallakoala.Beans;

/**
 * @class Clase que representa una imagen de un color.
 * Created by Daniel Mancebo on 11/12/2015.
 */

public class Image
{
    private String url;
    private String path;
    
    public Image() {}

    public Image( String url
            , String path )
    {
		this.url = url;
		this.path = path;
	}

	public String getUrl()  { return url; }
    public String getPath() { return path; }

    public void setUrl( String url )   { this.url = url; }
    public void setPath( String path ) { this.path = path; }
}
