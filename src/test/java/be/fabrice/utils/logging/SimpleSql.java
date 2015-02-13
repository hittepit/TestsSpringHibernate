package be.fabrice.utils.logging;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.validator.routines.RegexValidator;

import com.p6spy.engine.spy.appender.MessageFormattingStrategy;

public class SimpleSql implements MessageFormattingStrategy {
	private static List<String> sqlList = new ArrayList<String>();

	@Override
	public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared, String sql) {
		sqlList.add(sql);
		return sql;
	}

	public static void reinitSqlList(){
		sqlList.clear();
	}
	
	public static boolean contains(String regex){
		RegexValidator rev = new RegexValidator(regex);
		for(String sql:sqlList){
			if(rev.isValid(sql)) return true;
		}
		return false;
	}
	
	public static List<String> getSqlList(){
		return sqlList;
	}
}
