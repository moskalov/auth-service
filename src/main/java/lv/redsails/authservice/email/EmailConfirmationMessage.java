package lv.redsails.authservice.email;

import lombok.SneakyThrows;
import lv.redsails.authservice.utils.ResourcesReader;
import org.hibernate.pretty.MessageHelper;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.internet.MimeMessage;

public class EmailConfirmationMessage {
    private String confirmationToken;
    private String recipientMail;
    private String recipientFullName;


    public EmailConfirmationMessage setConfirmationToken(String token) {
        this.confirmationToken = token;
        return this;
    }

    public EmailConfirmationMessage setRecipientMail(String mail) {
        this.recipientMail = mail;
        return this;
    }

    @SneakyThrows
    private String loadConfirmationHtmlTemplate(String fullName, String confirmationToken) {
        return ResourcesReader.getResourceFileAsString("mail/email_confirmation.html");
    }

    @SneakyThrows
    public MimeMessage buildEmail() {
        // messageHelper.setTo(this.recipientMail);
        // messageHelper.setText("Your confirmation token - " + confirmationToken, true);
        // messageHelper.setSubject("Confirm Your Account");
        // messageHelper.setFrom("no-reply@mtracker.com");
        MimeMessageHelper messageHelper = null;
        return messageHelper.getMimeMessage();
    }

}
