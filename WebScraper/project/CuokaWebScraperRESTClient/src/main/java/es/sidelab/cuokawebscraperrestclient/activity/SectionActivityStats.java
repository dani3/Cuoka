package es.sidelab.cuokawebscraperrestclient.activity;

/**
 *
 * @author Lucia Fernandez Guzman
 */

public class SectionActivityStats 
{    
    private String section;
    private int prodOK;
    private int prodNOK;
    private boolean htmlOK;
    private boolean man;
    
    
    public SectionActivityStats(String section) 
    { 
        this.section = section; 
    } 

    public void setMan(boolean man) { this.man = man; }
    public void setHtmlOK(boolean htmlOK) { this.htmlOK = htmlOK; }

    public boolean isMan() { return man; }
    public String getSection() { return section; }
    public int getProdOK() { return prodOK; }
    public int getProdNOK() { return prodNOK; }
    public boolean isHtmlOK() { return htmlOK; }
    
    public void updateProducts(int prodOK, int prodNOK)
    {
        this.prodNOK = prodNOK;
        this.prodOK = prodOK;
    } 
    
}
