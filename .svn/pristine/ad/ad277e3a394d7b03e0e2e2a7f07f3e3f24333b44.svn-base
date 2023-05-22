package com.sm.base.dto;

import com.viettel.passport.PassportWS;
import com.viettel.passport.PassportWSService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import viettel.passport.client.AppToken;
import viettel.passport.client.ObjectToken;
import viettel.passport.client.UserToken;

import javax.net.ssl.*;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.ws.BindingProvider;
import java.io.IOException;
import java.io.StringReader;
import java.net.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Component
@Slf4j
public class VSAValidate
{
  private int connectTimeout = 20000;
  private int responseTimeout = 2000;

  private String casValidateUrl;
  private String user;
  private String password;
  private String domainCode;
  private UserToken userToken;
  private boolean successfulAuthentication;
  private static final int DEFAULT_TIME_OUT_VALUE = 5000;
  private int timeOutVal = 5000;
  private String ip;
  private String reasonFail;

  public void validate()
    throws Exception
  {
    URL url = null;
    try
    {
      trustAllHttpsCertificates();

      URL baseUrl = PassportWSService.class.getResource(".");
      url = new URL(baseUrl, getCasValidateUrl());
      HttpsURLConnection.setDefaultHostnameVerifier(new VpHostnameVerifier());

      PassportWSService pws = new PassportWSService(url, new QName("http://passport.viettel.com/", "passportWSService"));

      PassportWS wsPort = pws.getPassportWSPort();

      BindingProvider bp = (BindingProvider)wsPort;

      bp.getRequestContext().put("com.sun.xml.internal.ws.request.timeout", Integer.valueOf(5000));

      ((BindingProvider)wsPort).getRequestContext().put("com.sun.xml.internal.ws.request.timeout", Integer.valueOf(6000));

      String result = wsPort.validate(this.user, this.password, this.domainCode);

      String entireResponse = new String(result.getBytes("UTF8"));
      if ((entireResponse == null) || (entireResponse.trim().length() == 0) || ("no".equalsIgnoreCase(entireResponse.trim())))
      {
        this.log.info("Authenticate failure for username [" + this.user + "], domainCode [" + this.domainCode + "]");

        this.successfulAuthentication = false;
        this.userToken = null;
        this.reasonFail = "u/p wrong";
        return;
      }

      if ("access_time_invalid".equals(entireResponse)) {
        this.successfulAuthentication = false;
        this.userToken = null;
        this.reasonFail = "access_time_invalid";
        return;
      }

      this.userToken = UserToken.parseXMLResponse(entireResponse, false);
      if ((this.userToken != null) && (this.userToken.getObjectTokens() != null) && (this.userToken.getObjectTokens().size() > 0))
      {
        this.log.info("Authenticate successful for username [" + this.user + "], domainCode [" + this.domainCode + "]");

        this.successfulAuthentication = true;
      }
      else {
        this.log.info("Authenticate failure for username [" + this.user + "], domainCode [" + this.domainCode + "]");

        this.successfulAuthentication = false;
      }
    } catch (MalformedURLException e) {
      this.log.error("Get validate url error.", e);
      throw e;
    } catch (Exception ex) {
      this.log.error("Authenticate user error.", ex);
      throw ex;
    }
  }

  private static void trustAllHttpsCertificates()
    throws Exception
  {
    TrustManager[] trustAllCerts = new TrustManager[1];
    TrustManager tm = new MiTM();
    trustAllCerts[0] = tm;
    SSLContext sc = SSLContext.getInstance("SSL");
    sc.init(null, trustAllCerts, null);

    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
  }

  public void validateIncludeIp()
    throws IOException, ParserConfigurationException
  {
    URL url = null;
    try
    {
      URL baseUrl = PassportWSService.class.getResource(".");
      url = new URL(baseUrl, getCasValidateUrl(), new URLStreamHandler()
      {
        protected URLConnection openConnection(URL url) throws IOException
        {
          URL clone_url = new URL(url.toString());
          HttpURLConnection clone_urlconnection = (HttpURLConnection)clone_url.openConnection();

          clone_urlconnection.setConnectTimeout(VSAValidate.this.connectTimeout);
          clone_urlconnection.setReadTimeout(VSAValidate.this.responseTimeout);
          return clone_urlconnection;
        } } );
    } catch (MalformedURLException e) {
      this.log.error("Get validate url error.", e);
    }

    PassportWSService pws = new PassportWSService(url, new QName("http://passport.viettel.com/", "passportWSService"));

    String entireResponse = pws.getPassportWSPort().validateIncludeIp(this.user, this.password, this.domainCode, this.ip);
    try
    {
      if ((entireResponse == null) || (entireResponse.trim().length() == 0) || ("no".equalsIgnoreCase(entireResponse.trim())))
      {
        this.log.info("Authenticate failure for username [" + this.user + "], domainCode [" + this.domainCode + "]");

        this.successfulAuthentication = false;
        this.userToken = null;
        return;
      }

      if ("access_time_invalid".equals(entireResponse)) {
        this.successfulAuthentication = false;
        this.userToken = null;
        this.reasonFail = "access_time_invalid";
        return;
      }

      this.userToken = UserToken.parseXMLResponse(entireResponse, false);
      if ((this.userToken != null) && (this.userToken.getObjectTokens() != null) && (this.userToken.getObjectTokens().size() > 0))
      {
        this.log.info("Authenticate successful for username [" + this.user + "], domainCode [" + this.domainCode + "]");

        this.successfulAuthentication = true;
      } else {
        this.log.info("Authenticate failure for username [" + this.user + "], domainCode [" + this.domainCode + "]");

        this.successfulAuthentication = false;
      }
    } catch (SAXException ex) {
      this.log.error("Authenticate user error.", ex);
    }
  }

  public ArrayList<AppToken> getAllApp()
    throws Exception
  {
    URL url = null;
    try
    {
      URL baseUrl = PassportWSService.class.getResource(".");
      url = new URL(baseUrl, getCasValidateUrl(), new URLStreamHandler()
      {
        protected URLConnection openConnection(URL url) throws IOException
        {
          URL clone_url = new URL(url.toString());
          HttpURLConnection clone_urlconnection = (HttpURLConnection)clone_url.openConnection();

          clone_urlconnection.setConnectTimeout(VSAValidate.this.connectTimeout);
          clone_urlconnection.setReadTimeout(VSAValidate.this.responseTimeout);
          return clone_urlconnection;
        } } );
    } catch (MalformedURLException e) {
      this.log.error("Get validate url error.", e);
    }

    PassportWSService pws = new PassportWSService(url, new QName("http://passport.viettel.com/", "passportWSService"));

    String entireResponse = pws.getPassportWSPort().getAllowedApp(this.user);

    return AppToken.parseApp(entireResponse);
  }

  public ArrayList<ObjectToken> getAllMenu() throws IOException, ParserConfigurationException, SAXException {
    this.domainCode = this.domainCode.trim().toLowerCase();
    URL url = null;
    try {
      trustAllHttpsCertificates();

      URL baseUrl = PassportWSService.class.getResource(".");
      url = new URL(baseUrl, getCasValidateUrl(), new URLStreamHandler()
      {
        protected URLConnection openConnection(URL url) throws IOException
        {
          URL clone_url = new URL(url.toString());
          HttpURLConnection clone_urlconnection = (HttpURLConnection)clone_url.openConnection();

          clone_urlconnection.setConnectTimeout(VSAValidate.this.connectTimeout);
          clone_urlconnection.setReadTimeout(VSAValidate.this.responseTimeout);
          return clone_urlconnection;
        } } );
    } catch (Exception e) {
      this.log.error("Get validate url error.", e);
    }

    HttpsURLConnection.setDefaultHostnameVerifier(new VpHostnameVerifier());

    PassportWSService pws = new PassportWSService(url, new QName("http://passport.viettel.com/", "passportWSService"));

    String entireResponse = pws.getPassportWSPort().getAppFunctions(this.domainCode);

    DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();

    Document doc = db.parse(new InputSource(new StringReader(entireResponse)));

    ArrayList arrlObjects = new ArrayList();
    NodeList nl = doc.getElementsByTagName("ObjectAll");
    if ((nl != null) && (nl.getLength() > 0)) {
      Element objectEle = (Element)nl.item(0);
      NodeList nlObjects = objectEle.getElementsByTagName("Row");
      if ((nlObjects != null) && (nlObjects.getLength() > 0)) {
        for (int i = 0; i < nlObjects.getLength(); i++) {
          Element el = (Element)nlObjects.item(i);
          ObjectToken mt = ObjectToken.getMenuToken(el);
          arrlObjects.add(mt);
        }
      }
    }
    return arrlObjects;
  }

  public static class VpHostnameVerifier implements HostnameVerifier
  {
    public boolean verify(String urlHostName, SSLSession session) {
      return true;
    }
  }

  public static class MiTM
    implements TrustManager, X509TrustManager
  {
    public X509Certificate[] getAcceptedIssuers()
    {
      return new X509Certificate[0];
    }

    public boolean isServerTrusted(X509Certificate[] certs)
    {
      return true;
    }

    public boolean isClientTrusted(X509Certificate[] certs)
    {
      return true;
    }

    public void checkServerTrusted(X509Certificate[] certs, String authType)
      throws CertificateException
    {
    }

    public void checkClientTrusted(X509Certificate[] certs, String authType)
      throws CertificateException
    {
    }
  }
}