package org.example.library;

import java.sql.SQLException;
import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.example.library.repositories.BookRepository;

@SpringBootApplication(scanBasePackages = {"org.example.library"})
public class Main {

  @Autowired
  private BookRepository bookRepository;

  public static void main(String[] args) {
    SpringApplication.run(Main.class, args);
  }

  @Bean(initMethod = "start", destroyMethod = "stop")
  public Server inMemoryH2DatabaseServer() throws SQLException {
    return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9091");
  }
}