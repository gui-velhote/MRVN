/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package printer;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Loki
 */
public class PrinterData {
    
    
    private Pattern DATA_PATTERN = Pattern.compile("[0-9]+([.][0-9]+)?");
    
    private String printerName;
    private double tipTemp;
    private double baseTemp;
    private double tipFinalTemp;
    private double baseFinalTemp;
    
    public void parseData(String data){
        
        ArrayList<Double> dataList = new ArrayList();
        
        Matcher match = this.DATA_PATTERN.matcher(data);
        
        while(match.find()){
            dataList.add(Double.valueOf(match.group()));
        }
        
        this.tipTemp = dataList.get(0);
        this.tipFinalTemp = dataList.get(1);
        this.baseTemp = dataList.get(2);
        this.baseFinalTemp = dataList.get(3);
        
    }
    
    public void setPrinterName(String name){
        this.printerName = name;
    }
    
    public String getPrinterName(){
        return this.printerName;
    }

    public double getTipTemp() {
        return tipTemp;
    }

    public double getBaseTemp() {
        return baseTemp;
    }

    public double getTipFinalTemp() {
        return tipFinalTemp;
    }

    public double getBaseFinalTemp() {
        return baseFinalTemp;
    }
    
}
