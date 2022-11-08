/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.regex.*;
import java.util.ArrayList;

/**
 *
 * @author Loki
 */
public class ClientServer extends Thread{
    
    private final int PORT;
    private final ArrayList<PrinterData> PRINTERS;
    private final Pattern TEMP_PATTERN = Pattern.compile("[T][:][0-9]+[\\.][0-9]");
    private final Pattern PRINTER_PATTERN = Pattern.compile("SENT_FROM_PRINTER=[0-9]+");
    
    private DatagramSocket aSocket;
    
    public ClientServer(int port, ArrayList<PrinterData> printersData){
        this.PRINTERS = printersData;
        this.PORT = port;
        try{
            this.aSocket = new DatagramSocket(this.PORT);
        } catch(SocketException e){
                    System.out.println("Socket: " + e.getMessage());
        }
    }
    
    @Override
    public void run(){
    System.out.println("Server port: " + this.PORT);
            try{
                while(true){
                    
                    byte[] buffer = new byte[1000];
                    DatagramPacket request = 
                            new DatagramPacket(buffer, buffer.length);

                    this.aSocket.receive(request);
                    
                    byte[] data = request.getData();
                        
                    String msg = (new String(data));                    
                    
                    Matcher printerMatcher = this.PRINTER_PATTERN.matcher(msg);
                    
                        if(printerMatcher.find()){

                            int index = Integer.valueOf(printerMatcher.group().strip().replaceAll("[^0-9]", ""));
                            msg = msg.replaceAll(this.PRINTER_PATTERN.pattern(), "");

                            if(this.TEMP_PATTERN.matcher(msg).find()){
                                msg = msg.replaceAll("([^BT@: \n/0123456789\\.])", "");

                                msg = msg.replaceAll("(@:[0-9]+)", "");
                                msg = msg.replaceAll("[T: \n]", "");

                                String[] temps = msg.split("[/B]");
                                //System.out.println("\"" + msg + "\"");
                                //System.out.println("Setting temperatures:");

                                this.PRINTERS.get(index).setTempNozzle(Double.valueOf(temps[0]));
                                this.PRINTERS.get(index).setFinalNozzleTemp(Double.valueOf(temps[1]));
                                this.PRINTERS.get(index).setTempBase(Double.valueOf(temps[2]));
                                this.PRINTERS.get(index).setFinalBaseTemp(Double.valueOf(temps[3]));
                                /*
                                System.out.println(this.PRINTERS.get(index).getTempNozzle());
                                System.out.println(this.PRINTERS.get(index).getFinalNozzleTemp());
                                System.out.println(this.PRINTERS.get(index).getTempBase());
                                System.out.println(this.PRINTERS.get(index).getFinalBaseTemp());
                                */
                            }

                            System.out.println("Message from printer " + index + ":\n" + msg);
                        }
                }
            } catch(SocketException e){
                    System.out.println("Socket: " + e.getMessage());
            } catch(IOException e){
                    System.out.println("IO: " + e.getMessage());
            }
    }
}
