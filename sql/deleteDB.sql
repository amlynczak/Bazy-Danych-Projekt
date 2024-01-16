DROP TRIGGER IF EXISTS trigger_zmniejsz_dostepne_bilety ON ZamowieniaBiletow;
DROP FUNCTION IF EXISTS zmiejsz_dostepne_bilety();

-- Usuwanie tabeli
DROP TABLE IF EXISTS SztukiTeatralne CASCADE;
DROP TABLE IF EXISTS ObsadaSztuki CASCADE;
DROP TABLE IF EXISTS Bilety CASCADE;
DROP TABLE IF EXISTS TerminyRealizacji CASCADE;
DROP TABLE IF EXISTS ZamowieniaBiletow CASCADE;
DROP TABLE IF EXISTS Rezyser CASCADE;
DROP TABLE IF EXISTS Aktorzy CASCADE;

DROP FUNCTION IF EXISTS ObsadaPoId CASCADE;