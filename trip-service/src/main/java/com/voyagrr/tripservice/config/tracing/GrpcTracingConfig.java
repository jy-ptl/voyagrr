package com.voyagrr.tripservice.config.tracing;

import io.grpc.ClientInterceptor;
import io.grpc.ServerInterceptor;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.grpc.v1_6.GrpcTelemetry;
import net.devh.boot.grpc.client.interceptor.GrpcGlobalClientInterceptor;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcTracingConfig {

    private static final Logger log = LoggerFactory.getLogger(GrpcTracingConfig.class);

    @Bean
    @ConditionalOnMissingBean
    public OpenTelemetry openTelemetry() {
        log.warn("OpenTelemetry bean missing — providing no-op fallback");
        return OpenTelemetry.noop();
    }

    @Bean
    public GrpcTelemetry grpcTelemetry(OpenTelemetry openTelemetry) {
        log.info("Initializing gRPC OTel telemetry: {}", openTelemetry.getClass().getSimpleName());
        return GrpcTelemetry.create(openTelemetry);
    }

    @Bean
    @GrpcGlobalClientInterceptor
    public ClientInterceptor grpcTracingClientInterceptor(GrpcTelemetry grpcTelemetry) {
        log.info("Registering gRPC client tracing interceptor");
        return grpcTelemetry.newClientInterceptor();
    }

    @Bean
    @GrpcGlobalServerInterceptor
    public ServerInterceptor grpcTracingServerInterceptor(GrpcTelemetry grpcTelemetry) {
        log.info("Registering gRPC server tracing interceptor");
        return grpcTelemetry.newServerInterceptor();
    }

}
