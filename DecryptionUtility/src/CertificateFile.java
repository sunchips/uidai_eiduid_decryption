import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CertificateFile extends KeyFile {

  private static final Logger LOG = LogManager.getLogger(KeyFile.class);

  public CertificateFile(String filePath, String password) {
    super(password);
    FileInputStream keyFile;
    try {
      KeyStore ks = null;
      char[] pin = null;
      try {
        keyFile = new FileInputStream(Paths.get(filePath).toFile());
        ks = KeyStore.getInstance("pkcs12");
        pin = password.toCharArray();
        ks.load(keyFile, pin);
      } catch (Exception e) {
        try {
          keyFile = new FileInputStream(new File(Main.class.getResource(filePath).toURI()));
          ks = KeyStore.getInstance("pkcs12");
          pin = password.toCharArray();
          ks.load(keyFile, pin);
        } catch (Exception e2) {
          System.err.println(-1);
          e2.printStackTrace();
        }
      }
      Enumeration<String> aliasesEnum = ks.aliases();
      while (aliasesEnum.hasMoreElements()) {
        // Print alias
        String alias = (String) aliasesEnum.nextElement();
        // System.out.println("Alias: " + alias);

        // Print certificate
        X509Certificate cert = (X509Certificate) ks.getCertificate(alias);
        // System.out.println("Certificate: " + cert);

        // Print public key
        publicKey = cert.getPublicKey();
        // System.out.println("Public key: " + publicKey);

        // Print private key
        privateKey = (PrivateKey) ks.getKey(alias, pin);
        // System.out.println("Private key: " + privateKey);
      }
      System.out.println("Public and Private key found.");
    } catch (KeyStoreException e) {
      e.printStackTrace();
      LOG.error(ExceptionUtils.getStackTrace(e));
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
      LOG.error(ExceptionUtils.getStackTrace(e));
    } catch (UnrecoverableKeyException e) {
      e.printStackTrace();
      LOG.error(ExceptionUtils.getStackTrace(e));
    }
  }

  protected byte[] encrypt(byte[] plainText) {
    try {
      Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding");
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
    }
    return null;
  }

  protected byte[] decrypt(byte[] cipherText) {
    try {
      Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding");
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

  protected void encryptionDecryptionTest() {
    byte[] plainText = new String("Hello World!").getBytes();

    byte[] cipherText = encrypt(plainText);
    System.out.println("Cipher Text: " + Utils.toHex(cipherText));

    byte[] decryptedText = decrypt(cipherText);
    System.out.println("Decrypted Text: " + new String(decryptedText).trim());
  }

}
