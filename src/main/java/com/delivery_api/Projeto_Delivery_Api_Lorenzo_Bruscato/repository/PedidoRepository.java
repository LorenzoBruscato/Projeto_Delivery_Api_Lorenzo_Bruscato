package com.delivery_api.Projeto_Delivery_Api_Lorenzo_Bruscato.repository;

import com.delivery_api.Projeto_Delivery_Api_Lorenzo_Bruscato.entity.Pedido;
import com.delivery_api.Projeto_Delivery_Api_Lorenzo_Bruscato.enums.StatusPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByClienteIdOrderByDataPedidoDesc(Long clienteId);

    List<Pedido> findByStatus(StatusPedido status);

    List<Pedido> findTop10ByOrderByDataPedidoDesc();

    List<Pedido> findByDataPedidoBetween(LocalDateTime inicio, LocalDateTime fim);
}
