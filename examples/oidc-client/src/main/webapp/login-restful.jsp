<%@ page import="java.net.URI" %>
<%@ page import="java.util.Properties" %>
<%@ page import="java.io.InputStream" %>
<%@ page import="java.net.HttpURLConnection" %>
<%@ page import="java.io.ByteArrayOutputStream" %>
<%@ page import="com.nimbusds.oauth2.sdk.auth.ClientSecretJWT" %>
<%@ page import="com.nimbusds.jwt.SignedJWT" %>
<%@ page import="com.nimbusds.oauth2.sdk.*" %>
<%@ page import="com.nimbusds.jwt.JWT" %>
<%@ page import="com.nimbusds.oauth2.sdk.auth.ClientAuthentication" %>
<%@ page import="com.nimbusds.jose.crypto.MACSigner" %>
<%@ page import="com.nimbusds.jwt.JWTClaimsSet" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="javax.crypto.spec.SecretKeySpec" %>
<%@ page import="java.security.MessageDigest" %>
<%@ page import="com.nimbusds.jose.crypto.DirectEncrypter" %>
<%@ page import="com.nimbusds.jose.*" %>
<%@ page import="com.nimbusds.jwt.EncryptedJWT" %>
<%@ page import="com.nimbusds.jose.crypto.AESEncrypter" %>
<%@ page import="org.apache.commons.codec.binary.Base64" %>
<%@ page import="java.security.SecureRandom" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<%

    String outcome = "N/A";
    String error = null;

    try {
        Properties props = new Properties();
        InputStream is = getClass().getResourceAsStream("/oidc.properties");
        props.load(is);

        // use SHA-1 to generate a hash from your key and trim the result to 256 bit (32 bytes)
        byte[] key = props.getProperty("oidc.client.secret").getBytes("UTF-8");

        if (key.length != 32) {
            // We need a 32 byte length key, so  ...
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 32);
        }
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");

        URI tokenEndpoint = new URI(props.getProperty("oidc.token.endpoint"));

        // Client Authentication
        ClientAuthentication clientAuth = null;
        {

            byte[] n = new byte[64];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(n);
            String jid = Base64.encodeBase64URLSafeString(n);

            JWSSigner signer = new MACSigner(secretKey.getEncoded());

            // Prepare JWT with claims set
            JWTClaimsSet claimsSet = new JWTClaimsSet();
            claimsSet.setSubject(props.getProperty("oidc.client.id"));
            claimsSet.setIssuer(props.getProperty("oidc.client.id"));
            claimsSet.setIssueTime(new Date());
            claimsSet.setExpirationTime(new Date(System.currentTimeMillis() + (5L * 60L * 1000L)));
            claimsSet.setJWTID(jid);
            claimsSet.setAudience(Arrays.asList(props.getProperty("oidc.client.audience")));

            SignedJWT clientAssertion = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
            clientAssertion.sign(signer);

            // TODO : Set encrypted token as client authentication
            clientAuth = new ClientSecretJWT(clientAssertion);
        }

        JWT assertion = null;
        {

            byte[] n = new byte[64];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(n);
            String jid = Base64.encodeBase64URLSafeString(n);

            // Create HMAC signer
            JWSSigner signer = new MACSigner(secretKey.getEncoded());

            // Prepare JWT with claims set
            JWTClaimsSet claimsSet = new JWTClaimsSet();
            claimsSet.setSubject("user1");
            claimsSet.setIssuer(props.getProperty("oidc.client.id"));
            claimsSet.setAudience(Arrays.asList(props.getProperty("oidc.client.audience")));
            claimsSet.setJWTID(jid);
            claimsSet.setExpirationTime(new Date(System.currentTimeMillis() + (5L * 60L * 1000L)));
            claimsSet.setIssueTime(new Date());
            claimsSet.setClaim("cred", "user1pwd");

            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);

            // Apply the HMAC
            signedJWT.sign(signer);

            // Create JWE object with signed JWT as payload
            JWEObject jweObject = new JWEObject(
                    new JWEHeader.Builder(JWEAlgorithm.DIR, EncryptionMethod.A256GCM)
                            .contentType("JWT") // required to signal nested JWT
                            .build(),
                    new Payload(signedJWT));

            // Perform encryption
            jweObject.encrypt(new DirectEncrypter(secretKey.getEncoded()));

            // Serialise to JWE compact form
            String jweString = jweObject.serialize();

            // Create the encrypted JWT object
            assertion = EncryptedJWT.parse(jweString);
        }

        // Authorization Grant
        AuthorizationGrant authzGrant = new JWTBearerGrant(assertion);

        // Scopes
        Scope scope = Scope.parse(props.getProperty("oidc.client.scopes"));

        TokenRequest tokenRequest = new TokenRequest(tokenEndpoint, clientAuth, authzGrant, scope);

        // Issue request and read response
        HttpURLConnection c = tokenRequest.toHTTPRequest().toHttpURLConnection();
        InputStream in = c.getInputStream();
        byte[] buf = new byte[2048];

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int r = in.read(buf);
        while (r > 0) {
            baos.write(buf, 0, r);
            r = in.read();
        }

        in.close();
        outcome = baos.toString();
        baos.close();

//    } catch (Exception e) {
//        error = e.getMessage();
    } finally {
        //
    }


%>

<html>
<head>
    <title>ODIC Client Test - JWT Bearer with Password </title>
</head>

<h2>Outcome</h2>
<%=outcome%>
<br><br>
<h3>Errors:</h3>
<% if (error != null) out.println(error); %>
<br>
</html>
