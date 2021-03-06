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
package org.granite.gravity;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Constructor;

import org.granite.config.GraniteConfig;
import org.granite.context.GraniteContext;
import org.granite.gravity.selector.GravityMessageSelector;
import org.granite.gravity.selector.MessageSelector;
import org.granite.logging.Logger;
import org.granite.util.TypeUtil;

import flex.messaging.messages.AsyncMessage;
import flex.messaging.messages.CommandMessage;
import flex.messaging.messages.Message;

/**
 * @author William DRAI
 */
public class Subscription implements Serializable {

	private static final long serialVersionUID = -8527072003319223252L;

	private static final Logger log = Logger.getLogger(Subscription.class);
	
    private final Channel channel;
    private final String destination;
    private final String subTopicId;
    private final String subscriptionId;
    private String selectorText;
    private String selectorClassName;
    private transient Constructor<?> messageSelectorConstructor;
    private transient MessageSelector selector;
    private final boolean noLocal;


    public Subscription(Channel channel, String destination, String subTopicId, String subscriptionId, boolean noLocal) {
        super();
        this.channel = channel;
        this.destination = destination;
        this.subTopicId = subTopicId;
        this.subscriptionId = subscriptionId;
        this.noLocal = noLocal;
    }
    
    private void readObject(java.io.ObjectInputStream in) throws ClassNotFoundException, IOException {
    	in.defaultReadObject();
    	if (selectorClassName != null) {
    		try {
    			messageSelectorConstructor = TypeUtil.getConstructor(selectorClassName, new Class<?>[] { String.class });
    		}
    		catch (NoSuchMethodException e) {
    			throw new IOException("Could not get message selector: " + selectorClassName);
    		}
    	}
    	parseSelector();
    }

    public Channel getChannel() {
        return channel;
    }

    public String getDestination() {
        return destination;
    }

    public String getSubTopicId() {
        return subTopicId;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }


    public void setSelector(String selector) {
    	this.selectorText = selector;
    	parseSelector();
    }
    
    private void parseSelector() {
        if (selectorText != null) {
            try {
            	Constructor<?> messageSelectorConstructor = this.messageSelectorConstructor;
            	if (messageSelectorConstructor == null) {
            		GraniteContext context = GraniteContext.getCurrentInstance();
            		if (context == null)
            			throw new IllegalStateException("Cannot parse selector outside of GDS context");
            		messageSelectorConstructor = ((GraniteConfig)context.getGraniteConfig()).getMessageSelectorConstructor();
            	}
                if (messageSelectorConstructor == null)
                    this.selector = new GravityMessageSelector(selectorText);
                else
                    this.selector = (MessageSelector)messageSelectorConstructor.newInstance(selectorText);
            }
            catch (Exception e) {
                throw new RuntimeException("Could not create message selector", e);
            }
        }
    }
    
    
    public boolean accept(Channel fromClient, AsyncMessage message) {
    	if (noLocal && message.getClientId().equals(channel.getId()))
    		return false;
    	
        return selector == null || selector.accept(message);
    }
    

    public boolean deliver(Channel fromClient, AsyncMessage message) {
    	if (!accept(fromClient, message))
    		return false;
    	
        try {
    		message.setHeader(AsyncMessage.DESTINATION_CLIENT_ID_HEADER, subscriptionId);
            log.debug("Channel %s deliver message to subscription %s", channel.getId(), subscriptionId);
			getChannel().receive(message);
            return true;
		} 
        catch (MessageReceivingException e) {
			log.error(e, "Could not deliver message");
		}

        return false;
    }

    public Message getUnsubscribeMessage() {
    	CommandMessage unsubscribeMessage = new CommandMessage();
    	unsubscribeMessage.setOperation(CommandMessage.UNSUBSCRIBE_OPERATION);
    	unsubscribeMessage.setClientId(getChannel().getId());
    	unsubscribeMessage.setDestination(destination);
    	unsubscribeMessage.setHeader(AsyncMessage.SUBTOPIC_HEADER, getSubTopicId());
    	unsubscribeMessage.setHeader(AsyncMessage.DESTINATION_CLIENT_ID_HEADER, getSubscriptionId());
    	return unsubscribeMessage;
    }
    

    @Override
    public boolean equals(Object o) {
        if (!o.getClass().equals(Subscription.class))
            return false;

        Subscription s = (Subscription)o;
        return getChannel().equals(s.getChannel()) && getSubscriptionId().equals(s.getSubscriptionId());
    }

	@Override
	public int hashCode() {
		return (31 * getChannel().hashCode()) + getSubscriptionId().hashCode();
	}

	@Override
	public String toString() {
		return subscriptionId + ":" + subTopicId;
	}
}
