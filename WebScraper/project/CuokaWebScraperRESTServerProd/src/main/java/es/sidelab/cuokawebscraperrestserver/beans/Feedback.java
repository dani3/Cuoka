package es.sidelab.cuokawebscraperrestserver.beans;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Bean que almacena las opiniones sobre la App.
 * @author Daniel Mancebo Aldea.
 */

@Entity
@Table(name = "FEEDBACK")
public class Feedback 
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private long id;
    
    @Column(name = "STARS")
    private short stars;
    
    @Column(name = "OPINION")
    private String opinion;
    
    public Feedback() {}

    public Feedback(short stars, String opinion) 
    {
        this.stars = stars;
        this.opinion = opinion;
    } 

    public short getStars() {
        return stars;
    }

    public void setStars(short stars) {
        this.stars = stars;
    }

    public String getOpinion() {
        return opinion;
    }

    public void setOpinion(String opinion) {
        this.opinion = opinion;
    }
}
