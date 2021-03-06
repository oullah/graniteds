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
package org.granite.tide.seam;

import static org.jboss.seam.ScopeType.STATELESS;
import static org.jboss.seam.annotations.Install.FRAMEWORK;

import javax.faces.model.DataModel;

import org.granite.context.GraniteContext;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.faces.DataModels;


/**
 * TideDataModels override to disable DataModel wrapping
 * 
 * @author William DRAI
 */
@Name("org.jboss.seam.faces.dataModels")
@Install(precedence=FRAMEWORK, classDependencies="javax.faces.context.FacesContext")
@Scope(STATELESS)
@BypassInterceptors
public class TideDataModels extends DataModels {    
    
    @Override
    public DataModel getDataModel(Object value) {
        GraniteContext context = GraniteContext.getCurrentInstance();
        if (context == null)
            return super.getDataModel(value);
        
        return new TideDataModel(value);
    }
}
