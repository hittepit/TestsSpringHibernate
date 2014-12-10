Petit projet utile à des fins de démonstration.

* Démonstration de tests unitaires de Dao (avec TestNg et Spring)

* Démonstration d'un test unitaire de service, sans Spring et sans DB (pour ceux qui croiraient que c'est nécessaire parce que le service est instancié par Spring dans l'application et qu'il utilise un Dao)

* Démonstration de certaines fonctionnalités d'Hibernate
	+ Cohérence du modèle et bidirectionnalité
	+ [Démonstration du ManyToOne](https://github.com/hittepit/TestsSpringHibernate/wiki/Relations-ManyToOne)
		- Démontration de la récupération des 'many' à partir du 'one'
	+ Exemples OneToMany
		- La sauvegarde par cascading fait un insert du 'many' suivi d'un update pour la foreign key
		- Démonstration de la possibilité de récupérer le 'one' à partir d'un 'many'
	+ Exemples de ManyToMany
		- Cascading ou pas, côté maître de la relation
	+ [Démonstration des particularités des proxies](https://github.com/hittepit/TestsSpringHibernate/wiki/Proxies)
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
		- Le cache fonctionne aussi avec les références lazy-loadées, mais le batch fetch ne fonctionne plus alors
		- Fonctionnement des caches NONSTRICT_READ_WRITE
		- Cache 2nd level pour les collections
	+ "select e from Entity e where..." ou "from Entity e where..." fetches dependencies
	+ Utilisation de lazy 
		- type réel d'une List lazyloadée
		- méthodes qui initialisent une collection lazy loadée
	+ optimistic locking
		- fonctionnement avec Timestamp
		- fonctionnement avec Integer
		- locking en cas de delete et d'update/delete concurrent
		- pas de locking si enfant et parent modifiés séprément
	+ jointure sur des entités non liées
	+ jointure sur des colonnes qui ne sont ni des PK ni des FK
		- erreur dans le cas des mapping OneToOne dans ce cas (mais fonctionne avec des OneToMany)
	+ démonstration de la validation des propriétés
		- sans hibernate-validator, validation minimale au niveau d'Hibernate
	+ requêtes complexes
	+ Test d'un intercepteur
	+ Démonstration de l'initilisation lazy de propriétés associées à une entité
	+ Utilisation des propriétés lazy loadées et des listeners pour ajouter de la traçabilité
	+ Utilisation de batch-size pour le lazy-loading
		- chargement de collections (limiter le problème du N+1 select)
		- chargement des toOne (Proxy)

* Démonstration d'un curieux problème lorsque le critéria n'est pas tout à fait correct. Il fonctionne pour un list,
mais ne fonctionne pas si on lui ajoute une projection rowCount (voir criteria/alias)

* Démonstration qu'Hibernate peut s'en sortir avec un modèle robuste
	+ Pas de constructeur sans paramètre public
	+ Pas de setter
	+ Utilisation de Value Object avec des UserTypes
	

* TODO
	- [ ] proxies: hashcode (utilisation des getters)
	- [ ] héritage, les autres stratégies
	- [ ] Ajouter les commentaires sur les tests bidirectionnels
	- [ ] Commentaires sur ManyToOne
	- [ ] utilisation de usertypes
	- [ ] query cache
	- [ ] dirty checking
		- [ ] quand il y a flush
		- [ ] pas d'update si immutable...
	- [ ] cache de second niveau
		- [ ] sur requêtes 
		- [ ] avec un eager en fetch select (sur un manyToOne puisque cache hit en lazy)
	- [ ] concurrence
		- [ ] update d'une entité deleted (sans versioning)
		- [ ] delete d'une entité deleted (sans versioning)
	- [ ] question en cours sur be.fabrice.flush.dao.TestFlush

* Resources 
	+ <http://docs.jboss.org/hibernate/orm/3.5/reference/en/html/>
	+ <http://docs.jboss.org/hibernate/annotations/3.5/reference/en/html_single/>
	+ P6Spy <https://github.com/p6spy/p6spy>