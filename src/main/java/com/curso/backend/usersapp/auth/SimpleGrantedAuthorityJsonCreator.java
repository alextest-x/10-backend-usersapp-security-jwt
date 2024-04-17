package com.curso.backend.usersapp.auth;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

//es una clase mix una clase abstracta
public abstract class SimpleGrantedAuthorityJsonCreator {


    //ponemos el constructor
    @JsonCreator
    public SimpleGrantedAuthorityJsonCreator(@JsonProperty("authority") String role){


    }

}
