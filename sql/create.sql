-- Tworzenie tabeli Rezyser
CREATE TABLE Rezyser (
    id_rezysera SERIAL PRIMARY KEY,
    imie_rezysera VARCHAR(50) NOT NULL,
    nazwisko_rezysera VARCHAR(50) NOT NULL
);

-- Tworzenie tabeli Aktorzy
CREATE TABLE Aktorzy (
    id_aktora SERIAL PRIMARY KEY,
    imie VARCHAR(50) NOT NULL,
    nazwisko VARCHAR(50) NOT NULL
);

-- Tworzenie tabeli SztukiTeatralne
CREATE TABLE SztukiTeatralne (
    id_sztuki SERIAL PRIMARY KEY,
    tytul_sztuki VARCHAR(255) NOT NULL,
    informator TEXT,
    id_rezysera INT,
    FOREIGN KEY (id_rezysera) REFERENCES Rezyser(id_rezysera)
);

-- Tworzenie tabeli ObsadaSztuki
CREATE TABLE ObsadaSztuki (
    id_obsady_sztuki SERIAL PRIMARY KEY,
    id_sztuki INT NOT NULL,
    id_aktora INT NOT NULL,
    postac VARCHAR(255) NOT NULL,
    FOREIGN KEY (id_sztuki) REFERENCES SztukiTeatralne(id_sztuki),
    FOREIGN KEY (id_aktora) REFERENCES Aktorzy(id_aktora)
);

-- Tworzenie tabeli TerminyRealizacji
CREATE TABLE TerminyRealizacji (
    id_terminu SERIAL PRIMARY KEY,
    id_sztuki INT NOT NULL,
    data_realizacji DATE NOT NULL,
    miejsce_realizacji VARCHAR(100) NOT NULL,
    dostepne_bilety INT,
    cena_ulgowy INT,
    cena_normalny INT,
    FOREIGN KEY (id_sztuki) REFERENCES SztukiTeatralne(id_sztuki)
);

-- Tworzenie tabeli ZamowieniaBiletow
CREATE TABLE ZamowieniaBiletow (
    id_zamowienia SERIAL PRIMARY KEY,
    id_terminu INT,
    ilosc_biletow_ulgowe INT,
    ilosc_biletow_normalne INT,
    data_zamowienia DATE,
    FOREIGN KEY (id_terminu) REFERENCES TerminyRealizacji(id_terminu)
);
