-- Dodawanie przykładowych reżyserów
INSERT INTO Rezyser (imie_rezysera, nazwisko_rezysera) VALUES 
    ('Jan', 'Kowalski'),
    ('Anna', 'Nowak'),
    ('Michał', 'Lis');

-- Dodawanie przykładowych aktorów
INSERT INTO Aktorzy (imie, nazwisko) VALUES 
    ('Adam', 'Nowak'),
    ('Ewa', 'Kowalska'),
    ('Piotr', 'Szymański');

-- Dodawanie przykładowych sztuk teatralnych z przypisanymi reżyserami
INSERT INTO SztukiTeatralne (tytul_sztuki, informator, id_rezysera) VALUES 
    ('Hamlet', 'Klasyczna tragedia', 1),
    ('Romeo i Julia', 'Romantyczna historia miłosna', 2),
    ('Zemsta', 'Komedia', 3);

-- Dodawanie przykładowych obsad sztuk teatralnych
INSERT INTO ObsadaSztuki (id_sztuki, id_aktora, postac) VALUES 
    (1, 1, 'Hamlet'),
    (1, 2, 'Ofelia'),
    (2, 1, 'Romeo'),
    (2, 2, 'Julia'),
    (3, 3, 'Rejent Milczek'),
    (3, 2, 'Podstolina');

-- Dodawanie przykładowych biletów
INSERT INTO Bilety (dostepne_bilety, cena_ulgowy, cena_normalny) VALUES 
    (100, 20, 40),
    (150, 25, 50),
    (120, 18, 36);

-- Dodawanie przykładowych terminów realizacji sztuk z przypisanymi biletami
INSERT INTO TerminyRealizacji (id_sztuki, data_realizacji, miejsce_realizacji, bilet_id) VALUES 
    (1, '2022-01-15', 'Teatr Ludowy - Scena Główna', 1),
    (1, '2022-01-20', 'Teatr Ludowy - Scena Pod Ratuszem', 2),
    (2, '2022-02-01', 'Teatr Ludowy - Scena Pod Ratuszem', 2),
    (2, '2022-02-05', 'Teatr Ludowy - Scena Stolarnia', 3),
    (3, '2022-03-10', 'Teatr Ludowy - Scena Główna', 3),
    (3, '2022-03-15', 'Teatr Ludowy - Scena Stolarnia', 1);
