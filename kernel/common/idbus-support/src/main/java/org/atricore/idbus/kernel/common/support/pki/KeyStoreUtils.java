package org.atricore.idbus.kernel.common.support.pki;
import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Date;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509ExtensionUtils;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.bc.BcDigestCalculatorProvider;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

/**
 * Utility class for generating self-signed certificates.
 *
 * @author Mister PKI
 */
public final class KeyStoreUtils {

    private KeyStoreUtils() {

    }

    public static void generateDefaultKeystore(String certCN,
                                               String keyAlias,
                                               String storePassword,
                                               String keyPassword,
                                               OutputStream out) throws PKIException {
        try {

            String certAlias = keyAlias + "-cert";
            String hashAlgorithm = "SHA256withRSA";
            int keySize = 4096;
            int validDays = 3650;

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            generateKeystore(storePassword, baos);
            KeyStore ks = loadKeystore(storePassword, new ByteArrayInputStream(baos.toByteArray()));
            KeyPair keyPair = generateRSA(keySize);
            X509Certificate cert = generate(keyPair, hashAlgorithm, certCN, validDays);
            ks.setCertificateEntry(certAlias, cert);
            ks.setKeyEntry(keyAlias, keyPair.getPrivate(), keyPassword.toCharArray(), new Certificate [] {cert});
            ks.store(out, storePassword.toCharArray());

        } catch (IOException e) {
            throw new PKIException(e);
        } catch (CertificateException e) {
            throw new PKIException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new PKIException(e);
        } catch (KeyStoreException e) {
            throw new PKIException(e);
        } catch (OperatorCreationException e) {
            throw new PKIException(e);
        }

    }

    public static KeyStore loadKeystore(String password, InputStream in) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        char[] p = password.toCharArray();
        ks.load(in, p);
        return ks;
    }

    public static void generateKeystore(String password, OutputStream out) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        char[] p = password.toCharArray();
        ks.load(null, p);
        // Store away the keystore.
        ks.store(out, p);
        out.close();
    }

    /**
     * Generates an RSA key pair.
     *
     * @param keySize
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static KeyPair generateRSA(int keySize) throws NoSuchAlgorithmException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(keySize);
        KeyPair kp = kpg.generateKeyPair();

        return kp;
    }

    /**
     * Generates a self signed certificate using the BouncyCastle lib.
     *
     * @param keyPair used for signing the certificate with PrivateKey
     * @param hashAlgorithm Hash function
     * @param cn Common Name to be used in the subject dn
     * @param days validity period in days of the certificate
     *
     * @return self-signed X509Certificate
     *
     * @throws OperatorCreationException on creating a key id
     * @throws CertIOException on building JcaContentSignerBuilder
     * @throws CertificateException on getting certificate from provider
     */
    public static X509Certificate generate(final KeyPair keyPair,
                                           final String hashAlgorithm,
                                           final String cn,
                                           final int days)
            throws OperatorCreationException, CertificateException, CertIOException
    {
        final Date now = new Date();
        final Date notBefore = new Date(now.getTime());
        final Date notAfter = new Date(now.getTime() + (days * 24L * 60L * 60L * 1000L));

        final ContentSigner contentSigner = new JcaContentSignerBuilder(hashAlgorithm).build(keyPair.getPrivate());
        final X500Name x500Name = new X500Name("CN=" + cn);
        final X509v3CertificateBuilder certificateBuilder =
                new JcaX509v3CertificateBuilder(x500Name,
                        BigInteger.valueOf(now.getTime()),
                        notBefore,
                        notAfter,
                        x500Name,
                        keyPair.getPublic())
                        .addExtension(Extension.subjectKeyIdentifier, false, createSubjectKeyId(keyPair.getPublic()))
                        .addExtension(Extension.authorityKeyIdentifier, false, createAuthorityKeyId(keyPair.getPublic()))
                        .addExtension(Extension.basicConstraints, true, new BasicConstraints(true));

        return new JcaX509CertificateConverter()
                .setProvider(new BouncyCastleProvider()).getCertificate(certificateBuilder.build(contentSigner));
    }

    /**
     * Creates the hash value of the public key.
     *
     * @param publicKey of the certificate
     *
     * @return SubjectKeyIdentifier hash
     *
     * @throws OperatorCreationException
     */
    private static SubjectKeyIdentifier createSubjectKeyId(final PublicKey publicKey) throws OperatorCreationException {
        final SubjectPublicKeyInfo publicKeyInfo = SubjectPublicKeyInfo.getInstance(publicKey.getEncoded());
        final DigestCalculator digCalc =
                new BcDigestCalculatorProvider().get(new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1));

        return new X509ExtensionUtils(digCalc).createSubjectKeyIdentifier(publicKeyInfo);
    }

    /**
     * Creates the hash value of the authority public key.
     *
     * @param publicKey of the authority certificate
     *
     * @return AuthorityKeyIdentifier hash
     *
     * @throws OperatorCreationException
     */
    private static AuthorityKeyIdentifier createAuthorityKeyId(final PublicKey publicKey)
            throws OperatorCreationException
    {
        final SubjectPublicKeyInfo publicKeyInfo = SubjectPublicKeyInfo.getInstance(publicKey.getEncoded());
        final DigestCalculator digCalc =
                new BcDigestCalculatorProvider().get(new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1));

        return new X509ExtensionUtils(digCalc).createAuthorityKeyIdentifier(publicKeyInfo);
    }
}
