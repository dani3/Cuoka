package es.sidelab.cuokawebscraperrestclient.utils;

import es.sidelab.cuokawebscraperrestclient.beans.Shop;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.log4j.Logger;

/**
 * Clase que envia el correo con las estadisticas del proceso de scraping.
 * @author Daniel Mancebo Aldea
 */
public class MailSender 
{
    private static final Logger LOG = Logger.getLogger(MailSender.class); 
    
    /**
     * Metodo que envia el correo con las estadisticas del proceso de scraping.
     * @param analyzers: lista con todos los resultados del proceso de scraping de una tienda.
     * @param shop: tienda de la que se van a enviar las estadisticas.
     */
    public static void sendEmail(List<ScrapingAnalyzer> analyzers, Shop shop) 
    {
        Properties props = System.getProperties();
        
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", es.sidelab.cuokawebscraperrestclient.properties.Properties.HOST);
        props.put("mail.smtp.user", es.sidelab.cuokawebscraperrestclient.properties.Properties.FROM);
        props.put("mail.smtp.password", es.sidelab.cuokawebscraperrestclient.properties.Properties.PASSWORD);
        props.put("mail.smtp.port", es.sidelab.cuokawebscraperrestclient.properties.Properties.PORT);
        props.put("mail.smtp.auth", "true");

        Session session = Session.getDefaultInstance(props);
        MimeMessage message = new MimeMessage(session);

        try 
        {     
            StringBuilder body = new StringBuilder();
            for (ScrapingAnalyzer scrapingAnalyzer : analyzers)
            {
                body.append(scrapingAnalyzer.getResults());
            }
            
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);
            
            String subject = "[SCRAPING_ANALYZER] " 
                + "[" + String.valueOf(day) + "/" + String.valueOf(month) + "/" + String.valueOf(year) + "] " 
                + shop.getName();
            
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(es.sidelab.cuokawebscraperrestclient.properties.Properties.FROM));            
            message.setSubject(subject);
            message.setText(body.toString());
            message.setFrom(new InternetAddress(es.sidelab.cuokawebscraperrestclient.properties.Properties.FROM));
            
            Transport transport = session.getTransport("smtp");
            
            transport.connect(es.sidelab.cuokawebscraperrestclient.properties.Properties.HOST
                , es.sidelab.cuokawebscraperrestclient.properties.Properties.FROM
                , es.sidelab.cuokawebscraperrestclient.properties.Properties.PASSWORD);
            
            transport.sendMessage(message, message.getAllRecipients());
            
            transport.close();
            
        } catch (MessagingException ex) {
            LOG.error("ERROR: Error al enviar el correo con las estadisticas del proceso de Scraping");
            LOG.error(ex.getMessage());
        } 
    }
}
