package es.sidelab.cuokawebscraperrestserver.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Calendar;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @class Clase que representa el registro de un usuario.
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
    
    @Column( name = "AGE" )
    private short age;
    
    @Column( name = "MAN" )
    private boolean man;
    
    @Column( name = "PASSWORD" )
    private String password;
    
    @Column( name = "POSTAL_CODE" )
    private int postalCode;
    
    @JsonIgnore
    @Column( name = "DATE" )
    private Calendar registrationDate;
    
    public User() {}
    
    public User( String email
            , short age
            , boolean man
            , String password
            , int postalCode
            , Calendar registrationDate )
    {
        this.email = email;
        this.age = age;
        this.man = man;
        this.password = password;
        this.postalCode = postalCode;
        this.registrationDate = registrationDate;
    }
    
    @JsonIgnore
    public Calendar getRegistrationDate() { return this.registrationDate; }
    public String getEmail() { return this.email; }
    public String getPassword() { return this.password; }
    public short getAge() { return this.age; }
    public boolean getMan() { return this.man; }
    public int getPostalCode() { return this.postalCode; }
    
    @JsonIgnore
    public void setRegistrationDate( Calendar registrationDate ) { this.registrationDate = registrationDate; }    
    public void setEmail( String email ) { this.email = email; }
    public void setPassword( String password ) { this.password = password; }
    public void setAge( short age ) { this.age = age; }
    public void setMan( boolean man ) { this.man = man; }
    public void setPostalCode( int postalCode ) { this.postalCode = postalCode; }
}
