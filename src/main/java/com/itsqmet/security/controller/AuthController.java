package com.itsqmet.security.controller;
import com.itsqmet.security.Modelo.Usuario;
import com.itsqmet.security.service.UsuarioService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/registro")
    public ResponseEntity<?> registrar(
            @Valid @RequestBody Usuario usuario,
            BindingResult result) {

        if (result.hasErrors()) {

            Map<String, String> errores = new HashMap<>();

            result.getFieldErrors().forEach((e) ->
                    errores.put(e.getField(), e.getDefaultMessage()));

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errores);
        }
        return usuarioService.registrar(usuario)
                .map(error -> ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("error", error)))
                .orElse(ResponseEntity.status(HttpStatus.CREATED)
                        .body(Map.of("mensaje", "Usuario registrado correctamente")));
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody Map<String, String> credenciales,
            HttpServletRequest request) {

        String username = credenciales.get("username");
        String password = credenciales.get("password");

        try {

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            HttpSession session = request.getSession(true);
            session.setAttribute(
                    "SPRING_SECURITY_CONTEXT",
                    SecurityContextHolder.getContext()
            );
            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Login exitoso");
            respuesta.put("usuario", username);

            return ResponseEntity.ok(respuesta);
 //cambiar de lugar el return
        } catch (AuthenticationException e) {

            Map<String, String> error = new HashMap<>();
            error.put("error", "Usuario o contraseña incorrectos");

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    @GetMapping("/perfil")
    public ResponseEntity<?> perfil() {

        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        return ResponseEntity.ok(Map.of(
                "mensaje", "Acceso autorizado",
                "usuarioActual", username
        ));
    }


}
