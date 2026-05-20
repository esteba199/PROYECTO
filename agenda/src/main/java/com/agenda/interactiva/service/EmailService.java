package com.agenda.interactiva.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Servicio para el envío de correos electrónicos a través del servidor SMTP de Mailjet.
 * 
 * ¿Por qué usamos JavaMailSender?
 * Es el cliente de correo oficial de Spring Boot. Nos permite construir y enviar correos de forma sencilla.
 * 
 * ¿Por qué usamos MimeMessage?
 * A diferencia de SimpleMailMessage, MimeMessage permite estructurar correos en formato HTML,
 * lo que es vital para enviar plantillas elegantes con diseño premium.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from}")
    private String fromEmail;

    /**
     * Envía un correo electrónico en formato HTML de forma segura.
     * En caso de fallo (por ejemplo, si no se han configurado credenciales reales de Mailjet),
     * captura la excepción y la registra en logs para no interrumpir el flujo de la aplicación.
     */
    public void sendEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            // MimeMessageHelper ayuda a configurar campos adjuntos, destinatarios y contenido HTML
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true indica que el texto es HTML

            mailSender.send(message);
            log.info("Correo electrónico enviado con éxito a: {}", to);
        } catch (Exception e) {
            log.error("Fallo al enviar correo a {}. Razón: {}", to, e.getMessage());
            log.warn("Nota: Verifica que las credenciales de Mailjet (API Key y Secret) sean válidas en tu entorno.");
        }
    }

    /**
     * Plantilla de Correo de Bienvenida.
     */
    public void sendWelcomeEmail(String to, String username) {
        String subject = "¡Te damos la bienvenida a tu Agenda Interactiva Inteligente!";
        String content = "<html>" +
                "<body style='font-family: Arial, sans-serif; background-color: #090a0f; color: #ffffff; padding: 20px;'>" +
                "<div style='max-width: 600px; margin: auto; background: rgba(22, 27, 45, 0.9); border: 1px solid rgba(255,255,255,0.1); border-radius: 12px; padding: 30px; box-shadow: 0 10px 30px rgba(0,0,0,0.5);'>" +
                "<h1 style='color: #66fcf1; text-align: center; border-bottom: 1px solid rgba(255,255,255,0.1); padding-bottom: 15px;'>¡Bienvenido, " + username + "!</h1>" +
                "<p style='font-size: 16px; line-height: 1.6;'>Estamos encantados de tenerte en <strong>Agenda Interactiva Inteligente</strong>. Tu cuenta ha sido creada exitosamente con el correo: <strong>" + to + "</strong>.</p>" +
                "<p style='font-size: 16px; line-height: 1.6;'>A partir de ahora podrás:</p>" +
                "<ul style='font-size: 15px; line-height: 1.6; color: #a0aec0;'>" +
                "  <li>Crear y editar citas en tu calendario dinámico.</li>" +
                "  <li>Configurar alertas de correo automatizadas para que nunca olvides una reunión.</li>" +
                "  <li>Escribir notas rápidas estilo post-it.</li>" +
                "  <li>Hacer un seguimiento exhaustivo de tus tareas diarias.</li>" +
                "</ul>" +
                "<div style='text-align: center; margin-top: 30px;'>" +
                "  <a href='http://localhost:8080' style='background: #66fcf1; color: #000000; padding: 12px 24px; text-decoration: none; font-weight: bold; border-radius: 6px;'>Ir a mi Agenda</a>" +
                "</div>" +
                "<p style='font-size: 12px; color: #718096; text-align: center; margin-top: 40px;'>Este es un correo automático. Por favor, no respondas a este mensaje.</p>" +
                "</div>" +
                "</body>" +
                "</html>";

        sendEmail(to, subject, content);
    }
}
