CREATE OR REPLACE FUNCTION zmniejsz_dostepne_bilety() RETURNS TRIGGER AS $$
BEGIN
    UPDATE Bilety
    SET dostepne_bilety = GREATEST(dostepne_bilety - NEW.ilosc_biletow, 0)
    WHERE bilety_id = NEW.id_biletow;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger wywoływany po dodaniu nowego zamówienia biletów
CREATE TRIGGER trigger_zmniejsz_dostepne_bilety
AFTER INSERT ON ZamowieniaBiletow
FOR EACH ROW
EXECUTE FUNCTION zmniejsz_dostepne_bilety();
