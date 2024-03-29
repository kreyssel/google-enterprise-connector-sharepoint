/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//httpclient/src/contrib/org/apache/commons/httpclient/contrib/ssl/EasySSLProtocolSocketFactory.java,v 1.7 2004/06/11 19:26:27 olegk Exp $
 * $Revision: 480424 $
 * $Date: 2006-11-29 11:26:49 +0530 (Wed, 29 Nov 2006) $
 *
 * ====================================================================
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apache.commons.httpclient.contrib.ssl;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.HttpClientError;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.SecureProtocolSocketFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.logging.Logger;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * <p>
 * EasySSLProtocolSocketFactory can be used to creats SSL {@link Socket}s that
 * accept self-signed certificates.
 * </p>
 * <p>
 * This socket factory SHOULD NOT be used for productive systems due to security
 * reasons, unless it is a concious decision and you are perfectly aware of
 * security implications of accepting self-signed certificates
 * </p>
 * <p>
 * Example of using custom protocol socket factory for a specific host:
 *
 * <pre>
 * Protocol easyhttps = new Protocol(&quot;https&quot;, new EasySSLProtocolSocketFactory(),
 *     443);
 *
 * HttpClient client = new HttpClient();
 * client.getHostConfiguration().setHost(&quot;localhost&quot;, 443, easyhttps);
 * // use relative url only
 * GetMethod httpget = new GetMethod(&quot;/&quot;);
 * client.executeMethod(httpget);
 * </pre>
 *
 * </p>
 * <p>
 * Example of using custom protocol socket factory per default instead of the
 * standard one:
 *
 * <pre>
 * Protocol easyhttps = new Protocol(&quot;https&quot;, new EasySSLProtocolSocketFactory(),
 *     443);
 * Protocol.registerProtocol(&quot;https&quot;, easyhttps);
 *
 * HttpClient client = new HttpClient();
 * GetMethod httpget = new GetMethod(&quot;https://localhost/&quot;);
 * client.executeMethod(httpget);
 * </pre>
 *
 * </p>
 *
 * @author <a href="mailto:oleg -at- ural.ru">Oleg Kalnichevski</a>
 *         <p>
 *         DISCLAIMER: HttpClient developers DO NOT actively support this
 *         component. The component is provided as a reference material, which
 *         may be inappropriate for use without additional customization.
 *         </p>
 */

public class EasySSLProtocolSocketFactory implements
    SecureProtocolSocketFactory {

  /** Log object for this class. */
  // private static final Log LOG =
  // LogFactory.getLog(EasySSLProtocolSocketFactory.class);
  private static final Logger LOGGER = Logger.getLogger(EasySSLProtocolSocketFactory.class.getName());
  private final String className = EasySSLProtocolSocketFactory.class.getName();
  private SSLContext sslcontext = null;

  /**
   * Constructor for EasySSLProtocolSocketFactory.
   */
  public EasySSLProtocolSocketFactory() {
    super();
  }

  private static SSLContext createEasySSLContext() {
    final String sFunctionName = "createEasySSLContext()";
    LOGGER.entering(EasySSLProtocolSocketFactory.class.getName(), sFunctionName);
    try {
      final SSLContext context = SSLContext.getInstance("SSL");

      // Create a trust manager that does not validate certificate chains
      final TrustManager[] trustmanagers = new TrustManager[] { new X509TrustManager() {
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
          return null;
        }

        public void checkClientTrusted(
            java.security.cert.X509Certificate[] certs, String authType) {
        }

        public void checkServerTrusted(
            java.security.cert.X509Certificate[] certs, String authType) {
        }
      } };

      context.init(null,
      /* new TrustManager[] {new EasyX509TrustManager(null)} */trustmanagers, null);
      LOGGER.exiting(EasySSLProtocolSocketFactory.class.getName(), sFunctionName);
      return context;
    } catch (final Exception e) {
      // LOGGER.severe(e.getMessage(), e);
      LOGGER.severe(e.getMessage());
      throw new HttpClientError(e.toString());
    }
  }

  private SSLContext getSSLContext() {
    final String sFunctionName = "getSSLContext()";
    LOGGER.entering(this.className, sFunctionName);
    if (this.sslcontext == null) {
      this.sslcontext = createEasySSLContext();
    }
    LOGGER.exiting(this.className, sFunctionName);
    return this.sslcontext;
  }

  /**
   * @see SecureProtocolSocketFactory#createSocket(java.lang.String,int,java.net.InetAddress,int)
   */
  public Socket createSocket(final String host, final int port,
      final InetAddress clientHost, final int clientPort) throws IOException {

    return this.getSSLContext().getSocketFactory().createSocket(host, port, clientHost, clientPort);
  }

  /**
   * Attempts to get a new socket connection to the given host within the given
   * time limit.
   * <p>
   * To circumvent the limitations of older JREs that do not support connect
   * timeout a controller thread is executed. The controller thread attempts to
   * create a new socket within the given limit of time. If socket constructor
   * does not return until the timeout expires, the controller terminates and
   * throws an {@link ConnectTimeoutException}
   * </p>
   *
   * @param host the host name/IP
   * @param port the port on the host
   * @param clientHost the local host name/IP to bind the socket to
   * @param clientPort the port on the local machine
   * @param params {@link HttpConnectionParams Http connection parameters}
   * @return Socket a new socket
   * @throws IOException if an I/O error occurs while creating the socket
   */
  public Socket createSocket(final String host, final int port,
      final InetAddress localAddress, final int localPort,
      final HttpConnectionParams params) throws IOException {
    if (params == null) {
      throw new IllegalArgumentException("Parameters may not be null");
    }
    final int timeout = params.getConnectionTimeout();
    final SocketFactory socketfactory = this.getSSLContext().getSocketFactory();
    if (timeout == 0) {
      return socketfactory.createSocket(host, port, localAddress, localPort);
    } else {
      final Socket socket = socketfactory.createSocket();
      final SocketAddress localaddr = new InetSocketAddress(localAddress,
          localPort);
      final SocketAddress remoteaddr = new InetSocketAddress(host, port);
      socket.bind(localaddr);
      socket.connect(remoteaddr, timeout);
      return socket;
    }
  }

  /**
   * @see SecureProtocolSocketFactory#createSocket(java.lang.String,int)
   */
  public Socket createSocket(final String host, final int port)
      throws IOException {
    return this.getSSLContext().getSocketFactory().createSocket(host, port);
  }

  /**
   * @see SecureProtocolSocketFactory#createSocket(java.net.Socket,java.lang.String,int,boolean)
   */
  public Socket createSocket(final Socket socket, final String host,
      final int port, final boolean autoClose) throws IOException {
    return this.getSSLContext().getSocketFactory().createSocket(socket, host, port, autoClose);
  }

  public boolean equals(final Object obj) {
    return ((obj != null) && obj.getClass().equals(EasySSLProtocolSocketFactory.class));
  }

  public int hashCode() {
    return EasySSLProtocolSocketFactory.class.hashCode();
  }

}
