import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PostLogin extends JPanel {

    private TeatrDatabaseApp mainApp;
    private JButton wylogujButton;
    private JButton pobierzHarmonogramButton;
    private JButton zobaczRezyseraButton;
    private JButton zobaczAktoraButton;
    private JButton dodajSztukeButton;
    private JButton dodajObsadeButton;
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

        // Przyciski w pionie
        JPanel buttonPanelLeft = new JPanel(new GridLayout(4, 1));
        JPanel buttonPanelRight = new JPanel(new GridLayout(5, 1));
        pobierzHarmonogramButton = new JButton("Pobierz Harmonogram");
        zobaczAktoraButton = new JButton("Zobacz Aktorów");
        zobaczRezyseraButton = new JButton("Zobacz Rezyserów");
        wylogujButton = new JButton("Wyloguj");

        dodajSztukeButton = new JButton("Dodaj Sztukę");
        dodajObsadeButton = new JButton("Dodaj obsadę do sztuki");
        nowyAktorButton = new JButton("nowy Aktor");
        nowyRezyserButton = new JButton("nowy reżyser");
        restartButton = new JButton("Restart bazy");


        pobierzHarmonogramButton.addActionListener(e -> pobierzHarmonogram());
        zobaczAktoraButton.addActionListener(e -> zobaczAktora());
        zobaczRezyseraButton.addActionListener(e -> zobaczRezyser());
        wylogujButton.addActionListener(e -> mainApp.showPreLoginPanel());

        dodajSztukeButton.addActionListener(e -> dodajSztuke());
        dodajObsadeButton.addActionListener(e -> dodajObsade());
        nowyAktorButton.addActionListener(e -> nowyAktor());
        nowyRezyserButton.addActionListener(e -> nowyRezyser());
        restartButton.addActionListener(e -> restart());

        buttonPanelLeft.add(pobierzHarmonogramButton);
        buttonPanelLeft.add(zobaczAktoraButton);
        buttonPanelLeft.add(zobaczRezyseraButton);
        buttonPanelLeft.add(wylogujButton);

        buttonPanelRight.add(dodajSztukeButton);
        buttonPanelRight.add(dodajObsadeButton);
        buttonPanelRight.add(nowyAktorButton);
        buttonPanelRight.add(nowyRezyserButton);
        buttonPanelRight.add(restartButton);

        // Tabela dla informacji
        sztukiTable = new JTable();
        JScrollPane tableScrollPane = new JScrollPane(sztukiTable);

        // Dodanie przycisków i tabeli do panelu
        add(buttonPanelLeft, BorderLayout.WEST);
        add(buttonPanelRight, BorderLayout.EAST);
        add(tableScrollPane, BorderLayout.CENTER);
    }

    private void pobierzHarmonogram() {
        try {
            DataBase database = new DataBase();
            sztuki = new ArrayList<>(); // Initialize the sztuki list
            try {
                database.connect();

                String query = "SELECT * FROM Harmonogram_app";
                PreparedStatement preparedStatement = database.getConnection().prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery();

                DefaultTableModel model = new DefaultTableModel() {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return column == 4; // Make only the "Informator" column editable
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
                    String imieRezysera = resultSet.getString("imie_rezysera");
                    String nazwiskoRezysera = resultSet.getString("nazwisko_rezysera");
                    String d = resultSet.getString("data_realizacji");
                    String miejsce = resultSet.getString("miejsce_realizacji");

                    SztukaTeatralna sztuka = new SztukaTeatralna(id, tytul, " ", new Rezyser(imieRezysera, nazwiskoRezysera, 0), d, miejsce);
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
                    showInformatorInfo(sztuki.get(selectedRow)); // Modify this line according to your requirements
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

                // Call the SztukaPoId function
                String query = "SELECT * FROM SztukaPoId(?)";
                PreparedStatement preparedStatement = database.getConnection().prepareStatement(query);
                preparedStatement.setInt(1, sztuka.getId());
                ResultSet resultSet = preparedStatement.executeQuery();

                StringBuilder message = new StringBuilder();
                message.append("Informacje o sztuce: \n")
                        .append("tytul: ").append(sztuka.getTytul()).append("\n")
                        .append("informator: ").append(sztuka.getInformator()).append("\n")
                        .append("rezyser: ").append(sztuka.getRezyser().toString()).append("\n")
                        .append("miejsce realizacji: ").append(sztuka.getMiejsce()).append("\n")
                        .append("data: ").append(sztuka.getData()).append("\n");

                JOptionPane.showMessageDialog(this, message.toString(), "Informator", JOptionPane.INFORMATION_MESSAGE);

            } finally {
                database.disconnect();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error retrieving informator information: " + e.getMessage());
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
                        return column == 2; // Make only the button column editable
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

                // Set custom renderer and editor for the button column
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

                // Call the GetPlaysForActor function
                String query = "SELECT * FROM GetPlaysForActor(?)";
                PreparedStatement preparedStatement = database.getConnection().prepareStatement(query);
                preparedStatement.setInt(1, aktor.getId());
                ResultSet resultSet = preparedStatement.executeQuery();

                // Display the information using JOptionPane
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
                        return column == 2; // Make only the button column editable
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

                // Call the SztukiDanegoRezysera function
                String query = "SELECT * FROM SztukiDanegoRezysera(?)";
                PreparedStatement preparedStatement = database.getConnection().prepareStatement(query);
                preparedStatement.setInt(1, rezyser.getId());
                ResultSet resultSet = preparedStatement.executeQuery();

                // Display the information using JOptionPane
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
        // Implementacja dla dodajSztuke
        JOptionPane.showMessageDialog(this, "Implementacja dodawania sztuk do bazy danych - w toku!");
    }

    private void dodajObsade(){
        JOptionPane.showMessageDialog(this, "Implementacja dodawania obsady do bazy danych - w toku!");
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
                // Pobierz wartości wprowadzone przez użytkownika i przekaż do metody addNewActor
                String imie = imieField.getText();
                String nazwisko = nazwiskoField.getText();

                // Sprawdź, czy obie wartości są niepuste
                if (!imie.isEmpty() && !nazwisko.isEmpty()) {
                    // Wywołaj metodę, która dodaje nowego aktora do bazy danych
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

                // Execute an SQL query to insert the new actor into the database
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
                // Pobierz wartości wprowadzone przez użytkownika i przekaż do metody addNewActor
                String imie = imieField.getText();
                String nazwisko = nazwiskoField.getText();

                // Sprawdź, czy obie wartości są niepuste
                if (!imie.isEmpty() && !nazwisko.isEmpty()) {
                    // Wywołaj metodę, która dodaje nowego aktora do bazy danych
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

                // Execute an SQL query to insert the new actor into the database
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
