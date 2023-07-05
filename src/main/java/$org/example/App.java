package $org.example;

import java.sql.Connection;
import java.sql.DriverManager;
public class App
{
    public static void main(String args[]) {
        Postgres obj1 = new Postgres();
        obj1.connect("testdb", "postgres", "superuser");

    }
}
