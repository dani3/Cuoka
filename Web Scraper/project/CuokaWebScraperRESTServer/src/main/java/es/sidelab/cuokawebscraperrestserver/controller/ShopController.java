package es.sidelab.cuokawebscraperrestserver.controller;

import es.sidelab.cuokawebscraperrestserver.beans.BeanShop;
import es.sidelab.cuokawebscraperrestserver.repositories.ShopsRepository;
import java.util.List;
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
    @Autowired
    ShopsRepository shopsRepository;
    
    /*
     * Metodo que añade una nueva tienda, si ya existe se devuelve un error 400
     */
    @RequestMapping( value = "/add", method = RequestMethod.POST )
    public ResponseEntity<Boolean> addShop( @RequestBody BeanShop shop )
    {        
        // Si existe ya la tienda, se devuelve error 400
        if ( ( shopsRepository.findByName( shop.getName() ) != null ) && ( checkShop( shop ) ) )
            return new ResponseEntity<>( HttpStatus.BAD_REQUEST );
        
        else {
            shopsRepository.save( shop );            
            return new ResponseEntity<>( HttpStatus.CREATED );
        }
    }
    
    /*
     * Metodo que devuelve una tienda dado su nombre
     */
    @RequestMapping( value = "/get/{name}", method = RequestMethod.GET )
    public BeanShop getShop( @PathVariable String name )
    {
        return shopsRepository.findByName( name );
    }
    
    /*
     * Metodo que devuelve una lista con todas las tiendas
     */
    @RequestMapping( value = "/get", method = RequestMethod.GET )
    public List<BeanShop> getShops()
    {
        return shopsRepository.findAll();
    }
    
    /*
     * Metodo que devuelve true si hay algun atributo incorrecto
     */
    private boolean checkShop( BeanShop shop )
    {
        if ( ( shop.getName() == null ) ||
             ( shop.getUrl() == null ) ||
             ( shop.getName().equals( "" ) ) )
            return true;
        
        if ( shop.getSections().isEmpty() )
            return true;
        
        return false;
    }
}
