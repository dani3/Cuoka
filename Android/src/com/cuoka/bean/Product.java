package com.cuoka.bean;

public class Product 
{
	private String name;
	private String shop;
	private String section;
	private double price;
	private String imageURL;
	
	public Product() {}
	
	public Product( String name
					, String shop
					, String section
					, double price
					, String imageURL )
	{
		this.name = name;
		this.shop = shop;
		this.section = section;
		this.price = price;
		this.imageURL = imageURL;
	}

	public String getName() { return name; }
	public String getShop() { return shop; 	}
	public String getSection() { return section; }
	public double getPrice() { return price; }
	public String getImageURL() { return imageURL; }

	public void setName( String name ) { this.name = name; }
	public void setShop( String shop ) {	this.shop = shop; 	}
	public void setSection( String section ) { this.section = section; }
	public void setPrice( double price ) { this.price = price; }
	public void setImageURL( String imageURL ) { this.imageURL = imageURL; 	}
}
