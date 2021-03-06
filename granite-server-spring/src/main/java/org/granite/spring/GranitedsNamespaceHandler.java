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
package org.granite.spring;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ServiceLoader;

import org.granite.tide.spring.TideDataPublishingAdviceBeanDefinitionParser;
import org.granite.tide.spring.TideIdentityBeanDefinitionParser;
import org.granite.tide.spring.TidePersistenceBeanDefinitionParser;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;


public class GranitedsNamespaceHandler extends NamespaceHandlerSupport {

    public void init() {
        registerBeanDefinitionParser("server-filter", new ServerFilterBeanDefinitionParser());
        registerBeanDefinitionParser("security-service", new SecurityServiceBeanDefinitionParser());
        registerBeanDefinitionParser("remote-destination", new RemoteDestinationBeanDefinitionParser());
        registerBeanDefinitionParser("messaging-destination", new MessagingDestinationBeanDefinitionParser());
        registerBeanDefinitionParser("jms-topic-destination", new JmsTopicDestinationBeanDefinitionParser());
        registerBeanDefinitionParser("activemq-topic-destination", new ActiveMQTopicDestinationBeanDefinitionParser());
        registerBeanDefinitionParser("tide-persistence", new TidePersistenceBeanDefinitionParser());
        registerBeanDefinitionParser("tide-identity", new TideIdentityBeanDefinitionParser());
        registerBeanDefinitionParser("tide-data-publishing-advice", new TideDataPublishingAdviceBeanDefinitionParser());
        
        Map<String, BeanDefinitionParser> parsersMap = new HashMap<String, BeanDefinitionParser>();
        
        ServiceLoader<SpringConfigurator> configurators = ServiceLoader.load(SpringConfigurator.class);
        for (Iterator<SpringConfigurator> iconf = configurators.iterator(); iconf.hasNext(); ) {
        	SpringConfigurator conf = iconf.next();
        	conf.buildParsers(parsersMap);
        }
        
    	for (Entry<String, BeanDefinitionParser> ep : parsersMap.entrySet())
    		registerBeanDefinitionParser(ep.getKey(), ep.getValue());
    }

}