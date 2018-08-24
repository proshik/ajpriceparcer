BEGIN TRANSACTION;

alter table assortment
  add column fetch_type TEXT;

update assortment
set fetch_type = 'MAIN_DAY_SCHEDULE'
where fetch_type is null;

alter table assortment
  alter column fetch_type set Not null;

END TRANSACTION;