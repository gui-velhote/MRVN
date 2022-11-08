/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mrvn;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Loki
 */
public class FileSending extends Thread{
    
    private String path;
    private ArrayList<DataListener> dataListeners = new ArrayList();
    private Printer printer;
    private String sentLine;
    
    public FileSending(String path, ArrayList<DataListener> dataListeners, Printer p){
        this.path = path;
        this.dataListeners = dataListeners;
        this.printer = p;
        this.sentLine = "";
    }
    
    private void sendFile(String path){
        BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(path));
			String line = reader.readLine();
                        //char[] outputArray;
			while (line != null) {
                            char[] lineArray = line.toCharArray();
                            if((lineArray.length > 0) && (lineArray[0] != ';')){
                                printer.sendInfo(line);
                                this.printer.sentLine += line + "\n";
                                for(DataListener drl : this.dataListeners){
                                    drl.sentCommand();
                                }
				//System.out.println(line);
                                //while(this.recieved!="ok");
                                
                            }
                            // read next line
                            line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
    }
    
    @Override 
    public void run(){
        sendFile(this.path);
        this.interrupt();
    }
}
