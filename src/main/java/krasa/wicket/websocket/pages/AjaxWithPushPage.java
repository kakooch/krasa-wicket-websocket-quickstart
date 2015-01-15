package krasa.wicket.websocket.pages;

import java.util.Date;
import krasa.wicket.websocket.service.AsyncEvent;
import org.apache.wicket.ajax.*;
import org.apache.wicket.datetime.PatternDateConverter;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.*;
import org.apache.wicket.protocol.ws.api.*;
import org.apache.wicket.protocol.ws.api.message.IWebSocketPushMessage;
import org.apache.wicket.util.time.Duration;

public class AjaxWithPushPage extends WebPage {

	public AjaxWithPushPage() {
		add(new BookmarkablePageLink("TestPage", TestPage.class));
		push();
		ajax();
	}

	private void push() {
		final Label timePush = new DateLabel("timePush", currentTimeModel(),
				new PatternDateConverter("HH:mm:ss", true));
		timePush.setOutputMarkupId(true);
		add(timePush);

		add(new WebSocketBehavior() {

			@Override
			protected void onPush(WebSocketRequestHandler handler, IWebSocketPushMessage message) {
				if (message instanceof AsyncEvent) {
					handler.add(timePush);
				}
			}
		});
	}

	private void ajax() {
		final Label timeAjax = new DateLabel("timeAjax", currentTimeModel(),
				new PatternDateConverter("HH:mm:ss", true));
		timeAjax.setOutputMarkupId(true);
		add(timeAjax);

		AbstractAjaxTimerBehavior abstractAjaxTimerBehavior = new AbstractAjaxTimerBehavior(Duration.seconds(1)) {

			@Override
			protected void onTimer(AjaxRequestTarget ajaxRequestTarget) {
				ajaxRequestTarget.add(timeAjax);
			}
		};
		add(abstractAjaxTimerBehavior);
	}

	private IModel<Date> currentTimeModel() {
		return new AbstractReadOnlyModel<Date>() {

			@Override public Date getObject() {
				return new Date();
			}
		};
	}
}
