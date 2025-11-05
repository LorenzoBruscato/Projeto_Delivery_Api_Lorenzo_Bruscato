package com.delivery_api.Projeto_Delivery_Api_Lorenzo_Bruscato.service;

import com.delivery_api.Projeto_Delivery_Api_Lorenzo_Bruscato.dto.ClienteRequestDTO;
import com.delivery_api.Projeto_Delivery_Api_Lorenzo_Bruscato.dto.ClienteResponseDTO;
import com.delivery_api.Projeto_Delivery_Api_Lorenzo_Bruscato.entity.Cliente;
import com.delivery_api.Projeto_Delivery_Api_Lorenzo_Bruscato.exceptions.BusinessException;
import com.delivery_api.Projeto_Delivery_Api_Lorenzo_Bruscato.repository.ClienteRepository;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    /**
     * Cadastrar novo cliente
     */
    public ClienteResponseDTO cadastrar(ClienteRequestDTO dto) {
        // Verifica se já existe um cliente com o mesmo email
        if (clienteRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("Email já cadastrado: " + dto.getEmail());
        }

        Cliente cliente = new Cliente();
        cliente.setNome(dto.getNome());
        cliente.setEmail(dto.getEmail());
        cliente.setTelefone(dto.getTelefone());
        cliente.setEndereco(dto.getEndereco());
        cliente.setAtivo(true);
        cliente.setDataCadastro(LocalDateTime.now());

        Cliente salvo = clienteRepository.save(cliente);
        return new ClienteResponseDTO(salvo);
    }

    /**
     * Buscar cliente por ID
     */
    @Transactional
    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }

    /**
     * Buscar cliente por email
     */
    @Transactional
    public Optional<Cliente> buscarPorEmail(String email) {
        return clienteRepository.findByEmail(email);
    }

    /**
     * Listar todos os clientes ativos
     */
    @Transactional
    public List<Cliente> listarAtivos() {
        return clienteRepository.findByAtivoTrue();
    }

    /**
     * Atualizar dados do cliente
     */
    public ClienteResponseDTO atualizar(Long id, ClienteRequestDTO dto) {
        Cliente cliente = buscarPorId(id)
                .orElseThrow(() -> new BusinessException("Cliente não encontrado: " + id));

        // Verifica se o email foi alterado e se já está em uso
        if (!cliente.getEmail().equals(dto.getEmail()) && clienteRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("Email já cadastrado: " + dto.getEmail());
        }

        cliente.setNome(dto.getNome());
        cliente.setEmail(dto.getEmail());
        cliente.setTelefone(dto.getTelefone());
        cliente.setEndereco(dto.getEndereco());

        Cliente atualizado = clienteRepository.save(cliente);
        return new ClienteResponseDTO(atualizado);
    }

    /**
     * Inativar cliente (soft delete)
     */
    public void inativar(Long id) {
        Cliente cliente = buscarPorId(id)
                .orElseThrow(() -> new BusinessException("Cliente não encontrado: " + id));

        cliente.inativar();
        clienteRepository.save(cliente);
    }

    /**
     * Buscar clientes por nome (contendo)
     */
    @Transactional
    public List<Cliente> buscarPorNome(String nome) {
        return clienteRepository.findByNomeContainingIgnoreCase(nome);
    }
}
