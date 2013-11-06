insert into STATUT (ID,CODE,LIBELLE) values (1000,'S','Salarié');
insert into STATUT (ID,CODE,LIBELLE) values (2000,'C','Sans emploi');

insert into ETAT (ID,CODE,LIBELLE) values (1,'M','Marié');
insert into ETAT (ID,CODE,LIBELLE) values (2,'C','Célibataire');

insert into SIT(ID,ENFANTS) values (2001,0);

insert into CIV (ID,CODE,NOM) values (1,'M','Monsieur');
insert into CIV (ID,CODE,NOM) values (2,'Mme','Madame');

insert into PERS (ID,NOM,STATUT_ID,ETAT_ID,SIT_ID,CIV_ID) values (1001,'First',1000,2,2001,1);
