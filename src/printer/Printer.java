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
    
    private String data;
    private ArrayList<DataListener> dataListeners = new ArrayList();
    private ArrayList<String> fileLines;
    
    private SendCommand sendCommand;
    
    private void setupPrinter(){
        
        this.CONN_PORT.setComPortParameters(115200, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
        this.CONN_PORT.addDataListener(this);
        this.CONN_PORT.openPort(); //opens port
        
    }
    
    private void initializeVariables(){
        this.data = "";
    }
    
    public Printer(SerialPort connPort){
        
        this.CONN_PORT = connPort;
        setupPrinter();
        initializeVariables();
        
    }
    
    public void addListener(DataListener toAdd){
        this.dataListeners.add(toAdd);
    }
    
    public void sendInfo(String gcode){
        
        this.sendCommand = new SendCommand(this.CONN_PORT, gcode, 0);
        this.sendCommand.start();
    }
    
    public void printFile(String filePath){
        
        this.sendCommand = new SendCommand(this.CONN_PORT, filePath, 1);
        this.sendCommand.start();
        
    }
    
    public void sendFile(String filePath){
        
        this.sendCommand = new SendCommand(this.CONN_PORT, filePath, 2);
        this.sendCommand.start();
        
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
            
            for(DataListener drl : this.dataListeners){
                drl.dataChange();
            }
            
            char[] dataChar = this.data.toCharArray();
            
            //System.out.println(this.data.strip());
            
            //this.data = "";
            
            if(dataChar[dataChar.length-1] == '\n'){
                System.out.println(this.data.strip());
                
                /*
                for(SendToClient stc : this.serverListeners){
                    stc.sendData(this.data, this.INDEX);
                }*/
                
                Matcher matchError = this.ERROR_ON_LINE_READ.matcher(this.data);
                        
                if(matchError.find()){
                    try {
                        //this.recieved = 0;
                        System.out.println("Error Line");
                        int line = Integer.valueOf(matchError.group().replaceAll("[^0-9]+",""));
                        System.out.println(line);
                        
                        String fileLine = this.sendCommand.getFileLine(line - 1);
                        
                        this.sendCommand.sleep(100);
                        (new SendCommand(this.CONN_PORT, fileLine, 0)).start();
                        
                        Thread.sleep(100);
                        
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Printer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                }
                
                else if(this.TEMPERATURE_CHECK.matcher(this.data).find()){
                    try {
                        //this.tempActive = 1;
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
                else if(this.DATA_RECIEVED.matcher(this.data).find()){
                    //this.recieved = 1;
                    //this.fileRead.remove(0);
                }
                
                else if(this.FILE_OPEN_FAILED.matcher(this.data).find()){
                    
                }
                
                this.data = "";
            }
            
        }
        
        else if(event.getEventType() == SerialPort.LISTENING_EVENT_PORT_DISCONNECTED){
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
