package es.sidelab.cuokawebscraperrestserver.utils;

/**
 * Clase con utilidades varias.
 * @author Daniel Mancebo Aldea
 */
public class Utils 
{
    /**
     * Metodo que devuelve los dias de diferencia entre dos fechas.
     * @param t1: fecha inicio.
     * @param t2: fecha final.
     * @return dias de diferencia.
     */
    public static short daysBetween(final long t1, final long t2) 
    {
        return (short) ((t2 - t1) / (1000 * 60 * 60 * 24));
    } 
}
