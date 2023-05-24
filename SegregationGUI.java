import java.awt.*;
import javax.swing.*;

public class SegregationGUI {
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            JFrame frame = new SegregationFrame();
            frame.setTitle("Segregation");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.pack();
            frame.setVisible(true);
        });
    }
}