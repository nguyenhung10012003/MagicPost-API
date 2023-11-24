package com.app.magicpostapi.services;

import com.app.magicpostapi.components.AuthenticationResponse;
import com.app.magicpostapi.components.TokenType;
import com.app.magicpostapi.models.CustomUserDetails;
import com.app.magicpostapi.models.Token;
import com.app.magicpostapi.models.User;
import com.app.magicpostapi.repositories.TokenRepository;
import com.app.magicpostapi.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
public class AuthenticationService {
    @Autowired
    private UserRepository repository;
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;

    public AuthenticationResponse authenticate(Map<String, String> user) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.get("username"),
                        user.get("password")
                )
        );
        CustomUserDetails userFound = new CustomUserDetails(repository.findAccountByUsername(user.get("username"))
                .orElseThrow());
        String jwtToken = jwtService.generateToken(userFound);
        String refreshToken = jwtService.generateRefreshToken(userFound);
        revokeAllUserTokens(userFound.getUser());
        saveUserToken(userFound.getUser(), jwtToken);
        return new AuthenticationResponse(jwtToken, refreshToken, userFound.getUser().getRole());
    }

    private void saveUserToken(User user, String jwtToken) {
        Token token = new Token();
        token.setToken(jwtToken);
        token.setExpired(false);
        token.setRevoked(false);
        token.setUser(user);
        token.setTokenType(TokenType.BEARER);
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String username;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        username = jwtService.extractUsername(refreshToken);
        if (username != null) {
            CustomUserDetails user = new CustomUserDetails(this.repository.findAccountByUsername(username)
                    .orElseThrow());
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user.getUser());
                saveUserToken(user.getUser(), accessToken);
                AuthenticationResponse authResponse = new AuthenticationResponse(accessToken, refreshToken, user.getUser().getRole());
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }
}
