/*
 * Copyright (C) 2018 INFORMATION SERVICES INTERNATIONAL - DENTSU, LTD. All Rights Reserved.
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

package org.iplass.mtp.entity.definition.listeners;

import java.util.List;

import org.iplass.mtp.entity.definition.EventListenerDefinition;

public class SendNotificationEventListenerDefinition extends EventListenerDefinition {
	private static final long serialVersionUID = 7891113805980486070L;

	private SendNotificationType notificationType;
	private String tmplDefName;
	private String notificationCondScript;
	private List<EventType> listenEvent;
	
	private boolean sendTogether;
	/**ウェッブフックだけの設定項目*/
	private boolean synchronous;
	private List<String> endPointDefList;
	private String resultHandler;

	public SendNotificationType getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(SendNotificationType notificationType) {
		this.notificationType = notificationType;
	}

	public String getTmplDefName() {
		return tmplDefName;
	}

	public void setTmplDefName(String tmplDefName) {
		this.tmplDefName = tmplDefName;
	}

	public String getNotificationCondScript() {
		return notificationCondScript;
	}

	public void setNotificationCondScript(String notificationCondScript) {
		this.notificationCondScript = notificationCondScript;
	}

	public List<EventType> getListenEvent() {
		return listenEvent;
	}

	public void setListenEvent(List<EventType> listenEvent) {
		this.listenEvent = listenEvent;
	}

	public boolean isSynchronous() {
		return synchronous;
	}

	public void setSynchronous(boolean isSynchronous) {
		this.synchronous = isSynchronous;
	}

	public List<String> getEndPointDefList() {
		return endPointDefList;
	}

	public void setEndPointDefList(List<String> endPointDefList) {
		this.endPointDefList = endPointDefList;
	}

	public String getResultHandler() {
		return resultHandler;
	}

	public void setResultHandler(String resultHandler) {
		this.resultHandler = resultHandler;
	}

	public boolean isSendTogether() {
		return sendTogether;
	}

	public void setSendTogether(boolean isSendTogether) {
		sendTogether = isSendTogether;
	}
}
