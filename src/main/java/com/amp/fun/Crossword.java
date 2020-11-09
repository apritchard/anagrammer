package com.amp.fun;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Crossword {

  List<String> horizontal;
  List<String> vertical;
  List<String> diagAscending;
  List<String> diagDescending;

  public Crossword(List<String> lines){
    char[][] puzzle = lines.stream().map(s -> s.toLowerCase().replaceAll("\\s+", "").toCharArray()).toArray(char[][]::new);
    System.out.println(puzzle.length + "x" + puzzle[0].length);
    setHorizontal(puzzle);
    setVertical(puzzle);
    setDiag(puzzle);
    System.out.println(horizontal);
    System.out.println(vertical);
    System.out.println(diagAscending);
    System.out.println(diagDescending);
  }

  private void setHorizontal(char[][] puzzle){
    horizontal = Stream.of(puzzle).map(String::new).collect(Collectors.toList());
  }

  private void setVertical(char[][] puzzle){
    vertical = new ArrayList<String>();
    for(int x = 0; x < puzzle[0].length; x++){

      StringBuilder sb = new StringBuilder();
      for(int y = 0; y< puzzle.length; y++){
        sb.append(puzzle[y][x]);
      }
      vertical.add(sb.toString());
    }
  }

  private void setDiag(char[][] puzzle){
    diagAscending = new ArrayList<>();
    diagDescending = new ArrayList<>();
    for(int y = 0; y < puzzle.length; y++){
      diagAscending.add(findDiagAscending(0, y, puzzle));
      diagDescending.add(findDiagDescending(0, puzzle.length-1 - y, puzzle));
    }
    for(int x = 1; x < puzzle[0].length; x++){
      diagAscending.add(findDiagAscending(x, puzzle.length-1, puzzle));
      diagDescending.add(findDiagDescending(x, 0, puzzle));
    }
  }

  private String findDiagAscending(int startX, int startY, char[][] puzzle){
    StringBuilder sb = new StringBuilder();
    //each ascending diag adds x and subtracts y each step
    int x = startX;
    int y = startY;
    do {
      sb.append(puzzle[y--][x++]);
    } while (y > -1 && y < puzzle.length && x > -1 && x < puzzle[0].length);
    return sb.toString();
  }

  private String findDiagDescending(int startX, int startY, char[][] puzzle){
    StringBuilder sb = new StringBuilder();
    //each ascending diag adds x and y each step
    int x = startX;
    int y = startY;
    do {
      sb.append(puzzle[y++][x++]);
    } while (y > -1 && y < puzzle.length && x > -1 && x < puzzle[0].length);
    return sb.toString();
  }

  public static Crossword buildCrossword(String s) throws IOException, URISyntaxException{
    Path p = Paths.get(s);
    if(!Files.exists(p)){
      p = Paths.get(Crossword.class.getResource(s).toURI());
    }
    return new Crossword(Files.lines(p).collect(Collectors.toList()));
  }

  public List<String> getHorizontal() {
    return horizontal;
  }

  public List<String> getVertical() {
    return vertical;
  }

  public List<String> getDiagAscending() {
    return diagAscending;
  }

  public List<String> getDiagDescending() {
    return diagDescending;
  }

}
