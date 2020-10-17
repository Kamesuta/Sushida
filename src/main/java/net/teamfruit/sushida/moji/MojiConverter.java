package net.teamfruit.sushida.moji;


import java.util.List;

public class MojiConverter {

    public static String convertRomajiToHiragana(String string) {

        string = string.toLowerCase();
        string = replaceDoubleConsonantWithSokuonMarker(string, 'っ');
        string = replaceStringWithConversionTable(string, ConversionTable.getRomajiToHiragana());

        return string;
    }

    public static String replaceStringWithConversionTable(String string, ConversionTable conversionTable) {

        StringBuilder resultBuilder = new StringBuilder();

        int currentOffset = 0;

        while (currentOffset < string.length()) {

            int maxSubstringLength = Math.min(conversionTable.getMaxKeyLength(), string.length() - currentOffset);

            // Look-up the longest substring match.
            for (int substringLength = maxSubstringLength; substringLength > 0; substringLength--) {

                String substring = string.substring(currentOffset, currentOffset + substringLength);
                List<String> replacementString = conversionTable.get(substring);

                // Replace substring if there's a match.
                if (!replacementString.isEmpty()) {
                    resultBuilder.append(replacementString.get(0));

                    currentOffset += substring.length();
                    break;
                }

                // Keep original character if no match found.
                if (substringLength == 1) {
                    resultBuilder.append(substring);

                    currentOffset += 1;
                    break;
                }
            }
        }

        return resultBuilder.toString();
    }

    public static String replaceLongVowelWithDashMarker(String string) {

        StringBuilder resultBuilder = new StringBuilder(string);

        for (int currentOffset = 1; currentOffset < string.length(); currentOffset++) {

            char currentCharacter = string.charAt(currentOffset);
            char previousCharacter = string.charAt(currentOffset - 1);

            if (isRomanVowel(currentCharacter) && currentCharacter == previousCharacter) {
                resultBuilder.deleteCharAt(currentOffset);
                resultBuilder.insert(currentOffset, 'ー');
            }
        }

        return resultBuilder.toString();
    }

    public static String replaceDoubleConsonantWithSokuonMarker(String string, char sokuonMarker) {

        StringBuilder resultBuilder = new StringBuilder(string);

        for (int currentOffset = 1; currentOffset < string.length() - 1; currentOffset++) {

            char currentCharacter = string.charAt(currentOffset);
            char nextCharacter = string.charAt(currentOffset + 1);

            boolean isDoubleConsonant = currentCharacter == nextCharacter;
            boolean isExceptionalCase = currentCharacter == 't' && nextCharacter == 'c';

            if (isRomanConsonant(currentCharacter) && (isDoubleConsonant || isExceptionalCase)) {
                resultBuilder.deleteCharAt(currentOffset);
                resultBuilder.insert(currentOffset, sokuonMarker);
            }
        }

        return resultBuilder.toString();
    }

    public static String replaceSokuonMarkersWithDoubleConsonant(String string) {

        StringBuilder resultBuilder = new StringBuilder(string);

        for (int currentOffset = 0; currentOffset < string.length() - 1; currentOffset++) {

            char currentCharacter = string.charAt(currentOffset);
            char nextCharacter = string.charAt(currentOffset + 1);

            boolean isSokuon = currentCharacter == 'ッ' || currentCharacter == 'っ';

            if (isSokuon && isRomanConsonant(nextCharacter)) {
                char replacementCharacter = nextCharacter == 'c' ? 't' : nextCharacter;

                resultBuilder.deleteCharAt(currentOffset);
                resultBuilder.insert(currentOffset, replacementCharacter);
            }
        }

        return resultBuilder.toString();
    }

    public static String replaceDashMarkerWithLongVowel(String string) {

        StringBuilder resultBuilder = new StringBuilder(string);

        for (int currentOffset = 1; currentOffset < string.length(); currentOffset++) {

            char currentCharacter = string.charAt(currentOffset);
            char previousCharacter = string.charAt(currentOffset - 1);

            if (currentCharacter == '-' && isRomanVowel(previousCharacter)) {

                resultBuilder.deleteCharAt(currentOffset);
                resultBuilder.insert(currentOffset, previousCharacter);
            }
        }

        return resultBuilder.toString();
    }

    public static boolean isRomanConsonant(char character) {
        return character >= 'a' && character <= 'z' && !isRomanVowel(character);
    }

    public static boolean isRomanVowel(char character) {
        return character == 'a' || character == 'i' || character == 'u' || character == 'e' || character == 'o';
    }
}
