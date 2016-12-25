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
import javax.annotation.Nullable;
import org.apache.log4j.Logger;

/**
 * Scraper especifico para Pedro Del Hierro.
 * @author Daniel Mancebo Aldea
 */

public class PdHcraper implements Scraper
{
    private static final Logger LOG = Logger.getLogger(PdHcraper.class);
    
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
        Runtime.getRuntime().exec(new String[] {"python"
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
        while (!_isFinished())
        {               
            // Empezamos nuevo producto
            Product product = _readProductGeneralInfo(br);
            if (product != null) 
            {
                // Si todo ha ido bien, seguimos
                product = _readProductColors(product, br);
                
                // Si todo ha ido bien, añadimos a la lista
                if ((product != null) && (!_containsProduct(productList, product.getColors().get(0).getReference()))) 
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
    
    /**
     * Metodo que lee los atributos básicos del producto.
     * @param br: BuffereReader
     * @return producto.
     * @throws IOException 
     */
    @Nullable
    private Product _readProductGeneralInfo(final BufferedReader br) throws IOException
    {
        String name        = br.readLine();
        String description = br.readLine();
        String price       = br.readLine();
        String discount    = br.readLine();
        String link        = br.readLine();
        
        // Podemos haber leido ya todos los productos, por lo que name puede ser null
        if (name == null || name.contains("null") || price.contains("null"))
        {
            return null;
        }
        
        Product product = new Product();
        
        discount = discount.replaceAll("Descuento: ", "");    
        
        double _price = Double.valueOf(price.replace("Precio: ", ""));
        double _discount = (discount.isEmpty()) ? 0.0f : Double.valueOf(discount);
        
        product.setName(name.replace("Nombre: ", ""));
        product.setDescription(description.replace("Descripcion: ", ""));
        product.setPrice(Math.max(_price, _discount));            
        product.setDiscount(Math.min(_price, _discount));
        product.setLink(fixURL(link.replace("Link: ", "")));
        
        return product;        
    }
    
    /**
     * Metodo que lee los colores y los inserta en el producto.
     * @param product: producto al que insertar los colores.
     * @param br: BufferedReader.
     * @return producto.
     * @throws IOException 
     */
    @Nullable
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
            if (colorName.contains("null") || reference.contains("null"))
            {
                correct = false;
                
                String line = br.readLine();
                if(!line.contains("****"))
                {
                    doneColor = true;
                }                        
            }
            
            if (correct)
            {
                color.setName(colorName.replace("  Color: ", ""));
                color.setColorURL(fixURL(colorIcon.replace("  Icono: ", ""))); 
                color.setReference(reference.replace("  Referencia: ", ""));

                // Leemos las imagenes
                boolean doneImages = false;
                while (!doneImages)
                {
                    String url = br.readLine();
                    if (url == null)
                    {
                        doneImages = true;
                        doneColor  = true;
                        
                        _setFinished(true);
                        
                    } else if (url.contains("***")) {
                        // Hemos acabado con las imágenes pero no con los colores
                        doneImages = true; 
                        
                    } else if (url.contains("------") || url.length() == 0) {
                        doneImages = true;
                        doneColor = true;
                        
                    } else {
                        Image image = new Image(fixURL(url.replace("     Imagen: ", "")));
                        images.add(image);
                    }                    
                }
                
                color.setImages(images);
                colors.add(color);
            }
        }
        
        if (colors.isEmpty()) 
        {
            return null;
        }
        
        product.setColors(colors);
        
        return product;
    }   
    
    /**
     * Metodo que corrige una url si es incorrecta. Codifica los espacios y añade la cabecera HTTP.
     * @param url: url a corregir.
     * @return url corregida.
     */
    @Override
    public String fixURL(String url)
    {
        if (url.startsWith("//"))
        {
            return "http:".concat(url).replace(" " , "%20");
        }
            
        return url.replace(" " , "%20");
    }
    
    private boolean _isFinished() 
    {
        return threadFinished.get();
    }

    private void _setFinished(Boolean value) 
    {
        threadFinished.set(value);
    }
    
    /**
     * Metodo que comprueba si el producto esta ya en la lista.
     * @param productList: lista de productos.
     * @param reference: producto a buscar.
     * @return true si el producto ya se encuentra en la lista.
     */
    private static boolean _containsProduct(List<Product> productList, String reference)
    {
        synchronized (productList)
        {
            for (Product p : productList)
            {
                for (ColorVariant cv : p.getColors())
                {
                    if (cv.getReference().equals(reference))
                    {
                        return true;
                    }
                }
            }            
        }
            
        return false;
    }
}
