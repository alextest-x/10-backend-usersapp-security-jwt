package com.curso.backend.usersapp.models.entities;

import java.util.List;

import com.curso.backend.usersapp.models.IUser;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;




//se anota con entity para indicar que es una clase de persistencia
@Entity
@Table(name="users")
public class User implements IUser {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
/*
    //@Column(unique = true) campo unico no se puede repetir
    //este es cuando se crea la tabla de forma automatica con el ddl-auto
    //sino hay que ponerlo en el esquema de la base de datos mysql

    //reglas de validacion
    //para agregar la anotacio  @NotEmpty hay que agregar una dependencia en el pom.xml
    //I/O validation de java hibernate
    //@NotEmpty es para string valida solo que no sea vacio
    //@NotBlank es para string valida que no sea vacio y en espacios que no esten en blanco
    //@NotNull es para un no string o un objeto


*/

    @NotBlank
    @Size(min = 4, max = 8)
    @Column(unique = true)
    private String username;

    //@NotEmpty //lo cambiamos para que valide que no tenga espacios vacios y que no esten en blanco
    @NotBlank
    //@Size(min = 4, max = 8)
    private String password;


    @NotEmpty
    @Email
    @Column(unique = true)
    private String email;


    //el usuario tiene los roles
    @ManyToMany
    @JoinTable(
	        name="users_roles",
            joinColumns = @JoinColumn(name = "user_id"),  //la llave foranea de user_id
            inverseJoinColumns = @JoinColumn(name = "role_id"), //la llave foranea de role_id
            uniqueConstraints = { @UniqueConstraint(columnNames = { "user_id", "role_id" })}) // se ponen las llaves
            //porque no se repiten para que un usaurio no tenga dos veces el mismo role
    private List<Role> roles;


    /*
         hay que hacer una tabla intermedia para roles y usuarios
         tabla roles
         tabla usuarios
         tabla intermedia users_roles, con la anotacion @JoinTable(name="users_roles")
         donde estan los enlaces donde estan las llaves foraneas
         users_roles se guardan los id de cada tabla que son las llaves foraneas
         y un indice unico donde un usuario no puede tener el mismo role

     */

    //este campo no debe estar en la tablas de base de datos
    //lo excluimos solo debe estar en la clase
    //para el checkbox para seleccionar admin o user
    @Transient
   private boolean admin;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles){
        this.roles = roles;
    }

    @Override
    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}
