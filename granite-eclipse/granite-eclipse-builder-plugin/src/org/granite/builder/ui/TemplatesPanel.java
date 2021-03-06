/**
 *   GRANITE DATA SERVICES
 *   Copyright (C) 2006-2013 GRANITE DATA SERVICES S.A.S.
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

package org.granite.builder.ui;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.granite.builder.GraniteBuilderContext;
import org.granite.builder.properties.Gas3Template;
import org.granite.builder.properties.Gas3Transformer;
import org.granite.builder.properties.GraniteProperties;
import org.granite.builder.util.SWTUtil;
import org.granite.generator.TemplateUri;
import org.granite.generator.as3.DefaultAs3TypeFactory;
import org.granite.generator.as3.JavaAs3GroovyTransformer;
import org.granite.generator.as3.LCDSAs3TypeFactory;
import org.granite.generator.as3.reflect.JavaType.Kind;
import org.granite.generator.java.DefaultJavaTypeFactory;
import org.granite.generator.java.JavaGroovyTransformer;
import org.granite.generator.java.template.JavaTemplateUris;
import org.granite.generator.javafx.DefaultJavaFXTypeFactory;
import org.granite.generator.javafx.template.JavaFXTemplateUris;
import org.granite.generator.template.StandardTemplateUris;

/**
 * @author Franck WOLFF
 */
public class TemplatesPanel extends Composite {

	private final GraniteProperties properties;
	
	private Tree templatesTree = null;
	private boolean initialized = false;

	public TemplatesPanel(Composite parent, GraniteBuilderContext context) throws CoreException {
        super(parent, SWT.NONE);
        this.properties = context.getProperties();
        initializeComponents();
	}

	public Set<Gas3Template> getTemplates() {
		if (!initialized)
			return properties.getGas3().getTemplates();
		
		Set<Gas3Template> templates = new HashSet<Gas3Template>(templatesTree.getItemCount());
		for (TreeItem kindItem : templatesTree.getItems()) {
			StringBuilder sb = new StringBuilder();
			if (kindItem.getItemCount() > 0)
				sb.append((String)kindItem.getItem(0).getData());
			if (kindItem.getItemCount() > 1)
				sb.append(';').append((String)kindItem.getItem(1).getData());
			templates.add(new Gas3Template((Kind)kindItem.getData(), sb.toString()));
		}
		return templates;
	}

	@Override
	public Rectangle getClientArea() {
		initializeContent();
		return super.getClientArea();
	}

	private void initializeContent() {
		if (!initialized) {
			for (Kind kind : Kind.values()) {
				TreeItem kindItem = SWTUtil.addTreeItem(templatesTree, SWTUtil.IMG_TEMPLATE, kind.name(), null, null);
				kindItem.setData(kind);
				TemplateUri[] uris = properties.getGas3().getMatchingTemplateUris(kind);
				for (TemplateUri uri : uris) {
					TreeItem uriItem = SWTUtil.addTreeItem(kindItem, SWTUtil.IMG_FILE, uri.getUri() + (uri.isBase() ? " (base)" : ""), null, null);
					uriItem.setData(uri.getUri());
				}
				kindItem.setExpanded(true);
			}
			initialized = true;
		}
	}
	
	private void initializeComponents() {
        setLayout(new GridLayout(2, false));
        
        Label text = new Label(this, SWT.NONE);
        text.setText("Templates used for generation:");
        text.setLayoutData(SWTUtil.newGridData(SWT.NONE, 2));
        
        templatesTree = new Tree(this, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        templatesTree.setLayoutData(new GridData(
            GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL |
            GridData.VERTICAL_ALIGN_FILL | GridData.GRAB_VERTICAL
        ));
        
		
		Composite buttons = new Composite(this, SWT.NONE);
		buttons.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		buttons.setLayout(new GridLayout(1, false));
		
		final Button editButton = SWTUtil.newButton(buttons, "Edit...", false, new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				editTemplatesHandler(e);
			}
		});
		
		final Button removeButton = SWTUtil.newButton(buttons, "Remove", false, new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				removeTemplatesHandler(e);
			}
		});

		///////////////////////////////////////////////////////////////////////
		// Flex
		
        Group flexGroup = new Group(buttons, SWT.SHADOW_ETCHED_IN);
        flexGroup.setText("Flex:");
        flexGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        flexGroup.setLayout(new GridLayout(1, false));

		SWTUtil.newButton(flexGroup, "Basic", true, new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				properties.getGas3().setAs3TypeFactory(DefaultAs3TypeFactory.class.getName());
				properties.getGas3().setTransformer(new Gas3Transformer(JavaAs3GroovyTransformer.class.getName()));
				
				Gas3Template template = properties.getGas3().getTemplate(Kind.ENTITY);
				template.setUri(StandardTemplateUris.ENTITY, false);
				template.setUri(StandardTemplateUris.ENTITY_BASE, true);
				
				template = properties.getGas3().getTemplate(Kind.REMOTE_DESTINATION);
				template.setUri(StandardTemplateUris.REMOTE, false);
				template.setUri(StandardTemplateUris.REMOTE_BASE, true);
				
				template = properties.getGas3().getTemplate(Kind.BEAN);
				template.setUri(StandardTemplateUris.BEAN, false);
				template.setUri(StandardTemplateUris.BEAN_BASE, true);

				template = properties.getGas3().getTemplate(Kind.INTERFACE);
				template.setUri(StandardTemplateUris.INTERFACE, false);

				template = properties.getGas3().getTemplate(Kind.ENUM);
				template.setUri(StandardTemplateUris.ENUM, false);
				
				for (TreeItem item : templatesTree.getItems())
					item.dispose();

				initialized = false;
				initializeContent();
			}
		});
		
		SWTUtil.newButton(flexGroup, "Tide", true, new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				properties.getGas3().setAs3TypeFactory(DefaultAs3TypeFactory.class.getName());
				properties.getGas3().setTransformer(new Gas3Transformer(JavaAs3GroovyTransformer.class.getName()));

				Gas3Template template = properties.getGas3().getTemplate(Kind.ENTITY);
				template.setUri(StandardTemplateUris.ENTITY, false);
				template.setUri(StandardTemplateUris.TIDE_ENTITY_BASE, true);
				
				template = properties.getGas3().getTemplate(Kind.REMOTE_DESTINATION);
				template.setUri(StandardTemplateUris.REMOTE, false);
				template.setUri(StandardTemplateUris.TIDE_REMOTE_BASE, true);
				
				template = properties.getGas3().getTemplate(Kind.BEAN);
				template.setUri(StandardTemplateUris.BEAN, false);
				template.setUri(StandardTemplateUris.TIDE_BEAN_BASE, true);

				template = properties.getGas3().getTemplate(Kind.INTERFACE);
				template.setUri(StandardTemplateUris.INTERFACE, false);

				template = properties.getGas3().getTemplate(Kind.ENUM);
				template.setUri(StandardTemplateUris.ENUM, false);

				for (TreeItem item : templatesTree.getItems())
					item.dispose();
				
				initialized = false;
				initializeContent();
			}
		});
		
		SWTUtil.newButton(flexGroup, "LCDS", true, new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				properties.getGas3().setAs3TypeFactory(LCDSAs3TypeFactory.class.getName());
				properties.getGas3().setTransformer(new Gas3Transformer(JavaAs3GroovyTransformer.class.getName()));

				Gas3Template template = properties.getGas3().getTemplate(Kind.ENTITY);
				template.setUri(StandardTemplateUris.BEAN, false);
				template.setUri(StandardTemplateUris.LCDS_BEAN_BASE, true);

				template = properties.getGas3().getTemplate(Kind.REMOTE_DESTINATION);
				template.setUris("");
				
				template = properties.getGas3().getTemplate(Kind.BEAN);
				template.setUri(StandardTemplateUris.BEAN, false);
				template.setUri(StandardTemplateUris.LCDS_BEAN_BASE, true);

				template = properties.getGas3().getTemplate(Kind.INTERFACE);
				template.setUri(StandardTemplateUris.INTERFACE, false);

				template = properties.getGas3().getTemplate(Kind.ENUM);
				template.setUris("");
				
				for (TreeItem item : templatesTree.getItems())
					item.dispose();
				
				initialized = false;
				initializeContent();
			}
		});

		///////////////////////////////////////////////////////////////////////
		// JavaFX
		
        Group javafxGroup = new Group(buttons, SWT.SHADOW_ETCHED_IN);
        javafxGroup.setText("JavaFX:");
        javafxGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        javafxGroup.setLayout(new GridLayout(1, false));

        SWTUtil.newButton(javafxGroup, "Basic", true, new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				properties.getGas3().setAs3TypeFactory(DefaultJavaFXTypeFactory.class.getName());
				properties.getGas3().setTransformer(new Gas3Transformer(JavaGroovyTransformer.class.getName()));

				Gas3Template template = properties.getGas3().getTemplate(Kind.ENTITY);
				template.setUri(JavaFXTemplateUris.ENTITY, false);
				template.setUri(JavaFXTemplateUris.ENTITY_BASE, true);
				
				template = properties.getGas3().getTemplate(Kind.REMOTE_DESTINATION);
				template.setUri(JavaFXTemplateUris.REMOTE, false);
				template.setUri(JavaFXTemplateUris.REMOTE_BASE, true);
				
				template = properties.getGas3().getTemplate(Kind.BEAN);
				template.setUri(JavaFXTemplateUris.BEAN, false);
				template.setUri(JavaFXTemplateUris.BEAN_BASE, true);

				template = properties.getGas3().getTemplate(Kind.INTERFACE);
				template.setUri(JavaFXTemplateUris.INTERFACE, false);

				template = properties.getGas3().getTemplate(Kind.ENUM);
				template.setUri(JavaFXTemplateUris.ENUM, false);

				for (TreeItem item : templatesTree.getItems())
					item.dispose();
				
				initialized = false;
				initializeContent();
			}
		});

        SWTUtil.newButton(javafxGroup, "Tide", true, new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				properties.getGas3().setAs3TypeFactory(DefaultJavaFXTypeFactory.class.getName());
				properties.getGas3().setTransformer(new Gas3Transformer(JavaGroovyTransformer.class.getName()));

				Gas3Template template = properties.getGas3().getTemplate(Kind.ENTITY);
				template.setUri(JavaFXTemplateUris.ENTITY, false);
				template.setUri(JavaFXTemplateUris.TIDE_ENTITY_BASE, true);
				
				template = properties.getGas3().getTemplate(Kind.REMOTE_DESTINATION);
				template.setUri(JavaFXTemplateUris.TIDE_REMOTE, false);
				template.setUri(JavaFXTemplateUris.TIDE_REMOTE_BASE, true);
				
				template = properties.getGas3().getTemplate(Kind.BEAN);
				template.setUri(JavaFXTemplateUris.BEAN, false);
				template.setUri(JavaFXTemplateUris.TIDE_BEAN_BASE, true);

				template = properties.getGas3().getTemplate(Kind.INTERFACE);
				template.setUri(JavaFXTemplateUris.INTERFACE, false);

				template = properties.getGas3().getTemplate(Kind.ENUM);
				template.setUri(JavaFXTemplateUris.ENUM, false);

				for (TreeItem item : templatesTree.getItems())
					item.dispose();
				
				initialized = false;
				initializeContent();
			}
		});

		///////////////////////////////////////////////////////////////////////
		// Java/Android
		
        Group javaGroup = new Group(buttons, SWT.SHADOW_ETCHED_IN);
        javaGroup.setText("Java/Android:");
        javaGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        javaGroup.setLayout(new GridLayout(1, false));

        SWTUtil.newButton(javaGroup, "Basic", true, new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				properties.getGas3().setAs3TypeFactory(DefaultJavaTypeFactory.class.getName());
				properties.getGas3().setTransformer(new Gas3Transformer(JavaGroovyTransformer.class.getName()));

				Gas3Template template = properties.getGas3().getTemplate(Kind.ENTITY);
				template.setUri(JavaTemplateUris.ENTITY, false);
				template.setUri(JavaTemplateUris.ENTITY_BASE, true);
				
				template = properties.getGas3().getTemplate(Kind.REMOTE_DESTINATION);
				template.setUri(JavaTemplateUris.REMOTE, false);
				template.setUri(JavaTemplateUris.REMOTE_BASE, true);
				
				template = properties.getGas3().getTemplate(Kind.BEAN);
				template.setUri(JavaTemplateUris.BEAN, false);
				template.setUri(JavaTemplateUris.BEAN_BASE, true);

				template = properties.getGas3().getTemplate(Kind.INTERFACE);
				template.setUri(JavaTemplateUris.INTERFACE, false);

				template = properties.getGas3().getTemplate(Kind.ENUM);
				template.setUri(JavaTemplateUris.ENUM, false);

				for (TreeItem item : templatesTree.getItems())
					item.dispose();
				
				initialized = false;
				initializeContent();
			}
		});

        SWTUtil.newButton(javaGroup, "Tide", true, new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				properties.getGas3().setAs3TypeFactory(DefaultJavaTypeFactory.class.getName());
				properties.getGas3().setTransformer(new Gas3Transformer(JavaGroovyTransformer.class.getName()));

				Gas3Template template = properties.getGas3().getTemplate(Kind.ENTITY);
				template.setUri(JavaTemplateUris.ENTITY, false);
				template.setUri(JavaTemplateUris.TIDE_ENTITY_BASE, true);
				
				template = properties.getGas3().getTemplate(Kind.REMOTE_DESTINATION);
				template.setUri(JavaTemplateUris.TIDE_REMOTE, false);
				template.setUri(JavaTemplateUris.TIDE_REMOTE_BASE, true);
				
				template = properties.getGas3().getTemplate(Kind.BEAN);
				template.setUri(JavaTemplateUris.BEAN, false);
				template.setUri(JavaTemplateUris.TIDE_BEAN_BASE, true);

				template = properties.getGas3().getTemplate(Kind.INTERFACE);
				template.setUri(JavaTemplateUris.INTERFACE, false);

				template = properties.getGas3().getTemplate(Kind.ENUM);
				template.setUri(JavaTemplateUris.ENUM, false);

				for (TreeItem item : templatesTree.getItems())
					item.dispose();
				
				initialized = false;
				initializeContent();
			}
		});
		
		templatesTree.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Enable/Disable buttons based on selected tree item.
				editButton.setEnabled(templatesTree.getSelection() != null && templatesTree.getSelection().length > 0);
				removeButton.setEnabled(templatesTree.getSelection() != null && templatesTree.getSelection().length > 0);
			}
		});
	}
	
	private void editTemplatesHandler(SelectionEvent e) {
		if (templatesTree.getSelection() == null || templatesTree.getSelection().length == 0)
			return;
		
		TreeItem kindItem = templatesTree.getSelection()[0];
		while (kindItem.getParentItem() != null)
			kindItem = kindItem.getParentItem();
		
		String templateUri = (kindItem.getItemCount() <= 0 ? "" : (String)kindItem.getItems()[0].getData());
		String baseTemplateUri = (kindItem.getItemCount() <= 1 ? "" : (String)kindItem.getItems()[1].getData());
		
		String[] uris = Dialogs.editTemplateUris(
			getDisplay().getActiveShell(),
			"Templates for " + kindItem.getText() + " type",
			templateUri,
			baseTemplateUri
		);
		
		if (uris != null) {
			if (kindItem.getItemCount() > 0) {
				kindItem.getItem(0).setText(uris[0]);
				kindItem.getItem(0).setData(uris[0]);
			}
			else {
				TreeItem uriItem = SWTUtil.addTreeItem(kindItem, SWTUtil.IMG_FILE, uris[0], null, null);
				uriItem.setData(uris[0]);
			}
			
			if (uris[1].length() > 0) {
				if (kindItem.getItemCount() > 1) {
					kindItem.getItem(1).setText(uris[1]);
					kindItem.getItem(1).setData(uris[1]);
				}
				else {
					TreeItem uriItem = SWTUtil.addTreeItem(kindItem, SWTUtil.IMG_FILE, uris[1], null, null);
					uriItem.setData(uris[1]);
				}
			}
			else if (kindItem.getItemCount() > 1)
				kindItem.getItem(1).dispose();
		}
	}
	
	private void removeTemplatesHandler(SelectionEvent e) {
		if (templatesTree.getSelection() == null || templatesTree.getSelection().length == 0)
			return;
		
		TreeItem kindItem = templatesTree.getSelection()[0];
		if (kindItem.getParentItem() == null) {
			for (TreeItem child : kindItem.getItems())
				child.dispose();
		}
		else
			kindItem.dispose();
	}
}
