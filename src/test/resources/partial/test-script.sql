insert into CAT (ID,CODE,LIBELLE) values (1001,'S','Salarié');
insert into CAT (ID,CODE,LIBELLE) values (1002,'O','Ouvrier');
insert into CAT (ID,CODE,LIBELLE) values (1003,'D','Dirigeant');

insert into TRAV (ID,NOM,CAT_ID) values (1001,'Toto',1001);
insert into TRAV (ID,NOM,CAT_ID) values (1002,'Tutu',1001);
insert into TRAV (ID,NOM,CAT_ID) values (1003,'Tata',1002);