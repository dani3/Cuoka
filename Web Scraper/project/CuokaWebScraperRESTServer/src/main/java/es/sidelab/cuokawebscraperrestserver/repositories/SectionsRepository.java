package es.sidelab.cuokawebscraperrestserver.repositories;

import es.sidelab.cuokawebscraperrestserver.beans.Section;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @class Repositorio que gestiona las secciones
 * @author Daniel Mancebo Aldea
 */

public interface SectionsRepository extends JpaRepository<Section, Long> {}
