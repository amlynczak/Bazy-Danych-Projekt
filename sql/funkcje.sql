CREATE OR REPLACE FUNCTION SztukaPoId(id_sztuki INT)
RETURNS TABLE (
    tytul_sztuki VARCHAR(255),
    informacje TEXT,
    imie_rezysera VARCHAR(50),
    nazwisko_rezysera VARCHAR(50),
    dostepne_bilety INT,
    cena_ulgowy INT,
    cena_normalny INT,
    miejsce_realizacji VARCHAR(100),
    data_realizacji DATE
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        ST.tytul_sztuki,
        ST.informator,
        R.imie_rezysera,
        R.nazwisko_rezysera,
        TR.dostepne_bilety,
        TR.cena_ulgowy,
        TR.cena_normalny,
        TR.miejsce_realizacji,
        TR.data_realizacji
    FROM
        SztukiTeatralne ST
    JOIN
        Rezyser R ON ST.id_rezysera = R.id_rezysera
    JOIN
        TerminyRealizacji TR ON ST.id_sztuki = TR.id_sztuki
    WHERE
        ST.id_sztuki = SztukaPoId.id_sztuki;
END;
$$ LANGUAGE plpgsql;



CREATE OR REPLACE FUNCTION GetPlaysForActor(actor_id INT)
RETURNS TABLE (
    play_id INT,
    play_title VARCHAR(255),
    character_played VARCHAR(255)
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        ST.id_sztuki,
        ST.tytul_sztuki,
        OS.postac
    FROM
        ObsadaSztuki OS
    INNER JOIN
        SztukiTeatralne ST ON OS.id_sztuki = ST.id_sztuki
    WHERE
        OS.id_aktora = actor_id;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION SztukiDanegoRezysera(rezyser_id INT)
RETURNS TABLE (
    id_sztuki INT,
    tytul_sztuki VARCHAR(255)
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        st.id_sztuki,
        st.tytul_sztuki
    FROM
        SztukiTeatralne st
    WHERE
        st.id_rezysera = rezyser_id;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION ObsadaPoId(sztuka_id INT)
RETURNS TABLE (
    postac VARCHAR(255),
    imie_aktora VARCHAR(50),
    nazwisko_aktora VARCHAR(50)
)
AS $$
BEGIN
    RETURN QUERY
    SELECT
        os.postac,
        a.imie,
        a.nazwisko
    FROM
        ObsadaSztuki os
    INNER JOIN
        Aktorzy a ON os.id_aktora = a.id_aktora
    WHERE
        os.id_sztuki = sztuka_id;
END;
$$ LANGUAGE plpgsql;

