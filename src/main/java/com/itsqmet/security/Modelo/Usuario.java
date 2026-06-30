package com.itsqmet.security.Modelo;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Table(name = "usuarios")
@Data
@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "El usuario es obligatorio")
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank(message = "La contraseñ es obligatoria")
    @Column(unique = true, nullable = false)
    private String password;

    @NotBlank(message = "El email es obligatorio")
    @Column(unique = true, nullable = false)
    private String email;
}
