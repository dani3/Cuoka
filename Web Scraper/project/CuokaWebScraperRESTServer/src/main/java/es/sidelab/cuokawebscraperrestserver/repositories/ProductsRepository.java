package es.sidelab.cuokawebscraperrestserver.repositories;

import es.sidelab.cuokawebscraperrestserver.beans.Product;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * @class Repositorio que gestiona la tabla de productos
 * @author Daniel Mancebo Aldea
 */

public interface ProductsRepository extends JpaRepository<Product, Long>
{
    @Modifying
    @Transactional
    @Query( "DELETE FROM Product WHERE shop = ?1" )
    void deleteByShop( String shop );
    
    List<Product> findByShop( String shop );
}
