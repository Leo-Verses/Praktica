package com.praktica.praktica.filter;

import com.praktica.praktica.service.JwtService;
import com.praktica.praktica.service.UsuarioService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UsuarioService usuarioService;

    public JwtFilter(JwtService jwtService, UsuarioService usuarioService) {
        this.jwtService = jwtService;
        this.usuarioService = usuarioService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        System.out.println("=== JwtFilter ===");
        System.out.println("Ruta solicitada: " + request.getRequestURI());
        System.out.println("Authorization header: " + authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);
        String email = null;
        try {
            if (!jwtService.validateToken(jwt)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token JWT inválido o expirado");
                System.out.println("Token inválido: " + jwt);
                return;
            }

            email = jwtService.extractUsername(jwt);
            System.out.println("Token válido. Usuario extraído: " + email);
        } catch (Exception e) {
            System.out.println("Error al procesar token en JwtFilter: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Error al procesar el token: " + e.getMessage());
            return;
        }

        if (email != null) {
            request.setAttribute("usuario", email);
            System.out.println("Se estableció el usuario en la solicitud: " + email);
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = usuarioService.loadUserByUsername(email);
                if (userDetails != null) {
                    System.out.println("Usuario cargado: " + email);
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    System.out.println("No se pudo cargar el usuario: " + email);
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("Usuario no autorizado");
                    return;
                }
            } catch (Exception e) {
                System.out.println("Error cargando el usuario: " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("Error al cargar el usuario: " + e.getMessage());
                return;
            }
        }

        chain.doFilter(request, response);

        System.out.println("Filtro finalizado para ruta: " + request.getRequestURI());
        System.out.println("====================");
    }

}
