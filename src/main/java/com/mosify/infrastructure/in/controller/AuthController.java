package com.mosify.infrastructure.in.controller;

import com.mosify.api.controller.AuthApi;
import com.mosify.api.model.WebAuthResponse;
import com.mosify.api.model.WebLoginRequest;
import com.mosify.domain.model.User;
import com.mosify.infrastructure.in.mapper.UserWebConverter;
import com.mosify.infrastructure.security.JwtUtil;
import com.mosify.infrastructure.security.SecurityUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController implements AuthApi {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserWebConverter userWebConverter;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil,
                          UserWebConverter userWebConverter) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userWebConverter = userWebConverter;
    }

    @Override
    public ResponseEntity<WebAuthResponse> login(WebLoginRequest webLoginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        webLoginRequest.getUsername(),
                        webLoginRequest.getPassword()
                )
        );

        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        User user = securityUser.getUser();
        String token = jwtUtil.generateToken(user.getUsername());

        WebAuthResponse response = new WebAuthResponse();
        response.setAccessToken(token);
        response.setUser(userWebConverter.toWebResponse(user));

        return ResponseEntity.ok(response);
    }
}
