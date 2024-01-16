import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.sql.Statement;

public class PostLogin extends BothPanels {

    private JButton wylogujButton;
    private JButton dodajSztukeButton;
    private JButton dodajObsadeButton;
    private JButton dodajTerminyButton;
    private JButton nowyAktorButton;
    private JButton nowyRezyserButton;
    private JButton restartButton;

    public PostLogin(TeatrDatabaseApp app) {
        super(app);
        buttonPanelRight = new JPanel(new GridLayout(6, 1));
        
        wylogujButton = new JButton("Wyloguj");
        wylogujButton.addActionListener(e -> mainApp.showPreLoginPanel());
        buttonPanelLeft.add(wylogujButton);

        dodajSztukeButton = new JButton("Dodaj Sztukę");
        dodajObsadeButton = new JButton("Dodaj obsadę do sztuki");
        dodajTerminyButton = new JButton("Ustal terminy dla sztuki");
        nowyAktorButton = new JButton("nowy Aktor");
        nowyRezyserButton = new JButton("nowy reżyser");
        restartButton = new JButton("Restart bazy");

        dodajSztukeButton.addActionListener(e -> dodajSztuke());
        dodajObsadeButton.addActionListener(e -> dodajObsade());
        dodajTerminyButton.addActionListener(e -> noweTerminy());
        nowyAktorButton.addActionListener(e -> nowyAktor());
        nowyRezyserButton.addActionListener(e -> nowyRezyser());
        restartButton.addActionListener(e -> restart());

        buttonPanelRight.add(dodajSztukeButton);
        buttonPanelRight.add(dodajObsadeButton);
        buttonPanelRight.add(dodajTerminyButton);
        buttonPanelRight.add(nowyAktorButton);
        buttonPanelRight.add(nowyRezyserButton);
        buttonPanelRight.add(restartButton);

        add(buttonPanelRight, BorderLayout.EAST);
    }

    private void dodajSztuke() {
        try {
            JPanel panel = new JPanel(new GridLayout(5, 2));
            JTextField tytulField = new JTextField();
            JTextField informatorField = new JTextField();
            String[] dostepniRezyserzy = getAvailableRezyserzy();
            JComboBox<String> rezyserzyComboBox = new JComboBox<>(dostepniRezyserzy);

            panel.add(new JLabel("Tytuł sztuki:"));
            panel.add(tytulField);
            panel.add(new JLabel("Informator:"));
            panel.add(informatorField);
            panel.add(new JLabel("Reżyser:"));
            panel.add(rezyserzyComboBox);

            int result = JOptionPane.showConfirmDialog(this, panel, "Dodaj nową sztukę", JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                String tytul = tytulField.getText();
                String informator = informatorField.getText();
                String rezyser = (String) rezyserzyComboBox.getSelectedItem();
                String[] tmp = rezyser.split(" ");
                String rezyserImie = tmp[0];
                String rezyserNazwisko = tmp[1];

                if (!tytul.isEmpty() && !informator.isEmpty() && !rezyserImie.isEmpty() && !rezyserNazwisko.isEmpty()) {
                    addNewSztuka(tytul, informator, rezyserImie, rezyserNazwisko);
                } else {
                    JOptionPane.showMessageDialog(this, "Wszystkie pola są wymagane.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Błąd podczas dodawania sztuki: " + e.getMessage());
        }
    }

    private void addNewSztuka(String tytul, String informator, String imieRezysera, String nazwiskoRezysera) {
        try {
            DataBase database = new DataBase();
            try {
                database.connect();

                String findRezyserIdQuery = "SELECT id_rezysera FROM Rezyser WHERE imie_rezysera = ? AND nazwisko_rezysera = ?";
                PreparedStatement findRezyserIdStatement = database.getConnection().prepareStatement(findRezyserIdQuery);
                findRezyserIdStatement.setString(1, imieRezysera);
                findRezyserIdStatement.setString(2, nazwiskoRezysera);
                ResultSet rezyserIdResultSet = findRezyserIdStatement.executeQuery();

                if (rezyserIdResultSet.next()) {
                    int idRezysera = rezyserIdResultSet.getInt("id_rezysera");

                    String addSztukaQuery = "INSERT INTO SztukiTeatralne (tytul_sztuki, informator, id_rezysera) VALUES (?, ?, ?)";
                    PreparedStatement addSztukaStatement = database.getConnection().prepareStatement(addSztukaQuery);
                    addSztukaStatement.setString(1, tytul);
                    addSztukaStatement.setString(2, informator);
                    addSztukaStatement.setInt(3, idRezysera);

                    int rowsAffected = addSztukaStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(this, "Dodano nową sztukę do bazy danych.");
                    } else {
                        JOptionPane.showMessageDialog(this, "Nie udało się dodać sztuki do bazy danych.");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Nie znaleziono reżysera o podanych imieniu i nazwisku.");
                }
            } finally {
                database.disconnect();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Błąd podczas łączenia z bazą danych: " + e.getMessage());
        }
    }

    private void dodajObsade() {
        try {
            JPanel panel = new JPanel(new GridLayout(5, 2));

            String[] dostepneTytulySztuk = getAvailableTytulySztuk();
            JComboBox<String> tytulSztukiComboBox = new JComboBox<>(dostepneTytulySztuk);

            // Pobierz listę aktorów z bazy danych
            String[] dostepniAktorzy = getAvailableAktorzy();
            JComboBox<String> aktorzyComboBox = new JComboBox<>(dostepniAktorzy);

            JTextField postacField = new JTextField();

            panel.add(new JLabel("Tytuł sztuki:"));
            panel.add(tytulSztukiComboBox);

            // Dodaj nowy komponent JComboBox dla wyboru aktora z listy
            panel.add(new JLabel("Wybierz aktora:"));
            panel.add(aktorzyComboBox);

            panel.add(new JLabel("Postać:"));
            panel.add(postacField);

            int result = JOptionPane.showConfirmDialog(this, panel, "Dodaj obsadę do sztuki", JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                String tytulSztuki = (String) tytulSztukiComboBox.getSelectedItem();
                String aktor = (String) aktorzyComboBox.getSelectedItem();
                String[] tmp = aktor.split(" ");
                String imie = tmp[0];
                String nazwisko = tmp[1];
                String postac = postacField.getText().trim();

                if (!tytulSztuki.isEmpty() && !aktor.isEmpty() && !postac.isEmpty()) {
                    addNewObsada(tytulSztuki, postac, imie, nazwisko);
                } else {
                    JOptionPane.showMessageDialog(this, "Wszystkie pola muszą być wypełnione.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Błąd SQL podczas dodawania obsady: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Błąd podczas dodawania obsady: " + e.getMessage());
        }
    }


    private void addNewObsada(String tytulSztuki, String postac, String imieAktora, String nazwiskoAktora) {
        try {
            DataBase database = new DataBase();
            try {
                database.connect();

                String findSztukaIdQuery = "SELECT id_sztuki FROM SztukiTeatralne WHERE tytul_sztuki = ?";
                PreparedStatement findSztukaIdStatement = database.getConnection().prepareStatement(findSztukaIdQuery);
                findSztukaIdStatement.setString(1, tytulSztuki);
                ResultSet sztukaIdResultSet = findSztukaIdStatement.executeQuery();

                if (sztukaIdResultSet.next()) {
                    int idSztuki = sztukaIdResultSet.getInt("id_sztuki");

                    String findAktorIdQuery = "SELECT id_aktora FROM Aktorzy WHERE imie = ? AND nazwisko = ?";
                    PreparedStatement findAktorIdStatement = database.getConnection().prepareStatement(findAktorIdQuery);
                    findAktorIdStatement.setString(1, imieAktora);
                    findAktorIdStatement.setString(2, nazwiskoAktora);
                    ResultSet aktorIdResultSet = findAktorIdStatement.executeQuery();

                    if (aktorIdResultSet.next()) {
                        int idAktora = aktorIdResultSet.getInt("id_aktora");

                        String addObsadaQuery = "INSERT INTO ObsadaSztuki (id_sztuki, id_aktora, postac) VALUES (?, ?, ?)";
                        PreparedStatement addObsadaStatement = database.getConnection().prepareStatement(addObsadaQuery);
                        addObsadaStatement.setInt(1, idSztuki);
                        addObsadaStatement.setInt(2, idAktora);
                        addObsadaStatement.setString(3, postac);

                        int rowsAffected = addObsadaStatement.executeUpdate();

                        if (rowsAffected > 0) {
                            JOptionPane.showMessageDialog(this, "Dodano nową obsadę do bazy danych.");
                        } else {
                            JOptionPane.showMessageDialog(this, "Nie udało się dodać obsady do bazy danych.");
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Nie znaleziono aktora o podanych imieniu i nazwisku.");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Nie znaleziono sztuki o podanym tytule.");
                }
            } finally {
                database.disconnect();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Błąd podczas łączenia z bazą danych: " + e.getMessage());
        }
    }

    private String[] getAvailableTytulySztuk() {
        ArrayList<String> tytulySztuk = new ArrayList<>();
        try {
            DataBase database = new DataBase();
            try {
                database.connect();

                String query = "SELECT tytul_sztuki FROM SztukiTeatralne";
                PreparedStatement statement = database.getConnection().prepareStatement(query);
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    String tytulSztuki = resultSet.getString("tytul_sztuki");
                    tytulySztuk.add(tytulSztuki);
                }
            } finally {
                database.disconnect();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Błąd podczas pobierania tytułów sztuk: " + e.getMessage());
        }
        return tytulySztuk.toArray(new String[0]);
    }

    public String[] getAvailableAktorzy() throws SQLException {
        ArrayList<String> aktorzyList = new ArrayList<>();

        try {
            DataBase database = new DataBase();
            try {
                database.connect();

                String query = "SELECT imie, nazwisko FROM Aktorzy";
                PreparedStatement preparedStatement = database.getConnection().prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    String imie = resultSet.getString("imie");
                    String nazwisko = resultSet.getString("nazwisko");
                    String pelneImieNazwisko = imie + " " + nazwisko;
                    aktorzyList.add(pelneImieNazwisko);
                }
            } finally {
                database.disconnect();
            }
        } catch (SQLException e) {
            throw new SQLException("Błąd SQL podczas pobierania dostępnych aktorów: " + e.getMessage());
        }

        return aktorzyList.toArray(new String[0]);
    }

    public String[] getAvailableRezyserzy() throws SQLException {
        ArrayList<String> rezyserzyList = new ArrayList<>();

        try {
            DataBase database = new DataBase();
            try {
                database.connect();

                String query = "SELECT imie_rezysera, nazwisko_rezysera FROM Rezyser";
                PreparedStatement preparedStatement = database.getConnection().prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    String imie = resultSet.getString("imie_rezysera");
                    String nazwisko = resultSet.getString("nazwisko_rezysera");
                    String pelneImieNazwisko = imie + " " + nazwisko;
                    rezyserzyList.add(pelneImieNazwisko);
                }
            } finally {
                database.disconnect();
            }
        } catch (SQLException e) {
            throw new SQLException("Błąd SQL podczas pobierania dostępnych rezyserów: " + e.getMessage());
        }

        return rezyserzyList.toArray(new String[0]);
    }



    private void noweTerminy() {
        try {
            JPanel panel = new JPanel(new GridLayout(6, 2));

            String[] dostepneTytulySztuk = getAvailableTytulySztuk();
            JComboBox<String> tytulSztukiComboBox = new JComboBox<>(dostepneTytulySztuk);

            JTextField dataRealizacjiField = new JTextField();
            JComboBox<String> miejsceComboBox = new JComboBox<>(new String[]{"Teatr Ludowy - Scena Główna", "Teatr Ludowy - Scena Pod Ratuszem", "Teatr Ludowy - Scena Stolarnia"});
            JTextField dostepneBiletyField = new JTextField();
            JTextField cenaUlgowyField = new JTextField();
            JTextField cenaNormalnyField = new JTextField();

            panel.add(new JLabel("Tytuł sztuki:"));
            panel.add(tytulSztukiComboBox);
            panel.add(new JLabel("Data realizacji (RRRR-MM-DD):"));
            panel.add(dataRealizacjiField);
            panel.add(new JLabel("Miejsce realizacji:"));
            panel.add(miejsceComboBox);
            panel.add(new JLabel("Dostępne bilety:"));
            panel.add(dostepneBiletyField);
            panel.add(new JLabel("Cena biletu ulgowego:"));
            panel.add(cenaUlgowyField);
            panel.add(new JLabel("Cena biletu normalnego:"));
            panel.add(cenaNormalnyField);

            int result = JOptionPane.showConfirmDialog(this, panel, "Ustal terminy dla sztuki", JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date parsedDate = dateFormat.parse(dataRealizacjiField.getText());

                String tytulSztuki = (String)tytulSztukiComboBox.getSelectedItem();
                java.sql.Date dataRealizacji = new java.sql.Date(parsedDate.getTime());
                String miejsceRealizacji = (String) miejsceComboBox.getSelectedItem();
                int dostepneBilety = Integer.parseInt(dostepneBiletyField.getText());
                int cenaUlgowy = Integer.parseInt(cenaUlgowyField.getText());
                int cenaNormalny = Integer.parseInt(cenaNormalnyField.getText());

                if (!tytulSztuki.isEmpty() && !miejsceRealizacji.isEmpty()) {
                    addNewTerminy(tytulSztuki, dataRealizacji, miejsceRealizacji, dostepneBilety, cenaUlgowy, cenaNormalny);
                } else {
                    JOptionPane.showMessageDialog(this, "Wszystkie pola muszą być wypełnione.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Błąd podczas ustalania terminów realizacji: " + e.getMessage());
        }
    }

    private void addNewTerminy(String tytulSztuki, java.sql.Date dataRealizacji, String miejsceRealizacji,
                               int dostepneBilety, int cenaUlgowy, int cenaNormalny) {
        try {
            DataBase database = new DataBase();
            try {
                database.connect();

                // Wstawianie danych do tabeli Bilety
                String addBiletyQuery = "INSERT INTO Bilety (dostepne_bilety, cena_ulgowy, cena_normalny) " +
                        "VALUES (?, ?, ?)";
                PreparedStatement addBiletyStatement = database.getConnection().prepareStatement(addBiletyQuery, Statement.RETURN_GENERATED_KEYS);
                addBiletyStatement.setInt(1, dostepneBilety);
                addBiletyStatement.setInt(2, cenaUlgowy);
                addBiletyStatement.setInt(3, cenaNormalny);

                int rowsAffectedBilety = addBiletyStatement.executeUpdate();

                if (rowsAffectedBilety > 0) {
                    // Pobierz wygenerowane klucze główne dla nowo wstawionych rekordów
                    ResultSet generatedKeys = addBiletyStatement.getGeneratedKeys();

                    if (generatedKeys.next()) {
                        int idBiletu = generatedKeys.getInt(1);

                        String addTerminyQuery = "INSERT INTO TerminyRealizacji (id_sztuki, data_realizacji, miejsce_realizacji, bilet_id) " +
                                "VALUES ((SELECT id_sztuki FROM SztukiTeatralne WHERE tytul_sztuki = ?), ?, ?, ?)";
                        PreparedStatement addTerminyStatement = database.getConnection().prepareStatement(addTerminyQuery);
                        addTerminyStatement.setString(1, tytulSztuki);
                        addTerminyStatement.setDate(2, dataRealizacji);
                        addTerminyStatement.setString(3, miejsceRealizacji);
                        addTerminyStatement.setInt(4, idBiletu);

                        int rowsAffectedTerminy = addTerminyStatement.executeUpdate();

                        if (rowsAffectedTerminy > 0) {
                            JOptionPane.showMessageDialog(this, "Dodano nowe terminy realizacji do bazy danych.");
                        } else {
                            JOptionPane.showMessageDialog(this, "Nie udało się dodać terminów realizacji do bazy danych.");
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Błąd podczas uzyskiwania klucza głównego dla Bilety.");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Nie udało się dodać danych do tabeli Bilety.");
                }
            } finally {
                database.disconnect();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Błąd podczas łączenia z bazą danych: " + e.getMessage());
        }
    }


    private void nowyAktor() {
        try {
            JPanel panel = new JPanel(new GridLayout(2, 2));
            JTextField imieField = new JTextField();
            JTextField nazwiskoField = new JTextField();

            panel.add(new JLabel("Imię:"));
            panel.add(imieField);
            panel.add(new JLabel("Nazwisko:"));
            panel.add(nazwiskoField);

            int result = JOptionPane.showConfirmDialog(this, panel, "Dodaj nowego aktora", JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                String imie = imieField.getText();
                String nazwisko = nazwiskoField.getText();

                if (!imie.isEmpty() && !nazwisko.isEmpty()) {
                    addNewActor(imie, nazwisko);
                } else {
                    JOptionPane.showMessageDialog(this, "Imię i nazwisko nie mogą być puste.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Błąd podczas dodawania aktora: " + e.getMessage());
        }
    }

    private void addNewActor(String imie, String nazwisko) {
        try {
            DataBase database = new DataBase();
            try {
                database.connect();

                String query = "INSERT INTO Aktorzy (imie, nazwisko) VALUES (?, ?)";
                PreparedStatement preparedStatement = database.getConnection().prepareStatement(query);
                preparedStatement.setString(1, imie);
                preparedStatement.setString(2, nazwisko);

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Dodano nowego aktora do bazy danych.");
                } else {
                    JOptionPane.showMessageDialog(this, "Nie udało się dodać aktora do bazy danych.");
                }
            } finally {
                database.disconnect();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Błąd podczas łączenia z bazą danych: " + e.getMessage());
        }
    }

    private void nowyRezyser(){
        try {
            JPanel panel = new JPanel(new GridLayout(2, 2));
            JTextField imieField = new JTextField();
            JTextField nazwiskoField = new JTextField();

            panel.add(new JLabel("Imię:"));
            panel.add(imieField);
            panel.add(new JLabel("Nazwisko:"));
            panel.add(nazwiskoField);

            int result = JOptionPane.showConfirmDialog(this, panel, "Dodaj nowego reżysera", JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                String imie = imieField.getText();
                String nazwisko = nazwiskoField.getText();

                if (!imie.isEmpty() && !nazwisko.isEmpty()) {
                    addNewRezyser(imie, nazwisko);
                } else {
                    JOptionPane.showMessageDialog(this, "Imię i nazwisko nie mogą być puste.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Błąd podczas dodawania rezysera: " + e.getMessage());
        }
    }

    private void addNewRezyser(String imie, String nazwisko) {
        try {
            DataBase database = new DataBase();
            try {
                database.connect();

                String query = "INSERT INTO Rezyser (imie_rezysera, nazwisko_rezysera) VALUES (?, ?)";
                PreparedStatement preparedStatement = database.getConnection().prepareStatement(query);
                preparedStatement.setString(1, imie);
                preparedStatement.setString(2, nazwisko);

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Dodano nowego rezysera do bazy danych.");
                } else {
                    JOptionPane.showMessageDialog(this, "Nie udało się dodać rezysera do bazy danych.");
                }
            } finally {
                database.disconnect();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Błąd podczas łączenia z bazą danych: " + e.getMessage());
        }
    }

    private void restart(){
        DataBase db = new DataBase();
        db.delete();
        db.create();
        db.insertData();
        db.addFunctions();
        db.addTriggers();
        db.addView();

    }
}
