package com.anasboot.url_minify;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class Base62Test {

    @Test   
    void encodeZeroAsSingleZeroDigit() {
        String result = Base62.encode(0L);
        assertEquals("0", result);
    }

    @Test
    void encodeRejectsNegativeNumbers() {
        assertThrows(IllegalArgumentException.class, () -> Base62.encode(-1L));
    }

    @Test
    void encodeTenAsA() {
        String result = Base62.encode(10L);
        assertEquals("A", result);
    }

    @Test
    void encode61() {
        String result = Base62.encode(61L);
        assertEquals("z", result);
    }

    @Test 
    void encode62() {
        String result = Base62.encode(62L);
        assertEquals("10", result);
     }

     @Test 
     void encode125() {
        String result = Base62.encode(125L);
        assertEquals("21", result);
     }

     @Test
     void encode3844() {
        String result = Base62.encode(3844L);
        assertEquals("100", result);
     }

     @Test 
     void decode21() {
        long result = Base62.decode("21");
        assertEquals(125L, result);
     }

     @Test 
     void encodeAndDecode() {
        long original = 123456789L;
        String encoded = Base62.encode(original);
        long decoded = Base62.decode(encoded);
        assertEquals(original, decoded);
     }

     @Test 
     void decodeInvalidCharacter() {
        assertThrows(IllegalArgumentException.class, () -> Base62.decode("!"));
     }

     @Test 
     void decodeEmptyString() {
        assertThrows(IllegalArgumentException.class, () -> Base62.decode(""));
     }

     @Test 
     void decodeMalformedInput() {
        assertThrows(IllegalArgumentException.class, () -> Base62.decode("ab-c"));
     }

     @Test 
     void overflowTest() {
        long maxLong = Long.MAX_VALUE;
        String encoded = Base62.encode(maxLong);
        long decoded = Base62.decode(encoded);
        assertEquals(maxLong, decoded);
     }
}
