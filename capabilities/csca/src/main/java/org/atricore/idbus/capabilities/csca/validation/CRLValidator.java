package org.atricore.idbus.capabilities.csca.validation;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.*;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.DERIA5String;

import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.springframework.beans.factory.InitializingBean;

public final class CRLValidator implements X509CertificateValidator, InitializingBean {

    private static final Log logger = LogFactory.getLog(CRLValidator.class);

    /**
     * Comma separated list of CRL urls
     */
    protected String crls;

    protected List<X509CRL> x509CRLs = new ArrayList<X509CRL>();

    protected long lastRefresh = 0;

    // Default refresh interval set to 24 hours.
    protected long refreshInterval = 24L * 60L * 60L * 1000L;

    @Override
    public void validate(X509Certificate certificate) throws X509CertificateValidationException {
        if (crls != null) {
            checkCrls();
            verifyCertificateCRLs(certificate);
        } else {
            verifyCertificateCRLsWithDistributionPoints(certificate);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        checkCrls();
    }

    /**
     * This will only download CRLs once a day.
     *
     * @throws X509CertificateValidationException
     */
    protected void checkCrls() throws X509CertificateValidationException {

        if (lastRefresh + refreshInterval > System.currentTimeMillis())
            return;

        logger.debug("Refreshing CRLs");
        x509CRLs = downloadCRLs();

        lastRefresh = System.currentTimeMillis();

    }

    public List<X509CRL> downloadCRLs() throws X509CertificateValidationException {
        List<X509CRL> newCrls = new ArrayList<X509CRL>();
        StringTokenizer st = new StringTokenizer(crls, ",", false);
        while (st.hasMoreTokens()) {
            String crl = st.nextToken();
            newCrls.add(downloadCRL(crl));
        }
        return newCrls;

    }

    /**
     * Extracts the CRL distribution points from the certificate (if available)
     * and checks the certificate revocation status against the CRLs coming from
     * the distribution points. Supports HTTP, HTTPS, FILE, FTP and LDAP based URLs.
     *
     * @param cert
     *            the certificate to be checked for revocation
     * @throws X509CertificateValidationException
     *             if the certificate is revoked
     */
    public void verifyCertificateCRLsWithDistributionPoints(X509Certificate cert) throws X509CertificateValidationException {
        try {
            List<String> crlDistPoints = getCrlDistributionPoints(cert);
            for (String crlDP : crlDistPoints) {
                X509CRL crl = downloadCRL(crlDP);
                if (crl.isRevoked(cert)) {
                    throw new X509CertificateValidationException(
                            "The certificate is revoked by CRL: " + crlDP);
                }
            }
        } catch (Exception ex) {
            if (ex instanceof X509CertificateValidationException) {
                throw (X509CertificateValidationException) ex;
            } else {
                throw new X509CertificateValidationException(
                        "Can not verify CRL for certificate: "
                                + cert.getSubjectX500Principal());
            }
        }
    }

    /**
     * Checks the certificate revocation status against the configured CRLs
     * Supports HTTP, HTTPS, FILE, FTP and LDAP based URLs.
     *
     * @param cert
     *            the certificate to be checked for revocation
     * @throws X509CertificateValidationException
     *             if the certificate is revoked
     */    public void verifyCertificateCRLs(X509Certificate cert) throws X509CertificateValidationException {
        try {


            for (X509CRL crl : x509CRLs) {
                if (crl.isRevoked(cert)) {
                    throw new X509CertificateValidationException(
                            "The certificate " + cert.getSubjectX500Principal() + " is revoked by CRL: " + crl.getIssuerDN());
                }
            }
        } catch (Exception ex) {
            if (ex instanceof X509CertificateValidationException) {
                throw (X509CertificateValidationException) ex;
            } else {
                throw new X509CertificateValidationException(
                        "Can not verify CRL for certificate: "
                                + cert.getSubjectX500Principal());
            }
        }
    }

    /**
     * Downloads CRL from given URL. Supports http, https, ftp and ldap based
     * URLs.
     */
    protected X509CRL downloadCRL(String crlURL) throws X509CertificateValidationException {

        try {
            if (crlURL.startsWith("http://") || crlURL.startsWith("https://")
                    || crlURL.startsWith("ftp://")) {
                return downloadCRLFromWeb(crlURL);
            } else if (crlURL.startsWith("ldap://")) {
                return downloadCRLFromLDAP(crlURL);
            } else if (crlURL.startsWith("file://")) {
                return downloadCRLFromFile(crlURL);
            } else {
                throw new X509CertificateValidationException(
                        "Can not download CRL from certificate "
                                + "distribution point: " + crlURL);
            }
        } catch (Exception e) {
            logger.error("Cannot download CRL [" + crlURL + "] " + e.getMessage(), e);
            throw new X509CertificateValidationException(e);
        }
    }

    /**
     * Downloads a CRL from given LDAP url, e.g.
     * ldap://ldap.infonotary.com/dc=identity-ca,dc=infonotary,dc=com
     */
    protected static X509CRL downloadCRLFromLDAP(String ldapURL) throws CertificateException,
            NamingException, CRLException,
            X509CertificateValidationException {
        Map<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY,
                "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, ldapURL);

        DirContext ctx = new InitialDirContext((Hashtable)env);
        Attributes avals = ctx.getAttributes("");
        Attribute aval = avals.get("certificateRevocationList;binary");
        byte[] val = (byte[]) aval.get();
        if ((val == null) || (val.length == 0)) {
            throw new X509CertificateValidationException(
                    "Can not download CRL from: " + ldapURL);
        } else {
            InputStream inStream = new ByteArrayInputStream(val);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            return (X509CRL) cf.generateCRL(inStream);
        }
    }

    /**
     * Downloads a CRL from given HTTP/HTTPS/FTP URL, e.g.
     * http://crl.infonotary.com/crl/identity-ca.crl
     */
    protected X509CRL downloadCRLFromWeb(String crlURL) throws MalformedURLException,
            IOException, CertificateException,
            CRLException {
        URL url = new URL(crlURL);
        InputStream crlStream = url.openStream();
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            return (X509CRL) cf.generateCRL(crlStream);
        } finally {
            crlStream.close();
        }
    }

    protected X509CRL downloadCRLFromFile(String crlURL) throws MalformedURLException,
            IOException, CertificateException,
            CRLException {
        URL url = new URL(crlURL);
        File crlFile = new File(url.getPath());


        InputStream crlStream = new FileInputStream(crlFile);
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            return (X509CRL) cf.generateCRL(crlStream);
        } finally {
            crlStream.close();
        }
    }

    /**
     * Extracts all CRL distribution point URLs from the
     * "CRL Distribution Point" extension in a X.509 certificate. If CRL
     * distribution point extension is unavailable, returns an empty list.
     */
    public List<String> getCrlDistributionPoints(X509Certificate cert) throws CertificateParsingException, IOException {
        byte[] crldpExt = cert
                .getExtensionValue(X509Extensions.CRLDistributionPoints.getId());
        if (crldpExt == null) {
            return new ArrayList<String>();
        }
        ASN1InputStream oAsnInStream = new ASN1InputStream(
                new ByteArrayInputStream(crldpExt));
        ASN1Object derObjCrlDP = oAsnInStream.readObject();
        DEROctetString dosCrlDP = (DEROctetString) derObjCrlDP;
        byte[] crldpExtOctets = dosCrlDP.getOctets();
        ASN1InputStream oAsnInStream2 = new ASN1InputStream(
                new ByteArrayInputStream(crldpExtOctets));
        ASN1Object derObj2 = oAsnInStream2.readObject();
        CRLDistPoint distPoint = CRLDistPoint.getInstance(derObj2);
        List<String> crlUrls = new ArrayList<String>();
        for (DistributionPoint dp : distPoint.getDistributionPoints()) {
            DistributionPointName dpn = dp.getDistributionPoint();
            // Look for URIs in fullName
            if (dpn != null
                    && dpn.getType() == DistributionPointName.FULL_NAME) {
                GeneralName[] genNames = GeneralNames.getInstance(
                        dpn.getName()).getNames();
                // Look for an URI
                for (int j = 0; j < genNames.length; j++) {
                    if (genNames[j].getTagNo() == GeneralName.uniformResourceIdentifier) {
                        String url = DERIA5String.getInstance(
                                genNames[j].getName()).getString();
                        crlUrls.add(url);
                    }
                }
            }
        }
        return crlUrls;
    }

    public String getCrls() {
        return crls;
    }

    public void setCrls(String crls) {
        this.crls = crls;
    }

    public long getLastRefresh() {
        return lastRefresh;
    }

    public void setLastRefresh(long lastRefresh) {
        this.lastRefresh = lastRefresh;
    }

    public long getRefreshInterval() {
        return refreshInterval;
    }

    public void setRefreshInterval(long refreshInterval) {
        this.refreshInterval = refreshInterval;
    }
}