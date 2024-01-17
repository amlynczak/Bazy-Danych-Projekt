CREATE OR REPLACE VIEW Harmonogram_app AS
SELECT st.id_sztuki, st.tytul_sztuki, st.informator, r.imie_rezysera,
r.nazwisko_rezysera, tr.data_realizacji, tr.miejsce_realizacji,
tr.dostepne_bilety, tr.cena_ulgowy, tr.cena_normalny
FROM SztukiTeatralne st
JOIN Rezyser r ON st.id_rezysera = r.id_rezysera
JOIN TerminyRealizacji tr ON st.id_sztuki = tr.id_sztuki
ORDER BY tr.data_realizacji;

CREATE OR REPLACE VIEW Aktorzy_app AS
SELECT id_aktora, imie, nazwisko
FROM Aktorzy;

CREATE OR REPLACE VIEW Rezyser_app AS
SELECT id_rezysera, imie_rezysera, nazwisko_rezysera
FROM Rezyser;

CREATE OR REPLACE VIEW Zamowienia_app AS
SELECT zb.id_zamowienia, tr.data_realizacji, s.tytul_sztuki,
zb.ilosc_biletow_ulgowe, zb,ilosc_biletow_normalne, zb.data_zamowienia
FROM ZamowieniaBiletow zb
JOIN TerminyRealizacji tr ON zb.id_terminu = tr.id_terminu
JOIN SztukiTeatralne s ON s.id_sztuki = tr.id_sztuki
ORDER BY zb.data_zamowienia DESC;