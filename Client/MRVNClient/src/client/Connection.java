/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.regex.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Loki
 */
public class Connection {
    
    private InetAddress address;
    private DatagramSocket aSocket;
    private int currentLine;
    private ArrayList<String> fileRead = new ArrayList();
    
    private final int PORT;
    private final Pattern PRINTER_COUNT_PATTERN = Pattern.compile("PRINTER_COUNT=[0-9]+");
    
    /**
     * Constructor, save the address of server to controll the printers
     * @param address 
     */
    public Connection(String address){
        
        /* Add the address to connect to */
        this.PORT = 7042;
        try{
            this.address = InetAddress.getByName(address);
            this.aSocket = new DatagramSocket();
        }catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
            this.address = null;
        }
    }
    
    public void sendInfo(String info){
        
        String printerSelection = "USE_ALL_PRINTERS=1\n";
        
        info = printerSelection + info;
        
        byte[] m = info.getBytes();
        int size = info.length();
        try{
            DatagramPacket infoPacket =
                        new DatagramPacket(m, size, this.address, this.PORT);
            aSocket.send(infoPacket);
            
        } catch(IOException e){
            System.out.println("IO: " + e.getMessage());
        }
        
    }
    
    public void sendInfo(String info, boolean waitResponse){
        
        String printerSelection = "USE_ALL_PRINTERS=1\n";
        
        info = printerSelection + info;
        
        byte[] m = info.getBytes();
        int size = info.length();
        try{
            DatagramPacket infoPacket =
                        new DatagramPacket(m, size, this.address, this.PORT);
            aSocket.send(infoPacket);
            
            if(waitResponse){
                byte[] buffer = new byte[1000];
                DatagramPacket request = 
                        new DatagramPacket(buffer, buffer.length);

                this.aSocket.receive(request);

                byte[] data = request.getData();

                String msg = (new String(data));

                System.out.println(msg);

                if(this.PRINTER_COUNT_PATTERN.matcher(msg).find()){
                    MRVN.setPrinterCount(Double.valueOf(msg.replaceAll("ok|[\n]","").split("=")[1]));
                }
            }
            
        } catch(IOException e){
            System.out.println("IO: " + e.getMessage());
        }
        
    }
    
    public void sendInfo(String info, boolean waitResponse, int index){
        
        String printerSelection = "PRINTER_TO_USE=" + index + "\n";
        
        info = printerSelection + info;
        
        byte[] m = info.getBytes();
        int size = info.length();
        try{
            DatagramPacket infoPacket =
                        new DatagramPacket(m, size, this.address, this.PORT);
            aSocket.send(infoPacket);
            
            if(waitResponse){
                byte[] buffer = new byte[1000];
                DatagramPacket request = 
                        new DatagramPacket(buffer, buffer.length);

                this.aSocket.receive(request);

                byte[] data = request.getData();

                String msg = (new String(data));

                System.out.println(msg);

                if(this.PRINTER_COUNT_PATTERN.matcher(msg).find()){
                    MRVN.setPrinterCount(Double.valueOf(msg.replaceAll("ok|[\n]","").split("=")[1]));
                }
            }
            
        } catch(IOException e){
            System.out.println("IO: " + e.getMessage());
        }
        
    }
    
    public void sendInfo(byte[] info){
        
        int infoSize = info.length;
        try{
        DatagramPacket infoPacket =
                    new DatagramPacket(info, infoSize, this.address, this.PORT);
        aSocket.send(infoPacket);
        } catch(IOException e){
            System.out.println("IO: " + e.getMessage());
        }
    }
    
    public void sendFile(String path){
        sendInfo("M110 N0");
        
        BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(path));
                        this.currentLine = 1;
			String line = reader.readLine();
                        
                        this.fileRead.add("START_OF_FILE");
                        this.fileRead.add("USE_ALL_PRINTERS=1");
                        
			while (line != null) {
                            
                            line = line.replaceAll(";.*", "").strip();
                            
                            char[] lineArray = line.toCharArray();
                            
                            line = "N" + this.currentLine + " " + line;
                            
                            byte[] gcodeArray = line.getBytes();
                            byte charsum = 0;

                            for(int i=0;i<gcodeArray.length;i++){
                                charsum ^= gcodeArray[i];
                            }
                            
                            line += "*" + ((int)charsum);
                            //System.out.println(charsum);
                            
                            if((lineArray.length > 0) && (lineArray[0] != ';')){
                                
                                //System.out.println(line);
                                this.fileRead.add(line);
				///System.out.println(line);
                                this.currentLine++;
                            }
                            // read next line
                            line = reader.readLine();
			}
			reader.close();
                        
                        this.fileRead.add("END_OF_FILE");
                        
                        this.currentLine = 0;
                        
                        while(this.currentLine < this.fileRead.size()){
                            String info = this.fileRead.get(this.currentLine);
                            
                            //System.out.println("Sending line: " + info);
                            
                            byte[] m = info.getBytes();
                            int size = info.length();
                            DatagramPacket infoPacket =
                                        new DatagramPacket(m, size, this.address, this.PORT);
                            aSocket.send(infoPacket);
                            
                            byte[] buffer = new byte[1000];
                            DatagramPacket request = 
                                    new DatagramPacket(buffer, buffer.length);

                            this.aSocket.receive(request);

                            byte[] data = request.getData();

                            String msg = (new String(data));

                            //System.out.println(msg);
                            
                            this.currentLine++;
                        }
                        
		} catch (IOException e) {
			System.err.println(e.getMessage());
                }
		
    }
    
    public void sendFile(String path, int printerIndex){
        sendInfo("M110 N0");
        
        BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(path));
                        this.currentLine = 0;
			String line = reader.readLine();
                        
			while (line != null) {
                            
                            char[] lineArray = line.toCharArray();
                            
                            line = "N" + this.currentLine + " " + line;
                            
                            byte[] gcodeArray = line.getBytes();
                            byte charsum = 0;

                            for(int i=0;i<gcodeArray.length;i++){
                                charsum ^= gcodeArray[i];
                            }
                            
                            line += "*" + ((int)charsum);
                            //System.out.println(charsum);
                            
                            if((lineArray.length > 0) && (lineArray[0] != ';')){
                                
                                //System.out.println(line);
                                this.fileRead.add(line);
				//System.out.println(line);
                                this.currentLine++;
                            }
                            // read next line
                            line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
    }
    
}
