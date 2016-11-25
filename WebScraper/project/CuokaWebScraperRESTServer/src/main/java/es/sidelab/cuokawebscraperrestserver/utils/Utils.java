package es.sidelab.cuokawebscraperrestserver.utils;

/**
 * Clase con utilidades varias.
 * @author Daniel Mancebo Aldea
 */
public class Utils 
{
    public static short daysBetween(final long t1, final long t2) 
    {
        return (short) ((t2 - t1) / (1000 * 60 * 60 * 24));
    } 
}
