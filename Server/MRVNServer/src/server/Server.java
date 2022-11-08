/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.InetAddress;
import java.util.ArrayList;
import mrvn.Printer;
import com.fazecast.jSerialComm.*;
import java.util.regex.*;

/**
 *
 * @author Loki
 */
public class Server extends Thread implements mrvn.SendToClient{
    
    private final int PORT;
    private final Pattern MCODE_PATTERN = Pattern.compile("M[0-9]+");
    private final Pattern GCODE_PATTERN = Pattern.compile("G[0-9]+");
    private final Pattern MRVN_CODE_PATTERN = Pattern.compile("MRVN[0-9]+");
    private final Pattern PRINTER_TO_USE_PATTERN = Pattern.compile("PRINTER_TO_USE=[0-9]+");
    private final Pattern USE_ALL_PRINTERS_PATTERN = Pattern.compile("USE_ALL_PRINTERS=[01]+");
    private final Pattern START_OF_FILE_PATTERN = Pattern.compile("START_OF_FILE");
    private final Pattern END_OF_FILE_PATTERN = Pattern.compile("END_OF_FILE");
    
    private ArrayList<Printer> printers = new ArrayList();
    private ArrayList<InetAddress> conectedAddress = new ArrayList();
    private ArrayList<String> fileReceived = new ArrayList();
    
    private DatagramSocket aSocket;
    private InetAddress senderAddress;
    private int senderPort;
    private int usePrinter;
    private boolean useAllPrinters;
    private boolean storeData;
    
    public Server(int port){
        this.PORT = port;
        this.senderAddress = null;
        this.storeData = false;
        this.useAllPrinters = true;
        
        for(SerialPort p : SerialPort.getCommPorts()){
            //System.out.println(p.getDescriptivePortName());
            Printer r = new Printer(p,this.printers.size());
            r.addServerListener(this);
            this.printers.add(r);
        }
        
        try{
            this.aSocket = new DatagramSocket(this.PORT);
        } catch(SocketException e){
                    System.out.println("Socket: " + e.getMessage());
        }
    }
    
    public void connect(){
        
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
                    this.senderAddress = request.getAddress();
                    this.senderPort = request.getPort();
                    
                    if(!this.conectedAddress.contains(request.getAddress())){
                        this.conectedAddress.add(request.getAddress());
                    }
                    
                    this.senderPort = request.getPort();
                    
                    byte[] data = request.getData();
                        
                    String msg = (new String(data));
                    
                    //System.out.println("Got message:\n\"" + msg + "\"");
                    
                    Matcher matcherMRVN = this.MRVN_CODE_PATTERN.matcher(msg);
                    
                    if(this.storeData){
                        //System.out.println("Storing");
                        if(this.END_OF_FILE_PATTERN.matcher(msg).find()){
                            System.out.println("Ending file read");
                            this.storeData = false;
                            System.out.println("Starting print");
                            if(this.useAllPrinters){
                                for(Printer p : this.printers){
                                    p.printFile(this.fileReceived);
                                }
                            }
                            else{
                                this.printers.get(this.usePrinter).printFile(this.fileReceived);
                            }
                        }
                        else if(this.USE_ALL_PRINTERS_PATTERN.matcher(msg).find()){
                            this.useAllPrinters = true;
                        }
                        else if(this.PRINTER_TO_USE_PATTERN.matcher(msg).find()){
                            this.useAllPrinters = false;
                            Matcher matchPrinter = this.PRINTER_TO_USE_PATTERN.matcher(msg);
                            matchPrinter.find();
                            String printerIndex = matchPrinter.group().replaceAll("[^0-9]", "");
                            //System.out.println("Printer index = " + printerIndex);
                            this.usePrinter = Integer.valueOf(printerIndex);
                        }
                        else{
                            this.fileReceived.add(msg);
                        }
                        sendData("ok");
                    }
                    
                    else{
                        if(this.START_OF_FILE_PATTERN.matcher(msg).find()){
                                this.storeData = true;
                                System.out.println("Starting file read");
                                sendData("ok");
                                
                            }
                        
                        else if((this.MCODE_PATTERN.matcher(msg).find()) | this.GCODE_PATTERN.matcher(msg).find()){
                            
                            if(this.USE_ALL_PRINTERS_PATTERN.matcher(msg).find()){
                                msg = msg.replaceAll(this.USE_ALL_PRINTERS_PATTERN.pattern(), "").strip();
                                //System.out.println("Got message:\n\"" + msg + "\"");
                                for(Printer p : this.printers){
                                    p.sendInfo(msg);
                                }
                            }

                            else if(this.PRINTER_TO_USE_PATTERN.matcher(msg).find()){
                                this.useAllPrinters = false;
                                Matcher matchPrinter = this.PRINTER_TO_USE_PATTERN.matcher(msg);
                                matchPrinter.find();
                                String printerIndex = matchPrinter.group().replaceAll("[^0-9]", "");
                                //System.out.println("Printer index = " + printerIndex);
                                this.usePrinter = Integer.valueOf(printerIndex);
                                msg = msg.replaceAll(this.PRINTER_TO_USE_PATTERN.pattern() + "\n","");
                                //System.out.println("Message sent: \"" + msg + "\"");
                                this.printers.get(this.usePrinter).sendInfo(msg);
                            }
                        }

                        else if(matcherMRVN.find()){
                            String match = matcherMRVN.group();
                            System.out.println("Match: " + match);
                            System.out.println(matcherMRVN.group().replaceAll("[^0-9]+", ""));
                            /* Code to send printer count */
                            if(match.equals("MRVN1")){
                                sendData("PRINTER_COUNT=" + this.printers.size() + "\nok\n");
                            }
                        }
                    }
                    
                }
            } catch(SocketException e){
                    System.out.println("Socket: " + e.getMessage());
            } catch(IOException e){
                    System.out.println("IO: " + e.getMessage());
            } 
    }
    
    @Override 
    public void sendData(String data){
        try{
            byte[] sendData = data.getBytes();
            DatagramPacket infoPacket = null;
            //System.out.println("the data is:\n" + data);
            //System.out.println("Sending to port: " + this.senderPort);
            infoPacket = new DatagramPacket(sendData, sendData.length, this.senderAddress, this.senderPort);
            this.aSocket.send(infoPacket);
            
        } catch(SocketException e){
                System.out.println("Socket: " + e.getMessage());
        } catch(IOException e){
                System.out.println("IO: " + e.getMessage());
        }
    }
    
    @Override
    public void sendData(String data, int index){
        try{
            String dataToSend = "SENT_FROM_PRINTER=" + index + "\n" + data;
            byte[] sendData = dataToSend.getBytes();
            DatagramPacket infoPacket = null;
            System.out.println("the data is:\n" + data);
                    
            for(InetAddress ad : this.conectedAddress){
                infoPacket = new DatagramPacket(sendData, sendData.length, ad, 7420);
                this.aSocket.send(infoPacket);
            }
            
        } catch(SocketException e){
                System.out.println("Socket: " + e.getMessage());
        } catch(IOException e){
                System.out.println("IO: " + e.getMessage());
        }
    }
}
