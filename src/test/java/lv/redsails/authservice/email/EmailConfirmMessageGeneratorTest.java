package lv.redsails.authservice.email;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static javax.mail.Message.*;

@ExtendWith(MockitoExtension.class)
class EmailConfirmMessageGeneratorTest {
    EmailMessageGenerator emailMessageGenerator = new EmailMessageGenerator(null, null);
    JavaMailSender mailSender = new JavaMailSenderImpl();

    Faker faker = new Faker();
    JsonMapper mapper = new JsonMapper();

    @Test
    void shouldGenerateMessageWithSpecifiedEmail() throws MessagingException {
        String actualRecipientEmail = faker.internet().emailAddress();
        MimeMessage message = emailMessageGenerator
                .generateEmailConfirmationMessage(mailSender)
                .setRecipientEmail(actualRecipientEmail)
                .setFullName("Artem Moskalov")
                .setToken("random token")
                .getMessage();

        Address email = message.getRecipients(RecipientType.TO)[0];
        Assertions.assertEquals(actualRecipientEmail, email.toString());
    }

    @Test
    void shouldInsertConformationTokenAndFullNameInTextTemplate() throws MessagingException, IOException {
        String actualRecipientFullName = faker.name().fullName();
        String confirmationToken = UUID.randomUUID().toString();

        MimeMessage message = emailMessageGenerator
                .generateEmailConfirmationMessage(mailSender)
                .setRecipientEmail("mail@mail.com")
                .setFullName(actualRecipientFullName)
                .setToken(confirmationToken)
                .getMessage();

        Map<String, String> dataFromTemplate = getInsertedDataInTemplate(message);
        Assertions.assertEquals(actualRecipientFullName, dataFromTemplate.get("FullName"));
        Assertions.assertEquals(confirmationToken, dataFromTemplate.get("ConfirmationToken"));
    }

    private Map<String, String> getInsertedDataInTemplate(MimeMessage message) throws MessagingException, IOException {
        InputStream inputStream = message.getInputStream();
        TypeReference<HashMap<String, String>> typeRef = new TypeReference<>() {
        };
        return mapper.readValue(inputStream, typeRef);
    }


}