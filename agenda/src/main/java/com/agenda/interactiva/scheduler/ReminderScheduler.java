package com.agenda.interactiva.scheduler;

import com.agenda.interactiva.model.Reminder;
import com.agenda.interactiva.model.User;
import com.agenda.interactiva.model.UserConfig;
import com.agenda.interactiva.repository.ReminderRepository;
import com.agenda.interactiva.service.EmailService;
import com.agenda.interactiva.service.UserConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Tarea Automática Programada para procesar recordatorios de eventos.
 * 
 * ¿Por qué usamos @Scheduled?
 * Es el motor de tareas automáticas de Spring. Permite ejecutar un método en base a intervalos
 * de tiempo específicos (como cada minuto o usando expresiones Cron).
 * 
 * ¿Cómo funciona?
 * 1. Cada minuto, consulta los recordatorios en la base de datos que aún no han sido enviados ('isSent = false')
 *    y cuya fecha planificada de alerta es anterior o igual al momento actual.
 * 2. Si el usuario del evento tiene activado el recibimiento de notificaciones en sus configuraciones,
 *    le envía un correo electrónico formateado en HTML con la información de la cita.
 * 3. Marca el recordatorio como enviado ('isSent = true') para evitar envíos duplicados.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ReminderScheduler {

    private final ReminderRepository reminderRepository;
    private final EmailService emailService;
    private final UserConfigService userConfigService;

    // Se ejecuta de forma continua cada 60,000 milisegundos (1 minuto)
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void processReminders() {
        LocalDateTime now = LocalDateTime.now();
        
        // Consultamos la base de datos buscando alertas pendientes de enviar
        List<Reminder> pendingReminders = reminderRepository.findByIsSentFalseAndNotificationTimeBefore(now);

        if (pendingReminders.isEmpty()) {
            return;
        }

        log.info("ReminderScheduler: Procesando {} recordatorios pendientes...", pendingReminders.size());

        for (Reminder reminder : pendingReminders) {
            try {
                User user = reminder.getEvent().getUser();
                UserConfig config = userConfigService.getOrInitConfig(user);

                // Comprobamos si el usuario desea alertas por correo electrónico
                if (Boolean.TRUE.equals(config.getEmailNotificationsEnabled())) {
                    sendEventReminderEmail(reminder);
                } else {
                    log.info("ReminderScheduler: Recordatorio ID {} omitido por preferencias del usuario {}", 
                            reminder.getId(), user.getUsername());
                }

                // Marcamos el recordatorio como procesado
                reminder.setIsSent(true);
                reminderRepository.save(reminder);

            } catch (Exception e) {
                log.error("ReminderScheduler: Error al procesar el recordatorio ID: {}. Razón: {}", 
                        reminder.getId(), e.getMessage());
            }
        }
    }

    // Construye la plantilla HTML del correo del recordatorio y la envía
    private void sendEventReminderEmail(Reminder reminder) {
        String to = reminder.getEvent().getUser().getEmail();
        String eventTitle = reminder.getEvent().getTitle();
        String eventStart = reminder.getEvent().getStartTime().toString();
        String location = reminder.getEvent().getLocation() != null ? reminder.getEvent().getLocation() : "Sin ubicación especificada";

        String subject = "Recordatorio de Evento: " + eventTitle;
        String content = "<html>" +
                "<body style='font-family: Arial, sans-serif; background-color: #090a0f; color: #ffffff; padding: 20px;'>" +
                "<div style='max-width: 600px; margin: auto; background: rgba(22, 27, 45, 0.9); border: 1px solid rgba(255,255,255,0.1); border-radius: 12px; padding: 30px; box-shadow: 0 10px 30px rgba(0,0,0,0.5);'>" +
                "<h2 style='color: #66fcf1; border-bottom: 1px solid rgba(255,255,255,0.1); padding-bottom: 10px; text-align: center;'>Recordatorio de Actividad</h2>" +
                "<p style='font-size: 16px; line-height: 1.6;'>Hola, te recordamos que tienes una actividad agendada próximamente:</p>" +
                "<div style='background: rgba(255, 255, 255, 0.05); padding: 20px; border-radius: 8px; margin: 20px 0; border-left: 4px solid #66fcf1;'>" +
                "  <h3 style='margin: 0; color: #ffffff; font-size: 18px;'>" + eventTitle + "</h3>" +
                "  <p style='margin: 10px 0 0 0; color: #a0aec0; font-size: 14px;'><strong>Fecha y Hora:</strong> " + eventStart + "</p>" +
                "  <p style='margin: 4px 0 0 0; color: #a0aec0; font-size: 14px;'><strong>Ubicación:</strong> " + location + "</p>" +
                "</div>" +
                "<p style='font-size: 14px; color: #a0aec0; text-align: center;'>¡Gracias por usar la Agenda Interactiva Inteligente!</p>" +
                "</div>" +
                "</body>" +
                "</html>";

        emailService.sendEmail(to, subject, content);
    }
}
