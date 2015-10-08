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
 * @class Controlador que maneja las peticiones entrantes
 * @author Daniel Mancebo Aldea
 */

@RestController
public class ShopController 
{
    @Autowired
    ShopsRepository shopsRepository;
    
    @RequestMapping( value = "/add", method = RequestMethod.POST )
    public ResponseEntity<Boolean> addShop( @RequestBody BeanShop shop )
    {        
        // Si existe ya la tienda, se devuelve error
        if ( shopsRepository.findByName( shop.getName() ) != null )
        {
            return new ResponseEntity<Boolean>( HttpStatus.NOT_ACCEPTABLE );
            
        } else {
            shopsRepository.save( shop );
            
            return new ResponseEntity<Boolean>( HttpStatus.CREATED );
        }
    } // addShop
    
    @RequestMapping( value = "/get/{name}", method = RequestMethod.GET )
    public BeanShop getShop( @PathVariable String name )
    {
        return shopsRepository.findByName( name );
    }
    
    @RequestMapping( value = "/get", method = RequestMethod.GET )
    public List<BeanShop> getShops()
    {
        return shopsRepository.findAll();
    }
}
