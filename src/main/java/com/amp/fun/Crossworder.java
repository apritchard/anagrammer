package com.amp.fun;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Crossworder {
  public static void main(String[] args) throws Exception {
    Dictionary dictionary = Dictionary.buildDictionary("/dict2/enable1.txt");
    Crossword cw = Crossword.buildCrossword("/cw/eyetest.txt");
    printStream(cw.getHorizontal(), dictionary, "Horizontal");
    printStream(cw.getVertical(), dictionary, "Vertical");
    printStream(cw.getDiagAscending(), dictionary, "Diagonal Ascending");
    printStream(cw.getDiagDescending(), dictionary, "Diagonal Descending");
  }

  public static void printStream(List<String> words, Dictionary d, String name){
    System.out.println(name + ":");
    AtomicInteger i = new AtomicInteger(1);
    words.stream().map(d::getExactWords)
      .map(
        ws -> ws.stream()
          .filter(w -> w.getLength() >= 3)
          .map(Word::toString)
          .collect(Collectors.joining(", ")))
      .forEach(s -> System.out.println("" + i.getAndIncrement() + ": " + s));

    i.set(1);
    System.out.println(name + "(reversed):");
    words.stream()
      .map(s -> new StringBuilder(s)
          .reverse().toString())
      .map(d::getExactWords)
      .map(
        ws -> ws.stream().filter(w -> w.getLength() >= 3)
          .map(Word::toString)
          .collect(Collectors.joining(", ")))
      .forEach(s -> System.out.println("" + i.getAndIncrement() + ": " + s));
  }

}
