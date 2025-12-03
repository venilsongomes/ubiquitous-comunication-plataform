package br.com.yourcompany.platformcore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.Instant;

@Entity
public class ConnectivityCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String serviceName;
    private Instant checkTimestamp;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
    public Instant getCheckTimestamp() {
        return checkTimestamp;
    }

    public void setCheckTimestamp(Instant checkTimestamp) {
        this.checkTimestamp = checkTimestamp;
    }

    public ConnectivityCheck() {}

    public ConnectivityCheck(String serviceName) {
        this.serviceName = serviceName;
        this.checkTimestamp = Instant.now();
    }
  
}
