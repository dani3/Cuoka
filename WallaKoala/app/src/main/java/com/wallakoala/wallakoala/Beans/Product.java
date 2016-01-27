package com.wallakoala.wallakoala.Beans;

import android.graphics.Bitmap;

import java.util.Calendar;
import java.util.List;

/*
 * @class Clase que representa un producto
 * Created by Daniel Mancebo on 11/12/2015.
 */

public class Product
{
	private String name;
	private String shop;
	private String section;
	private double price;
	private String link;
	private boolean newness;
	private List<ColorVariant> colors;
	
	public Product() {}
	
	public Product( String name
			 , String shop
			 , String section
			 , double price
			 , String link
			 , List<ColorVariant> colors
			 , boolean newness )
	{
		this.name = name;
		this.shop = shop;
		this.section = section;
		this.price = price;
		this.link = link;
		this.colors = colors;
		this.newness = newness;
	}

	public String getName()               { return this.name; }
	public String getShop()               { return this.shop; }
	public String getSection()            { return this.section; }
	public double getPrice()              { return this.price; }
	public List<ColorVariant> getColors() { return this.colors; }
	public String getLink()               { return this.link; }
	public boolean isNewness()            { return this.newness; }

	public void setName( String name )                 { this.name = name; }
	public void setShop( String shop )                 { this.shop = shop; }
	public void setSection( String section )           { this.section = section; }
	public void setPrice( double price )               { this.price = price; }
	public void setColors( List<ColorVariant> colors ) { this.colors = colors; }
	public void setLink( String link )                 { this.link = link; }
	public void setNewness( boolean newness )          { this.newness = newness; }

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

		for (ColorVariant cv : this.colors)
		{
			// Comprobar cada color
		}

		return true;
	}
}
