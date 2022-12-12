package printer;

import java.util.ArrayList;
import com.fazecast.jSerialComm.*;

/**
 * Essa classe serve como a principal forma de verificar o sistema operacional
 * sendo utilizado para rodar o programa e procura pelas impressoras conectadas
 * ao computador.
 * 
 * @author Guilherme Velhote
 */
public class Connection extends Thread implements PrinterDisconnectListener{
    
    /* Definição de variáveios */
    private int connectedPrinters;  //conta a quantidade de impressoras conectadas
    private final String comPortName;   //O nome da porta a ser reconhecida
    private final ArrayList<Printer> printers = new ArrayList();    //Lista com as impressoras conectadas
    
    /**
     * Construtor da classe. Descobre o sistema operacional utilizado e
     * guarda o nome da porta em comPortName.
     */
    public Connection(){
        /* Checando sistema operacional */
        if(System.getProperty("os.name").contains("Windows")){
            this.comPortName = "CH340";
        }
        /* Unix-based utiliza o nome USB Serial */
        else{
            this.comPortName = "USB Serial";
        }
    }
    
    /**
     * Retorna a impressora com o indice passado.
     * @param index
     * @return 
     */
    public Printer getPrinter(int index){
        return this.printers.get(index);
    }
    
    /**
     * Retorna um array com todas as impressoras conectadas.
     * @return 
     */
    public Printer[] getPrinters(){
        Printer[] printersArray = new Printer[this.printers.size()];
        return this.printers.toArray(printersArray);
    }
    
    /**
     * Método utilizado para inicializar a Thread utilizada para a conexão
     * com as impressoras.
     */
    @Override
    public void run(){
        System.out.println("Running");
        /* Loop de execução para verificar se alguma impressora se conectou */
        while(true){
            /* Verifica se existem impressoras conectadas */
            if(this.printers.size() == 0){
                /* Se não existem conexões, procura por impressoras e se conecta a elas */
                int i=0;
                for(SerialPort s : SerialPort.getCommPorts()){
                    System.out.println(s.getDescriptivePortName());
                    if(s.getDescriptivePortName().contains(this.comPortName)){
                        Printer p = new Printer(i,s);
                        p.addDisconnectListener(this);
                        this.printers.add(p);
                        i++;
                    }
                }
                System.out.println("Printer Connected Size: " + this.printers.size());
                this.connectedPrinters = this.printers.size();
            }
            /* Caso já existam impressoras conectadas verifica se existem impressoras novas */
            else{
                int numberOfPrinters = 0;
                ArrayList<SerialPort> serialPorts = new ArrayList();
                for(SerialPort s : SerialPort.getCommPorts()){
                    if(s.getDescriptivePortName().contains(this.comPortName)){
                        serialPorts.add(s);
                        numberOfPrinters++;
                    }
                }
                /* Se o número de impressoras conectadas for maior que o de
                   impressoras conectadas, conecta a nova impressora */
                if(this.connectedPrinters < numberOfPrinters){
                    int match = 0;
                    /* Procura para ver se já foi conectada a impressora */
                    for(int i=0;i<=numberOfPrinters-1;i++){
                        for(int j=0;j<=this.connectedPrinters-1;j++){
                            if(serialPorts.get(i) == this.printers.get(j).getCONN_PORT()){
                                System.out.println(this.printers.get(j).getCONN_PORT().getPortDescription());
                                match = 1;
                                break;
                            }
                            else{
                                continue;
                            }
                        }
                        /* Se a impressora não está coenectada, conecta a ela */
                        if(match == 0){
                            System.out.println("Printer Connected Size: " + this.printers.size());
                            this.connectedPrinters = this.printers.size();
                            Printer p = new Printer(this.printers.size(), serialPorts.get(i));
                            p.addDisconnectListener(this);
                            this.printers.add(p);
                        }
                    }
                    this.connectedPrinters = this.printers.size();
                    System.out.println(this.connectedPrinters);
                }   
            }
        }
    }
    
    /**
     * Função de tratamento para reconexão de impressoras.
     * @param printer 
     */
    @Override
    public void pinterDisconnect(Printer printer){
        System.out.println(this.printers.remove(this.printers.indexOf(printer)));
        System.out.println("Printer disconnected Size: " + this.printers.size());
        this.connectedPrinters = this.printers.size();
    }
    
}
