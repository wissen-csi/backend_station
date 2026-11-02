package com.station.project.infrastructure.components;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.station.project.domain.enumerations.TemplateEnum;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter 
@Component 
@RequiredArgsConstructor 
public class MailManager {
    private final JavaMailSender javaMailSender;
    private  final TemplateEngine templateEngine;
    @Value("${spring.mail.username}")
    private  String sender;
    
    public void sendMail(String mail, String message, String title) throws MessagingException{
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
        messageHelper.setTo(mail);
        messageHelper.setText(message);
        messageHelper.setFrom(sender);
        messageHelper.setText(title);
        javaMailSender.send(mimeMessage);
    }
    public void sendMail(String mail, String title, Context context, TemplateEnum type) throws MessagingException{
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
        messageHelper.setTo(mail);
        messageHelper.setText(templateEngine.process(type.name(), context),true);
        messageHelper.setFrom(sender);
        messageHelper.setText(title);
        javaMailSender.send(mimeMessage);
    }
}
