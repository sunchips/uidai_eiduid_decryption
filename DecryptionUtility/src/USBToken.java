import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.Properties;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.management.RuntimeErrorException;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sun.security.pkcs11.SunPKCS11;

public class USBToken extends KeyFile {

  private static Provider pkcs11Provider;
  private X509Certificate cert;
  private static final Logger LOG = LogManager.getLogger(USBToken.class);

  public USBToken(String password) {
    super(password);
    try {
      Properties prop = new Properties();
      prop.load(new FileInputStream("DecryptorUtility.properties"));
      pkcs11Provider = new SunPKCS11(prop.getProperty("pkcsConfigFileName").trim());
      Security.addProvider(pkcs11Provider);

      // prop.load(new FileInputStream(pkcs11Provider.getName()))

      char[] pin = password.toCharArray();
      // Load KeyStore
      // System.out.println(KeyStore.getDefaultType());

      KeyStore smartCardKeyStore = KeyStore.getInstance("PKCS11", pkcs11Provider);
      smartCardKeyStore.load(null, pin);

      // Get the enumeration of the entries in the keystore
      Enumeration<String> aliasesEnum = smartCardKeyStore.aliases();
      int start = 1;
      while (aliasesEnum.hasMoreElements()) {
        // Print alias
        String alias = (String) aliasesEnum.nextElement();
//        System.out.println("Alias: " + alias);

        // Print certificate
        cert = (X509Certificate) smartCardKeyStore.getCertificate(alias);
//        System.out.println("Certificate: " + cert);

        // Print public key
        publicKey = cert.getPublicKey();
//        System.out.println("Public key: " + publicKey);

        // Print private key
        privateKey = (PrivateKey) smartCardKeyStore.getKey(alias, null);
//        System.out.println("Private Key: " + privateKey);

        if (start == Main.keyNumber)
          break;
        start++;
      }
      System.out.println("Public and Private key found");
    } catch (UnrecoverableKeyException e) {
      e.printStackTrace();
      LOG.error(ExceptionUtils.getStackTrace(e));
      throw new RuntimeErrorException(new Error("Unable to load USB Device."));
    } catch (KeyStoreException e) {
      e.printStackTrace();
      LOG.error(ExceptionUtils.getStackTrace(e));
      throw new RuntimeErrorException(new Error("Unable to load USB Device."));
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
      LOG.error(ExceptionUtils.getStackTrace(e));
      throw new RuntimeErrorException(new Error("Unable to load USB Device."));
    } catch (CertificateException e) {
      e.printStackTrace();
      LOG.error(ExceptionUtils.getStackTrace(e));
      throw new RuntimeErrorException(new Error("Unable to load USB Device."));
    } catch (IOException e) {
      e.printStackTrace();
      LOG.error(ExceptionUtils.getStackTrace(e));
      throw new RuntimeErrorException(new Error("Unable to load USB Device."));
    } finally {
      
    }
  }

  protected byte[] decrypt(byte[] cipherText) {
    try {
      Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding", pkcs11Provider);
      cipher.init(Cipher.DECRYPT_MODE, privateKey);
      return cipher.doFinal(cipherText);
    } catch (InvalidKeyException e) {
      e.printStackTrace();
      LOG.error(ExceptionUtils.getStackTrace(e));
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
      LOG.error(ExceptionUtils.getStackTrace(e));
    } catch (NoSuchPaddingException e) {
      e.printStackTrace();
      LOG.error(ExceptionUtils.getStackTrace(e));
    } catch (IllegalBlockSizeException e) {
      e.printStackTrace();
      LOG.error(ExceptionUtils.getStackTrace(e));
    } catch (BadPaddingException e) {
      e.printStackTrace();
      LOG.error(ExceptionUtils.getStackTrace(e));
    }
    return null;
  }

  protected byte[] encrypt(byte[] plainText) {
    try {
      Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding", pkcs11Provider);
      cipher.init(Cipher.ENCRYPT_MODE, publicKey);
      return cipher.doFinal(plainText);
    } catch (InvalidKeyException e) {
      e.printStackTrace();
      LOG.error(ExceptionUtils.getStackTrace(e));
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
      LOG.error(ExceptionUtils.getStackTrace(e));
    } catch (NoSuchPaddingException e) {
      e.printStackTrace();
      LOG.error(ExceptionUtils.getStackTrace(e));
    } catch (IllegalBlockSizeException e) {
      e.printStackTrace();
      LOG.error(ExceptionUtils.getStackTrace(e));
    } catch (BadPaddingException e) {
      e.printStackTrace();
      LOG.error(ExceptionUtils.getStackTrace(e));
    }
    return null;
  }

  protected void encryptionDecryptionTest() {
    byte[] plainText = new String("Hello World!").getBytes();

    byte[] cipherText = encrypt(plainText);
    System.out.println("Cipher Text: " + Utils.toHex(cipherText));

    byte[] decryptedText = decrypt(cipherText);
    System.out.println("Decrypted Text: " + new String(decryptedText));
  }

  // private void getCapabilities() {
  // List<String> arrayList = new ArrayList<String>();
  // Iterator<Object> it = pkcs11Provider.keySet().iterator();
  // while (it.hasNext()) {
  // String entry = (String) it.next();
  // if (entry.startsWith("Alg.Alias.")) {
  // entry = entry.substring("Alg.Alias.".length());
  // }
  // String factoryClass = entry.substring(0, entry.indexOf('.'));
  // String name = entry.substring(factoryClass.length() + 1);
  // arrayList.add(factoryClass + ": " + name);
  // }
  // Collections.sort(arrayList);
  // Iterator<String> is = arrayList.iterator();
  // while (is.hasNext()) {
  // System.out.println(is.next());
  // }
  // }

}
