package br.com.yourcompany.platform_core.dto;

import java.util.UUID;

import br.com.yourcompany.platform_core.dominio.user.User;

public class UserResponse {
    private UUID id;
    private String username;
    private String displayName;

    public UserResponse(UUID id, String username, String displayName) {
        this.id = id;
        this.username = username;
        this.displayName = displayName;
    }
    public UUID getId() {
        return id;
    }
    public String getUsername() {
        return username;
    }
    public String getDisplayName() {
        return displayName;
    }
    public static UserResponse fromEntity(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getDisplayName()
        );
    }
}
