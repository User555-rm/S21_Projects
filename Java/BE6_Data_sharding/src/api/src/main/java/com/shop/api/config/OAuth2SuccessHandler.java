package com.shop.api.config;

import com.shop.api.model.User;
import com.shop.api.repository.UserRepository;
import com.shop.api.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final String succesUrl;

    public OAuth2SuccessHandler(UserRepository userRepository,
                                JwtService jwtService,
                                @Value("${app.oauth.success-redirect:http://localhost:8080/}")
                                String successUrl) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.succesUrl = successUrl;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        Object principal = authentication.getPrincipal();
        if(!(principal instanceof OAuth2User userInfo)) {
            throw new IllegalStateException("OAuth2 principal отсутствует");
        }

        String email = getString(userInfo.getAttribute("email"));
        String sub = getString(userInfo.getAttribute("sub")); // id Google
        String name = getString(userInfo.getAttribute("given_name"));
        String surname = getString(userInfo.getAttribute("family_name"));

        User user = userRepository.findByEmail(email).orElseGet(
                () -> {
                    User newuser = new User();
                    newuser.setName(name);
                    newuser.setEmail(email);
                    newuser.setSurname(surname);
                    newuser.setGoogleSub(sub);
                    return userRepository.save(newuser);
                });
        String token = jwtService.generateToken(user.getEmail());

        String target = succesUrl + (succesUrl.contains("?") ? "&" : "?")
                + "token=" + token;
        getRedirectStrategy().sendRedirect(request, response, target);
    }

    private String getString(Object o) {
        return o == null ? "" : o.toString();
    }
}
