/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;
 
public class DOS
{
    public static String executeCommand(String command, String executionDir)
    {
        String steps[]= new String[3];
        steps[0]="cmd.exe";
        steps[1]="/C";
        steps[2]=command ;
        Process proc=null;
        try
        {
            if(executionDir!=null)
            {
                 proc=Runtime.getRuntime().exec(steps, null, new File(executionDir));
            }
            else
            {
                proc=Runtime.getRuntime().exec(steps);
            }
            InputStream stdin = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(stdin);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            StringBuffer bf = new StringBuffer("");
            while ( (line = br.readLine()) != null)
            {
                    bf.append(line+"\r\n");
            }
            br.close();
            isr.close();
            stdin.close();
            return bf.toString();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return "ERROR";
    }
    public static String executeCommand(String command)
    {
        return executeCommand(command, null);
    }
 
}