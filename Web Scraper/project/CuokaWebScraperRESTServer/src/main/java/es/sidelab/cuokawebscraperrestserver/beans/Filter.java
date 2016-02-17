package es.sidelab.cuokawebscraperrestserver.beans;

import java.util.List;

/**
 *
 * @author Daniel Mancebo Aldea
 */

public class Filter 
{
    private boolean man;
    private boolean newness;
    private int priceFrom;
    private int priceTo;
    private List<String> colors;
    private List<String> sections;
    private List<String> shops;
    
    public Filter() {}

    public Filter(boolean man
            , boolean newness
            , int priceFrom
            , int priceTo
            , List<String> colors
            , List<String> sections
            , List<String> shops) 
    {
        this.man = man;
        this.newness = newness;
        this.priceFrom = priceFrom;
        this.priceTo = priceTo;
        this.colors = colors;
        this.sections = sections;
        this.shops = shops;
    }
    
    public void setMan(boolean man) {
        this.man = man;
    }

    public void setNewness(boolean newness) {
        this.newness = newness;
    }

    public void setPriceFrom(int priceFrom) {
        this.priceFrom = priceFrom;
    }

    public void setPriceTo(int priceTo) {
        this.priceTo = priceTo;
    }

    public void setColors(List<String> colors) {
        this.colors = colors;
    }

    public void setSections(List<String> sections) {
        this.sections = sections;
    }

    public void setShops(List<String> shops) {
        this.shops = shops;
    }   

    public boolean isMan() {
        return man;
    }

    public boolean isNewness() {
        return newness;
    }

    public int getPriceFrom() {
        return priceFrom;
    }

    public int getPriceTo() {
        return priceTo;
    }

    public List<String> getColors() {
        return colors;
    }

    public List<String> getSections() {
        return sections;
    }

    public List<String> getShops() {
        return shops;
    }
}
