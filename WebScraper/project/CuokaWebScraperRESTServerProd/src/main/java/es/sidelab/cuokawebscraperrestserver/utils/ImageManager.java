package es.sidelab.cuokawebscraperrestserver.utils;

import es.sidelab.cuokawebscraperrestserver.beans.ColorVariant;
import es.sidelab.cuokawebscraperrestserver.beans.Product;
import es.sidelab.cuokawebscraperrestserver.properties.Properties;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

/**
 * Clase que gestionara todo lo relacionado con las imagenes de los productos.
 * @author Daniel Mancebo Aldea
 */

public class ImageManager 
{
    private static final Log LOG = LogFactory.getLog(ImageManager.class);
    
    /**
     * Metodo que descarga las imagenes de los productos, las deja en la ruta predefinida
     * y actualiza los paths de los productos.
     * @param products: Lista de productos de la que se quieren descargar las imagenes.
     * @param shop: Tienda a la que pertenecen los productos.
     * @return Lista de productos con los paths actualizados.
     */
    public static List<Product> downloadImages(List<Product> products, String shop)
    {
        List<Product> productsUpdated = new ArrayList<>();
        
        // Creamos los directorios si es necesario
        FileManager.createProductsDirectory(shop);
        
        for (int i = 0; i < products.size(); i++) 
        {
            Product product = products.get(i);
            
            for (int j = 0; j < product.getColors().size(); j++)
            {
                ColorVariant cv = product.getColors().get(j);
                
                // Descargar las imagenes si es necesario
                if (cv.getImages() != null)
                {
                    for (int k = 0; k < cv.getImages().size(); k++)
                    {
                        String path = Properties.PRODUCTS + shop + "/" + shop + "_" + product.getSection() 
                                + "_" + cv.getReference() + "_" + cv.getName() + "_" + k + ".jpg";
                        String pathSmall = Properties.IMAGE_PATH + shop + "/" + shop + "_" + product.getSection() 
                                + "_" + cv.getReference() + "_" + cv.getName() + "_" + k + "_" + "Small.jpg";
                        LOG.info("[SCRAPER] Comprobando la imagen: " + path);

                        if (!FileManager.existsFile(pathSmall))
                        {
                            LOG.info("[SCRAPER] La imagen no existe, descargando");
                            boolean ok = downloadImage(cv.getImages().get(k).getUrl(), pathSmall.replaceAll("_Small" , ""));

                            if (ok)
                            {
                                LOG.info("[SCRAPER] Imagen descargada correctamente");
                                product.getColors().get(j)
                                        .getImages().get(k).setPath(path);
                                
                            } else {
                                product.getColors().get(j)
                                        .getImages().get(k).setPath(null);                                
                            }                            
                            
                        } else {
                            LOG.info("[SCRAPER] La imagen ya existe");
                            product.getColors().get(j)
                                        .getImages().get(k).setPath(path);                        
                        }   
                        
                    } // for images
                    
                    cv.setNumberOfImages((short)cv.getImages().size());
                    
                } // if images != null
                
                // Comprobamos que el link del color no este vacio
                if ((cv.getColorURL() != null) && (!cv.getColorURL().isEmpty()))
                {
                    // Descargar los iconos si es necesario
                    String path = Properties.COLOR_PATH + shop + "/" + shop + "_" + product.getSection() 
                                    + "_" + cv.getReference() + "_" + cv.getName().replaceAll(" " , "_") + "_ICON.jpg";
                    
                    if (!FileManager.existsFile(path))
                    {
                        boolean ok = downloadImage(cv.getColorURL(), path);
                        if (ok)
                        {
                            product.getColors().get(j).setPath("0");
                            
                        } else {
                            LOG.warn("[SCRAPER] URL del icono incorrecta. Se intenta averiguar el color...");
                    
                            String color_found = ColorManager.findOutColor(cv.getName());

                            if (color_found != null)
                            {
                                LOG.info("[SCRAPER] Color (" + cv.getName() +") encontrado!");
                                product.getColors().get(j).setPath(color_found);

                            } else {
                                LOG.info("[SCRAPER] Color (" + cv.getName() +") no encontrado");
                            }
                        }
                        
                    } else {
                        product.getColors().get(j).setPath("0");
                    }
                    
                } else {
                    LOG.warn("[SCRAPER] URL del icono vacio. Se intenta averiguar el color...");
                    
                    String color_path = ColorManager.findOutColor(cv.getName());
                    
                    if (color_path != null)
                    {
                        LOG.info("[SCRAPER] Color (" + cv.getName() +") encontrado!");
                        product.getColors().get(j).setPath(color_path);
                        
                    } else {
                        LOG.info("[SCRAPER] Color (" + cv.getName() +") no encontrado");                        
                    }     
                }
            } // for colors      
            
            productsUpdated.add(product);
            
        } // for products
        
        LOG.info("[SCRAPER] Todas las imagenes se han descargado correctamente, se reescalan");
        resizeImages(shop);    
        
        LOG.info("[SCRAPER] Se reescalan los iconos de los colores");
        resizeColors(shop);        
        
        return productsUpdated;
    }
    
    /**
     * Metodo que descarga una imagen y la almacena en la ruta especificada.
     * @param imageURL: url de la imagen a descargar.
     * @param path: path donde se quiere dejar la imagen.
     * @return true si todo ha ido correctamente.
     */
    private static boolean downloadImage(String imageURL, String path)
    {
        InputStream in;
                
        try 
        {            
            URL url = new URL(imageURL);
            
            in = new BufferedInputStream(url.openStream());
            
            Files.copy(in, Paths.get(path), StandardCopyOption.REPLACE_EXISTING);
                        
        } catch (MalformedURLException ex) {
            LOG.error("ERROR: Error al formar la URL de la imagen");
            LOG.error("URL: " + imageURL);
            LOG.error(ex.toString());
            
            return false;
            
        } catch (IOException ex) {
            LOG.error("ERROR: Error en la conexion");
            LOG.error(ex.toString());
            
            return false;
        }
                        
        return true;
    }
    
    /**
     * Metodo que devuelve el aspect ratio de las imagenes.
     * @param shop: tienda a la pertenecen las imágenes.
     * @return aspect ratio de la imagen.
     */
    public static float getAspectRatio(String shop)
    {
        float aspectRatio = 0.0f;
            
        if (shop.equalsIgnoreCase("Pedro Del Hierro"))
            aspectRatio = Properties.PDH_ASPECT_RATIO;

        if (shop.equalsIgnoreCase("Springfield"))
            aspectRatio = Properties.SPRINGFIELD_ASPECT_RATIO;

        if (shop.equalsIgnoreCase("HyM"))
            aspectRatio = Properties.HYM_ASPECT_RATIO;

        if (shop.equalsIgnoreCase("Zara"))
            aspectRatio = Properties.ZARA_ASPECT_RATIO;

        if (shop.equalsIgnoreCase("Bershka"))
            aspectRatio = Properties.BERSHKA_ASPECT_RATIO;

        if (shop.equalsIgnoreCase("Massimo Dutti"))
            aspectRatio = Properties.MASSIMO_DUTTI_ASPECT_RATIO;
        
        return aspectRatio;
    }
    
    /**
     * Metodo que llama a un script de python para reescalar las imagenes de una tienda.
     * @param shop: Nombre de la tienda de la que se quieren reescalar las imagenes.
     */
    private static void resizeImages(String shop)
    {
        try 
        {   
            double aspectRatio = getAspectRatio(shop);
            
            // El script tiene que estar en el mismo path que el jar
            Runtime.getRuntime().exec(new String[]{"sudo"
                , Properties.PYTHON_PATH
                , Properties.SCRIPTS_PATH + "resizeProducts.py"
                , Properties.IMAGE_PATH + shop
                , Double.toString(aspectRatio)
                , Integer.toString(Properties.IMAGE_HEIGHT_L)
                , Integer.toString(Properties.IMAGE_HEIGHT_S)});
            
        } catch (IOException ex) {
            LOG.error("ERROR: Error al ejecutar el script 'resizeProducts.py'");
            LOG.error(ex.getMessage());            
        }
    }
    
    /**
     * Metodo que llama a un script de python para reescalar los iconos de los colores.
     * @param shop: Nombre de la tienda de la que se quieren reescalar los iconos.
     */
    private static void resizeColors(String shop)
    {
        try 
        {      
            // El script tiene que estar en el mismo path que el jar
            Runtime.getRuntime().exec(new String[]{"sudo"
                , Properties.PYTHON_PATH
                , Properties.SCRIPTS_PATH + "resizeColors.py"
                , Properties.COLOR_PATH + shop
                , Integer.toString(Properties.ICON_WIDTH)
                , Integer.toString(Properties.ICON_HEIGHT)});
            
        } catch (IOException ex) {
            LOG.error("ERROR: Error al ejecutar el script 'resizeColors.py'");
            LOG.error(ex.getMessage());            
        }
    }
}
