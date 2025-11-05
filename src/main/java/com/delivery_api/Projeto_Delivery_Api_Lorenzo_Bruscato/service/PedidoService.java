package com.delivery_api.Projeto_Delivery_Api_Lorenzo_Bruscato.service;

import com.delivery_api.Projeto_Delivery_Api_Lorenzo_Bruscato.entity.*;
import com.delivery_api.Projeto_Delivery_Api_Lorenzo_Bruscato.enums.StatusPedido;
import com.delivery_api.Projeto_Delivery_Api_Lorenzo_Bruscato.repository.*;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private RestauranteRepository restauranteRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    /**
     * Cadastrar um novo pedido
     */
    public Pedido cadastrar(Pedido pedido) {
        // Valida cliente
        Cliente cliente = clienteRepository.findById(pedido.getClienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + pedido.getClienteId()));

        // Valida restaurante
        Restaurante restaurante = restauranteRepository.findById(pedido.getRestaurante().getId())
                .orElseThrow(() -> new IllegalArgumentException("Restaurante não encontrado: " + pedido.getRestaurante().getId()));

        if (!cliente.getAtivo()) {
            throw new IllegalArgumentException("Cliente inativo não pode realizar pedidos");
        }

        if (!restaurante.getAtivo()) {
            throw new IllegalArgumentException("Restaurante inativo não pode receber pedidos");
        }

        // Valida itens
        if (pedido.getItens() == null || pedido.getItens().isEmpty()) {
            throw new IllegalArgumentException("O pedido deve conter ao menos um item");
        }

        pedido.setStatus(StatusPedido.PENDENTE.name());
        return pedidoRepository.save(pedido);
    }

    /**
     * Buscar pedido por ID
     */
    @Transactional
    public Optional<Pedido> buscarPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    /**
     * Listar todos os pedidos
     */
    @Transactional
    public List<Pedido> listarTodos() {
        List<Pedido> pedidos = pedidoRepository.findAll();
        if (pedidos.isEmpty()) {
            throw new IllegalArgumentException("Nenhum pedido encontrado");
        }
        return pedidos;
    }

    /**
     * Listar pedidos por cliente
     */
    @Transactional
    public List<Pedido> listarPorCliente(Long clienteId) {
        List<Pedido> pedidos = pedidoRepository.findByClienteIdOrderByDataPedidoDesc(clienteId);
        if (pedidos.isEmpty()) {
            throw new IllegalArgumentException("Nenhum pedido encontrado para o cliente ID: " + clienteId);
        }
        return pedidos;
    }

    /**
     * Atualizar status do pedido
     */
    public Pedido atualizarStatus(Long id, StatusPedido novoStatus) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado: " + id));

        if (pedido.getStatus().equals(StatusPedido.ENTREGUE.name())) {
            throw new IllegalArgumentException("Pedido já finalizado");
        }

        pedido.setStatus(novoStatus.name());
        return pedidoRepository.save(pedido);
    }

    /**
     * Atualizar dados do pedido
     */
    public Pedido atualizar(Long id, Pedido pedidoAtualizado) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado: " + id));

        if (pedidoAtualizado.getObservacoes() != null) {
            pedido.setObservacoes(pedidoAtualizado.getObservacoes());
        }

        if (pedidoAtualizado.getValorTotal() != null) {
            pedido.setValorTotal(pedidoAtualizado.getValorTotal());
        }

        return pedidoRepository.save(pedido);
    }

    /**
     * Cancelar um pedido
     */
    public void cancelar(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado: " + id));

        if (pedido.getStatus().equals(StatusPedido.ENTREGUE.name())) {
            throw new IllegalArgumentException("Não é possível cancelar um pedido já entregue");
        }

        pedido.setStatus(StatusPedido.CANCELADO.name());
        pedidoRepository.save(pedido);
    }

    /**
     * Excluir pedido do banco
     */
    public void deletar(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado: " + id));

        pedidoRepository.delete(pedido);
    }
}
