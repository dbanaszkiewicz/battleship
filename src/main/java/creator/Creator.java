package creator;

import console.ConsoleHelper;
import data.model.Map;

import java.util.Scanner;

public class Creator {
    public static Map createUserMap() {
        Map map = new Map();
        setShips(map);
        return map;
    }

    private static void setShips(Map map) {
        int[] shipSize = new int[]{4, 3, 3, 2, 2, 2, 1, 1, 1, 1};

        for(int size : shipSize) {
            ConsoleHelper.clear();

            Map.displayUserMap(map);

            Scanner in = new Scanner(System.in);
            String start;
            String end;
            while (true) {
                if (size > 1) {
                    while (true) {
                        System.out.print("Podaj pierwszą współrzędną statku zajmującego " + size + " pola: ");
                        start = in.nextLine();
                        if (!Map.isValidField(start)) {
                            System.out.println("Podana współrzędna jest niepoprawna! Wprowadź współrzędną jeszcze raz!");
                        } else {
                            break;
                        }
                    }
                    while (true) {
                        System.out.print("Podaj ostatnią współrzędną statku zajmującego " + size + " pola: ");
                        end = in.nextLine();
                        if (!Map.isValidField(start)) {
                            System.out.println("Podana współrzędna jest niepoprawna! Wprowadź współrzędną jeszcze raz!");
                        } else {
                            break;
                        }
                    }
                } else {
                    while (true) {
                        System.out.print("Podaj współrzędną statku zajmującego 1 pole: ");
                        start = in.nextLine();
                        if (!Map.isValidField(start)) {
                            System.out.println("Podana współrzędna jest niepoprawna! Wprowadź współrzędną jeszcze raz!");
                        } else {
                            break;
                        }
                    }
                    end = start;
                }

                if (!map.canShipOnMap(start, end)) {
                    System.out.println("Nie można utworzyć statku dla podanych współrzędnych. Podaj je jeszcze raz!");
                } else if (map.getShipSize(start, end) != size) {
                    System.out.println("Statek o podanych wzpółrzędnych musi zajmować " + size + " pola. Podaj je jeszcze raz!");
                } else {
                    break;
                }
            }

            map.setShip(start, end);
        }
    }
}
