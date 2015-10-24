package es.sidelab.cuokawebscraperrestserver.controller;

import es.sidelab.cuokawebscraperrestserver.beans.Product;
import es.sidelab.cuokawebscraperrestserver.beans.Shop;
import es.sidelab.cuokawebscraperrestserver.repositories.ProductsRepository;
import es.sidelab.cuokawebscraperrestserver.repositories.ShopsRepository;
import es.sidelab.cuokawebscraperrestserver.utils.ImageManager;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @class Controlador que proporcionara las URLs a los scrapers y tambien guardara los productos
 * @author Daniel Mancebo Aldea
 */

@RestController
public class Controller 
{
    private static final Log LOG = LogFactory.getLog( Controller.class );
    
    @Autowired
    ShopsRepository shopsRepository;
    
    @Autowired
    ProductsRepository productsRepository;
    
    /*
     * Metodo que añade una nueva tienda, si ya existe se devuelve un error 400
     */
    @RequestMapping( value = "/addShop", method = RequestMethod.POST )
    public ResponseEntity<Boolean> addShop( @RequestBody Shop shop )
    {      
        LOG.info( "Peticion POST recibida para añadir una nueva tienda..." );
        
        // Se devuelve error 400 si hay algun atributo incorrecto
        if ( checkShop( shop ) )
            return new ResponseEntity<>( HttpStatus.BAD_REQUEST );
        
        LOG.info( "Comprobando si existe la tienda..." );
        
        // Si existe ya la tienda, añadimos solo las URLs que no existan
        Shop currentShop = shopsRepository.findByName( shop.getName() );
        if ( currentShop != null ) 
        {
            LOG.info( "La tienda existe, se elimina de la BD..." );            
            shopsRepository.delete( currentShop );
            LOG.info( "Tienda eliminada correctamente" );
        }
        
        LOG.info( "Se añade la nueva tienda a la BD:\n" );  
        LOG.info( shop.toString() );
        shopsRepository.save( shop );          
        LOG.info( "Tienda insertada correctamente, saliendo del metodo addShop" );
        
        return new ResponseEntity<>( HttpStatus.CREATED );
    }
    
    /*
     * Metodo que devuelve una tienda dado su nombre
     */
    @RequestMapping( value = "/getShop/{name}", method = RequestMethod.GET )
    public Shop getShop( @PathVariable String name )
    {
        LOG.info( "Peticion GET para obtener la tienda: '" + name + "' recibida"  );
        Shop shop = shopsRepository.findByName( name );
        
        if ( shop != null )
            LOG.info( "Tienda encontrada:\n" + shop.toString() );
        else 
            LOG.info( "No se ha encontrado la tienda " + name );
        
        return shop;
    }
    
    /*
     * Metodo que devuelve una lista con todas las tiendas
     */
    @RequestMapping( value = "/getShops", method = RequestMethod.GET )
    public List<Shop> getShops()
    {
        LOG.info( "Peticion GET para obtener todas las tiendas recibida" );
        List<Shop> shops = shopsRepository.findAll();
        
        if ( ! shops.isEmpty() )
        {
            LOG.info( "Lista de tiendas encontradas:\n" );
            for ( Shop shop : shops )
                LOG.info( shop.toString() );
            
        } else {
            LOG.info( "No se ha encontrado ninguna tienda" );
        }
        
        return shops;
    }
    
    /*
     * Metodo que elimina los productos de la tienda e inserta los nuevos recibidos
     */
    @RequestMapping( value = "/addProducts/{shop}", method = RequestMethod.POST )
    public ResponseEntity<Boolean> addProducts( @RequestBody List<Product> products
                                        , @PathVariable String shop )
    {
        LOG.info( "Peticion POST para añadir productos recibida" );
        LOG.info( "Eliminando los productos existentes de la tienda " + shop );
        productsRepository.deleteByShop( shop );
        ImageManager.deleteProducts( shop );
        LOG.info( "Productos eliminados!" );
        
        LOG.info( "Insertando nuevos productos" );
        for ( Product product: products )
        {   
            // Guardamos el producto sin el path de la imagen, ya que no se ha descargado
            LOG.info( "Guardando el producto en BD" );
            productsRepository.save( product );
            
            // Descargamos la imagen y obtenemos el path en el HDD
            LOG.info( "Llamando a ImageManager para descargar la imagen" );
            String path = ImageManager.downloadImageFromURL( product );
            LOG.info( "Imagen descargada en la ruta: " + path );
            product.setImagePath( path );
            
            // Actualizamos el path en BD
            LOG.info( "Guardando el path en BD" );
            productsRepository.updateImagePath( product.getId(), path );
            LOG.info( "Producto guardado correctamente" );
        }
        LOG.info( "Productos de " + shop + " insertados correctamente" );        
        LOG.info( "Saliendo del metodo addShop" );
                
        return new ResponseEntity<>( HttpStatus.CREATED );
    }
    
    /*
     * Metodo que devuelve una lista de productos de una tienda
     */
    @RequestMapping( value = "/getProducts/{shop}", method = RequestMethod.GET )
    public List<Product> getProducts( @PathVariable String shop )
    {
        LOG.info( "Peticion GET para obtener todos los productos de " + shop );
        return productsRepository.findByShop( shop );
    }
    
    /*
     * Metodo que devuelve una lista de todos los productos
     */
    @RequestMapping( value = "/getProducts", method = RequestMethod.GET )
    public List<Product> getProducts()
    {
        LOG.info( "Peticion GET para obtener todos los productos" );
        return productsRepository.findAll();
    }
    
    /*
     * Metodo que devuelve true si hay algun atributo incorrecto
     */
    private boolean checkShop( Shop shop )
    {
        LOG.info( "Comprobando campos del JSON recibido..." );
        
        if ( ( shop.getName() == null ) ||
             ( shop.getUrl() == null ) ||
             ( shop.getName().equals( "" ) ) )
        {
            LOG.error( "ERROR: Uno de los campos está vacío" );            
            return true;
        }
        
        if ( shop.getSections().isEmpty() )
        {
            LOG.error( "ERROR: Tienda recibida sin secciones" );            
            return true;
        }
        
        LOG.info( "El JSON es correcto" );        
        return false;
    }
}
