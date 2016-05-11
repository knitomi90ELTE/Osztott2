package szolanc;

public class SzolancSzimulacio {

    public static void main(String[] args) throws InterruptedException {

        TiltottDeploy.main(new String[]{"2"});
        Thread.sleep(100);
        new Thread() {
            @Override
            public void run() {
                GameServer server = new GameServer(32123);
                server.handleClients();
            }
        }.start();
        Thread.sleep(100);
        new Thread() {
            @Override
            public void run() {
                GepiJatekos jatekos1 = new GepiJatekos("Jatekos1", "szokincs1.txt");
            }
        }.start();
        Thread.sleep(100);
        new Thread() {
            @Override
            public void run() {
                GepiJatekos jatekos2 = new GepiJatekos("Jatekos2", "szokincs1.txt");
            }
        }.start();
        Thread.sleep(100);
        new Thread() {
            @Override
            public void run() {
                GepiJatekos jatekos3 = new GepiJatekos("Jatekos3", "szokincs1.txt");
            }
        }.start();
        Thread.sleep(100);
        new Thread() {
            @Override
            public void run() {
                GepiJatekos jatekos4 = new GepiJatekos("Jatekos4", "szokincs2.txt");
            }
        }.start();
        /*new Thread() {
            @Override
            public void run() {
                GepiJatekos jatekos1 = new GepiJatekos("Jatekos5", "szokincs1.txt");
            }
        }.start();

        new Thread() {
            @Override
            public void run() {
                GepiJatekos jatekos2 = new GepiJatekos("Jatekos6", "szokincs1.txt");
            }
        }.start();

        new Thread() {
            @Override
            public void run() {
                GepiJatekos jatekos3 = new GepiJatekos("Jatekos7", "szokincs1.txt");
            }
        }.start();

        new Thread() {
            @Override
            public void run() {
                GepiJatekos jatekos4 = new GepiJatekos("Jatekos8", "szokincs2.txt");
            }
        }.start();*/
    }

}
