package src;

public class TestMain {
    public static void main(String[] args) throws Exception {
        Nao naoTyrone = new Nao();

        naoTyrone.verbind();
        naoTyrone.talking("Verbonden");
        naoTyrone.volume(50);
        naoTyrone.setLanguage("Dutch");
        naoTyrone.bepaalOogKleur("white",0.5f);
        Thread.sleep(2000);
//        naoTyrone.waitForPeople();

        new Thread(new Nao.checkPoints(naoTyrone)).start();
        new Thread(new Nao.randomEyes(naoTyrone)).start();

        naoTyrone.touchButton("Front");
        naoTyrone.touchButton("Middle");
        naoTyrone.touchButton("Rear");

        naoTyrone.doWhile(1000, 10000);
    }
}
