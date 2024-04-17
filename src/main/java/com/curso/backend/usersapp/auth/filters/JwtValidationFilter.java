package com.curso.backend.usersapp.auth.filters;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;


import com.curso.backend.usersapp.auth.SimpleGrantedAuthorityJsonCreator;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.GrantedAuthority;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

//import javax.crypto.SecretKey;
//import java.io.IOException;
//import com.curso.backend.usersapp.auth.TokenJwtConfig;


import static com.curso.backend.usersapp.auth.TokenJwtConfig.*;

//import static io.jsonwebtoken.Jwts.*;
//import static java.util.Base64.getDecoder;

//filtro para validar token jwtValidationFilter
  //1 crear la clase jwtValidationFilter
  //2 extender de BasicAuthenticationFilter
  //3 crear el constructor JwtValidationFilter(AuthenticationManager authenticationManager)
  //4 implementar el metodo @Overrrider doFilterInternal
  //4.1 hacer cabeceras

 //BasicAuthenticationFilter simpre se ejecuta en cada request

public class JwtValidationFilter extends BasicAuthenticationFilter {


    //private static final long serialVersionUID = -5116101128118950844L;

    public JwtValidationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {

        //obtener las cabeceras
        String header = request.getHeader(HEADER_AUTHORIZATION);

        //validar si Authorization tiene la palabra Bearer que si tiene token
        //porque se ejecuta GET, POST en cada request
        //si es nulo se sale O si no empieza con Bearer es diferente que Bearer se sale
        //pero antes de salir continua con la cadena de los filtros con el request
        // para listar a los usuarios
        if (header == null || !header.startsWith(PREFIX_TOKEN)) {
            chain.doFilter(request, response);
            return;
        }
        //obtener el token hay que quitar la palabra Bearer de la cabecera Authorization
        //quitamos el prefijo Bearer para que solo quede la cadena del token
        //String token = header.replace(PREFIX_TOKEN, "");


      /**********************************************************/
        //inicio de comentario para implementar el token jwt


        //System.out.println("token = " + token);

        //obtener la palabra secreta donde se creo el token JwtAuthenticationFilter
        //para compararla con el token que nos estan envaindo
        //entonces hay que decodificar con Base64.getDecoder
        //pero el decode retorna bytes y requiere una cadena
        //hacemos un arrgelo de byte y la convertimos a un string

        //byte[] tokenDecodeBytes = Base64.getDecoder().decode(token);
        //String tokenDecode = new String(tokenDecodeBytes);


        //System.out.println("tokenDecodeBytes byte[] : " + tokenDecodeBytes); //[B@6ec5091
        //System.out.println("tokenDecodeBytes byte[] Arrays.toString: " + Arrays.toString(tokenDecodeBytes));
        //[97, 108, 103, 117, 110]

        //System.out.println("tokenDecode-frase :" + tokenDecode);

        //obtenemos del token la palabra secreta y el username
        //con el split divide en cada punto con el patron (.)


         /*
           para punto (.) tiene que ser de la siguiente forma:
           tokenDecode.split("\\.") hay que escapar el punto con doble backslash \\.
           porque es (.) un caracter resevado en una expresion regular,
           que representa cualquier cracter
         */

        //String[] tokenArr = tokenDecode.split("\\.");
        //String[] tokenArr = tokenDecode.split(":");
        //System.out.println("tokenArr: " + Arrays.toString(tokenArr));
        //System.out.println("tokenArr: " + Arrays.toString(tokenArr));


        //String secret = tokenArr[0];
        //String username = tokenArr[1];

        //System.out.println("secret: " + secret);
        //System.out.println("username: " + username);

        // fin  de comentario para implementar e token jwt

        /**************************************************************/

        String token = header.replace(PREFIX_TOKEN, "");


        try {

           // Claims claims = (Claims) (Claims)(Jwts.parser().verifyWith((SecretKey) SECRET_KEY).build().parseSignedClaims(token));



            //implementando el jwt
            //Claims claims = parser()
			Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();



            /*
            //implementando el jwt
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parserClaimsJws(token)
                    .getBody();
            */


            //regresa el username en un string
            //String username = claims.getSubject();

            //regresa un objecto
            //Object username2 = claims.get("username"); //sale un null


            // comparamos el secret la ponemos en una constante en SECRET_KEY
            // if("algun token con una frase secreta".equals(secret)){

            //lo quitamos por que se implemnta el try catch par el token jwt
            //if (SECRET_KEY.equals(secret)) {

            //si es igual nos authenticamos
            //comentamos porque el role ROLE_USER esta en duro
            //hay que ponerlo que venga en el token
            //List<GrantedAuthority> authorities = new ArrayList<>();
            //authorities.add(new SimpleGrantedAuthority("ROLE_USER"));


            //ponemos que el rol venga del token
            //comentamos porque aqui esta en duro y lo pasamos a una variable
            //
            //List<GrantedAuthority> authorities = new ArrayList<>();
            //authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

            Object authoritiesClaims = claims.get("authorities");
            String username = claims.getSubject();
            Object username2 = claims.get("username");
            System.out.println(username);
            System.out.println(username2);


            //Arrays.asList() convierte un arreglo de java a una lista
            //que va a tener los roles de tipo GrantedAuthority y lo obtenemos de authoritiesClaims
            //pero hay que convertirlo de un json a un objecto con el objectMapper un json como string y luego hay que convertir
            //a byte para poblar de tipo SimpleGrantedAuthority tipo de dato
            //authorities viene del token
            //crear una clase para addMixIn para conbinar con una nueva y agregamos un constructor
			 Collection<? extends GrantedAuthority> authorities = Arrays
                    .asList(new ObjectMapper()
           
                            .addMixIn(SimpleGrantedAuthority.class, SimpleGrantedAuthorityJsonCreator.class)
                            .readValue(authoritiesClaims.toString().getBytes(), SimpleGrantedAuthority[].class));


            //ponemos null en el password porque solo vamos a generar el token no es necesario el password
             UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null,
                    authorities);




            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response);
            //} else {   //lo quitamos por que se implementa el try catch par el token jwt
        } catch (JwtException e) {
            //el token no es igual ala palabra secreta
            Map<String, String> body = new HashMap<>();
            body.put("error", e.getMessage());
            body.put("message", "El token JWT no es valido");

            response.getWriter().write(new ObjectMapper().writeValueAsString(body));
            //ponemos 401 cuando hay error en la validacion del token o se termina la sesion
            //el token ya no es igual
            response.setStatus(401); //no Autorizado
            response.setContentType("application/json");

        }
    }
}





