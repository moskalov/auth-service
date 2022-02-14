package lv.redsails.authservice.controller;


import lombok.RequiredArgsConstructor;
import lv.redsails.authservice.model.request.ForgotPasswordRequestBody;
import lv.redsails.authservice.model.request.PasswordResetRequestBody;
import lv.redsails.authservice.model.request.RegistrationRequestBody;
import lv.redsails.authservice.security.utils.WebUtils;
import lv.redsails.authservice.service.AccountService;
import lv.redsails.authservice.service.RegistrationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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

    @PostMapping(consumes = APPLICATION_JSON_VALUE, value = "/register")
    public ResponseEntity<Void> registration(@Valid @RequestBody RegistrationRequestBody registration) {
        registrationService.registerUserAccount(registration);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping(value = "/sign-out")
    public ResponseEntity<Void> signOut(HttpServletRequest request, HttpServletResponse response) {
        WebUtils.removeCookie(request, response, "refresh");
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/confirmation/email")
    public ResponseEntity<Void> confirmEmail(@RequestParam String token) {
        accountService.confirmEmailByToken(token);
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/reset-password")
    public ResponseEntity<Void> confirmNewPassword(@RequestBody @Validated PasswordResetRequestBody requestBody) {
        accountService.confirmPasswordReset(requestBody.getToken(), requestBody.getPassword());
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/forgot-password")
    public ResponseEntity<Void> requestPasswordReset(@RequestBody @Validated ForgotPasswordRequestBody requestBody) {
        accountService.sendPasswordResetEmail(requestBody.getEmail(), requestBody.getResetUrl());
        return ResponseEntity.ok().build();
    }

}
