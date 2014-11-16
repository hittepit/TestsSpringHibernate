package be.fabrice.utils;

import java.util.Properties;

import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

import be.fabrice.join.notOnPk.entity.Parametres;
import be.fabrice.join.notOnPk.entity.Personne;

public class GenerateSchema {
	public static void main(String[] args) {
		//hibernate.dialect=org.hibernate.dialect.H2Dialect
		Properties extraProperties = new Properties();
		extraProperties.put("hibernate.dialect","org.hibernate.dialect.H2Dialect");
		AnnotationConfiguration cfg = new AnnotationConfiguration().addProperties(extraProperties);
	   	 cfg.addAnnotatedClass(Personne.class);
	   	 cfg.addAnnotatedClass(Parametres.class);
	   	 SchemaExport se = new SchemaExport(cfg);
	   	 se.create(true, false);
	}
}
