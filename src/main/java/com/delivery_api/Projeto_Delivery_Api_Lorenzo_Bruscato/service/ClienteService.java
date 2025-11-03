package com.delivery_api.Projeto_Delivery_Api_Lorenzo_Bruscato.service;

import com.delivery_api.Projeto_Delivery_Api_Lorenzo_Bruscato.entity.Cliente;
import com.delivery_api.Projeto_Delivery_Api_Lorenzo_Bruscato.repository.ClienteRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public Cliente cadastrar(@Validated Cliente cliente) {
       if(clienteRepository.existsByEmail(cliente.getEmail())){
           throw new IllegalArgumentException("Email já cadastro: " + cliente.getEmail());
       }

        validarDadosCliente(cliente);
        cliente.setAtivo(true);
        return clienteRepository.save(cliente);
    }

    public Cliente atualizar(Long id, @Validated Cliente clienteAtualizado) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + id));

        if (!cliente.getEmail().equals(clienteAtualizado.getEmail()) &&
                clienteRepository.existsByEmail(clienteAtualizado.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado: " + clienteAtualizado.getEmail());
        }

        cliente.setNome(clienteAtualizado.getNome());
        cliente.setEmail(clienteAtualizado.getEmail());
        cliente.setTelefone(clienteAtualizado.getTelefone());
        cliente.setEndereco(clienteAtualizado.getEndereco());

        return clienteRepository.save(cliente);
    }

    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }

    public Optional<Cliente> buscarPorEmail(String email) {
        return clienteRepository.findByEmail(email);
    }

    public List<Cliente> buscarPorNome(String nome) {
        return clienteRepository.findByNomeContainingIgnoreCase(nome);
    }

    public List<Cliente> listarClientesAtivos() {
        return clienteRepository.findByAtivoTrue();
    }

    public void desativarCliente(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + id));
        cliente.setAtivo(false);
        clienteRepository.save(cliente);
    }

    public void ativarCliente(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + id));
        cliente.setAtivo(true);
        clienteRepository.save(cliente);
    }

    private void validarDadosCliente(Cliente cliente) {
        if (cliente.getNome() == null || cliente.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        if (cliente.getEmail() == null || cliente.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email é obrigatório");
        }
        if (cliente.getTelefone() == null || cliente.getTelefone().trim().isEmpty()) {
            throw new IllegalArgumentException("Telefone é obrigatório");
        }
        // Add more validation rules as needed
    }
}