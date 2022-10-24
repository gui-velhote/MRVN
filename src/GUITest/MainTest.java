/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package GUITest;

import com.fazecast.jSerialComm.SerialPort;
import mrvn.Printer;
import java.util.ArrayList;
        
/**
 *
 * @author Loki
 */
public class MainTest {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        
        
        SerialPort[] portsAvailable = SerialPort.getCommPorts();
        ArrayList<Printer> printers = new ArrayList();
        ArrayList<PrinterPanel> pPanel = new ArrayList();
        
        
        for(SerialPort S : portsAvailable){
            //System.out.println("Port: " + S.getDescriptivePortName());
            if(S.getDescriptivePortName().contains("USB-SERIAL CH340")){
                Printer p = new Printer(S);
                printers.add(p);
                pPanel.add(new PrinterPanel(p));
            }
        }
        
        TestGUI test = new TestGUI(printers);
        AllPrinters allPanel = new AllPrinters(printers);
        
        int X, Y, width, height, testWidth, testHeight;
        
        X = Y = 0;
        testWidth = width = 400;
        testHeight = height = 800;
        
        for(PrinterPanel p : pPanel){
            p.getTxtPrinter().setText("Printer " + (pPanel.indexOf(p) + 1));
            test.add(p);
            p.setBounds(X, Y, width, height);
            printers.get(pPanel.indexOf(p)).addListener(p);
            X += width + 10;
            testWidth += width;
        }
        //System.out.println("X: " + X + " Y: " + Y + " width: " + width + " height: " + height);
        test.add(allPanel);
        allPanel.setBounds(X, Y, width, height);
        allPanel.setVisible(true);
        testWidth += 50;
        
        test.setSize(testWidth, testHeight);
        
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TestGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TestGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TestGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TestGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                test.setVisible(true);
            }
        });
        
        
        /*
        Scanner input = new Scanner(System.in);
        ArrayList<Printer> printers = new ArrayList();
        //PrinterCommunication comm = new PrinterCommunication(SerialPort.getCommPort("USB Serial"));
        
        SerialPort[] portsAvailable = SerialPort.getCommPorts();
        
        for(SerialPort S : portsAvailable){
            System.out.println("Port: " + S.getDescriptivePortName());
            if(S.getDescriptivePortName().contains("USB-SERIAL CH340")){
                printers.add(new Printer(S));
            }
        }
        
        String command;
        
        while(true){
            
             System.out.println("""
                               f - Firmware info
                               a - Set temperature auto info on
                               A - Set temperature auto info off
                               p - Print Data last stored
                               F - Print file
                               P - Send file to printer
                               t - Send Gcode directly to printer
                               T - Test Gcode reader
                               S - Select one or more printers to work
                                
                               0 - exit""");
            command = input.nextLine();
            switch(command){
                case "t":
                    System.out.println("Gcode: ");
                    String gcode = input.nextLine();
                    for(int i=0;i<printers.size();i++){
                        (new PrinterCommands(printers.get(i), PrinterCommands.COMMAND_USER_INPUT, gcode)).start();
                    }
                    break;
                case "S":
                    System.out.println("Select which printer(s) to use: ");
                    int i=1;
                    for(Printer p : printers){
                        System.out.println(i + ": " + p.getCONN_PORT().getDescriptivePortName());
                    }
                case "0":
                    System.exit(0);
                    break;
                default:
                    System.out.println("Command not recognized");
                    break;
            }
        }*/
    }
    
}
