package es.sidelab.cuokawebscraperrestserver.controller;

import es.sidelab.cuokawebscraperrestserver.beans.DescubreShop;
import es.sidelab.cuokawebscraperrestserver.beans.User;
import es.sidelab.cuokawebscraperrestserver.repositories.DescubreShopsRepository;
import es.sidelab.cuokawebscraperrestserver.repositories.UsersRepository;
import es.sidelab.cuokawebscraperrestserver.utils.ShopManager;
import java.util.ArrayList;
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
 * Controlador de Descubre.
 * @author Daniel Mancebo Aldea
 */

@RestController
public class DescubreController 
{
    private static final Log LOG = LogFactory.getLog(DescubreController.class);
    
    @Autowired
    private UsersRepository usersRepository;
    
    @Autowired
    private DescubreShopsRepository descubreShopsRepository;
    
    @Autowired
    private ShopManager shopManager;
    
    /**
     * Metodo que anade una nueva tienda de Descubre.
     * @param shop: tienda de Descubre.
     * @return HTTP Code 200.
     */
    @RequestMapping(value = "/descubre", method = RequestMethod.POST)
    public ResponseEntity<Boolean> addDesubreShop(@RequestBody DescubreShop shop)
    {
        LOG.info("[DESCUBRE] Peticion POST para anadir la tienda (" + shop.getName() + ") a Descubre");
        
        descubreShopsRepository.save(shop);
        
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    
    /**
     * Metodo que devuelve la lista de tiendas recomendadas de un usuario.
     * @param id: id del usuario.
     * @return Lista de tiendas recomendadas.
     */
    @RequestMapping(value = "/descubre/{id}", method = RequestMethod.GET)
    public List<DescubreShop> getDescubreShops(@PathVariable long id)
    {
        LOG.info("[DESCUBRE] Peticion GET para obtener todos las tiendas recomendadas del usuario (ID: " + id + ")");
        
        User user = usersRepository.findOne(id);
        
        if (user == null)
        {
            LOG.warn("[DESCUBRE] Usuario no encontrado (ID: " + id + ")");
            
            return new ArrayList<>();
        }
        
        List<DescubreShop> descubreShops = new ArrayList<>();
        List<String> recommendedShops = shopManager.getRecommendedShops(user.getStyles());        
        for (String shop : recommendedShops)
        {         
            DescubreShop descubreShop;
            if ((descubreShop = descubreShopsRepository.findByName(shop)) == null)
            {
                continue;
            }
            
            // Si el usuario es hombre y la tienda es de hombre, o es mujer y la tienda es de mujer.
            if ((user.getMan() && descubreShop.getMan()) || (!user.getMan() && descubreShop.getWoman()))
            {
                descubreShops.add(descubreShop);
            }            
        }
        
        return descubreShops;
    }
}
