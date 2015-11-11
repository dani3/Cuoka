package com.wallakoala.wallakoala.Beans;

public class Image 
{
    private String url;
    private String pathSmallSize;
    private String pathLargeSize;
    
    public Image() {}

    public Image( String url
            , String pathSmallSize
            , String pathLargeSize )
    {
		this.url = url;
		this.pathSmallSize = pathSmallSize;
		this.pathLargeSize = pathLargeSize;
	}

	public String getUrl()           { return url; }
    public String getPathSmallSize() { return pathSmallSize; }
    public String getPathLargeSize() { return pathLargeSize; }

    public void setUrl( String url )            { this.url = url; }
    public void setPathSmallSize( String path ) { this.pathSmallSize = path; }
    public void setPathLargeSize( String path ) { this.pathLargeSize = path; }  
}
