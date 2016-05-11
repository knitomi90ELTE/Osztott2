package szolanc;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
//import java.util.ArrayList;
//import java.util.List;
import java.util.Scanner;

public class InteraktivKliens {

    private String name;
    private final int PORT = 32123;
    private final boolean debug = false;
    private PrintWriter pw;
    private Scanner serverOutput;
    private Scanner userInput;
    //private List<String> words;
    private String kapottSzo;

    public InteraktivKliens() {
        try {
            //words = new ArrayList<>();
            Socket s = new Socket("localhost", PORT);
            pw = new PrintWriter(s.getOutputStream(), true);
            serverOutput = new Scanner(s.getInputStream());
            userInput = new Scanner(System.in);
            System.out.println("USERCL-LOG: Adja meg a nevet");
            this.name = userInput.nextLine();
            pw.println(name);
        } catch (IOException ex) {
            System.out.println("USERCL-LOG: init hiba");
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
        String fromServer;
        try {
            fromServer = serverOutput.nextLine();
        } catch (Exception e) {
            //Ez szépen lekezeli, ha a szerver leállt.
            System.out.println("USERCL-LOG: hiba az üzenet olvasasakor.");
            return 0;
        }
        System.out.println("USERCL-LOG: " + fromServer);
        switch (fromServer) {
            case "nyert":
                System.out.println("USERCL-LOG: " + name + " nyert");
                status = 0;
                break;
            case "looser":
                status = 0;
                break;
            case "ok":
                break;
            case "nok":
                status = calculateMessage(kapottSzo);
                break;
            default:
                kapottSzo = fromServer;
                status = calculateMessage(fromServer);
                break;
        }
        return status;
    }

    private int calculateMessage(String szo) {
        int status = 1;
        debug("USERCL-LOG: Irjon be egy szot!");
        String input = userInput.nextLine();
        if (!input.equals("exit") && !szo.equals("start")) {
            while (isWrongInput(input, szo)) {
                input = userInput.nextLine();
                if ("exit".equals(input)) {
                    break;
                }
            }
            //words.add(input);
        }
        pw.println(input);
        debug("USERCL-LOG: Szo elkuldve, varakozas...");
        return status;
    }

    private boolean isWrongInput(String input, String fromServer) {
        boolean b = false;
        if (input.length() == 0) {
            System.out.println("ures szo");
            b = true;
        }
        /*
        if (words.contains(input)) {
            System.out.println("mar mondtad");
            b = true;
        }
         */
        if (fromServer.charAt(fromServer.length() - 1) != input.charAt(0)) {
            System.out.println("nem stimmel a karakter");
            b = true;
        }

        if (!input.chars().allMatch(x -> Character.isLetter(x))) {
            System.out.println("nem csupa betu");
            b = true;
        }
        return b;
    }

    private void debug(String s) {
        if (debug) {
            System.out.println(s);
        }
    }

    public static void main(String[] args) {
        new InteraktivKliens();
    }
}
