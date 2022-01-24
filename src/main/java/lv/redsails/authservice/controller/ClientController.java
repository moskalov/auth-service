package lv.redsails.authservice.controller;


import lombok.RequiredArgsConstructor;
import lv.redsails.authservice.model.request.RegistrationRequestBody;
import lv.redsails.authservice.security.utils.WebUtils;
import lv.redsails.authservice.service.AccountService;
import lv.redsails.authservice.service.RegistrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static org.springframework.http.MediaType.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/client")
public class ClientController {

    private final RegistrationService registrationService;
    private final AccountService accountService;

    @PostMapping(consumes = APPLICATION_JSON_VALUE, value = "/sign-up")
    public ResponseEntity<String> registration(@Valid @RequestBody RegistrationRequestBody registration) {
        registrationService.registerUserAccount(registration);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/sign-out")
    public ResponseEntity<Void> signOut(HttpServletRequest request, HttpServletResponse response) {
        WebUtils.removeCookie(request, response, "refresh");
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/emails/confirmation")
    public ResponseEntity<Void> confirmEmail(@RequestParam String token) {
        accountService.confirmEmailByToken(token);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/passwords/reset")
    public ResponseEntity<Void> passwordReset(@RequestParam String email) {
        accountService.sendPasswordResetEmail(email);
        return ResponseEntity.ok().build();
    }

}
