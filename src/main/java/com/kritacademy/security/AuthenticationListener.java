package com.kritacademy.security;

import com.kritacademy.users.User;
import com.kritacademy.users.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.util.RandomValueStringGenerator;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

/**
 * @author krit on 8/14/2017.
 */
@Slf4j
@Component
public class AuthenticationListener implements ApplicationListener<InteractiveAuthenticationSuccessEvent> {
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private RandomValueStringGenerator randomValueStringGenerator = new RandomValueStringGenerator();

    @Override
    public void onApplicationEvent(InteractiveAuthenticationSuccessEvent interactiveAuthenticationSuccessEvent) {
        if (interactiveAuthenticationSuccessEvent.getAuthentication() instanceof OAuth2Authentication) {
            OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) interactiveAuthenticationSuccessEvent.getAuthentication();
            Map userDetails = (Map) oAuth2Authentication.getUserAuthentication().getDetails();
            log.info("[~] find one user using facebook id {}", userDetails.get("id"));
            Optional<User> user = userService.findOneByFacebookId( new BigInteger(userDetails.get("id").toString()));
            if (!user.isPresent()) {
                log.info("[!] user is not registered");
                log.info("[+] start register user facebook id {} to system", userDetails.get("id"));
                User newUser = new User();
                newUser.setEmail(userDetails.get("email").toString());
                String[] name = userDetails.get("name").toString().split(" ");
                String firstName = name[0];
                String lastName;
                if (name.length == 1) {
                    lastName = "";
                }
                else {
                    lastName = String.join(" ", Arrays.copyOfRange(name, 1, name.length));
                }
                newUser.setFirstName(firstName);
                newUser.setLastName(lastName);
                newUser.setPassword(passwordEncoder.encode(randomValueStringGenerator.generate()));
                userService.create(newUser);
            }
        }
    }
}
