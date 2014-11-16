insert into EMP (ID,COUNTRY) values (1000,'Belgium');
insert into EMP (ID,COUNTRY) values (1001,'France');
insert into BOSS (ID,NOM) values (1000,'Trois');
insert into SOCIETE (ID,NUM) values (1001,'456789123');

insert into TRAV (ID,NOM,EMP_ID,LAZY_EMP_ID) values (1001,'One',1000,1001);
insert into TRAV (ID,NOM,EMP_ID,LAZY_EMP_ID) values (1002,'Two',1001,1000);
