package com.pia.client.util;

import com.pia.client.common.model.BaseClientProperties;
import com.pia.client.config.test.CertificateDetails;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.tls.HandshakeCertificates;
import okhttp3.tls.HeldCertificate;
import org.springframework.util.ResourceUtils;

/**
 * @author Cezmi Aslan
 */
public class CertificateUtil {

  protected static final String CERT_PATH = "src/test/resources/mTls/cert.jks";
  protected static final String JKS_KEY_ENTRY_NAME = "certificate";

  public static CertificateDetails getCertificateDetails(String jksPath, String jksPassword) {
    try {
      KeyStore keyStore = KeyStore.getInstance("JKS");
      // Provide location of Java Keystore and password for access
      keyStore.load(new FileInputStream(jksPath), jksPassword.toCharArray());

      String alias = findAlias(keyStore);
      KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(alias,
          new KeyStore.PasswordProtection(jksPassword.toCharArray()));
      PrivateKey myPrivateKey = pkEntry.getPrivateKey();
      // Load certificate chain
      Certificate[] chain = keyStore.getCertificateChain(alias);
      CertificateDetails certDetails = new CertificateDetails();
      certDetails.setPrivateKey(myPrivateKey);
      certDetails.setX509Certificate((X509Certificate) chain[0]);
      return certDetails;
    } catch (Exception e) {
      throw new IllegalArgumentException("invalid certificate:" + e.getMessage(), e);
    }
  }

  private static String findAlias(KeyStore keyStore) throws KeyStoreException {
    // iterate over all aliases
    boolean isAliasWithPrivateKey = false;
    Enumeration<String> es = keyStore.aliases();
    String alias = "";
    while (es.hasMoreElements()) {
      alias = es.nextElement();
      // if alias refers to a private key break at that point
      // as we want to use that certificate
      if (keyStore.isKeyEntry(alias)) {
        isAliasWithPrivateKey = true;
        break;
      }
    }
    if (!isAliasWithPrivateKey) {
      throw new IllegalArgumentException("Invalid Certificate. Could not find Key Entry in Keystore with Private key.");
    }
    return alias;
  }

  public static MockWebServer setupMtlsMockServer(BaseClientProperties clientProperties)
      throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException {
    var mockServer = new MockWebServer();

    KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
    keyStore.load(new FileInputStream(ResourceUtils.getFile(CERT_PATH)),
        clientProperties.getCertificates().getKeyStore().getPassword().toCharArray());
    var deviceCertificate = keyStore.getCertificate(JKS_KEY_ENTRY_NAME); // as X509Certificate
    var publicKey = deviceCertificate.getPublicKey();
    var privateKey = (PrivateKey) keyStore.getKey(JKS_KEY_ENTRY_NAME,
        clientProperties.getCertificates().getKeyStore().getPassword().toCharArray());
    var keyPair = new KeyPair(publicKey, privateKey);
    var cetDetails =
        CertificateUtil.getCertificateDetails(
            CERT_PATH,
            clientProperties.getCertificates().getKeyStore().getPassword());
    var heldCertificate = new HeldCertificate(keyPair, cetDetails.getX509Certificate());
    var serverCertificates =
        new HandshakeCertificates.Builder().heldCertificate(heldCertificate).build();
    mockServer.useHttps(serverCertificates.sslSocketFactory(), false);
    return mockServer;
  }
}
