package szolanc;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GepiJatekos {

    private String name;
    private List<String> words;
    private final boolean DEBUG = true;
    private PrintWriter pw;
    private Scanner sc;
    private final int PORT = 32123;
    private String kapottSzo;
    private List<String> sentWords;

    public GepiJatekos(String name, String file) {
        try {
            this.name = name;
            debug(name);
            words = readFile(file);
            sentWords = new ArrayList<>();
            Socket client = new Socket("localhost", PORT);
            pw = new PrintWriter(client.getOutputStream(), true);
            sc = new Scanner(client.getInputStream());
            pw.println(name);
            pw.flush();
            debug("Nev elkuldve");
        } catch (Exception e) {
            debug("GepiJatekos init hiba");
        }
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    if (process() == 0) {
                        break;
                    }
                }
            }
        }.start();
    }

    private int process() {
        int status = 1;
        String fromServer = sc.nextLine();
        debug("From server: " + fromServer);
        switch (fromServer) {
            case "start":
                //debug("start");
                sendMessage(0);
                break;
            case "nyert":
                //debug("nyert");
                status = 0;
                break;
            case "looser":
                //debug("looser");
                status = 0;
                break;
            case "ok":
                //debug("ok");
                //minden ok
                break;
            case "nok":
                //debug("nok");
                if (kapottSzo != null) {
                    status = calculcateWord(kapottSzo);
                } else {
                    status = calculcateWord();
                }
                break;
            default:
                //debug("default");
                kapottSzo = fromServer;
                System.out.println("Kapott szó felülírva: " + kapottSzo);
                status = calculcateWord(fromServer);
                break;
        }
        return status;
    }

    private int calculcateWord() {
        int status = 1;
        int i = getFirstMatch();
        if (i != -1) {
            sendMessage(i);
        } else {
            pw.println("exit");
            pw.flush();
            status = 0;
        }
        return status;
    }

    private int getFirstMatch() {
        int index = -1;
        for (int i = 0; i < words.size(); i++) {
            if (!sentWords.contains(words.get(i))) {
                index = i;
                break;
            }
        }
        return index;
    }

    private int calculcateWord(String fromServer) {
        //debug("calculcateWord " + fromServer);
        if (fromServer == null) {
            System.err.println("NULL " + name);
        }
        int status = 1;
        int i = getFirstMatch(fromServer);
        //debug("match " + i);
        if (i != -1) {
            sendMessage(i);
        } else {
            //debug("No match, exiting...");
            pw.println("exit");
            pw.flush();
            status = 0;
        }
        return status;
    }

    private int getFirstMatch(String input) {
        int index = -1;
        String last = input.substring(input.length() - 1);
        for (int i = 0; i < words.size(); i++) {
            String first = words.get(i).substring(0, 1);
            if (last.equals(first)) {
                index = i;
                break;
            }
        }
        return index;
    }

    private void sendMessage(int index) {
        String m = words.get(index);
        debug(name + " " + m);
        pw.println(m);
        pw.flush();
        words.remove(index);
        sentWords.add(m);
    }

    private List<String> readFile(String filename) {
        List<String> ls = new ArrayList<>();
        try (Reader reader = new FileReader(filename)) {
            BufferedReader br = new BufferedReader(reader);
            String line;
            while (br.ready()) {
                line = br.readLine();
                ls.add(line);
            }
        } catch (FileNotFoundException e) {
            debug("No such file " + filename);
        } catch (IOException ex) {
            debug("IOException while reading " + filename);
        }
        return ls;
    }

    private void debug(String s) {
        if (DEBUG) {
            System.out.println("GEPI-CLIENT: " + name + " " + s);
        }
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Nem megfelelo szamu parameter");
            return;
        }
        new GepiJatekos(args[0], args[1]);
    }
}
