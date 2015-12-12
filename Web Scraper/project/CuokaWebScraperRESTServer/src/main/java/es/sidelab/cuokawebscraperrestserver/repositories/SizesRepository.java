package es.sidelab.cuokawebscraperrestserver.repositories;

import es.sidelab.cuokawebscraperrestserver.beans.Size;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @interface Interfaz que gestiona la tabla de tallas
 * @author Daniel Mancebo Aldea
 */

public interface SizesRepository extends JpaRepository<Size, Long> {}
