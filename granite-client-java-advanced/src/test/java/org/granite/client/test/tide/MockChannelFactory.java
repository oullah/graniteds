/**
 *   GRANITE DATA SERVICES
 *   Copyright (C) 2006-2014 GRANITE DATA SERVICES S.A.S.
 *
 *   This file is part of the Granite Data Services Platform.
 *
 *                               ***
 *
 *   Community License: GPL 3.0
 *
 *   This file is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published
 *   by the Free Software Foundation, either version 3 of the License,
 *   or (at your option) any later version.
 *
 *   This file is distributed in the hope that it will be useful, but
 *   WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *                               ***
 *
 *   Available Commercial License: GraniteDS SLA 1.0
 *
 *   This is the appropriate option if you are creating proprietary
 *   applications and you are not prepared to distribute and share the
 *   source code of your application under the GPL v3 license.
 *
 *   Please visit http://www.granitedataservices.com/license for more
 *   details.
 */
package org.granite.client.test.tide;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.granite.client.messaging.ClientAliasRegistry;
import org.granite.client.messaging.ServerApp;
import org.granite.client.messaging.channel.ChannelBuilder;
import org.granite.client.messaging.channel.ChannelFactory;
import org.granite.client.messaging.channel.ChannelType;
import org.granite.client.messaging.channel.MessagingChannel;
import org.granite.client.messaging.channel.RemotingChannel;
import org.granite.client.messaging.codec.JMFAMF0MessagingCodec;
import org.granite.client.messaging.jmf.ClientSharedContext;
import org.granite.client.messaging.jmf.DefaultClientSharedContext;
import org.granite.client.messaging.transport.Transport;
import org.granite.client.test.MockAMFRemotingChannel;
import org.granite.client.test.MockTransport;
import org.granite.messaging.AliasRegistry;
import org.granite.util.ContentType;

public class MockChannelFactory implements ChannelFactory {

    private Transport transport = new MockTransport();
    private ClientSharedContext sharedContext = new DefaultClientSharedContext();

    public MockChannelFactory(Object context) {
    }
    
    @Override
    public ContentType getContentType() {
        return null;
    }

    @Override
    public long getDefaultTimeToLive() {
        return 0;
    }

    @Override
    public void setDefaultTimeToLive(long defaultTimeToLive) {

    }

    @Override
    public Object getContext() {
        return null;
    }

    @Override
    public void setContext(Object context) {

    }

    @Override
    public void setDefaultChannelType(String channelType) {

    }

    @Override
    public String getDefaultChannelType() {
        return null;
    }

    @Override
    public void setDefaultChannelBuilder(ChannelBuilder channelBuilder) {

    }

    @Override
    public Transport getRemotingTransport() {
        return transport;
    }

    @Override
    public void setRemotingTransport(Transport remotingTransport) {
    }

    @Override
    public void setMessagingTransport(Transport messagingTransport) {
    }

    @Override
    public void setMessagingTransport(String channelType, Transport messagingTransport) {

    }

    @Override
    public Transport getMessagingTransport() {
        return transport;
    }

    @Override
    public Map<String, Transport> getMessagingTransports() {
        return Collections.singletonMap(ChannelType.LONG_POLLING, transport);
    }

    @Override
    public Transport getMessagingTransport(String channelType) {
        return transport;
    }

    private ClientAliasRegistry aliasRegistry;
    private Set<String> scanPackageNames;

    @Override
    public void setAliasRegistry(AliasRegistry aliasRegistry) {
        this.aliasRegistry = (ClientAliasRegistry)aliasRegistry;
    }

    @Override
    public void setScanPackageNames(Set<String> packageNames) {
        this.scanPackageNames = packageNames;
    }

    @Override
    public void start() {
        if (scanPackageNames != null)
            aliasRegistry.scan(scanPackageNames);
    }

    @Override
    public void stop() {

    }

    @Override
    public void stop(boolean stopTransports) {

    }

    @Override
    public RemotingChannel newRemotingChannel(String id, String uri) {
    	try {
    		return new MockAMFRemotingChannel(transport, null, id, new URI(uri), 1);
    	}
    	catch (URISyntaxException e) {
    		return null;
    	}
    }

    @Override
    public RemotingChannel newRemotingChannel(String id, String uri, int maxConcurrentRequests) {
    	try {
    		return new MockAMFRemotingChannel(transport, new JMFAMF0MessagingCodec(sharedContext), id, new URI(uri), maxConcurrentRequests);
    	}
    	catch (URISyntaxException e) {
    		return null;
    	}
    }

    @Override
    public RemotingChannel newRemotingChannel(String id, URI uri) {
		return new MockAMFRemotingChannel(transport, new JMFAMF0MessagingCodec(sharedContext), id, uri, 1);
    }

    @Override
    public RemotingChannel newRemotingChannel(String id, URI uri, int maxConcurrentRequests) {
		return new MockAMFRemotingChannel(transport, new JMFAMF0MessagingCodec(sharedContext), id, uri, maxConcurrentRequests);
    }

    @Override
    public RemotingChannel newRemotingChannel(String id, ServerApp serverApp) {
    	try {
    		return new MockAMFRemotingChannel(transport, new JMFAMF0MessagingCodec(sharedContext), id, new URI("http://" + serverApp.getServerName() + ":" + serverApp.getServerPort() + "/" + serverApp.getContextRoot()), 1);
    	}
    	catch (URISyntaxException e) {
    		return null;
    	}
    }

    @Override
    public RemotingChannel newRemotingChannel(String id, ServerApp serverApp, int maxConcurrentRequests) {
    	try {
    		return new MockAMFRemotingChannel(transport, new JMFAMF0MessagingCodec(sharedContext), id, new URI("http://" + serverApp.getServerName() + ":" + serverApp.getServerPort() + "/" + serverApp.getContextRoot()), maxConcurrentRequests);
    	}
    	catch (URISyntaxException e) {
    		return null;
    	}
    }

    @Override
    public MessagingChannel newMessagingChannel(String id, String uri) {
        return null;
    }

    @Override
    public MessagingChannel newMessagingChannel(String channelType, String id, String uri) {
        return null;
    }

    @Override
    public MessagingChannel newMessagingChannel(String id, URI uri) {
        return null;
    }

    @Override
    public MessagingChannel newMessagingChannel(String channelType, String id, URI uri) {
        return null;
    }

    @Override
    public MessagingChannel newMessagingChannel(String id, ServerApp serverApp) {
        return null;
    }

    @Override
    public MessagingChannel newMessagingChannel(String channelType, String id, ServerApp serverApp) {
        return null;
    }
}