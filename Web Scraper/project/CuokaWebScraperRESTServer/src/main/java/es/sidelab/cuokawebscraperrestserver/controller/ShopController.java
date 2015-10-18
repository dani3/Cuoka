package es.sidelab.cuokawebscraperrestserver.controller;

import es.sidelab.cuokawebscraperrestserver.beans.BeanProduct;
import es.sidelab.cuokawebscraperrestserver.beans.BeanShop;
import es.sidelab.cuokawebscraperrestserver.repositories.ProductsRepository;
import es.sidelab.cuokawebscraperrestserver.repositories.ShopsRepository;
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
public class ShopController 
{
    private static final Log LOG = LogFactory.getLog( ShopController.class );
    
    @Autowired
    ShopsRepository shopsRepository;
    
    @Autowired
    ProductsRepository productsRepository;
    
    /*
     * Metodo que añade una nueva tienda, si ya existe se devuelve un error 400
     */
    @RequestMapping( value = "/addShop", method = RequestMethod.POST )
    public ResponseEntity<Boolean> addShop( @RequestBody BeanShop shop )
    {      
        LOG.info( "Peticion POST recibida para añadir una nueva tienda..." );
        
        // Se devuelve error 400 si hay algun atributo incorrecto
        if ( checkShop( shop ) )
            return new ResponseEntity<>( HttpStatus.BAD_REQUEST );
        
        LOG.info( "Comprobando si existe la tienda..." );
        
        // Si existe ya la tienda, añadimos solo las URLs que no existan
        BeanShop currentShop = shopsRepository.findByName( shop.getName() );
        if ( currentShop != null ) 
        {
            LOG.info( "La tienda existe, se elimina de la BD..." );            
            shopsRepository.delete( currentShop );
            LOG.info( "Tienda eliminada correctamente" );
        }
        
        LOG.info( "Se añade la nueva tienda a la BD..." );        
        shopsRepository.save( shop );          
        LOG.info( "Tienda insertada correctamente, saliendo del metodo addShop" );
        
        return new ResponseEntity<>( HttpStatus.CREATED );
    }
    
    /*
     * Metodo que devuelve una tienda dado su nombre
     */
    @RequestMapping( value = "/getShop/{name}", method = RequestMethod.GET )
    public BeanShop getShop( @PathVariable String name )
    {
        LOG.info( "Peticion GET para obtener la tienda: '" + name + "' recibida"  );
        return shopsRepository.findByName( name );
    }
    
    /*
     * Metodo que devuelve una lista con todas las tiendas
     */
    @RequestMapping( value = "/getShops", method = RequestMethod.GET )
    public List<BeanShop> getShops()
    {
        LOG.info( "Peticion GET para obtener todas las tiendas recibida" );
        return shopsRepository.findAll();
    }
    
    /*
     * Metodo que elimina los productos de la tienda e inserta los nuevos recibidos
     */
    @RequestMapping( value = "/addProducts/{shop}", method = RequestMethod.POST )
    public ResponseEntity<Boolean> addProducts( @RequestBody List<BeanProduct> products
                                        , @PathVariable String shop )
    {
        LOG.info( "Peticion POST para añadir productos recibida" );
        LOG.info( "Eliminando los productos existentes de la tienda " + shop );
        productsRepository.deleteByShop( shop );
        LOG.info( "Productos eliminados!" );
        
        LOG.info( "Insertando nuevos productos" );
        for ( BeanProduct product: products )
            productsRepository.save( product );
        LOG.info( "Productos insertados correctamente!" );
        
        return new ResponseEntity<>( HttpStatus.CREATED );
    }
    
    /*
     * Metodo que devuelve una lista de productos de una tienda
     */
    @RequestMapping( value = "/getProducts/{shop}", method = RequestMethod.GET )
    public List<BeanProduct> getProducts( @PathVariable String shop )
    {
        LOG.info( "Peticion GET para obtener todas los productos de la tienda " + shop );
        return productsRepository.findAll();
    }
    
    /*
     * Metodo que devuelve true si hay algun atributo incorrecto
     */
    private boolean checkShop( BeanShop shop )
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
