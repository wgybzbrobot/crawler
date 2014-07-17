/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zxsoft.crawler.protocol;

import org.jsoup.nodes.Document;

/**
 * Simple aggregate to pass from protocol plugins both content and protocol
 * status.
 * 
 */
public class ProtocolOutput {
	private Document document;
	private ProtocolStatus status;
	private long fetchtime;
	
	public ProtocolOutput(Document document, ProtocolStatus status) {
		this.document = document;
		this.status = status;
		this.fetchtime = System.currentTimeMillis();
	}

	public ProtocolOutput(Document document) {
		this.document = document;
		this.status = ProtocolStatusUtils.STATUS_SUCCESS;
		this.fetchtime = System.currentTimeMillis();
	}

	public long getFetchTime() {
		return fetchtime;
	}
	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public ProtocolStatus getStatus() {
		return status;
	}

	public void setStatus(ProtocolStatus status) {
		this.status = status;
	}

}
