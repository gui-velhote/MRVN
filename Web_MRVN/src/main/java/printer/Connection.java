/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package printer;

import java.util.ArrayList;
import com.fazecast.jSerialComm.*;

/**
 *
 * @author Loki
 */
public class Connection extends Thread implements PrinterDisconnectListener{
    
    private int connectedPrinters;
    private String comPortName;
    private ArrayList<Printer> printers = new ArrayList();
    
    public Connection(){
        System.out.println(System.getProperty("os.name"));
        if(System.getProperty("os.name").contains("Windows")){
            this.comPortName = "CH340";
        }
    }
    
    public Printer getPrinter(int index){
        return this.printers.get(index);
    }
    
    public Printer[] getPrinters(){
        Printer[] printersArray = new Printer[this.printers.size()];
        return this.printers.toArray(printersArray);
    }
    
    @Override
    public void run(){
        System.out.println("Running");
        while(true){
            if(this.printers.size() == 0){
                for(SerialPort s : SerialPort.getCommPorts()){
                    System.out.println(s.getDescriptivePortName());
                    if(s.getDescriptivePortName().contains(this.comPortName)){
                        Printer p = new Printer(s);
                        p.addDisconnectListener(this);
                        this.printers.add(p);
                    }
                }
                System.out.println("Printer Connected Size: " + this.printers.size());
                this.connectedPrinters = this.printers.size();
            }
            else{
                int numberOfPrinters = 0;
                ArrayList<SerialPort> serialPorts = new ArrayList();
                for(SerialPort s : SerialPort.getCommPorts()){
                    if(s.getDescriptivePortName().contains(this.comPortName)){
                        serialPorts.add(s);
                        numberOfPrinters++;
                    }
                }
                if(this.connectedPrinters < numberOfPrinters){
                    int match = 0;
                    for(int i=0;i<=numberOfPrinters-1;i++){
                        for(int j=0;j<=this.connectedPrinters-1;j++){
                            if(serialPorts.get(i) == this.printers.get(j).getCONN_PORT()){
                                System.out.println(this.printers.get(j).getCONN_PORT().getPortDescription());
                                match = 1;
                                break;
                            }
                            else{
                                continue;
                            }
                        }
                        if(match == 0){
                            System.out.println("Printer Connected Size: " + this.printers.size());
                            this.connectedPrinters = this.printers.size();
                            Printer p = new Printer(serialPorts.get(i));
                            p.addDisconnectListener(this);
                            this.printers.add(p);
                        }
                    }
                    this.connectedPrinters = this.printers.size();
                    System.out.println(this.connectedPrinters);
                }   
            }
        }
    }
    
    @Override
    public void pinterDisconnect(Printer printer){
        System.out.println(this.printers.remove(printer));
        System.out.println("Printer disconnected Size: " + this.printers.size());
        this.connectedPrinters = this.printers.size();
    }
    
}
