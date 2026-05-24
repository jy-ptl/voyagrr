package com.voyagrr.processingservice.config.tracing;

import io.grpc.ClientInterceptor;
import io.grpc.ServerInterceptor;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.grpc.v1_6.GrpcTelemetry;
import net.devh.boot.grpc.client.interceptor.GrpcGlobalClientInterceptor;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcTracingConfig {

    private static final Logger log = LoggerFactory.getLogger(GrpcTracingConfig.class);

    @Bean
    public GrpcTelemetry grpcTelemetry(OpenTelemetry openTelemetry) {
        log.info("Initializing gRPC tracing with OpenTelemetry: {}", openTelemetry.getClass().getName());
        return GrpcTelemetry.create(openTelemetry);
    }

    @GrpcGlobalClientInterceptor
    public ClientInterceptor tracingClientInterceptor(GrpcTelemetry grpcTelemetry) {
        log.info("Registering gRPC tracing client interceptor");
        return grpcTelemetry.newClientInterceptor();
    }

    @GrpcGlobalServerInterceptor
    public ServerInterceptor tracingServerInterceptor(GrpcTelemetry grpcTelemetry) {
        log.info("Registering gRPC tracing server interceptor");
        return grpcTelemetry.newServerInterceptor();
    }

}
