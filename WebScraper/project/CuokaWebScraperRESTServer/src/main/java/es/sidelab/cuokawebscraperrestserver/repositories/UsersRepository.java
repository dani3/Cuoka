package es.sidelab.cuokawebscraperrestserver.repositories;

import es.sidelab.cuokawebscraperrestserver.beans.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @class Repositorio que maneka los usuarios en BD.
 * @author Daniel Mancebo Aldea
 */

public interface UsersRepository extends JpaRepository<User, Long> 
{
    User findByEmail( String email );
    User findByEmailAndPassword( String email, String password );
}
