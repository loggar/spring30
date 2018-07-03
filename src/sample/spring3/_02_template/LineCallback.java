package sample.spring3._02_template;

public interface LineCallback<T> {
	T doSomethingWithLine(String line, T value);
}
