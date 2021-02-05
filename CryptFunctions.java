/** This class has been written to keep cryptography functions more organized. **/
public class CryptFunctions {

    /** Second half (48-96) of the encryptedString is taken.
     * Then it is inserted into the scrambleFunction with the subKey from the subKeyGenerator function.
     * The resulting scrambledString word is concatenated with the first half (0-48) of the encrypted string.
     * After this process is done 10 times, the encrypted word appears.
     * @param block --> block String.
     * @param key   --> binary input. **/
    public String encryption(String block, String key) {
        String encryptedString = block;
        for (int i = 0; i < 10; i++) {
            String temp = encryptedString.substring(48, 96);
            String subKey = subKeyGenerator(key, i);
            String scrambledString = scrambleFunction(encryptedString.substring(48, 96), subKey);
            temp = temp.concat(xor(scrambledString, encryptedString.substring(0, 48)));
            encryptedString = temp;
        }
        return encryptedString;
    }

    /** First half (0-48) of the decryptedString is taken.
     * Then it is inserted into the scrambleFunction with the subKey from the subKeyGenerator function.
     * The resulting scrambledString word is concatenated with the first half (48-96) of the encrypted string.
     * After this process is done 10 times, the encrypted word appears.
     * @param block --> block String.
     * @param key   --> binary input. **/
    public String decryption(String block, String key) {
        String decryptedString = block;
        for (int i = 0; i < 10; i++) {
            String subKey = subKeyGenerator(key, 9 - i);
            String scrambledString = scrambleFunction(decryptedString.substring(0, 48), subKey);
            String temp = xor(scrambledString, decryptedString.substring(48, 96));
            temp = temp.concat(decryptedString.substring(0, 48));
            decryptedString = temp;
        }
        return decryptedString;
    }


    /** This function is used in cases of enc and dec.
     * Because in OFB encryption mode, the same method is used for both cases. **/
    public void modeOFB(String binaryKey, String input, StringBuilder decryptedString, String x0) {
        for (int i = 0; i < 96; i++) x0 = x0.concat("1");
        for (int i = 0; i < input.length(); i += 96) {
            x0 = encryption(x0, binaryKey);
            decryptedString.append(xor(x0, input.substring(i, i + 96)));
        }
    }

    public String scrambleFunction(String right, String subKey) {
        String[][] sBox = {
                {"0010", "1100", "0100", "0001", "0111", "1010", "1011", "0110", "1000", "0101", "0011", "1111", "1101", "0000", "1110", "1001"},
                {"1110", "1011", "0010", "1100", "0100", "0111", "1101", "0001", "0101", "0000", "1111", "1010", "0011", "1001", "1000", "0110"},
                {"0100", "0010", "0001", "1011", "1010", "1101", "0111", "1000", "1111", "1001", "1100", "0101", "0110", "0011", "0000", "1110"},
                {"1011", "1000", "1100", "0111", "0001", "1110", "0010", "1101", "0110", "1111", "0000", "1001", "1010", "0100", "0101", "0011"}};

        String temp = xor(right, subKey);
        StringBuilder xorString = new StringBuilder(temp);
        int len = temp.length();

        for (int i = 0, j = 6; i < len; i += 12, j += 12) {
            xorString.append(xor(temp.substring(i, j), temp.substring(j, j + 6)));
        }

        StringBuilder afterSBOX = new StringBuilder();

        for (int i = 0; i < xorString.length(); i += 6) {
            int row = Character.getNumericValue(xorString.charAt(i)) * 2 + Character.getNumericValue(xorString.charAt(i + 5));
            int column = 0;
            for (int j = i + 1; j <= i + 4; j++) {
                column += Character.getNumericValue(xorString.charAt(j)) * Math.pow(2, i + 4 - j);
            }
            afterSBOX.append(sBox[row][column]);
        }

        StringBuilder output = new StringBuilder();
        for (int i = 0; i < afterSBOX.length() - 1; i += 2) {
            char even = afterSBOX.charAt(i);
            char odd = afterSBOX.charAt(i + 1);
            output.append(odd);
            output.append(even);
        }
        return output.toString();
    }

    /** First, the most significant bit (0th char) is taken from the circledKey and added to the part that is the length of that word.
     * If the number roundNumber is even, the digits of the 2 and multiples of the circledKey are appended to the subKey string.
     * If the number is odd, the opposite happens. **/
    public static String subKeyGenerator(String key, int roundNumber) {
        String circledKey = key;
        for (int i = 0; i < roundNumber + 1; i++) {
            char msb = circledKey.charAt(0);
            circledKey = circledKey.substring(1, key.length()) + msb;
        }

        StringBuilder subKey = new StringBuilder();

        if (roundNumber % 2 == 0) {
            for (int i = 0; i < 96; i += 2) {
                subKey.append(circledKey.charAt(i));
            }
        } else {
            for (int i = 1; i < 96; i += 2) {
                subKey.append(circledKey.charAt(i));
            }
        }
        return subKey.toString();
    }

    /** This function does the same as xor.
     * It does this by handling strings char by char. **/
    public static String xor(String String1, String String2) {
        StringBuilder xorString = new StringBuilder();
        for (int i = 0; i < String1.length(); i++) {
            if (String1.charAt(i) == String2.charAt(i)) {
                xorString.append("0");
            } else {
                xorString.append("1");
            }
        }
        return xorString.toString();
    }
}