package es.sidelab.cuokawebscraperrestclient.scrapers;

import es.sidelab.cuokawebscraperrestclient.beans.ColorVariant;
import es.sidelab.cuokawebscraperrestclient.beans.Image;
import es.sidelab.cuokawebscraperrestclient.beans.Product;
import es.sidelab.cuokawebscraperrestclient.beans.Section;
import es.sidelab.cuokawebscraperrestclient.beans.Shop;
import es.sidelab.cuokawebscraperrestclient.properties.Properties;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * Scraper especifico para Blanco.
 * @author lucittro
 */

public class BlancoScraper implements Scraper
{
    private static final Logger LOG = Logger.getLogger(BlancoScraper.class);
    
    // Lista preparada para la concurrencia donde escribiran todos los scrapers.
    private static List<Product> productList = Collections.synchronizedList(new ArrayList<>());
    
    // Atributo local para comprobar que se ha terminado.
    private final ThreadLocal<Boolean> threadFinished = 
            new ThreadLocal<Boolean>() 
            {
                @Override 
                protected Boolean initialValue() 
                {
                    return false;
                }
            };
    
    @Override
    public List<Product> scrap(Shop shop, Section section) throws IOException
    {        
        threadFinished.set(false);
        
        LOG.info("Se inicia el scraping de la seccion " + section.getName() + " de la tienda " + shop.getName());
        
        // Ejecutamos el script que crea el fichero con todos los productos.
        Process process = Runtime.getRuntime().exec(new String[] {"python"
                                , section.getPath() + "renderProducts.py"
                                , Properties.CHROME_DRIVER
                                , section.getName()
                                , section.getPath()});
        
        // Nos quedamos esperando hasta que termine.
        File file = new File(section.getPath() + section.getName() + "_done.dat");
        while (!file.exists()) 
        {
            file = new File(section.getPath() + section.getName() + "_done.dat");
        }

        file.delete();
        
        // Una vez ha terminado de generar el fichero de productos, lo leemos.
        BufferedReader br = new BufferedReader(
            new FileReader(new File(section.getPath() + section.getName() + "_products.txt")));
               
        br.readLine();
        while(!get())
        {   
            // Empezamos nuevo producto
            Product product = _readProductGeneralInfo(br);
            if (product != null) //todo ha ido bien, seguimos leyendo los colores
            {
                product = _readProductColors(product, br);
                
                // Si todo ha ido bien, añadimos a la lista
                if ((product != null) && (!containsProduct(productList, product.getColors().get(0).getReference()))) 
                {
                    product.setShop(shop.getName());
                    product.setSection(section.getName());
                    product.setMan(section.isMan());
                    
                    productList.add(product);
                }
            }
        }
        
        LOG.info("El scraping de la seccion " + section.getName() + " de la tienda " + shop.getName() + " ha terminado");
        LOG.info("Ha sacado " + productList.size() + " productos");
        
        return productList;
    }
    
    private Product _readProductGeneralInfo(BufferedReader br) throws IOException
    {
        String name = br.readLine();
        String description = br.readLine();
        String price = br.readLine();
        String link = br.readLine();
        
        // Podemos haber leido ya todos los productos, por lo que name puede ser null
        if (name == null || name.contains("null") || price.contains("null"))
        {
            return null;
        }
        
        Product product = new Product();
        
        product.setName(name.replace("Nombre: ", ""));
        product.setDescription(description.replace("Descripcion: ", ""));
        product.setPrice(Double.valueOf(price.replace("Precio: ", "")));
        product.setLink(fixURL(link.replace("Link: ", "")));
        
        return product;        
    }
    
    private Product _readProductColors(Product product, BufferedReader br) throws IOException
    {
        
        List<ColorVariant> colors = new ArrayList<>();
        boolean doneColor = false;
        
        // Leemos los asteriscos
        br.readLine();       
        while (!doneColor)
        {
            boolean correct = true;
            
            ColorVariant color = new ColorVariant();
            List<Image> images = new ArrayList<>();
            
            String colorName = br.readLine();
            String colorIcon = br.readLine();
            String reference = br.readLine();
            if(colorName.contains("null") || reference.contains("null"))
            {
                correct = false;
                
                String line = br.readLine();
                if (!line.contains("******"))
                {
                    doneColor = true;
                }
            }
            
            if (correct)
            {
                color.setName(colorName.replace("  Color: ", ""));
                color.setColorURL(null);            
                color.setReference(reference.replace("  Referencia: ", ""));

                // Leemos las imagenes
                boolean doneImages = false;
                while (!doneImages)
                {
                    String url = br.readLine();
                    if (url == null){
                        doneImages = true;
                        doneColor = true;
                        set(true);
                    }
                    else if (url.contains("***")){
                        /*hemos acabado con las imágenes pero no con los colores*/
                        doneImages = true; 
                    } 
                    else if (url.contains("------") || url.length() == 0) //producto final ==0
                    {
                        doneImages = true;
                        doneColor = true;
                    }
                    else {
                        Image image = new Image(fixURL(url.replace("     Imagen: ", "")));
                        images.add(image);
                    }

                }

                color.setImages(images);
                colors.add(color);      
            }
        }
        
        product.setColors(colors);
        return product;
    }
    
    @Override
    public String fixURL(String url)
    {
        if (url.startsWith("//"))
            return "http:".concat(url).replace(" " , "%20");
        
        return url.replace(" " , "%20");
    }
    
    private boolean get() 
    {
        return threadFinished.get();
    }

    private void set(Boolean value) 
    {
        threadFinished.set(value);
    }
    
    private static boolean containsProduct(List<Product> productList, String reference)
    {
        synchronized (productList)
        {
            for (Product p : productList)
                for (ColorVariant cv : p.getColors())
                    if (cv.getReference().equals(reference))
                        return true;
        }
            
        return false;
    }
}
