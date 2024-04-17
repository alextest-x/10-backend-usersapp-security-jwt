package com.curso.backend.usersapp.services;




import java.util.List;
import java.util.Optional;


import com.curso.backend.usersapp.models.dto.UserDto;
import com.curso.backend.usersapp.models.entities.User;
import com.curso.backend.usersapp.models.request.UserRequest;
public interface UserService {

    //findAll() pude ser cualquier nombre
    List<UserDto> findAll();


    //el optional valida
    Optional<UserDto> findById(Long id);


    //el save regresa el objeto entity ya actualizado
    //le pasamos un User y user guarda el id del json
    //si tiene el id lo actualiza sino hace un insert
     UserDto save(User user);

     //otra forma de actualizar al usuario pasando la logica del controlador
     //al service y lo implementamos en el ServiceUserImpl


    //comentamos el update porque vamos a gregar
    //UserRequest por que solo va a validar el username y el email
    //en la clase UserRequest

    //Optional<User> update(User user, Long id);

    //aqui pasamos el UserRequest.
    //hay que tambien modificar el UserServiceImpl
    Optional<UserDto> update(UserRequest user, Long id);


     void remove(Long id);

}
