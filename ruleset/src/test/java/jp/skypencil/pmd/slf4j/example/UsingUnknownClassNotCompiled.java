package jp.skypencil.pmd.slf4j.example;

import jp.skypencil.pmd.slf4j.example.UsingStaticLogger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UsingUnknownClassNotCompiled {
	@SuppressWarnings("unused")
	private final Logger LOGGER = LoggerFactory.getLogger(UsingUnknownClassNotCompiled.class);
}
