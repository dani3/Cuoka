package com.cuoka.cuoka.Beans;

import android.support.annotation.NonNull;

/**
 * Clase que representa una notificacion.
 * Created by Daniel Mancebo Aldea on 25/11/2016.
 */

public class Notification implements Comparable<Notification>
{
    private long id;
    private String title;
    private String text;
    private String extraInfo;
    private String image;
    private short offset;
    private short action;

    public Notification(long id
            , String text
            , String title
            , String extraInfo
            , String image
            , short offset
            , short action)
    {
        this.id = id;
        this.title = title;
        this.text = text;
        this.extraInfo = extraInfo;
        this.action = action;
        this.image = image;
        this.offset = offset;
    }

    public long getId()          { return id; }
    public String getExtraInfo() { return extraInfo; }
    public String getText()      { return text; }
    public String getImage()     { return image; }
    public String getTitle()     { return title; }
    public short getOffset()     { return offset; }
    public short getAction()     { return action; }

    @Override
    public int compareTo(@NonNull Notification o)
    {
        return (offset >= o.offset) ? 1 : -1;
    }
}
