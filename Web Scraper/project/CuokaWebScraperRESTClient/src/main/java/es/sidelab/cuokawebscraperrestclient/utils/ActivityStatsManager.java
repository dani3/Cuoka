package es.sidelab.cuokawebscraperrestclient.utils;

import es.sidelab.cuokawebscraperrestclient.activity.SectionActivityStats;
import es.sidelab.cuokawebscraperrestclient.activity.ShopActivityStats;
import es.sidelab.cuokawebscraperrestclient.beans.Section;
import es.sidelab.cuokawebscraperrestclient.properties.Properties;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.log4j.Logger;

/**
 *
 * @author Lucia Fernandez Guzman
 */

public class ActivityStatsManager 
{    
   private static CopyOnWriteArrayList<ShopActivityStats> shopActivityList = 
                                                                new CopyOnWriteArrayList<>();
   
   private static final Logger LOG = Logger.getLogger( ActivityStatsManager.class );
   
   public static void addShopActivity( ShopActivityStats shopActivity )
   {
       shopActivityList.add( shopActivity );
   }
   
   public static void writeOnFile()
   {
       FileWriter file = null;
       PrintWriter pw = null;
       
       int onlineShops = 0;
       int offlineShops = 0;
       
       /* Contamos las tiendas que estan online y offline */
       for ( ShopActivityStats shopActivity : shopActivityList)
       {
           if ( shopActivity.isOnline() )
               onlineShops++;
           else
               offlineShops++;
       }
       
       try
       {
           file = new FileWriter( Properties.ACTIVITY_PATH );
           pw = new PrintWriter( file );
           
           pw.println("Numero de tiendas online: "+ onlineShops);
           pw.println("Numero de tiendas offline: "+ offlineShops);           
           pw.println();
           pw.println();           
           
           int i = 0;
           for( ShopActivityStats shopActivity : shopActivityList )
           {
                if( shopActivity.isOnline() )
                {                    
                    pw.println("ShopActivityStat numero: " + i++);
                    pw.println("La tienda es " + shopActivity.getShop());
                    pw.println("URL: " + shopActivity.getUrl());
                    pw.println("Está online: " + shopActivity.isOnline());
                    pw.println("Hay hombre: " + shopActivity.isMan());
                    
                    List<SectionActivityStats> listSectionStats = shopActivity.getListSectionStats();                   
                    for ( SectionActivityStats sectionActivity : listSectionStats)
                    {
                        if( sectionActivity.isMan() )
                        {
                            pw.println("        " + sectionActivity.getSection().replaceAll(".txt", "") + "  Html: " + sectionActivity.isHtmlOK());
                            pw.println("            Productos OK: " + sectionActivity.getProdOK());
                            pw.println("            Productos NOK: " + sectionActivity.getProdNOK());
                        }
                    }
                    
                    pw.println();
                    pw.println("Hay mujer: " + shopActivity.isWoman());
                    
                    for ( SectionActivityStats sectionActivity : listSectionStats)
                    {
                        if( ! sectionActivity.isMan() )
                        {
                            pw.println("        " + sectionActivity.getSection().replaceAll(".txt", "") + "  Html: " + sectionActivity.isHtmlOK());
                            pw.println("            Productos OK: " + sectionActivity.getProdOK());
                            pw.println("            Productos NOK: " + sectionActivity.getProdNOK());
                        }
                    }
                    
                    pw.println();
                }
                
            } 
           
       } catch ( Exception e ) {
            LOG.error("Error en fichero Activity");
            
       } finally {
           
           try 
           {
              if (file != null)
                file.close();
              
           } catch ( Exception e ) {
              LOG.error("Error cerrando el fichero Activity.txt");
              
           }
           
       }
       
   }
   
   public static void updateProducts( String shop, Section section, int prodOK, int prodNOK )
   {
       boolean found = false;
       int i = 0;
       
       // Buscamos la tienda correspondiente
       while( ( ! found ) && ( i < shopActivityList.size() ) )
       {
           ShopActivityStats shopActivity = shopActivityList.get( i++ );
           if( shopActivity.getShop().equals(shop) )
           {
               found = true;
               boolean found2 = false;
               int j = 0;
               
               // Buscamos la seccion correspondiente
               while( (! found2 ) && ( j < shopActivity.getListSectionStats().size() ) )
               {
                   SectionActivityStats sectionActivity = shopActivity.getListSectionStats().get( j++ );
                   if( sectionActivity.getSection().equals( section.getName() ) && 
                     ( sectionActivity.isMan() == section.isMan() ) )
                   {
                       sectionActivity.updateProducts( prodOK, prodNOK );
                       found2 = true;
                   }
                   
               } // while #2
               
           } // if   
           
       } // while #1
       
   }
    
}
