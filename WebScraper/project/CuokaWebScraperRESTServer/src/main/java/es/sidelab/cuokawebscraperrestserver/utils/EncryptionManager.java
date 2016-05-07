package es.sidelab.cuokawebscraperrestserver.utils;

import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @class Clase que se encarga del cifrado y descifrado de strings.
 * @author Daniel Mancebo Aldea
 */

public class EncryptionManager 
{
    private static final Log LOG = LogFactory.getLog( EncryptionManager.class );
    
    /**
     * Metodo que cifra el string con la clave recibida utilizando el algoritmo AES.
     * @param key: clave de 128 bits.
     * @param initVector: vector de inicializacion de 16 bytes.
     * @param message: string a cifrar.
     * @return String con el mensaje cifrado.
     */
    public static String encrypt( String key, String initVector, String message ) 
    {
        try 
        {
            LOG.info( "Cifrando " + message );
            
            IvParameterSpec iv = new IvParameterSpec( initVector.getBytes( "UTF-8" ) );
            SecretKeySpec skeySpec = new SecretKeySpec( key.getBytes( "UTF-8" ), "AES" );

            Cipher cipher = Cipher.getInstance( "AES/CBC/PKCS5PADDING" );
            cipher.init( Cipher.ENCRYPT_MODE, skeySpec, iv );

            byte[] encrypted = cipher.doFinal( message.getBytes() );
            
            LOG.info( message + " cifrado con exito" );

            return Base64.getEncoder().encodeToString( encrypted );
            
        } catch ( Exception ex ) {
            LOG.error( "Error al cifrar " + message + " (" + ex.getMessage() + ")" );
            
        }

        return null;
    }    
    
    /**
     * Metodo que descifra el string con la clave recibida utilizando el algoritmo AES.
     * @param key: clave de 128 bits.
     * @param initVector: vector de inicializacion de 16 bytes.
     * @param message: string a cifrar.
     * @return String con el mensaje descifrado.
     */
    public static String decrypt( String key, String initVector, String message ) 
    {
        try 
        {
            LOG.info( "Descifrando..." );
            
            IvParameterSpec iv = new IvParameterSpec( initVector.getBytes( "UTF-8" ) );
            SecretKeySpec skeySpec = new SecretKeySpec( key.getBytes( "UTF-8" ), "AES" );

            Cipher cipher = Cipher.getInstance( "AES/CBC/PKCS5PADDING" );
            cipher.init( Cipher.DECRYPT_MODE, skeySpec, iv );

            byte[] original = cipher.doFinal( Base64.getDecoder().decode( message ) );

            LOG.info( "Mensaje descifrado con exito (" + original +")" );
            
            return new String(original);
            
        } catch (Exception ex) {
            LOG.error( "Error al descifrar (" + ex.getMessage() + ")" );
            
        }

        return null;
    }
}
