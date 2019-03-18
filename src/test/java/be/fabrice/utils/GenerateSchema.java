package be.fabrice.utils;

import java.util.Properties;

import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

import be.fabrice.fetch.lazy.oneToOne.BadMaster;
import be.fabrice.fetch.lazy.oneToOne.BadSlave;

public class GenerateSchema {
	public static void main(String[] args) {
		Properties extraProperties = new Properties();
		extraProperties.put("hibernate.dialect","org.hibernate.dialect.H2Dialect");
		AnnotationConfiguration cfg = new AnnotationConfiguration().addProperties(extraProperties);
	   	 cfg.addAnnotatedClass(BadMaster.class);
	   	 cfg.addAnnotatedClass(BadSlave.class);
	   	 SchemaExport se = new SchemaExport(cfg);
	   	 se.create(true, false);
	}
}
