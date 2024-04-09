package Kitchen;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FileSelectionUI extends JFrame implements ActionListener {

    private JList<String> fileList;
    private JScrollPane scrollPane;

    public FileSelectionUI() {
        setTitle("File Retrieval");
        setBounds(100, 100, 500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout()); // Use BorderLayout for simplicity

        // Sample file names, replace with actual file retrieval logic
        String[] files = {"File1.txt", "File2.txt", "File3.txt", "File4.txt", "File5.txt", "File6.txt", "File7.txt", "File8.txt", "File9.txt", "File10.txt"};

        fileList = new JList<>(files); // Create a JList to display the files
        fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Allow only single selection

        scrollPane = new JScrollPane(fileList); // Add the JList to a JScrollPane
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); // Always show vertical scroll bar

        add(scrollPane, BorderLayout.CENTER); // Add the scroll pane to the center of the layout
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                FileSelectionUI frame = new FileSelectionUI();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
