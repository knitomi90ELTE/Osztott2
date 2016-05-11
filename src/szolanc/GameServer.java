package szolanc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class GameServer {

    private static final int TIMEOUT = 30000;
    private ServerSocket server;
    private int count = 1;
    private boolean logging = true;
    private int c = 1;

    public GameServer(int port) {
        try {
            server = new ServerSocket(port);
            server.setSoTimeout(TIMEOUT);
            serverLog("A szerver elindult a " + port + " porton", 2);
        } catch (Exception e) {
            serverLog("Hiba a szerver inditasanal.", 2);
        }
    }

    private void serverLog(String s, int handlerID) {
        if (logging && handlerID == 2) {
            System.out.println("SERVER-LOG: " + s);
        }
    }

    public void handleClients() {
        while (true) {
            try {
                Socket s1 = server.accept();
                Socket s2 = server.accept();
                Registry r = LocateRegistry.getRegistry();
                TiltInf tsz = null;
                try {
                    tsz = (TiltInf) Naming.lookup("rmi://localhost:12345/tiltott" + count);
                    //Módosítva NotBoundException-re
                    //8 kliensel szépen látszik, hogy 1-2-1-2 a kiosztás
                } catch (NotBoundException e) {
                    serverLog("setting couter to 1", 2);
                    count = 1;
                    try {
                        tsz = (TiltInf) Naming.lookup("rmi://localhost:12345/tiltott" + count);
                    } catch (NotBoundException ex) {
                        serverLog("Hiba az RMI létrehozásakor.", 2);
                    }
                }
                serverLog("contacted to tiltott" + count, 2);
                new Handler(s1, s2, tsz, c).start();
                c++;
                count++;
            } catch (IOException e) {
                serverLog("Hiba a kliensek fogadasakor vagy timeout.", 2);
                e.printStackTrace();
                break;
            }
        }
    }

    private class Handler extends Thread {

        private final Player player1;
        private final Player player2;
        private final File logFile;
        private final Writer logWriter;
        private final TiltInf tsz;
        private int handlerID;

        public Handler(Socket s1, Socket s2, TiltInf tsz, int id) throws IOException {
            this.player1 = new Player(s1);
            this.player2 = new Player(s2);
            this.tsz = tsz;
            this.handlerID = id;
            serverLog("connected " + player1.name + " " + player2.name + " " + handlerID, handlerID);
            String s = player1.name + "_" + player2.name + "_" + getTimeStamp() + ".txt";
            this.logFile = new File(s);
            logFile.createNewFile();
            logWriter = new FileWriter(logFile);
        }

        private String getTimeStamp() {
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd h-mm-ss");
            String formattedDate = sdf.format(date);
            return formattedDate;
        }

        private void logToFile(String log) {
            try {
                BufferedWriter bw = new BufferedWriter(logWriter);
                bw.write(log);
                bw.newLine();
                bw.flush();
            } catch (IOException e) {
                serverLog("Hiba a logoláskor.", handlerID);
            }

        }

        @Override
        public void run() {
            Player playerOnTurn = player1;
            try {
                playerOnTurn.sendMessage("start");
                while (true) {
                    String s = playerOnTurn.getMessage();
                    serverLog(playerOnTurn.name + " kuldte: " + s, handlerID);
                    while (tsz.tiltottE(s) && !"exit".equals(s)) {
                        playerOnTurn.sendMessage("nok");
                        s = playerOnTurn.getMessage();
                    }
                    playerOnTurn.sendMessage("ok");
                    if (s.equals("exit")) {
                        playerOnTurn.sendMessage("looser");
                        playerOnTurn = (playerOnTurn.equals(player1)) ? player2 : player1;
                        playerOnTurn.sendMessage("nyert");
                        break;
                    }
                    logToFile(playerOnTurn.name + " " + s);
                    playerOnTurn = (playerOnTurn.equals(player1)) ? player2 : player1;
                    playerOnTurn.sendMessage(s);
                    serverLog("A kuldott ertek: " + s, handlerID);
                }
                player1.closeConnection();
                player2.closeConnection();
            } catch (IOException e) {
                serverLog("Hiba a klienessel valo kommunikacioban.", handlerID);
            }
        }
    }

    private class Player {

        private final Socket socket;
        private final String name;
        private final PrintWriter pw;
        private final Scanner sc;

        public Player(Socket socket) throws IOException {
            this.socket = socket;
            pw = new PrintWriter(socket.getOutputStream(), true);
            sc = new Scanner(socket.getInputStream());
            this.name = sc.nextLine();
        }

        public void sendMessage(String s) {
            pw.println(s);
        }

        public String getMessage() {
            return sc.nextLine();
        }

        public void closeConnection() throws IOException {
            socket.close();
        }
    }

    public static void main(String[] args) throws RemoteException, NotBoundException {
        GameServer server = new GameServer(32123);
        server.handleClients();
    }
}
