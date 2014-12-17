package krasa.wicket.websocket;

import org.apache.wicket.protocol.ws.javax.MyWicketServerEndpointConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.*;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@EnableWebSocket
@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = { "krasa" })
public class Start extends SpringBootServletInitializer
		implements WebSocketConfigurer {

	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(Start.class, args);
	}

	@Override public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {

	}

	@Component
	public static class JettyCustomizer implements EmbeddedServletContainerCustomizer {

		@Override
		public void customize(ConfigurableEmbeddedServletContainer container) {
			container.setPort(8080);
		}
	}

	@Bean
	public ServerEndpointExporter serverEndpointExporter() {
		return new ServerEndpointExporter();
	}

	@Bean
	public MyWicketServerEndpointConfig myEndpointConfig() {
		return new MyWicketServerEndpointConfig();
	}

}