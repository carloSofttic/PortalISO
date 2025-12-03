package com.normasiso.normaiso9001.config;

import com.normasiso.normaiso9001.security.login.CustomAuthSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService uds,
                                                         PasswordEncoder encoder) {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider();
        p.setUserDetailsService(uds);
        p.setPasswordEncoder(encoder);
        return p;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           AuthenticationProvider authProvider,
                                           CustomAuthSuccessHandler successHandler) throws Exception {

        http
            // ==== CSRF ====
            // Solo ignoramos el endpoint específico de checkout para POST,
            // sin usar AntPathRequestMatcher (evitamos warning de deprecated)
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/checkout/intent")
            )

            // ==== Autenticación personalizada (SqlUserDetailsService + BCrypt) ====
            .authenticationProvider(authProvider)

            // ==== Autorización por rutas y roles ====
            .authorizeHttpRequests(auth -> auth
                // Público
                .requestMatchers(
                    "/login",
                    "/register",
                    "/css/**",
                    "/js/**",
                    "/img/**",
                    "/memberships",
                    "/checkout/**"
                ).permitAll()

                // Solo ADMINISTRADOR y SGI pueden gestionar usuarios y licencias
                .requestMatchers("/usuarios/**", "/licencias/**", "/usersCrud/**", "admin/**")
                    .hasAnyRole("ADMINISTRADOR", "SGI")

                // El resto requiere estar autenticado
                .anyRequest().authenticated()
            )

            // ==== Login ====
            .formLogin(form -> form
                .loginPage("/login").permitAll()
                // tu handler decide si va a onboarding o dashboard
                .successHandler(successHandler)
            )

            // ==== Logout ====
            .logout(logout -> logout
                .logoutUrl("/logout")                    // URL del form de logout
                .logoutSuccessUrl("/login?logout")       // a dónde mandar después
                .invalidateHttpSession(true)             // invalida la sesión
                .clearAuthentication(true)               // limpia el contexto
                .deleteCookies("JSESSIONID")             // elimina cookie de sesión
                .permitAll()
            );

        return http.build();
    }
}
