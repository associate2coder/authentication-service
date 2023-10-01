package ua.com.associate2coder.authenticationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration(proxyBeanMethods = false)
public class TestAuthenticationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.from(AuthenticationServiceApplication::main).with(TestAuthenticationServiceApplication.class).run(args);
    }

}
