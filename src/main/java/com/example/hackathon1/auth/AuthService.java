package com.example.hackathon1.auth;

import com.example.hackathon1.domain.Rol;
import com.example.hackathon1.domain.Usuario;
import com.example.hackathon1.dto.request.AuthRequestDto;
import com.example.hackathon1.dto.request.LoginRequestDto;
import com.example.hackathon1.dto.response.AuthResponseDto;
import com.example.hackathon1.dto.response.RegisterResponseDto;
import com.example.hackathon1.exceptions.UserAlreadyExistsException;
import com.example.hackathon1.repository.UsuarioRepository;
import com.example.hackathon1.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public RegisterResponseDto register(AuthRequestDto request) {
        usuarioRepository.findByUsername(request.getUsername()).ifPresent(u -> {
            throw new UserAlreadyExistsException("Username " + request.getUsername() + " ya está en uso.");
        });
        usuarioRepository.findByEmail(request.getEmail()).ifPresent(u -> {
            throw new UserAlreadyExistsException("Email " + request.getEmail() + " ya está registrado.");
        });

        Rol role;
        try {
            role = Rol.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("El rol especificado no es válido. Debe ser 'CENTRAL' o 'BRANCH'.");
        }

        if (role == Rol.BRANCH && (request.getBranch() == null || request.getBranch().isBlank())) {
            throw new IllegalArgumentException("El campo 'branch' es obligatorio para el rol 'BRANCH'.");
        }

        Usuario usuario = Usuario.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .rol(role)
                .branch(role == Rol.CENTRAL ? null : request.getBranch())
                .build();

        Usuario savedUsuario = usuarioRepository.save(usuario);

        return RegisterResponseDto.builder()
                .id(savedUsuario.getId())
                .username(savedUsuario.getUsername())
                .email(savedUsuario.getEmail())
                .role(savedUsuario.getRol().name())
                .branch(savedUsuario.getBranch())
                .createdAt(savedUsuario.getCreatedAt())
                .build();
    }

    public AuthResponseDto login(LoginRequestDto request) {
        Usuario usuario = usuarioRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Usuario o contraseña incorrectos."));

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(usuario.getEmail(), request.getPassword()));
        String token = jwtService.generateToken(usuario);

        return new AuthResponseDto(token, 3600, usuario.getRol().name(), usuario.getBranch());
    }

}
