/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.sidelab.cuokawebscraperrestclient.scrapers;

import es.sidelab.cuokawebscraperrestclient.beans.ColorVariant;
import es.sidelab.cuokawebscraperrestclient.beans.Image;
import es.sidelab.cuokawebscraperrestclient.beans.Product;
import es.sidelab.cuokawebscraperrestclient.beans.Section;
import es.sidelab.cuokawebscraperrestclient.beans.Shop;
import es.sidelab.cuokawebscraperrestclient.properties.Properties;
import static es.sidelab.cuokawebscraperrestclient.test.mainPdH.fixURL;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author lux_f
 */
public class PdHScraper implements Scraper {
    
    private static List<Product> productList = new CopyOnWriteArrayList<>();
    
    @Override
    public List<Product> scrap( Shop shop, Section section ) throws IOException 
    {
    Document document = Jsoup.connect( section.getURL().toString() )
                                    .timeout( Properties.TIMEOUT ).get();
          
        // Obtener los links a todos los productos. 
        // En este caso, los links estan en el 'a' que hay dentro de los li de un ul llamado product-listing.
        // Los links los guardamos en una lista de Element llamada 'products'.
        Elements products = document.select( "ul.product-listing li div.content_product > a" );
        
        // Comprobamos que hemos sacado bien los links. El link está en un atributo llamado 'href'.
        // Recorremos cada elemento de la lista, y con cada uno, mostramos el atributo 'href' con el metodo .attr("NOMBRE")
        // También sacamos el tamaño de la lista, asi nos aseguramos de que se han sacado todos los productos.
        System.out.println(products.size());
          
        // Recorremos todos los productos y sacamos sus atributos
        for ( Element element : products )
        {
            System.out.println("------------------------LEEMOS PRODUCTO------------------------------");
            // Obtener el HTML del producto conectandonos al link que hemos sacado antes (atributo 'href')
            document = Jsoup.connect( shop 
                            + element.attr( "href" ) ).timeout( Properties.TIMEOUT )
                                                      .ignoreHttpErrors( true ).get();

            // Obtener los atributos propios del producto
            String link = shop + element.attr( "href" );
            String name = document.select( "#product-information h1" ).first().ownText(); 
            String price = document.select( "strong.product-price span" ).first().ownText().replaceAll( "€", "" ).replaceAll( ",", "." ).trim();
            String reference = document.select( "div.m_tabs_cont p.patron" ).first().ownText().replaceAll("Ref:", "");
                        
            //Nos conectamos al link de cada producto
            Document doc = Jsoup.connect(link).timeout(Properties.TIMEOUT).ignoreHttpErrors(true).get();
            
           
            Elements prueba1 = doc.select("ul.product_colors ");
            Elements prueba2 = prueba1.select("li");
            Elements colors = prueba2.select("a img");
            
            List<ColorVariant> variants = new ArrayList<>();
                if(colors.size()>1){
                    int index = link.lastIndexOf("=");
                    String colorCode = link.substring(index+1);
                    colorCode = "color_".concat(colorCode);
                    
                    for(Element color : colors){
                        String colorName = color.attr("alt").toUpperCase();
                        String colorURL = fixURL(color.attr("src"));

                        //sacamos las imagenes correspondientes a cada color
                        List<Image> imagesURL = new ArrayList();
                        Elements images = doc.select("#product_image_list li");

                        for(Element img : images){
                            Set<String> set = img.classNames();
                            for(String classname : set){
                                if(classname.equals(colorCode)){
                                    imagesURL.add(new Image( fixURL(img.select("a img").first().attr("src"))));
                                    System.out.println(fixURL(img.select("a img").first().attr("src")));
                                }
                            }
                        }
                        variants.add( new ColorVariant( reference, colorName, colorURL, imagesURL ) );
                    }
                }
                /*Si solo hay un color se trata de forma distinta*/
                else{
                    List<Image> imagesURL = new ArrayList();
                    Elements images = doc.select("#product_image_list li");
                    String colorName = colors.attr("alt").toUpperCase();
                    String colorURL = fixURL(colors.attr("src"));
                    for(Element img : images){
                        imagesURL.add(new Image( fixURL(img.select("a img").first().attr("src"))));  
                    }
                    variants.add( new ColorVariant( reference, colorName, colorURL, imagesURL ) );
                }
            productList.add( new Product( Double.parseDouble( price )
                                    , name
                                    , ""
                                    , ""
                                    , link 
                                    , true
                                    , variants ) );
        }
        return productList;
    }
    
    
    
    
    
    
    public String fixURL( String url )
    {
        if ( url.startsWith( "//" ) )
            return "http:".concat( url ).replace( " " , "%20" );
        
        return url.replace( " " , "%20" );
    }    
    
    /*
     * Metodo que devuelve true si el producto esta ya en la lista
     */
    private boolean containsProduct( List<Product> productList, String reference )
    {
        for ( Product p : productList )
            for ( ColorVariant cv : p.getColors() )
                if ( cv.getReference().equals( reference ) )
                    return true;
        
        return false;
    }
}
