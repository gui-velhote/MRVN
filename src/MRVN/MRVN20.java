/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package MRVN;

import printer.Printer;
import com.fazecast.jSerialComm.*;
import java.util.Scanner;

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
        
        for(SerialPort ports : SerialPort.getCommPorts()){
            System.out.println(ports.getDescriptivePortName());
        }
        
        SerialPort port = SerialPort.getCommPorts()[0];
        Printer p = new Printer(port);
        
        System.out.println("Digite o comando: ");
        
        while(true){
            switch(input.nextLine()){
                case "t":
                    System.out.println("Digite o gcode: ");
                    p.sendInfo(input.nextLine());
                    break;
                case "T":
                    System.out.println("Digite o caminho: ");
                    p.printFile(input.nextLine());
                    break;
                case "P":
                    System.out.println("Digite o caminho: ");
                    p.sendFile(input.nextLine());
                    break;
                case "0":
                    System.exit(0);

            }
        }
        
    }
    
}
