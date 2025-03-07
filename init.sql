CREATE DATABASE skins;

\c skins

CREATE SEQUENCE IF NOT EXISTS users_id_seq;
CREATE SEQUENCE IF NOT EXISTS skins_id_seq;
CREATE SEQUENCE IF NOT EXISTS user_security_id_seq;

create table if not exists public.users
(
    id       bigint default (nextval('users_id_seq'::regclass))::regclass not null
    primary key,
    age      integer                                                      not null,
    balance  integer                                                      not null,
    created  timestamp(6)                                                 not null,
    email    varchar(255)                                                 not null
    constraint uk_6dotkott2kjsp8vw4d0m25fb7
    unique,
    username varchar(255)                                                 not null
    );

alter table public.users
    owner to postgres;

create table if not exists public.skins
(
    id        bigint default (nextval('skins_id_seq'::regclass))::regclass not null
    primary key,
    cost      integer                                                      not null,
    float     real                                                         not null,
    skin_name varchar(255)                                                 not null
    constraint skins_skin_name_check
    check ((skin_name)::text = ANY
((ARRAY ['LOTOS'::character varying, 'DRAGON'::character varying, 'REDLINE'::character varying, 'FOBOS'::character varying, 'LUCKY'::character varying, 'WINSTON'::character varying, 'TESS'::character varying, 'FLASH'::character varying, 'POWER'::character varying, 'IMPERIAL'::character varying, 'GALAXY'::character varying, 'RED'::character varying, 'LEGION'::character varying, 'GIGABYTE'::character varying, 'DARK'::character varying, 'MAGNESUIM'::character varying, 'MAJOR'::character varying, 'CONDIUT'::character varying, 'FERRARI'::character varying, 'BEAR'::character varying, 'CLOSE'::character varying, 'HIKARI'::character varying, 'MAGNUS'::character varying, 'EAC'::character varying, 'FACEIT'::character varying, 'TORNADO'::character varying, 'MELIDOAS'::character varying, 'DIPSIZE'::character varying])::text[])),
    skin_type varchar(255)                                                 not null
    constraint skins_skin_type_check
    check ((skin_type)::text = ANY
((ARRAY ['AWP'::character varying, 'DEAGLE'::character varying, 'USP'::character varying, 'AK47'::character varying])::text[])),
    user_id   bigint
    constraint skins_users_id_fk
    references public.users
    on update set null on delete set null
    );

alter table public.skins
    owner to postgres;

create table if not exists public.user_security
(
    id            bigint       default (nextval('user_security_id_seq'::regclass))::regclass not null
    primary key,
    is_blocked    boolean      default false                                                 not null,
    role          varchar(255) default 'USER'::character varying                             not null
    constraint user_security_role_check
    check ((role)::text = ANY
((ARRAY ['USER'::character varying, 'ADMIN'::character varying, 'SUPERADMIN'::character varying])::text[])),
    user_id       bigint
    constraint user_security_users_id_fk
    references public.users
    on update cascade on delete cascade,
    user_login    varchar(255)                                                               not null
    constraint uk_767aan4k40trdlnsywykqbsox
    unique,
    user_password varchar(255)                                                               not null,
    activation_token    varchar(255)
    );

alter table public.user_security
    owner to postgres;

create unique index if not exists user_security_user_id_uindex
    on public.user_security (user_id);

