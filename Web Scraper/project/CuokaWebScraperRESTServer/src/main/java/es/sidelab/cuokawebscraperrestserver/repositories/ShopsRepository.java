package es.sidelab.cuokawebscraperrestserver.repositories;

import es.sidelab.cuokawebscraperrestserver.beans.BeanShop;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @class Repositorio que gestiona la tabla de tiendas
 * @author Daniel Mancebo Aldea
 */

public interface ShopsRepository extends JpaRepository<BeanShop, Long>
{
    BeanShop findByName( String name );
}
