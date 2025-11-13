package br.com.yourcompany.platformcore.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import br.com.yourcompany.platformcore.domain.user.User;
import br.com.yourcompany.platformcore.dto.CreateUserRequest;
import br.com.yourcompany.platformcore.dto.UserResponse;
import br.com.yourcompany.platformcore.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional 
    public UserResponse createUser(CreateUserRequest request) {
      
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

       // User newUser = new User(request.getUsername(), request.getDisplayName());
       // User savedUser = userRepository.save(newUser);

        //return UserResponse.fromEntity(savedUser);

        String encryptedPassword = passwordEncoder.encode(request.getPassword());

        User newUser = new User(
                request.getUsername(), 
                request.getDisplayName(), 
                encryptedPassword // <-- Salvar a senha criptografada
        );
        User savedUser = userRepository.save(newUser);

        return UserResponse.fromEntity(savedUser);
    }
}
