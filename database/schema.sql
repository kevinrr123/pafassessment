-- drop database if exists
drop schema if exists eshop;

create schema eshop;

use eshop;

create table customers(
    name varchar(32) not null unique,
    address varchar(128) not null,
    email varchar(128) not null, 

    primary key(name)
);

insert into customers(name, address, email) values
    ('fred', '201 Cobblestone Lane', 'fredflintstone@bedrock.com'),
    ('sherlock', '221B Baker Street, London', 'sherlock@consultingdetective.org'),
    ('spongebob', '124 Conch Street, Bikini Bottom', 'spongebob@yahoo.com'),
    ('jessica', '698 Candlewood Land, Cabot Cove', 'fletcher@gmail.com'),
    ('dursley', '4 Privet Drive, Little Whinging, Surrey', 'dursley@gmail.com');

create table orders(
    orderid varchar(8) not null,
    name varchar(32) not null unique, 
    deliveryid varchar(32) not null,
    address varchar(128) not null,
    email varchar(128) not null,
    orderdate date,

    
    primary key(orderid),

    constraint fk_name
        foreign key (name)
        references customers(name)
        on delete cascade,
    
    constraint fk_orderid
		foreign key(orderid) 
        references lineitem(orderid)
        on delete cascade
    
);

create table lineitem(
    item varchar(128) not null, 
    quantity int default '1',
    orderid varchar(8) not null,
    primary key(orderid)
    
	);

create table order_status(
    order_id varchar(8) not null,
    delivery_id varchar(128),
    status varchar(32) not null,
    status_update timestamp not null,

    primary key(order_id),

    constraint fk_order_id
		foreign key(order_id) 
        references orders(orderid)
        on delete cascade
    
	);


