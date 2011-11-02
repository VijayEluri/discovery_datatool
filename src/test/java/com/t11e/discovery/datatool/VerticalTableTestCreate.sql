create table vertical_master_items (
  id bigint generated by default as identity,
  lastUpdated timestamp);

insert into vertical_master_items (id, lastUpdated) values
  (1, '2010-01-01-00.00.00.000000'),
  (2, '2010-01-01-00.00.00.000000'),
  (3, '2010-01-01-00.00.00.000000');

create table vertical_deleted (
  id bigint primary key,
  lastUpdated timestamp
);

insert into vertical_deleted (id, lastUpdated) values
  (4, '2010-01-01-00.00.00.000000'),
  (5, '2010-01-01-00.00.00.000000');

create table vertical_data (
  id bigint not null,
  name varchar(255),
  value varchar(255));

insert into vertical_data (id, name, value) values
  (1, 'color', 'red'),
  (1, 'color', ''),
  (1, 'color', null),
  (1, 'provider', 'p1'),
  (1, 'kind', 'k1'),
  (2, 'color', 'orange'),
  (2, 'color', 'yellow');

create table vertical_subquery_address (
  id bigint generated by default as identity,
  parent_id bigint not null,
  discriminator varchar(255),
  street varchar(255));

insert into vertical_subquery_address (parent_id, discriminator, street) values
  (1, 'biz', '1 main st'),
  (2, 'biz', '123 hidden by second biz address'),
  (2, 'biz', '123 main st'),
  (2, 'per', '456 main st');
