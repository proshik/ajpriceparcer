BEGIN TRANSACTION;

alter table product
  alter column parameters type text;

END TRANSACTION;