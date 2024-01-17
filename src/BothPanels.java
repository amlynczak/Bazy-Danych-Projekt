import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class BothPanels extends JPanel {
    public TeatrDatabaseApp mainApp;
    private JButton pobierzHarmonogramButton;
    private JButton nasiRezyserowieButton;
    private JButton nasiAktorzyButton;
    public JTable sztukiTable;
    private ArrayList<Aktor> aktorzy;
    private ArrayList<Rezyser> rezyserzy;
    private ArrayList<SztukaTeatralna> sztuki;
    public JPanel buttonPanelLeft;
    public JPanel buttonPanelRight;
    public JPanel infoPanel;
    public BothPanels(TeatrDatabaseApp app){
        mainApp = app;
        setLayout(new BorderLayout());

        buttonPanelLeft = new JPanel(new GridLayout(4, 1));
        buttonPanelRight = new JPanel(new GridLayout(7, 1));
        pobierzHarmonogramButton = new JButton("Pobierz Harmonogram");
        nasiAktorzyButton = new JButton("Nasi Aktorzy");
        nasiRezyserowieButton = new JButton("Nasi Reżyserzy");

        pobierzHarmonogramButton.addActionListener(e -> pobierzHarmonogram());
        nasiAktorzyButton.addActionListener(e -> AktorzyInfo());
        nasiRezyserowieButton.addActionListener(e -> RezyserzyInfo());

        buttonPanelLeft.add(pobierzHarmonogramButton);
        buttonPanelLeft.add(nasiAktorzyButton);
        buttonPanelLeft.add(nasiRezyserowieButton);

        sztukiTable = new JTable();
        JScrollPane tableScrollPane = new JScrollPane(sztukiTable);

        add(buttonPanelLeft, BorderLayout.WEST);
        add(tableScrollPane, BorderLayout.CENTER);
        add(buttonPanelRight, BorderLayout.EAST);

        infoPanel = new JPanel(new BorderLayout());
        add(infoPanel, BorderLayout.SOUTH);
    }

    private void pobierzHarmonogram() {
        infoPanel.setVisible(false);
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
                    int ilosc = resultSet.getInt("dostepne_bilety");
                    int ulgowy = resultSet.getInt("cena_ulgowy");
                    int normalny = resultSet.getInt("cena_normalny");

                    SztukaTeatralna sztuka = new SztukaTeatralna(id, tytul, informator, new Rezyser(imieRezysera, nazwiskoRezysera, 0), d, miejsce, ilosc, ulgowy, normalny);
                    sztuki.add(sztuka);

                    model.addRow(new Object[]{tytul, imieRezysera + " " + nazwiskoRezysera, d, miejsce, "Informator"});
                }

                sztukiTable.setModel(model);

                sztukiTable.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
                sztukiTable.getColumnModel().getColumn(4).setCellEditor(new ButtonEditorInformator(new JCheckBox()));

            } finally {
                database.disconnect();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Błąd podczas pobierania harmonogramu: \n" + e.getMessage());
        }
    }

    private void pokazInformator(SztukaTeatralna sztuka) {
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
                        .append("Tytuł: ").append(sztuka.getTytul()).append("\n")
                        .append(sztuka.getInformator()).append("\n")
                        .append("Reżyser: ").append(sztuka.getRezyser().toString()).append("\n")
                        .append("Miejsce realizacji: ").append(sztuka.getMiejsce()).append("\n")
                        .append("Data: ").append(sztuka.getData()).append("\n")
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

                    message.append("Postać: ").append(postac).append(", grana przez: ").append(imie_aktora).append(" ").append(nazwisko_aktora).append("\n");
                }

                message.append("\n\nIlość dostępnych biletów: ").append(sztuka.getIlosc_biletow()).append("\n")
                        .append("Cena biletu normalnego: ").append(sztuka.getNormalny_cena()).append(" zł\n")
                        .append("Cena biletu ulgowego: ").append(sztuka.getUlgowy_cena()).append(" zł\n");

                JPanel panel = new JPanel(new BorderLayout());
                JTextArea textArea = new JTextArea(message.toString());
                textArea.setEditable(false);
                panel.add(new JScrollPane(textArea), BorderLayout.CENTER);

                JPanel buttonPanel = new JPanel(new FlowLayout());  // Zmiana na FlowLayout
                JButton purchaseButton = new JButton("Zakup Biletów");
                buttonPanel.add(purchaseButton);
                purchaseButton.addActionListener(e -> zakupBilety(id_sztuki));

                JButton saveToFileButton = new JButton("Zapisz informator do pliku");
                buttonPanel.add(saveToFileButton);
                saveToFileButton.addActionListener(e -> zapiszInformatorDoPliku(sztuka, message.toString()));

                panel.add(buttonPanel, BorderLayout.SOUTH);  // Dodane do panelu z użyciem BorderLayout

                JOptionPane.showMessageDialog(this, panel, "Informator", JOptionPane.INFORMATION_MESSAGE);


            } finally {
                database.disconnect();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Błąd podczas pobierania informacji: \n" + e.getMessage());
        }
    }

    private void zakupBilety(int id_terminu) {
        String input_u = JOptionPane.showInputDialog(this, "Ile biletów ulgowych chcesz kupic?");
        String input_n = JOptionPane.showInputDialog(this, "Ile biletów normalnych chcesz kupic?");
        try {
            int ulgowe = Integer.parseInt(input_u);
            int normalne = Integer.parseInt(input_n);

            Date currentDate = new Date();
            Timestamp timestamp = new Timestamp(currentDate.getTime());

            noweZamownienie(id_terminu, ulgowe, normalne, timestamp);

            JOptionPane.showMessageDialog(this, "Zakupiono " + ulgowe + " biletów ulgowych oraz " + normalne + " biletów normalnych.");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Wprowadź poprawną liczbę biletów.");
        }
    }

    private void zapiszInformatorDoPliku(SztukaTeatralna sztuka, String informatorText) {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Wybierz miejsce do zapisania informatora");
            int userSelection = fileChooser.showSaveDialog(this);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                FileWriter writer = new FileWriter(fileToSave);
                writer.write(informatorText);
                writer.close();
                JOptionPane.showMessageDialog(this, "Informator zapisany do pliku.");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Błąd podczas zapisywania informatora do pliku: " + ex.getMessage());
        }
    }

    private void noweZamownienie(int id_teminu, int nr_u, int nr_n, Timestamp dataZamowienia) {
        try {
            DataBase database = new DataBase();
            try {
                database.connect();

                String addOrderQuery = "INSERT INTO ZamowieniaBiletow (id_terminu, ilosc_biletow_ulgowe, ilosc_biletow_normalne, data_zamowienia) VALUES (?, ?, ?, ?)";
                PreparedStatement addOrderStatement = database.getConnection().prepareStatement(addOrderQuery);
                addOrderStatement.setInt(1, id_teminu);
                addOrderStatement.setInt(2, nr_u);
                addOrderStatement.setInt(3, nr_n);
                addOrderStatement.setTimestamp(4, dataZamowienia);

                int rowsAffected = addOrderStatement.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Zamówienie biletów dodane do bazy danych.");
                } else {
                    JOptionPane.showMessageDialog(this, "Nie udało się dodać zamówienia biletów do bazy danych.");
                }
            } finally {
                database.disconnect();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Błąd podczas łączenia z bazą danych: " + e.getMessage());
        }
    }


    private void AktorzyInfo() {
        infoPanel.setVisible(false);
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

                sztukiTable.getColumnModel().getColumn(2).setCellRenderer(new ButtonRenderer());
                sztukiTable.getColumnModel().getColumn(2).setCellEditor(new ButtonEditorAktor(new JCheckBox()));

            } finally {
                database.disconnect();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Błąd podczas pobierania listy aktorów: " + e.getMessage());
        }
    }

    private void AktorInfo(Aktor aktor) {
        try {
            DataBase database = new DataBase();
            try {
                database.connect();

                String query = "SELECT * FROM SztukiDanegoAktora(?)";
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

                JOptionPane.showMessageDialog(this, message.toString(), "Informacje o aktorze", JOptionPane.INFORMATION_MESSAGE);

            } finally {
                database.disconnect();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Błąd podczas pobierania informacji o aktorze: \n" + e.getMessage());
        }
    }

    private void RezyserzyInfo() {
        infoPanel.setVisible(false);
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

                sztukiTable.getColumnModel().getColumn(2).setCellRenderer(new BothPanels.ButtonRenderer());
                sztukiTable.getColumnModel().getColumn(2).setCellEditor(new BothPanels.ButtonEditorRezyser(new JCheckBox()));
            } finally {
                database.disconnect();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Błąd podczas pobierania listy rezyserow: " + e.getMessage());
        }
    }

    private void RezyserInfo(Rezyser rezyser) {
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

                JOptionPane.showMessageDialog(this, message.toString(), "Informacje o reżyserze", JOptionPane.INFORMATION_MESSAGE);

            } finally {
                database.disconnect();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Błąd podczas pobierania informacji o reżyserze: " + e.getMessage());
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
                    AktorInfo(aktorzy.get(selectedRow));
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
                    RezyserInfo(rezyserzy.get(selectedRow));
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

    private class ButtonEditorInformator extends DefaultCellEditor {
        private JButton button;

        public ButtonEditorInformator(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton("Informator");
            button.addActionListener(e -> {
                int selectedRow = sztukiTable.getSelectedRow();
                if (selectedRow != -1) {
                    pokazInformator(sztuki.get(selectedRow));
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
}
