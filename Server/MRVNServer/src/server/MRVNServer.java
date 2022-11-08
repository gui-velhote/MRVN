/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package server;

import java.util.Scanner;

/**
 *
 * @author Loki
 */
public class MRVNServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        try{
            System.out.print("Port: ");
            Server sv = new Server(input.nextInt());
            sv.start();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
}
