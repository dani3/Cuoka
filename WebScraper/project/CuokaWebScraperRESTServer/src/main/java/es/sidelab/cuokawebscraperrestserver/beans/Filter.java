package es.sidelab.cuokawebscraperrestserver.beans;

import java.util.List;

/**
 * Clase que representa el estado de un filtro.
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
    
    public Filter() {}

    public Filter(boolean man
            , boolean newness
            , int priceFrom
            , int priceTo
            , List<String> colors
            , List<String> sections) 
    {
        this.man = man;
        this.newness = newness;
        this.priceFrom = priceFrom;
        this.priceTo = priceTo;
        this.colors = colors;
        this.sections = sections;
    }
    
    public void setMan(boolean man) 
    {
        this.man = man;
    }

    public void setNewness(boolean newness)
    {
        this.newness = newness;
    }

    public void setPriceFrom(int priceFrom) 
    {
        this.priceFrom = priceFrom;
    }

    public void setPriceTo(int priceTo) 
    {
        this.priceTo = priceTo;
    }

    public void setColors(List<String> colors) 
    {
        this.colors = colors;
    }

    public void setSections(List<String> sections) 
    {
        this.sections = sections;
    }

    public boolean isMan() 
    {
        return man;
    }

    public boolean isNewness() 
    {
        return newness;
    }

    public int getPriceFrom() 
    {
        return priceFrom;
    }

    public int getPriceTo() 
    {
        return priceTo;
    }

    public List<String> getColors()
    {
        return colors;
    }

    public List<String> getSections() 
    {
        return sections;
    }
    
    @Override
    public String toString()
    {
        String _sections = "";
        String _colors = "";
        
        for (String section : this.sections)
        {
            _sections += section + ","; 
        }
        
        if (!_sections.isEmpty())
        {
            _sections = _sections.substring(0, _sections.length() - 2);
        }
        
        for (String color : this.colors)
        {
            _colors += color + ","; 
        }
        
        if (!_colors.isEmpty())
        {
            _colors = _colors.substring(0, _colors.length() - 2);
        }
        
        String line = "{man:" + this.man 
            + ";newness:" + this.newness 
            + ";priceFrom:" + this.priceFrom 
            + ";priceTo:" + this.priceTo 
            + ";colors:" + _colors
            + ";sections:" + _sections + "}";
        
        return line;
    }
}
