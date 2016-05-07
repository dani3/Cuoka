package es.sidelab.cuokawebscraperrestserver.repositories;

import es.sidelab.cuokawebscraperrestserver.beans.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Daniel Mancebo Aldea
 */

public interface UsersRepository extends JpaRepository<User, Long> 
{
    
}
