package flickfinder.View;

import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

@Component
public class RegView extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField passwordCheck;
    private JButton submitButton;

    public RegView(){
        setTitle("Flickfinder Register");
        setSize(350, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(5, 1));
        panel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        panel.add(usernameField);

        panel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        panel.add(new JLabel("Password Again"));
        passwordCheck = new JPasswordField();
        panel.add(passwordCheck);

        submitButton = new JButton("Submit");
        panel.add(new JLabel());
        panel.add(submitButton);

        add(panel);
    }

    public String getUsername() {
        return usernameField.getText();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    public String getPassCheck(){
        return new String(passwordCheck.getPassword());
    }

    public void addSubmitListener(ActionListener listener) {
        submitButton.addActionListener(listener);
    }

}
