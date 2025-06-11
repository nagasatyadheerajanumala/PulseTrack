package com.spring.pulsetrackbackend.service;

import com.spring.pulsetrackbackend.model.Monitor;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendAlert(String to, Monitor monitor, int statusCode, long responseTime) {
        String subject = "⚠️ Alert: Monitor \"" + monitor.getName() + "\" is DOWN";

        String body = String.format(
                "Dear User,%n%n" +
                        "This is to inform you that one of your monitors is currently facing an issue.%n%n" +
                        "🔍 Monitor Details:%n" +
                        "• Name         : %s%n" +
                        "• URL          : %s%n" +
                        "• Checked At   : %s%n" +
                        "• Status Code  : %d%n" +
                        "• Response Time: %d ms%n%n" +
                        "🚨 Issue:%n" +
                        "The endpoint responded with a non-200 status code (%d), which likely indicates a failure or downtime.%n%n" +
                        "✅ Recommended Action:%n" +
                        "Please investigate the endpoint or check related server logs for more details.%n%n" +
                        "Regards,%n" +
                        "PulseTrack Monitoring System",
                monitor.getName(),
                monitor.getUrl(),
                LocalDateTime.now(),
                statusCode,
                responseTime,
                statusCode
        );

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }
}