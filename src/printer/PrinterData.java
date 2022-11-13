/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package printer;

/**
 *
 * @author Loki
 */
public class PrinterData {
    
    private String printerName;
    private int tipTemp;
    private int baseTemp;
    private int tipFinalTemp;
    private int baseFinalTemp;
    
    public void parseData(String data){
        
        String temps = data.replace("/", " ").replaceAll("[^0-9 ]", "");
        
        String[] parsedTemps = temps.split(" ");
        
        this.tipTemp = Integer.valueOf(parsedTemps[0]);
        this.tipFinalTemp = Integer.valueOf(parsedTemps[1]);
        this.baseTemp = Integer.valueOf(parsedTemps[2]);
        this.baseFinalTemp = Integer.valueOf(parsedTemps[3]);
        
    }
    
    public void setPrinterName(String name){
        this.printerName = name;
    }
    
    public String getPrinterName(){
        return this.printerName;
    }
    
}
