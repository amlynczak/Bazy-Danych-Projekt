-- Tworzenie funkcji
CREATE OR REPLACE FUNCTION aktualizuj_liczbe_biletow()
RETURNS TRIGGER AS $$
BEGIN
  UPDATE TerminyRealizacji
  SET dostepne_bilety = dostepne_bilety - NEW.ilosc_biletow_ulgowe - NEW.ilosc_biletow_normalne
  WHERE id_terminu = NEW.id_terminu;

  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER po_wstawieniu_do_ZamowieniaBiletow
AFTER INSERT ON ZamowieniaBiletow
FOR EACH ROW
EXECUTE FUNCTION aktualizuj_liczbe_biletow();
