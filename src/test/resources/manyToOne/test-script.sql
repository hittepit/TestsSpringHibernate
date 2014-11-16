insert into EMP (ID,NOM) values (1000,'Emp1');
insert into EMP (ID,NOM) values (1001,'Emp2');
insert into EMP (ID,NOM) values (1002,'Emp3');

insert into TRAV (ID,NOM,EMP_ID,EMP_2_ID) values (2000,'Trav1',1000,1001);
insert into TRAV (ID,NOM,EMP_ID,EMP_2_ID) values (2001,'Trav2',1000,1001);
insert into TRAV (ID,NOM,EMP_ID,EMP_2_ID) values (2002,'Trav3',1001,1000);