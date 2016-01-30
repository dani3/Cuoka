package com.wallakoala.wallakoala.Beans;

import java.io.Serializable;

/**
 * @class Clase que representa una imagen de un color.
 * Created by Daniel Mancebo on 11/12/2015.
 */

public class Image implements Serializable
{
    private String path;
    
    public Image() {}

    public Image(  String path )
    {
		this.path = path;
	}

    public String getPath() { return path; }

    public boolean isOkay()
    {
        return ((path != null) && (!path.isEmpty()));
    }
}
