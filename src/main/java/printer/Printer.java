package printer;

import com.fazecast.jSerialComm.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Essa classe serve como base do controle da impressora
 * @author Guilherme Velhote
 */
public class Printer implements SerialPortDataListener{
    
    /* Definição de variáveis finais */
    private final SerialPort CONN_PORT; // Porta da conexão
    private final int INDEX;    // Índice da impressora conectada
    private final ArrayList<String> filesInSD = new ArrayList(); // Lista os arquivos no cartão SD da impressora
    
    /* Listas para alocar listeners */
    private final ArrayList<CommandListener> sentDataListeners = new ArrayList();
    private final ArrayList<DataListener> dataListeners = new ArrayList();
    private final ArrayList<PrinterDisconnectListener> disconnectListener = new ArrayList();
    
    /* Definição de padrões de expressões regulares */
    private final Pattern ERROR_ON_LINE_READ = Pattern.compile("Resend: [0-9]+");
    private final Pattern DATA_RECIEVED = Pattern.compile("ok");
    private final Pattern TEMPERATURE_CHECK = Pattern.compile("T:[-]?[0-9]+[.][0-9]+ /[0-9]+[.][0-9]+ B:[-]?[0-9]+[.][0-9]+ /[0-9]+[.][0-9]+");
    private final Pattern POSITION_MONITOR = Pattern.compile("X:[0-9]+[.][0-9]+ Y:[0-9]+[.][0-9]+ Z:[0-9]+[.][0-9]+");
    private final Pattern FILE_OPEN_FAILED = Pattern.compile("open failed");
    private final Pattern FILE_IN_SD = Pattern.compile(".*[.].*[ ]");
    
    /* Definição de variáveis globais */
    private String data;    // Dados recebidos pela conexão
    private int printFile;  // Variável de controle de impressão
    private PrinterData printerData;    // Dados de monitoramento da impressora
    private SendCommand sendCommand;    // Objeto de envio de dados
    
    /**
     * Função de configuração de impressora
     */
    private void setupPrinter(){
        
        this.CONN_PORT.setComPortParameters(115200, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
        this.CONN_PORT.addDataListener(this);
        this.CONN_PORT.openPort(); // Abre a porta de comunicação
        sendInfo("M20");    // M20 lista os conteúdos do cartão SD
        sendInfo("M105");   // M105 recebe informações da firmware
        
    }
    
    /**
     * Inicializa as variáveis com seus valores iniciais
     */
    private void initializeVariables(){
        this.data = "";
        this.printFile = 0;
        this.printerData = new PrinterData();
    }
    
    /**
     * Construtora da classe, define a porta de conexão utilizada, o índice da impressora
     * além de chamar as funções de configuração e inicialização de variáveis
     * @param index
     * @param connPort 
     */
    public Printer(int index, SerialPort connPort){
        
        this.INDEX = index;
        this.CONN_PORT = connPort;
        setupPrinter();
        initializeVariables();
        
    }
    
    /**
     * Adiciona um CommandListener para a lista de listeners
     * @param toAdd 
     */
    public void addCommandListener(CommandListener toAdd){
        this.sentDataListeners.add(toAdd);
    }
    
    /**
     * Adiciona um DataListener para a lista de listeners
     * @param toAdd 
     */
    public void addDataListener(DataListener toAdd){
        this.dataListeners.add(toAdd);
    }
    
    /**
     * Adiciona um DisconnectListener para a lista de listeners
     * @param toAdd 
     */
    public void addDisconnectListener(PrinterDisconnectListener toAdd){
        this.disconnectListener.add(toAdd);
    }
    
    /**
     * Envia um gcode para a impressora a partir de uma instância de SendCommand
     * @param gcode 
     */
    public void sendInfo(String gcode){
        
        /* Configura o objeto para o envio de dados e envia o gcode */
        this.sendCommand = new SendCommand(gcode, 0, this);
        this.sendCommand.start();
    }
    
    /**
     * Método de impressão de arquivos, recebe a localização de um arquivo para o
     * processamento do arquivo e sucessivamente sua impressão
     * @param filePath 
     */
    public void printFile(String filePath){
        
        /* Seta a flag de impressão para 1 */
        this.printFile = 1;
                
        this.sendCommand = new SendCommand(filePath, 1, this);
        addCommandListener(this.sendCommand);
        this.sendCommand.start();
        
    }
    
    /**
     * Métido de envio de arquivos para o cartão SD da impressora, processo
     * semelhante ao de impressão de arquivos.
     * @param filePath 
     */
    public void sendFile(String filePath){
        
        /* Descobre o nome do arquivo */
        File file = new File(filePath);
        String filePathName = file.getName().replaceAll("[.].*", "").strip().toUpperCase();
        
        /* Procura arquivo na memória do cartão SD */
        for(String s : this.filesInSD){
            
            System.out.println(filePathName);
            String filesSDName = s.replaceAll("[.].*", "").strip();
            System.out.println(filesSDName);
            
            /* Se acha o arquivo, seleciona e manda imprimir do cartão */
            if(filePathName.equals(filesSDName)){
                System.out.println("file in SD Card: " + filesSDName);
                
                this.sendCommand = new SendCommand("M23" + filesSDName.toUpperCase() + ".GCO", 0, this);
                this.sendCommand.start();
                this.sendCommand = new SendCommand("M24", 0, this);
                this.sendCommand.start();
                return; // Retorna da função para não executar o processamento do arquivo
            }
        }
        
        /* Envia forma de impressão de arquivo a partir do cartão SD */
        this.sendCommand = new SendCommand(filePath, 2, this);
        this.sendCommand.start();
        
    }
    
    /**
     * Printa no terminal os conteúdos do cartão SD da impressora.
     */
    public void listFiles(){
        for(String s : this.filesInSD){
            System.out.println(s);
        }
    }
    
    /**
     * Retorna o objeto contendo os dados da impressora.
     * @return 
     */
    public PrinterData getPrinterData(){
        return this.printerData;
    }
    
    /**
     * Retorna a porta de conexão da impressora.
     * @return 
     */
    public SerialPort getCONN_PORT(){
        return this.CONN_PORT;
    }
    
    /**
     * Avissa para os DataListeners que a porcentagem de impressão foi alterada e
     * altera o valor da porcentagem no objeto com dados da impressora.
     * @param percentage 
     */
    public void percentageChange(int percentage){
        this.printerData.setPrinterPercentage(percentage);
        for(DataListener d : this.dataListeners){
            d.percentageChange(this.INDEX, percentage);
        }
    }
    
    /**
     * Método do jSerialComm para determinar os tipos de interrupção a serem ouvidas.
     * @return 
     */
    @Override
    public int getListeningEvents(){
        return (SerialPort.LISTENING_EVENT_DATA_AVAILABLE | SerialPort.LISTENING_EVENT_PORT_DISCONNECTED);
    }
    
    /**
     * Função de tratamento de interrupção.
     * @param event 
     */
    @Override
    public void serialEvent(SerialPortEvent event){ //Terminar de implementar
        
        /* Checa se a interrupção é de dados */
        if(event.getEventType() == SerialPort.LISTENING_EVENT_DATA_AVAILABLE){
            
            // Define a variavel de dados como um array de bytes do tamanho dos dados disponíveis
            byte[] newData = new byte[this.CONN_PORT.bytesAvailable()];
            
            /* Lê os dados recebidos e guarda na String data */
            this.CONN_PORT.readBytes(newData, newData.length);
            for(int i=0;i<newData.length;i++){
                this.data += (char)newData[i];
            }
            
            // Guarda em um array de caracteres a String com dados
            char[] dataChar = this.data.toCharArray();
            
            /* Verifica se a impressora enviou todos os dados */
            if(dataChar[dataChar.length-1] == '\n'){
                
                /* Printa os dados no terminal */
                System.out.println(this.data.strip());
                
                /* Definição de matchers para procurar pelos padrões */
                Matcher matchError = this.ERROR_ON_LINE_READ.matcher(this.data);
                Matcher matchFile = this.FILE_IN_SD.matcher(this.data);
                Matcher matchTemp = this.TEMPERATURE_CHECK.matcher(this.data);
                
                /* Checa se os dados enviados foram recebidos pela impressora */
                if(this.DATA_RECIEVED.matcher(this.data).find()){
                    /* Se uma impressão estiver acontecendo, avisa que a impressora recebeu o dado */
                    if(this.printFile == 1){
                        for(CommandListener drl : this.sentDataListeners){
                            drl.sentCommand();
                        }
                        
                        /* Atualiza o valor da flag de impressão */
                        this.printFile = this.sendCommand.getPrinting();
                    }
                }
                
                /* Verifica se os dados recebidos são dados de temperatura */
                if(matchTemp.find()){
                    try {
                        
                        /* Trata os dados de temperatura */
                        this.printerData.parseTempData(matchTemp.group());
                        
                        Thread.sleep(10);   // Espera os dados serem tratados
                        
                        /* Avisa os listeners que houve mudanças em temperatura */
                        for(DataListener dl : this.dataListeners){
                            dl.tempChange(this.INDEX, this.printerData);
                        }
                    } catch (InterruptedException ex) {
                    }
                }
                
                /* Verifica se houve erros com o envio do comando */
                if(matchError.find()){
                    this.printFile = 0; // Pausa a impressão
                    try {
                        this.sendCommand.sleep(100);    // Pausa a impressão
                        
                        
                        System.out.println("Error Line"); // Printa que houve um erro no envio
                        
                        // Desobre a linha onde houve o erro e printa no terminal
                        int line = Integer.valueOf(matchError.group().replaceAll("[^0-9]+",""));    
                        System.out.println(line);
                        
                        // Guarda o valor da linha que deu erro em uma variavel
                        String fileLine = this.sendCommand.getFileLine(line - 1);
                        
                        // Envia a linha com erro para a impressora
                        (new SendCommand(fileLine, 0, this)).start();
                        
                        this.printFile = 1; // Retorna a impressão
                        Thread.sleep(100);  // Espera para ver se o envio foi correto
                        
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Printer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                }
                
                /* Verifica se existe algum arquivo nos dados */
                else if(matchFile.find()){
                    
                    /* Adiciona a lista de arquivos no cartão SD */
                    while(matchFile.find()){
                        this.filesInSD.add(matchFile.group().toUpperCase());
                    }
                }
                
                this.data = ""; // Limpa a String para a aquisição de mais dados
                
            }
            
        }
        /* Tratamento para caso a impressora seja desconectada */
        else if(event.getEventType() == SerialPort.LISTENING_EVENT_PORT_DISCONNECTED){
            
            /* Para todos os listeners, avisa que foi desconectado */
            for(PrinterDisconnectListener p : this.disconnectListener){
                p.pinterDisconnect(this); // Passa este objeto para ser desconectado
            }
            
            /* Fecha a comunicação para não dar erros */
            if(this.CONN_PORT.isOpen()){
                this.CONN_PORT.closePort();
            }
            
            System.err.println("Error: Printer Disconnected!"); // Print de controle
        }
    }
}
