package writetofile;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * @author Peter
 */
public class WriteToFile {

    public static void main(String[] args) throws IOException {
        
        
        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("PATH.txt"))); 
        out.println("TEAM 293 SPIKE AUTONOMOUS PATH");
        
        
        
        out.close();
        
    }
    
}
