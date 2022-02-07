package lv.redsails.authservice.email.message;

import lombok.Setter;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.stringtemplate.v4.ST;

import javax.mail.internet.MimeMessage;

import static lombok.AccessLevel.*;

@Setter
@ToString
@Accessors(chain = true)
public class ConfirmEmailMessage implements EmailMessage {

    @Setter(NONE)
    private MimeMessageHelper messageHelper;
    private String clientEmailConfirmUrl;
    private String recipientEmail;
    private String fullName;
    private String token;

    @Setter(NONE)
    private String textTemplate;

    public ConfirmEmailMessage(MimeMessageHelper helper, String template) {
        this.messageHelper = helper;
        this.textTemplate = template;
    }

    @Override
    @SneakyThrows
    public MimeMessage getMessage() {
        String text = generateConfirmationMailHtml();
        messageHelper.setTo(recipientEmail);
        messageHelper.setText(text, true);
        return messageHelper.getMimeMessage();
    }

    private String generateConfirmationMailHtml() {
        String confirmationUrl = clientEmailConfirmUrl + "?token=" + token;
        ST body = new ST(textTemplate, '$', '$');
        body.add("full_name", fullName);
        body.add("confirmation_url", confirmationUrl);
        return body.render();
    }

}

