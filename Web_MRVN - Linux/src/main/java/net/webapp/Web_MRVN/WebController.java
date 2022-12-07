/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.webapp.Web_MRVN;

import java.io.File;
import com.fazecast.jSerialComm.SerialPort;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import jdk.internal.loader.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static org.springframework.http.ContentDisposition.formData;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import printer.*;
import com.fazecast.jSerialComm.SerialPort;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.DispatcherServlet;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 *
 * @author Diego Oliveira
 */
@Controller
public class WebController implements DataListener{
    
        private  String passo = "0.10";
        private  String printer1 = "Arquivo: ";
        private  String printer2 = "Arquivo: ";
        private  String printer3 = "Arquivo: ";
        private  String printer4 = "Arquivo: ";
        private Printer[] printers;
        private PrinterData[] printersData;
        private Connection conn = new Connection();
        private int sizePrinter = 0;
        
          
         @Autowired
         private WebConfig service;
          
        public WebController() {
            conn.start();
            this.printers = conn.getPrinters();
            
            this.printersData = new PrinterData[this.printers.length];
            
            for(int i=0;i<this.printers.length;i++){
                this.printersData[i] = this.printers[i].getPrinterData();
            }
            
            this.sizePrinter = this.printers.length;
            
        }
   
        @RequestMapping(value = "/", method = RequestMethod.GET)
        public String Refresh(Model model){
   
             service.files(model);
             
            this.printers = conn.getPrinters();
            this.sizePrinter = this.printers.length;
            
            if(sizePrinter >= 1){
               model.addAttribute("wifi1", "wifi");
               model.addAttribute("wifi2", "wifi"); 
            } 
            else{
               model.addAttribute("wifi1", "wifi_off");
               model.addAttribute("wifi2", "wifi_off"); 
            }
            
            
            
            
            model.addAttribute("arquivo1", this.printer1);
            model.addAttribute("arquivo2", this.printer2);
            model.addAttribute("arquivo3", this.printer3);
            model.addAttribute("arquivo4", this.printer4);
    
        return "index";
    
    } 
    

    @RequestMapping(value="/Home")
        public String home(Model model){
            service.files(model);

            model.addAttribute("arquivo1", this.printer1);
            model.addAttribute("arquivo2", this.printer2);
            model.addAttribute("arquivo3", this.printer3);
            model.addAttribute("arquivo4", this.printer4);

            return "index";
        }
   
        
    @RequestMapping(value = "/File1", method = RequestMethod.POST)
        public String File_1(@RequestParam("file1") String file, Model model) {
            String path = "/home/orangepi/Documents/upload/"+file;
            //String path = System.getProperty("user.dir")+"/File/"+file;
            System.out.println("Impressora 1 imprimindo:");
            System.out.println(path);
            
            this.printers[0].printFile(path);

          /*  AQUI TU COLOCA a parte de envio de arquivo
            SerialPort port = SerialPort.getCommPorts()[0];
            Printer p = new Printer(port);
            p.sendFile(file);
            */
          
            service.files(model);
            
            printer1="Aquivo: "+file;
            model.addAttribute("arquivo1", this.printer1);
            model.addAttribute("arquivo2", this.printer2);
            model.addAttribute("arquivo3", this.printer3);
            model.addAttribute("arquivo4", this.printer4);
          
            return "index";
        }
        
    @RequestMapping(value = "/File2", method = RequestMethod.POST)
        public String File_2(@RequestParam("file2") String file, Model model) {
            String path = "home/orangepi/Documents/upload"+file;
            //String path = System.getProperty("user.dir")+"\\Files\\"+file;
            System.out.println("Impressora 2 imprimindo:");
            System.out.println(path);
            
            this.printers[1].printFile(path);

            service.files(model);
            printer2="Aquivo: "+file;
            model.addAttribute("arquivo1", this.printer1);
            model.addAttribute("arquivo2", this.printer2);
            model.addAttribute("arquivo3", this.printer3);
            model.addAttribute("arquivo4", this.printer4);
         
            return "index";
        }
        
        
    @RequestMapping(value = "/File3", method = RequestMethod.POST)
        public String File_3(@RequestParam("file3") String file, Model model) {
          
            String path = System.getProperty("user.dir")+"\\Files\\"+file;
            System.out.println("Impressora 3 imprimindo:");
            System.out.println(path);
          
            service.files(model);
            printer2="Aquivo: "+file;
            model.addAttribute("arquivo1", this.printer1);
            model.addAttribute("arquivo2", this.printer2);
            model.addAttribute("arquivo3", this.printer3);
            model.addAttribute("arquivo4", this.printer4);
         
            return "index";
        }

    @RequestMapping(value = "/File4", method = RequestMethod.POST)
        public String File_4(@RequestParam("file4") String file, Model model) {
          
            String path = System.getProperty("user.dir")+"\\Files\\"+file;
            System.out.println("Impressora 4 imprimindo:");
            System.out.println(path);
          
            service.files(model);
            printer2="Aquivo: "+file;
            model.addAttribute("arquivo1", this.printer1);
            model.addAttribute("arquivo2", this.printer2);
            model.addAttribute("arquivo3", this.printer3);
            model.addAttribute("arquivo4", this.printer4);
         
            return "index";
        }

        @RequestMapping(value = "/AutoHomePrinter", method = RequestMethod.POST)
        public String AutoHomePrinter1(@RequestParam("Home") String printer){
           
            String home = "G28";
            switch (printer) {
                case "Printer1":
                    this.printers[0].sendInfo(home);
                    break;
                case "Printer2":
                    this.printers[1].sendInfo(home);
                    break;
                case "Printer3":
                   this.printers[2].sendInfo(home);
                    break;
                case "Printer4":
                    this.printers[3].sendInfo(home);
                    break;
                default:
                    break;
            }
            
            System.out.println(printer);
            
            return "index2";
        
        }
        
        
        @RequestMapping(value = "/ABSPrinter", method = RequestMethod.POST)
        public String ABSPrinter1(@RequestParam("ABS") String printer){
            
            String ABS = "M140 S100 \nM104 S230";
            System.out.println(ABS);
            switch (printer) {
                case "Printer1":
                    this.printers[0].sendInfo(ABS);
                    break;
                case "Printer2":
                    this.printers[1].sendInfo(ABS);
                    break;
                case "Printer3":
                   this.printers[2].sendInfo(ABS);
                    break;
                case "Printer4":
                    this.printers[3].sendInfo(ABS);
                    break;
                default:
                    break;
            }            
            
            
            return "index2";
        
        }
        
        
        @RequestMapping(value = "/PLAPrinter", method = RequestMethod.POST)
        public String PLAPrinter1(@RequestParam("PLA") String printer){
            
            String PLA = "M140 S60 \nM104 S200";
            System.out.println(PLA);
            switch (printer) {
                case "Printer1":
                    this.printers[0].sendInfo(PLA);
                    break;
                case "Printer2":
                    this.printers[1].sendInfo(PLA);
                    break;
                case "Printer3":
                   this.printers[2].sendInfo(PLA);
                    break;
                case "Printer4":
                    this.printers[3].sendInfo(PLA);
                    break;
                default:
                    break;
            }              
            
            return "index2";
        
        }
        
         @RequestMapping(value = "/MovePrinter", method = RequestMethod.POST)
         public String Axis(Model model, @RequestParam("x") String x,@RequestParam("passo") String passo){
             System.out.println(x);
             System.out.println(passo);
             
             
             
             String move = "G0 ";
             try{
             switch(x){
                 case "Printerx1+":
                     move = move + "X"+passo;
                     System.out.println(move);
                     this.printers[0].sendInfo("G91");
                     this.printers[0].sendInfo(move);
                     break;
                 case "Printerx1-":
                     move = move + "X-"+passo;
                     System.out.println(move);
                     this.printers[0].sendInfo("G91");
                     this.printers[0].sendInfo(move);
                     break;
                 case "Printery1+":
                     move = move + "Y"+passo;
                     System.out.println(move);
                     this.printers[0].sendInfo("G91");
                     this.printers[0].sendInfo(move);
                     break;
                 case "Printery1-":
                     move = move + "Y-"+passo;
                     System.out.println(move);
                     this.printers[0].sendInfo("G91");
                     this.printers[0].sendInfo(move);
                     break;
                 case "Printerz1+":
                     move = move + "Z"+passo;
                     System.out.println(move);
                     this.printers[0].sendInfo("G91");
                     this.printers[0].sendInfo(move);
                     break;
                 case "Printerz1-":
                     move = move + "Z-"+passo;
                     System.out.println(move);
                     this.printers[0].sendInfo("G91");
                     this.printers[0].sendInfo(move);
                     break;
                 case "Printerx2+":
                     move = move + "X"+passo;
                     System.out.println(move);
                  //   this.printers[1].sendInfo(move);
                     break;
                 case "Printerx2-":
                     move = move + "X"+passo;
                     System.out.println(move);
                  //   this.printers[1].sendInfo(move);
                     break;
                 case "Printery2+":
                     move = move + "Y"+passo;
                     System.out.println(move);
                   //  this.printers[1].sendInfo(move);
                     break;
                 case "Printery2-":
                     move = move + "Y"+passo;
                     System.out.println(move);
                   //  this.printers[1].sendInfo(move);  
                     break;
                 case "Printerz2+":
                     move = move + "Z"+passo;
                     System.out.println(move);
                     //this.printers[1].sendInfo(move);
                     break;
                 case "Printerz2-":
                     move = move + "Z"+passo;
                     System.out.println(move);
                     //this.printers[1].sendInfo(move);
                     break;
                     
                }
             } catch(IndexOutOfBoundsException e ){
                 System.err.println("Printer not connected!");
             }
             
             
             
             
         
             model.addAttribute("passo",passo);

             
             
           
         
             return "index2";
         }
        
        
       
         
        @RequestMapping(value = "/MovePrinterMobile", method = RequestMethod.POST)
        public String Axis(Model model, @RequestParam("x") String x,@RequestParam("y") String y,@RequestParam("z") String z){
        
            
            
                
            if (x.equals("") && !y.equals("") && !z.equals("")){
                String axis = "G0 Y"+y+" Z"+z;
                this.printers[0].sendInfo(axis);
                System.out.println(axis);
            }
            else if(y.equals("") && z.equals("") && !x.equals("")){
                String axis = "G0 X"+x;
                this.printers[0].sendInfo(axis);
                System.out.println(axis);
            }
            else if(y.equals("") && x.equals("") && !z.equals("")){
                String axis = "G0 Z"+z;
                this.printers[0].sendInfo(axis);
                System.out.println(axis);
            }
            else if(z.equals("") && x.equals("") && !y.equals("")){
                String axis = "G0 Y"+y;
                this.printers[0].sendInfo(axis);
                System.out.println(axis);
            }
            else if(y.equals("") && !x.equals("") && !z.equals("")){
                String axis = "G0 X"+x+" Z"+z;
                this.printers[0].sendInfo(axis);
                System.out.println(axis);
            }
            else if(z.equals("")&& !x.equals("") && !y.equals("")){
                String axis = "G0 X"+x+" Y"+y;
                this.printers[0].sendInfo(axis);
                System.out.println(axis);
            }

            else{
            String axis = "G0 X"+x+" Y"+y+" Z"+z;
            
            System.out.println(axis);
            }
            
            
            
            
            return "index2";
        
        }
        
        
        @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
        public String submit(@RequestParam("file") MultipartFile file, ModelMap modelMap) {
        modelMap.addAttribute("file", file);
        System.out.println(file);
        return "index";
                }
        
        
        @PostMapping("/upload") 
        public ResponseEntity<?> handleFileUpload( @RequestParam("file") MultipartFile file ) {

        String fileName = file.getOriginalFilename();
        try {
        Files.createDirectories(Paths.get("/home/orangepi/Documents/upload/"));
        file.transferTo( new File("/home/orangepi/Documents/upload/" + fileName));  
        //file.transferTo( new File("C:\\upload\\" + fileName));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } 
            return ResponseEntity.ok("File uploaded successfully.");
        }

        
        
        
        @Override
        public void tempChange(int index, PrinterData pd){
            switch(index){
                case 0:
                    /* Coloca aqui a forma de colocar os dados na interface
                        this.printersData[index] = pd;
                        temperatura bico = pd.getTipTemp();
                        temperatura final bico = pd.getTipFinalTemp();
                        temperatura base = pd.getBaseTemp();
                        temperatura final base = pd.getBaseFinalTemp();
                    */
                    break;
            }
        }
        
        
    
     
    
    
}
