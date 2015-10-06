package es.sidelab.cuokawebscraperrestserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * @class Clase principal que arranca Spring
 * @author Daniel Mancebo Aldea
 */

@ComponentScan
@EnableAutoConfiguration
public class main 
{
    public static void main( String[] args ) 
    {
        SpringApplication.run( main.class, args );
    }    
}
