create sequence url_id_seq start 100000 increment 1;
create table urls (
    id bigint primary key,
    code varchar(32) not null,
    long_url TEXT not null,
    is_custom boolean default false,
    created_at timestamptz default now(),
    expires_at timestamptz,
    click_count bigint not null default 0
);
create unique index idx_urls_code on urls(code);