insert into CA (ID,NAME) values (1000,'CA1');
insert into CA (ID,NAME) values (1001,'CA2');
insert into CB (ID,NAME) values (1000,'CB1');
insert into CB (ID,NAME) values (1001,'Cb2');

insert into COMMENT(ID,COMMENT,PARENT_TYPE,PARENT_FK) values(2000,'TestA1','A',1000);
insert into COMMENT(ID,COMMENT,PARENT_TYPE,PARENT_FK) values(2001,'TestA2','A',1000);
insert into COMMENT(ID,COMMENT,PARENT_TYPE,PARENT_FK) values(2002,'TestA3','A',1001);
insert into COMMENT(ID,VALEUR,PARENT_TYPE,PARENT_FK) values(2003,1,'B',1000);
insert into COMMENT(ID,VALEUR,PARENT_TYPE,PARENT_FK) values(2004,2,'B',1001);
insert into COMMENT(ID,VALEUR,PARENT_TYPE,PARENT_FK) values(2005,3,'B',1001);
