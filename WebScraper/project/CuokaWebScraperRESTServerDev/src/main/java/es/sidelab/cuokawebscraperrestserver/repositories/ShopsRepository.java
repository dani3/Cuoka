package es.sidelab.cuokawebscraperrestserver.repositories;

import es.sidelab.cuokawebscraperrestserver.beans.Shop;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Repositorio que gestiona la tabla de tiendas. 
 * @author Daniel Mancebo Aldea
 */

public interface ShopsRepository extends JpaRepository<Shop, Long> 
{
    @Query("FROM Shop WHERE man = true")
    List<Shop> findByMan();
    
    @Query("FROM Shop WHERE woman = true")
    List<Shop> findByWoman();
    
    Shop findByName(String name);
}
