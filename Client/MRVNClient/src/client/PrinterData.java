/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client;

/**
 *
 * @author Loki
 */
public class PrinterData {
    
    private double tempNozzle;
    private double tempBase;
    private double finalNozzleTemp;
    private double finalBaseTemp;

    public PrinterData(){
        this.tempBase = 0;
        this.tempNozzle = 0;
        this.finalBaseTemp = 0;
        this.finalNozzleTemp = 0;
    }
    
    public PrinterData(int tempNozzle, int tempBase){
        this.tempNozzle = tempNozzle;
        this.tempBase = tempBase;
        this.finalBaseTemp = 0;
        this.finalNozzleTemp = 0;
    }
    
    public PrinterData(int tempNozzle, int tempBase, int finalNozzleTemp, int finalBaseTemp){
        this.tempNozzle = tempNozzle;
        this.tempBase = tempBase;
        this.finalBaseTemp = finalBaseTemp;
        this.finalNozzleTemp = finalNozzleTemp;
    }
        
    public double getTempNozzle() {
        return tempNozzle;
    }

    public void setTempNozzle(double tempNozzle) {
        this.tempNozzle = tempNozzle;
    }

    public double getTempBase() {
        return tempBase;
    }

    public void setTempBase(double tempBase) {
        this.tempBase = tempBase;
    }

    public double getFinalNozzleTemp() {
        return finalNozzleTemp;
    }

    public void setFinalNozzleTemp(double finalNozzleTemp) {
        this.finalNozzleTemp = finalNozzleTemp;
    }

    public double getFinalBaseTemp() {
        return finalBaseTemp;
    }

    public void setFinalBaseTemp(double finalBaseTemp) {
        this.finalBaseTemp = finalBaseTemp;
    }
    
    
    @Override
    public String toString(){
        return ("Nozzle temp: " + this.tempNozzle + " / Nozzle final temp: "
                + this.finalNozzleTemp + " / Base temp: " + this.tempBase
                + " / Base final temp: " + this.finalBaseTemp);
    }
    
    
}
