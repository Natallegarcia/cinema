package com.fatesg.cinema.config;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Configuracao do Spring Security.
 * - ADMIN: acesso aos cadastros (filmes, sessoes, salas, usuarios).
 * - USER (cliente): catalogo, compra de ingressos e "meus ingressos".
 * - Web: login por formulario. API (/api/**): HTTP Basic, stateless.
 */
@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/api/**")
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // consulta ao catalogo e compra: qualquer usuario autenticado
                .requestMatchers(HttpMethod.GET, "/api/filmes/**", "/api/sessoes/**").authenticated()
                .requestMatchers("/api/compras/**").authenticated()
                // demais operacoes (cadastros): somente ADMIN
                .anyRequest().hasRole("ADMIN"))
            .httpBasic(basic -> {})
            .exceptionHandling(ex -> ex.authenticationEntryPoint(
                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf
                .ignoringRequestMatchers(new AntPathRequestMatcher("/h2-console/**")))
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/css/**", "/js/**").permitAll()
                .requestMatchers("/filmes/**", "/sessoes/**", "/salas/**", "/usuarios/**",
                        "/h2-console/**").hasRole("ADMIN")
                .anyRequest().authenticated())
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler(this::redirecionarAposLogin)
                .permitAll())
            .exceptionHandling(ex -> ex.accessDeniedHandler((req, res, e) ->
                res.sendRedirect(req.getContextPath() + "/catalogo?acessoNegado")))
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll());
        return http.build();
    }

    /** ADMIN vai para os cadastros; cliente vai para o catalogo. */
    private void redirecionarAposLogin(HttpServletRequest request, HttpServletResponse response,
                                       Authentication authentication) throws IOException {
        boolean admin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        response.sendRedirect(request.getContextPath() + (admin ? "/filmes" : "/catalogo"));
    }
}
