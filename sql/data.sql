-- Dodawanie przykładowych reżyserów
INSERT INTO Rezyser (imie_rezysera, nazwisko_rezysera) VALUES 
    ('Jan', 'Kowalski'),
    ('Anna', 'Nowak'),
    ('Michał', 'Lis');

-- Dodawanie przykładowych aktorów
INSERT INTO Aktorzy (imie, nazwisko) VALUES 
    ('Adam', 'Nowak'),
    ('Ewa', 'Kowalska'),
    ('Piotr', 'Szymański'),
    ('Adam', 'Abacki'),
    ('Ewa', 'Kot'),
    ('Apolonia', 'Tajner');

-- Dodawanie przykładowych sztuk teatralnych z przypisanymi reżyserami
INSERT INTO SztukiTeatralne (tytul_sztuki, informator, id_rezysera) VALUES 
    ('Hamlet', 'Klasyczna tragedia', 1),
    ('Romeo i Julia', 'Romantyczna historia miłosna', 2),
    ('Zemsta', 'Komedia', 3);

-- Dodawanie przykładowych obsad sztuk teatralnych
INSERT INTO ObsadaSztuki (id_sztuki, id_aktora, postac) VALUES 
    (1, 1, 'Hamlet'),
    (1, 2, 'Ofelia'),
    (1, 6, 'Gertruda'),
    (1, 4, 'Horacy'),
    (2, 1, 'Romeo'),
    (2, 2, 'Julia'),
    (2, 3, 'Merkucjo'),
    (2, 4, 'Tybalt'),
    (2, 5, 'Marta'),
    (2, 6, 'pani Capuleti'),
    (3, 3, 'Rejent Milczek'),
    (3, 2, 'Podstolina'),
    (3, 6, 'Klara');

-- Dodawanie przykładowych terminów realizacji sztuk z przypisanymi biletami
INSERT INTO TerminyRealizacji (id_sztuki, data_realizacji, miejsce_realizacji, dostepne_bilety, cena_ulgowy, cena_normalny) VALUES
    (1, '2024-05-15', 'Teatr Ludowy - Scena Główna', 140, 20, 40),
    (1, '2024-02-20', 'Teatr Ludowy - Scena Pod Ratuszem', 120, 15, 30),
    (2, '2025-02-01', 'Teatr Ludowy - Scena Pod Ratuszem', 100, 10, 15),
    (2, '2024-07-05', 'Teatr Ludowy - Scena Stolarnia', 150, 30, 50),
    (3, '2024-03-10', 'Teatr Ludowy - Scena Główna', 120, 15, 30),
    (3, '2024-03-15', 'Teatr Ludowy - Scena Stolarnia', 140, 20, 40);
