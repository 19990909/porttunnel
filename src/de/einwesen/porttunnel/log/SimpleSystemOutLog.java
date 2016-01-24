package de.einwesen.porttunnel.log;

import org.apache.commons.logging.impl.SimpleLog;

/**
 * 
 * @author EinWesen
 * 
 * A Simple Logger, which redirect to System.out instead of System.err
 */
public class SimpleSystemOutLog extends SimpleLog {

	private static final long serialVersionUID = -6992254168086971560L;

	public SimpleSystemOutLog(String name) {
		super(name);
	}

	@Override
	protected void write(StringBuffer buffer) {
		System.out.println(buffer.toString());
	}

}
