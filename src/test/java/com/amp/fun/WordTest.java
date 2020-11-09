package com.amp.fun;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class WordTest {

    @Test
    public void subtract_wordFound_lettersRemoved(){
        Word word = new Word("testString");
        Word word2 = new Word("String");
        Word result = word.subtract(word2);

        Assertions.assertEquals(4, result.getLength());
        Assertions.assertEquals(2, result.getLetters().get('t'));
        Assertions.assertEquals(1, result.getLetters().get('e'));
        Assertions.assertEquals(null, result.getLetters().get('g'));

    }

    @Test
    public void subtract_wordMissing_returnNull() {
        Word word = new Word("testString");
        Word word2 = new Word("String2");
        Word result = word.subtract(word2);
        Assertions.assertEquals(null, result);
    }

    @Test
    public void subtract_wordFound_newWordStringCorrect() {
        Word word = new Word("testString");
        Word word2 = new Word("String");
        Word result = word.subtract(word2);
        String expectedDiff = "stte";
        Assertions.assertEquals(expectedDiff, Word.makeWordString(result.getLetters()));
    }
}
