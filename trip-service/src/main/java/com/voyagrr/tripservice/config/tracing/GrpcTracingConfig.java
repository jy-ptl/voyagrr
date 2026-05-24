package com.voyagrr.tripservice.config.tracing;

import io.grpc.ClientInterceptor;
import io.grpc.ServerInterceptor;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.grpc.v1_6.GrpcTelemetry;
import net.devh.boot.grpc.client.interceptor.GrpcGlobalClientInterceptor;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcTracingConfig {

    @Bean
    public GrpcTelemetry grpcTelemetry(OpenTelemetry openTelemetry) {
        return GrpcTelemetry.create(openTelemetry);
    }

    @GrpcGlobalClientInterceptor
    public ClientInterceptor tracingClientInterceptor(GrpcTelemetry grpcTelemetry) {
        return grpcTelemetry.newClientInterceptor();
    }

    @GrpcGlobalServerInterceptor
    public ServerInterceptor tracingServerInterceptor(GrpcTelemetry grpcTelemetry) {
        return grpcTelemetry.newServerInterceptor();
    }

}
