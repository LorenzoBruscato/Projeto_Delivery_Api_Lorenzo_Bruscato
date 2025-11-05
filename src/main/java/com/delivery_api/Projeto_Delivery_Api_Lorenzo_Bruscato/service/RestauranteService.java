package com.delivery_api.Projeto_Delivery_Api_Lorenzo_Bruscato.service;

import com.delivery_api.Projeto_Delivery_Api_Lorenzo_Bruscato.entity.Restaurante;
import com.delivery_api.Projeto_Delivery_Api_Lorenzo_Bruscato.entity.RestauranteDTO;
import com.delivery_api.Projeto_Delivery_Api_Lorenzo_Bruscato.repository.PedidoRepository;
import com.delivery_api.Projeto_Delivery_Api_Lorenzo_Bruscato.repository.ProdutoRepository;
import com.delivery_api.Projeto_Delivery_Api_Lorenzo_Bruscato.repository.RestauranteRepository;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class RestauranteService {

    @Autowired
    private RestauranteRepository restauranteRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    /**
     * Cadastrar um novo restaurante
     */
    public Restaurante cadastrar(Restaurante restaurante) {
        validarDadosRestaurante(restaurante);

        // Evita duplicidade de nomes
        boolean nomeExistente = restauranteRepository.findAll().stream()
                .anyMatch(r -> r.getNome().equalsIgnoreCase(restaurante.getNome()));
        if (nomeExistente) {
            throw new IllegalArgumentException("Restaurante já cadastrado: " + restaurante.getNome());
        }

        restaurante.setAtivo(true);
        return restauranteRepository.save(restaurante);
    }

    /**
     * Buscar restaurante por ID
     */
    @Transactional
    public Optional<Restaurante> buscarPorId(Long id) {
        return restauranteRepository.findById(id);
    }

    /**
     * Converter restaurante para DTO e buscar por ID
     */
    @Transactional
    public Optional<RestauranteDTO> findById(Long id) {
        Optional<Restaurante> restauranteOpt = restauranteRepository.findById(id);
        return restauranteOpt.map(restaurante -> new RestauranteDTO(
                restaurante.getId(),
                restaurante.getNome(),
                restaurante.getCategoria(),
                restaurante.getEndereco(),
                restaurante.getTelefone(),
                restaurante.getTaxaEntrega(),
                restaurante.getAvaliacao(),
                restaurante.getAtivo()
        ));
    }

    /**
     * Listar restaurantes ativos
     */
    @Transactional
    public List<RestauranteDTO> listarAtivos() {
        List<Restaurante> ativos = restauranteRepository.findByAtivoTrue();
        if (ativos.isEmpty()) {
            throw new IllegalArgumentException("Nenhum restaurante ativo encontrado");
        }

        return ativos.stream()
                .map(this::toDTO)
                .toList();
    }

    /**
     * Buscar restaurantes por categoria
     */
    @Transactional
    public List<RestauranteDTO> buscarPorCategoria(String categoria) {
        List<Restaurante> restaurantes = restauranteRepository.findByCategoria(categoria);
        if (restaurantes.isEmpty()) {
            throw new IllegalArgumentException("Nenhum restaurante encontrado na categoria: " + categoria);
        }

        return restaurantes.stream()
                .map(this::toDTO)
                .toList();
    }

    /**
     * Buscar restaurantes com taxa de entrega menor ou igual ao valor informado
     */
    @Transactional
    public List<RestauranteDTO> buscarPorTaxaEntrega(BigDecimal taxaMaxima) {
        List<Restaurante> restaurantes = restauranteRepository.findByTaxaEntregaLessThanEqual(taxaMaxima);
        if (restaurantes.isEmpty()) {
            throw new IllegalArgumentException("Nenhum restaurante encontrado com taxa de entrega até: " + taxaMaxima);
        }

        return restaurantes.stream()
                .map(this::toDTO)
                .toList();
    }

    /**
     * Listar os 5 primeiros restaurantes (ordenados por nome)
     */
    @Transactional
    public List<RestauranteDTO> listarTop5() {
        List<Restaurante> top5 = restauranteRepository.findTop5ByOrderByNomeAsc();
        if (top5.isEmpty()) {
            throw new IllegalArgumentException("Nenhum restaurante encontrado");
        }

        return top5.stream()
                .map(this::toDTO)
                .toList();
    }

    /**
     * Atualizar dados do restaurante
     */
    @Transactional
    public Restaurante atualizar(Long id, Restaurante atualizado) {
        Restaurante restaurante = restauranteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Restaurante não encontrado: " + id));

        validarDadosRestaurante(atualizado);

        restaurante.setNome(atualizado.getNome());
        restaurante.setCategoria(atualizado.getCategoria());
        restaurante.setEndereco(atualizado.getEndereco());
        restaurante.setTelefone(atualizado.getTelefone());
        restaurante.setTaxaEntrega(atualizado.getTaxaEntrega());
        restaurante.setAvaliacao(atualizado.getAvaliacao());

        return restauranteRepository.save(restaurante);
    }

    /**
     * Inativar restaurante
     */
    public void inativar(Long id) {
        Restaurante restaurante = restauranteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Restaurante não encontrado: " + id));

        restaurante.setAtivo(false);
        restauranteRepository.save(restaurante);
    }

    /**
     * Excluir restaurante definitivamente
     */
    public void deletar(Long id) {
        Restaurante restaurante = restauranteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Restaurante não encontrado: " + id));

        restauranteRepository.delete(restaurante);
    }

    /**
     * Conversão auxiliar de entidade para DTO
     */
    private RestauranteDTO toDTO(Restaurante restaurante) {
        return new RestauranteDTO(
                restaurante.getId(),
                restaurante.getNome(),
                restaurante.getCategoria(),
                restaurante.getEndereco(),
                restaurante.getTelefone(),
                restaurante.getTaxaEntrega(),
                restaurante.getAvaliacao(),
                restaurante.getAtivo()
        );
    }

    /**
     * Validação dos dados básicos do restaurante
     */
    private void validarDadosRestaurante(Restaurante restaurante) {
        if (restaurante.getNome() == null || restaurante.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        if (restaurante.getCategoria() == null || restaurante.getCategoria().trim().isEmpty()) {
            throw new IllegalArgumentException("Categoria é obrigatória");
        }
        if (restaurante.getEndereco() == null || restaurante.getEndereco().trim().isEmpty()) {
            throw new IllegalArgumentException("Endereço é obrigatório");
        }
        if (restaurante.getTaxaEntrega() != null &&
                restaurante.getTaxaEntrega().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Taxa de entrega não pode ser negativa");
        }
    }
}
