
package com.station.project.infrastructure.config;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

@Component
public class MqttSSLConfig {

    public javax.net.ssl.SSLSocketFactory
    createSocketFactory(Resource caResource) throws Exception {

        CertificateFactory certificateFactory =
                CertificateFactory.getInstance("X.509");

        X509Certificate caCertificate;

        try (InputStream input =
                     caResource.getInputStream()) {

            caCertificate = (X509Certificate)
                    certificateFactory.generateCertificate(input);
        }

        KeyStore keyStore =
                KeyStore.getInstance(KeyStore.getDefaultType());

        keyStore.load(null, null);

        keyStore.setCertificateEntry(
                "emqx-ca",
                caCertificate
        );

        TrustManagerFactory trustManagerFactory =
                TrustManagerFactory.getInstance(
                        TrustManagerFactory.getDefaultAlgorithm()
                );

        trustManagerFactory.init(keyStore);

        SSLContext sslContext =
                SSLContext.getInstance("TLS");

        sslContext.init(
                null,
                trustManagerFactory.getTrustManagers(),
                new SecureRandom()
        );

        return sslContext.getSocketFactory();
    }
}
