package com.wallakoala.wallakoala.Beans;

import java.util.List;

public class Product 
{
	private String name;
	private String shop;
	private String section;
	private double price;
	private boolean man;
	private String link;
	private List<ColorVariant> colors;
	
	public Product() {}
	
	public Product( String name
			 , String shop
			 , String section
			 , double price
			 , boolean man
			 , String link
			 , List<ColorVariant> colors )
	{
		this.name = name;
		this.shop = shop;
		this.section = section;
		this.price = price;
		this.man = man;
		this.link = link;
		this.colors = colors;
	}

	public String getName() { return this.name; }
	public String getShop() { return this.shop; 	}
	public String getSection() { return this.section; }
	public double getPrice() { return this.price; }
	public List<ColorVariant> getColors() { return this.colors; }
	public boolean isMan() { return this.man; }
	public String getLink() { return this.link; }

	public void setName( String name ) { this.name = name; }
	public void setShop( String shop ) {	this.shop = shop; 	}
	public void setSection( String section ) { this.section = section; }
	public void setPrice( double price ) { this.price = price; }
	public void setColors( List<ColorVariant> colors ) { this.colors = colors; }
	public void setMan( boolean man ) { this.man = man; }
	public void setLink( String link ) { this.link = link; }
}
