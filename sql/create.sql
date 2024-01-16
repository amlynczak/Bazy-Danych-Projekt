-- Tworzenie tabeli Rezyser
CREATE TABLE Rezyser (
    id_rezysera SERIAL PRIMARY KEY,
    imie_rezysera VARCHAR(50),
    nazwisko_rezysera VARCHAR(50)
);

-- Tworzenie tabeli Aktorzy
CREATE TABLE Aktorzy (
    id_aktora SERIAL PRIMARY KEY,
    imie VARCHAR(50),
    nazwisko VARCHAR(50)
);

-- Tworzenie tabeli SztukiTeatralne
CREATE TABLE SztukiTeatralne (
    id_sztuki SERIAL PRIMARY KEY,
    tytul_sztuki VARCHAR(255),
    informator TEXT,
    id_rezysera INT,
    FOREIGN KEY (id_rezysera) REFERENCES Rezyser(id_rezysera)
);

-- Tworzenie tabeli ObsadaSztuki
CREATE TABLE ObsadaSztuki (
    id_obsady_sztuki SERIAL PRIMARY KEY,
    id_sztuki INT,
    id_aktora INT,
    postac VARCHAR(255),
    FOREIGN KEY (id_sztuki) REFERENCES SztukiTeatralne(id_sztuki),
    FOREIGN KEY (id_aktora) REFERENCES Aktorzy(id_aktora)
);

-- Tworzenie tabeli Bilety
CREATE TABLE Bilety (
    bilety_id SERIAL PRIMARY KEY,
    dostepne_bilety INT,
    cena_ulgowy INT,
    cena_normalny INT
);

-- Tworzenie tabeli TerminyRealizacji
CREATE TABLE TerminyRealizacji (
    id_terminu SERIAL PRIMARY KEY,
    id_sztuki INT,
    data_realizacji DATE,
    miejsce_realizacji VARCHAR(100),
    bilety_id INT,
    FOREIGN KEY (id_sztuki) REFERENCES SztukiTeatralne(id_sztuki),
    FOREIGN KEY (bilety_id) REFERENCES Bilety(bilety_id)
);

-- Tworzenie tabeli ZamowieniaBiletow
CREATE TABLE ZamowieniaBiletow (
    id_zamowienia SERIAL PRIMARY KEY,
    id_biletow INT,
    ilosc_biletow INT,
    data_zamowienia DATE,
    FOREIGN KEY (id_biletow) REFERENCES Bilety(bilety_id)
);
