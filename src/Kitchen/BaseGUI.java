package Kitchen;

import javax.swing.*;
import java.awt.*;

public class BaseGUI extends JFrame {
    private JPanel panel1;

    public BaseGUI(){
        ImageIcon img = new ImageIcon("src/Kitchen/Img/Lancaster.jpeg");
        setIconImage(img.getImage());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
    }
}
