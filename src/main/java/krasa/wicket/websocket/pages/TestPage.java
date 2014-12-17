package krasa.wicket.websocket.pages;

import java.util.Date;
import krasa.wicket.websocket.AsyncEvent;
import org.apache.wicket.datetime.PatternDateConverter;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.*;
import org.apache.wicket.protocol.ws.api.*;
import org.apache.wicket.protocol.ws.api.message.IWebSocketPushMessage;

public class TestPage extends WebPage {

	public TestPage() {
		final Label label = new DateLabel("time", currentTimeModel(), new PatternDateConverter("HH:mm:ss", true));
		label.setOutputMarkupId(true);
		add(label);

		add(new WebSocketBehavior() {

			@Override
			protected void onPush(WebSocketRequestHandler handler, IWebSocketPushMessage message) {
				if (message instanceof AsyncEvent) {
					handler.add(label);
				}
			}
		});
	}

	private IModel<Date> currentTimeModel() {
		return new AbstractReadOnlyModel<Date>() {

			@Override public Date getObject() {
				return new Date();
			}
		};
	}
}
