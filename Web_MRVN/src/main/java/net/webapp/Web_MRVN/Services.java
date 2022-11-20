/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.webapp.Web_MRVN;


import java.io.File;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

/**
 *
 * @author Diego Oliveira
 */

@Service
public class Services {
    
   
    List<String> files = new ArrayList<>();
   // String dir = System.getProperty("user.dir")+"\\Files\\";
    String dir = "C:\\Users\\";
    File file_1_1 = new File(dir);
    File afile[] = file_1_1.listFiles();
    
    public void files(Model model){
               files.clear();
               int i = 0;
               for (int j = afile.length; i < j; i++) {
		File arquivos = afile[i];
                files.add(arquivos.getName());
		//System.out.println(files.get(i));
               }
    
    
              model.addAttribute("files", files);
    }
    


    public void paths() {

    }
    
    
    
}
