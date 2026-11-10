package com.anasboot.url_minify;

public class Base62 {
    private static final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public static String encode(long input) {
        if(input < 0) throw new IllegalArgumentException("Input must be non-negative");
        if (input == 0) return "0";
        StringBuilder encoded = new StringBuilder();
        while (input > 0) {
            int remainder = (int) (input % 62);
            encoded.append(BASE62_CHARS.charAt(remainder));
            input /= 62;
        }
        return encoded.reverse().toString();
    }

    public static long decode(String input) {
        if (input == null) throw new IllegalArgumentException("Input cannot be null");
        if (input.isEmpty()) throw new IllegalArgumentException("Input cannot be empty");
        long result = 0;
        for (char c : input.toCharArray()) {
            int index = BASE62_CHARS.indexOf(c);
            if (index == -1) throw new IllegalArgumentException("Invalid character: " + c);
            try {
                result = Math.addExact(Math.multiplyExact(result, 62), index);
            } catch (ArithmeticException e) {
                throw new IllegalArgumentException("Decoded value is too large for a long", e);
            }
        }
        return result;
    }
}
