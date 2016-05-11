package szolanc;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class TiltottDeploy {

    private static Registry reg;

    public TiltottDeploy(int n) {
        for (int i = 1; i <= n; i++) {
            String s_name = "tiltott" + i;
            try {
                reg.rebind(s_name, new TiltottSzerver(s_name));
            } catch (RemoteException ex) {
                System.out.println("Hiba a " + s_name + " szerver bejegyzésekor.");
                ex.printStackTrace();
            }
            System.out.println(s_name + " létrehozva.");
        }
    }

    public static void main(String[] args) {
        if (!validParameter(args)) {
            System.out.println("Incorrect args!");
            return;
        }
        try {
            reg = LocateRegistry.createRegistry(12345);
        } catch (RemoteException ex) {
            System.out.println("Hiba a registry létrehozásakor.");
            ex.printStackTrace();
        }
        int n = Integer.parseInt(args[0]);
        new TiltottDeploy(n);
    }

    private static boolean validParameter(String[] t) {
        return t.length == 1 && Integer.parseInt(t[0]) > 0;
    }
}
