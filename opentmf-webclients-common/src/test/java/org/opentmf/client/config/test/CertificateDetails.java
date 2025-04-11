package org.opentmf.client.config.test;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CertificateDetails { 
  private PrivateKey privateKey;
  private X509Certificate x509Certificate;
}