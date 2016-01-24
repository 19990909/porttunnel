package de.einwesen.porttunnel.log;

import org.apache.commons.logging.Log;

/**
 * 
 * @author EinWesen
 * 
 * A logger, which optionally can logs exception stacktraces only 
 * when a certain loglevel is enabled.
 *
 */
public class ConditionalStackLog implements Log {

	public enum STACK_LOGLEVEL {
		TRACE, DEBUG, INFO, WARN, ERROR, FATAL;
		public boolean isEnabledOn(Log log) {
			switch (this) {
				case TRACE:
					return log.isTraceEnabled();
				case DEBUG:
					return log.isDebugEnabled();
				case INFO:
					return log.isInfoEnabled();
				case WARN:
					return log.isWarnEnabled();
				case ERROR:
					return log.isErrorEnabled();
				case FATAL:
					return log.isFatalEnabled();
				default:
					throw new IllegalArgumentException("Unknow level " + this);
			}
		}
	}
	private final Log master;

	private ConditionalStackLog(Log log) {
		this.master = log;
	}
	
	/** 
	 * Log the exception on the named level. The stacktrace isincluded only, if 
	 * <i>showStackTraceOnLevel</i> is enabled on the master logger 
	 * 
	 * @param message
	 * @param t
	 * @param showStackTraceOnLevel
	 */
	public void debug(Object message, Throwable t, STACK_LOGLEVEL showStackTraceOnLevel) {
		if (showStackTraceOnLevel.isEnabledOn(master)) {
			master.debug(message, t);
		} else {
			master.debug(formatExceptionMessage(message, t));
		}
	}
	
	/** 
	 * Log the exception on the named level. The stacktrace isincluded only, if 
	 * <i>showStackTraceOnLevel</i> is enabled on the master logger 
	 * 
	 * @param message
	 * @param t
	 * @param showStackTraceOnLevel
	 */
	public void error(Object message, Throwable t, STACK_LOGLEVEL showStackTraceOnLevel) {
		if (showStackTraceOnLevel.isEnabledOn(master)) {
			master.error(message, t);
		} else {
			master.error(formatExceptionMessage(message, t));
		}
	}
	
	/** 
	 * Log the exception on the named level. The stacktrace isincluded only, if 
	 * <i>showStackTraceOnLevel</i> is enabled on the master logger 
	 * 
	 * @param message
	 * @param t
	 * @param showStackTraceOnLevel
	 */	
	public void fatal(Object message, Throwable t, STACK_LOGLEVEL showStackTraceOnLevel) {
		if (showStackTraceOnLevel.isEnabledOn(master)) {
			master.fatal(message, t);
		} else {
			master.fatal(formatExceptionMessage(message, t));
		}
	}	
	
	public static ConditionalStackLog getInstance(Log log) {
		return new ConditionalStackLog(log);
	}
	
	private static String formatExceptionMessage(Object message, Throwable t) {
		return message + " < "+t.toString()+" >";
	}
	
	@Override
	public void debug(Object message) {
		master.debug(message);
	}

	@Override
	public void debug(Object message, Throwable t) {
		master.debug(message, t);
	}
	

	@Override
	public void error(Object message) {
		master.error(message);
	}

	@Override
	public void error(Object message, Throwable t) {
		master.error(message, t);
	}

	@Override
	public void fatal(Object message) {
		master.fatal(message);
	}

	@Override
	public void fatal(Object message, Throwable t) {
		master.fatal(message, t);
	}

	@Override
	public void info(Object message) {
		master.info(message);
	}

	@Override
	public void info(Object message, Throwable t) {
		master.info(message, t);
	}

	@Override
	public boolean isDebugEnabled() {
		return master.isDebugEnabled();
	}

	@Override
	public boolean isErrorEnabled() {
		return master.isErrorEnabled();
	}

	@Override
	public boolean isFatalEnabled() {
		return master.isFatalEnabled();
	}

	@Override
	public boolean isInfoEnabled() {
		return master.isInfoEnabled();
	}

	@Override
	public boolean isTraceEnabled() {
		return master.isTraceEnabled();
	}

	@Override
	public boolean isWarnEnabled() {
		return master.isWarnEnabled();
	}

	@Override
	public void trace(Object message) {
		master.trace(message);
	}

	@Override
	public void trace(Object message, Throwable t) {
		master.trace(message, t);
	}

	@Override
	public void warn(Object message) {
		master.warn(message);
	}

	@Override
	public void warn(Object message, Throwable t) {
		master.warn(message, t);
	}
	
}
