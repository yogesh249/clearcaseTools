package changeset;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;



public class myFrame extends JFrame {

    JLabel lblViewName  = new JLabel("View Name");
    JLabel lblActivityName  = new JLabel("Activity Name");
    JLabel lblDataVOB  = new JLabel("Datavob");
    JTextField  txtViewName = new JTextField("", 30);
    JTextField  txtActivityName = new JTextField("", 30);
    JTextField  txtDataVOB = new JTextField("", 30);
    JButton jbutton = new JButton("Get Change List");
	public myFrame(String title) {
        super(title);
        String viewName = txtViewName.getText();
        String activityName = txtActivityName.getText();
        String datavob = txtDataVOB.getText();
        try
        {
            viewName = readViewName();
            activityName = readActivityName();
            datavob = readDataVob();
            txtViewName.setText(viewName);
            txtActivityName.setText(activityName);
            txtDataVOB.setText(datavob);
        }
        catch(Throwable t)
        {

        }        
        
        jbutton.addActionListener(new ActionListener()
        {
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				String filePath="D:\\temp.txt";
				// By the time we click on the button, the text boxes
				// have been filled by the user.
				String viewName = txtViewName.getText();
				String activityName = txtActivityName.getText();
				String datavob = txtDataVOB.getText();
	
				
				try {
					// Write it to file, for next time...so that
					// when user opens the application next time, he should see
					// the previous values.
					write2File(viewName, activityName, datavob);
				} catch (IOException ex) {
					Logger.getLogger(myFrame.class.getName()).log(Level.SEVERE, null, ex);
				}
				
				String command = "cleartool lsactivity -l " + 
								 viewName + "@\\" +
								 datavob + " activity \"" +
								 activityName+"\"";
				String steps[]= new String[3];
				steps[0]="cmd.exe";
				steps[1]="/C";
				steps[2]=command ;
				Process proc=null;
				Process proc2=null;
				try
				{
					proc=Runtime.getRuntime().exec(steps, null, new File("M:\\"+viewName));//, envp); 
					InputStream stdin = proc.getInputStream();
					InputStreamReader isr = new InputStreamReader(stdin);
					BufferedReader br = new BufferedReader(isr);
					String line = null;
					StringBuffer bf = new StringBuffer("");
					Set files = new HashSet();
					while ( (line = br.readLine()) != null)
					{
						if(line.indexOf("@@")!=-1)
						{
							line = line.substring(0, line.indexOf("@@"));
							files.add(line);                            
						}
						else
						{
							bf.append(line+"\r\n");
						}
					}
					int exitVal = proc.waitFor();            
					if(proc!=null)
					{
						proc.exitValue();
					}
					write2File(bf.toString(), files, filePath);
					proc2 = Runtime.getRuntime().exec("notepad.exe "+filePath);
					
					System.out.println("Process exitValue: " + exitVal);
					
				}
				catch(Exception ee)
				{
					JOptionPane.showMessageDialog(null, ee.getMessage());
					ee.printStackTrace();;
				}
				finally
				{
					if(proc!=null)
					{
						proc.destroy();
					}    
					System.exit(0);
				}
			}
		});
        
        getContentPane().setLayout(new FlowLayout()); 
		getContentPane().add(lblViewName);
        getContentPane().add(txtViewName);
        getContentPane().add(lblActivityName);
        getContentPane().add(txtActivityName);
        getContentPane().add(lblDataVOB);
        getContentPane().add(txtDataVOB);
        getContentPane().add(jbutton);
        setDefaultCloseOperation(EXIT_ON_CLOSE);        
		this.setSize(350, 300);
		setVisible(true);
	}

    private void write2File(String content, Set set, String filePath) throws Exception
    {
        FileWriter writer = new FileWriter(new File(filePath));
        writer.write(content);
        Iterator it = set.iterator();
        while(it.hasNext())
        {
            writer.write(it.next().toString());
        }        
        writer.flush();
        writer.close();
    }
    private void write2File(String viewName, String activityName, String datavob) throws IOException {
        FileWriter fw = new FileWriter("info.txt");
        fw.append(viewName);
        fw.append("\r\n");
        fw.append(activityName);
        fw.append("\r\n");
        fw.append(datavob);
        fw.flush();
        fw.close();
    } 
	private String readViewName() throws FileNotFoundException, Exception {
		BigFile bf = new BigFile("info.txt");
		for (String line : bf)  
		{
			bf.Close();                    
			// First one is the view name.
			return line;
		}
		return "";                
	}

	private String readActivityName() throws Exception {
		BigFile bf = new BigFile("info.txt");
		int i=0;
		for (String line : bf)  
		{
			if(i==1)
			{
				// second one is the view name.
				bf.Close();                         
				return line;
			}
			i++;
		}
		return "";               
	}

	private String readDataVob() throws Exception {
		BigFile bf = new BigFile("info.txt");
		int i=0;
		for (String line : bf)  
		{
			if(i==2)
			{
				// third one is the datavob name.
				bf.Close();                         
				return line;
			}
			i++;
		}            
		return "";
	}
    
    /**
     * 
     * @param args
     */
    public static void main(String[] args)
    {
		new myFrame("Developed by Yogesh Gandhi");
    }
}
