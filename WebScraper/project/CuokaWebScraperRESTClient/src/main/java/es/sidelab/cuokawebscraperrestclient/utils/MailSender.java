package es.sidelab.cuokawebscraperrestclient.utils;

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
     * @param subject: asunto del correo.
     * @param body: cuerpo del correo.
     */
    public static void sendEmail(String subject, String body) 
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
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(es.sidelab.cuokawebscraperrestclient.properties.Properties.FROM));            
            message.setSubject(subject);
            message.setText(body);
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
