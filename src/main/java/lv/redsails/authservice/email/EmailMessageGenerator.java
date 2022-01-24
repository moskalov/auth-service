package lv.redsails.authservice.email;

import lombok.SneakyThrows;
import lv.redsails.authservice.email.message.ConfirmEmailMessage;
import lv.redsails.authservice.email.message.PasswordResetMessage;
import lv.redsails.authservice.utils.ResourcesReader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;

@Component
public class EmailMessageGenerator {

    private final String emailConfirmTemplate;
    private final String passwordResetTemplate;

    @SneakyThrows
    public EmailMessageGenerator() {
        emailConfirmTemplate = ResourcesReader.getResourceFileAsString("mail/email_confirmation.html");
        passwordResetTemplate = ResourcesReader.getResourceFileAsString("mail/password_reset.html");
    }

    @SneakyThrows
    public ConfirmEmailMessage generateEmailConfirmationMessage(JavaMailSender mailSender) {
        MimeMessageHelper messageHelper = getMessageHelper(mailSender);
        messageHelper.setFrom("no-reply@mtracker.com");
        messageHelper.setSubject("Email Confirmation");
        return new ConfirmEmailMessage(messageHelper, emailConfirmTemplate);
    }

    @SneakyThrows
    public PasswordResetMessage generatePasswordResetMessage(JavaMailSender mailSender) {
        MimeMessageHelper messageHelper = getMessageHelper(mailSender);
        messageHelper.setFrom("no-reply@mtracker.com");
        messageHelper.setSubject("Password Reset");
        return new PasswordResetMessage(messageHelper, passwordResetTemplate);
    }

    private MimeMessageHelper getMessageHelper(JavaMailSender mailSender) {
        MimeMessage message = mailSender.createMimeMessage();
        return new MimeMessageHelper(message, "utf-8");
    }


}
