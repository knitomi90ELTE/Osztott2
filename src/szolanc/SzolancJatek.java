package szolanc;

public class SzolancJatek {

    public static void main(String[] args) {

        TiltottDeploy.main(new String[]{"1"});

        new Thread() {
            @Override
            public void run() {
                GameServer server = new GameServer(32123);
                server.handleClients();
            }
        }.start();

        new Thread() {
            @Override
            public void run() {
                GepiJatekos robot = new GepiJatekos("Robot", "szokincs1.txt");
            }
        }.start();

        new Thread() {
            @Override
            public void run() {
                InteraktivKliens client = new InteraktivKliens();
            }
        }.start();

    }
}
