package com.delivery_api.Projeto_Delivery_Api_Lorenzo_Bruscato.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class ClienteRequestDTO {

    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @NotBlank(message = "O nome é obrigatório")
    @Email(message = "O email deve ser valido")
    private String email;

    @NotBlank(message = "O nome é obrigatório")
    private String telefone;

    @NotBlank(message = "O nome é obrigatório")
    private String endereco;
}
