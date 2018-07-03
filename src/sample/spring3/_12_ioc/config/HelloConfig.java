package sample.spring3._12_ioc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import sample.spring3._12_ioc.bean.Hello;
import sample.spring3._12_ioc.bean.Printer;
import sample.spring3._12_ioc.bean.StringPrinter;

@Component
public class HelloConfig {
	@Bean
	public Hello hello() {
		Hello hello = new Hello();
		hello.setName("Spring");
		hello.setPrinter(printer());
		return hello;
	}

	@Bean
	public Hello hello2() {
		Hello hello = new Hello();
		hello.setName("Spring2");
		hello.setPrinter(printer());
		return hello;
	}

	@Bean
	public Printer printer() {
		return new StringPrinter();
	}
}
