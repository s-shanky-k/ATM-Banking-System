CREATE TABLE atm(
    card_no varchar(6) NOT NULL,
    holder_name varchar(10) NOT NULL,
    pin varchar(4) NOT NULL,
    balance int NOT NULL
);

INSERT INTO ATM VALUES ('100000','Shankar','1000',99999);
INSERT INTO ATM VALUES ('110000','Preeth','1234',100);
INSERT INTO ATM VALUES ('120000','Kumar','1111',9999);
INSERT INTO ATM VALUES ('130000','Jacob','0000',999);
INSERT INTO ATM VALUES ('140000','Felix','2222',999);


create table transfer (
    from_card varchar2(10) not null,
    to_card varchar2(10) not null,
    amount number(38) not null,
    transfer_date date not null,
    transfer_time varchar2(12) not null,
    balance number(38) not null,
    foreign key from_card references atm(card_no), 
    foreign key to_card references  atm(card_no)
);


insert into transfer values('100000','120000',500,to_date(sysdate,'dd-mm-yyy'),to_char(sysdate,'hh24:mi:ss'));
insert into transfer values('110000','120000',10,to_date(sysdate,'dd-mm-yyy'),to_char(sysdate,'hh24:mi:ss'));
insert into transfer values('100000','110000',500,to_date(sysdate,'dd-mm-yyy'),to_char(sysdate,'hh24:mi:ss'));
