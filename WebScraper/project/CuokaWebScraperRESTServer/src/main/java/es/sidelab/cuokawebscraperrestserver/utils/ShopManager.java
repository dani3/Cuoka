package es.sidelab.cuokawebscraperrestserver.utils;

import es.sidelab.cuokawebscraperrestserver.beans.Shop;
import es.sidelab.cuokawebscraperrestserver.properties.Properties;
import es.sidelab.cuokawebscraperrestserver.repositories.ShopsRepository;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
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
    
    @PostConstruct
    private void init()
    {
        shopMaleList   = shopsRepository.findByMan();
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
    
    /**
     * Metodo que devuelve una tienda que empiece por la palabra recibida.
     * @param keyword palabra con la que se busca la tienda.
     * @param man: true si la tienda a buscar es de hombre.
     * @return tienda sugerida.
     */
    public Shop getShopStartingWith(String keyword, boolean man)
    {        
        // Buscamos tiendas que empiecen por la palabra recibida.
        for (Shop shop : ((man) ? shopMaleList : shopFemaleList))
        {
            if (shop.getName().toUpperCase().startsWith(keyword.toUpperCase()))
            {
                return shop;
            }
        }    
        
        return null;
    }
    
    /**
     * Metodo que intenta encontrar la tienda en las palabras recibidas.
     * Si la encuentra, elimina la tienda de las palabras recibidas y las devuelve.
     * @param keywords: palabras donde buscar la tienda.
     * @param man: true si es de hombre la tienda.
     * @return la tienda si la encuentra, null EOC.
     */
    public List<Object> findShop(List<String> keywords, boolean man)
    {
        Shop shop = new Shop();
        boolean found = false;
        int i;
              
        // Buscamos la tienda.
        for (i = 0; i < keywords.size(); ++i)
        {
            shop = getShopStartingWith(keywords.get(i), man);
            
            if (shop != null)
            {
                found = true;
                
                break;
            }
        }
        
        if (found)
        {
            // Sacamos cuantas palabras forman el nombre de la tienda.
            String[] aux = shop.getName().split(" ");
            List<String> newKeywords = new ArrayList<>();
            
            // Debido a que no se pueden borrar facilmente las keywords que forman la tienda
            // (los indices se alteran), se recorren las keywords originales, y se salta la tienda.
            for (int j = 0; j < keywords.size(); ++j)
            {
                // Insertamos en la nueva lista las palabras que no esten en el rango que forman las palabras de la tienda
                if (!((j >= i) && (j < (i + aux.length))))
                {
                    newKeywords.add(keywords.get(j));
                }
            }
            
            List<Object> result = new ArrayList<>();
            
            result.add(shop);
            result.add(newKeywords);
            
            return result;
        }
        
        return null;
    }
}
