package com.fastariam.config;

import com.fastariam.model.Usuario;
import com.fastariam.repository.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UsuarioRepository usuarioRepo;

    public SecurityConfig(UsuarioRepository usuarioRepo) {
        this.usuarioRepo = usuarioRepo;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**","/js/**","/img/**","/favicon.ico").permitAll()
                .requestMatchers("/h2-console/**").hasRole("ADMINISTRADOR")
                .requestMatchers("/admin/**").hasRole("ADMINISTRADOR")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login").defaultSuccessUrl("/dashboard",true)
                .failureUrl("/login?error=true").permitAll()
            )
            .logout(logout -> logout.logoutSuccessUrl("/login?logout=true").permitAll())
            .exceptionHandling(ex -> ex.accessDeniedHandler((request, response, denied) ->
                    response.sendRedirect(request.getContextPath() + "/dashboard?semAcesso")))
            .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
            .headers(h -> h.frameOptions(fo -> fo.sameOrigin()));
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            Usuario u = usuarioRepo.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));
            if (!u.isAtivo()) throw new UsernameNotFoundException("Usuário inativo");
            return new org.springframework.security.core.userdetails.User(
                    u.getUsername(), u.getSenha(),
                    List.of(new SimpleGrantedAuthority("ROLE_" + u.getPerfil().name()))
            );
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
