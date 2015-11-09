package es.sidelab.cuokawebscraperrestserver.beans;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @class Clase que representa una imagen de un producto, contendra la url y el path en nuestro servidor
 * @author Daniel Mancebo Aldea
 */

@Entity
@Table( name = "IMAGE" )
public class Image 
{
    @Id
    @GeneratedValue( strategy = GenerationType.AUTO )
    @Column( name = "ID" )
    private long id;
    
    @Column( name = "URL" )
    private String url;
    
    @Column( name = "PATH_SMALL_SIZE" )
    private String pathSmallSize;
    
    @Column( name = "PATH_LARGE_SIZE" )
    private String pathLargeSize;
    
    public Image() {}

    public String getUrl() { return url; }
    public String getPathSmallSize() { return pathSmallSize; }
    public String getPathLargeSize() { return pathLargeSize; }

    public void setUrl( String url ) { this.url = url; }
    public void setPathSmallSize( String path ) { this.pathSmallSize = path; }
    public void setPathLargeSize( String path ) { this.pathLargeSize = path; }  
}
