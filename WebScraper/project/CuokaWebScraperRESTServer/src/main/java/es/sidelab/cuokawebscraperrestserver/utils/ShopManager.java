package es.sidelab.cuokawebscraperrestserver.utils;

import es.sidelab.cuokawebscraperrestserver.beans.Shop;
import es.sidelab.cuokawebscraperrestserver.properties.Properties;
import es.sidelab.cuokawebscraperrestserver.repositories.ShopsRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Clase que se encarga de gestionar las tiendas.
 * @author Daniel Mancebo Aldea
 */

@Component
public class ShopManager 
{
    @Autowired
    private ShopsRepository shopsRepository;
    
    private List<Shop> shopMaleList;
    private List<Shop> shopFemaleList;
    
    public ShopManager()
    {
        shopMaleList = shopsRepository.findByMan();
        shopFemaleList = shopsRepository.findByWoman();
    }
    
    /**
     * Metodo que dado un string determina si es una tienda.
     * @param keyword: palabra a buscar.
     * @param man: true si la tienda a buscar es de hombre.
     * @return la tienda si se encuentra, null EOC.
     */
    public Shop getShop(String keyword, boolean man)
    {        
        // Buscamos primero tiendas con poca tolerancia.
        for (Shop shop : ((man) ? shopMaleList : shopFemaleList))
        {
            if (org.apache.commons.lang3.StringUtils
                            .getJaroWinklerDistance(shop.getName()
                                    , keyword) >= Properties.MAX_SIMILARITY_THRESHOLD)
            {
                return shop;
            }
        }
        
        // Si no encontramos nada, aumentamos la tolerancia.
        for (Shop shop : ((man) ? shopMaleList : shopFemaleList))
        {
            if (org.apache.commons.lang3.StringUtils
                            .getJaroWinklerDistance(shop.getName()
                                    , keyword) >= Properties.MEDIUM_SIMILARITY_THRESHOLD)
            {
                return shop;
            }
        }
        
        return null;
    }
    
    /**
     * Metodo que devuelve un máximo de cinco tiendas sugeridas.
     * @param keyword palabra con la que se buscan las sugerencias.
     * @param man: true si la tienda a buscar es de hombre.
     * @return lista de tiendas sugeridas.
     */
    public List<Shop> getShopsStartingWith(String keyword, boolean man)
    {
        List<Shop> shops = new ArrayList<>();
        
        // Primero buscamos tiendas que empiecen por la palabra recibida.
        for (Shop shop : ((man) ? shopMaleList : shopFemaleList))
        {
            if (shop.getName().toUpperCase().startsWith(keyword.toUpperCase()))
            {
                shops.add(shop);
            }

            if (shops.size() == Properties.MAX_SUGGESTIONS)
            {
                return shops;
            }
        }    
        
        // Si no se encuentra nada, buscamos la palabra en la tienda.
        if (shops.isEmpty())
        {
            for (Shop shop : ((man) ? shopMaleList : shopFemaleList))
            {
                if (shop.getName().toUpperCase().contains(keyword.toUpperCase()))
                {
                    shops.add(shop);
                }

                if (shops.size() == Properties.MAX_SUGGESTIONS)
                {
                    return shops;
                }
            }   
        }
        
        return shops;
    }
}
