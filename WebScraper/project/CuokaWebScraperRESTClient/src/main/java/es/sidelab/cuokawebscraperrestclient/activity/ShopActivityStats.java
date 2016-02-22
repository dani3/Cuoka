package es.sidelab.cuokawebscraperrestclient.activity;

import java.util.List;

/**
 *
 * @author Lucia Fernandez Guzman
 */

public class ShopActivityStats 
{    
    private String shop;
    private boolean online;
    private boolean man;
    private boolean woman;
    private String url;
    
    private List<SectionActivityStats> listSectionStats;

    public ShopActivityStats( String shop ) 
    {
        this.shop = shop;
    }

    public String getShop() { return shop; }
    public boolean isOnline() { return online; }
    public boolean isMan() { return man; }
    public boolean isWoman() { return woman; }
    public String getUrl() { return url; }
    public List<SectionActivityStats> getListSectionStats() 
    { 
        return listSectionStats; 
    }

    public void setOnline( boolean online ) { this.online = online; }
    public void setMan( boolean man ) { this.man = man; }
    public void setWoman( boolean woman ) { this.woman = woman; }
    public void setUrl( String url ) { this.url = url; }   
    public void setListSectionStats( List<SectionActivityStats> listSectionStats ) 
    { 
        this.listSectionStats = listSectionStats;
    }

}
