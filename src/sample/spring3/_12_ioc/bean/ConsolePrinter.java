package sample.spring3._12_ioc.bean;

public class ConsolePrinter implements Printer {
	public void print(String message) {
		System.out.println(message);
	}
}
