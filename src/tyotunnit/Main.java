package tyotunnit;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;


public class Main {
   static String[] v_paivat = {"su", "ma", "ti", "ke", "to", "pe", "la"};

    static class Tyo {
        public String idx;
        public String paiva;
        public String tyo_id;
        public int duunaa_minuutteina;
        public String alku_aika;
        public String loppu_aika;
    }

    static class Duunari {
        public int tyo_aika;
        public String paiva;
        public String loppu_aika;
        public ArrayList<Tyo> tyot;

        public Duunari() {
            tyot = new ArrayList<>();
            this.tyo_aika = 0;
            this.loppu_aika = "12:12";
            this.paiva = "ma0";
        }

    }

    public static void main(String[] args) {
        ArrayList<Tyo> tyot = lue_tyot_filusta();
        HashSet<Integer> tehdyt_duunit = new HashSet<>();

        int max_minuutit = 117 * 60;
        int duuni_tehty = 0;


        ArrayList<Duunari> duunarit = new ArrayList<>();

        // niin kaua kun on Tyo tekemäti
        while (duuni_tehty < tyot.size()) {
            //palkkaa uusi Duunari
            Duunari nyt_duunari = new Duunari();
            int idi = 0;

            // niin kauan kun kyseinen Tyo ei ylitä maksimi minuutteja
            while (nyt_duunari.tyo_aika < max_minuutit && idi < tyot.size()) {
                if (!tehdyt_duunit.contains(idi) && ok(nyt_duunari, tyot.get(idi)) &&
                        nyt_duunari.tyo_aika + tyot.get(idi).duunaa_minuutteina <= max_minuutit) {

                    // lisää työ
                    nyt_duunari = lisaa_tyo(nyt_duunari, tyot.get(idi));
                    duuni_tehty++;
                    tehdyt_duunit.add(idi);

                }
                idi++;

            }
            duunarit.add(nyt_duunari);
        }

        // tuntilista 
        System.out.println("### Duunarit = " + duunarit.size() + " ###");
        int idnumero = 1;
        for (Main.Duunari Duunari : duunarit) {
            HashSet<String> tyo_paiva_maara = new HashSet<>();
            for (Tyo t : Duunari.tyot) {
                tyo_paiva_maara.add(t.paiva);
            }

            System.out.println("Duunari " + idnumero + "  ### duunaa " + (Duunari.tyo_aika / 60) +
                    " tuntia" + " ### ja " + (21 - tyo_paiva_maara.size()) + " lomapäivää");
            idnumero+=1;
        }

    }

    private static Duunari lisaa_tyo(Duunari nyt_duunari, Tyo tyo) {

        // onko työ eka tai menee pikkutunneille
        if (nyt_duunari.tyo_aika == 0 || (Integer.parseInt(tyo.alku_aika.substring(0, 2))) > (Integer.parseInt(tyo.loppu_aika.substring(0, 2)))) {
            nyt_duunari.paiva = tyo.paiva;
        } else {
            // seuraava päivänumero
            int nyt_paiva = tyo_paiva_nro(tyo.paiva);
            int nyt_viikko = Integer.parseInt("" + tyo.paiva.charAt(2));
            int seuraava_paiva = (nyt_paiva + 1) % 7;
            if (seuraava_paiva == 0) {
                nyt_viikko += 1;
                if (nyt_viikko > 3) {
                    nyt_viikko = 3;
                }
            }
            nyt_duunari.paiva = v_paivat[seuraava_paiva] + Integer.toString(nyt_viikko);
        }
        // lisää aika -> Tyo
        nyt_duunari.tyo_aika += tyo.duunaa_minuutteina;

        nyt_duunari.loppu_aika = tyo.loppu_aika;
        nyt_duunari.tyot.add(tyo);

        return nyt_duunari;

    }

    private static int tyo_paiva_nro(String paiva) {
        // hae paiva numero
        String[] paivat1 = {"su1", "ma1", "ti1", "ke1", "to1", "pe1", "la1"};
        String[] paivat2 = {"su2", "ma2", "ti2", "ke2", "to2", "pe2", "la2"};
        String[] paivat3 = {"su3", "ma3", "ti3", "ke3", "to3", "pe3", "la3"};
        for (int i = 0; i < paivat1.length; i++) {
            if (paiva.equals(paivat1[i]) || paiva.equals(paivat2[i]) || paiva.equals(paivat3[i])) {
                return i;
            }
        }
        return 0;
    }

    private static boolean ok(Duunari nyt_duunari, Tyo tyo) {
        if (nyt_duunari.tyo_aika == 0) {
            return true;
            // tarkista edeltävä 
        } else if ((nyt_duunari.paiva.charAt(2) - '0') < (tyo.paiva.charAt(2) - '0')) {
            return true;

            // tarkista sama viikko ja edeltävä päivä
        } else if ((nyt_duunari.paiva.charAt(2) - '0') == (tyo.paiva.charAt(2) - '0') &&
                (tyo_paiva_nro(nyt_duunari.paiva) < tyo_paiva_nro(tyo.paiva))) {
            return true;

            // tunnit
        } else if ((nyt_duunari.paiva.charAt(2) - '0') == (tyo.paiva.charAt(2) - '0') &&
                (tyo_paiva_nro(nyt_duunari.paiva) == tyo_paiva_nro(tyo.paiva)) &&
                (Integer.parseInt((nyt_duunari.loppu_aika.substring(0, 2))) <
                        Integer.parseInt(tyo.alku_aika.substring(0, 2))
                )) {
            return true;
            // minuutit
        } else if ((nyt_duunari.paiva.charAt(2) - '0') == (tyo.paiva.charAt(2) - '0') &&
                (tyo_paiva_nro(nyt_duunari.paiva) == tyo_paiva_nro(tyo.paiva)) &&
                (Integer.parseInt((nyt_duunari.loppu_aika.substring(0, 2))) ==
                        Integer.parseInt(tyo.alku_aika.substring(0, 2))
                )
                && ((Integer.parseInt((nyt_duunari.loppu_aika.substring(3, 5))) <
                Integer.parseInt(tyo.alku_aika.substring(3, 5))))
                ) {

            return true;
        } else {
            return false;
        }

    }

    private static ArrayList<Tyo> lue_tyot_filusta() {

        ArrayList<Tyo> tyot = new ArrayList<>();
                                                            
        try (BufferedReader br = new BufferedReader(new FileReader("tyot.txt"))) {
            String line;
            // lukee datan rivirivilta
            while ((line = br.readLine()) != null) {
                String[] data = line.split("\t");
                Tyo nyt = new Tyo();
                nyt.idx = data[0];
                nyt.paiva = data[1];
                nyt.tyo_id = data[2];
                nyt.duunaa_minuutteina = Integer.parseInt(data[3]);
                nyt.alku_aika = data[4];
                String[] loppuaika_normalisoimatta = data[5].trim().split(":");

                // puristaa tunnit 0 24
                int tunti;
                if (Integer.parseInt(loppuaika_normalisoimatta[0]) % 24 == 0) {
                    tunti = 24;
                } else {
                    tunti = Integer.parseInt(loppuaika_normalisoimatta[0]) % 24;
                }
                String muokattu = String.format("%02d", tunti);
                nyt.loppu_aika = muokattu + ":" + loppuaika_normalisoimatta[1];
                tyot.add(nyt);
            }
            return tyot;
        } catch (Exception erkki) {
            return null;
        }
    }
}
