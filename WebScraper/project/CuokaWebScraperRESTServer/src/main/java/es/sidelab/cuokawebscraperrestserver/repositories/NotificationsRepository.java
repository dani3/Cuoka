package es.sidelab.cuokawebscraperrestserver.repositories;

import es.sidelab.cuokawebscraperrestserver.beans.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Interfaz que gestiona la tabla donde se guardan las notificaciones.
 * @author Daniel Mancebo Aldea
 */

public interface NotificationsRepository extends JpaRepository<Notification, Long> {}
