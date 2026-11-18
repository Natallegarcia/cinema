package com.fatesg.cinema.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.fatesg.cinema.exception.RegraNegocioException;
import com.fatesg.cinema.model.Usuario;
import com.fatesg.cinema.model.Usuario.Perfil;
import com.fatesg.cinema.repository.UsuarioRepository;

/**
 * Testes unitarios (JUnit + Mockito) das regras de negocio de Usuario.
 */
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private UsuarioService service;

    @Test
    @DisplayName("Deve criptografar a senha ao salvar um novo usuario")
    void deveCriptografarSenhaAoSalvar() {
        service = new UsuarioService(repository, passwordEncoder);
        Usuario novo = new Usuario("Joao", "joao", "joao@mail.com", "123456", Perfil.USER);
        when(repository.existsByUsername("joao")).thenReturn(false);
        when(repository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario salvo = service.salvar(novo);

        assertNotEquals("123456", salvo.getSenha(), "A senha nao deve ficar em texto puro");
        org.junit.jupiter.api.Assertions.assertTrue(
                passwordEncoder.matches("123456", salvo.getSenha()));
    }

    @Test
    @DisplayName("Deve impedir cadastro com username duplicado")
    void deveImpedirUsernameDuplicado() {
        service = new UsuarioService(repository, passwordEncoder);
        Usuario novo = new Usuario("Maria", "maria", "maria@mail.com", "senha", Perfil.USER);
        when(repository.existsByUsername("maria")).thenReturn(true);

        RegraNegocioException ex = assertThrows(
                RegraNegocioException.class, () -> service.salvar(novo));

        assertEquals("Ja existe um usuario com o login 'maria'", ex.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Nao deve alterar a senha ao atualizar quando ela vier em branco")
    void naoDeveAlterarSenhaEmBranco() {
        PasswordEncoder encoderMock = org.mockito.Mockito.mock(PasswordEncoder.class);
        service = new UsuarioService(repository, encoderMock);

        Usuario existente = new Usuario("Ana", "ana", "ana@mail.com", "hash-original", Perfil.USER);
        existente.setId(5L);
        when(repository.findById(5L)).thenReturn(java.util.Optional.of(existente));
        when(repository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario dados = new Usuario("Ana Paula", "ana", "ana2@mail.com", "", Perfil.ADMIN);
        Usuario atualizado = service.atualizar(5L, dados);

        assertEquals("hash-original", atualizado.getSenha());
        assertEquals("Ana Paula", atualizado.getNome());
        assertEquals(Perfil.ADMIN, atualizado.getPerfil());
        verify(encoderMock, never()).encode(anyString());
    }
}
