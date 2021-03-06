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
package org.granite.gravity.jetty8;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketHandler;
import org.granite.context.GraniteContext;
import org.granite.gravity.GravityInternal;
import org.granite.gravity.GravityManager;
import org.granite.gravity.websocket.WebSocketUtil;
import org.granite.logging.Logger;
import org.granite.messaging.webapp.ServletGraniteContext;
import org.granite.util.ContentType;

import flex.messaging.messages.CommandMessage;
import flex.messaging.messages.Message;

/**
 * @author William DRAI
 */
public class JettyWebSocketHandler extends WebSocketHandler {
	
	private static final Logger log = Logger.getLogger(JettyWebSocketHandler.class);
	
	private final ServletContext servletContext;
	
	public JettyWebSocketHandler(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
	
    public WebSocket doWebSocketConnect(HttpServletRequest request, String protocol) {
    	if (!protocol.startsWith("org.granite.gravity"))
    		return null;

        GravityInternal gravity = (GravityInternal)GravityManager.getGravity(servletContext);
		JettyWebSocketChannelFactory channelFactory = new JettyWebSocketChannelFactory(gravity);
		
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
                gravity.getGraniteConfig().getSecurityService().prelogin(session, request, null);

			CommandMessage pingMessage = new CommandMessage();
			pingMessage.setMessageId(connectMessageId != null ? connectMessageId : "OPEN_CONNECTION");
			pingMessage.setOperation(CommandMessage.CLIENT_PING_OPERATION);
			if (clientId != null)
				pingMessage.setClientId(clientId);
			
			Message ackMessage = gravity.handleMessage(channelFactory, pingMessage);
            if (sessionId != null)
                ackMessage.setHeader("JSESSIONID", sessionId);
			
			JettyWebSocketChannel channel = gravity.getChannel(channelFactory, (String)ackMessage.getClientId());
            channel.setSession(session);

            String ctype = request.getContentType();
            ContentType contentType = WebSocketUtil.getContentType(ctype, protocol);
			channel.setContentType(contentType);
			
			if (!ackMessage.getClientId().equals(clientId))
				channel.setConnectAckMessage(ackMessage);
			
			return channel;
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
