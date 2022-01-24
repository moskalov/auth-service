package lv.redsails.authservice.listener;

import lombok.AllArgsConstructor;
import lv.redsails.authservice.event.RegistrationCompleteEvent;
import lv.redsails.authservice.service.AccountService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class RegistrationListener implements ApplicationListener<RegistrationCompleteEvent> {

    private final AccountService accountService;

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
        String email = event.getUser().getEmail();
        String clientConfirmUrl = event.getRegistration().getEmailConfirmUrl();
        accountService.sendConfirmationEmail(email, clientConfirmUrl);
    }

}
