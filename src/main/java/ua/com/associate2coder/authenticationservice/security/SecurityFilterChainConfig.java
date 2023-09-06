package ua.com.associate2coder.authenticationservice.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityFilterChainConfig {

    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                //.httpBasic(AbstractHttpConfigurer::disable)
                //.cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(handing -> handing
                        .authenticationEntryPoint(restAuthenticationEntryPoint) // Handles auth error
                )
                .headers(AbstractHttpConfigurer::disable)           // for Postman, the H2 console
                .headers(
                        headerConfig ->
                                headerConfig.frameOptions(
                                        HeadersConfigurer.FrameOptionsConfig::disable))           // for Postman, the H2 console
                .authorizeHttpRequests((authorize) -> authorize                     // manage access
                                .requestMatchers("/api/v1/auth/**").permitAll()
                                .requestMatchers("/api/v1/email/confirmation/**").permitAll()
                                .requestMatchers("/actuator/shutdown").permitAll()      // needs to run test
                                .anyRequest().authenticated()
                        // other matchers
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // no session
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore
                        (
                                jwtAuthenticationFilter,
                                UsernamePasswordAuthenticationFilter.class
                        )

                // other configurations
                .build();
    }
}
