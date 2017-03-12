package com.kritacademy;

import com.kritacademy.authorities.Authority;
import com.kritacademy.authorities.AuthorityService;
import com.kritacademy.storage.StorageService;
import com.kritacademy.users.User;
import com.kritacademy.users.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@SpringBootApplication
public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);
    @Autowired
    UserService userService;
    @Autowired
    AuthorityService authorityService;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	CommandLineRunner init(StorageService storageService) {
		return (args) -> {
			storageService.deleteAll();
			storageService.init();

            Authority roleAdmin = new Authority();
            roleAdmin.setName("ROLE_ADMIN");
            roleAdmin = authorityService.create(roleAdmin);
            logger.info("[+] create authority: ", roleAdmin);

            Authority roleUser = new Authority();
            roleUser.setName("ROLE_USER");
            authorityService.create(roleUser);
            logger.info("[+] create authority: ", roleUser);

			User user = new User();
			user.setFirstName("user");
			user.setEmail("user01@kritacademy.com");
			user.getAuthorities().add(roleUser);
			user.setPassword("test");
			user = userService.create(user);
			logger.info("[+] create user: ", user);

			User user2 = new User();
			user2.setFirstName("usertwo");
            user2.setEmail("user02@kritacademy.com");
            user2.getAuthorities().add(roleUser);
            user2.getAuthorities().add(roleAdmin);
            user2.setPassword("test");
            user2 = userService.create(user2);
            logger.info("[+] create user: ", user2);

			User admin = new User();
			admin.setFirstName("admin");
			admin.setEmail("admin@kritacademy.com");
			admin.getAuthorities().add(roleUser);
			admin.getAuthorities().add(roleAdmin);
			admin.setPassword("admin");
			admin = userService.create(admin);
			logger.info("[+] create user: ", admin);
		};
	}
	@GetMapping("/")
	public String home(){
		return "index";
	}
}
