insert into EMP (ID,NOM) values (1000,'Anybody');
insert into EMPBIS (ID,NOM) values (1001,'Anybody else');
insert into EMPTER (ID,NOM) values (1002,'Another Anybody else');

insert into TRAV (ID,NOM,EMP_ID,EMPBIS_ID,EMPTER_ID) values (1001,'Happy one',1000,1001,1002);
