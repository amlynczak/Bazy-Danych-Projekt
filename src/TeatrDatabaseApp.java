import javax.swing.*;
import java.awt.*;

public class TeatrDatabaseApp extends JFrame {

    private PreLogin preLoginPanel;
    private PostLogin postLoginPanel;

    public TeatrDatabaseApp() {
        setTitle("Teatr Database App");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        preLoginPanel = new PreLogin(this);
        postLoginPanel = new PostLogin(this);

        setLayout(new BorderLayout());
        add(preLoginPanel, BorderLayout.WEST);
        add(postLoginPanel, BorderLayout.CENTER);

        preLoginPanel.setVisible(true);
        postLoginPanel.setVisible(false);
    }

    public void showPostLoginPanel() {
        preLoginPanel.setVisible(false);
        postLoginPanel.setVisible(true);
    }

    public void showPreLoginPanel() {
        preLoginPanel.setVisible(true);
        postLoginPanel.setVisible(false);
    }
}
