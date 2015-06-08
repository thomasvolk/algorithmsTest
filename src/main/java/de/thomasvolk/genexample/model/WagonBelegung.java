/**
 * Copyright (C) 2015 Thomas Volk
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package de.thomasvolk.genexample.model;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class WagonBelegung {
    private final Wagon wagon;
    private final Passagier[] passagierListe;
    private int[] passagierReihenfolge;

    private static int[] getInitialPassagierReihenfolge(int length) {
        int[] passagierReihenfolge = new int[length];
        for(int i=0; i < length; i++) {
            passagierReihenfolge[i] = i;
        }
        return passagierReihenfolge;
    }

    public WagonBelegung(Wagon wagon, Passagier[] passagierListe) {
        this(wagon, passagierListe, getInitialPassagierReihenfolge(passagierListe.length));
    }

    public WagonBelegung(Wagon wagon, Passagier[] passagierListe, int[] passagierReihenfolge) {
        this.wagon = wagon;
        this.passagierListe = passagierListe;
        if(wagon.getSitzplatzListe().length != passagierListe.length) {
            throw new IllegalStateException(String.format(
                    "Sitzplatzanzah %s weicht von Passagieranzahl %s ab!", wagon.getSitzplatzListe().length, passagierListe.length));
        }
        HashSet<Integer> sortierungSet = IntStream.of(passagierReihenfolge).mapToObj(i -> i).collect(Collectors.toCollection(HashSet::new));
        if(passagierReihenfolge.length != passagierListe.length) {
            throw new IndexOutOfBoundsException(String.format(
                    "Laenge der Reihenfolge %s weicht von Passagieranzahl %s ab!", passagierReihenfolge.length, passagierListe.length));
        }
        if(sortierungSet.size() != passagierListe.length) {
            throw new ArrayStoreException("Reihenfolge enthält dubletten!");
        }
        this.passagierReihenfolge = passagierReihenfolge;
    }

    public int getZufriedenheit() {
        return getSitzplatzVergabeListe().stream().map(SitzplatzVergabe::getZufriedenheit).reduce(0, (a, v) -> a + v);
    }

    public int getMaximaleZufriedenheit() {
        return getSitzplatzVergabeListe().stream().map(SitzplatzVergabe::getPassagier).map(Passagier::getMaximaleZufriedenheit).reduce(0, (a, v) -> a + v);
    }

    public int[] getPassagierReihenfolge() {
        return passagierReihenfolge;
    }

    public List<SitzplatzVergabe> getSitzplatzVergabeListe() {
        List<SitzplatzVergabe> result = new ArrayList<>();
        for(int i = 0; i < passagierReihenfolge.length; i++) {
            int sitzplatz = passagierReihenfolge[i];
            result.add(new SitzplatzVergabe(getSitzplatzListe()[sitzplatz], passagierListe[i]));
        }
        result.sort((sv1, sv2) -> sv1.getSitzplatz().getNummer() - sv2.getSitzplatz().getNummer());
        return result;
    }

    public WagonBelegung copy(int[] newPassagierReihenfolge) {
       return new WagonBelegung(wagon, passagierListe, newPassagierReihenfolge);
    }

    public Passagier[] getPassagierListe() {
        return passagierListe;
    }

    public Sitzplatz[] getSitzplatzListe() {
        return wagon.getSitzplatzListe();
    }

    public Wagon getWagon() {
        return wagon;
    }

    @Override
    public String toString() {
        return "Wagon{" +
                "plaetze=" + getAnzahlPlaetze() +
                ", passagierReihenfolge=" + Arrays.asList(passagierReihenfolge) +
                '}';
    }

    public int getAnzahlPlaetze() {
        return getSitzplatzListe().length;
    }
}
