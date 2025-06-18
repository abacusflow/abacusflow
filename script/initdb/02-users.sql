INSERT INTO public.users (age, created_at, enabled, locked, name, nick, password, sex, updated_at)
VALUES (18, '2025-06-12 15:33:17.384000 +00:00', true, false, 'admin', '超级管理员', '$2a$10$w6HLBTQcJhIFQcS6kOtgaOrJG3gm8GgmIGMfp3wiMwGW6OCA1Jd1S', 'M',
        '2025-06-12 15:34:18.513000 +00:00');
ALTER SEQUENCE users_id_seq RESTART WITH 100;

INSERT INTO public.roles (created_at, label, name, updated_at)
VALUES ('2025-06-12 15:35:07.223000 +00:00', '超级管理员', 'admin', '2025-06-12 15:35:16.941000 +00:00');
ALTER SEQUENCE roles_id_seq RESTART WITH 100;

INSERT INTO public.users_roles (user_id, role_id)
SELECT (SELECT id FROM public.users WHERE name = 'admin'),
       (SELECT id FROM public.roles WHERE name = 'admin');

INSERT INTO public.permissions (description, label, name)
VALUES ('用户管理', '用户管理', 'user');
ALTER SEQUENCE permissions_id_seq RESTART WITH 100;

INSERT INTO public.roles_permissions (role_id, permissions_id)
SELECT (SELECT id FROM public.roles WHERE name = 'admin'),
       (SELECT id FROM public.permissions WHERE name = 'user');


