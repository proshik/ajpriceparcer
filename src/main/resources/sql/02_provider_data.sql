BEGIN TRANSACTION;

  insert into provider (id, created_date, title, url, enabled, type)
  values (nextval('provider_id_seq'), CURRENT_TIMESTAMP, 'AJ.ru', 'http://aj.ru', true, 'AJ');

  insert into provider (id, created_date, title, url, enabled, type)
  values (nextval('provider_id_seq'), CURRENT_TIMESTAMP, 'GSM Store', 'http://gsm-store.ru', true, 'GSM_STORE');

  insert into provider (id, created_date, title, url, enabled, type)
  values (nextval('provider_id_seq'), CURRENT_TIMESTAMP, 'iStore-sbp', 'http://istorespb.ru', false, 'ISTORE_SBP');

  insert into provider (id, created_date, title, url, enabled, type)
  values (nextval('provider_id_seq'), CURRENT_TIMESTAMP, 'Citilink', 'http://citilink.ru', false, 'CITI_LINK');

END TRANSACTION;