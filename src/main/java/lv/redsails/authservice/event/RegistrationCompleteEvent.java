package lv.redsails.authservice.event;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lv.redsails.authservice.domain.User;
import lv.redsails.authservice.model.request.RegistrationRequestBody;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
@Accessors(chain = true)

public class RegistrationCompleteEvent extends ApplicationEvent {
    private User user;
    private RegistrationRequestBody registration;

    public RegistrationCompleteEvent(Object source, User user, RegistrationRequestBody registration) {
        super(source);
        this.user = user;
        this.registration = registration;
    }
}