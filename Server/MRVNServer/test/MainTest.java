/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */

import com.fazecast.jSerialComm.*;

/**
 *
 * @author Loki
 */
public class MainTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Printer p = new Printer(SerialPort.getCommPorts()[1], 0);
        p.sendFile("C:\\Users\\Loki\\Documents\\FEI\\TCC\\Server\\MRVN\\MRVNServer.jar");
    }
    
}
