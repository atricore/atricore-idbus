package org.atricore.idbus.capabilities.samlr2.support.test;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.DOMBuilder;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

/**
 * This utility class is used across the various servlets that make up the
 * SAML-based Single Sign-On Reference Tool. It includes various helper methods
 * that are used for the SAML transactions.
 * 
 */
public class Util {

  // used for creating a randomly generated string
  private static Random random = new Random();
  private static final char[] charMapping = {
    'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
    'p'};
  private static final DateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
  static {
    DATE_TIME_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
  }
  private static final int ASSERTION_NOT_BEFORE_MINUTES = -5;
  private static final int ASSERTION_NOT_ON_OR_AFTER_MINUTES = 10;

  /**
   * Returns a String containing the contents of the file located at the
   * specified path.
   * 
   * @param path location of file to be read
   * @return String containing contents of file, null if error reading file
   * @throws IOException
   */
  public static String readFileContents(String path) throws Exception {
    String requestXML = "";
    StringBuffer contents = new StringBuffer();
    BufferedReader input = null;
    try {
      input = new BufferedReader(new FileReader(new File(path)));
      String line = null;
      while ((line = input.readLine()) != null) {
        contents.append(line);
      }
      input.close();
      return contents.toString();
    } catch (FileNotFoundException e) {
      throw new Exception("File not found: " + path);
    } catch (IOException e) {
      throw new Exception("Error reading file: " + path);
    }
  }

  /**
   * Converts a JDOM Document to a W3 DOM document.
   * 
   * @param doc JDOM Document
   * @return W3 DOM Document if converted successfully, null otherwise
   */
  public static org.w3c.dom.Document toDom(org.jdom.Document doc)
      throws Exception {
    try {
      XMLOutputter xmlOutputter = new XMLOutputter();
      StringWriter elemStrWriter = new StringWriter();
      xmlOutputter.output(doc, elemStrWriter);
      byte[] xmlBytes = elemStrWriter.toString().getBytes();
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      dbf.setNamespaceAware(true);
      return dbf.newDocumentBuilder().parse(new ByteArrayInputStream(xmlBytes));
    } catch (IOException e) {
      throw new Exception(
        "Error converting JDOM document to W3 DOM document: " + e.getMessage());
    } catch (ParserConfigurationException e) {
      throw new Exception(
        "Error converting JDOM document to W3 DOM document: " + e.getMessage());
    } catch (SAXException e) {
      throw new Exception(
        "Error converting JDOM document to W3 DOM document: " + e.getMessage());
    }
  }

  /**
   * Converts a JDOM Element to a W3 DOM Element
   * 
   * @param element JDOM Element
   * @return W3 DOM Element if converted successfully, null otherwise
   */
  public static org.w3c.dom.Element toDom(org.jdom.Element element)
      throws Exception {
    return toDom(element.getDocument()).getDocumentElement();
  }

  /**
   * Converts a W3 DOM Element to a JDOM Element
   * 
   * @param e W3 DOM Element
   * @return JDOM Element
   */
  public static org.jdom.Element toJdom(org.w3c.dom.Element e) {
    DOMBuilder builder = new DOMBuilder();
    org.jdom.Element jdomElem = builder.build(e);
    return jdomElem;
  }

  /**
   * Creates a JDOM Document from a string containing XML
   * 
   * @return JDOM Document if file contents converted successfully, null
   *         otherwise
   */
  public static Document createJdomDoc(String xmlString) throws Exception {
    try {
      SAXBuilder builder = new SAXBuilder();
      Document doc = builder.build(new ByteArrayInputStream(xmlString
        .getBytes()));
      return doc;
    } catch (IOException e) {
      throw new Exception("Error creating JDOM document from XML string: "
        + e.getMessage());
    } catch (JDOMException e) {
      throw new Exception("Error creating JDOM document from XML string: "
        + e.getMessage());
    }
  }

  /**
   * Creates a PublicKey from the specified public key file and algorithm.
   * Returns null if failure to generate PublicKey.
   * 
   * @param publicKeyFilepath location of public key file
   * @param algorithm algorithm of specified key file
   * @return PublicKey object representing contents of specified public key
   *         file, null if error in generating key or invalid file specified
   */
  public static PublicKey getPublicKey(String publicKeyFilepath,
      String algorithm) throws Exception {
    try {
      InputStream pubKey = null;
      try {
        URL url = new URL(publicKeyFilepath);
        pubKey = url.openStream();
      } catch (MalformedURLException e) {
        pubKey = new FileInputStream(publicKeyFilepath);
      }
      byte[] bytes = new byte[pubKey.available()];
      pubKey.read(bytes);
      pubKey.close();
      X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(bytes);
      KeyFactory factory = KeyFactory.getInstance(algorithm);
      return factory.generatePublic(pubSpec);
    } catch (FileNotFoundException e) {
      throw new Exception("ERROR: Public key file not found - "
        + publicKeyFilepath);
    } catch (IOException e) {
      throw new Exception("ERROR: Invalid public key file - "
        + e.getMessage());
    } catch (NoSuchAlgorithmException e) {
      throw new Exception("ERROR: Invalid public key algorithm - "
        + e.getMessage());
    } catch (InvalidKeySpecException e) {
      throw new Exception("ERROR: Invalid public key spec - "
        + e.getMessage());
    }
  }

  /**
   * Creates a PrivateKey from the specified public key file and algorithm.
   * Returns null if failure to generate PrivateKey.
   * 
   * @param algorithm algorithm of specified key file
   * @return PrivateKey object representing contents of specified private key
   *         file, null if error in generating key or invalid file specified
   */
  public static PrivateKey getPrivateKey(String privateKeyFilepath,
      String algorithm) throws Exception {
    try {
      InputStream privKey = null;
      try {
        URL url = new URL(privateKeyFilepath);
        privKey = url.openStream();
      } catch (MalformedURLException e) {
        privKey = new FileInputStream(privateKeyFilepath);
      }
      byte[] bytes = new byte[privKey.available()];
      privKey.read(bytes);
      privKey.close();
      PKCS8EncodedKeySpec privSpec = new PKCS8EncodedKeySpec(bytes);
      KeyFactory factory = KeyFactory.getInstance(algorithm);
      return factory.generatePrivate(privSpec);
    } catch (FileNotFoundException e) {
      throw new Exception("ERROR: Private key file not found - "
        + privateKeyFilepath);
    } catch (IOException e) {
      throw new Exception("ERROR: Invalid private key file - "
        + e.getMessage());
    } catch (NoSuchAlgorithmException e) {
      throw new Exception("ERROR: Invalid private key algorithm - "
        + e.getMessage());
    } catch (InvalidKeySpecException e) {
      throw new Exception("ERROR: Invalid private key spec - "
        + e.getMessage());
    }
  }

  /**
   * Create a randomly generated string conforming to the xsd:ID datatype.
   * containing 160 bits of non-cryptographically strong pseudo-randomness, as
   * suggested by SAML 2.0 core 1.2.3. This will also apply to version 1.1
   * 
   * @return the randomly generated string
   */
  public static String createID() {
    byte[] bytes = new byte[20]; // 160 bits
    random.nextBytes(bytes);

    char[] chars = new char[40];

    for (int i = 0; i < bytes.length; i++) {
      int left = (bytes[i] >> 4) & 0x0f;
      int right = bytes[i] & 0x0f;
      chars[i * 2] = charMapping[left];
      chars[i * 2 + 1] = charMapping[right];
    }

    return String.valueOf(chars);
  }

  /**
   * Gets the current date and time in the format specified by xsd:dateTime in
   * UTC form, as described in SAML 2.0 core 1.3.3 This will also apply to
   * Version 1.1
   * 
   * @return the date and time as a String
   */
  public static String getDateAndTime() {
    Date date = new Date();
    return DATE_TIME_FORMAT.format(date);
  }

  /**
   * Gets the date and time for the beginning of the Assertion time interval in
   * as specified by SAML v2.0 section 2.5.1.
   *
   * @return the date and time as a String
   */
  public static String getNotBeforeDateAndTime() {
    Calendar beforeCal = Calendar.getInstance();
    beforeCal.add(Calendar.MINUTE, ASSERTION_NOT_BEFORE_MINUTES);
    return DATE_TIME_FORMAT.format(beforeCal.getTime());
  }

  /**
   * Gets the date and time for the end of the Assertion time interval in
   * as specified by SAML v2.0 section 2.5.1.
   *
   * @return the date and time as a String
   */
  public static String getNotOnOrAfterDateAndTime() {
    Calendar afterCal = Calendar.getInstance();
    afterCal.add(Calendar.MINUTE, ASSERTION_NOT_ON_OR_AFTER_MINUTES);
    return DATE_TIME_FORMAT.format(afterCal.getTime());
  }

}
