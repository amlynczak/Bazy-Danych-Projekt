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

public class BothPanels extends JPanel {
    public TeatrDatabaseApp mainApp;
    private JButton pobierzHarmonogramButton;
    private JButton zobaczRezyseraButton;
    private JButton zobaczAktoraButton;
    private JTable sztukiTable;
    private ArrayList<Aktor> aktorzy;
    private ArrayList<Rezyser> rezyserzy;
    private ArrayList<SztukaTeatralna> sztuki;
    public JPanel buttonPanelLeft;
    public JPanel buttonPanelRight;

    public BothPanels(TeatrDatabaseApp app){
        mainApp = app;
        setLayout(new BorderLayout());

        buttonPanelLeft = new JPanel(new GridLayout(4, 1));
        pobierzHarmonogramButton = new JButton("Pobierz Harmonogram");
        zobaczAktoraButton = new JButton("Zobacz Aktorów");
        zobaczRezyseraButton = new JButton("Zobacz Rezyserów");

        pobierzHarmonogramButton.addActionListener(e -> pobierzHarmonogram());
        zobaczAktoraButton.addActionListener(e -> zobaczAktora());
        zobaczRezyseraButton.addActionListener(e -> zobaczRezyser());

        buttonPanelLeft.add(pobierzHarmonogramButton);
        buttonPanelLeft.add(zobaczAktoraButton);
        buttonPanelLeft.add(zobaczRezyseraButton);

        sztukiTable = new JTable();
        JScrollPane tableScrollPane = new JScrollPane(sztukiTable);

        add(buttonPanelLeft, BorderLayout.WEST);
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
                System.out.println(selectedRow);
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

                message.append("\n\nIlość dostępnych biletów: ").append(sztuka.getIlosc_biletow()).append("\n")
                        .append("Cena biletu normalnego: ").append(sztuka.getNormalny_cena()).append(" zł\n")
                        .append("Cena biletu ulgowego: ").append(sztuka.getUlgowy_cena()).append(" zł\n");

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

    private int getBiletyIdByTerminId(int id_terminu) {
        int biletyId = -1; // wartość domyślna, jeśli coś pójdzie nie tak

        try {
            DataBase database = new DataBase();
            try {
                database.connect();

                String query = "SELECT bilety_id FROM TerminyRealizacji WHERE id_terminu = ?";
                PreparedStatement statement = database.getConnection().prepareStatement(query);
                statement.setInt(1, id_terminu);

                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    biletyId = resultSet.getInt("bilety_id");
                }
            } finally {
                database.disconnect();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Obsługa błędu
        }

        return biletyId;
    }

    private void purchaseTickets(int id_terminu) {
        String input = JOptionPane.showInputDialog(this, "Ile biletów chcesz kupić?");
        try {
            int numberOfTickets = Integer.parseInt(input);

            Date currentDate = new Date();
            Timestamp timestamp = new Timestamp(currentDate.getTime());

            int bilety_id = getBiletyIdByTerminId(id_terminu);

            addNewOrder(bilety_id, numberOfTickets, timestamp);

            JOptionPane.showMessageDialog(this, "Zakupiono " + numberOfTickets + " biletów dla terminu o ID: " + id_terminu);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Wprowadź poprawną liczbę biletów.");
        }
    }

    private void addNewOrder(int id_biletow, int iloscBiletow, Timestamp dataZamowienia) {
        try {
            DataBase database = new DataBase();
            try {
                database.connect();

                String addOrderQuery = "INSERT INTO ZamowieniaBiletow (id_biletow, ilosc_biletow, data_zamowienia) VALUES (?, ?, ?)";
                PreparedStatement addOrderStatement = database.getConnection().prepareStatement(addOrderQuery);
                addOrderStatement.setInt(1, id_biletow);
                addOrderStatement.setInt(2, iloscBiletow);
                addOrderStatement.setTimestamp(3, dataZamowienia);

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
}
