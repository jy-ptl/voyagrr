package com.voyagrr.processingservice.config.kafka;

import java.util.HashMap;
import java.util.Map;

import com.voyagrr.processingservice.dto.EncodingProcessedEvent;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@EnableKafka
@Configuration
public class EncodingProcessedConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String BOOTSTARP_SERVERS;

    @Bean
    public ConsumerFactory<String, EncodingProcessedEvent> encodingConsumerFactory() {

        JsonDeserializer<EncodingProcessedEvent> deserializer = new JsonDeserializer<>(EncodingProcessedEvent.class);
        deserializer.addTrustedPackages("*");

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTARP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "encoding-processed-response-handler");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);

    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, EncodingProcessedEvent> encodingKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, EncodingProcessedEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(encodingConsumerFactory());
        factory.getContainerProperties().setObservationEnabled(true);
        return factory;

    }

}
