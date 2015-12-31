package es.sidelab.cuokawebscraperrestserver.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    
    @JsonIgnore
    @Column( name = "URL" )
    private String url;
    
    @Column( name = "PATH" )
    private String path;
    
    public Image() {}

    @JsonIgnore
    public String getUrl() { return url; }
    public String getPath() { return path; }

    @JsonIgnore
    public void setUrl( String url ) { this.url = url; }
    public void setPath( String path ) { this.path = path; }
}
