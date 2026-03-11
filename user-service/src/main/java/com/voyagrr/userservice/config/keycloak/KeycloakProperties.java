package com.voyagrr.userservice.config.keycloak;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "keycloak")
public class KeycloakProperties {

    private String keycloakUrl;
    private String realm;
    private String clientId;
    private String clientSecret;

}
