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
package org.iplass.mtp.impl.webhook.endpointaddress;

import org.iplass.mtp.definition.DefinitionModifyResult;
import org.iplass.mtp.impl.definition.AbstractTypedDefinitionManager;
import org.iplass.mtp.impl.definition.TypedMetaDataService;
import org.iplass.mtp.impl.metadata.RootMetaData;
import org.iplass.mtp.impl.script.template.GroovyTemplate;
import org.iplass.mtp.spi.ServiceRegistry;
import org.iplass.mtp.webhook.endpoint.definition.WebHookEndPointDefinition;
import org.iplass.mtp.webhook.endpoint.definition.WebHookEndPointDefinitionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebHookEndPointDefinitionManagerImpl extends AbstractTypedDefinitionManager<WebHookEndPointDefinition> implements WebHookEndPointDefinitionManager{
	private static final Logger logger = LoggerFactory.getLogger(WebHookEndPointDefinitionManager.class);
	private WebHookEndPointService service;
	
	public WebHookEndPointDefinitionManagerImpl() {
		this.service = ServiceRegistry.getRegistry().getService(WebHookEndPointService.class);
	}
	
	@Override
	public Class<WebHookEndPointDefinition> getDefinitionType() {
		return WebHookEndPointDefinition.class; 
	}

	@Override
	protected RootMetaData newInstance(WebHookEndPointDefinition definition) {
		return new MetaWebHookEndPointDefinition();
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected TypedMetaDataService getService() {
		return service;
	}

	/**
	 * remove時、自動でdb削除。
	 * */
	@Override
	public DefinitionModifyResult remove(String definitionName) {
		WebHookEndPointDefinition definition = super.get(definitionName);
		
		WebHookEndPointService weps = (WebHookEndPointService) getService();
		try {
			weps.deleteSecurityTokenByDefinitionName(definition.getName());
		} catch (Exception e) {
			logger.warn("Exception occured while removing the data from Database for :"+definitionName+". Caused by: "+e.getCause()+". With following message : "+e.getCause().getMessage());
			throw (RuntimeException) e;
		}
		
		return super.remove(definitionName);
	}
	
	/**
	 * <p>updateToken</p> 
	 *  トークンタイプ:WHHM,WHBT,WHBA
	 * 
	 * */
	@Override
	public void modifySecurityToken(int tenantId, String definitionName, String secret, String tokenType) {
		if(tokenType==null) {
			throw new RuntimeException("null TokenType");
		}
		if( tokenType.equals("WHHM")){
			service.updateHmacSecurityTokenByDefinitionName(tenantId,definitionName,secret);
		}
		if( tokenType.equals("WHBT")){
			service.updateBearerSecurityTokenByDefinitionName(tenantId,definitionName,secret);
		}
		if( tokenType.equals("WHBA")){
			service.updateBasicSecurityTokenByDefinitionName(tenantId,definitionName,secret);
		}
		if( tokenType.equals("WHCT")){
			service.updateCustomSecurityTokenByDefinitionName(tenantId,definitionName,secret);
		}
	}

	@Override
	public String generateHmacKey() {
		return service.generateHmacTokenString();
	}

	public GroovyTemplate  getUrlTemplateByName(String definitionName) {
		return service.getUrlTemplateByName(definitionName);
	}
}
