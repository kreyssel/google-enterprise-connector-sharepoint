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
import com.google.enterprise.connector.sharepoint.client.SPConstants;
import com.google.enterprise.connector.sharepoint.client.SharepointClientContext;
import com.google.enterprise.connector.sharepoint.client.SPConstants.FeedType;
import com.google.enterprise.connector.sharepoint.client.SPConstants.SPType;
import com.google.enterprise.connector.spi.Property;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.SpiConstants;

import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.methods.GetMethod;

import com.sun.jndi.toolkit.url.UrlUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import junit.framework.TestCase;

public class SPDocumentTest extends TestCase {

  SPDocument doc;

  protected void setUp() throws Exception {
    super.setUp();
    SharepointClientContext spContext = TestConfiguration.initContext();
    List<SPDocument> allDocs = TestConfiguration.initState(spContext).lookupList(TestConfiguration.Site1_URL, TestConfiguration.Site1_List1_GUID).getCrawlQueue();
    this.doc = allDocs.get(0);
    this.doc.setSharepointClientContext(spContext);
    this.doc.setContentDwnldURL(doc.getUrl());
    try {
      String str = UrlUtil.encode(doc.getUrl(), "UTF-8");
      String charset = new GetMethod(str).getParams().getUriCharset();
      URI uri = new URI(doc.getUrl(), true, charset);
      System.out.println(str);
      System.out.println(uri.toString());
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  public void testCompare() {
    final SPDocument tmpdoc1 = new SPDocument("1", "HTTP://MYCOMP.COM",
        Calendar.getInstance(), SPConstants.NO_AUTHOR, SPConstants.NO_OBJTYPE,
        SPConstants.PARENT_WEB_TITLE, FeedType.CONTENT_FEED, SPType.SP2007);
    final SPDocument tmpdoc2 = new SPDocument("2", "HTTP://MYCOMP.COM",
        Calendar.getInstance(), SPConstants.NO_AUTHOR, SPConstants.NO_OBJTYPE,
        SPConstants.PARENT_WEB_TITLE, FeedType.CONTENT_FEED, SPType.SP2007);
    final List<SPDocument> testSet = new ArrayList<SPDocument>();
    testSet.add(tmpdoc1);
    testSet.add(tmpdoc2);
    Collections.sort(testSet);
    System.out.println(testSet);
  }

  public final void testFindProperty() {
    System.out.println("Testing findProperty()..");
    try {
      final Property prop = this.doc.findProperty(SpiConstants.PROPNAME_DOCID);
      assertNotNull(prop);
      System.out.println("[ findProperty(() ] Test passd");
    } catch (final Exception e) {
      System.out.println("[ findProperty(() ] Test failed");
    }
  }

  public final void testDownloadContents() {
    System.out.println("Testing downloadContents()..");
    try {
      final String responseCode = this.doc.downloadContents();
      assertEquals(responseCode, SPConstants.CONNECTIVITY_SUCCESS);
    } catch (final Exception e) {
      assertTrue(false);
    }
    System.out.println("[ downloadContents(() ] Test passd");
  }

  public void testGetPropertyNamesWithoutExcludedMetadata() {
    Set<String> documentMetadata;
    try {
      documentMetadata = this.doc.getPropertyNames();
      assertTrue(documentMetadata.contains(SPConstants.PARENT_WEB_TITLE));
    } catch (RepositoryException e) {
      fail("Could not get property names for document");
    }
  }

  public void testGetPropertyNamesWithExcludedMetadata() {
    this.doc.getSharepointClientContext().getExcluded_metadata().add(Pattern.compile(".*title$"));
    try {
      Set<String> documentMetadata = this.doc.getPropertyNames();
      assertFalse(documentMetadata.contains(SPConstants.PARENT_WEB_TITLE));
    } catch (RepositoryException e) {
      fail("Could not get property names for document");
    }
  }
}
