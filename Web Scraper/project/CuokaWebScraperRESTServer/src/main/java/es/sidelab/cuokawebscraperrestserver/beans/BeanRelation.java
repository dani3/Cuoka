package es.sidelab.cuokawebscraperrestserver.beans;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.hibernate.jpa.internal.schemagen.JpaSchemaGenerator.Generation;

/**
 * @class Bean que enlaza cada tienda con todas sus secciones
 * @author Daniel Mancebo Aldea
 */

@Entity
public class BeanRelation 
{
    @Id
    @GeneratedValue( strategy = GenerationType.AUTO )
    private long id;    
}
