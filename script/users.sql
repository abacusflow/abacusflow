INSERT INTO public.users (age, created_at, enabled, locked, name, nick, password, sex, updated_at)
VALUES (18, '2025-06-12 15:33:17.384000 +00:00', true, false, 'admin', '超级管理员', '123456', 'MALE',
        '2025-06-12 15:34:18.513000 +00:00');

INSERT INTO public.role (created_at, label, name, updated_at)
VALUES ('2025-06-12 15:35:07.223000 +00:00', '超级管理员', 'admin', '2025-06-12 15:35:16.941000 +00:00');

INSERT INTO public.users_roles (user_id, roles_id)
SELECT (SELECT id FROM public.users WHERE name = 'admin'),
       (SELECT id FROM public.role WHERE name = 'admin');

INSERT INTO public.permission (description, label, name)
VALUES ('用户管理', '用户管理', 'user');

INSERT INTO public.role_permissions (role_id, permissions_id)
SELECT (SELECT id FROM public.role WHERE name = 'admin'),
       (SELECT id FROM public.permission WHERE name = 'user');


