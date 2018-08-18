package bulkcheckout;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class BulkCheckout extends JFrame {

    JLabel lblFilePath = new JLabel("Input File : ");
    JLabel lblStatus = new JLabel("");
    JTextField txtFileTextBox = new JTextField("", 30);
    JFileChooser txtFileChooser = new JFileChooser();
    JButton btnSelectFile = new JButton("Select File");
    JLabel lblComments = new JLabel("Comments : ");
    JTextField txtComments = new JTextField("Comments", 30);
    JButton btnBulkCheckout = new JButton("Bulk Checkout");
    public BulkCheckout(String title) {
        super(title);
        // Add the code for selecting a file.
        btnSelectFile.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                //Handle open button action.
                if (e.getSource() == btnSelectFile) {
                    int returnVal = txtFileChooser.showOpenDialog(BulkCheckout.this);

                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File file = txtFileChooser.getSelectedFile();
                        txtFileTextBox.setText(file.getAbsolutePath());
                    } else {
                    }
                }
            }
        });
        btnBulkCheckout.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                // This thread will checkout and update the label as well.
                Thread jobThread = new CheckoutThread(txtComments, lblStatus, txtFileTextBox) ;
                jobThread.start();
            }
        });
        

        getContentPane().setLayout(new FlowLayout());
        getContentPane().add(lblFilePath);
        getContentPane().add(txtFileTextBox);
        getContentPane().add(btnSelectFile);
        getContentPane().add(lblComments);
        getContentPane().add(txtComments);
        getContentPane().add(btnBulkCheckout);
        getContentPane().add(lblStatus);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(550, 300);
        setResizable(false);
        setVisible(true);
    }

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        new BulkCheckout("Developed by Yogesh Gandhi");
    }



}
