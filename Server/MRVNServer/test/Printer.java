/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


import mrvn.*;
import com.fazecast.jSerialComm.*;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;


/**
 *
 * @author Loki
 */
public class Printer implements SerialPortDataListener{
    
    private final SerialPort CONN_PORT;
    private final int INDEX;
    
    private String data;
    private ArrayList<DataListener> dataListeners = new ArrayList();
    private ArrayList<SendToClient> serverListeners = new ArrayList();
    private ArrayList<String> fileRead = new ArrayList();
    //Change variables names to english names
    private double tempBico;
    private double tempMesa;
    private double tempAtingeBico;
    private double tempAtingeMesa;
    private int recieved;
    private int lineCount;
    private int tempActive;
    
    //Test variables
    protected String sentLine;
    
    private void setupPrinter(){
        
        this.CONN_PORT.setComPortParameters(115200, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
        this.CONN_PORT.addDataListener(this);
        this.CONN_PORT.openPort(); //opens port
        //sendInfo("M155 S1");
        
    }
    
    private void initializeVariables(){
        this.data = "";
        this.tempAtingeBico = 0;
        this.tempAtingeMesa = 0;
        this.tempBico = 0;
        this.tempMesa = 0;
        this.recieved = 0;
        this.sentLine= "";
        this.tempActive = 0;
    }
    
    /**
     * Constructor for printer
     * @param connPort 
     */
    public Printer(SerialPort connPort, int index){
        this.CONN_PORT = connPort; //Sets port for printer
        this.INDEX = index;
        setupPrinter();
        initializeVariables();
        //System.out.println("Printer connected!");
    }
    
    public void addListener(DataListener toAdd){
        this.dataListeners.add(toAdd);
    }
    
    public void addServerListener(SendToClient toAdd){
        this.serverListeners.add(toAdd);
    }
    
    public void sendInfo(String gcode){
        
        if(this.tempActive == 1){
            this.tempActive = 0;
            sendInfo("M155 S0");
        }
        
        String info = gcode + "\n";
        this.sentLine = gcode + "\n";
        for(DataListener drl : this.dataListeners){
            drl.sentCommand();
        }
        this.CONN_PORT.writeBytes(info.getBytes(), info.getBytes().length);
    }
    
    public void sendFile(String path){
        
        File fileToSend = new File(path);
        String fileName = fileToSend.getName().split("\\.")[0] + ".gco";
        
        for(String s : fileToSend.getName().split("\\.")){
            System.out.println(s);
        }
        
        this.CONN_PORT.closePort();
        
        System.out.println(fileName);
        
        sendInfo("M28 " + fileName);
        printFile(path);
        sendInfo("M29"); 
    }
    
    public void printFile(String path){
        sendInfo("M110 N0");
        //(new FileSending(path, this.dataListeners, this)).start();
        
        BufferedReader reader;
		try {
                        int size = 0;
			reader = new BufferedReader(new FileReader(path));
			String line = reader.readLine();
                        this.lineCount = 1;
                        
			while (line != null) {
                            
                            char[] lineArray = line.toCharArray();
                            
                            line = "N" + this.lineCount + " " + line;
                            
                            byte[] gcodeArray = line.getBytes();
                            byte charsum = 0;

                            for(int i=0;i<gcodeArray.length;i++){
                                charsum ^= gcodeArray[i];
                            }
                            
                            line += "*" + ((int)charsum);
                            //System.out.println(charsum);
                            
                            if((lineArray.length > 0) && (lineArray[0] != ';')){
                                
                                System.out.println(line);
                                this.fileRead.add(line);
				//System.out.println(line);
                                this.lineCount++;
                            }
                            // read next line
                            line = reader.readLine();
			}
                        
                        size = this.lineCount - 1;
                        
                        this.lineCount = 0;
                        
                        while(this.lineCount < size){
                            this.recieved = 0;
                            sendInfo(this.fileRead.get(this.lineCount));
                            /*
                            if(this.recieved == 2){
                                System.err.println("Error, resending");
                                continue;
                            }
                            else if(this.recieved == 1){
                                System.out.println("Sent");
                                this.sentLine += line + "\n";
                                for(DataListener drl : this.dataListeners){
                                    drl.sentCommand();
                                }
                                this.lineCount++;
                            }*/
                            this.lineCount++;
                        }
                        
                        
                        this.fileRead.clear();
			reader.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
    }
    
    public SerialPort getCONN_PORT(){
        return this.CONN_PORT;
    }
    
    public String getData(){
        return this.data;
    }

    public double getTempBico() {
        return this.tempBico;
    }

    public double getTempMesa() {
        return this.tempMesa;
    }

    public double getTempAtingeBico() {
        return this.tempAtingeBico;
    }

    public double getTempAtingeMesa() {
        return this.tempAtingeMesa;
    }
    
    public String getSentLine(){
        return this.sentLine;
    }
    
    /**
     *
     * @return
     */
    @Override
    public int getListeningEvents(){
        return (SerialPort.LISTENING_EVENT_DATA_AVAILABLE | SerialPort.LISTENING_EVENT_PORT_DISCONNECTED);
    }
    @Override
    public void serialEvent(SerialPortEvent event){ //Terminar de implementar
        
        if(event.getEventType() == SerialPort.LISTENING_EVENT_DATA_AVAILABLE){
            //System.out.println("Hey");
            byte[] newData = new byte[this.CONN_PORT.bytesAvailable()];
            
            this.CONN_PORT.readBytes(newData, newData.length);
            for(int i=0;i<newData.length;i++){
                    this.data += (char)newData[i];
                    //System.out.print((char)newData[i]);
            }
            
            for(DataListener drl : this.dataListeners){
                drl.dataChange();
            }
            
            //System.out.println(this.data.strip());
            
            char[] dataChar = this.data.strip().toCharArray();
            
            //this.data = "";
            
            if(dataChar[dataChar.length-1] == 'k'){
                System.out.println("ok");
                
                for(SendToClient stc : this.serverListeners){
                    stc.sendData(this.data, this.INDEX);
                }
                
                this.data = "";
            }
            else if(dataChar[0] == 'T'){
                System.out.println("Temperature");
                
                for(SendToClient stc : this.serverListeners){
                    stc.sendData(this.data, this.INDEX);
                }
                
                this.data = "";
            }
            else{
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
