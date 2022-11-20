/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package printer;

import com.fazecast.jSerialComm.SerialPort;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Loki
 */
public class SendCommand extends Thread {
    
    private final SerialPort CONN_PORT;
    private final String INPUT_STRING;
    private final int mode;
    
    private ArrayList<String> fileLines;
    
    public SendCommand(SerialPort connPort, String gcode, int mode){
        this.CONN_PORT = connPort;
        this.INPUT_STRING = gcode;
        this.mode = mode;
    }
    
    public void readFile(File file){
        
        FileReader rd = null;
        this.fileLines = new ArrayList();
        
        try {
            rd = new FileReader(file);
            BufferedReader brf = new BufferedReader(rd);
            
            String line = brf.readLine();
            int lineCount = 1;
            
            while(line != null){
                
                char[] lineArray = line.toCharArray();
                            
                line = line.replaceAll(";.*", "").strip();

                if(line.equals("")){
                    line = brf.readLine();
                    continue;
                }

                line = "N" + lineCount + " " + line;

                byte[] gcodeArray = line.getBytes();
                byte charsum = 0;

                for(int i=0;i<gcodeArray.length;i++){
                    charsum ^= gcodeArray[i];
                }

                line += "*" + ((int)charsum);
                
                this.fileLines.add(line);
                lineCount++;
                line = brf.readLine();
            }
            
        } catch (FileNotFoundException ex) {
            this.fileLines = null;
            Logger.getLogger(Printer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            this.fileLines = null;
            Logger.getLogger(Printer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                rd.close();
            } catch (IOException ex) {
                Logger.getLogger(Printer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }    
    
    private void sendInfo(String gcode){
        
        if((gcode != null) && (gcode != "")){
            System.out.println(gcode);

            String info = gcode.strip() + "\r\n";
            this.CONN_PORT.writeBytes(info.getBytes(), info.getBytes().length);
        }
    }
    
    public void printFile(String filePath){
        
        File file = new File(filePath);
        
        String fileName = file.getName().replaceAll("[.].*", ".gco");
        
        System.out.println(fileName);
        
        readFile(file);
        
        for(String gcode : this.fileLines){
            
            try {
                sendInfo(gcode);
                
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                Logger.getLogger(SendCommand.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        
    }
    
    public void sendFile(String filePath){
        
        File file = new File(filePath);
        
        String fileName = file.getName().replaceAll("[.].*", ".gco");
        
        System.out.println(fileName);
        
        readFile(file);
        
        sendInfo("M28 " + fileName.toUpperCase());
        
        for(String gcode : this.fileLines){
            
            try {
                sendInfo(gcode);
                
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                Logger.getLogger(SendCommand.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        
        sendInfo("M29");
        sendInfo("M23 " + fileName);
        sendInfo("M24");
        
        
    }
    
    public String getFileLine(int index){
        return this.fileLines.get(index);
    }
    
    @Override
    public void run(){
        
        switch(this.mode){
            case 0:
                sendInfo(this.INPUT_STRING);
                break;
            case 1:
                printFile(this.INPUT_STRING);
                break;
            case 2:
                sendFile(this.INPUT_STRING);
                break;
        }
    }
    
}
