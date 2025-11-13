package br.com.yourcompany.platformcore.controller;
import br.com.yourcompany.platformcore.dto.AuthRequest;
import br.com.yourcompany.platformcore.dto.AuthResponse;
import br.com.yourcompany.platformcore.dto.CreateUserRequest;
import br.com.yourcompany.platformcore.dto.UserResponse;
import br.com.yourcompany.platformcore.security.JwtService;
import br.com.yourcompany.platformcore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private UserService userService;
    @Autowired private JwtService jwtService;

    // 1. Endpoint de Registro (o novo POST /users)
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody CreateUserRequest request) {
        UserResponse userResponse = userService.createUser(request);
        // TODO: Poderia logar o usuário e retornar um token aqui também
        return ResponseEntity.status(201).body(userResponse);
    }

    // 2. Endpoint de Login
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        // 1. Pedir ao Spring Security para autenticar
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // 2. Se chegou aqui, o usuário é válido. Pegar os detalhes.
        var userDetails = (UserDetails) authentication.getPrincipal();

        // 3. Gerar o token
        String jwtToken = jwtService.generateToken(userDetails);

        // 4. Retornar o token
        return ResponseEntity.ok(new AuthResponse(jwtToken));
    }
}