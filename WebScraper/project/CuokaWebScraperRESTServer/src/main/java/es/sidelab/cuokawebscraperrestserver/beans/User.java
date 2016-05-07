package es.sidelab.cuokawebscraperrestserver.beans;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author Daniel Mancebo Aldea
 */

@Entity
@Table( name = "USERS" )
public class User 
{
    @Id
    @GeneratedValue( strategy = GenerationType.AUTO )
    @Column( name = "ID" )
    private long id;
    
    @Column( name = "EMAIL" )
    private String email;
    
    public User() {}
    
    public User( String email )
    {
        this.email = email;
    }
    
    public String getEmail() { return this.email; }
    
    public void setEmail( String email ) { this.email = email; }
}
