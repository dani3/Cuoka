package es.sidelab.cuokawebscraperrestserver.controller;

import es.sidelab.cuokawebscraperrestserver.beans.ColorVariant;
import es.sidelab.cuokawebscraperrestserver.beans.HistoricProduct;
import es.sidelab.cuokawebscraperrestserver.beans.Product;
import es.sidelab.cuokawebscraperrestserver.beans.Shop;
import es.sidelab.cuokawebscraperrestserver.repositories.HistoricProductsRepository;
import es.sidelab.cuokawebscraperrestserver.repositories.ProductsRepository;
import es.sidelab.cuokawebscraperrestserver.repositories.ShopsRepository;
import es.sidelab.cuokawebscraperrestserver.utils.ImageManager;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @class Controlador que proporcionara las URLs a los scrapers y tambien guardara los productos.
 * @author Daniel Mancebo Aldea
 */

@RestController
public class Controller 
{
    private static final Log LOG = LogFactory.getLog( Controller.class );
    
    @Autowired
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool( 1 );
    
    @Autowired
    ShopsRepository shopsRepository;
    
    @Autowired
    ProductsRepository productsRepository;
    
    @Autowired
    HistoricProductsRepository historicProductsRepository;
    
    /**
     * Metodo que anade una nueva tienda, si ya existe se devuelve un error 400.
     * @param shop: Tienda a anadir.
     * @return Codigo HTTP con el resultado de la ejecucion.
     */
    @RequestMapping( value = "/addShop", method = RequestMethod.POST )
    public ResponseEntity<Boolean> addShop( @RequestBody Shop shop )
    {      
        LOG.info( "Peticion POST recibida para anadir una nueva tienda..." );
        
        // Se devuelve error 400 si hay algun atributo incorrecto
        if ( ! shop.isOkay() )
            return new ResponseEntity<>( HttpStatus.BAD_REQUEST );
        
        LOG.info( "Comprobando si existe la tienda..." );
        
        // Si existe ya la tienda, anadimos solo las URLs que no existan
        Shop currentShop = shopsRepository.findByName( shop.getName() );
        if ( currentShop != null ) 
        {
            LOG.info( "La tienda existe, se elimina de la BD..." );            
            shopsRepository.delete( currentShop );
            LOG.info( "Tienda eliminada correctamente" );
        }
        
        LOG.info( "Se anade la nueva tienda a la BD:\n" );  
        LOG.info( shop.toString() );
        shopsRepository.save( shop );          
        LOG.info( "Tienda insertada correctamente, saliendo del metodo addShop" );
        
        return new ResponseEntity<>( HttpStatus.CREATED );
    }
    
    /*
     * Metodo que devuelve una tienda dado su nombre.
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
    
    /**
     * Metodo que devuelve una lista con todas las tiendas.
     * @return Lista con todas las tiendas.
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
    
    /**
     * Metodo que elimina los productos de la tienda e inserta los nuevos recibidos.
     * @param products: Lista de los productos a insertar.
     * @param shop: Tienda a la que pertenecen los productos.
     * @return Codigo HTTP con el resultado de la ejecucion.
     */
    @CacheEvict( value = "products", key = "#shop" )
    @RequestMapping( value = "/addProducts/{shop}", method = RequestMethod.POST )
    public ResponseEntity<Boolean> addProducts( @RequestBody List<Product> products
                                        , @PathVariable String shop )
    {
        LOG.info( "Peticion POST para anadir productos recibida" );
        
        Runnable task = () -> {
            LOG.info( "Eliminando los productos existentes de la tienda " + shop );
            List<Product> productsToBeRemoved = productsRepository.findByShop( shop );
            for ( Product product : productsToBeRemoved )
                productsRepository.delete( product.getId() );

            LOG.info( "Productos eliminados!" );

            LOG.info( "Insertando nuevos productos" );
            LOG.info( "Llamando a ImageManager para descargar las imagenes que no existan " );
            List<Product> productsUpdated = ImageManager.downloadImages( products, shop );
            for ( Product product : productsUpdated )
            {
                boolean newness = false;
                Calendar insertDate = Calendar.getInstance();

                // Comprobamos si el producto se ha insertado anteriormente, si no es asi, se considera novedad
                for ( ColorVariant cv : product.getColors() )
                {
                    insertDate = historicProductsRepository.getInsertDateByReference( shop
                                                    , product.getSection()
                                                    , cv.getReference()
                                                    , cv.getColorName() );


                    if ( insertDate == null )
                    {
                        historicProductsRepository.save( new HistoricProduct( shop
                                                                , product.getSection()
                                                                , cv.getReference() 
                                                                , cv.getColorName() 
                                                                , Calendar.getInstance() ) );

                        newness = true;
                    }               
                }

                product.setNewness( newness );
                if ( newness )            
                    product.setInsertDate( Calendar.getInstance() );

                else 
                    product.setInsertDate( insertDate );

                productsRepository.save( product );
            }
            
            LOG.info( "Todas las imagenes han sido reescaladas correctamente" );
            LOG.info( "Todos los iconos han sido reescalados correctamente" );
            
            LOG.info( "Productos de " + shop + " insertados correctamente" );        
            LOG.info( "Saliendo del metodo addShop" );
        };
        
        EXECUTOR.execute( task );       
                
        return new ResponseEntity<>( HttpStatus.CREATED );
    }
    
    /**
     * Metodo que devuelve una lista de productos de una tienda.
     * @param shop: Tienda de la que se quieren los productos.
     * @param man: true si se quiere solo los products de hombre.
     * @return Lista de productos.
     */
    @Cacheable( value = "products", key = "#shop" )
    @RequestMapping( value = "/getProducts/{shop}/{man}", method = RequestMethod.GET )
    public List<Product> getProducts( @PathVariable String shop, @PathVariable boolean man )
    {
        LOG.info( "Peticion GET para obtener todos los productos de " + shop );
        return productsRepository.findByShopAndGender( shop, man );
    }
    
    /**
     * Metodo que devuelve una lista de todos los productos.
     * @return Lista de todos los productos de todas las tiendas.
     */
    @RequestMapping( value = "/getProducts", method = RequestMethod.GET )
    public List<Product> getProducts()
    {
        LOG.info( "Peticion GET para obtener todos los productos" );
        return productsRepository.findAll();
    }
}
