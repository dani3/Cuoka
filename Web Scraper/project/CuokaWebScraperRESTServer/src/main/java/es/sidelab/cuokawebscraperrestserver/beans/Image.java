package es.sidelab.cuokawebscraperrestserver.beans;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @class Clase que representa una imagen de un producto, contendra la url y el path en nuestro servidor
 * @author Daniel Mancebo Aldea
 */

@Entity
public class Image 
{
    @Id
    @GeneratedValue( strategy = GenerationType.AUTO )
    private long id;
    private String url;
    private String pathSmallSize;
    private String pathLargeSize;
    
    public Image() {}

    public String getUrl() { return url; }
    public String getPathSmallSize() { return pathSmallSize; }
    public String getPathLargeSize() { return pathLargeSize; }

    public void setUrl( String url ) { this.url = url; }
    public void setPathSmallSize( String path ) { this.pathSmallSize = path; }
    public void setPathLargeSize( String path ) { this.pathLargeSize = path; }  
}
