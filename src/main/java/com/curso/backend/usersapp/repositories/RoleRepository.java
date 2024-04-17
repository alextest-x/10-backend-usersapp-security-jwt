package com.curso.backend.usersapp.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.curso.backend.usersapp.models.entities.Role;

public interface RoleRepository
        extends CrudRepository<Role, Long> {
        Optional<Role> findByName(String name);
}
