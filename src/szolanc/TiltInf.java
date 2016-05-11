package szolanc;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface TiltInf extends Remote {

    public boolean tiltottE(String szo) throws RemoteException;
    
}
