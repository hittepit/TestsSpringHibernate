insert into EMP (ID,NOM,KIND) values (1000,'Un','SOCIETE');
insert into EMP (ID,NOM,KIND) values (1001,'Deux','SOCIETE');
insert into EMP (ID,NOM,KIND) values (1002,'Trois','BOSS');
insert into EMP (ID,NOM,KIND) values (1003,'Quatre','BOSS');

insert into TRAV (ID,NOM,EMP_ID,LAZY_EMP_ID) values (1001,'First one',1000,1001);
insert into TRAV (ID,NOM,EMP_ID,LAZY_EMP_ID) values (1002,'Second one',1002,1003);