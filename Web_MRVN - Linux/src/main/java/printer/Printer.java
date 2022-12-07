/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package printer;

import com.fazecast.jSerialComm.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Loki
 */
public class Printer implements SerialPortDataListener{
    
    private final SerialPort CONN_PORT;
    private final Pattern ERROR_ON_LINE_READ = Pattern.compile("Resend: [0-9]+");
    private final Pattern ERROR_UNKNOWN_COMMAND = Pattern.compile("echo:Unknown command:");
    private final Pattern DATA_RECIEVED = Pattern.compile("ok");
    private final Pattern TEMPERATURE_CHECK = Pattern.compile("T:[0-9]+[.][0-9]+ /[0-9]+[.][0-9]+ B:[0-9]+[.][0-9]+ /[0-9]+[.][0-9]+");
    private final Pattern POSITION_MONITOR = Pattern.compile("X:[0-9]+[.][0-9]+ Y:[0-9]+[.][0-9]+ Z:[0-9]+[.][0-9]+");
    private final Pattern FILE_OPEN_FAILED = Pattern.compile("open failed");
    private final Pattern FILE_CODE = Pattern.compile("^N[0-9]+");
    private final Pattern FILE_IN_SD = Pattern.compile(".*[.].*[ ]");
    private final int INDEX;
    
    private String data;
    private int printFile;
    private ArrayList<CommandListener> sentDataListeners = new ArrayList();
    private ArrayList<DataListener> TempDataListeners = new ArrayList();
    private ArrayList<String> filesInSD = new ArrayList();
    private ArrayList<PrinterDisconnectListener> disconnectListener = new ArrayList();
    private PrinterData printerData;
    
    
    private SendCommand sendCommand;
    
    private void setupPrinter(){
        
        this.CONN_PORT.setComPortParameters(115200, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
        this.CONN_PORT.addDataListener(this);
        this.CONN_PORT.openPort(); //opens port
        sendInfo("M20");
        
        
    }
    
    private void initializeVariables(){
        this.data = "";
        this.printFile = 0;
        this.printerData = new PrinterData();
    }
    
    public Printer(int index, SerialPort connPort){
        
        this.INDEX = index;
        this.CONN_PORT = connPort;
        setupPrinter();
        initializeVariables();
        
    }
    
    public void addCommandListener(CommandListener toAdd){
        this.sentDataListeners.add(toAdd);
    }
    
    public void addDataListener(DataListener toAdd){
        this.TempDataListeners.add(toAdd);
    }
    
    public void addDisconnectListener(PrinterDisconnectListener toAdd){
        this.disconnectListener.add(toAdd);
    }
    
    public void sendInfo(String gcode){
        
        this.sendCommand = new SendCommand(this.CONN_PORT, gcode, 0, this);
        this.sendCommand.start();
    }
    
    public void printFile(String filePath){
        
        this.printFile = 1;
                
        this.sendCommand = new SendCommand(this.CONN_PORT, filePath, 1, this);
        addCommandListener(this.sendCommand);
        this.sendCommand.start();
        
    }
    
    public void sendFile(String filePath){
        
        File file = new File(filePath);
        String filePathName = file.getName().replaceAll("[.].*", "").strip().toUpperCase();
        
        for(String s : this.filesInSD){
            
            System.out.println(filePathName);
            String filesSDName = s.replaceAll("[.].*", "").strip();
            System.out.println(filesSDName);
            if(filePathName.equals(filesSDName)){
                System.out.println("file in SD Card: " + filesSDName);
               
                this.sendCommand = new SendCommand(this.CONN_PORT, "M23" + filesSDName.toUpperCase() + ".GCO", 0, this);
                this.sendCommand.start();
                this.sendCommand = new SendCommand(this.CONN_PORT, "M24", 0, this);
                this.sendCommand.start();
                
                return;
            }
        }
        
        this.sendCommand = new SendCommand(this.CONN_PORT, filePath, 2, this);
        this.sendCommand.start();
        
    }
    
    public void listFiles(){
        for(String s : this.filesInSD){
            System.out.println(s);
        }
    }
    
    public PrinterData getPrinterData(){
        return this.printerData;
    }
    
    public SerialPort getCONN_PORT(){
        return this.CONN_PORT;
    }
    
    public void percentageChange(){
        for(DataListener d : this.TempDataListeners){
            d.percentageChange(this.INDEX, this.printerData);
        }
    }
    
    @Override
    public int getListeningEvents(){
        return (SerialPort.LISTENING_EVENT_DATA_AVAILABLE | SerialPort.LISTENING_EVENT_PORT_DISCONNECTED);
    }
    @Override
    public void serialEvent(SerialPortEvent event){ //Terminar de implementar
        
        if(event.getEventType() == SerialPort.LISTENING_EVENT_DATA_AVAILABLE){
            
            byte[] newData = new byte[this.CONN_PORT.bytesAvailable()];
            
            this.CONN_PORT.readBytes(newData, newData.length);
            for(int i=0;i<newData.length;i++){
                    this.data += (char)newData[i];
            }
            
            
            
            char[] dataChar = this.data.toCharArray();
            
            if(dataChar[dataChar.length-1] == '\n'){
                System.out.println(this.data.strip());
                
                
                Matcher matchError = this.ERROR_ON_LINE_READ.matcher(this.data);
                Matcher matchFile = this.FILE_IN_SD.matcher(this.data);
                
                if(this.DATA_RECIEVED.matcher(this.data).find()){
                  
                    
                    if(this.printFile == 1){
                        System.out.println("Sending to listeners");
                        for(CommandListener drl : this.sentDataListeners){
                            
                            drl.sentCommand();
                            
                        }
                        
                        this.printFile = this.sendCommand.getPrinting();
                        
                          System.out.println(this.printFile);
                    }
                }
                
                if(matchError.find()){
                    this.printFile = 0;
                    try {
                        //this.recieved = 0;
                        
                        this.sendCommand.sleep(200);
                        
                        System.out.println("Error Line");
                        int line = Integer.valueOf(matchError.group().replaceAll("[^0-9]+",""));
                        System.out.println(line);
                        
                        String fileLine = this.sendCommand.getFileLine(line - 1);
                        
                        (new SendCommand(this.CONN_PORT, fileLine, 0, this)).start();
                        this.printFile = 1;
                        Thread.sleep(100);
                        
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Printer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                }
                
                else if(matchFile.find()){
                    
                    while(matchFile.find()){
                        this.filesInSD.add(matchFile.group().toUpperCase());
                    }
                }
                
                else if(this.TEMPERATURE_CHECK.matcher(this.data).find()){
                    try {
                        
                        this.printerData.parseData(this.data);
                        
                        for(DataListener dl : this.TempDataListeners){
                            dl.tempChange(this.INDEX, this.printerData);
                        }
                        
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                    }
                }
                
                else if(this.POSITION_MONITOR.matcher(this.data).find()){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                    }
                }
                /*
                else if(this.DATA_RECIEVED.matcher(this.data).find()){
                    System.out.println("found: ok");
                    if(this.printFile == 1){
                        System.out.println("printFile");
                        this.sendCommand.setWait(0);
                    }
                }
                */
                else if(this.FILE_OPEN_FAILED.matcher(this.data).find()){
                    
                    
                }
                
                this.data = "";
                
            }
            
        }
        
        else if(event.getEventType() == SerialPort.LISTENING_EVENT_PORT_DISCONNECTED){
            
            for(PrinterDisconnectListener p : this.disconnectListener){
                p.pinterDisconnect(this);
            }
            
            if(this.CONN_PORT.isOpen()){
                this.CONN_PORT.closePort();
            }
            System.err.println("Error: Printer Disconnected!");
            while(!this.CONN_PORT.isOpen()){
                try{
                    this.CONN_PORT.openPort();
                } catch(Exception e){
                    System.err.println(e.getMessage());
                }
            }
        }
    }
}
