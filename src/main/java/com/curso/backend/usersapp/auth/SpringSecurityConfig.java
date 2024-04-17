package com.curso.backend.usersapp.auth;


//import com.curso.backend.usersapp.auth.filters.JwtAuthenticationFilter;
//import com.curso.backend.usersapp.auth.filters.JwtValidationFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


//import com.andres.backend.usersapp.backendusersapp.auth.filters.JwtAuthenticationFilter;
//import com.andres.backend.usersapp.backendusersapp.auth.filters.JwtValidationFilter;

import com.curso.backend.usersapp.auth.filters.JwtAuthenticationFilter;
import com.curso.backend.usersapp.auth.filters.JwtValidationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.ArrayList;
import java.util.Arrays;


@Configuration
public class SpringSecurityConfig {

    //1. crear un metodo que retorne un filterChain
    //una cadena de spring security con todas las reglas de auntentificacion autorizacion
    //@Bean crea un componente de spring de configuracion un nuevo filtro personalizado
    //cuando un @Bean esta dentro de una clase @Configuration el regresa el metodo
    //se guarda en el contexto de spring como un componente como un bean  parecido una clase de servicio
    //lo que regresa el metodo anotada con @Bean regresa un nuevo componente

    /*
       .csrf para evitar exploit o vulnerabilidades en los formularios generando un token de segridad y validando
      luego con un filtro de spring cuando se envia el formulario
     */

    //2. crear un filter de autentificacion para hacer login formulario
    // no apiRest solo en un monolitico en un mvc
    //aqui esperamos un json en el cuerpo de request

    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;


    /*
      BCryptPasswordEncoder genera varios pasword solo encripta no se puede desencriptar
      solo envia
     */

    //80
    //@Bean para PasswordEncoder para que no salga error en el log there is no PasswordEncoder mapped for the id "null"
    // no hay un password encode mapeado
    //falta hacer el passwordEncoder este bean regresa un passwordEncoder este es solo para tester

    @Bean
    PasswordEncoder passwordEncoder(){
        //return NoOpPasswordEncoder.getInstance();
        //utilizar el BCryptPasswordEncoder para encryptar
        return new BCryptPasswordEncoder();

    }

    //81 crear el autentication manager con un bean
    //con el authenticationConfiguration lamamos el getAuthenticationManager();
    // y lo guardamos como un componente de spring
    @Bean
    AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();

    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

         return http.authorizeHttpRequests()
                 .requestMatchers(HttpMethod.GET, "/users").permitAll() // permite a todos en users solo cuando es GET
                 .requestMatchers(HttpMethod.GET, "/users/{id}").hasAnyRole("USER", "ADMIN")
                 .requestMatchers(HttpMethod.POST, "/users").hasRole("ADMIN")
                 .requestMatchers("/users/**").hasRole("ADMIN")
                 // .requestMatchers(HttpMethod.DELETE, "/users/{id}").hasRole("ADMIN")
                 // .requestMatchers(HttpMethod.PUT, "/users/{id}").hasRole("ADMIN")
                 .anyRequest().authenticated()   //requiere auntentificacion
                 .and()
                 //configurando a spring security.addFilter (instancia de nuestro filtro) para que funcione llamar el getAuthenticationManager()
                 .addFilter(new JwtAuthenticationFilter(authenticationConfiguration.getAuthenticationManager()))
                 .addFilter(new JwtValidationFilter(authenticationConfiguration.getAuthenticationManager()))
                 //.csrf()solo formularios dentro de spring hay que dehabilitarlo
                 .csrf(config -> config.disable())
                 //guarda en la sesion http en mvs y en un api se guarda en un token
                 .sessionManagement(managment -> managment.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                 .cors(cors-> cors.configurationSource(corsConfigurationSource()))
                 .build();

    }


    //configurando cors
    @Bean
    CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        config.setAllowCredentials(true);


        //configura la ruta
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    FilterRegistrationBean<CorsFilter> corsFilter(){
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(
                new CorsFilter(corsConfigurationSource()));
             bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
             return bean;
     }

}
