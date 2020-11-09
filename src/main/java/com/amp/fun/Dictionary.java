package com.amp.fun;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Dictionary {
  Set<Word> words;

  private Dictionary(Reader reader) throws IOException{
    words = new HashSet<Word>();
    BufferedReader br = new BufferedReader(reader);
    String word;
    int count = 0;
    word = br.readLine();
    while(word != null){
      words.add(new Word(word));
      word = br.readLine();
      if(count++ % 10000 == 0){
        System.out.println(count-1 + " read...");
      }
    }

    System.out.println("Read in " + words.size() + " words");
  }

  protected Dictionary(Set<Word> words){
    this.words = words;
  }

  public static Dictionary buildDictionary(File path) throws FileNotFoundException, IOException{
    return new Dictionary(new FileReader(path));
  }

  public static Dictionary buildDictionary(InputStream is) throws IOException{
    return new Dictionary(new InputStreamReader(is));
  }

  public static Dictionary buildDictionary(String s) throws URISyntaxException, IOException{
    Path p = Paths.get(s);
    if(!Files.exists(p)){
      p = Paths.get(Dictionary.class.getResource(s).toURI());
    }
    return new Dictionary(Files.lines(p).map(Word::new).collect(Collectors.toSet()));
  }

  public SortedSet<Word> getExactWords(String wordString){
    Word word = new Word(wordString);
    SortedSet<Word> exactWords = new TreeSet<Word>();
    for(Word w : words){
      if(word.containsExactly(w)){
        exactWords.add(w);
      }
    }
    return exactWords;
  }

  public SortedSet<Word> getContainedWords(String wordString, boolean allowRepetition, boolean inOrder){
    if(inOrder) {
      return getContainedWordsCrossword(wordString);
    }else if(allowRepetition) {
      return getContainedWordsWithRepetition(wordString);
    } else {
      return getContainedWords(wordString);
    }
  }

  public SortedSet<Word> getContainedWordsCrossword(String wordString){
    System.out.println("wordString = [" + wordString + "]");
    Word word = new Word(wordString);
    SortedSet<Word> containedWords = words.stream()
                                          .filter(w -> word.containsCrossword(w))
                                          .collect(Collectors.toCollection(()-> new TreeSet<>()));
    return containedWords;
  }

  public NavigableSet<Word> getContainedWords(String wordString){
    System.out.println(wordString);
    Word word = new Word(wordString);
    NavigableSet<Word> containedWords = new TreeSet<>();
    for(Word w : words){
      if(word.contains(w)){
        containedWords.add(w);
      }
    }
    return containedWords;
  }

  public List<SortedSet<Word>> getMultiWordSolutions(String wordString) {
    var ret = getMultiWordSolutions(new Word(wordString), getContainedWords(wordString), new TreeSet<>());
    ret.sort((s1, s2) -> s2.size() - s1.size());
    return ret;
  }


  public static List<SortedSet<Word>> getMultiWordSolutions(Word startingWord, NavigableSet<Word> possibleWords, NavigableSet<Word> currentWords) {

    List<SortedSet<Word>> newSets = new ArrayList<>();
    for(Word w : possibleWords) {
      Word remainingLetters = startingWord.subtract(w);
      if(remainingLetters == null) {
        continue;
      }
      NavigableSet<Word> newWords = new TreeSet<>(currentWords);
      newWords.add(w);
      newSets.addAll(getMultiWordSolutions(remainingLetters, possibleWords.tailSet(w, false), newWords ));
    }

    if(newSets.isEmpty()) {
      newSets.add(currentWords);
    }

    return newSets;
  }

  public SortedSet<Word> getContainedWordsWithRepetition(String wordString){
    System.out.println("wordString = [" + wordString + "]");
    Word word = new Word(wordString);
    SortedSet<Word> containedWords = words.stream()
                                          .filter(w -> word.containsRepetition(w))
                                          .collect(Collectors.toCollection(()-> new TreeSet<>()));
    return containedWords;
  }
}
