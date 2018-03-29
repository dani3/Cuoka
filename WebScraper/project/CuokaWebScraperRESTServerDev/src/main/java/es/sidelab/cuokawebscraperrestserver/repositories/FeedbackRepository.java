package es.sidelab.cuokawebscraperrestserver.repositories;

import es.sidelab.cuokawebscraperrestserver.beans.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Interfaz que gestiona la tabla donde se guardan las opiniones de los usuarios.
 * @author Daniel Mancebo Aldea
 */

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {}
