package lv.redsails.authservice.config;

import lombok.RequiredArgsConstructor;
import lv.redsails.authservice.properties.ExternalPropertiesLoader;
import lv.redsails.authservice.properties.CorsProperties;
import lv.redsails.authservice.security.filter.authentication.EmailPasswordAuthenticationFilter;
import lv.redsails.authservice.security.filter.authentication.TokenAuthenticationFilter;
import lv.redsails.authservice.jwt.JwtTokensManager;
import lv.redsails.authservice.security.provider.EmailPasswordAuthenticationProvider;
import lv.redsails.authservice.security.provider.RefreshTokenAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


import static org.springframework.security.config.http.SessionCreationPolicy.*;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtTokensManager jwtTokensManager;
    private final EmailPasswordAuthenticationProvider emailPasswordAuthenticationProvider;
    private final RefreshTokenAuthenticationProvider refreshTokenAuthenticationProvider;
    private final ExternalPropertiesLoader configurationLoader;

    private final String signInUrl = "/api/v1/client/sign-in";

    @Override
    protected void configure(AuthenticationManagerBuilder managerBuilder) {
        managerBuilder
                .authenticationProvider(emailPasswordAuthenticationProvider)
                .authenticationProvider(refreshTokenAuthenticationProvider);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .sessionManagement().sessionCreationPolicy(STATELESS)
                .and().cors();

        http.authorizeRequests()
                .antMatchers("/api/v1/client/**").permitAll()
                .antMatchers("/api/v1/admin/**").authenticated()
                .anyRequest().authenticated();

        initializeFilters(http);
    }


    private void initializeFilters(HttpSecurity http) throws Exception {
        EmailPasswordAuthenticationFilter emailPasswordAuthenticationFilter = getEmailPasswordAuthentication();
        TokenAuthenticationFilter tokenAuthenticationFilter = getAuthenticationFilter();
        http.addFilterBefore(tokenAuthenticationFilter, EmailPasswordAuthenticationFilter.class);
        http.addFilter(emailPasswordAuthenticationFilter);
    }

    private EmailPasswordAuthenticationFilter getEmailPasswordAuthentication() throws Exception {
        EmailPasswordAuthenticationFilter emailPasswordAuthenticationFilter =
                new EmailPasswordAuthenticationFilter(
                        authenticationManagerBean(),
                        jwtTokensManager);

        emailPasswordAuthenticationFilter.setFilterProcessesUrl(signInUrl);
        return emailPasswordAuthenticationFilter;
    }

    private TokenAuthenticationFilter getAuthenticationFilter() throws Exception {
        TokenAuthenticationFilter authenticationFilter =
                new TokenAuthenticationFilter(
                        authenticationManagerBean(),
                        jwtTokensManager);

        authenticationFilter.setFilterProcessesUrl(signInUrl);
        return authenticationFilter;
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final CorsProperties properties = configurationLoader.readProperty(CorsProperties.class);
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(properties.getAllowedOrigins());
        configuration.setAllowedMethods(properties.getAllowMethods());
        configuration.setAllowCredentials(properties.getAllowedCredentials());
        configuration.setAllowedHeaders(properties.getAllowedHeader());
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}
