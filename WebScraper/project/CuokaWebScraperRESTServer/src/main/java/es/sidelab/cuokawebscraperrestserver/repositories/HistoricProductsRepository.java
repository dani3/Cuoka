package es.sidelab.cuokawebscraperrestserver.repositories;

import es.sidelab.cuokawebscraperrestserver.beans.HistoricProduct;
import java.util.Calendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Repositorio que gestiona el historico de productos
 * @author Daniel Mancebo Aldea
 */

public interface HistoricProductsRepository extends JpaRepository<HistoricProduct, Long> 
{
    @Query( "SELECT insertDate "
            + "FROM HistoricProduct "
            + "WHERE shop = ?1 AND section = ?2 AND reference = ?3 AND color = ?4" )
    Calendar getInsertDateByReference( String shop, String section, String reference, String color );
}
