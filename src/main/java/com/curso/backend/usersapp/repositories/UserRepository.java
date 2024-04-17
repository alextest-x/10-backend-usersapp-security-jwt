package com.curso.backend.usersapp.repositories;

import com.curso.backend.usersapp.models.entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

    //ORM es un mapeo de objeto relacional como hibernate, jpa
    //enviamos el sql a objetos

    Optional<User> findByUsername(String username);

    /*
       ejemplo
           @Query("select u from User u where u.username=?1 and u.email=?2")
           Optional<User> getUserNyUsername(String username, String email);
                                         campo username 1=?1, campo email 2=?2
     */

    //metodo personalizado
    //consulta a la clase entity un alias u que me regresa uel primer u
    // que va al <User> no a la tabla where el objeto u.username
    //por debajo tiene el getUsername() = al parametro ? que esta en (String username)
    //implementamos el JpaUserDetailsService.java
    @Query("select u from User u where u.username=?1")
    Optional<User> getUserByUsername(String username);




}
