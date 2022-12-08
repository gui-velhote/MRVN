/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package printer;

/**
 *
 * @author Loki
 */
public interface DataListener {
    
    public void tempChange(int index, PrinterData pd);
    
    public void percentageChange(int index, int percentage);
    
}
