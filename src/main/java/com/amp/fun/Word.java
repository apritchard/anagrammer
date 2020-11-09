package com.amp.fun;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Word implements Serializable, Comparable<Word> {
  private static final long serialVersionUID = 1L;

  private String                  word;
  private Set<Character>          requiredLetters = new HashSet<>();
  private Map<Character, Integer> letters         = new HashMap<>();
  private Map<Character, Integer> optionalLetters;

  Pattern requiredCharactersRegex = Pattern.compile("(\\S)\\*"); //letters followed by * are required
  Matcher matcher                 = null;

  public Word(String word){
    matcher = requiredCharactersRegex.matcher(word);
    while(matcher.find()){
      requiredLetters.add(matcher.group().charAt(0));
    }
    word = word.replace("*", "");

    this.word = word;
    for(Character c : word.toCharArray()){
      if(letters.containsKey(c)){
        letters.put(c, letters.get(c) + 1);
      } else {
        letters.put(c,1);
      }
    }
  }

  public Word(Map<Character,Integer> letters, Map<Character,Integer> optionalLetters, String word){
    this.word = word;
    this.letters = letters;
    this.optionalLetters = optionalLetters;
  }

  //Don't parse dictionary words for [] to save time
  public static Word getParametizedWord(String word){
    Map<Character,Integer> l = new HashMap<>();
    Map<Character,Integer> ol = new HashMap<>();
    Map<Character,Integer> current;

    current = l;

    for(Character c : word.toCharArray()){
      if(c == '['){
        current = ol;
      } else if (c == ']'){
        current = l;
      }
      if(current.containsKey(c)){
        current.put(c, current.get(c) + 1);
      } else {
        current.put(c,1);
      }
    }
    return new Word(l, ol, word);
  }

  public boolean containsExactly(Word other){
    return contains(other) && word.contains(other.word);
  }

  public boolean contains(Word other){
    Map<Character, Integer> otherLetters = other.getLetters();
    int wildcards = letters.get('?') == null ? 0 : letters.get('?');
    for(Character c : otherLetters.keySet()){
      if(!letters.containsKey(c)) {
        wildcards -= otherLetters.get(c);
        if(wildcards < 0){
          return false;
        }
      } else if (letters.get(c) < otherLetters.get(c)){
        if(--wildcards < 0){
          return false;
        }
      }
    }
    return true;
  }

  public boolean containsRepetition(Word other){
    if(!other.letters.keySet().containsAll(requiredLetters)){
      return false;
    }
    Map<Character, Integer> otherLetters = other.getLetters();
    for(Character c : otherLetters.keySet()){
      if(!letters.containsKey(c)){
        return false;
      }
    }
    return true;
  }

  public boolean containsCrossword(Word other){
    if(word.length() != other.word.length()){
      return false;
    }
    for(int i=0 ; i < word.length() ; i++){
      if(word.charAt(i) != '?' &&
          word.charAt(i) != other.word.charAt(i)){
        return false;
      }
    }
    return true;
  }


  /**
   * Creates a new word that contains the letters of the current word minutes the letters of the other word.
   * Returns null if there are not enough characters.
   * @param other
   * @return
   */
  public Word subtract(Word other) {
    Map<Character, Integer> newWordLetters = letters.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    for(Map.Entry<Character, Integer> c : other.letters.entrySet()) {
      if(!newWordLetters.containsKey(c.getKey()) || c.getValue() > newWordLetters.get(c.getKey())) {
        return null;
      } else if (c.getValue() == newWordLetters.get(c.getKey())) {
        newWordLetters.remove(c.getKey());
      } else {
        newWordLetters.put(c.getKey(), newWordLetters.get(c.getKey()) - c.getValue());
      }
    }
    return new Word(newWordLetters, null, makeWordString(newWordLetters));
  }

  /**
   * Creates a string containing all the letters represented by the provided map.
   * @param letters
   * @return
   */
  public static String makeWordString(Map<Character, Integer> letters) {
    StringBuilder sb = new StringBuilder();
    for(Map.Entry<Character,Integer> c : letters.entrySet()) {
      for(int i = 0; i<c.getValue(); i++) {
        sb.append(c.getKey());
      }
    }
    return sb.toString();
  }

  public Map<Character, Integer> getLetters(){
    return letters;
  }
  public String toString(){
    return word + "(" + word.length() + ")";
  }
  public int getLength(){
    return word.length();
  }

  @Override
  public int compareTo(Word other) {
    if(other.toString().length() == this.toString().length()){
      return this.toString().compareTo(other.toString());
    }
    return other.toString().length() - this.toString().length();
  }
}
