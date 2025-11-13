package br.com.yourcompany.platformcore.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.yourcompany.platformcore.domain.user.UserIdentity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserIdentityRepository extends JpaRepository<UserIdentity, Long> {

    /**
     * Encontra todas as identidades externas de um usuário interno.
     * (Usado para saber para onde rotear uma mensagem)
     */
    List<UserIdentity> findByUserId(UUID userId);

    /**
     * Encontra um usuário interno com base em uma identidade externa.
     * (Usado pelo Webhook para saber quem enviou a mensagem)
     */
    Optional<UserIdentity> findByPlatformAndExternalId(String platform, String externalId);
}
