package br.com.yourcompany.platformcore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConnectivityCheckRepository extends JpaRepository<ConnectivityCheck, Long> {
    
}