package com.curso.backend.usersapp.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


import com.curso.backend.usersapp.models.IUser;
import com.curso.backend.usersapp.models.dto.UserDto;
import com.curso.backend.usersapp.models.dto.mapper.DtoMapperUser;
import com.curso.backend.usersapp.models.entities.Role;
import com.curso.backend.usersapp.models.entities.User;
import com.curso.backend.usersapp.models.request.UserRequest;

import com.curso.backend.usersapp.repositories.RoleRepository;
import com.curso.backend.usersapp.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/*
aqui implementamos los metodos abstractos de la inteface UserService
e implementamos los metodos con @overrider lo hace el ide en automatico
y anotamos que es un componente @Service
inyectamos el UserRepository para hacer uso del repository.find() etc
*/

/*
  Los DTO o objeto de trnsferencia de datos ( en ingles data transfer object
  son un tipo de objetos que sirven unicamente para trasportar datos en la red, remotas
  (por ejemplo servicios web) datos formateados, modificados, simplificados, optimizados
  con cierta informacion de entidades que necesitamos incluir puede ser de un solo entity
  o mas de una combinadas en un solo DTO.
*/

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository repository;

    //inyectamos
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
   private PasswordEncoder passwordEncoder;

/*

    //el findAll(); es del tipo iterable hay que hacer un cast hay que convertirlo a un lista
    // en lugar de regresar un clase entity tambien se puede recibir el List<User> en un DTO
    // para regresar solo lo que necesitemos no el objeto completo
    //por ejemplo direccion solo la calle y el numero y no con todos sus atributos
    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return (List<User>) repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return repository.findById(id);
    }

*/

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
        List<User> users = (List<User>) repository.findAll();
        return users
                .stream()
                .map(u -> DtoMapperUser.builder().setUser(u).build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDto> findById(Long id) {
        return repository.findById(id).map(u -> DtoMapperUser
                .builder()
                .setUser(u)
                .build());

    }
	

    //el metodo save regresa el metodo guardado
    @Override
    @Transactional
    public UserDto save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(getRoles(user));


        //aqui encriptamos el password
        //porque viene la clave del usuario que no viene encriptada del cliente
        //inyectamos el PasswordEncoder

        //llamamos al metodo
        List<Role> roles = getRoles(user);

        //lo pasamos aun metodo getRoles()
    /*
        //ou rol usuario
        Optional<Role> ou = roleRepository.findByName("ROLE_USER");

        List<Role> roles = new ArrayList<>();

        //roles.add(new Role("ROLE_USER"));
        //buscamos a la BD pero hay que validar primero que exista en la BD

        if(ou.isPresent()){
             roles.add(ou.orElseThrow());
         }

        //si es admin entonces buscamos el rol a la base de datos
        //
        if(user.isAdmin()){
            //oa rol admin
            Optional<Role> oa = roleRepository.findByName("ROLE_ADMIN");
            if(oa.isPresent()){
                roles.add(oa.orElseThrow());
            }

        }
    */

        //si esta presente le pasamos los roles
        //user.setRoles(roles);
        //return repository.save(user);

        return DtoMapperUser.builder().setUser(repository.save(user)).build();
    }

    /*
    //primera forma del update x-update
    @Override
    public Optional<User> update(User user, Long id) {
          //pasando la locica que teniamos en el controlador al service

        Optional<User> o = this.findById(id);
        if (o.isPresent()){
            //obteniendo el user de la base de datos con userDb para guardar ya actualizado
            User userDb = o.orElseThrow();

            //le modificamos los datos que solo usamos del RequestBody user
            userDb.setUsername(user.getUsername());
            userDb.setEmail(user.getEmail());
            //el .of encuantra el usuario
            return Optional.of(this.save(userDb));

        }

        return Optional.empty();
    }
    */

    //de otra forma el update
    @Override
    @Transactional
    public Optional<UserDto> update(UserRequest user, Long id) {

    //comentamos porque se agrega el UserRequest para que solo valide en username y el email
    //tambien se agrego en la inteface de UserService
    //public Optional<User> update(User user, Long id) {

        Optional<User> o = repository.findById(id);
        User userOptional = null;

        //busca el usuario si esta se agrega un rol o se actualiza
        //hace un set o una nueva lista de los roles y borra la que estaba en la bd
        //con los roles dependiendo del checkbox
        if (o.isPresent()){

        /*
        lo pasamos a un metodo getRoles()
            //ou rol usuario
            Optional<Role> ou = roleRepository.findByName("ROLE_USER");

            List<Role> roles = new ArrayList<>();

            //roles.add(new Role("ROLE_USER"));
            //buscamos a la BD pero hay que validar primero que exista en la BD

            if(ou.isPresent()){
                roles.add(ou.orElseThrow());
            }

            //si es admin entonces buscamos el rol a la base de datos
            //
            if(user.isAdmin()){
                //oa rol admin
                Optional<Role> oa = roleRepository.findByName("ROLE_ADMIN");
                if(oa.isPresent()){
                    roles.add(oa.orElseThrow());
                }

            }
        */

            //obteniendo el user de la base de datos con userDb para guardar ya actualizado
            User userDb = o.orElseThrow();

            //le modificamos los datos que solo usamos del RequestBody user
            userDb.setRoles(getRoles(user)); //pasamos los roles
            userDb.setUsername(user.getUsername());
            userDb.setEmail(user.getEmail());
            userOptional = repository.save(userDb);

        }

        // ofNullable pregunta si el userOptional es nulo
        // regresa un optional empty
        // sino regresa un optional of con el valor userOptional
        //return Optional.ofNullable(userOptional);
	      return Optional.ofNullable(DtoMapperUser.builder().setUser(userOptional).build());
    }


    @Override
    @Transactional
    public void remove(Long id) {
        repository.deleteById(id);
    }

    private List<Role> getRoles(IUser user) {

        //ou rol usuario
        Optional<Role> ou = roleRepository.findByName("ROLE_USER");

        List<Role> roles = new ArrayList<>();

        //roles.add(new Role("ROLE_USER"));
        //buscamos a la BD pero hay que validar primero que exista en la BD

        if (ou.isPresent()) {
            roles.add(ou.orElseThrow());
        }

        //si es admin entonces buscamos el rol a la base de datos
        //
        if (user.isAdmin()) {
            //oa rol admin
            Optional<Role> oa = roleRepository.findByName("ROLE_ADMIN");
            if (oa.isPresent()) {
                roles.add(oa.orElseThrow());
            }

        }
        return roles;
    }
}
