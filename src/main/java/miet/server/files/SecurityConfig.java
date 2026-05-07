package miet.server.files;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${app.security.csrf.enabled:true}")
    private boolean csrfEnabled;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/hello", "/css/**", "/js/**", "/login", "/webjars/**").permitAll()
                        .requestMatchers("/dashboard/**", "/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/client/**").hasAnyRole("ADMIN", "CLIENT")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/dashboard", true)
                        .permitAll()
                )
                .httpBasic(Customizer.withDefaults());

        if (!csrfEnabled) {
            http.csrf(csrf -> csrf.disable());
        }

        return http.build();
    }
}