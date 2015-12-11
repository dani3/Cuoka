package com.wallakoala.wallakoala.Beans;

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
	private boolean man;
	private String link;
	private boolean newness;
	private Calendar insertDate;
	private List<ColorVariant> colors;
	
	public Product() {}
	
	public Product( String name
			 , String shop
			 , String section
			 , double price
			 , boolean man
			 , String link
			 , List<ColorVariant> colors
			 , boolean newness
			 , Calendar insertDate )
	{
		this.name = name;
		this.shop = shop;
		this.section = section;
		this.price = price;
		this.man = man;
		this.link = link;
		this.colors = colors;
		this.newness = newness;
		this.insertDate = insertDate;
	}

	public String getName() { return this.name; }
	public String getShop() { return this.shop; 	}
	public String getSection() { return this.section; }
	public double getPrice() { return this.price; }
	public List<ColorVariant> getColors() { return this.colors; }
	public boolean isMan() { return this.man; }
	public String getLink() { return this.link; }
	public boolean isNewness() { return this.newness; }
	public Calendar getInsertDate() { return this.insertDate; }

	public void setName( String name ) { this.name = name; }
	public void setShop( String shop ) {	this.shop = shop; 	}
	public void setSection( String section ) { this.section = section; }
	public void setPrice( double price ) { this.price = price; }
	public void setColors( List<ColorVariant> colors ) { this.colors = colors; }
	public void setMan( boolean man ) { this.man = man; }
	public void setLink( String link ) { this.link = link; }
	public void setNewness( boolean newness ) { this.newness = newness; }
	public void setInsertDate( Calendar insertDate ) { this.insertDate = insertDate; }
}
