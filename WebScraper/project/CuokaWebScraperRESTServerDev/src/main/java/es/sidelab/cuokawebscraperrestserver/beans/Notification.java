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
 * Bean que representa una notificacion.
 * @author Daniel Mancebo Aldea
 */

@Entity
@Table(name = "NOTIFICATION")
public class Notification
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private long id;
    
    @Column(name = "TITLE")
    private String title;
    
    @Column(name = "TEXT")
    private String text;
    
    @Column(name = "ACTION")
    private short action;
    
    @Column(name = "EXTRA_INFO")
    private String extraInfo;
    
    @JsonIgnore
    @Column(name = "INSERT_DATE")
    private Calendar insert_date;
    
    @Column(name = "IMAGE")
    private String image;
    
    @Column(name = "OFFSET")
    private short offset;
    
    public Notification() {}

    public Notification(String text
                , String title
                , short action
                , String extraInfo
                , String image) 
    {
        this.title = title;
        this.text = text;
        this.extraInfo = extraInfo;
        this.action = action;
        this.insert_date = Calendar.getInstance();
        this.image = image;
    }
    
    @JsonIgnore
    public Calendar getInsert_date() { return insert_date; }
    public long getId()              { return this.id; }
    public String getExtraInfo()     { return extraInfo; }
    public String getText()          { return text; }
    public short getAction()         { return action; }
    public String getImage()         { return image; }
    public String getTitle()         { return title; }
    public short getOffset()         { return offset; }
    
    @JsonIgnore
    public void setInsert_date(Calendar insert_date) { this.insert_date = insert_date; }
    public void setExtraInfo(String extraInfo)       { this.extraInfo = extraInfo; }
    public void setText(String text)                 { this.text = text; }
    public void setTitle(String title)               { this.title = title; }
    public void setAction(short action)              { this.action = action; }
    public void setImage(String image)               { this.image = image; }
    public void setOffset(short offset)              { this.offset = offset; }
}
