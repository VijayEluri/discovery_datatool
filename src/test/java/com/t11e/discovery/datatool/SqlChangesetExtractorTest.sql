create table empty_table (
  id bigint generated by default as identity,
  last_updated timestamp
);

create table date_range_test (
  id bigint generated by default as identity,
  last_updated timestamp
);
insert into date_range_test (last_updated) values
  ('2010-02-01-00.00.00.000000'),
  ('2010-02-01-00.00.01.000000'),
  ('2010-02-01-00.00.03.000000');

create table string_column_test (
  id bigint generated by default as identity,
  col_fixed char(20),
  col_string varchar(255),
  col_clob clob
);
insert into string_column_test (col_fixed, col_string, col_clob) values
  (NULL, NULL, NULL),
  ('', '', ''),
  ('a', 'b', 'c'),
  (' a ', ' b ', ' c ');

create table numeric_column_test (
  id bigint generated by default as identity,
  col_int int,
  col_double double
);
insert into numeric_column_test (col_int, col_double) values
  (NULL, NULL),
  (0, 0),
  (12, 34.56);

create table datetime_column_test (
  id bigint generated by default as identity,
  col_date date,
  col_time time,
  col_datetime timestamp
);
insert into datetime_column_test (col_date, col_time, col_datetime) values
  (NULL, NULL, NULL),
  ('2010-01-01', '00:00:00', '2010-01-01 00:00:00');

create table subquery_test (
  id bigint primary key generated by default as identity
);
create table subquery_joined_test (
  id bigint generated by default as identity,
  parent_id bigint not null,
  name varchar(255)
);
alter table subquery_joined_test add foreign key (parent_id) references subquery_test(id);

insert into subquery_test (id) values
  (1),
  (2),
  (3);
insert into subquery_joined_test (parent_id, name) values
  (1, 'redfish'),
  (1, 'bluefish'),
  (3, 'onefish'),
  (3, 'twofish');
