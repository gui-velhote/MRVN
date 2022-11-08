/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package client;

import java.net.InetAddress;
import java.util.Scanner;

/**
 *
 * @author Loki
 */
public class MRVNClient {
    
    public static void main(String[] args){
        
        Scanner input = new Scanner(System.in);
        
        try{
            MRVN mrvn = new MRVN("192.168.15.18");
            //MRVN mrvn = new MRVN("localhost");
            
            ClientServer sv = new ClientServer(7420, mrvn.getPRINTERS());
            sv.start();
            
            String command;
            
            //mrvn.sendFile("C:\\Users\\Loki\\Documents\\FEI\\TCC\\Tests\\Youtek_2h2_B8_0410.gcode");
            
            while(true){
                System.out.println("Insert Command:");
                command = input.nextLine();
                
                if(command.equals("0")){
                    System.exit(0);
                }
                else if(command.equals("t")){
                    System.out.println("Enter Gcode:");
                    mrvn.sendCommand(input.nextLine(),false,0);
                }
                else if(command.equals("T")){
                    System.out.println("Enter file path:");
                    mrvn.sendFile(input.nextLine());
                }
            }
            
        } catch(Exception e){
            e.printStackTrace();
        }
        
    }
    
}
