insert into STATUT (ID,CODE,LIBELLE) values (1,'S','Salarié');
insert into STATUT (ID,CODE,LIBELLE) values (2,'C','Sans emploi');

insert into ETAT (ID,CODE,LIBELLE) values (1,'M','Marié');
insert into ETAT (ID,CODE,LIBELLE) values (2,'C','Célibataire');

insert into SIT(ID,ENFANTS) values (2001,0);

insert into PERS (ID,NOM,STATUT_ID,ETAT_ID,SIT_ID) values (1001,'First',1,2,2001);