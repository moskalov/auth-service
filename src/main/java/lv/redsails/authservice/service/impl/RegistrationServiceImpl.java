package lv.redsails.authservice.service.impl;

import lombok.RequiredArgsConstructor;
import lv.redsails.authservice.domain.Role;
import lv.redsails.authservice.domain.User;
import lv.redsails.authservice.model.request.RegistrationRequestBody;
import lv.redsails.authservice.event.RegistrationCompleteEvent;
import lv.redsails.authservice.exception.registration.EmailUsedException;
import lv.redsails.authservice.repository.RoleRepository;
import lv.redsails.authservice.repository.UserRepository;
import lv.redsails.authservice.security.model.PasswordEncoder;
import lv.redsails.authservice.service.RegistrationService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final ApplicationEventPublisher eventPublisher;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User registerUserAccount(RegistrationRequestBody registration) {
        throwIfEmailAlreadyExist(registration.getEmail());
        User newUser = createNewUser(registration);
        User nonActivatedUser = userRepository.save(newUser);
        publishRegistrationCompleteEvent(registration, nonActivatedUser);
        return nonActivatedUser;
    }

    private void throwIfEmailAlreadyExist(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) throw new EmailUsedException();
    }

    private User createNewUser(RegistrationRequestBody credentials) {
        String encodedPassword = passwordEncoder.encode(credentials.getPassword());
        String externalUid = DigestUtils.md5Hex(credentials.getEmail());
        Set<Role> defaultRoles = Set.of(roleRepository.findByName("CLIENT"));
        return new User().setEmail(credentials.getEmail())
                .setFirstName(credentials.getFirstName())
                .setLastName(credentials.getLastName())
                .setPassword(encodedPassword)
                .setRoles(defaultRoles)
                .setUid(externalUid);
    }

    private void publishRegistrationCompleteEvent(RegistrationRequestBody registration, User user) {
        eventPublisher.publishEvent(new RegistrationCompleteEvent(this, user, registration));
    }

}
