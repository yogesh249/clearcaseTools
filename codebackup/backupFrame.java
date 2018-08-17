package codebackup;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import util.DOS;
public class backupFrame extends JFrame {

    JLabel lblViewPath  = new JLabel("View Path");
    JTextField  txtViewPath = new JTextField("W:\\", 30);
    JLabel lblOuputDirPath  = new JLabel("Output directory Path (must end in \\)");    
    JTextField  txtOutputDirPath = new JTextField("D:\\", 30);
    JButton btnTakeBackup = new JButton("Take Backup");
	public backupFrame(String title) {
        super(title);
        btnTakeBackup.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if(!txtViewPath.getText().endsWith("\\"))
                {
                    JOptionPane.showMessageDialog(null, "View Path must end in \\");
                    return;
                }            
                if(!txtOutputDirPath.getText().endsWith("\\"))
                {
                    JOptionPane.showMessageDialog(null, "Output directory must end in \\");
                    return;
                }
                String viewPath = txtViewPath.getText();
                String command = "cleartool lsco -avobs -cview -short"; 
                try
                {
                    //JOptionPane.showMessageDialog(null, "Command = " + command);
                    //JOptionPane.showMessageDialog(null, "View Path = " +viewPath);
                    String bf = DOS.executeCommand(command, viewPath);
                    //Convert the String to a set of lines.
                    Set set = getFiles(bf);
                    Set set2 = new HashSet();
                    Iterator it = set.iterator();
                    while(it.hasNext())
                    {
                        String line = it.next().toString();
                        if(!line.trim().equals(""))
                        {
                            if(viewPath.startsWith("M:"))
                            {
                                set2.add(line);
                            }
                            else
                            {
                                set2.add(viewPath.substring(0, viewPath.length()-1) + line);
                            }
                        }
                    }
                    // At this point set2 contains the list of all the 
                    // checked out files.
                    if(set2.size()!=0)
                    {
                        takeBackup(set2, txtOutputDirPath.getText());
                        System.exit(0);
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(null, "No files were checkedout from this view");
                    }
                }
                catch(Exception ee)
                {
                    JOptionPane.showMessageDialog(null, ee.getMessage());
                    ee.printStackTrace();;
                    System.exit(0);
                }
                finally
                {
                    
                }
                
            }
        });
        
        getContentPane().setLayout(new FlowLayout()); 
		getContentPane().add(lblViewPath);
        getContentPane().add(txtViewPath);
		getContentPane().add(lblOuputDirPath);
        getContentPane().add(txtOutputDirPath);
        getContentPane().add(btnTakeBackup);
        setDefaultCloseOperation(EXIT_ON_CLOSE);        
		this.setSize(350, 300);
        setResizable(false);
		setVisible(true);
	}

    private static boolean write2File(String content, Set set, String filePath) throws Exception
    {
        FileWriter writer = new FileWriter(new File(filePath));
        if(content!=null) writer.write(content);
        
        writer.write("Total Number of files : " );
              
        if(set!=null)
        {
            writer.write(set.size()+"\r\n");
            Iterator it = set.iterator();
            while(it.hasNext())
            {
                writer.write(it.next().toString());
                writer.write("\r\n");
            }        
        }
        writer.flush();
        writer.close();
        return false;
    }
    /**
     * 
     * @param args
     */
    public static void main(String[] args)
    {
		new backupFrame("Developed by Yogesh Gandhi");
    }
    public static Set getFiles(String command)
    {
        Set set = new HashSet();
        if(command!=null)
        {
            String[] lines = command.split("\n");   
            for(int i=0;i<lines.length;i++)
            {
                set.add(lines[i]);
            }
            return set;
        }
        else
            return null;
    }
    public static boolean takeBackup(Set set, String outputDirPath)
    {
        String months[]={"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        try
        {
            Date d = new Date();
            String currentYear = String.valueOf(d.getYear()+1900);
            String currentMonth =String.valueOf(d.getMonth()); 
            String currentDay =String.valueOf(d.getDate()); 
            String hours = d.getHours() <=9 ? "0"+d.getHours() : String.valueOf(d.getHours());
            String minutes = d.getMinutes()<=9 ? "0" + d.getMinutes() : String.valueOf(d.getMinutes());
            DOS.executeCommand("mkdir "+outputDirPath+currentYear);
            DOS.executeCommand("mkdir "+outputDirPath+currentYear+"\\"+months[d.getMonth()]);
            DOS.executeCommand("mkdir "+outputDirPath+currentYear+"\\"+months[d.getMonth()]+"\\"+currentDay);
            String finalOutputDir = outputDirPath+currentYear+"\\"+months[d.getMonth()]+"\\"+currentDay+"\\"+hours+"."+minutes;            
            DOS.executeCommand("mkdir "+ finalOutputDir);
            boolean status = write2File(null, set, finalOutputDir + "\\filePaths.txt"); 
            Iterator it = set.iterator();
            while(it.hasNext())
            {
                String srcPath = it.next().toString() ;
                String copyCommand = "copy " + srcPath + " " + finalOutputDir;
                int lastBkslshIndex = srcPath.lastIndexOf("\\");
                String fileName = srcPath.substring(lastBkslshIndex+1);
                String fullFilePath= finalOutputDir + "\\"+fileName.trim();
                // if a file already exists, warn the user.
                boolean fileExists = new File(fullFilePath).exists();
                if(fileExists)
                {
                    JOptionPane.showMessageDialog(null, fullFilePath + " already exists. Not overwriting it.. ");
                    return false;
                }
                else
                {
                    DOS.executeCommand(copyCommand);                    
                }

            }

//            if(!status)
//            {
//                JOptionPane.showMessageDialog(null, "Couldn't write to file ");
//            }
            DOS.executeCommand("explorer " + finalOutputDir);
            return true;
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        return false;
    }
}
