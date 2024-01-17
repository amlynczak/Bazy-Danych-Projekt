import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DataBase {
    private Connection c = null;
    public DataBase() {
        String url = "jdbc:postgresql://balarama.db.elephantsql.com:5432/rfbrrvpp";
        String user = "rfbrrvpp";
        String pass = "mYvNAQeY_1dwl7Vc2UoyNDOAAqr-CK61";

        try {
            c = DriverManager.getConnection(url, user, pass);
            System.out.println("Połączono!");
        } catch (SQLException e) {
            System.out.println("Brak połączenia! \n" + e);
            System.exit(1);
        }
    }
    public Connection getConnection() {
        return c;
    }

    public void disconnect() {
        if (c != null) {
            try {
                c.close();
                System.out.println("Rozłączono!");
            } catch (SQLException e) {
                System.out.println("Błąd podczas zamykania połączenia: \n" + e);
            }
        }
    }

    public void connect() throws SQLException {
        if (c == null || c.isClosed()) {
            String url = "jdbc:postgresql://balarama.db.elephantsql.com:5432/rfbrrvpp";
            String user = "rfbrrvpp";
            String pass = "mYvNAQeY_1dwl7Vc2UoyNDOAAqr-CK61";
            c = DriverManager.getConnection(url, user, pass);
            System.out.println("Połączono!");
        }
    }

    public boolean create() {
        String filePath = "sql/create.sql";
        try {
            connect();

            StringBuilder queryBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    queryBuilder.append(line).append("\n");
                }
            }

            String query = queryBuilder.toString();
            try (Statement statement = c.createStatement()) {
                statement.execute(query);
                System.out.println("Tabela została utworzona.");
                return true;
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
            System.out.println("Błąd podczas tworzenia tabeli: \n" + e.getMessage());
            return false;
        } finally {
            disconnect();
        }
    }

    public boolean insertData() {
        String filePath = "sql/data.sql";
        try {
            connect();

            StringBuilder queryBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    queryBuilder.append(line).append("\n");
                }
            }

            String query = queryBuilder.toString();
            try (Statement statement = c.createStatement()) {
                statement.execute(query);
                System.out.println("Dane zostały wprowadzone.");
                return true;
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
            System.out.println("Błąd podczas dodawania danych: \n" + e.getMessage());
            return false;
        } finally {
            disconnect();
        }
    }

    public boolean addFunctions(){
        String filePath = "sql/funkcje.sql";
        try{
            connect();

            StringBuilder queryBuilder = new StringBuilder();
            try(BufferedReader reader = new BufferedReader(new FileReader(filePath))){
                String line;
                while((line=reader.readLine()) != null){
                    queryBuilder.append(line).append("\n");
                }
            }

            String query = queryBuilder.toString();
            try (Statement statement = c.createStatement()) {
                statement.execute(query);
                System.out.println("Dodano funkcje.");
                return true;
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
            System.out.println("Błąd podczas dodawania funkcji: \n" + e.getMessage());
            return false;
        } finally {
            disconnect();
        }
    }

    public boolean addTriggers(){
        String filePath = "sql/trigger.sql";
        try{
            connect();

            StringBuilder queryBuilder = new StringBuilder();
            try(BufferedReader reader = new BufferedReader(new FileReader(filePath))){
                String line;
                while((line=reader.readLine()) != null){
                    queryBuilder.append(line).append("\n");
                }
            }

            String query = queryBuilder.toString();
            try (Statement statement = c.createStatement()) {
                statement.execute(query);
                System.out.println("Dodano trigger.");
                return true;
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
            System.out.println("Błąd podczas dodawania triggera: \n" + e.getMessage());
            return false;
        } finally {
            disconnect();
        }
    }

    public boolean addView(){
        String filePath = "sql/widoki.sql";
        try{
            connect();

            StringBuilder queryBuilder = new StringBuilder();
            try(BufferedReader reader = new BufferedReader(new FileReader(filePath))){
                String line;
                while((line=reader.readLine()) != null){
                    queryBuilder.append(line).append("\n");
                }
            }

            String query = queryBuilder.toString();
            try (Statement statement = c.createStatement()) {
                statement.execute(query);
                System.out.println("Dodano widoki.");
                return true;
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
            System.out.println("Błąd podczas dodawania widoków: \n" + e.getMessage());
            return false;
        } finally {
            disconnect();
        }
    }

    public boolean delete() {
        String filePath = "sql/deleteDB.sql";
        try {
            connect();

            StringBuilder queryBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    queryBuilder.append(line).append("\n");
                }
            }

            String query = queryBuilder.toString();
            try (Statement statement = c.createStatement()) {
                statement.execute(query);
                System.out.println("Usunieto.");
                return true;
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
            System.out.println("Błąd podczas usuwania danych: \n" + e.getMessage());
            return false;
        } finally {
            disconnect();
        }
    }
}
