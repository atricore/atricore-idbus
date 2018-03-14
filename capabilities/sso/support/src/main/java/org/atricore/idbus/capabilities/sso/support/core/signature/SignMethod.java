package org.atricore.idbus.capabilities.sso.support.core.signature;

import org.jcp.xml.dsig.internal.dom.DOMSignatureMethod;

/**
 * Created by sgonzalez.
 */
public enum SignMethod {

    SHA1_WITH_DSA("SHA1", "DSA", "http://www.w3.org/2000/09/xmldsig#dsa-sha1"),

    SHA1_WITH_RSA("SHA1", "RSA", "http://www.w3.org/2000/09/xmldsig#rsa-sha1"),

    SHA256_WITH_RSA("SHA256", "RSA", "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256"),

    SHA384_WITH_RSA("SHA384", "RSA", "http://www.w3.org/2001/04/xmldsig-more#rsa-sha384"),

    SHA512_WITH_RSA("SHA512", "RSA", "http://www.w3.org/2001/04/xmldsig-more#rsa-sha512"),

    SHA1_WITH_ECDSA("SHA1", "ECDSA", "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1"),

    SHA256_WITH_ECDSA("SHA256", "ECDSA", "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256"),

    SHA384_WITH_ECDSA("SHA384", "ECDSA", "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha384"),

    SHA512_WITH_ECDSA("SHA512", "ECDSA", "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha512"),

    ;

    private String algorithm;

    private String digest;

    private String spec;

    private String name;

    SignMethod(String digest, String algorithm, String spec) {
        this.digest = digest;
        this.algorithm = algorithm;
        this.spec = spec;

        this.name = digest + "with" + algorithm;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public String getDigest() {
        return digest;
    }

    public String getName() {
        return name;
    }

    public String getSpec() {
        return spec;
    }

    public static SignMethod fromValues(String digest, String algorithm) {
        String name = digest + "with" + algorithm;

        for (SignMethod m : SignMethod.values()) {
            if (m.getName().equals(name))
                return m;
        }

        throw new IllegalArgumentException("Invalid digest/algorithm " + digest + "/" + algorithm);

    }

    public static SignMethod fromSpec(String spec) {
        for (SignMethod m : SignMethod.values()) {
            if (m.getSpec().equals(spec))
                return m;
        }
        throw new IllegalArgumentException("Invalid digest/algorithm " + spec);
    }
}
