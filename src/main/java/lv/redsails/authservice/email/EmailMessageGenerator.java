package lv.redsails.authservice.email;

import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lv.redsails.authservice.email.message.EmailConfirmMessage;
import lv.redsails.authservice.email.message.PasswordResetMessage;
import lv.redsails.authservice.utils.ResourcesReader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Accessors(chain = true)
public class EmailMessageGenerator {

    private final EmailOptions emailConfirmation;
    private final EmailOptions passwordReset;

    private String emailConfirmTemplate;
    private String passwordResetTemplate;

    public EmailMessageGenerator(EmailOptions emailConfirmation, EmailOptions passwordReset) {
        this.passwordReset = passwordReset;
        this.emailConfirmation = emailConfirmation;
        readTemplates();
    }

    @SneakyThrows
    private void readTemplates() {
        emailConfirmTemplate = Files.readString(Path.of(emailConfirmation.getTemplatePath()));
        passwordResetTemplate = Files.readString(Path.of(passwordReset.getTemplatePath()));
    }

    @SneakyThrows
    public EmailConfirmMessage generateEmailConfirmationMessage(JavaMailSender mailSender) {
        MimeMessageHelper messageHelper = getMessageHelper(mailSender);
        messageHelper.setFrom(emailConfirmation.getFrom());
        messageHelper.setSubject(emailConfirmation.getSubject());
        return new EmailConfirmMessage(messageHelper, emailConfirmTemplate);
    }

    @SneakyThrows
    public PasswordResetMessage generatePasswordResetMessage(JavaMailSender mailSender) {
        MimeMessageHelper messageHelper = getMessageHelper(mailSender);
        messageHelper.setFrom(passwordReset.getFrom());
        messageHelper.setSubject(passwordReset.getSubject());
        return new PasswordResetMessage(messageHelper, passwordResetTemplate);
    }

    private MimeMessageHelper getMessageHelper(JavaMailSender mailSender) {
        MimeMessage message = mailSender.createMimeMessage();
        return new MimeMessageHelper(message, "utf-8");
    }

}
