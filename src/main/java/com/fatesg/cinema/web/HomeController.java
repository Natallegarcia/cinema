package com.fatesg.cinema.web;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Redireciona a raiz: ADMIN vai para os cadastros, cliente para o catalogo.
 */
@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Authentication auth) {
        boolean admin = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return admin ? "redirect:/filmes" : "redirect:/catalogo";
    }
}
