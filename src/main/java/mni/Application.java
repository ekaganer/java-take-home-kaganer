package mni;


import mni.api.DBUtils;
import mni.api.Utils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.io.FileInputStream;
import java.io.InputStream;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {

            //TODO - this is an OK place to do data import
            System.out.println("Import data here.....");
            DBUtils.prepareTable("MESSAGE");
            //byte[] fileContent = Utils.getFileContent("test.dat");
            //Utils.parseFile(fileContent);
            InputStream input = new FileInputStream("test.dat");
            Utils.processFile(input);
            input.close();
        };
    }

}
