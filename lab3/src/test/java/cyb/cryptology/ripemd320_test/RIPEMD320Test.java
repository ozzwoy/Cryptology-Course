package cyb.cryptology.ripemd320_test;

import cyb.cryptology.ripemd320.RIPEMD320;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RIPEMD320Test {

    @Test
    public void testOnWikiExamples() {
        Assertions.assertEquals(
                "22d65d5661536cdc75c1fdf5c6de7b41b9f27325ebc61e8557177d705a0ec880151c3a32a00899b8",
                RIPEMD320.hash(""));

        Assertions.assertEquals(
                "ce78850638f92658a5a585097579926dda667a5716562cfcf6fbe77f63542f99b04705d6970dff5d",
                RIPEMD320.hash("a"));
    }

    @Test
    public void testOnOnlineCalculatorExamples() {
        Assertions.assertEquals(
                "e7660e67549435c62141e51c9ab1dcc3b1ee9f65c0b3e561ae8f58c5dba3d21997781cd1cc6fbc34",
                RIPEMD320.hash("The quick brown fox jumps over the lazy dog"));

        Assertions.assertEquals(
                "393e0df728c4ce3d79e7dcfd357d5c26f5c6d64c6d652dc53b6547b214ea9183e4f61c477ebf5cb0",
                RIPEMD320.hash("The quick brown fox jumps over the lazy cog"));
    }
}
