package com.curso.backend.usersapp.auth.filters;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

//import io.jsonwebtoken.ClaimsBuilder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.curso.backend.usersapp.models.entities.User;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


//import java.util.Base64;
//import java.util.Date;


import static com.curso.backend.usersapp.auth.TokenJwtConfig.*;
//import static io.jsonwebtoken.Jwts.*;


//filtro UsernamePasswordAuthenticationFilter maneja una ruta url login
//e implementamos los overrrider attemptAuthentication, successfulAuthentication, unsuccessfulAuthentication
// se ejecuta cuando el metodo del request sean un POST y la ruta url sea un login
// controla cada request con un filter

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;


    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;

    }

    //hace la auntenticacion attemptAuthentication obtiene los datos del request que vienen en json
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        //este user es el que viene del entity
        User user = null;
        String username = null;
        String password = null;

        //capturamos los datos viene en el cuerpo del request en estructura json
        //con request.getInputStream() mediante el ObjectMapper
        //readValue toma los datos y los puebla en User.class
        //objectMapper traer el objeto

        try {
            user = new ObjectMapper().readValue(request.getInputStream(), User.class);

               //asignamos los datos
               username = user.getUsername();
               password = user.getPassword();

               //muestra username: admin, password: 12345 hay que comentar en produccion
               //logger.info("Username desde el request InputStream (raw)" + username);
               //logger.info("Password desde el request InputStream (raw)" + password);

            } catch (StreamReadException e) {
              e.printStackTrace();

            } catch (DatabindException e) {
              e.printStackTrace();

            } catch (IOException e) {
              e.printStackTrace();
        }


        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

        return authenticationManager.authenticate(authToken); //aqui pasa un username y el password token
    }


    //hace la aunticacion cuando sale bien
    //regresa un token y genera un respuesta para el cliente en react
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
	           Authentication authResult) throws IOException, ServletException {

        //hacemos un casts
        //hay que importar el UserCore de security
        //String username = authResult.getPrincipal().getUsername();
        //hacemos un casts se castea authResult.getPrincipal()) al objeto de (org springframeworñ.security)
        // para obtener el getUsername() de spring no es el username entity(models.entities) es el de spring security
        //String username = ((org.springframework.security.core.userdetails.User) authResult.getPrincipal()).getUsername();

        //aqui utilizamos el Base64 para encodificar el token
        //y hacemos un cast con .getbytes()
        //String token = Base64.getEncoder().encodeToString(("algun token con una frase secreta." + username).getBytes());
        //hacemos un token manual

        //la comentamos porque el texto lo ponemos en una constante
        //String originalInput = "algun token con una frase secreta" + username;

        /*
        //se comenta porque se remplaza para hacer el token Jwt
        String originalInput = SECRET_KEY + ":" + username;
        String token = Base64.getEncoder().encodeToString(originalInput.getBytes());
        */

        //generar el token Jwt
        String username = ((org.springframework.security.core.userdetails.User) authResult.getPrincipal())
                .getUsername();
        //authResult.getAuthorities();

        //regresa una lista de objetos que hereda de la interface GrantedAuthority los roles que viene de la BD
        //hay que pasar los roles al token
        Collection<? extends GrantedAuthority> roles = authResult.getAuthorities();

        //para saber si es Administrador comparamos con anyMatch es un predicate
        //si encuentra que un rol es igual a ROLE_ADMIN regresa un true y lo pasamos a un claim
        //para el token
        boolean isAdmin = roles.stream().anyMatch(r-> r.getAuthority().equals("ROLE_ADMIN"));

        //pasando los roles que vienen del token pero como claims son data que enviamos en el token
        //pero viene como objeto hay que pasarlo a un json
        //Claims claims = Jwts.claims();

        Claims claims = Jwts.claims();


        //Claims claims = (Claims) (Claims) Jwts.parser()

         /*
         class io.jsonwebtoken.impl.DefaultClaimsBuilder
         cannot be cast to class io.jsonwebtoken.Claims
         (io.jsonwebtoken.impl.DefaultClaimsBuilder and io.jsonwebtoken.Claims are in unnamed module of loader 'app')
         */

        //guardamos con put los roles
        //ObjectMapper() escribe el valor como un string
        //writeValueAsString(roles) lo convierte a un json
        //pasando a un json los roles
        claims.put("authorities", new ObjectMapper().writeValueAsString(roles));
        claims.put("isAdmin", isAdmin);

        //pasando a un json los roles
        //claims.put("authorities", new ObjectMapper().writeValueAsString(roles));
        //claims.put("isAdmin", isAdmin);

        //claims trae el autorities = ROLE_ADMIN

        claims.put("username", username);
		
         //pasando los claims aqui en el token
		 //setExpiration(new Date(new Date().getTime() + 3600000))
		 //mas una hora para que expire el token
		 
        String token = Jwts.builder()
                .setClaims(claims)  
                .setSubject(username)
                .signWith(SECRET_KEY)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
        .compact();





        //regresar una respuesta al usuario http repsonse con el token y pasar el token en las cabeceras

        //la pasamos a una constante
        //response.addHeader("Autorization", "Bearer " + token);

        response.addHeader(HEADER_AUTHORIZATION, PREFIX_TOKEN + token);

        //pasando json en el body en la respuesta
        //%s es un patron indicando que es un string

        Map<String, Object> body = new HashMap<>();
        body.put("token", token);
        body.put("message", String.format("Hola %s, has iniciado sesion con exito!", username));
        body.put("username", username);
        //body.put(" ", new String(String.valueOf(new Date()))); envia la fecha


        //guadamos el hashMap el body en el response
        // y pasamos el new objectMapper para convertir el objecto en un json
        // y lo pasamos en el cuerpo de la respuesta


        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        response.setStatus(200);
        response.setContentType("application/json");

    }



    //hace la aunticacion cuando hay error
    //regresa un menaje de error
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
	    AuthenticationException failed) throws IOException, ServletException {

        Map<String, Object> body= new HashMap<>();
        body.put("message", "Error en la autenticacion username o password incorrecto!");
		//se puede poner la fecha
        body.put("error", failed.getMessage());

        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
		//no autorizado
        response.setStatus(401);
        response.setContentType("application/json");

    }


}
