import javax.swing.*;
import java.awt.*;

public class PreLogin extends BothPanels {

    private JButton zalogujButton;

    public PreLogin(TeatrDatabaseApp app) {
        super(app);
        zalogujButton = new JButton("Logowanie pracownika");
        zalogujButton.addActionListener(e -> mainApp.showPostLoginPanel());
        buttonPanelLeft.add(zalogujButton);

        panelPanel.removeAll();
        JTextArea tekst = new JTextArea("Panel klienta");
        panelPanel.add(new JScrollPane(tekst), BorderLayout.CENTER);
        panelPanel.setVisible(true);
        panelPanel.revalidate();
        panelPanel.repaint();
    }
}