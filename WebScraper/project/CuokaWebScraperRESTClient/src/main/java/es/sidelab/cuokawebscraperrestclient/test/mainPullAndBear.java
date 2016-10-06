package es.sidelab.cuokawebscraperrestclient.test;

import es.sidelab.cuokawebscraperrestclient.beans.ColorVariant;
import es.sidelab.cuokawebscraperrestclient.beans.Image;
import es.sidelab.cuokawebscraperrestclient.beans.Product;
import es.sidelab.cuokawebscraperrestclient.properties.Properties;
import es.sidelab.cuokawebscraperrestclient.utils.Printer;
import es.sidelab.cuokawebscraperrestclient.utils.PythonManager;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Esteban Hidalgo Sancho
 */

public class mainPullAndBear 
{        
    public static void main(String[] args) throws Exception 
    {
        String url = "http://www.pullandbear.com/es/es/";
        String sectionName = "Jeans";
        String path = "C:\\Users\\Dani\\Documents\\shops\\Pull&Bear_true\\false\\";
        List<Product> productList = new ArrayList<>();
        
        List<String> productsLink = getListOfLinks(path + sectionName + ".html" , url);

        // Recorremos todos los productos y sacamos sus atributos
        for (String productLink : productsLink)
        {
            String pathProduct = "C:\\Users\\Dani\\Documents\\shops\\Pull&Bear_true\\false\\Jeans_PRODUCTO.html";
            
            try 
            {
                // Lista para introducir cada color y lo relacionado a él
                List<ColorVariant> variants = new ArrayList<>();

                File file = PythonManager.executeRenderProduct(productLink, path, pathProduct);
            
                Document document = Jsoup.parse(file, "UTF-8");

                // Obtener los atributos propios del producto
                String link = productLink;
                String name = document.select("h1.prodMainhead").first().ownText(); 
                String priceInt = document.select("div.product_price span.integer").first().ownText();
                String priceDecs = document.select("div.product_price span.decimals").first().ownText().replaceAll(",", ".");
                String price = priceInt + priceDecs;
                String reference = document.select("#Product_Ref").first().ownText().replaceAll("Ref.", "").trim();
                String description = "";
                if (document.select("#Product_Desc").hasText())
                    description = document.select("#Product_Desc").first().ownText();                 
                                
                if (description.length() > 255)
                    description = description.substring(0, 255);                

                // Se cogen los colores
                Elements colors = document.select("#Product_ColorContainer > div");
                  
                //Si hay varios colores
                if (colors.size() > 1)
                {                    
                    // este bool se usa para coger el color por defecto en caso de que haya más de uno  
                    boolean first = true;
                    String colorRefDefault = "";
                    
                    for (Element color : colors)
                    {
                        List<Image> imagesURL = new ArrayList<>();
                        
                        Elements e = color.select("div[title]");
                        
                        String colorName = e.attr("title"); 
                        String colorUrl = e.attr("style").replace("background-image: url(\"","").replace("\");",""); 
                       
                        int colorRefLength = reference.length();
                        String colorRef =  colorUrl.substring((colorUrl.indexOf(reference ) + colorRefLength)).replaceAll("_(.*)", "");
                        
                        // se selecciona el color por defecto
                        if (first)
                        {
                            colorRefDefault = colorRef;
                            first = false;        
                        }                  

                        // se obtienen las imágenes del producto
                        Elements images = document.select("#Product_ImagesContainer > img");
                        
                        for (Element image : images)
                        {
                            Elements im = image.select("img[src]");
                            String imageUrl = im.attr("src");
                            String imageUrlMod;
                            
                            // cuando el color es otro del inicial, se modifica la url de las imágenes
                            int comp = colorRef.compareTo(colorRefDefault);
                            
                            if (comp != 0)
                                imageUrlMod = imageUrl.replaceAll(colorRefDefault, colorRef);
                            else 
                                imageUrlMod = imageUrl;
                                                        
                            imagesURL.add(new Image(fixURL(imageUrlMod)));                    


                        } // for images
                     
                        variants.add(new ColorVariant(colorRef, colorName, colorUrl, imagesURL));
                        
                    } // for colors

                } else { // si solo hay un color...
                      
                    List<Image> imagesURL = new ArrayList<>();

                    Element color = colors.first();

                    Elements e = color.select("div[title]");

                    String colorName = e.attr("title"); 
                    String colorUrl = e.attr("style").replace("background-image: url(\"","").replace("\");",""); 

                    int colorRefLength = reference.length();
                    String colorRef =  colorUrl.substring((colorUrl.indexOf(reference ) + colorRefLength)).replaceAll("_(.*)", ""); 

                    //imágenes del producto
                    Elements images = document.select("#Product_ImagesContainer > img");

                    for (Element image : images)
                    {
                        Elements im = image.select("img[src]");
                        String imageUrl = im.attr("src"); 

                        imagesURL.add(new Image(fixURL(imageUrl )));                    
                    } // for images

                    variants.add(new ColorVariant(colorRef, colorName, colorUrl, imagesURL));
                }                  

                // Si el producto es nuevo, se inserta directamente, si no, se actualiza con el nuevo color
                if (! containsProduct(productList, reference))
                {
                    productList.add(new Product(Double.parseDouble(price)
                                            , name
                                            , ""
                                            , ""
                                            , link 
                                            , description
                                            , true
                                            , variants));                
                } else {
                    // Buscamos el producto
                    for (Product product : productList)
                    {
                        for (int i = 0; i < product.getColors().size(); i++)
                        {
                            ColorVariant color = product.getColors().get(i);

                            if (color.getReference().equals(reference))
                            {
                                product.getColors().addAll(variants);                            
                                break;
                            }                        
                        }
                    }
                }
                
            } catch (Exception ex) { ex.printStackTrace(); }
            
        } // for products 
        
        System.out.println(productList.size());
        
        Product p = productList.get(0);
        
        System.out.println("-------- INFO PRODUCTO ----------");
        System.out.println("Nombre: " + p.getName());
        System.out.println("Link: " + p.getLink());
        System.out.println("Descripcion: " + p.getDescription());
        System.out.println("Precio: " + p.getPrice() + " €");
        System.out.println("-------- INFO COLORES -----------");
        for (ColorVariant cv : p.getColors())
        {
            System.out.println(" - Color: " + cv.getName());
            System.out.println(" - Icono: " + cv.getColorURL());
            System.out.println(" - Referencia: " + cv.getReference());
            for (Image image : cv.getImages())
                System.out.println(" - " + image.getUrl());
            
            System.out.println("\n");            
        }

        
    } // main    
    
    public static String fixURL(String url)
    {
        if (url.startsWith("//"))
            return "http:".concat(url).replace(" " , "%20");
        
        return url;
    }     
     
    private static boolean containsProduct(List<Product> productList, String reference)
    {
        for (Product p : productList)
            for (ColorVariant cv : p.getColors())
                if (cv.getReference().equals(reference))
                    return true;
        
        return false;
    }
    
    private static List<String> getListOfLinks(String htmlPath, String shopUrl) throws IOException
    {
        List<String> links = new ArrayList<>();        
        
        File html = new File(htmlPath);
        Document document = Jsoup.parse(html, "UTF-8");
                  
        Elements products = document.select("a.grid_itemContainer");
        
        for(Element element : products)
        {
            links.add(fixURL(element.attr("href")));
        }
        
        return links;
    }

} // class mainPandB
