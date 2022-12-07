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
public class SendCommand extends Thread implements CommandListener {
    
    private final SerialPort CONN_PORT;
    private final String INPUT_STRING;
    private final int mode;
    private final Printer printer;
    
    private int printing;
    private int nextFileLine;
    private ArrayList<String> fileLines;
    private int printPercentage;
    
    public SendCommand(SerialPort connPort, String gcode, int mode, Printer p){
        this.CONN_PORT = connPort;
        this.INPUT_STRING = gcode;
        this.mode = mode;
        this.printing = 0;
        this.nextFileLine = 0;
        this.printer = p;
    }
    
    public ArrayList<String> readFile(File file){
        
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
        
        return this.fileLines;
    }    
    
    private void sendInfo(String gcode){
        
        if((gcode != null) && (gcode != "")){
            System.out.println(gcode);

            String info = gcode.strip() + "\r\n";
            this.CONN_PORT.writeBytes(info.getBytes(), info.getBytes().length);
        }
    }
    
    public void printFile(String filePath){
        
        try {
            this.printing = 1;
            
            this.nextFileLine = 0;
            this.printPercentage = 0;
            
            readFile(new File(filePath));
            
            sendInfo("M110 N0");
            
            Thread.sleep(10);
            
            //sendInfo(this.fileLines.get(0));
            
        } catch (InterruptedException ex) {
            Logger.getLogger(SendCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void sendFile(String filePath){
        
        File file = new File(filePath);
        
        String fileName = file.getName().replaceAll("[.].*", ".gco");
        
        System.out.println(fileName);
        
        readFile(file);
        
        sendInfo("M110 N0");
        
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
    
    public int getPrinting(){
        return this.printing;
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
 
    @Override
    public void sentCommand(){
        /*
        System.out.println("Listened");
        
        System.out.println("Line: " + this.nextFileLine);
        System.out.println("Size: " + this.fileLines.size());
        */
        
        System.out.println("Next line: " + this.nextFileLine);
        System.out.println("File size: " + this.fileLines.size());
        
        if(this.nextFileLine == this.fileLines.size()){
            this.nextFileLine = 0;
            this.printing = 0;
            this.fileLines.clear();
            //sendInfo("G28");
        }
        else{
            this.printPercentage = 100*(this.nextFileLine / this.fileLines.size());
            this.printer.getPrinterData().setPrinterPercentage(this.printPercentage);
            this.printer.percentageChange();
            
            System.out.println("Print percentage = " + this.printPercentage + "%");
            
            System.out.println("Sending: " + this.fileLines.get(this.nextFileLine));
            sendInfo(this.fileLines.get(this.nextFileLine));
            this.nextFileLine++;
        }
    }
    
}
