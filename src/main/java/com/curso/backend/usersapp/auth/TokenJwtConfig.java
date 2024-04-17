package com.curso.backend.usersapp.auth;

import java.security.Key;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;



//clase para usar como cosntantes
public class TokenJwtConfig {


    
    //token en duro
    //public final static String SECRET_KEY = "algun token con una frase secreta";
    //public final static String PREFIX_TOKEN = "Bearer ";
    //public final static String HEADER_AUTHORIZATION = "Authorization";
    



    //crear el token
    //Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);



    public final static Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    public final static String PREFIX_TOKEN = "Bearer ";
    public final static String HEADER_AUTHORIZATION = "Authorization";

}
