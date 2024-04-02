alter table public.users add username varchar(255);


UPDATE public.users SET username = 'admin1'::varchar(255) WHERE id = 1::bigint;
UPDATE public.users SET username = 'user1'::varchar(255) WHERE id = 2::bigint;
UPDATE public.users SET username = 'user2'::varchar(255) WHERE id = 3::bigint;
UPDATE public.users SET username = 'user3'::varchar(255) WHERE id = 4::bigint;
UPDATE public.users SET username = 'admin2'::varchar(255) WHERE id = 5::bigint;
UPDATE public.users SET username = 'user4'::varchar(255) WHERE id = 6::bigint;
UPDATE public.users SET username = 'user5'::varchar(255) WHERE id = 7::bigint;
UPDATE public.users SET username = 'user6'::varchar(255) WHERE id = 8::bigint;
