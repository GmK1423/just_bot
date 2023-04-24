create table person
(
    id               integer,
    nickname         varchar,
    rang             varchar,
    number_of_points integer not null,
    admin            boolean default false
);

alter table person
    owner to postgres;
