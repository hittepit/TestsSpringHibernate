insert into CARGO (ID,NOM,PAYS) values (10,'Alpha','Panama');
insert into CARGO (ID,NOM,PAYS) values (11,'Beta','Panama');
insert into CARGO (ID,NOM,PAYS) values (12,'Gamma','Japan');

insert into ROUTE (ID,DE,A,CARGO_ID) values (20,'Anvers','Le Havre',10);
insert into ROUTE (ID,DE,A,CARGO_ID) values (21,'Le Havre','La Havane',10);
insert into ROUTE (ID,DE,A,CARGO_ID) values (22,'Anvers','Le Havre',11);
insert into ROUTE (ID,DE,A,CARGO_ID) values (23,'Le Havre','La Havane',11);

insert into CONTAINER (ID,NUMERO,CARGO_ID) values (30,'C001',10);
insert into CONTAINER (ID,NUMERO,CARGO_ID) values (31,'C002',10);
insert into CONTAINER (ID,NUMERO,CARGO_ID) values (32,'C003',11);
insert into CONTAINER (ID,NUMERO,CARGO_ID) values (33,'C004',11);
insert into CONTAINER (ID,NUMERO,CARGO_ID) values (34,'C005',12);

insert into ARTICLE (ID,NOM,CONTAINER_ID) values (40,'Livres',30);
insert into ARTICLE (ID,NOM,CONTAINER_ID) values (41,'Papier',30);
insert into ARTICLE (ID,NOM,CONTAINER_ID) values (42,'Papier',31);
insert into ARTICLE (ID,NOM,CONTAINER_ID) values (43,'Graines',31);
insert into ARTICLE (ID,NOM,CONTAINER_ID) values (44,'Graines',32);
insert into ARTICLE (ID,NOM,CONTAINER_ID) values (45,'Papier',33);
insert into ARTICLE (ID,NOM,CONTAINER_ID) values (46,'Livres',34);
insert into ARTICLE (ID,NOM,CONTAINER_ID) values (47,'Papier',34);
