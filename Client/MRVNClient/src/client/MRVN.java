/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client;

import java.util.ArrayList;

/**
 *
 * @author Loki
 */
public final class MRVN {
    
    /* Constants */
    public static final boolean SYNCHRONOUS = true;
    public static final boolean ASYNCHRONOUS = false;
    
    /* Final variables */
    private final ArrayList<PrinterData> PRINTERS;
    private final Connection CONNECTION;
    
    /* Local variables */
    private static int printerCount;
    
    private void test(){
        
        //System.out.println(this.PRINTER.toString());
    }
    
    public MRVN(String address){
        this.CONNECTION = new Connection(address); //Connects to server
        
        this.CONNECTION.sendInfo("MRVN1", true); //MRVN1 gets the number of printers connected
        System.out.println(printerCount);
        this.PRINTERS = new ArrayList();
        /* Create and stores intances of printer data */
        for(int i=0;i<printerCount;i++){
            this.PRINTERS.add(new PrinterData());
        }
        sendCommand("M105"); //initialize temperature info
    }
    
    public static void setPrinterCount(double count){
        printerCount = (int)count;
    }
    
    public void sendCommand(String command){
        this.CONNECTION.sendInfo(command);
    }
    
    public void sendCommand(String command, boolean waitResponse, ArrayList<Integer> printers){
        for(int index : printers){
            this.CONNECTION.sendInfo(command, waitResponse,index);
        }
    }
    
    public void sendCommand(String command, boolean waitResponse, int index){
        this.CONNECTION.sendInfo(command, waitResponse,index);
    }
    
    public void sendFile(String path){
        this.CONNECTION.sendFile(path);
    }
    
    public void sendFile(String path, int printerIndex){
        this.CONNECTION.sendFile(path, printerIndex);
    }

    public ArrayList<PrinterData> getPRINTERS() {
        return this.PRINTERS;
    }    
}
