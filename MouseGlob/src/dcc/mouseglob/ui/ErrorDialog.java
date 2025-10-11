package dcc.mouseglob.ui;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Simple error dialog helper to show friendly messages and corrective hints.
 */
public final class ErrorDialog {
    private ErrorDialog() {}

    public static void showError(String title, String message) {
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE));
    }

    public static void showError(String title, String message, Throwable t) {
        String detail = (t != null && t.getMessage() != null) ? ("\nDetails: " + t.getMessage()) : "";
        showError(title, message + detail);
    }
}
