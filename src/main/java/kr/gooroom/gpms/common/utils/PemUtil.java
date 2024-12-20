package kr.gooroom.gpms.common.utils;

import org.bouncycastle.openssl.PEMParser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

public final class PemUtil {

	private PemUtil() {
	}

	public static PrivateKey getPrivateKey(String privateKeyPem) {
		PEMParser pemParser = null;
		try {
			pemParser = new PEMParser(new StringReader(privateKeyPem));
			PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(pemParser.readPemObject().getContent());
			//KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			
			pemParser.close();
			return KeyFactory.getInstance("RSA").generatePrivate(pkcs8EncodedKeySpec);
		} catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
			if(pemParser != null) {
				try {
					pemParser.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			e.printStackTrace();
		}
		return null;
	}

	public static PublicKey getPublicKey(String certificatePem) {
		try {
			X509Certificate cert = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(certificatePem.getBytes(StandardCharsets.UTF_8)));
			return  cert.getPublicKey();
		} catch (CertificateException e) {
			e.printStackTrace();
		}
		return null;
	}
}
