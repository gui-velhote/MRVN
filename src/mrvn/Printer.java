/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mrvn;

import com.fazecast.jSerialComm.*;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Loki
 */
public class Printer implements SerialPortDataListener{
    
    private final SerialPort CONN_PORT;
    
    private String data;
    private ArrayList<DataRecievedListener> dataRecieved = new ArrayList();
    //Change variables names to english names
    private double tempBico;
    private double tempMesa;
    private double tempAtingeBico;
    private double tempAtingeMesa;
    private String recieved;
    
    //Test variables
    private String sentLine;
    
    private void setupPrinter(){
        
        this.CONN_PORT.setComPortParameters(115200, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
        this.CONN_PORT.addDataListener(this);
        this.CONN_PORT.openPort(); //opens port
        sendInfo("M155 S1");
        
    }
    
    private void initializeVariables(){
        this.data = "";
        this.tempAtingeBico = 0;
        this.tempAtingeMesa = 0;
        this.tempBico = 0;
        this.tempMesa = 0;
        this.recieved = null;
        this.sentLine= "";
    }
    
    /**
     * Constructor for printer
     * @param connPort 
     */
    public Printer(SerialPort connPort){
        this.CONN_PORT = connPort; //Sets port for printer
        setupPrinter();
        initializeVariables();
        //System.out.println("Printer connected!");
    }
    
    public void addListener(DataRecievedListener toAdd){
        this.dataRecieved.add(toAdd);
    }
    
    public void sendInfo(String gcode){
        String info = gcode + "\n";
        this.sentLine += gcode + "\n";
        for(DataRecievedListener drl : this.dataRecieved){
            drl.sentCommand();
        }
        this.CONN_PORT.writeBytes(info.getBytes(), info.getBytes().length);
    }
    
    public void sendFile(String path){
        BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(path));
			String line = reader.readLine();
                        char[] outputArray;
			while (line != null) {
                            char[] lineArray = line.toCharArray();
                            if((lineArray.length > 0) && (lineArray[0] != ';')){
                                sendInfo(line);
                                this.sentLine += line + "\n";
                                for(DataRecievedListener drl : this.dataRecieved){
                                    drl.sentCommand();
                                }
				//System.out.println(line);
                                //while(this.recieved!="ok");
                                
                            }
                            // read next line
                            line = reader.readLine();
			}
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
            byte[] newData = new byte[this.CONN_PORT.bytesAvailable()];
            this.CONN_PORT.readBytes(newData, newData.length);
            if((char)newData[0] == ' '){
                if((char)newData[1] == 'T'){
                    //System.out.println("Temperature");
                    ArrayList<Double> tempValues = new ArrayList();

                    int i=2;

                    String temp = "";
                    while(i<newData.length){
                        if((((char)newData[i] == 'B')) || ((char)newData[i] == '/') || (char)newData[i] == '@'){
                            //tempValues.add(Integer.parseInt(temp));
                            tempValues.add(Double.parseDouble(temp));
                            //System.out.println("Index = " + i + " Temp size = " + tempValues.size());
                            temp = "";
                        }
                        else if((char)newData[i] == ':'){
                        }
                        else{
                            temp += String.valueOf((char)newData[i]);
                        }
                        if(tempValues.size() == 4){
                            break;
                        }
                        i++;
                    }
                    
                    this.tempBico = tempValues.get(0);
                    this.tempMesa = tempValues.get(2);
                    this.tempAtingeBico = tempValues.get(1);
                    this.tempAtingeMesa = tempValues.get(3);

                    for(DataRecievedListener drl : this.dataRecieved){
                        drl.tempChange();
                    }
                    
                }
                for(int i=0;i<newData.length;i++){
                    this.data += (char)newData[i];
                }
                for(DataRecievedListener drl : this.dataRecieved){
                    drl.dataChange();
                }
                
            }
            else if((char)newData[newData.length-1] == '\n'){
                if(newData[newData.length-2] == 'k'){
                    this.recieved = "ok";
                    System.out.println("Ok");
                }
            }
            //Change after implementing String analysis
            else{
            }
            for(int i=0;i<newData.length;i++){
                    this.data += (char)newData[i];
                }
            for(DataRecievedListener drl : this.dataRecieved){
                    drl.dataChange();
                }
        }
        else if(event.getEventType() == SerialPort.LISTENING_EVENT_PORT_DISCONNECTED){
            System.err.println("Error: Printer Disconnected!");
            long startTime = System.currentTimeMillis();
            while(true){
                if((startTime - System.currentTimeMillis())/ 1000F < 10){
                    System.out.println("Hello");
                    try{
                        this.CONN_PORT.openPort();
                        break;
                    } catch(Exception e){
                        System.err.println(e.getMessage());
                    }
                }
                else{
                    System.out.println("World");
                    break;
                }
            }
        }
    }
}
