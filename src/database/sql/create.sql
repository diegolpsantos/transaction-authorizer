DROP TABLE IF EXISTS accounts, merchants, balances, transactions;

create table accounts (
    id text primary key,
    first_name text,
    last_name text,
    age integer,
    document text,
    inserted_at timestamp
    updated_at timestamp default current_timestamp
);

create table merchants (
    id text primary key,
    name varchar,
    description text,
    inserted_at timestamp
    updated_at timestamp default current_timestamp
);

CREATE INDEX merchant_name ON merchants USING btree (name);

create table balances (
    id text primary key,
    account_id text REFERENCES accounts,
    code varchar[],
    total_amount numeric,
    type text,
    inserted_at timestamp
    updated_at timestamp default current_timestamp);

CREATE INDEX balances_account_id ON balances USING btree (account_id);
CREATE INDEX codes ON balances USING btree (code);

create table transactions (
    id text primary key,
    account_id text REFERENCES accounts,
    total_amount numeric,
    merchant text,
    mcc text,
    balances varchar []);