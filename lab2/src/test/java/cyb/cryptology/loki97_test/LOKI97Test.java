package cyb.cryptology.loki97_test;

import cyb.cryptology.loki97.LOKI97;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LOKI97Test {

    @Test
    public void testOnOriginalExample() {
        String key = "000102030405060708090A0B0C0D0E0F101112131415161718191A1B1C1D1E1F";
        String plain = "000102030405060708090A0B0C0D0E0F";
        String expectedCipher = "75080E359F10FE640144B35C57128DAD";

        String cipher = LOKI97.encrypt(plain, key);
        Assertions.assertEquals(expectedCipher, cipher);

        String decrypted = LOKI97.decrypt(cipher, key);
        Assertions.assertEquals(plain, decrypted);
    }

    @Test
    public void testOnShortKeys() {
        String key = "000102030405060708090A0B0C0D0E0F1011121314151617";
        String plain = "000102030405060708090A0B0C0D0E0F";

        String cipher = LOKI97.encrypt(plain, key);
        String decrypted = LOKI97.decrypt(cipher, key);
        Assertions.assertEquals(plain, decrypted);

        key = "000102030405060708090A0B0C0D0E0F";
        plain = "000102030405060708090A0B0C0D0E0F";

        cipher = LOKI97.encrypt(plain, key);
        decrypted = LOKI97.decrypt(cipher, key);
        Assertions.assertEquals(plain, decrypted);
    }
}
