package com.lerner.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;


//etra imports for test till java.util.logging.Logger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller; 
import org.springframework.web.bind.annotation.RequestMapping; 
import org.springframework.web.bind.annotation.ResponseBody; 
import java.util.ArrayList; 
import java.util.List; 
import java.util.HashMap; 
import java.util.Map; 
import java.util.logging.Logger;

@SpringBootApplication
@EnableEurekaServer
public class LernerEurekaApplication {

	public static void main(String[] args) {
		SpringApplication.run(LernerEurekaApplication.class, args);
	}

}
