package es.sidelab.cuokawebscraperrestserver.repositories;

import es.sidelab.cuokawebscraperrestserver.beans.BeanProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * @class Repositorio que gestiona la tabla de productos
 * @author Daniel Mancebo Aldea
 */

public interface ProductsRepository extends JpaRepository<BeanProduct, Long>
{
    @Modifying
    @Transactional
    @Query( "DELETE FROM BeanProduct WHERE shop = ?1" )
    void deleteByShop( String shop );
}
