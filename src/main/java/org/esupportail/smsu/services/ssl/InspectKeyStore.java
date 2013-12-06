package org.esupportail.smsu.services.ssl;

import java.io.FileInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;


public class InspectKeyStore
{
	
    private final Logger logger = new LoggerImpl(getClass());

    public Error internalError(Exception e) {
	return new Error("internal error", e);
    }

    public File checkFileReadable(File f, String context) {
	try {
	    new FileInputStream(f);
	    return f;
	} catch (java.io.FileNotFoundException e) {
	    logger.fatal(context + " error: " + e.getMessage());
	    return null;
	}
    }
		
    public ArrayList<String> getKeyStoreAliases(KeyStore ks) {
	try {
	    return Collections.list(ks.aliases());
	} catch (KeyStoreException e) { throw internalError(e); }
    }

    public boolean isPrivateKeyEntry(KeyStore ks, String alias) {
	try {
	    return !ks.isCertificateEntry(alias);
	} catch (KeyStoreException e) { throw internalError(e); }
    }
    public Certificate getCertificate(KeyStore ks, String alias) {
	try {
	    return ks.getCertificate(alias);
	} catch (KeyStoreException e) { throw internalError(e); }
    }
    public Certificate[] getCertificateChain(KeyStore ks, String alias) {
	try {
	    return ks.getCertificateChain(alias);
	} catch (KeyStoreException e) { throw internalError(e); }
    }

    public String getIssuerDN(Certificate cert) {
	return ((X509Certificate)cert).getIssuerDN().getName();
    }
    public String getSubjectDN(Certificate cert) {
	return ((X509Certificate)cert).getSubjectDN().getName();
    }

    public KeyStore loadKeystore(File file, String password, String type) {
	try {
	    KeyStore ks = KeyStore.getInstance(type == null ? KeyStore.getDefaultType() : type);
	    FileInputStream fi = new FileInputStream(file);
	    ks.load(fi, password.toCharArray());
	    fi.close();
	    return ks;
	} catch (java.security.NoSuchAlgorithmException e) {
	    throw new Error("should never happen", e);
	} catch (java.security.cert.CertificateException e) {
	    throw new Error(e);
	} catch (KeyStoreException e) {
	    throw new Error(e);
	} catch (java.io.IOException e) {
	    throw new Error(e);
	}
    }


    public File getTrustStoreFile() {
	String s = System.getProperty("javax.net.ssl.trustStore");

	if (s != null) return new File(s);

	// Fallback to JSEE standard files
	File securityDir = new File(new File(System.getProperty("java.home"), "lib"), "security");
	File f = new File(securityDir, "jssecacerts");
	if (!f.exists()) 
	    f = new File(securityDir, "cacerts");
	return checkFileReadable(f, "javax.net.ssl.trustStore");
    }

    public File getKeyStoreFile() {
	String s = System.getProperty("javax.net.ssl.keyStore");
	if (s == null) {
	    logger.fatal("javax.net.ssl.keyStore is not set");
	    return null;
	}
	return checkFileReadable(new File(s), "javax.net.ssl.keyStore");
    }

    public String getKeyStorePassword() {
	String s = System.getProperty("javax.net.ssl.keyStorePassword");
	if (s == null) {
	    logger.fatal("javax.net.ssl.keyStorePassword is not set");
	}
	return s;
    }

    public String getTrustStorePassword() {
	String s = System.getProperty("javax.net.ssl.trustStorePassword");
	if (s == null) {
	    s = "changeit";
	}
	return s;
    }


    public void inspectPrivateKeyStore() {
	File file = getKeyStoreFile();
	String password = getKeyStorePassword();
	if (file != null && password != null)
	    inspectPrivateKeyStore(file, password, System.getProperty("javax.net.ssl.keyStoreType"));
    }

    public void inspectTrustStore() {
	File file = getTrustStoreFile();
	String password = getTrustStorePassword();
	if (file != null && password != null)
	    inspectTrustStore(file, password, System.getProperty("javax.net.ssl.trustStoreType"));
    }

    public void checkPrivateKeyStoreIsConfigured() {
	File file = getKeyStoreFile();
	String password = getKeyStorePassword();
	if (password == null || file == null) throw new Error();
    }

    public void displayCertificate(KeyStore ks, String alias) {
	Certificate cert = getCertificate(ks, alias);
	if (!(cert instanceof X509Certificate)) {
	    logger.info(alias + ": non X509 key");
	    return;
	}
	String subjectDn = getSubjectDN(cert);
	String issuerDn = getIssuerDN(cert);
	logger.info("   subject: " + subjectDn);
	if (!subjectDn.equals(issuerDn))
	    logger.info(" issued by: " + issuerDn);
    }

    public void checkCertificateChain(KeyStore ks, String alias) {
	Certificate cert = getCertificate(ks, alias);
	
	if (!(cert instanceof X509Certificate)) {
	    logger.info(alias + ": non X509 key");
	    return;
	}

	String subjectDn = getSubjectDN(cert);
	String issuerDn = getIssuerDN(cert);
	logger.info("   subject: " + subjectDn);
	logger.info(" issued by: " + issuerDn);

	int i = 0;
	for (Certificate certIssuer : getCertificateChain(ks, alias)) {
	    i++;
	    if (certIssuer instanceof X509Certificate) {
		String subjectDn_ = getSubjectDN(certIssuer);
		String issuerDn_ = getIssuerDN(certIssuer);

		if (i == 1) {
		    if (certIssuer == cert) continue;
		    else logger.error(alias + ": first certificate chain should be the certificate");
		}
		if (!subjectDn_.equals(issuerDn)) {
		    logger.error(subjectDn + " should be the same as " + subjectDn_);
		    logger.info("   subject: " + subjectDn_);
		}
		logger.info(" issued by: " + issuerDn_);
		subjectDn = subjectDn_; issuerDn = issuerDn_;
	    } else logger.info(alias + ": non X509 key");
	}
	if (!subjectDn.equals(issuerDn)) {
	    if (i == 0)
		logger.error("certificate chain missing");
	    else
		logger.error("last certificate in the chain should be self signed");
	}
    }

    public void inspectTrustStore(File file, String password, String type) {
	logger.info("------------ inspecting public certificates " + file + " ----------------------------------------");
	KeyStore ks = loadKeystore(file, password, type);
	for (String alias : getKeyStoreAliases(ks)) {	
	    if (isPrivateKeyEntry(ks, alias)) {
		logger.info("(skipping private key " + alias + ")");
	    } else {
		displayCertificate(ks, alias);
	    }
	}
    }

    public void inspectPrivateKeyStore(File file, String password, String type) {
	logger.info("------------ inspecting private keys " + file + " ----------------------------------------");
	KeyStore ks = loadKeystore(file, password, type);
	for (String alias : getKeyStoreAliases(ks)) {	
	    if (!isPrivateKeyEntry(ks, alias)) {
		logger.info("(skipping public key " + alias + ")");
	    }  else {
		checkCertificateChain(ks, alias);
	    }
	}
    }
}