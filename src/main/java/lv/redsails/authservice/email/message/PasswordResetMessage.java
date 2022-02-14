package lv.redsails.authservice.email.message;

import lombok.Setter;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.stringtemplate.v4.ST;

import javax.mail.internet.MimeMessage;

import static lombok.AccessLevel.NONE;

@Setter
@ToString
@Accessors(chain = true)
public class PasswordResetMessage implements EmailMessage {

    @Setter(NONE)
    private MimeMessageHelper messageHelper;
    private String passwordResetUrl;
    private String recipientEmail;
    private String fullName;
    private String token;

    @Setter(NONE)
    private String textTemplate;

    public PasswordResetMessage(MimeMessageHelper helper, String template) {
        this.messageHelper = helper;
        this.textTemplate = template;
    }

    @Override
    @SneakyThrows
    public MimeMessage getMessage() {
        String text = generatePasswordResetMail();
        messageHelper.setText(text, true);
        messageHelper.setTo(recipientEmail);
        return messageHelper.getMimeMessage();
    }

    private String generatePasswordResetMail() {
        String confirmationUrl = passwordResetUrl + "?token=" + token;
        ST body = new ST(textTemplate, '$', '$');
        body.add("full_name", fullName);
        body.add("reset_password_url", confirmationUrl);
        return body.render();
    }

}
