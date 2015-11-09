package es.sidelab.cuokawebscraperrestserver.repositories;

import es.sidelab.cuokawebscraperrestserver.beans.ColorVariant;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @interface Interfaz que gestiona la tabla de los distintos colores
 * @author Daniel Mancebo Aldea
 */

public interface ColorVariantsRepository extends JpaRepository<ColorVariant, Long>
{
    
}
