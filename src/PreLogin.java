import javax.swing.*;

public class PreLogin extends BothPanels {

    private JButton zalogujButton;

    public PreLogin(TeatrDatabaseApp app) {
        super(app);
        zalogujButton = new JButton("Zaloguj");
        zalogujButton.addActionListener(e -> mainApp.showPostLoginPanel());
        buttonPanelLeft.add(zalogujButton);
    }
}