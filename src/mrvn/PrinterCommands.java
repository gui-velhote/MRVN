/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mrvn;

import com.fazecast.jSerialComm.*;

/**
 *
 * @author Loki
 */
public class PrinterCommands extends Thread {
    
    //Declaration of static constants for commands input
    public static final int COMMAND_USER_INPUT = 0x01;
    public static final int COMMAND_FIRMWARE_INFO = 0x02;
    public static final int COMMAND_SET_AUTO_TEMP_ON = 0x04;
    public static final int COMMAND_SEND_FILE = 0x08;
    
    private final Printer PRINTER;
    private final int command;
    private final String gcode;
    
    public PrinterCommands(Printer p, int command){
        this.PRINTER = p;
        this.command = command;
        this.gcode = null;
    }
    
    public PrinterCommands(Printer p, int command, String gcode){
        this.PRINTER = p;
        this.command = command;
        this.gcode = gcode;
    }
    
    public void setTemperatureAutoInfo(int mode){
        this.PRINTER.sendInfo("M155 S" + mode + "\n");
    }
    
    @Override
    public void run(){
        //System.out.println(this.command);
        switch(this.command){
            case COMMAND_USER_INPUT:
                this.PRINTER.sendInfo(this.gcode);
                //System.out.println("Sent \"" + gcode + "\" to printer" + this.PRINTER.getCONN_PORT().getDescriptivePortName());
                break;
            case COMMAND_FIRMWARE_INFO:
                
                break;
            case COMMAND_SEND_FILE:
                this.PRINTER.sendFile(gcode);
                break;
            case COMMAND_SET_AUTO_TEMP_ON:
                setTemperatureAutoInfo(1);
                break;
            default:
                break;
        }
    }
}
