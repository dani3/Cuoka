package es.sidelab.cuokawebscraperrestserver.controller;

import es.sidelab.cuokawebscraperrestserver.beans.ColorVariant;
import es.sidelab.cuokawebscraperrestserver.beans.Filter;
import es.sidelab.cuokawebscraperrestserver.beans.HistoricProduct;
import es.sidelab.cuokawebscraperrestserver.beans.Product;
import es.sidelab.cuokawebscraperrestserver.beans.User;
import es.sidelab.cuokawebscraperrestserver.properties.Properties;
import es.sidelab.cuokawebscraperrestserver.repositories.HistoricProductsRepository;
import es.sidelab.cuokawebscraperrestserver.repositories.ProductsRepository;
import es.sidelab.cuokawebscraperrestserver.repositories.UsersRepository;
import es.sidelab.cuokawebscraperrestserver.utils.ColorManager;
import es.sidelab.cuokawebscraperrestserver.utils.EncryptionManager;
import es.sidelab.cuokawebscraperrestserver.utils.ImageManager;
import es.sidelab.cuokawebscraperrestserver.utils.SectionManager;
import java.security.SecureRandom;
import java.util.ArrayList;
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
    ColorManager colorManager;
    
    @Autowired
    SectionManager sectionManager;
    
    @Autowired
    ProductsRepository productsRepository;
    
    @Autowired
    HistoricProductsRepository historicProductsRepository;
    
    @Autowired
    UsersRepository usersRepository;
    
    /**
     * 
     * @param user
     * @return 
     */
    @RequestMapping( value = "/users", method = RequestMethod.POST )
    public ResponseEntity<Boolean> addUser( @RequestBody User user )
    {     
        LOG.info( "Peticion POST para anadir un nuevo usuario" );
        user.setRegistrationDate( Calendar.getInstance() );
        
        SecureRandom sr = new SecureRandom();
        byte[] IV = sr.generateSeed( Properties.IV_LENGTH );
        
        final String encryptedPassword = EncryptionManager.encrypt( Properties.KEY, IV, user.getPassword() );
        
        user.setPassword( encryptedPassword );
        
        usersRepository.save( user );
        
        LOG.info( "Usuario guardado correctamente" );
        return new ResponseEntity<>( HttpStatus.CREATED );
    }
    
    /**
     * Metodo que elimina los productos de la tienda e inserta los nuevos recibidos.
     * @param products: Lista de los productos a insertar.
     * @param shop: Tienda a la que pertenecen los productos.
     * @return Codigo HTTP con el resultado de la ejecucion.
     */
    @CacheEvict( value = "products", allEntries = true )
    @RequestMapping( value = "/products/{shop}", method = RequestMethod.POST )
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
                                                    , cv.getName() );


                    if ( insertDate == null )
                    {
                        historicProductsRepository.save( new HistoricProduct( shop
                                                                , product.getSection()
                                                                , cv.getReference() 
                                                                , cv.getName() 
                                                                , Calendar.getInstance() ) );

                        newness = true;
                    }               
                }

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
     * @return Lista de productos.
     */
    @RequestMapping( value = "/products/{shop}", method = RequestMethod.GET )
    public List<Product> getProducts( @PathVariable String shop )
    {
        LOG.info( "Peticion GET para obtener todos los productos de " + shop );
        return productsRepository.findByShop( shop) ;
    }
    
    /**
     * Metodo que devuelve una lista de novedades de una tienda
     * @param shop: Tienda de la que se quieren las novedades.
     * @param man: true si se quiere solo los productos de hombre.
     * @param offset: numero de dias del que se quiere los productos.
     * @return Lista de productos.
     */
    @Cacheable( value = "products", key = "#shop.toString() + #man.toString() + #offset.toString()" )
    @RequestMapping( value = "/products/{shop}/{man}/{offset}", method = RequestMethod.GET )
    public List<Product> getProductsByShopAndByDay( @PathVariable String shop
                            , @PathVariable String man
                            , @PathVariable String offset )
    {
        LOG.info( "Peticion GET para obtener los productos de " + shop + " de hace " + offset + " dias" );
        return productsRepository.findByShopAndDate( shop, Boolean.valueOf( man ), Integer.valueOf( offset ) ) ;
    }
    
    /**
     * Metodo que devuelve una lista de productos de una seccion de una tienda.
     * @param shop: Tienda a la que pertenece la seccion.
     * @param section: Seccion de la que se quieren los productos.
     * @return Lista de productos.
     */
    @RequestMapping( value = "/products/{shop}/{section}", method = RequestMethod.GET )
    public List<Product> getProductsBySection( @PathVariable String shop
                                , @PathVariable String section )
    {
        LOG.info( "Peticion GET para obtener los productos de la seccion de " 
                        + section + " de la tienda " + shop );
        return productsRepository.findBySectionAndShop( section, shop ) ;
    }
    
    /**
     * Metodo que devuelve una lista de productos que cumplen una serie de condiciones.
     * @param filter: Filtro por el que tienen que pasar los productos.
     * @param shop: Tienda de la que se quiere filtrar los productos.
     * @return Lista de productos.
     */
    @RequestMapping( value = "/filter/{shop}", method = RequestMethod.POST )
    public List<Product> getProductsByFilter( @RequestBody Filter filter
                                    , @PathVariable String shop )
    {
        LOG.info( "Peticion GET para obtener los productos de " + shop + " que cumplan los siguientes filtros:" );
        
        List<Product> productList = new ArrayList<>();
        
        if ( filter.isMan() )
            LOG.info( " - Solo hombre" );
        else
            LOG.info( " - Solo mujer" ); 
            
        if ( filter.getPriceFrom() > 0 )
            LOG.info( " - Precio minimo = " + filter.getPriceFrom() );

        if ( filter.getPriceTo() > 0 )
            LOG.info( " - Precio maximo = " + filter.getPriceTo() ); 

        // Ponemos un valor minimo y maximo si no se reciben en el JSON.
        double from = ( filter.getPriceFrom() > 0 ) ? filter.getPriceFrom() : -1;
        double to = ( filter.getPriceTo() > 0 ) ? filter.getPriceTo() : 999; 

        if ( filter.isNewness() )
        {
            LOG.info( " - Solo novedades" );                                 

            productList = productsRepository.findByShopAndManAndNewnessAndPrice( shop
                                        , filter.isMan()
                                        , 0
                                        , from
                                        , to );

        } else {
            LOG.info( " - Todos los productos" );                                 

            productList = productsRepository.findByShopAndManAndPrice( shop
                                        , filter.isMan()
                                        , from
                                        , to );
        }       
           
        List<Product> newList = new ArrayList<>();
        
        // Buscamos primero si tiene el filtro de color y de secciones
        if ( ! filter.getSections().isEmpty() && ! filter.getColors().isEmpty() )
        {
            LOG.info( " - De las siguientes secciones:" );            
            for ( String section : filter.getSections() )
                LOG.info( "   " + section );   
            
            LOG.info( " - De los siguientes colores:" );            
            for ( String color : filter.getColors() )
                LOG.info( "   " + color );  
            
            for ( Product product : productList )
            {
                if ( _searchForSection( product, filter.getSections() ) )
                {
                    Product aux = _searchForColor( product, filter.getColors() );
                    if ( aux != null )
                        newList.add( aux );                     
                }
            }
            
            return newList;
        }
        
        // Buscamos la seccion si no tiene el filtro de color
        if ( ! filter.getSections().isEmpty() && filter.getColors().isEmpty() )
        {         
            LOG.info( " - De las siguientes secciones:" );            
            for ( String section : filter.getSections() )
                LOG.info( "   " + section );   
            
            for ( Product product : productList )
                if ( _searchForSection( product, filter.getSections() ) )
                    newList.add( product );  
            
            return newList;
        }  

        // Buscamos el color si no tiene el filtro de secciones
        if ( filter.getSections().isEmpty() && ! filter.getColors().isEmpty() )
        {   
            LOG.info( " - De los siguientes colores:" );            
            for ( String color : filter.getColors() )
                LOG.info( "   " + color );         
            
            for ( Product product : productList )
            {
                Product aux =_searchForColor( product, filter.getColors() );
                if ( aux != null )
                    newList.add( product );
            }
            
            return newList;
        }         
        
        return productList;
    }
    
    /**
     * Metodo que realiza una busqueda de productos.
     * @param shop: tienda de la que se quieren los productos.
     * @param man: hombre o mujer.
     * @param search: productos a buscar.
     * @return lista de productos encontrados.
     */
    @RequestMapping( value = "/search/{shop}/{man}/{search}", method = RequestMethod.GET )
    public List<Product> getProductsBySearch( @PathVariable String shop
                                , @PathVariable String man
                                , @PathVariable String search )
    {
        List<Product> productList = new ArrayList<>();
        List<Product> newList = new ArrayList<>();
        String[] aux = search.split( " " ); 
        
        // Sacamos los productos de la tienda
        productList = productsRepository.findByManAndShop( Boolean.valueOf( man ), shop );
        
        // Eliminamos palabras irrelevantes ('a', 'de', etc)
        List<String> keywords = new ArrayList<>();
        for ( String keyword : aux )
        {
            if ( ( keyword.length() > 2 ) && ( ! keyword.equalsIgnoreCase( "Con" ) ) )
            {
                keywords.add( keyword );
            }
        }
        
        // Recorremos los productos 
        for ( Product product : productList )
        {
            boolean candidate = true;            
            Product paux = null;
            
            // Recorremos las palabras buscadas
            for ( String keyword : keywords )
            {
                // Comprobamos si es una seccion
                List<String> section = new ArrayList<>();
                String saux = sectionManager.getSection( keyword );
                if ( saux != null )
                    section.add( saux );
                    
                if ( ! section.isEmpty() )
                {
                    candidate = _searchForSection( product, section );
                    
                    if ( ! candidate )
                        break;
                }
                
                // Comprobamos si es un color
                List<String> color = new ArrayList<>();
                String caux = colorManager.getColor( keyword );
                if ( caux != null )
                    color.add( caux );
                
                if ( ! color.isEmpty() )
                {
                    paux = _searchForColor( product, color );
                    
                    candidate = ( paux != null );
                    if ( ! candidate )
                        break;
                }
                
                // Si no es ni seccion ni color
                if ( color.isEmpty() && section.isEmpty() )
                {
                    candidate = _searchForKeyword( product, keyword );
                    
                    if ( ! candidate )
                        break;
                }
                
            }
            
            if ( candidate )
                newList.add( ( paux == null ) ? product : paux );
        }
        
        return newList;
    }
    
    /**
     * Metodo que devuelve una lista de sugerencias.
     * @param word palabras buscadas.
     * @return lista de sugerencias.
     */
    @RequestMapping( value = "/suggest/{word}", method = RequestMethod.GET )
    public List<String> getSuggestions( @PathVariable String word )
    {
        List<String> suggestions = new ArrayList<>();
        String[] words = word.split( " " );
        
        // Si solo recibimos un palabra, buscamos una seccion.
        if ( words.length == 1 )
        {            
            suggestions = sectionManager.getSectionsStartingWith( word );
        }
        
        // Si recibimos dos palabras, buscamos la primera palabra como seccion
        // la segunda se busca como color
        if ( words.length == 2 )
        {
            List<String> firstWordSuggestions = sectionManager.getSectionsStartingWith( words[0] );
            
            if ( firstWordSuggestions.isEmpty() )
                return new ArrayList<>();
            
            List<String> colors = colorManager.getColorStartingWith( words[1] );
            
            if ( colors == null )
                return new ArrayList<>();
            
            for ( String firstWordSuggestion : firstWordSuggestions )
            {                
                for ( String color : colors )
                {
                    suggestions.add( firstWordSuggestion + " " 
                        + ( ( sectionManager.getSectionGender( firstWordSuggestion ) ) ? color : colorManager.getFemaleColor( color ) ) );
                }    
            }
        }
        
        return suggestions;
    }
    
    /**
     * Metodo que busca en el producto los colores recibidos.
     * @param product: producto en el que buscar los colores.
     * @param colors: colores que buscar en el producto.
     * @return producto con el color buscado en primera posicion, null si no esta el color.
     */
    private Product _searchForColor( Product product, List<String> colors )
    {        
        boolean bingo = false;
        
        colors = colorManager.getEquivalentColors( colors );
        
        // Buscamos primero en el color
        for ( String color : colors )
        {
            int pos = 0;
            for ( ColorVariant cv : product.getColors() )
            {
                String[] decomposedColor = cv.getName().split( " " );
                
                for ( String single : decomposedColor )
                {
                    single = single.replaceAll( "[0-9]" , "" );
                    
                    if ( org.apache.commons.lang3.StringUtils
                                .getJaroWinklerDistance( color
                                        , single ) >= Properties.MAX_SIMILARITY_THRESHOLD )
                    {
                        bingo = true;
                        if ( bingo )
                            LOG.info( "Color '" + color + "' encontrado: " + single );
                        
                        return _reorderColorVariants( product, pos );
                    }
                }
                
                pos++;
            }            
        } 
        
        // Si no encontramos nada en el color, lo buscamos en el nombre
        for ( String color : colors )
        {
            String[] decomposedName = product.getName().split( " " );
            
            for ( String single : decomposedName )
            {
                single = single.replace( "," , "" ).replace( "." , "" ).replace( "\n", "" ).trim();
                
                if ( org.apache.commons.lang3.StringUtils
                                .getJaroWinklerDistance( color
                                        , single ) >= Properties.MAX_SIMILARITY_THRESHOLD )
                {
                    bingo = true;
                    if ( bingo )
                        LOG.info( "Color '" + color + "' encontrado: " + single );
                    
                    return product;
                }
            }  
        }
        
        return null;
    }
    
    /**
     * Metodo que busca en el producto las secciones recibidas.
     * @param product: producto en el que buscar las secciones.
     * @param sections: secciones que buscar en el producto.
     * @return true si alguna seccion esta en el producto.
     */
    private boolean _searchForSection( Product product, List<String> sections )
    {   
        boolean bingo = false;
        
        sections = sectionManager.getEquivalentSections( sections );
        
        // Buscamos primero en el campo seccion
        for ( String section : sections )
        {
            if ( org.apache.commons.lang3.StringUtils
                                .getJaroWinklerDistance( section
                                        , product.getSection() ) >= Properties.MAX_SIMILARITY_THRESHOLD )
            {
                bingo = true;
                if ( bingo )
                    LOG.info( "Seccion '" + section + "' encontrada: " + product.getSection() );
                
                return true;
            }
        }
        
        // Si no encontramos nada en el campo seccion, buscamos en el nombre
        for ( String section : sections )
        {
            String[] decomposedName = product.getName().split( " " );
            
            for ( String single : decomposedName )
            {
                single = single.replace( "," , "" ).replace( "." , "" ).replace( "\n", "" ).trim();
                
                if ( org.apache.commons.lang3.StringUtils
                                .getJaroWinklerDistance( section
                                        , single ) >= Properties.MAX_SIMILARITY_THRESHOLD )
                {
                    bingo = true;
                    if ( bingo )
                        LOG.info( "Seccion '" + section + "' encontrada: " + single );
                    
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Metodo que coloca el color especificado en la primera posicion.
     * @param product: producto.
     * @param pos: posicion del color que hay que colocar en la primera posicion.
     * @return producto reordenado.
     */
    private Product _reorderColorVariants( Product product, int pos )
    {        
        if ( product.getColors().size() > 1 )
        {
            List<ColorVariant> aux = product.getColors();
            
            aux.add( 0, aux.get( pos ) );
            aux.remove( pos + 1 );
            
            product.setColors( aux );
        }
        
        return product;
    }
    
    /**
     * Metodo que busca una palabra clave en el producto.
     * @param product: producto donde buscar la palabra.
     * @param keyword: palabra a buscar.
     * @return true si el producto contiene la palabra.
     */
    private boolean _searchForKeyword( Product product, String keyword )
    {
        boolean bingo = false;
        
        // Buscamos la palabra en el nombre.        
        String[] decomposedName = product.getName().split( " " );
        for ( String single : decomposedName )
        {
            single = single.replace( "," , "" ).replace( "." , "" ).replace( "\n", "" ).trim();
            
            if ( org.apache.commons.lang3.StringUtils
                            .getJaroWinklerDistance( keyword
                                    , single ) >= Properties.MAX_SIMILARITY_THRESHOLD )
            {
                bingo = true;
                if ( bingo )
                    LOG.info( "Keyword '" + keyword + "' encontrado: " + single );

                return true;
            }
        }  
        
        // Si no la encontramos en el nombre, la buscamos en la descripcion.
        decomposedName = product.getDescription().split( " " );            
        for ( String single : decomposedName )
        {
            single = single.replace( "," , "" ).replace( "." , "" ).replace( "\n", "" ).trim();
            
            if ( org.apache.commons.lang3.StringUtils
                            .getJaroWinklerDistance( keyword
                                    , single ) >= Properties.MAX_SIMILARITY_THRESHOLD )
            {
                bingo = true;
                if ( bingo )
                    LOG.info( "Keyword '" + keyword + "' encontrado: " + single );

                return true;
            }
        }
        
        return false;
    }
}
