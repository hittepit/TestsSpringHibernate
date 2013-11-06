Petit projet utile à des fins de démonstration.

* Démonstration de tests unitaires de Dao (avec TestNg et Spring)

* Démonstration d'un test unitaire de service, sans Spring et sans DB (pour ceux qui croiraient que c'est nécessaire parce que le service est instancié par Spring dans l'application et qu'il utilise un Dao)

* Démonstration de certaines fonctionnalités d'Hibernate
	+ Cohérence du modèle en bidirectionnalité
	+ Démonstration du ManyToOne
		- Démontration de la récupération des 'many' à partir du 'one'
	+ Exemples OneToMany
		- La sauvegarde par cascading fait un insert du 'many' suivi d'un update pour la foreign key
		- Démonstration de la possibilité de récupérer le 'one' à partir d'un 'many'
	+ Exemples de ManyToMany
		- Cascading ou pas, côté maître de la relation
	+ Démonstration de particularité des proxies
		- Problème du equals
		- Initialisation des proxies
		- Différences entre session.get et session.load
		- Pour le fun, récupération de l'entité réelle derrière le proxy
	+ Démonstration de l'héritage en une seule table
		- Single table
		- One table per class
		- Join
	+ Démonstration du flush
	+ Démonstration d'une mauvaise utilisation de evict (lire les commentaires)
	+ Fonctionnement des transactions nestées (REQUIRES_NEW)
	+ Utilisation du cache de second niveau
		- Démonstration qu'une propriété lazy loadée peut donner un cache hit alors qu'une propriété eager loadée n'en donnera jamais
		- Fonctionnement des caches NONSTRICT_READ_WRITE

* TODO
	+ modèle robuste et Hibernate
	+ proxies: hashcode (utilisation des getters)
	+ TODO: héritage, les autres stratégies
	+ Ajouter les commentaires sur les tests bidirectionnels
	+ Commentaires sur ManyToOne
	+ utilisation de usertypes
	+ testing avec H2 lorsqu'un schéma est défini
	+ dirty checking
		- quand il y a flush
		- pas d'update si immutable...

* Resources 
	+ <http://docs.jboss.org/hibernate/orm/3.5/reference/en/html/>
	+ <http://docs.jboss.org/hibernate/annotations/3.5/reference/en/html_single/>