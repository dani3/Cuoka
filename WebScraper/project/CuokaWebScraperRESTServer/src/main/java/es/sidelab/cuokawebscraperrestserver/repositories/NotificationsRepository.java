package es.sidelab.cuokawebscraperrestserver.repositories;

import es.sidelab.cuokawebscraperrestserver.beans.Notification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Interfaz que gestiona la tabla donde se guardan las notificaciones.
 * @author Daniel Mancebo Aldea
 */

public interface NotificationsRepository extends JpaRepository<Notification, Long> 
{
    @Query("FROM Notification WHERE DATEDIFF(CURDATE(), insert_date) < ?1 AND id > ?2")
    List<Notification> findActive(int offset, long lastNotification);
}
