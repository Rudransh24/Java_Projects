package com.example.SpringKafkaRedis;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.web.server.WebFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;
//import oxus.lib.common.spring.TraceResponseFilter;
//import springfox.documentation.swagger2.EnableSwagger2WebFlux;
//import springfox.documentation.swagger2.annotations.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.web.servlet.function.RequestPredicates.GET;
import static org.springframework.web.servlet.function.RouterFunctions.route;


@EnableWebMvc
@SpringBootApplication
public class SpringKafkaRedisApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringKafkaRedisApplication.class, args);
	}

	@Value("${kafka.broker.url}")
	private String kafkaBrokerUrl;

	@Bean
	RouterFunction<ServerResponse> routerFunction() {
		return route(GET("/swagger/index.html"), req ->
				ServerResponse.temporaryRedirect(URI.create("/swagger-ui/")).build());
	}

	//@Bean
	//WebFilter traceResponseFilter() {
	//	return new TraceResponseFilter();
	//}

	@Bean
	public ProducerFactory<String, String> producerFactory() {
		return new DefaultKafkaProducerFactory<>(producerConfigs());
	}

	@Bean
	public Map<String, Object> producerConfigs() {
		Map<String, Object> props = new HashMap<>();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBrokerUrl);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		return props;
	}

	@Bean
	public KafkaTemplate<String, String> kafkaTemplate() {
		return new KafkaTemplate<String, String>(producerFactory());
	}
}
