package es.sidelab.cuokawebscraperrestserver.utils;

import es.sidelab.cuokawebscraperrestserver.beans.User;
import es.sidelab.cuokawebscraperrestserver.properties.Properties;
import es.sidelab.cuokawebscraperrestserver.repositories.UsersRepository;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

/**
 * Clase que gestiona el envío de correos.
 * @author Daniel Mancebo Aldea
 */

@Component
public class MailManager 
{   
    private static final Log LOG = LogFactory.getLog(MailManager.class);
    
    @Autowired
    private JavaMailSender javaMailSender;    
    
    @Autowired
    private UsersRepository usersRepository;
    
    @PostConstruct
    public void sendPendingEmails()
    {
        List<User> users = usersRepository.findByEmailSent(false);
        
        for (User user : users)
        {
            LOG.info("[EMAIL] Se envia correo de bienvenida");
            sendWelcomeEmail(user.getEmail(), Properties.WELCOME_EMAIL_FROM, user.getName(), user.getMan());
            
            user.setEmailSent(true);
            
            usersRepository.save(user);
        }
    }
    
    /**
     * Metodo que envia un correo al destinatario especificado.
     * @param to: destinatario.
     * @param from: emisor.
     * @param name: nombre del usuario.
     * @param man: hombre o mujer.
     */
    public void sendWelcomeEmail(String to, String from, String name, boolean man)
    {
        MimeMessage mail = javaMailSender.createMimeMessage();
        
        try 
        {
            MimeMessageHelper helper = new MimeMessageHelper(mail, true);
            
            String message = FileManager.getHTMLFromFile(Properties.MAIL_PATH + Properties.WELCOME_EMAIL_NAME);
            if (message != null)
            {
                message = message.replace("?1", (man ? "o" : "a"));
                message = message.replace("?2", name);
            }
                
            helper.setTo(to);
            helper.setFrom(from);
            helper.setSubject(Properties.WELCOME_EMAIL_SUBJECT);
            helper.setText(message, true);
        
            javaMailSender.send(mail);
            
            LOG.info("[EMAIL] Email enviado correctamente");
            
        } catch (MessagingException | MailException e) {
            LOG.error("[EMAIL] Error enviando email (" + e.getMessage() + ")");
        }
    }
    
    /**
     * Metodo que envia un correo al destinatario especificado.
     * @param to: destinatario.
     * @param from: emisor.
     * @param password: contraseña a enviar.
     */
    public void sendPasswordEmail(String to, String from, String password)
    {
        MimeMessage mail = javaMailSender.createMimeMessage();
        
        try 
        {
            MimeMessageHelper helper = new MimeMessageHelper(mail, true);
            
            String message = FileManager.getHTMLFromFile(Properties.MAIL_PATH + Properties.REMEMBER_PWD_EMAIL_NAME);
            if (message != null)
            {
                message = message.replace("?1", password);
            }
                
            helper.setTo(to);
            helper.setFrom(from);
            helper.setSubject(Properties.RECOVER_PASSWORD_EMAIL_SUBJECT);
            helper.setText(message, true);
        
            javaMailSender.send(mail);
            
            LOG.info("[EMAIL] Email enviado correctamente");
            
        } catch (MessagingException | MailException e) {
            LOG.error("[EMAIL] Error enviando email (" + e.getMessage() + ")");
        }
    }
    
    /**
     * Metodo que envia un email con el feedback recibido.
     * @param stars: numero de estrellas.
     * @param message: mensaje.
     */
    public void sendFeedbackEmail(int stars, String message)
    {
        MimeMessage mail = javaMailSender.createMimeMessage();
        
        try 
        {
            MimeMessageHelper helper = new MimeMessageHelper(mail);
                
            helper.setTo(
                Properties.FEEDBACK_EMAIL_FROM);
            helper.setFrom(
                Properties.FEEDBACK_EMAIL_FROM);
            helper.setSubject(
                Properties.FEEDBACK_EMAIL_SUBJECT.replace("?1", Integer.toString(stars)));
            helper.setText(
                ((message == null || message.isEmpty()) ? "No se ha recibido ningún comentario" : message));
        
            javaMailSender.send(mail);
            
        } catch (MessagingException | MailException e) {
            LOG.error("[EMAIL] Error enviando email (" + e.getMessage() + ")");
        }
    }
    
    /**
     * Metodo que envia un email con la tienda sugerida.
     * @param shop: nombre de la tienda sugerida.
     * @param link: link de la tienda.
     */
    public void sendShopSuggestion(String shop, String link)
    {
        MimeMessage mail = javaMailSender.createMimeMessage();
        
        try 
        {
            MimeMessageHelper helper = new MimeMessageHelper(mail);
                
            helper.setTo(
                Properties.SHOP_SUGGESTION_EMAIL_FROM);
            helper.setFrom(
                Properties.SHOP_SUGGESTION_EMAIL_FROM);
            helper.setSubject(
                Properties.SHOP_SUGGESTION_EMAIL_SUBJECT);
            helper.setText(
                " - Nombre de la tienda: " + shop.toUpperCase() + "\n - Link: " + ((link == null || link.isEmpty()) ? "No especificado" : link));
        
            javaMailSender.send(mail);
            
        } catch (MessagingException | MailException e) {
            LOG.error("[EMAIL] Error enviando email (" + e.getMessage() + ")");
        }
    }
    
    /**
     * Metodo que envía un correo con las estadísticas del scraping.
     * @param shop: nombre de la tienda.
     * @param total: total de productos.
     * @param newProducts: numero de productos nuevos.
     * @param sameProducts: numero de productos iguales.
     * @param obsoleteProducts: numero de productos obsoletos.
     */
    public void sendScrapingStatsEmail(String shop, int total, int newProducts, int sameProducts, int obsoleteProducts)
    {
        MimeMessage mail = javaMailSender.createMimeMessage();
        
        try 
        {
            MimeMessageHelper helper = new MimeMessageHelper(mail);
                
            String message = " - Total de productos recibidos: " + total + "\n"
                + " - Productos nuevos: " + newProducts + "\n"
                + " - Productos iguales: " + sameProducts + "\n"
                + " - Productos obsoletos: " + obsoleteProducts + "\n";
            
            helper.setTo(
                Properties.SCRAPING_STATS_EMAIL_FROM);
            helper.setFrom(
                Properties.SCRAPING_STATS_EMAIL_FROM);
            helper.setSubject(
                Properties.SCRAPING_STATS_EMAIL_SUBJECT.replace("?1", shop));
            helper.setText(message);
        
            javaMailSender.send(mail);
            
        } catch (MessagingException | MailException e) {
            LOG.error("[EMAIL] Error enviando email (" + e.getMessage() + ")");
        }
    }
}
