/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bulkcheckout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import util.DOS;

/**
 *
 * @author yogesh.gandhi
 */
class CheckoutThread extends Thread {

    private JTextField txtComments;
    private JLabel lblStatus;
    private JTextField txtFileTextBox;

    public CheckoutThread(JTextField txtComments, JLabel lblStatus, JTextField txtFileTextBox) {
        this.txtComments = txtComments;
        this.lblStatus = lblStatus;
        this.txtFileTextBox = txtFileTextBox;
    }

    public void run() {
        FileOutputStream fos = null;
        File logFile= new File("log.txt");

        try {
            if (txtComments == null || txtComments.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "Please enter comments");
                return;
            }
            Set<String> files = getFiles(txtFileTextBox.getText());
            fos = new FileOutputStream(logFile);
            int successCount=0;
            int failureCount=0;
            for (final String file : files) {
                File f = new File(file);
                if (!f.exists())
                {
                    JOptionPane.showMessageDialog(null, "This file does not exist : " + file );
                    continue;
                }
                VersionControl clearcase = (fileToBeCheckedOut, comments) -> {
					        String command = "cleartool co -c \"" + comments + "\" -reserved ";
							command = command + fileToBeCheckedOut;
							String bf = DOS.executeCommand(command);
							return bf;
				};
                String output = clearcase.checkout(file, txtComments.getText());
                if(output.contains("Checked out") && output.contains("reserved : 1"))
                {
                    successCount++;
                }
                else
                {
                    // output is coming blank in case of failure.
                    // So i had to write manually into file.
                    fos.write(("Unable to checkout : " + new File(file).getAbsolutePath()).getBytes());
                    failureCount++;
                }
                fos.write(output.getBytes());    
                lblStatus.setText("Checking out : " + new File(file).getName());
            }
            String msg = "";
            if(successCount>0) msg = msg + successCount + " Files checked out.\n";
            if(failureCount>0) msg = msg + failureCount + " Unable to checkout";
            JOptionPane.showMessageDialog(null, msg);
            DOS.executeCommand("notepad log.txt");
        } catch (IOException ex) {
            Logger.getLogger(CheckoutThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            try {
                if(fos!=null) fos.close();
            } catch (IOException ex) {
                Logger.getLogger(CheckoutThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    private String checkout(final String file, String comments) {
        String command = "cleartool co -c \"" + comments + "\" -reserved ";
        command = command + file;
        String bf = DOS.executeCommand(command);
        return bf;
    }

    public static Set<String> getFiles(String filePath) {
        try {
            BigFile bf = new BigFile(filePath);
            Set<String> files = new TreeSet<String>();
            for (String line : bf) {
                line = line.trim();
                files.add(line);
            }
            return files;
        } catch (Throwable t) {
            JOptionPane.showMessageDialog(null, t.getMessage());
        }
        return null;
    }
}
