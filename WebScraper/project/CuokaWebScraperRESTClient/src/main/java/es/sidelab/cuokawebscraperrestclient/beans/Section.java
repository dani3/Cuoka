package es.sidelab.cuokawebscraperrestclient.beans;

/**
 * @class Clase que representa una sección de una tienda, contendrá el path,
 *        el nombre y el sexo.
 * 
 * @author Daniel Mancebo Aldea
 */

public class Section 
{
    private String name;
    private String path;
    private boolean man;
    
    public Section() {}
    
    public Section( String name, String path, boolean man )
    {
        this.name = name;
        this.path = path;
        this.man = man;
    }

    public void setName( String name ) { this.name = name; }
    public void setPath( String path ) { this.path = path; }
    public void setMan( boolean man ) { this.man = man; }
    
    public String getName() { return this.name; }
    public String getPath() { return this.path; }
    public boolean isMan() { return this.man; }
}
