package szolanc;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class TiltottSzerver extends UnicastRemoteObject implements TiltInf {

    private List<String> tiltottSzavak; //TODO: volatile?

    public TiltottSzerver(String name) throws RemoteException {
        super();//hozzáadva
        tiltottSzavak = readFile(name + ".txt");
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
            System.out.println("No such file " + filename);
        } catch (IOException ex) {
            System.out.println("IOException while reading " + filename);
        }
        return ls;
    }
    
    //módosítva synchronized-ra, szükségesnek érzem, hogy egyszerre csak egy szál módosíthassa a listát
    @Override
    public synchronized boolean tiltottE(String szo) throws RemoteException {
        if (!tiltottSzavak.contains(szo)) {
            tiltottSzavak.add(szo);
            return false;
        }
        return true;
    }

}
