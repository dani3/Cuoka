package es.sidelab.cuokawebscraperrestserver.repositories;

import es.sidelab.cuokawebscraperrestserver.beans.BeanSection;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @class Repositorio que gestiona la tabla de categorias de una tienda
 * @author Daniel Mancebo Aldea
 */

public interface SectionsRepository extends JpaRepository<BeanSection, Long>
{
    
}
