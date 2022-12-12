package printer;

/**
 * Listener para descobrir se a impressora foi desconectada.
 * @author Guilherme Velhote
 */
public interface PrinterDisconnectListener {
    
    public void pinterDisconnect(Printer printer);
    
}
