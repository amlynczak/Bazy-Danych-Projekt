CREATE OR REPLACE VIEW Harmonogram_app AS
SELECT st.id_sztuki, st.tytul_sztuki, st.informator, r.imie_rezysera,
r.nazwisko_rezysera, tr.data_realizacji, tr.miejsce_realizacji
FROM SztukiTeatralne st
JOIN Rezyser r ON st.id_rezysera = r.id_rezysera
JOIN TerminyRealizacji tr ON st.id_sztuki = tr.id_sztuki;

CREATE OR REPLACE VIEW Aktorzy_app AS
SELECT id_aktora, imie, nazwisko
FROM Aktorzy;

CREATE OR REPLACE VIEW Rezyser_app AS
SELECT id_rezysera, imie_rezysera, nazwisko_rezysera
FROM Rezyser;
