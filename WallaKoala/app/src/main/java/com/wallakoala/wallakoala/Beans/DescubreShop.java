package com.wallakoala.wallakoala.Beans;

/**
 * Clase DescubreShop que representa una tienda de Descubre.
 * Created by Daniel Mancebo Aldea on 05/11/2017.
 */

public class DescubreShop
{
    private String name;
    private String description;
    private String url;
    private String urlBanner;

    public DescubreShop(String name, String description, String url, String urlBanner)
    {
        this.name = name;
        this.description = description;
        this.url = url;
        this.urlBanner = urlBanner;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getUrl() { return url; }
    public String getUrlBanner() { return urlBanner; }

    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setUrl(String url) { this.url = url; }
    public void setUrlBanner(String urlBanner) { this.urlBanner = urlBanner; }
}
