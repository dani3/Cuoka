package com.wallakoala.wallakoala.Beans;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

/*
 * @class Clase que representa un producto.
 * Created by Daniel Mancebo on 11/12/2015.
 */

public class Product implements Serializable
{
	private String name;
	private String shop;
	private String section;
	private double price;
	private String link;
	private String description;
	private List<ColorVariant> colors;
	
	public Product() {}
	
	public Product(String name
			 , String shop
			 , String section
			 , double price
			 , String link
             , String description
			 , List<ColorVariant> colors)
	{
		this.name = name;
		this.shop = shop;
		this.section = section;
		this.price = price;
		this.link = link;
        this.description = description;
		this.colors = colors;
	}

	public String getName()               { return this.name; }
	public String getShop()               { return this.shop; }
	public String getSection()            { return this.section; }
	public double getPrice()              { return this.price; }
    public String getDescription()        { return this.description; }
	public List<ColorVariant> getColors() { return this.colors; }
	public String getLink()               { return this.link; }

	public void setName(String name) { this.name = name; }

	public boolean isOkay()
	{
		if ((this.name == null) || (this.name.isEmpty()))
			return false;

		if ((this.shop == null) || (this.shop.isEmpty()))
			return false;

		if ((this.section == null) || (this.section.isEmpty()))
			return false;

		if (this.price <= 0.0f)
			return false;

		if ((this.link == null) || (this.link.isEmpty()))
			return false;

		if ((this.colors == null) || (this.colors.isEmpty()))
			return false;

		if (!this.colors.get(0).isOkay())
			return false;

		return true;
	}
}
