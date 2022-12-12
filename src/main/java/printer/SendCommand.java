package printer;

import com.fazecast.jSerialComm.SerialPort;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Guilherme Velhote
 */
public class SendCommand extends Thread implements CommandListener {
    
    /* Definição de variáveis finais */
    private final SerialPort CONN_PORT;     // Porta de conexão com impressora
    private final String INPUT_STRING;      // String enviada pelo usuário
    private final int mode;                 // Modo de funcionamento da classe
    private final Printer printer;          // Impressora utilizada
    
    /* Definição de variáveis comuns */
    private int printing;                   // Flag de impressão
    private int nextFileLine;               // Próxima linha a ser enviada
    private ArrayList<String> fileLines;    // Linhas do arquivo gcode
    private int printPercentage;            // Progresso da impressão
    
    /**
     * Contrutor da classe, recebe todos os dados necessários para que ao executar
     * possa chamar corretamente a função necessária.
     * @param input String a ser utilizada pelas funções
     * @param mode  Modo de envio a ser utilizado
     * @param printer Impressora a ser utilizada
     */
    public SendCommand(String input, int mode, Printer printer){
        this.printer = printer;
        this.CONN_PORT = printer.getCONN_PORT(); // Definido para fácil acesso à variável
        this.INPUT_STRING = input;
        this.mode = mode;
        
        /* Inicialização de variáveis de controle de impressão*/
        this.printing = 0;
        this.nextFileLine = 0;
    }
    
    /**
     * Método para leitura de arquivo gcode.
     * @param file
     * @return 
     */
    public ArrayList<String> readFile(File file){
        
        /* Cria um reader e define a lista com as linhas de arquivo
           como uma nova lista                                      */
        FileReader rd = null;
        this.fileLines = new ArrayList();
        try {
            
            /* Abre o reader com o arquivo passado */
            rd = new FileReader(file);
            BufferedReader brf = new BufferedReader(rd);
            
            /* Aloca a primeira linha na String linha e coloca a contagem como 1 */
            String line = brf.readLine();
            int lineCount = 1;
            
            /* Loop para ler todas as linhas do arquivo */
            while(line != null){
                
                line = line.replaceAll(";.*", "").strip();  // Retira todos os comentários da linha
                
                /* Checa se a linha está vazia para não guardar dados vazios */
                if(line.equals("")){
                    line = brf.readLine();
                    continue;
                }
                
                line = "N" + lineCount + " " + line;    // Adiciona o número de linha para o envio
                
                /* Transforma os dados em um array do tipo byte para descobrir o charsum */
                byte[] gcodeArray = line.getBytes();
                byte charsum = 0;
                
                /* Calcula o charsum da linha de envio */
                for(int i=0;i<gcodeArray.length;i++){
                    charsum ^= gcodeArray[i];
                }
                
                line += "*" + ((int)charsum);   // Concatena um * seguido do valor do charsum ao final da linha
                this.fileLines.add(line);       // Adiciona a linha na lista com todas as linhas
                lineCount++;                    // Incrementa o contador de linha
                line = brf.readLine();          // Lê a próxima linha
            }
            
        } 
        /* Caso dê erros, não envia dados para a impressora */
        catch (FileNotFoundException ex) {
            this.fileLines = null;
            Logger.getLogger(Printer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            this.fileLines = null;
            Logger.getLogger(Printer.class.getName()).log(Level.SEVERE, null, ex);
        } 
        /* Fecha o reader após a utilização */
        finally {
            try {
                rd.close();
            } catch (IOException ex) {
                Logger.getLogger(Printer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return this.fileLines;
    }    
    
    /**
     * Função para tratar e enviar o gcode para a impressora.
     * @param gcode 
     */
    private void sendInfo(String gcode){
        
        /* Verifica se o gcode enviado é um texto */
        if((gcode != null) && (gcode != "")){
            
            /* Adiciona elementos textuais reconhecidos pelo Marlin como fim de comando */
            String info = gcode.strip() + "\r\n";
            
            /* Escreve na porta da impressora o gcode */
            this.CONN_PORT.writeBytes(info.getBytes(), info.getBytes().length);
        }
    }
    
    /**
     * Função de impressão de arquivos gcode.
     * @param filePath 
     */
    public void printFile(String filePath){
        try {
            this.printing = 1;          // Seta o flag de impressão como ativa
            this.nextFileLine = 0;      // Define a primeira linha de envio como 0
            this.printPercentage = 0;   // Define a porcentagem de impressão como 0%
            
            readFile(new File(filePath));   // Lê o arquivo a partir da função
            
            /* Envia como linha 0 o código que define o começo de linha como 0.
               Como a impressão espera por um ok para enviar a próxima linha,
               não precisa enviar a próxima linha por ora.               */
            sendInfo("M110 N0");
            Thread.sleep(10);   // Espera 10ms para sincronizar com a impressora
            
        } catch (InterruptedException ex) {
            Logger.getLogger(SendCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    /**
     * Função para o envio de arquivos para o cartão SD da impressora.
     * @param filePath 
     */
    public void sendFile(String filePath){
        
        /* Descobre o nome do arquivo e altera o tipo de
           arquivo de .gcode para .gco para salvar na impressora */
        File file = new File(filePath); 
        String fileName = file.getName().replaceAll("[.].*", ".gco");
        
        readFile(file); // Lê as linhas do arquivo 
        
        sendInfo("M110 N0");    // Seta primeira linha de comando como a linha 0
        sendInfo("M28 " + fileName.toUpperCase());  // Envia comando para salvar arquivo no cartão SD
        
        /* Envia cada linha do arquivo para a impressora */
        for(String gcode : this.fileLines){
            try {
                sendInfo(gcode);
                Thread.sleep(10);   // Espera 10ms para sincronizar com impressora
            } catch (InterruptedException ex) {
                Logger.getLogger(SendCommand.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        
        sendInfo("M29");                // Envia comando para parar gravação de arquivo
        sendInfo("M23 " + fileName);    // Envia comando para selecionar o arquivo salvo
        sendInfo("M24");                // Envia comando para começar impressão do arquivo selecionado
        
    }
    
    /**
     * Retorna a linha definida pelo índice passado.
     * @param index
     * @return 
     */
    public String getFileLine(int index){
        return this.fileLines.get(index);
    }
    
    /**
     * Retorna a flag de impressão.
     * @return 
     */
    public int getPrinting(){
        return this.printing;
    }
    
    /**
     * Método de iniciar a Thread. Identifica qual modo será utilizado e 
     * chama a função definida pelo modo.
     */
    @Override
    public void run(){
        /* Verifica qual modo será utilizado */
        switch(this.mode){
            /* Modo 0: Envio direto de gcode */
            case 0:
                sendInfo(this.INPUT_STRING);
                break;
            /* Modo 1: Impressão de arquivo */
            case 1:
                printFile(this.INPUT_STRING);
                break;
            /* Modo 2: Envio de arquivo para cartão SD */
            case 2:
                sendFile(this.INPUT_STRING);
                break;
        }
    }
    
    /**
     * Função de tratamento a interrupção de comando enviado. Utilizado para
     * sincronizar o envio de comandos quando imprimindo arquivos.
     */
    @Override
    public void sentCommand(){
        
        /* Verifica se o total de linhas foi atingido */
        if(this.nextFileLine == this.fileLines.size()){
            /* Reseta os valores internos -> Talvez desnecessário,
            mas utilizado para ter certeza de que não existirão erros */
            this.nextFileLine = 0;
            this.printing = 0;
            this.fileLines.clear();
        }
        else{
            /* Calcula a porcentagem de impressão e printa no terminal */
            this.printPercentage = (this.nextFileLine * 100 / this.fileLines.size());
            this.printer.percentageChange(this.printPercentage);
            System.out.println("Print percentage = " + this.printPercentage + "%");
            
            /* Envia a linha para a impressora */
            System.out.println("Sending: " + this.fileLines.get(this.nextFileLine));
            sendInfo(this.fileLines.get(this.nextFileLine));
            this.nextFileLine++;    // Aumenta o contador de linha
        }
    }
    
}
