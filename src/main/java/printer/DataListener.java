package printer;

/**
 * Listener para alterações de dados.
 * @author Guilherme Velhote
 */
public interface DataListener {
    
    public void tempChange(int index, PrinterData pd);
    
    public void percentageChange(int index, int percentage);
    
}
