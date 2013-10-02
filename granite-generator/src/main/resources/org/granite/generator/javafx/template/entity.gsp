<%--
  GRANITE DATA SERVICES
  Copyright (C) 2011 GRANITE DATA SERVICES S.A.S.

  This file is part of Granite Data Services.

  Granite Data Services is free software; you can redistribute it and/or modify
  it under the terms of the GNU Library General Public License as published by
  the Free Software Foundation; either version 2 of the License, or (at your
  option) any later version.

  Granite Data Services is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
  FITNESS FOR A PARTICULAR PURPOSE. See the GNU Library General Public License
  for more details.

  You should have received a copy of the GNU Library General Public License
  along with this library; if not, see <http://www.gnu.org/licenses/>.

  @author Franck WOLFF
--%>/**
 * Generated by Gfx v${gVersion} (Granite Data Services).
 *
 * NOTE: this file is only generated if it does not exist. You may safely put
 * your custom code here.
 */

package ${jClass.clientType.packageName};<%

    ///////////////////////////////////////////////////////////////////////////
    // Write Import Statements.

	Set javaImports = new TreeSet();
	
	javaImports.add("org.granite.messaging.annotations.Serialized");
	javaImports.add("org.granite.client.persistence.Entity");
	javaImports.add("org.granite.client.messaging.RemoteAlias");
	
    if (jClass.hasInterfaces()) {
        for (jProperty in jClass.interfacesProperties) {
            if (jProperty.clientType.hasPackage() && jProperty.clientType.packageName != jClass.clientType.packageName)
            	javaImports.add(jProperty.clientType.qualifiedName);
        }
    }

    if (javaImports.size() > 0) {%>
<%
    }
    for (javaImport in javaImports) {%>
import ${javaImport};<%
    }
    %>

@Entity
@Serialized
@RemoteAlias("${jClass.qualifiedName}")
public class ${jClass.clientType.name} extends ${jClass.clientType.name}Base {

    private static final long serialVersionUID = 1L;<%

    ///////////////////////////////////////////////////////////////////////////
    // (Re)Write Public Getters/Setters for Implemented Interfaces.

    if (jClass.hasInterfaces()) {
        for (jProperty in jClass.interfacesProperties) {
            if (jProperty.readable || jProperty.writable) {%>
<%
                if (jProperty.writable) {%>
    @Override
    public void set${jProperty.capitalizedName}(${jProperty.clientType.name} value) {
        // TODO: Gas3 empty generated setter.
    }<%
                }
                if (jProperty.readable) {%>
    @Override
    public ${jProperty.clientType.name} get${jProperty.capitalizedName}() {
        // TODO: Gas3 default generated getter.
        return ${jProperty.clientType.nullValue};
    }<%
                }
            }
        }
    }%>
}