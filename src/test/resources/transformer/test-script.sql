-- Premier joueur
insert into JOUEUR (ID,NOM) values (10, 'Raoul');
insert into PERSO (ID,NOM,JOUEUR_FK) values (100,'Tork',10);
insert into PERSO (ID,NOM,JOUEUR_FK) values (101,'Rana',10);
insert into CLONE (ID,NOM,PERSO_FK) values (1000,'Tork-1',100);
insert into CLONE (ID,NOM,PERSO_FK) values (1001,'Tork-2',100);
insert into CLONE (ID,NOM,PERSO_FK) values (1002,'Rana-1',101);
insert into CLONE (ID,NOM,PERSO_FK) values (1003,'Rana-2',101);

-- Propriétés
insert into PROP_DEF (ID,NOM,VALEUR_INIT,JOUEUR_FK) values (100,'Argent',true,10);
insert into PROP_DEF (ID,NOM,VALEUR_INIT,JOUEUR_FK) values (101,'Expérience',false,10);
-- 1000 po pour Tork, 2000 pour Rana
insert into INIT (ID,VAL_INIT,PROP_DEF_FK,PERSO_FK) values (100,1000.0,100,100);
insert into INIT (ID,VAL_INIT,PROP_DEF_FK,PERSO_FK) values (101,2000.0,100,101);
-- Commation PO
-- Tork-1, 120 au tour 1, 100 au tour 2
insert into CONSO (ID,TOUR,VALEUR,CLONE_FK,PROP_DEF_FK) values (1000,1,120.0,1000,100);
insert into CONSO (ID,TOUR,VALEUR,CLONE_FK,PROP_DEF_FK) values (1001,2,100.0,1000,100);
-- Tork-2, 20 au tour 3, 100 au tour 4
insert into CONSO (ID,TOUR,VALEUR,CLONE_FK,PROP_DEF_FK) values (1002,3,20.0,1001,100);
insert into CONSO (ID,TOUR,VALEUR,CLONE_FK,PROP_DEF_FK) values (1003,4,100.0,1001,100);
-- Rana-1, 150 au tour 1, 10 au tour 2, 25 tour 3
insert into CONSO (ID,TOUR,VALEUR,CLONE_FK,PROP_DEF_FK) values (1004,1,150.0,1002,100);
insert into CONSO (ID,TOUR,VALEUR,CLONE_FK,PROP_DEF_FK) values (1005,2,10.0,1002,100);
insert into CONSO (ID,TOUR,VALEUR,CLONE_FK,PROP_DEF_FK) values (1006,3,25.0,1002,100);
-- Rana-2, 100 au tour 4
insert into CONSO (ID,TOUR,VALEUR,CLONE_FK,PROP_DEF_FK) values (1007,4,100.0,1003,100);

-- Experience
-- Tork-1, 1 au tour 1, 2 au tour 2
insert into CONSO (ID,TOUR,VALEUR,CLONE_FK,PROP_DEF_FK) values (1008,1,1.0,1000,101);
insert into CONSO (ID,TOUR,VALEUR,CLONE_FK,PROP_DEF_FK) values (1009,2,2.0,1000,101);
-- Tork-2, 2 au tour 3, 0 au tour 4
insert into CONSO (ID,TOUR,VALEUR,CLONE_FK,PROP_DEF_FK) values (1011,3,1.0,1001,101);
insert into CONSO (ID,TOUR,VALEUR,CLONE_FK,PROP_DEF_FK) values (1012,4,0.0,1001,101);
-- Rana-1, 1 au tour 1, 0 au tour 2, 2 tour 3
insert into CONSO (ID,TOUR,VALEUR,CLONE_FK,PROP_DEF_FK) values (1013,1,1.0,1002,101);
insert into CONSO (ID,TOUR,VALEUR,CLONE_FK,PROP_DEF_FK) values (1014,2,0.0,1002,101);
insert into CONSO (ID,TOUR,VALEUR,CLONE_FK,PROP_DEF_FK) values (1015,3,2.0,1002,101);
-- Rana-2, 4 au tour 4
insert into CONSO (ID,TOUR,VALEUR,CLONE_FK,PROP_DEF_FK) values (1016,4,4.0,1003,101);
