package es.sidelab.cuokawebscraperrestserver.repositories;

import es.sidelab.cuokawebscraperrestserver.beans.Image;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Interfaz que gestiona la tabla donde se guardan las rutas de las imagenes
 * @author Daniel Mancebo Aldea
 */

public interface ImagesRepository extends JpaRepository<Image, Long> {}
