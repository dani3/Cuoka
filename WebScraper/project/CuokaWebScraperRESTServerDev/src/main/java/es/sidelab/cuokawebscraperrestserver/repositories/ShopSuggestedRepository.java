package es.sidelab.cuokawebscraperrestserver.repositories;

import es.sidelab.cuokawebscraperrestserver.beans.ShopSuggested;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Interfaz que gestiona la tabla donde se guardan las sugerencias de tiendas de los usuarios.
 * @author Daniel Mancebo Aldea
 */

public interface ShopSuggestedRepository extends JpaRepository<ShopSuggested, Long> {}
