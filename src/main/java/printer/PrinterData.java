package printer;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Guilherme Velhote
 */
public class PrinterData{
    
    /* Definição de variaveis finais */
    private final Pattern DATA_PATTERN = Pattern.compile("[-]?[0-9]+([.][0-9]+)?");
    
    /* Definição de variaveis de alocação de dados */
    private String printerName;     // Nome da impressora
    private int printPercentage;    // Porcentagem de impressão
    
    /* Variaveis de temperaturas */
    private double tipTemp;
    private double tipFinalTemp;
    private double baseTemp;
    private double baseFinalTemp;
    
    /**
     * Contrutor da classe.
     */
    public PrinterData(){
        this.printPercentage = 0;   // Seta porcentagem de impressão como 0%
    }
    
    /**
     * Função para tratamento de dados de temperatura. Como as informações de
     * temperatura são recebidas sempre com o mesmo formato, não é necessário
     * se preocupar com erros de aquisição de dados.
     * @param data 
     */
    public void parseTempData(String data){
        
        ArrayList<Double> tempDataList = new ArrayList();   // Lista para guardar valores de temperatura
        Matcher match = this.DATA_PATTERN.matcher(data);    // Matcher para achar valores de temperatura
        
        /* Procura por dados na String data */
        while(match.find()){
            tempDataList.add(Double.valueOf(match.group()));    // Para cada valor achado, adiciona a lista
        }
        
        /* Guarda temperaturas nas variáveis certas */
        this.tipTemp = tempDataList.get(0);
        this.tipFinalTemp = tempDataList.get(1);
        this.baseTemp = tempDataList.get(2);
        this.baseFinalTemp = tempDataList.get(3);
        
    }
    
    /**
     * Seta o valor da porcentagem de impressão pelo valor passado.
     * @param percentage 
     */
    public void setPrinterPercentage(int percentage){
        this.printPercentage = percentage;
    }
    
    /**
     * Retorna o valor da porcentagem de impressão.
     * @return 
     */
    public int getPrinterPercentage(){
        return this.printPercentage;
    }
    
    /**
     * Seta o nome da impressora pelo nome passado.
     * @param name 
     */
    public void setPrinterName(String name){
        this.printerName = name;
    }
    
    /**
     * Retorna o nome da impressora.
     * @return 
     */
    public String getPrinterName(){
        return this.printerName;
    }

    /**
     * Retorna o valor da temperatura do bico.
     * @return 
     */
    public double getTipTemp() {
        return tipTemp;
    }
    
    /**
     * Retorna o valor da temperatura da base.
     * @return 
     */
    public double getBaseTemp() {
        return baseTemp;
    }

    /**
     * Retorna o valor a ser atingido pelo bico.
     * @return 
     */
    public double getTipFinalTemp() {
        return tipFinalTemp;
    }
    
    /**
     * Retorna o valor a ser atingido pela base.
     * @return 
     */
    public double getBaseFinalTemp() {
        return baseFinalTemp;
    }
    
}
