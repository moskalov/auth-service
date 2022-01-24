package lv.redsails.authservice.security.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import lombok.SneakyThrows;
import org.springframework.http.MediaType;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

public class WebUtils {

    public static void removeCookie(HttpServletRequest request, HttpServletResponse response, String cookieName) {
        Cookie[] cookies = Optional.ofNullable(request.getCookies()).orElse(new Cookie[]{});
        Optional<Cookie> refreshToken = Arrays.stream(cookies)
                .filter(cookie -> cookieName.equals(cookie.getName()))
                .findFirst();

        refreshToken.ifPresent(cookie -> {
            cookie.setMaxAge(0);
            cookie.setValue("");
            response.addCookie(cookie);
        });
    }

    public static Optional<String> extractCookieValue(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = Optional.ofNullable(request.getCookies()).orElse(new Cookie[]{});
        return Arrays.stream(cookies)
                .filter(cookie -> cookieName.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }

    @SneakyThrows
    public static Optional<Object> extractBody(HttpServletRequest request, Class<?> clazz) {
        try (InputStream inputStream = request.getInputStream()) {
            return Optional.of(new ObjectMapper().readValue(inputStream, clazz));
        } catch (MismatchedInputException e) {
            return Optional.empty();
        }
    }

    public static void putJsonInPayload(HttpServletResponse response, Map<String, String> jsonFields) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        OutputStream stream = response.getOutputStream();
        new ObjectMapper().writeValue(stream, jsonFields);
        stream.close();
    }

}
