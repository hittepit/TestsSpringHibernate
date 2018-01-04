insert into SOCIETE (ID,NAME) values (1000,'Soc1');
insert into SOCIETE (ID,NAME) values (1001,'Soc2');

insert into PATRON (ID,NAME,SOC_ID) values (2001,'Pat1',1000);
insert into PATRON (ID,NAME,SOC_ID) values (2002,'Pat2',1001);

insert into EMPLOYE (ID,NAME,PAT_ID) values (3001,'Emp1',2001);
insert into EMPLOYE (ID,NAME,PAT_ID) values (3002,'Emp2',2001);
insert into EMPLOYE (ID,NAME,PAT_ID) values (3003,'Emp3',2001);
insert into EMPLOYE (ID,NAME,PAT_ID) values (3004,'Emp4',2002);