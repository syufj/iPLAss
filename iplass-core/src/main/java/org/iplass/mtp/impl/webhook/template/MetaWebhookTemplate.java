/*
 * Copyright (C) 2020 INFORMATION SERVICES INTERNATIONAL - DENTSU, LTD. All Rights Reserved.
 * 
 * Unless you have purchased a commercial license,
 * the following license terms apply:
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.iplass.mtp.impl.webhook.template;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import org.iplass.mtp.impl.core.ExecuteContext;
import org.iplass.mtp.impl.definition.DefinableMetaData;
import org.iplass.mtp.impl.metadata.BaseMetaDataRuntime;
import org.iplass.mtp.impl.metadata.BaseRootMetaData;
import org.iplass.mtp.impl.metadata.MetaDataConfig;
import org.iplass.mtp.impl.script.GroovyScriptEngine;
import org.iplass.mtp.impl.script.ScriptEngine;
import org.iplass.mtp.impl.script.ScriptRuntimeException;
import org.iplass.mtp.impl.script.template.GroovyTemplate;
import org.iplass.mtp.impl.script.template.GroovyTemplateBinding;
import org.iplass.mtp.impl.script.template.GroovyTemplateCompiler;
import org.iplass.mtp.impl.util.ObjectUtil;
import org.iplass.mtp.webhook.Webhook;
import org.iplass.mtp.webhook.WebhookHeader;
import org.iplass.mtp.webhook.template.definition.WebhookHeaderDefinition;
import org.iplass.mtp.webhook.template.definition.WebhookTemplateDefinition;

@XmlRootElement
public class MetaWebhookTemplate extends BaseRootMetaData implements DefinableMetaData<WebhookTemplateDefinition> {

	private static final long serialVersionUID = 6383360434482999137L;
	
	/** webhook 内容部分 */
	private String contentType;
	private String webhookContent;
	private String httpMethod;

	private List<MetaWebhookHeader> headers;
	
	private String urlQuery;

	@Override
	public WebhookTemplateRuntime createRuntime(MetaDataConfig metaDataConfig) {
		return new WebhookTemplateRuntime();
	}

	@Override
	public MetaWebhookTemplate copy() {
		return ObjectUtil.deepCopy(this);
	}

	//Definition → Meta
	@Override
	public void applyConfig(WebhookTemplateDefinition definition) {
		name = definition.getName();
		displayName = definition.getDisplayName();
		description = definition.getDescription();
		
		contentType = definition.getContentType();
		webhookContent = definition.getWebhookContent();
		httpMethod = definition.getHttpMethod();
		urlQuery = definition.getUrlQuery();
		
		ArrayList<MetaWebhookHeader> newHeaders = new ArrayList<MetaWebhookHeader>();
		if (definition.getHeaders()!=null) {
			for (WebhookHeaderDefinition headerDefinition: definition.getHeaders()) {
				MetaWebhookHeader temp = new MetaWebhookHeader();
				temp.applyConfig(headerDefinition);
				newHeaders.add(temp);
			}
		}
		headers = newHeaders;
	}



	//Meta → Definition
	@Override
	public WebhookTemplateDefinition currentConfig() {
		WebhookTemplateDefinition definition = new WebhookTemplateDefinition();
		definition.setName(name);
		definition.setDisplayName(displayName);
		definition.setDescription(description);
		
		definition.setContentType(contentType);
		definition.setWebhookContent(webhookContent);
		definition.setUrlQuery(urlQuery);
		definition.setHttpMethod(httpMethod);
		
		ArrayList<WebhookHeaderDefinition> newHeaders = new ArrayList<WebhookHeaderDefinition>();
		if (headers!=null) {
			for (MetaWebhookHeader metaHeader: headers) {
				newHeaders.add(metaHeader.currentConfig());
			}
		}
		definition.setHeaders(newHeaders);
		
		return definition;
	}

	public List<MetaWebhookHeader> getHeaders() {
		return headers;
	}

	public void setHeaders(List<MetaWebhookHeader> headers) {
		this.headers = headers;
	}

	public String getHttpMethod() {
		return httpMethod;
	}

	public void setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
	}
	
	public String getContentType() {
		if (contentType == null) {
			contentType = "";
		}
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getWebhookContent() {
		if (webhookContent == null) {
			webhookContent = "";
		}
		return webhookContent;
	}

	public String getUrlQuery() {
		if (urlQuery == null) {
			urlQuery = "";
		}
		return urlQuery;
	}

	public void setUrlQuery(String urlQuery) {
		this.urlQuery = urlQuery;
	}

	public void setWebhookContent(String webhookContent) {
		this.webhookContent = webhookContent;
	}

	public class WebhookTemplateRuntime extends BaseMetaDataRuntime {
		private GroovyTemplate contentTemplate;
		private GroovyTemplate urlQueryTemplate;
		
		public WebhookTemplateRuntime() {
			super();
			try {
				ScriptEngine se = ExecuteContext.getCurrentContext().getTenantContext().getScriptEngine();
				contentTemplate = GroovyTemplateCompiler.compile(getWebhookContent(), "WebhookTemplate_Text" + getName(), (GroovyScriptEngine) se);
				urlQueryTemplate = GroovyTemplateCompiler.compile(getUrlQuery(), "WebhookUrlQueryTemplate_Text" + getName(), (GroovyScriptEngine) se);
			} catch (RuntimeException e) {
				setIllegalStateException(e);
			}
		}


		public GroovyTemplate getUrlQueryTemplate() {
			return urlQueryTemplate;
		}
		
		public GroovyTemplate getContentTemplate() {
			return contentTemplate;
		}
		@Override
		public MetaWebhookTemplate getMetaData() {
			return MetaWebhookTemplate.this;
		}
		
		public Webhook createWebhook(Map<String, Object> parameter) {
			checkState();
			
			//fill up the info to webhooktemplate
			Webhook webhook = new Webhook(); 
			
			ArrayList<WebhookHeader> newHeaders = new ArrayList<WebhookHeader>();
			if (headers !=null) {
				for (MetaWebhookHeader metaHeader: headers) {
					newHeaders.add(new WebhookHeader(metaHeader.getKey(),metaHeader.getValue()));
				}
			}
			webhook.setHeaders(newHeaders);
			webhook.setHttpMethod(httpMethod);
			webhook.setContentType(contentType);
			webhook.setUrlQuery(urlQuery);
			//common binding
			Map<String, Object> binding = new HashMap<String, Object>();
			if (parameter != null) {
				for (Map.Entry<String, Object> e: parameter.entrySet()) {
					binding.put(e.getKey(), e.getValue());
				}
			}
			webhook.setContentType(contentType);
			binding.put("webhook", webhook);

			//template
			if (contentTemplate != null) {
				StringWriter sw = new StringWriter();
				GroovyTemplateBinding gtb = new GroovyTemplateBinding(sw,binding);
				try {
					contentTemplate.doTemplate(gtb);
				} catch (IOException e) {
					throw new ScriptRuntimeException(e);
				}
				webhook.setPayloadContent(sw.toString());
			}
			if (urlQueryTemplate!=null) {
				StringWriter sw = new StringWriter();
				GroovyTemplateBinding gtb = new GroovyTemplateBinding(sw,binding);
				try {
					urlQueryTemplate.doTemplate(gtb);
				} catch (IOException e) {
					throw new ScriptRuntimeException(e);
				}
				webhook.setUrlQuery(sw.toString());
			}
			return webhook;
		}
		
		
		
	} 
}