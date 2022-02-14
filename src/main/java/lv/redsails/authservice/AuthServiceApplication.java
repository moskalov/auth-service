package lv.redsails.authservice;

import javax.servlet.Filter;

import lv.redsails.authservice.domain.Role;
import lv.redsails.authservice.domain.User;
import lv.redsails.authservice.properties.ApplicationProperties;
import lv.redsails.authservice.properties.ExternalPropertiesLoader;
import lv.redsails.authservice.security.model.PasswordEncoder;
import lv.redsails.authservice.service.RolesManagementService;
import lv.redsails.authservice.service.UserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;

import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@SpringBootApplication
public class AuthServiceApplication {


    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }

    @Bean
    @Transactional
    CommandLineRunner applicationStartInitialization(
            @Qualifier("springSecurityFilterChain") Filter springSecurityFilterChain,
            RolesManagementService rolesService,
            ExternalPropertiesLoader loader,
            PasswordEncoder encoder,
            UserService userService) {

        return args -> {
            ApplicationProperties appProperty = loader.readProperty(ApplicationProperties.class);

            if (appProperty.getIsAppFirstStart()) {
                List<Role> defaultRoles = createDefaultRoles();
                List<Role> roles = rolesService.createRoles(defaultRoles);
                Role adminRole = findAdminRole(roles);

                User defaultAdmin = createDefaultAdmin(encoder);
                defaultAdmin.setRoles(Set.of(adminRole));
                userService.createUser(defaultAdmin);

                showFilters(springSecurityFilterChain);
                appProperty.setIsAppFirstStart(false);
                loader.updateProperty(appProperty);
            }
        };
    }

    public List<Role> createDefaultRoles() {
        return Arrays.asList(
                new Role().setId(null).setName("ADMIN"),
                new Role().setId(null).setName("CLIENT"),
                new Role().setId(null).setName("SERVICE")
        );
    }

    public User createDefaultAdmin(PasswordEncoder encoder) {
        return new User().setEmail("admin")
                .setUid(UUID.randomUUID().toString())
                .setPassword(encoder.encode("admin"))
                .setEnabled(true);
    }

    private Role findAdminRole(List<Role> roles) {
        return roles.stream()
                .filter(role -> role.getName().equals("ADMIN"))
                .findFirst().orElseThrow(() -> new RuntimeException("ADMIN ROLE NOT FOUND"));
    }

    public void showFilters(Filter springSecurityFilterChain) {
        FilterChainProxy filterChainProxy = (FilterChainProxy) springSecurityFilterChain;
        List<SecurityFilterChain> list = filterChainProxy.getFilterChains();
        list.stream()
                .flatMap(chain -> chain.getFilters().stream())
                .forEach(filter -> System.out.println(filter.getClass()));
    }

}
