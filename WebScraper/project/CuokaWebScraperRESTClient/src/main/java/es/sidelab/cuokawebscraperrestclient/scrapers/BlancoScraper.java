package es.sidelab.cuokawebscraperrestclient.scrapers;

import es.sidelab.cuokawebscraperrestclient.beans.ColorVariant;
import es.sidelab.cuokawebscraperrestclient.beans.Image;
import es.sidelab.cuokawebscraperrestclient.beans.Product;
import es.sidelab.cuokawebscraperrestclient.beans.Section;
import es.sidelab.cuokawebscraperrestclient.beans.Shop;
import es.sidelab.cuokawebscraperrestclient.properties.Properties;
import es.sidelab.cuokawebscraperrestclient.utils.ActivityStatsManager;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Scraper especifico para Blanco.
 * @author Daniel Mancebo Aldea
 */

public class BlancoScraper implements Scraper
{
    private static final Logger LOG = Logger.getLogger(BlancoScraper.class);
    
    // Lista preparada para la concurrencia donde escribiran todos los scrapers
    private static List<Product> productList = new CopyOnWriteArrayList<>();
    
    @Override
    public List<Product> scrap(Shop shop, Section section) throws IOException
    {        
        // Lista con los links de cada producto
        String htmlPath = section.getPath() + section.getName() + ".html";
        // Sacamos los links de cada producto
        List<String> productsLink = getListOfLinks(htmlPath, shop.getURL().toString());
        
        int prodOK = 0;
        int prodNOK = 0;
            
        for (String productLink : productsLink)
        {
            try 
            {
                LOG.info("Scraping: " + productLink);
                
                Document document = Jsoup.connect(productLink)
                                         .header("Accept-Language", "es")
                                         .timeout(Properties.TIMEOUT)
                                         .ignoreHttpErrors(true).get();

                // Obtener todos los atributos propios del producto
                String link = productLink;
                // El nombre se pone todo en mayusculas
                String name = document.select("h1.product-name").first().ownText()
                                                                          .toUpperCase(); 
                // De la referencia se quitan todos los caracteres no numericos
                String reference = document.select("p.product-number").first().ownText()
                                                                                .replaceAll("[^0-9]", "");
                // De la descripcion se cambian los saltos de linea por espacios
                String description = document.select("p.product-description").first().ownText()
                                                                                       .replaceAll("\n", " ");
                // El precio esta desglosado en la parte entera y la decimal
                String price = document.select("p.product-price").first().ownText()
                                                                           .replaceAll("[^0-9]", "");
                String decimals = document.select("p.product-price small").first().ownText()
                                                                                    .replaceAll(",", ".")
                                                                                    .trim();
                price = price + decimals;
                
                // En BD no podemos guardar un string de mas de 255 caracteres, si es mas grande lo acortamos
                if (description.length() > 255)
                    description = description.substring(0, 255);
                
                // Obtenemos los colores del producto
                boolean first = true;
                List<ColorVariant> variants = new ArrayList<>();
                
                // Hay dos product-color-selector repetidos, nos quedamos solo con uno
                Element colorList = document.select("div.product-color-selector").first();            
                Elements colors = colorList.select("span");
                for (Element color : colors)
                {
                    List<Image> imagesURL = new ArrayList<>();

                    String colorName = color.ownText().toUpperCase();

                    // De Blanco no podemos acceder a las imagenes de los colores alternativos, solo las del color principal
                    if (first)
                    {
                        Elements images = document.select("#product-gallery-list img");
                        for (Element img : images)
                            imagesURL.add(new Image(fixURL(shop.getURL().toString() + img.attr("src"))));

                        first = false;

                        variants.add(new ColorVariant(reference, colorName, null, imagesURL));
                    }
                }

                if (! colors.isEmpty())
                {
                    prodOK++;
                    
                    productList.add(new Product(Double.parseDouble(price)
                                            , name
                                            , shop.getName()
                                            , section.getName()
                                            , link 
                                            , description
                                            , section.isMan()
                                            , variants));
                } else
                    prodNOK++;
                
            } catch (Exception e) { 
                LOG.error("Excepcion en producto: " + productLink + " (" + e.toString() + ")");
                
                prodNOK++; 
                
            }
            
        } // for products
        
        ActivityStatsManager.updateProducts(shop.getName(), section, prodOK, prodNOK);
    
        return productList;
    }
    
    @Override
    public String fixURL(String url)
    {
        if (url.startsWith("//"))
            return "http:".concat(url).replace(" " , "%20");
        
        return url;
    } 
    
    @Override
    public List<String> getListOfLinks(String htmlPath, String shopUrl) throws IOException
    {
        List<String> links = new ArrayList<>();        
        
        File html = new File(htmlPath);
        Document document = Jsoup.parse(html, "UTF-8");
                  
        Elements products = document.select("div.products-list a");
        
        for(Element element : products)
        {
            links.add(fixURL(shopUrl + element.attr("href")));
        }
        
        return links;
    }
}
