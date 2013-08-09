Petit projet utile à des fins de démonstration.

* Démonstration de tests unitaires de Dao (avec TestNg et Spring)

* Démonstration d'un test unitaire de service, sans Spring et sans DB (pour ceux qui croiraient que c'est nécessaire parce que le service est instancié par Spring dans l'application et qu'il utilise un Dao)

* Démonstration de certaines fonctionnalités d'Hibernate
	+ Cohérence du modèle en bidirectionnalité
	+ Démonstration de particularité des proxies
		- Problème du equals
		- Initialisation des proxies
		- Différences entre session.get et session.load
		- Pour le fun, récupération de l'entité réelle derrière le proxy
	+ Démontsration de l'héritage en une seule table
		- Single table
		- One table per class
	+ Démonstration du flush

* TODO
	+ proxies: hashcode (utilisation des getters)
	+ TODO: héritage, les autres stratégies
	+ Ajouter les commentaires sur les tests bidirectionnels
	+ utilisation de usertypes
	+ testing avec H2 lorsqu'un schéma est défini
	+ dirty checking
		- quand il y a flush
		- pas d'update si immutable...

* Resources 
	+ <http://docs.jboss.org/hibernate/orm/3.5/reference/en/html/>
	+ <http://docs.jboss.org/hibernate/annotations/3.5/reference/en/html_single/>