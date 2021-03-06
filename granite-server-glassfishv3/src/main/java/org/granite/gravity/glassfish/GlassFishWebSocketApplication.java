/**
 *   GRANITE DATA SERVICES
 *   Copyright (C) 2006-2014 GRANITE DATA SERVICES S.A.S.
 *
 *   This file is part of the Granite Data Services Platform.
 *
 *   Granite Data Services is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU Lesser General Public
 *   License as published by the Free Software Foundation; either
 *   version 2.1 of the License, or (at your option) any later version.
 *
 *   Granite Data Services is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 *   General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the Free Software
 *   Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301,
 *   USA, or see <http://www.gnu.org/licenses/>.
 */
package org.granite.gravity.glassfish;

import com.sun.grizzly.tcp.Request;
import com.sun.grizzly.websockets.DefaultWebSocket;
import com.sun.grizzly.websockets.WebSocket;
import com.sun.grizzly.websockets.WebSocketApplication;

import flex.messaging.messages.CommandMessage;
import flex.messaging.messages.Message;

import org.granite.context.GraniteContext;
import org.granite.gravity.GravityInternal;
import org.granite.gravity.websocket.WebSocketUtil;
import org.granite.logging.Logger;
import org.granite.messaging.webapp.ServletGraniteContext;
import org.granite.util.ContentType;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;


public class GlassFishWebSocketApplication extends WebSocketApplication {
	
	private static final Logger log = Logger.getLogger(GlassFishWebSocketApplication.class);
	
	private final ServletContext servletContext;
	private final GravityInternal gravity;
	private final Pattern mapping;
    private final String servletName;
    private String protocol;


	public GlassFishWebSocketApplication(ServletContext servletContext, GravityInternal gravity, String mapping, String servletName) {
		this.servletContext = servletContext;
		this.gravity = gravity;
		this.mapping = Pattern.compile(".*" + mapping.replace("*", ".*") + "$");
        this.servletName = servletName;
	}

	@Override
	public List<String> getSupportedProtocols(List<String> subProtocols) {
        for (String subProtocol : subProtocols) {
		    if (subProtocol.startsWith("org.granite.gravity")) {
                this.protocol = subProtocol;
			    return Collections.singletonList(subProtocol);
            }
        }
		return Collections.emptyList();
	}

	@Override
	public boolean isApplicationRequest(Request request) {
        final String uri = request.requestURI().toString();
        return mapping.matcher(uri).matches();
	}

	@Override
    public void onConnect(WebSocket websocket) {
        if (!(websocket instanceof DefaultWebSocket))
            throw new IllegalStateException("Only DefaultWebSocket supported");

		GlassFishWebSocketChannelFactory channelFactory = new GlassFishWebSocketChannelFactory(gravity);
        HttpServletRequest request = ((DefaultWebSocket)websocket).getRequest();
		
		try {
            String connectMessageId = getHeaderOrParameter(request, "connectId");
            String clientId = getHeaderOrParameter(request, "GDSClientId");
            String clientType = getHeaderOrParameter(request, "GDSClientType");
            
            String sessionId = null;
            HttpSession session = request.getSession("true".equals(servletContext.getInitParameter("org.granite.gravity.websocket.forceCreateSession")));
            if (session != null) {
                ServletGraniteContext.createThreadInstance(gravity.getGraniteConfig(), gravity.getServicesConfig(),
                        this.servletContext, session, clientType);

                sessionId = session.getId();
            }
            else if (request.getCookies() != null) {
                for (int i = 0; i < request.getCookies().length; i++) {
                    if ("JSESSIONID".equals(request.getCookies()[i].getName())) {
                        sessionId = request.getCookies()[i].getValue();
                        break;
                    }
                }
                ServletGraniteContext.createThreadInstance(gravity.getGraniteConfig(), gravity.getServicesConfig(),
                        this.servletContext, sessionId, clientType);
            }
            else {
                ServletGraniteContext.createThreadInstance(gravity.getGraniteConfig(), gravity.getServicesConfig(),
                        this.servletContext, (String)null, clientType);
            }

            log.info("WebSocket connection started %s clientId %s sessionId %s", protocol, clientId, sessionId);

            if (gravity.getGraniteConfig().getSecurityService() != null)
                gravity.getGraniteConfig().getSecurityService().prelogin(session, request, servletName);

            CommandMessage pingMessage = new CommandMessage();
			pingMessage.setMessageId(connectMessageId != null ? connectMessageId : "OPEN_CONNECTION");
			pingMessage.setOperation(CommandMessage.CLIENT_PING_OPERATION);
			if (clientId != null)
				pingMessage.setClientId(clientId);
			
			Message ackMessage = gravity.handleMessage(channelFactory, pingMessage);
            if (sessionId != null)
                ackMessage.setHeader("JSESSIONID", sessionId);

			GlassFishWebSocketChannel channel = gravity.getChannel(channelFactory, (String)ackMessage.getClientId());
            channel.setSession(session);

            String ctype = request.getContentType();
            ContentType contentType = WebSocketUtil.getContentType(ctype, protocol);
			channel.setContentType(contentType);

			if (!ackMessage.getClientId().equals(clientId))
				channel.setConnectAckMessage(ackMessage);
			
			channel.setWebSocket(websocket);
		}
		finally {
			GraniteContext.release();
		}
    }
    
    private static String getHeaderOrParameter(HttpServletRequest servletRequest, String key) {
    	String value = servletRequest.getHeader(key);
    	if (value == null)
    		value = servletRequest.getParameter(key);
    	return value;
    }
}
