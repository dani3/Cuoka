package es.sidelab.cuokawebscraperrestserver.repositories;

import es.sidelab.cuokawebscraperrestserver.beans.DescubreShop;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio que gestiona la tabla de tiendas. 
 * @author Daniel Mancebo Aldea
 */

public interface DescubreShopsRepository extends JpaRepository<DescubreShop, Long> 
{    
    DescubreShop findByName(String name);
}
