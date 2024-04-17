package com.curso.backend.usersapp.services;

import java.util.List;

import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.curso.backend.usersapp.repositories.UserRepository;


@Service
public class JpaUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository repository;


    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //implemnetamos la consulta que esta en el UserRepository findByUsername()
        //Optional<com.curso.backend.usersapp.models.entities.User> o = repository.findByUsername(username);

        //implemnetamos la consulta personalizada con JPA que esta en el UserRepository getUserByUsername()
        Optional<com.curso.backend.usersapp.models.entities.User> o = repository.getUserByUsername(username);

        if (!o.isPresent()) {
            throw new UsernameNotFoundException
                    (String.format("Username %s no esta registrado en el sistema", username));

        }
        //obtenemos el usuario de la base de datos
        com.curso.backend.usersapp.models.entities.User user = o.orElseThrow();


/*    se comenta porque es es un busca el usuario en codigo

        //regresar un usuario authenticate que esta en el repositorio.

        //test emula que busca al usuario en la base de datos
        //sino esta lanza la exception
        if(!username.equals("admin")) {
            throw new UsernameNotFoundException(String.format("Username no esta registrado en el sistema", username));

        }
*/

        //true 1 usuario habilitado
        //true 2 la cuenta no espira
        //true 3 las crednciales no espira
        //true 4 no esta bloqueado
        //authorities son los roles

        //creamos una lista de roles authorities
        //busca en codigo duro hay que pasarlo a que busque en la base de datos
        //List<GrantedAuthority> authorities = new ArrayList<>();
        //authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        //busca en la base de datos pero hay que hacer un cast porque es imcompatible porque espera una lista  List<GrantedAuthority>
        //y getName() es un stream() de tipo object
        //List<GrantedAuthority> authorities = user.getRoles().stream().map( r-> new SimpleGrantedAuthority(r.getName()));


        List<GrantedAuthority> authorities = user.getRoles()  //obtiene los roles
                .stream() //llamamos el api stream
                .map(r-> new SimpleGrantedAuthority(r.getName())) //r.getName le pasamos el nombre del constructor
                //el map para modificar por cada rol crean un nuevo flujo un nuevo stream con instancia de SimpleGrantedAuthority(r.getName())) lo implementa la interface
                //y no de tipo rol = r
                .collect(Collectors.toList()); //convierte a una lista


        //aqui le pasamos los datos de spring security

        return new User(
                user.getUsername(),
                user.getPassword(),
                true,
                true,
                true,
                true,
                authorities);
    }

        /*
          se comenta porque lo ponemos ya el objeto para ir a buscar el username a la bd

        return new User(username,
                //"1234",
                "$2a$10$DOMDxjYyfZ/e7RcBfUpzqeaCs8pLgcizuiQWXPkU35nOhZlFcE9MS",
                true,
                true,
                true,
                true,
                authorities);

    }  */
}
