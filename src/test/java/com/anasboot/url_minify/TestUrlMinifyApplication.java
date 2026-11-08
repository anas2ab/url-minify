package com.anasboot.url_minify;

import org.springframework.boot.SpringApplication;

public class TestUrlMinifyApplication {

	public static void main(String[] args) {
		SpringApplication.from(UrlMinifyApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
