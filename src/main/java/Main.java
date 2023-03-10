import program.Session;

public class Main {

    public static void main(String[] args) {

        Session session;
        if ("-databaseFileName".equals(args[0])) {
            session = new Session(args[1]);
        } else {
            session = new Session("public");
        }
        session.start();
    }
}