package com.amp.fun;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

public class DictionaryTest {

    Dictionary dictionary;

    @BeforeEach
    public void initDictionary() {
        Set<Word> words = new HashSet<>();
        words.add(new Word("a"));
        words.add(new Word("ab"));
        words.add(new Word("b"));
        words.add(new Word("ba"));
        words.add(new Word("c"));
        words.add(new Word("cc"));
        dictionary = new Dictionary(words);
    }

    @Test
    public void getMultiWordSolutions_simple_returnsWordList() {
        List<SortedSet<Word>> wordLists = dictionary.getMultiWordSolutions("abcd");
        for(SortedSet<Word> s : wordLists) {
            System.out.println(s);
        }
    }
}
