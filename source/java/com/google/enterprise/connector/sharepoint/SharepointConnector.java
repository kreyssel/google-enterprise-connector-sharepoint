// Copyright 2007 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.enterprise.connector.sharepoint;

import com.google.enterprise.connector.sharepoint.client.SharepointClientContext;
import com.google.enterprise.connector.spi.Connector;
import com.google.enterprise.connector.spi.LoginException;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.Session;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * 
 * Implementation of the Connector interface from the spi.
 * It implements the login method and is used by the Spring instantiator.
 *
 */
public class SharepointConnector implements Connector {
  
  private final SharepointClientContext sharepointClientContext;
  
  public SharepointConnector(String sharepointUrl, String domain, 
                             String username, String password) {
    
    sharepointClientContext = new SharepointClientContext(sharepointUrl, 
        domain, username, password);
    
  }
  
  public void setDomain(String domain) {
    sharepointClientContext.setDomain(domain);
  }
  
  public void setHost(String host) {
    sharepointClientContext.setHost(host);
  }
  
  public void setPort(int port) {
    sharepointClientContext.setPort(port);
  }
  
  public void setSharepointUrl(String sharepointUrl) {
    try {
      URL url = new URL(sharepointUrl);
      sharepointClientContext.setHost(url.getHost());
      if (-1 != url.getPort()) {
        sharepointClientContext.setPort(url.getPort());
      }
      sharepointClientContext.setsiteName(url.getPath());
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
  }
  
  public void setUsername(String username) {
    sharepointClientContext.setUsername(username);
  }
  
  public void setPassword(String password) {
    sharepointClientContext.setPassword(password);
  }  
  
  public Session login() throws LoginException, RepositoryException {
    
    return new SharepointSession(this, sharepointClientContext);
  }
}
