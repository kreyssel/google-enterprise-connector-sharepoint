//Copyright 2007 Google Inc.

//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at

//http://www.apache.org/licenses/LICENSE-2.0

//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.

package com.google.enterprise.connector.sharepoint.spiimpl;

import com.google.enterprise.connector.sharepoint.TestConfiguration;
import com.google.enterprise.connector.sharepoint.client.SharepointClientContext;
import com.google.enterprise.connector.sharepoint.spiimpl.SharepointConnector;

import junit.framework.TestCase;

public class SharePointConnectorTest extends TestCase {

	SharepointClientContext sharepointClientContext;
	SharepointConnector connector;
		
	protected void setUp() throws Exception {
		System.out.println("\n...Setting Up...");
		System.out.println("Initializing SharepointClientContext ...");
		this.sharepointClientContext = new SharepointClientContext(TestConfiguration.sharepointUrl, TestConfiguration.domain, 
				  TestConfiguration.username, TestConfiguration.Password, TestConfiguration.googleConnectorWorkDir, 
				  TestConfiguration.includedURls, TestConfiguration.excludedURls, TestConfiguration.mySiteBaseURL, 
				  TestConfiguration.AliasMap, TestConfiguration.feedType);		
		assertNotNull(this.sharepointClientContext);
		this.connector.setIncluded_metadata(TestConfiguration.whiteList);
		this.connector.setExcluded_metadata(TestConfiguration.blackList);		
		
		System.out.println("initializing SharepointConnector ...");
		this.connector = new SharepointConnector(TestConfiguration.sharepointUrl, TestConfiguration.domain, 
				TestConfiguration.username, TestConfiguration.Password, TestConfiguration.googleConnectorWorkDir, TestConfiguration.includedURls, TestConfiguration.excludedURls, TestConfiguration.mySiteBaseURL, TestConfiguration.AliasMap,TestConfiguration.feedType);
		this.connector.setFQDNConversion(TestConfiguration.FQDNflag);					
	}
	
	// The class under test contains only getters/setters
}
