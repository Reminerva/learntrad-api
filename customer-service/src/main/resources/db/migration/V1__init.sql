create table m_customer (
    balance numeric,
    birth_date date,
    is_active boolean,
    created_at timestamp(6),
    updated_at timestamp(6),
    address varchar(255),
    fullname varchar(255),
    id varchar(255) not null,
    user_id varchar(255) not null unique,
    primary key (id)
)