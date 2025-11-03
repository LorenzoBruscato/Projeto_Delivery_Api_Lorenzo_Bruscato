package com.delivery_api.Projeto_Delivery_Api_Lorenzo_Bruscato.repository;

import java.util.List;
import java.util.Optional;

import com.delivery_api.Projeto_Delivery_Api_Lorenzo_Bruscato.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository <Cliente, Long> {

    Optional<Cliente> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Cliente> findByAtivoTrue();

    List<Cliente> findByNomeContainingIgnoreCase(String nome);

}