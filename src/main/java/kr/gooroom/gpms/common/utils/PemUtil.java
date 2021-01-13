package kr.gooroom.gpms.common.utils;

import java.io.IOException;
import java.io.StringReader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

import org.bouncycastle.openssl.PEMParser;

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
		} catch (IOException e) {
			if(pemParser != null) {
				try {
					pemParser.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			if(pemParser != null) {
				try {
					pemParser.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
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
			return X509Certificate.getInstance(certificatePem.getBytes()).getPublicKey();
		} catch (CertificateException e) {
			e.printStackTrace();
		}
		return null;
	}
}
