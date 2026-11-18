package com.fatesg.cinema.service;

import java.util.List;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fatesg.cinema.exception.RecursoNaoEncontradoException;
import com.fatesg.cinema.exception.RegraNegocioException;
import com.fatesg.cinema.model.Usuario;
import com.fatesg.cinema.repository.UsuarioRepository;

/**
 * Regras de negocio do CRUD de Usuario e provedor de autenticacao (login).
 */
@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Usuario> listar() {
        return repository.findAll();
    }

    public Usuario buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Usuario nao encontrado com id " + id));
    }

    public Usuario salvar(Usuario usuario) {
        if (repository.existsByUsername(usuario.getUsername())) {
            throw new RegraNegocioException(
                    "Ja existe um usuario com o login '" + usuario.getUsername() + "'");
        }
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return repository.save(usuario);
    }

    public Usuario atualizar(Long id, Usuario dados) {
        Usuario usuario = buscarPorId(id);
        usuario.setNome(dados.getNome());
        usuario.setEmail(dados.getEmail());
        usuario.setPerfil(dados.getPerfil());
        // Atualiza a senha apenas quando uma nova for informada
        if (dados.getSenha() != null && !dados.getSenha().isBlank()) {
            usuario.setSenha(passwordEncoder.encode(dados.getSenha()));
        }
        return repository.save(usuario);
    }

    public void excluir(Long id) {
        Usuario usuario = buscarPorId(id);
        repository.delete(usuario);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario nao encontrado: " + username));
        return User.builder()
                .username(usuario.getUsername())
                .password(usuario.getSenha())
                .roles(usuario.getPerfil().name())
                .build();
    }
}
