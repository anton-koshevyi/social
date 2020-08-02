insert into users(email, username, publicity,
                  first_name, last_name,
                  role_moder, role_admin,
                  password)

values ('regular@mail.com', 'regular', 30,
        'User', 'Public',
        false, false,
        '$2a$10$XGftkeHPJIrQxXYWOkKeLeap8geTudQdKp1nPBiJ728gGOyf6ctfm'),

       ('moder@mail.com', 'moder', 20,
        'Moder', 'Internal',
        true, false,
        '$2a$10$XGftkeHPJIrQxXYWOkKeLeap8geTudQdKp1nPBiJ728gGOyf6ctfm'),

       ('admin@mail.com', 'admin', 10,
        'Admin', 'Private',
        true, true,
        '$2a$10$XGftkeHPJIrQxXYWOkKeLeap8geTudQdKp1nPBiJ728gGOyf6ctfm');

-- Passwords: password
