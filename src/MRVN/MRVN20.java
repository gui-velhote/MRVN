/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package MRVN;

import printer.Printer;
import com.fazecast.jSerialComm.*;
import java.util.Scanner;
import java.util.ArrayList;

/**
 *
 * @author Loki
 */
public class MRVN20 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        Scanner input = new Scanner(System.in);
        
        ArrayList<Printer> printers = new ArrayList();
        
        for(SerialPort port : SerialPort.getCommPorts()){
            printers.add(new Printer(port));
        }
        
        System.out.println("Digite o comando: ");
        
        String gcode;
        String path;
        
        while(true){
            switch(input.nextLine()){
                case "t":
                    System.out.println("Digite o gcode: ");
                    gcode = input.nextLine();
                    for(Printer p : printers){
                        p.sendInfo(gcode);
                    }
                    
                    break;
                
                case "T":
                    System.out.println("Digite o caminho: ");
                    path = gcode = input.nextLine();
                    for(Printer p : printers){
                        p.printFile(path);
                    }
                    
                    break;
                
                case "P":
                    System.out.println("Digite o caminho: ");
                    path = gcode = input.nextLine();
                    for(Printer p : printers){
                       p.sendFile(path);
                    }
                    break;
                case "p":
                    for(Printer p : printers){
                       p.listFiles();
                    }
                    break;
                case "0":
                    System.exit(0);

            }
        }
        
    }
    
}
