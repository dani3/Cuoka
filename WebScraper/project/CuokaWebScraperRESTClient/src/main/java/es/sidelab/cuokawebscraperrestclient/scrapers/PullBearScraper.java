package es.sidelab.cuokawebscraperrestclient.scrapers;

import es.sidelab.cuokawebscraperrestclient.beans.ColorVariant;
import es.sidelab.cuokawebscraperrestclient.beans.Image;
import es.sidelab.cuokawebscraperrestclient.beans.Product;
import es.sidelab.cuokawebscraperrestclient.beans.Section;
import es.sidelab.cuokawebscraperrestclient.beans.Shop;
import es.sidelab.cuokawebscraperrestclient.properties.Properties;
import es.sidelab.cuokawebscraperrestclient.utils.ScrapingAnalyzer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

/**
 * Scraper especifico para Pedro Del Hierro.
 * @author Daniel Mancebo Aldea
 */

public class PullBearScraper implements Scraper
{
    private static final Logger LOG = Logger.getLogger(PullBearScraper.class);
    
    // Lista preparada para la concurrencia donde escribiran todos los scrapers.
    @SuppressWarnings("FieldMayBeFinal")
    private static List<Product> productList = Collections.synchronizedList(new ArrayList<>());
    
    // Lista preparada para la concurrencia donde se guardaran las estadisticas de los scrapers.
    @SuppressWarnings("FieldMayBeFinal")
    private static List<ScrapingAnalyzer> scrapingAnalyzerList = Collections.synchronizedList(new ArrayList<>());
    
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
    
    // Analizador que almacena los resultados del proceso de scraping.
    private ThreadLocal<ScrapingAnalyzer> scrapingAnalyzer;
    
    @Override
    public Map<String, Object> scrap(Shop shop, Section section) throws IOException
    {        
        scrapingAnalyzer = 
            new ThreadLocal<ScrapingAnalyzer>() 
            {
                @Override 
                protected ScrapingAnalyzer initialValue() 
                {
                    return new ScrapingAnalyzer(shop, section);
                }
            };
        
        threadFinished.set(false);
        
        LOG.info("Se inicia el scraping de la seccion " + section.getName() + "(" 
            + ((section.isMan()) ? "Hombre)" : "Mujer)") + " de la tienda " + shop.getName());
        
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
            new FileReader(new File(section.getPath() + "Productos_" + section.getName() + ".txt")));
               
        br.readLine();
        int count = 0;
        while (!_isFinished())
        {               
            try
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

                        count++;
                    }
                }
                
            } catch (Exception e) {
                LOG.error("Excepcion producida en el scraping de la seccion " + section.getName() + "(" 
                        + ((section.isMan()) ? "Hombre)" : "Mujer)") + " de la tienda " + shop.getName());
                
                e.printStackTrace();
            }
        }
        
        scrapingAnalyzerList.add(scrapingAnalyzer.get());
        
        LOG.info("El scraping de la seccion " + section.getName() + "(" 
            + ((section.isMan()) ? "Hombre)" : "Mujer)") + " de la tienda " + shop.getName() + " ha terminado");
        LOG.info("Ha sacado " + count + " productos");
        
        Map<String, Object> map = new HashMap<>();
        
        map.put(Properties.KEY_LIST, productList);
        map.put(Properties.KEY_ANALYZER, scrapingAnalyzerList);
        
        return map;
    }
    
    /**
     * Metodo que lee los atributos básicos del producto.
     * @param br: BuffereReader
     * @return producto.
     * @throws IOException 
     */
    @Nullable
    private Product _readProductGeneralInfo(BufferedReader br) throws IOException
    {
        Product product = new Product();
        
        String name;
        String description;
        String price;
        String discount;
        String link;
        
        // Leemos el nombre.
        name = br.readLine();        
        // Podemos haber leido ya todos los productos, por lo que name puede ser null
        // o podemos leer la linea de guiones si la conexion ha fallado.
        if (name == null || name.contains("null") || name.contains("---------"))
        {
            if ((name != null) && name.contains("null"))
            {
                scrapingAnalyzer.get().saveError(Properties.NAME_NOT_FOUND);
                
                // Leemos la linea de guiones
                br.readLine();
            }
            
            if ((name != null) && name.contains("---------"))
            {
                // Leemos la linea de guiones
                br.readLine();
            }
            
            // Si leemos el EOF marcamos finished a true.
            if (name == null)
            {
                _setFinished(true);
            }
            
            return null;
        }
        
        // Leemos la descripcion y el precio.
        description = br.readLine();
        price       = br.readLine();      
        
        if (price == null || price.contains("null"))
        {
            if (price == null)
            {
                return null;
            }
            
            scrapingAnalyzer.get().saveError(Properties.PRICE_NOT_FOUND);
            
            // Leemos la linea de guiones
            br.readLine();
            
            return null;
        }
        
        // Leemos el descuento y el link.
        discount = br.readLine();
        link     = br.readLine();
        
        // Eliminamos las cabeceras.
        name        = name.replace("Nombre: ", "");
        description = description.replace("Descripcion: ", "");
        discount    = discount.replace("Descuento: ", "").trim();   
        price       = price.replace("Precio: ", "").trim();
        link        = link.replace("Link: ", "");
        
        if (description.isEmpty())
        {
            scrapingAnalyzer.get().saveError(Properties.DESCRIPTION_NOT_FOUND);
        }
        
        // Eliminamos el primer '.' en caso de que el precio supere los 1000 euros.
        if (StringUtils.countOccurrencesOf(price, ".") > 1)
        {
            price = price.replace(".", "");
        }
        
        // Lo mismo con el descuento.
        if (StringUtils.countOccurrencesOf(discount, ".") > 1)
        {
            discount = discount.replace(".", "");
        }
        
        double _price;
        double _discount;
        
        try
        {
            _price = Double.valueOf(price);
            
        } catch (Exception e) {
            return null;
        }
        
        try
        {
            _discount = (discount.isEmpty()) ? 0.0f : Double.valueOf(discount);
            
        } catch (Exception e) {
            _discount = 0.0f;
        }
        
        product.setName(name);
        product.setDescription(description);
        
        if (_discount > 0.0f)
        {
            product.setPrice(Math.min(_price, _discount));            
            product.setDiscount(Math.max(_price, _discount));
        } else {
            product.setPrice(_price);            
            product.setDiscount(_discount);
        }
        
        product.setLink(fixURL(link));
        
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
            
            List<Image> images = new ArrayList<>();
            ColorVariant color = new ColorVariant();
            
            String colorName = br.readLine();
            String colorIcon = br.readLine();
            String reference = br.readLine();
            if (colorName == null || reference == null || colorName.contains("null") || reference.contains("null"))
            {
                correct = false;
                
                if (colorName.contains("null"))
                {
                    scrapingAnalyzer.get().saveError(Properties.COLOR_NAME_NOT_FOUND);
                }
                
                if (reference.contains("null"))
                {
                    scrapingAnalyzer.get().saveError(Properties.REFERENCE_NOT_FOUND);
                }
                
                String line = br.readLine();                
                // Podemos llegar al final de fichero.
                if (line == null)
                {
                    _setFinished(true);
                    break;
                }
                
                // O podemos llegar al siguiente producto.
                if (!line.contains("******"))
                {
                    doneColor = true;
                }
            }
            
            if (correct)
            {
                color.setName(colorName.replace("  Color: ", ""));
                color.setColorURL(colorIcon.replace("  Icono: ", ""));  
                color.setReference(reference.replace("  Referencia: ", ""));

                // Leemos las imagenes
                boolean doneImages = false;
                while (!doneImages)
                {
                    String url = br.readLine();
                    if (url == null)
                    {
                        // Si la url es null, es que hemos llegado al final del fichero.
                        doneImages = true;
                        doneColor  = true;
                        
                        _setFinished(true);

                    } else if (url.contains("***")) {
                        // Hemos acabado con las imágenes pero no con los colores
                        doneImages = true; 

                    } else if (url.contains("------") || url.length() == 0) {
                        // Producto final == 0
                        doneImages = true;
                        doneColor = true;

                    } else {
                        if (!url.replace("     Imagen: ", "").isEmpty() && !url.contains("null"))
                        {
                            Image image = new Image(fixURL(url.replace("     Imagen: ", "")));
                            images.add(image);
                            
                        } else {
                            scrapingAnalyzer.get().saveError(Properties.IMAGE_NOT_FOUND);
                        } 
                    }
                }

                if (images != null && !images.isEmpty())
                {
                    color.setImages(images);
                    colors.add(color);  
                }
            }
        }
        
        // Nos aseguramos de que no insertamos productos sin ningun color.
        if (colors.isEmpty()) 
        {
            scrapingAnalyzer.get().saveError(Properties.NO_COLORS);
            
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
