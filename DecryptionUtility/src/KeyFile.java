import java.security.PrivateKey;
import java.security.PublicKey;

public abstract class KeyFile {
  
	protected PublicKey publicKey;
	protected PrivateKey privateKey;

	KeyFile(String password) {
	}

	protected byte[] decrypt(byte[] cipherText) {
		return null;
	}

	protected byte[] encrypt(byte[] plainText) {
		return null;
	}
	
	protected abstract void encryptionDecryptionTest();
}
