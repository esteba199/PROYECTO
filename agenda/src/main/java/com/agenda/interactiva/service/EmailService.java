package com.agenda.interactiva.service;

import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.resource.Emailv31;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Servicio para el envío de correos electrónicos utilizando la API REST de Mailjet.
 */
@Service
@Slf4j
public class EmailService {

    @Value("${spring.mail.username}")
    private String apiKey;

    @Value("${spring.mail.password}")
    private String apiSecret;

    @Value("${app.mail.sender}")
    private String fromEmail;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    /**
     * Envía un correo electrónico en formato HTML de forma asíncrona usando la API de Mailjet.
     */
    @Async
    public void sendEmail(String to, String subject, String htmlContent) {
        try {
            log.info("Intentando enviar correo a: {} con asunto: '{}'", to, subject);
            log.info("Usando API REST de Mailjet con remitente: {}", fromEmail);
            
            MailjetClient client = new MailjetClient(apiKey, apiSecret, new ClientOptions("v3.1"));

            MailjetRequest request = new MailjetRequest(Emailv31.resource)
                .property(Emailv31.MESSAGES, new JSONArray()
                    .put(new JSONObject()
                        .put(Emailv31.Message.FROM, new JSONObject()
                            .put("Email", fromEmail)
                            .put("Name", "Agenda Interactiva"))
                        .put(Emailv31.Message.TO, new JSONArray()
                            .put(new JSONObject()
                                .put("Email", to)))
                        .put(Emailv31.Message.SUBJECT, subject)
                        .put(Emailv31.Message.HTMLPART, htmlContent)
                    ));

            MailjetResponse response = client.post(request);
            
            if (response.getStatus() == 200) {
                log.info("✅ Correo electrónico enviado con éxito a: {}", to);
            } else {
                log.error("❌ Fallo al enviar correo a {}. Status: {}, Data: {}", to, response.getStatus(), response.getData());
            }
        } catch (Exception e) {
            log.error("❌ Excepción al enviar correo a {}. Razón: {}", to, e.getMessage(), e);
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
                "  <a href='" + baseUrl + "' style='background: #66fcf1; color: #000000; padding: 12px 24px; text-decoration: none; font-weight: bold; border-radius: 6px;'>Ir a mi Agenda</a>" +
                "</div>" +
                "<p style='font-size: 12px; color: #718096; text-align: center; margin-top: 40px;'>Este es un correo automático. Por favor, no respondas a este mensaje.</p>" +
                "</div>" +
                "</body>" +
                "</html>";

        sendEmail(to, subject, content);
    }

    /**
     * Plantilla de Correo de Recordatorio.
     */
    public void sendReminderEmail(String to, String username, com.agenda.interactiva.model.Event event) {
        String subject = "Recordatorio: " + event.getTitle();
        String eventDate = event.getStartTime().toLocalDate().toString();
        String eventTime = event.getStartTime().toLocalTime().toString();
        
        String content = "<html>" +
                "<body style='font-family: Arial, sans-serif; background-color: #090a0f; color: #ffffff; padding: 20px;'>" +
                "<div style='max-width: 600px; margin: auto; background: rgba(22, 27, 45, 0.9); border: 1px solid rgba(255,255,255,0.1); border-radius: 12px; padding: 30px; box-shadow: 0 10px 30px rgba(0,0,0,0.5);'>" +
                "<h2 style='color: #66fcf1; border-bottom: 1px solid rgba(255,255,255,0.1); padding-bottom: 10px;'>¡Hola, " + username + "!</h2>" +
                "<p style='font-size: 16px; line-height: 1.6;'>Este es un recordatorio de tu próximo evento programado en tu <strong>Agenda Interactiva</strong>.</p>" +
                "<div style='background: rgba(255,255,255,0.05); padding: 15px; border-radius: 8px; margin: 20px 0; border-left: 4px solid " + event.getColor() + ";'>" +
                "  <h3 style='margin-top: 0; color: #fff;'>" + event.getTitle() + "</h3>" +
                "  <p style='margin: 5px 0; color: #a0aec0;'><strong>Fecha:</strong> " + eventDate + " a las " + eventTime + "</p>" +
                "  <p style='margin: 5px 0; color: #a0aec0;'><strong>Lugar:</strong> " + (event.getLocation() != null ? event.getLocation() : "No especificado") + "</p>" +
                "  <p style='margin: 5px 0; color: #a0aec0;'>" + (event.getDescription() != null ? event.getDescription() : "") + "</p>" +
                "</div>" +
                "<div style='text-align: center; margin-top: 30px;'>" +
                "  <a href='" + baseUrl + "/calendario' style='background: #66fcf1; color: #000000; padding: 12px 24px; text-decoration: none; font-weight: bold; border-radius: 6px;'>Ver en Calendario</a>" +
                "</div>" +
                "<p style='font-size: 12px; color: #718096; text-align: center; margin-top: 40px;'>Este es un correo automático de tu Agenda Interactiva Inteligente.</p>" +
                "</div>" +
                "</body>" +
                "</html>";

        sendEmail(to, subject, content);
    }
}
