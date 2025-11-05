package com.delivery_api.Projeto_Delivery_Api_Lorenzo_Bruscato.service;

import com.delivery_api.Projeto_Delivery_Api_Lorenzo_Bruscato.entity.Produto;
import com.delivery_api.Projeto_Delivery_Api_Lorenzo_Bruscato.entity.ProdutoDTO;
import com.delivery_api.Projeto_Delivery_Api_Lorenzo_Bruscato.repository.ProdutoRepository;
import com.delivery_api.Projeto_Delivery_Api_Lorenzo_Bruscato.repository.RestauranteRepository;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private RestauranteRepository restauranteRepository;

    /**
     * Cadastrar um novo produto
     */
    public Produto cadastrar(Produto produto) {
        validarProduto(produto);

        if (produto.getRestauranteId() == null) {
            throw new IllegalArgumentException("O produto deve estar vinculado a um restaurante");
        }

        if (!restauranteRepository.existsById(produto.getRestauranteId())) {
            throw new IllegalArgumentException("Restaurante não encontrado para o ID informado: " + produto.getRestauranteId());
        }

        produto.setDisponivel(true);
        return produtoRepository.save(produto);
    }

    /**
     * Buscar produto por ID
     */
    @Transactional
    public Optional<Produto> buscarPorId(Long id) {
        return produtoRepository.findById(id);
    }

    /**
     * Buscar produto e converter para DTO
     */
    @Transactional
    public Optional<ProdutoDTO> findById(Long id) {
        Optional<Produto> produtoOpt = produtoRepository.findById(id);
        return produtoOpt.map(this::toDTO);
    }

    /**
     * Listar todos os produtos disponíveis
     */
    @Transactional
    public List<ProdutoDTO> listarDisponiveis() {
        List<Produto> disponiveis = produtoRepository.findByDisponivelTrue();
        if (disponiveis.isEmpty()) {
            throw new IllegalArgumentException("Nenhum produto disponível encontrado");
        }

        return disponiveis.stream()
                .map(this::toDTO)
                .toList();
    }

    /**
     * Buscar produtos de um restaurante específico
     */
    @Transactional
    public List<ProdutoDTO> buscarPorRestaurante(Long restauranteId) {
        if (!restauranteRepository.existsById(restauranteId)) {
            throw new IllegalArgumentException("Restaurante não encontrado: " + restauranteId);
        }

        List<Produto> produtos = produtoRepository.findByRestauranteId(restauranteId);
        if (produtos.isEmpty()) {
            throw new IllegalArgumentException("Nenhum produto encontrado para este restaurante");
        }

        return produtos.stream()
                .map(this::toDTO)
                .toList();
    }

    /**
     * Buscar produtos por categoria
     */
    @Transactional
    public List<ProdutoDTO> buscarPorCategoria(String categoria) {
        List<Produto> produtos = produtoRepository.findByCategoria(categoria);
        if (produtos.isEmpty()) {
            throw new IllegalArgumentException("Nenhum produto encontrado na categoria: " + categoria);
        }

        return produtos.stream()
                .map(this::toDTO)
                .toList();
    }

    /**
     * Buscar produtos por preço máximo
     */
    @Transactional
    public List<ProdutoDTO> buscarPorPreco(BigDecimal precoMaximo) {
        List<Produto> produtos = produtoRepository.findByPrecoLessThanEqual(precoMaximo);
        if (produtos.isEmpty()) {
            throw new IllegalArgumentException("Nenhum produto encontrado com preço até: " + precoMaximo);
        }

        return produtos.stream()
                .map(this::toDTO)
                .toList();
    }

    /**
     * Atualizar produto
     */
    @Transactional
    public Produto atualizar(Long id, Produto atualizado) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + id));

        validarProduto(atualizado);

        produto.setNome(atualizado.getNome());
        produto.setDescricao(atualizado.getDescricao());
        produto.setCategoria(atualizado.getCategoria());
        produto.setPreco(atualizado.getPreco());
        produto.setDisponivel(atualizado.getDisponivel());
        produto.setRestauranteId(atualizado.getRestauranteId());

        return produtoRepository.save(produto);
    }

    /**
     * Alterar disponibilidade de um produto
     */
    @Transactional
    public void alterarDisponibilidade(Long id, boolean disponivel) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + id));

        produto.setDisponivel(disponivel);
        produtoRepository.save(produto);
    }

    /**
     * Excluir produto
     */
    public void deletar(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + id));

        produtoRepository.delete(produto);
    }

    /**
     * Converter entidade para DTO
     */
    private ProdutoDTO toDTO(Produto produto) {
        return new ProdutoDTO(
                produto.getId(),
                produto.getNome(),
                produto.getDescricao(),
                produto.getPreco(),
                produto.getCategoria(),
                produto.getDisponivel()
        );
    }

    /**
     * Validação de dados do produto
     */
    private void validarProduto(Produto produto) {
        if (produto.getNome() == null || produto.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do produto é obrigatório");
        }
        if (produto.getCategoria() == null || produto.getCategoria().trim().isEmpty()) {
            throw new IllegalArgumentException("A categoria do produto é obrigatória");
        }
        if (produto.getPreco() == null || produto.getPreco().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("O preço do produto deve ser maior ou igual a zero");
        }
    }
}
