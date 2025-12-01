package br.com.yourcompany.platformcore;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    @Bean
    public Counter messagesProcessedCounter(MeterRegistry registry) {
        return Counter.builder("messages_processed_total")
                .description("Total de mensagens processadas")
                .register(registry);
    }

    @Bean
    public Counter errorsCounter(MeterRegistry registry) {
        return Counter.builder("errors_total")
                .description("Total de erros no processamento")
                .register(registry);
    }

    @Bean
    public Timer latencyTimer(MeterRegistry registry) {
        return Timer.builder("latency_ms")
            .description("Latência do processamento de mensagens em ms")
            .publishPercentileHistogram(true) // Expor buckets para Prometheus
            .register(registry);
    }
}
