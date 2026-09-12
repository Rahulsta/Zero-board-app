import client.gui.WhiteboardGUI;

public class Main {
    public static void main(String[] args) {

        javax.swing.SwingUtilities.invokeLater(() -> {
            WhiteboardGUI gui = new WhiteboardGUI();
            gui.setVisible(true);
        });
    }
}