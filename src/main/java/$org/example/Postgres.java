package $org.example;

import java.sql.Connection;
import java.sql.DriverManager;

public class Postgres {

    private final int piec;

    public Postgres(){
        this.piec=5;
    }

    public void connect(String database, String user, String password){
        Connection c = null;
        String url = "jdbc:postgresql://localhost:5432/" + database;
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection(url, user, password);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        System.out.println("Opened database successfully");
        System.out.println(piec);
    }

    public void insert(String data1, String data2, String data3){

    }
}
