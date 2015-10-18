package es.sidelab.cuokawebscraperrestserver.repositories;

import es.sidelab.cuokawebscraperrestserver.beans.BeanProduct;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @class Repositorio que gestiona la tabla de productos
 * @author Daniel Mancebo Aldea
 */

public interface ProductsRepository extends JpaRepository<BeanProduct, Long>
{
    
}
