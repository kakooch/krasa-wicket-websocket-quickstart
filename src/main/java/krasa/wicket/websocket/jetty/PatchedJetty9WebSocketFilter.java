/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package krasa.wicket.websocket.jetty;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.*;
import org.apache.wicket.protocol.ws.AbstractUpgradeFilter;
import org.eclipse.jetty.websocket.api.WebSocketPolicy;
import org.eclipse.jetty.websocket.server.WebSocketServerFactory;
import org.eclipse.jetty.websocket.servlet.*;
import org.slf4j.*;

/**
 * An upgrade filter that uses Jetty9's WebSocketServerFactory to decide whether to upgrade or not.
 */
public class PatchedJetty9WebSocketFilter extends AbstractUpgradeFilter {

	private static final Logger LOG = LoggerFactory.getLogger(PatchedJetty9WebSocketFilter.class);

	private WebSocketServerFactory _webSocketFactory;

	@Override
	public void init(final boolean isServlet, final FilterConfig filterConfig)
			throws ServletException {
		super.init(isServlet, filterConfig);

		try {
			WebSocketPolicy serverPolicy = WebSocketPolicy.newServerPolicy();
			String bs = filterConfig.getInitParameter("inputBufferSize");
			if (bs != null) {
				serverPolicy.setInputBufferSize(Integer.parseInt(bs));
			}
			String max = filterConfig.getInitParameter("maxIdleTime");
			if (max != null) {
				serverPolicy.setIdleTimeout(Integer.parseInt(max));
			}

			max = filterConfig.getInitParameter("maxMessageSize");
			if (max != null) {
				serverPolicy.setMaxTextMessageBufferSize(Integer.parseInt(max));
			}

			_webSocketFactory = new WebSocketServerFactory(serverPolicy);

			_webSocketFactory.setCreator(new WebSocketCreator() {

				@Override public Object createWebSocket(ServletUpgradeRequest req, ServletUpgradeResponse resp) {

					return new PatchedJetty9WebSocketProcessor(req, resp,
							getApplication());
				}

			});

			_webSocketFactory.start();
		} catch (ServletException x) {
			throw x;
		} catch (Exception x) {
			throw new ServletException(x);
		}
	}

	@Override
	protected boolean acceptWebSocket(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		return super.acceptWebSocket(req, resp) &&
				_webSocketFactory.acceptWebSocket(req, resp);
	}

	/* ------------------------------------------------------------ */
	@Override
	public void destroy() {
		try {
			if (_webSocketFactory != null) {
				_webSocketFactory.stop();
			}
		} catch (Exception x) {
			LOG.warn("A problem occurred while stopping the web socket factory", x);
		}

		super.destroy();
	}
}
