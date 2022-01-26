package be.ucll.javafxspringboot;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import javafx.application.Application;

@SpringBootApplication
public class JavafxSpringbootApplication {

	public static void main(String[] args) {
		Application.launch(JavaFxApplication.class, args);
	}
}
