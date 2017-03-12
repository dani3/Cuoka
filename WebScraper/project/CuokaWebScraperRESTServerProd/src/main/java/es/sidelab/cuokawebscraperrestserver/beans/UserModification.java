package es.sidelab.cuokawebscraperrestserver.beans;

/**
 * Clase que con las modificaciones que se quieren realizar a un usuario.
 * @author Daniel Mancebo Aldea
 */

public class UserModification 
{
    private String name;
    private String email;
    private String password;
    private short age;
    private int postalCode;
    
    public UserModification() {}

    public UserModification(String name
                    , String email
                    , String password
                    , short age
                    , int postalCode) 
    {
        this.name = name;
        this.email = email;
        this.password = password;
        this.age = age;
        this.postalCode = postalCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public short getAge() {
        return age;
    }

    public void setAge(short age) {
        this.age = age;
    }

    public int getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(int postalCode) {
        this.postalCode = postalCode;
    }
}
