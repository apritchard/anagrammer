package com.amp.fun;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.SortedSet;
import java.util.prefs.Preferences;

public class AnagramGui extends JFrame {
  private static final long serialVersionUID = 1L;

  private JPanel contentPane;
  private JTextField textField;
  JList list;
  DefaultListModel listModel;

  Preferences prefs = Preferences.userNodeForPackage(AnagramGui.class);
  Dictionary dict;

  private final String CROSSWORD_MODE = "crossword-mode";
  private final String ALLOW_REPEATS = "allow-repeats";
  private final String DICTIONARY_PATH = "dictionary-path";

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          AnagramGui frame = new AnagramGui();
          frame.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the frame.
   */
  public AnagramGui() {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setBounds(100, 100, 450, 300);

    JMenuBar menuBar = new JMenuBar();
    setJMenuBar(menuBar);

    JMenu mnFile = new JMenu("File");
    menuBar.add(mnFile);

    JMenuItem mntmLoadDictionary = new JMenuItem("Load Dictionary");
    mntmLoadDictionary.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int returnVal = fc.showOpenDialog(AnagramGui.this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                loadDictionary(file);
            }
      }
    });
    mnFile.add(mntmLoadDictionary);

    JMenuItem mntmDefaultDictionary = new JMenuItem("Default Dictionary");
    mntmDefaultDictionary.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        loadDefaultDictionary();
      }
    });
    mnFile.add(mntmDefaultDictionary);
    mnFile.add(new JSeparator());

    JCheckBoxMenuItem mntmAllowRepeats = new JCheckBoxMenuItem("Allow Repeated Characters");
    mntmAllowRepeats.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        prefs.put(ALLOW_REPEATS, Boolean.toString(mntmAllowRepeats.getState()));
      }
    });
    mntmAllowRepeats.setState(prefs.getBoolean(ALLOW_REPEATS, false));
    mnFile.add(mntmAllowRepeats);

    JCheckBoxMenuItem mntmInOrder = new JCheckBoxMenuItem("Crossword Mode");
    mntmInOrder.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        prefs.put(CROSSWORD_MODE, Boolean.toString(mntmInOrder.getState()));
      }
    });
    mntmInOrder.setState(prefs.getBoolean(CROSSWORD_MODE, false));
    mnFile.add(mntmInOrder);

    contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    setContentPane(contentPane);
    contentPane.setLayout(new BorderLayout(0, 0));

    JPanel panel = new JPanel();
    contentPane.add(panel, BorderLayout.NORTH);
    panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

    JLabel lblNewLabel = new JLabel("Word");
    panel.add(lblNewLabel);

    textField = new JTextField();
    textField.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if( e.getKeyCode() == KeyEvent.VK_ENTER ){
          doSearch();
        }
      }
    });
    panel.add(textField);
    textField.setColumns(10);

    JButton btnSearch = new JButton("Search");
    btnSearch.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        doSearch();
      }
    });
    panel.add(btnSearch);

    JScrollPane scrollPane = new JScrollPane();
    contentPane.add(scrollPane, BorderLayout.CENTER);

    listModel = new DefaultListModel();
    list = new JList(listModel);

    scrollPane.setViewportView(list);

    String dictPref = prefs.get(DICTIONARY_PATH,"default");
    if(dictPref.equals("default")){
      loadDefaultDictionary();
    } else {
      loadDictionary(new File(dictPref));
    }

  }

  private void doSearch(){
    SortedSet<Word> words = dict.getContainedWords(textField.getText(),
                                                   prefs.getBoolean(ALLOW_REPEATS, false),
                                                   prefs.getBoolean(CROSSWORD_MODE, false));
    listModel.clear();
    for(Word w:words){
      listModel.addElement(w);
    }
    textField.requestFocus();
    textField.selectAll();
    System.out.println(words.size());
  }

  private void loadDefaultDictionary(){
    System.out.println("Current path: " + System.getProperty("user.dir"));
    InputStream is = this.getClass().getResourceAsStream("/dict2/enable1.txt");
    try {
      dict = Dictionary.buildDictionary(is);
      prefs.put(DICTIONARY_PATH, "default");
    } catch (IOException e) {
      JOptionPane.showMessageDialog(this, "Error loading default dictionary.  Please load a dictionary from the File menu.");
    }
  }

  private void loadDictionary(File f){
    try {
      dict = Dictionary.buildDictionary(f);
      prefs.put(DICTIONARY_PATH, f.getAbsolutePath());
      System.out.println(f.getAbsolutePath());
    } catch (IOException e) {
      JOptionPane.showMessageDialog(this, "Invalid dictionary location.  Please load a dictionary from the File menu.");
      e.printStackTrace();
    }
  }

}
