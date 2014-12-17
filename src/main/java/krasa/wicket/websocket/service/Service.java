package krasa.wicket.websocket.service;

import java.util.Collection;
import java.util.concurrent.*;
import krasa.wicket.websocket.WebInitializer;
import org.apache.wicket.Application;
import org.apache.wicket.protocol.ws.WebSocketSettings;
import org.apache.wicket.protocol.ws.api.IWebSocketConnection;
import org.apache.wicket.protocol.ws.api.message.IWebSocketPushMessage;
import org.apache.wicket.protocol.ws.api.registry.IWebSocketConnectionRegistry;
import org.slf4j.*;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class Service implements ApplicationListener<ContextRefreshedEvent> {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	private ScheduledThreadPoolExecutor scheduledExecutorService = new ScheduledThreadPoolExecutor(1);

	public void sendEvent(IWebSocketPushMessage event) {
		Application application = Application.get(WebInitializer.WICKET_WEBSOCKET);
		WebSocketSettings webSocketSettings = WebSocketSettings.Holder.get(application);
		IWebSocketConnectionRegistry connectionRegistry = webSocketSettings.getConnectionRegistry();
		Collection<IWebSocketConnection> connections = connectionRegistry.getConnections(application);
		log.trace("sending event to {} connections", connections.size());
		for (IWebSocketConnection connection : connections) {
			connection.sendMessage(event);
		}
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (scheduledExecutorService.getTaskCount() > 0) {
			return;
		}
		scheduledExecutorService.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				try {
					sendEvent(new AsyncEvent());
				} catch (Throwable e) {
					log.error(String.valueOf(e), e);
				}
			}
		}, 1, 1, TimeUnit.SECONDS);
	}
}
