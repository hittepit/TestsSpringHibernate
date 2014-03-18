package be.fabrice.logging;

import com.p6spy.engine.spy.appender.MessageFormattingStrategy;

public class SimpleSql implements MessageFormattingStrategy {

	@Override
	public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared, String sql) {
		return sql;
	}

}
