package br.com.yourcompany.platform_core.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.yourcompany.platform_core.dto.CreateUserRequest;
import br.com.yourcompany.platform_core.dto.UserResponse;
import br.com.yourcompany.platform_core.repository.UserRepository;
import br.com.yourcompany.platform_core.dominio.user.User;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional // Garante que a operação seja atômica (tudo ou nada)
    public UserResponse createUser(CreateUserRequest request) {
        // Lógica de Negócio: Verificar se o username já existe
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists"); // TODO: Criar exceção customizada
        }

        User newUser = new User(request.getUsername(), request.getDisplayName());
        User savedUser = userRepository.save(newUser);

        return UserResponse.fromEntity(savedUser);
    }
}
