package es.sidelab.cuokawebscraperrestserver.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Clase que se encarga del cifrado, hashing, etc.
 * @author Daniel Mancebo Aldea
 */

public class EncryptionManager 
{
    private static final Log LOG = LogFactory.getLog(EncryptionManager.class);
    
    /**
     * Metodo que cifra el string con la clave recibida utilizando el algoritmo AES.
     * @param key: clave de 128 bits.
     * @param initVector: vector de inicializacion de 16 bytes.
     * @param message: string a cifrar.
     * @return String con el mensaje cifrado.
     */
    public static String encrypt(String key, byte[] initVector, String message) 
    {
        try 
        {
            LOG.info("Cifrando " + message);
            
            IvParameterSpec iv = new IvParameterSpec(initVector);            
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(message.getBytes("UTF-8"));
            
            LOG.info("Contraseña cifrada con exito");

            return Base64.getEncoder().encodeToString(encrypted);
            
        } catch (Exception ex) {
            LOG.error("Error al cifrar (" + ex.getMessage() + ")");
            
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
    public static String decrypt(String key, byte[] initVector, String message) 
    {
        try 
        {
            LOG.info("Descifrando...");
            
            IvParameterSpec iv = new IvParameterSpec(initVector);
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(Base64.getDecoder().decode(message));

            LOG.info("Contraseña descifrada con exito");
            
            return new String(original);
            
        } catch (Exception ex) {
            LOG.error("Error al descifrar (" + ex.getMessage() + ")");
            
        }

        return null;
    }
    
    /**
     * Metodo que realiza el hash utilizando el algoritmo recibido.
     * @param input: texto del que se quiere hacer el hash.
     * @param algorithm: algoritmo deseado para realizar el hash.
     * @return hash de 16 bytes del texto recibido.
     */
    public static String calculateMD5Hash(String input, String algorithm)
    {
        try 
        {
            LOG.info("Calculando HASH usando " + algorithm);
            
            MessageDigest md = MessageDigest.getInstance(algorithm);
            
            LOG.info(input.length());
            
            md.update(input.getBytes("UTF-8"));
            MessageDigest in = (MessageDigest)md.clone();
            
            String inDigest = new String(in.digest(), "UTF-8");       
            
            LOG.info("Hash calculado: " + inDigest);
            return inDigest;
            
        } catch (CloneNotSupportedException | NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            LOG.error("ERROR: No se ha podido calcular el hash (" + ex.getMessage() + ")");
            
        }
            
        return null;
    }
}
