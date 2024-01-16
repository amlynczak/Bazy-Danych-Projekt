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

public class PostLogin extends JPanel {

    private TeatrDatabaseApp mainApp;
    private JButton wylogujButton;
    private JButton pobierzHarmonogramButton;
    private JButton zobaczRezyseraButton;
    private JButton zobaczAktoraButton;
    private JButton dodajSztukeButton;
    private JButton dodajObsadeButton;
    private JButton dodajTerminyButton;
    private JButton nowyAktorButton;
    private JButton nowyRezyserButton;
    private JButton restartButton;
    private JTable sztukiTable;
    private ArrayList<Aktor> aktorzy;
    private ArrayList<Rezyser> rezyserzy;
    private ArrayList<SztukaTeatralna> sztuki;

    public PostLogin(TeatrDatabaseApp app) {
        mainApp = app;
        setLayout(new BorderLayout());

        JPanel buttonPanelLeft = new JPanel(new GridLayout(4, 1));
        JPanel buttonPanelRight = new JPanel(new GridLayout(6, 1));
        pobierzHarmonogramButton = new JButton("Pobierz Harmonogram");
        zobaczAktoraButton = new JButton("Zobacz Aktorów");
        zobaczRezyseraButton = new JButton("Zobacz Rezyserów");
        wylogujButton = new JButton("Wyloguj");

        dodajSztukeButton = new JButton("Dodaj Sztukę");
        dodajObsadeButton = new JButton("Dodaj obsadę do sztuki");
        dodajTerminyButton = new JButton("Ustal terminy dla sztuki");
        nowyAktorButton = new JButton("nowy Aktor");
        nowyRezyserButton = new JButton("nowy reżyser");
        restartButton = new JButton("Restart bazy");

        pobierzHarmonogramButton.addActionListener(e -> pobierzHarmonogram());
        zobaczAktoraButton.addActionListener(e -> zobaczAktora());
        zobaczRezyseraButton.addActionListener(e -> zobaczRezyser());
        wylogujButton.addActionListener(e -> mainApp.showPreLoginPanel());

        dodajSztukeButton.addActionListener(e -> dodajSztuke());
        dodajObsadeButton.addActionListener(e -> dodajObsade());
        dodajTerminyButton.addActionListener(e -> noweTerminy());
        nowyAktorButton.addActionListener(e -> nowyAktor());
        nowyRezyserButton.addActionListener(e -> nowyRezyser());
        restartButton.addActionListener(e -> restart());

        buttonPanelLeft.add(pobierzHarmonogramButton);
        buttonPanelLeft.add(zobaczAktoraButton);
        buttonPanelLeft.add(zobaczRezyseraButton);
        buttonPanelLeft.add(wylogujButton);

        buttonPanelRight.add(dodajSztukeButton);
        buttonPanelRight.add(dodajObsadeButton);
        buttonPanelRight.add(dodajTerminyButton);
        buttonPanelRight.add(nowyAktorButton);
        buttonPanelRight.add(nowyRezyserButton);
        buttonPanelRight.add(restartButton);

        sztukiTable = new JTable();
        JScrollPane tableScrollPane = new JScrollPane(sztukiTable);

        add(buttonPanelLeft, BorderLayout.WEST);
        add(buttonPanelRight, BorderLayout.EAST);
        add(tableScrollPane, BorderLayout.CENTER);
    }

    private void pobierzHarmonogram() {
        try {
            DataBase database = new DataBase();
            sztuki = new ArrayList<>();
            try {
                database.connect();

                String query = "SELECT * FROM Harmonogram_app";
                PreparedStatement preparedStatement = database.getConnection().prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery();

                DefaultTableModel model = new DefaultTableModel() {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return column == 4;
                    }
                };
                model.addColumn("Tytuł");
                model.addColumn("Reżyser");
                model.addColumn("Data");
                model.addColumn("Miejsce");
                model.addColumn("Informator");

                while (resultSet.next()) {
                    int id = resultSet.getInt("id_sztuki");
                    String tytul = resultSet.getString("tytul_sztuki");
                    String informator = resultSet.getString("informator");
                    String imieRezysera = resultSet.getString("imie_rezysera");
                    String nazwiskoRezysera = resultSet.getString("nazwisko_rezysera");
                    String d = resultSet.getString("data_realizacji");
                    String miejsce = resultSet.getString("miejsce_realizacji");

                    SztukaTeatralna sztuka = new SztukaTeatralna(id, tytul, informator, new Rezyser(imieRezysera, nazwiskoRezysera, 0), d, miejsce);
                    sztuki.add(sztuka);

                    model.addRow(new Object[]{tytul, imieRezysera + " " + nazwiskoRezysera, d, miejsce, "Informator"});
                }

                sztukiTable.setModel(model);

                sztukiTable.getColumnModel().getColumn(4).setCellRenderer(new PostLogin.ButtonRenderer());
                sztukiTable.getColumnModel().getColumn(4).setCellEditor(new PostLogin.ButtonEditorInformator(new JCheckBox()));

            } finally {
                database.disconnect();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error retrieving schedule information: " + e.getMessage());
        }
    }

    private class ButtonEditorInformator extends DefaultCellEditor {
        private JButton button;

        public ButtonEditorInformator(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton("Informator");
            button.addActionListener(e -> {
                int selectedRow = sztukiTable.getSelectedRow();
                if (selectedRow != -1) {
                    showInformatorInfo(sztuki.get(selectedRow));
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return button;
        }
    }

    private void showInformatorInfo(SztukaTeatralna sztuka) {
        try {
            DataBase database = new DataBase();
            try {
                database.connect();

                int id_sztuki = sztuka.getId();
                String query = "SELECT * FROM SztukaPoId(?)";
                PreparedStatement preparedStatement = database.getConnection().prepareStatement(query);
                preparedStatement.setInt(1, id_sztuki);
                ResultSet resultSet = preparedStatement.executeQuery();

                StringBuilder message = new StringBuilder();
                message.append("Informacje o sztuce: \n")
                        .append("tytul: ").append(sztuka.getTytul()).append("\n")
                        .append(sztuka.getInformator()).append("\n")
                        .append("rezyser: ").append(sztuka.getRezyser().toString()).append("\n")
                        .append("miejsce realizacji: ").append(sztuka.getMiejsce()).append("\n")
                        .append("data: ").append(sztuka.getData()).append("\n")
                        .append("\nObsada\n");

                database.disconnect();
                database.connect();

                query = "SELECT * FROM ObsadaPoId(?)";
                preparedStatement = database.getConnection().prepareStatement(query);
                preparedStatement.setInt(1, id_sztuki);
                ResultSet resultSet2 = preparedStatement.executeQuery();

                while (resultSet2.next()) {
                    String postac = resultSet2.getString("postac");
                    String imie_aktora = resultSet2.getString("imie_aktora");
                    String nazwisko_aktora = resultSet2.getString("nazwisko_aktora");

                    message.append("Postać: ").append(postac).append(", Grana przez: ").append(imie_aktora).append(" ").append(nazwisko_aktora).append("\n");
                }

                JPanel panel = new JPanel(new BorderLayout());
                JTextArea textArea = new JTextArea(message.toString());
                textArea.setEditable(false);
                panel.add(new JScrollPane(textArea), BorderLayout.CENTER);

                JButton purchaseButton = new JButton("Zakup Biletów");
                panel.add(purchaseButton, BorderLayout.SOUTH);
                purchaseButton.addActionListener(e -> purchaseTickets(id_sztuki));

                JOptionPane.showMessageDialog(this, panel, "Informator", JOptionPane.INFORMATION_MESSAGE);

            } finally {
                database.disconnect();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error retrieving informator information: " + e.getMessage());
        }
    }

    private void purchaseTickets(int id_sztuki) {
        String input = JOptionPane.showInputDialog(this, "Ile biletów chcesz kupić?");
        try {
            int numberOfTickets = Integer.parseInt(input);
            JOptionPane.showMessageDialog(this, "Zakupiono " + numberOfTickets + " biletów dla sztuki o ID: " + id_sztuki);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Wprowadź poprawną liczbę biletów.");
        }
    }

    private void zobaczAktora() {
        try {
            DataBase database = new DataBase();

            aktorzy = new ArrayList<>();
            try {
                database.connect();

                String query = "SELECT * FROM Aktorzy_app";

                PreparedStatement preparedStatement = database.getConnection().prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery();

                DefaultTableModel model = new DefaultTableModel() {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return column == 2;
                    }
                };
                model.addColumn("Imię");
                model.addColumn("Nazwisko");
                model.addColumn("Informacje o aktorze");

                while (resultSet.next()) {
                    int id = resultSet.getInt("id_aktora");
                    String name = resultSet.getString("imie");
                    String lname = resultSet.getString("nazwisko");

                    Aktor aktor = new Aktor(name, lname, id);
                    aktorzy.add(aktor);
                }

                for (Aktor data : aktorzy) {
                    model.addRow(new Object[]{data.getImie(), data.getNazwisko(), "Informacje o aktorze"});
                }

                sztukiTable.setModel(model);
                sztukiTable.getColumnModel().getColumn(2).setCellRenderer(new PostLogin.ButtonRenderer());
                sztukiTable.getColumnModel().getColumn(2).setCellEditor(new PostLogin.ButtonEditorAktor(new JCheckBox()));

            } finally {
                database.disconnect();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Błąd podczas pobierania listy aktorów: " + e.getMessage());
        }
    }

    private class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    private class ButtonEditorAktor extends DefaultCellEditor {
        private JButton button;

        public ButtonEditorAktor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton("Informacje");
            button.addActionListener(e -> {
                int selectedRow = sztukiTable.getSelectedRow();
                if (selectedRow != -1) {
                    showAktorInfo(aktorzy.get(selectedRow));
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return button;
        }
    }

    private class ButtonEditorRezyser extends DefaultCellEditor {
        private JButton button;

        public ButtonEditorRezyser(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton("Informacje");
            button.addActionListener(e -> {
                int selectedRow = sztukiTable.getSelectedRow();
                if (selectedRow != -1) {
                    showRezyserInfo(rezyserzy.get(selectedRow));
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return button;
        }
    }
    private void showAktorInfo(Aktor aktor) {
        try {
            DataBase database = new DataBase();
            try {
                database.connect();

                String query = "SELECT * FROM GetPlaysForActor(?)";
                PreparedStatement preparedStatement = database.getConnection().prepareStatement(query);
                preparedStatement.setInt(1, aktor.getId());
                ResultSet resultSet = preparedStatement.executeQuery();

                StringBuilder message = new StringBuilder();
                message.append("Pełne informacje o aktorze:\n")
                        .append("Imię: ").append(aktor.getImie()).append("\n")
                        .append("Nazwisko: ").append(aktor.getNazwisko()).append("\n\n")
                        .append("Występuje w:\n");

                while (resultSet.next()) {
                    String playTitle = resultSet.getString("play_title");
                    String characterPlayed = resultSet.getString("character_played");

                    message.append(playTitle).append(", ")
                            .append("grana postać: ").append(characterPlayed).append("\n\n");
                }

                JOptionPane.showMessageDialog(this, message.toString(), "Actor Information", JOptionPane.INFORMATION_MESSAGE);

            } finally {
                database.disconnect();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error retrieving actor information: " + e.getMessage());
        }
    }

    private void zobaczRezyser() {
        try {
            DataBase database = new DataBase();

            rezyserzy = new ArrayList<>();
            try {
                database.connect();

                String query = "SELECT * FROM Rezyser_app";

                PreparedStatement preparedStatement = database.getConnection().prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery();

                DefaultTableModel model = new DefaultTableModel() {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return column == 2;
                    }
                };
                model.addColumn("Imię");
                model.addColumn("Nazwisko");
                model.addColumn("Informacje o reżyserze");

                while (resultSet.next()) {
                    int id = resultSet.getInt("id_rezysera");
                    String name = resultSet.getString("imie_rezysera");
                    String lname = resultSet.getString("nazwisko_rezysera");

                    Rezyser rez = new Rezyser(name, lname, id);
                    rezyserzy.add(rez);
                }

                for(Rezyser data: rezyserzy){
                    model.addRow(new Object[]{data.getImie(), data.getNazwisko(), "Informacje o rezyserze"});
                }

                sztukiTable.setModel(model);

                sztukiTable.getColumnModel().getColumn(2).setCellRenderer(new PostLogin.ButtonRenderer());
                sztukiTable.getColumnModel().getColumn(2).setCellEditor(new PostLogin.ButtonEditorRezyser(new JCheckBox()));
            } finally {
                database.disconnect();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Błąd podczas pobierania listy rezyserow: " + e.getMessage());
        }
    }

    private void showRezyserInfo(Rezyser rezyser) {
        try {
            DataBase database = new DataBase();
            try {
                database.connect();

                String query = "SELECT * FROM SztukiDanegoRezysera(?)";
                PreparedStatement preparedStatement = database.getConnection().prepareStatement(query);
                preparedStatement.setInt(1, rezyser.getId());
                ResultSet resultSet = preparedStatement.executeQuery();

                StringBuilder message = new StringBuilder();
                message.append("Informacje o reżyserze:\n")
                        .append("Imię: ").append(rezyser.getImie()).append("\n")
                        .append("Nazwisko: ").append(rezyser.getNazwisko()).append("\n\n")
                        .append("Sztuki w jego reżyserii:\n");

                while (resultSet.next()) {
                    String playTitle = resultSet.getString("tytul_sztuki");

                    message.append(playTitle).append("\n\n");
                }

                JOptionPane.showMessageDialog(this, message.toString(), "Director Information", JOptionPane.INFORMATION_MESSAGE);

            } finally {
                database.disconnect();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error retrieving director information: " + e.getMessage());
        }
    }

    private void dodajSztuke() {
        try {
            JPanel panel = new JPanel(new GridLayout(5, 2));
            JTextField tytulField = new JTextField();
            JTextField informatorField = new JTextField();
            JTextField rezyserImieField = new JTextField();
            JTextField rezyserNazwiskoField = new JTextField();

            panel.add(new JLabel("Tytuł sztuki:"));
            panel.add(tytulField);
            panel.add(new JLabel("Informator:"));
            panel.add(informatorField);
            panel.add(new JLabel("Imię reżysera:"));
            panel.add(rezyserImieField);
            panel.add(new JLabel("Nazwisko reżysera:"));
            panel.add(rezyserNazwiskoField);

            int result = JOptionPane.showConfirmDialog(this, panel, "Dodaj nową sztukę", JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                String tytul = tytulField.getText();
                String informator = informatorField.getText();
                String rezyserImie = rezyserImieField.getText();
                String rezyserNazwisko = rezyserNazwiskoField.getText();

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
            JPanel panel = new JPanel(new GridLayout(4, 2));
            JTextField tytulSztukiField = new JTextField();
            JTextField postacField = new JTextField();
            JTextField imieAktoraField = new JTextField();
            JTextField nazwiskoAktoraField = new JTextField();

            panel.add(new JLabel("Tytuł sztuki:"));
            panel.add(tytulSztukiField);
            panel.add(new JLabel("Postać:"));
            panel.add(postacField);
            panel.add(new JLabel("Imię aktora:"));
            panel.add(imieAktoraField);
            panel.add(new JLabel("Nazwisko aktora:"));
            panel.add(nazwiskoAktoraField);

            int result = JOptionPane.showConfirmDialog(this, panel, "Dodaj obsadę do sztuki", JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                String tytulSztuki = tytulSztukiField.getText();
                String postac = postacField.getText();
                String imieAktora = imieAktoraField.getText();
                String nazwiskoAktora = nazwiskoAktoraField.getText();

                if (!tytulSztuki.isEmpty() && !postac.isEmpty() && !imieAktora.isEmpty() && !nazwiskoAktora.isEmpty()) {
                    addNewObsada(tytulSztuki, postac, imieAktora, nazwiskoAktora);
                } else {
                    JOptionPane.showMessageDialog(this, "Wszystkie pola muszą być wypełnione.");
                }
            }
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
