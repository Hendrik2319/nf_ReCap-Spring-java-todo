package com.example.backend.changelog;

public class ChangeLogException extends Exception {
	public ChangeLogException(String format, Object... values) {
		super(format.formatted(values));
	}
}
