package data.model;

import java.util.HashMap;

public class Map {

    private final static char SHIP = 'O'; // okręt
    public final static char HIT = 'H'; // trafiony
    public final static char SUNK = 'X'; // zatopiony
    private final static char MISS = '@'; // pudło

    private HashMap<Character, HashMap<Integer, Character>> fields;

    public Map() {
        this.fields = new HashMap<>();
        // set empty map
        for (int a = 1; a <= 10; a++) {
            HashMap<Integer, Character> row = new HashMap<>();
            for (int b = 1; b <= 10; b++) {
                row.put(b, ' ');
            }

            this.fields.put(this.intToChar(a), row);
        }
    }

    public void setShip(String startField, String endField) {
        char startLetter = this.getLetter(startField);
        char endLetter = this.getLetter(endField);
        int startDigit = this.getDigit(startField);
        int endDigit = this.getDigit(endField);

        if (startLetter == endLetter) {
            int min = Math.min(startDigit, endDigit);
            int max = Math.max(startDigit, endDigit);

            for (int i = min; i <= max; i++) {
                this.fields.get(startLetter).replace(i, Map.SHIP);
            }
        } else {
            int min = Math.min(this.charToInt(startLetter), this.charToInt(endLetter));
            int max = Math.max(this.charToInt(startLetter), this.charToInt(endLetter));

            for (int i = min; i <= max; i++) {
                this.fields.get(this.intToChar(i)).replace(startDigit, Map.SHIP);
            }
        }
    }

    public boolean canShipOnMap(String startField, String endField) {
        char startLetter = this.getLetter(startField);
        char endLetter = this.getLetter(endField);
        int startDigit = this.getDigit(startField);
        int endDigit = this.getDigit(endField);

        if (startLetter == endLetter) {
            int min = Math.min(startDigit, endDigit);
            int max = Math.max(startDigit, endDigit);

            for (int i = min; i <= max; i++) {
                if (this.isCannotBeShipInField("" + startLetter + i)) {
                    return false;
                }
            }
        } else if (startDigit == endDigit) {
            int min = Math.min(this.charToInt(startLetter), this.charToInt(endLetter));
            int max = Math.max(this.charToInt(startLetter), this.charToInt(endLetter));

            for (int i = min; i <= max; i++) {
                if (this.isCannotBeShipInField("" + this.intToChar(i) + startDigit)) {
                    return false;
                }
            }
        } else {
            return false;
        }

        return true;
    }

    public int getShipSize(String startField, String endField) {
        char startLetter = this.getLetter(startField);
        char endLetter = this.getLetter(endField);
        int startDigit = this.getDigit(startField);
        int endDigit = this.getDigit(endField);

        if (startLetter == endLetter) {
            int min = Math.min(startDigit, endDigit);
            int max = Math.max(startDigit, endDigit);

            return max - min + 1;
        } else {
            int min = Math.min(this.charToInt(startLetter), this.charToInt(endLetter));
            int max = Math.max(this.charToInt(startLetter), this.charToInt(endLetter));

            return max - min + 1;
        }
    }

    public boolean isEndGame() {
        int numberOfSunkShip = 0;
        for (HashMap<Integer, Character> f : this.fields.values()) {
            for (Character c : f.values()) {
                if (c.equals(Map.SUNK)) {
                    numberOfSunkShip++;
                }
            }
        }

        return numberOfSunkShip == 20;
    }


    private boolean isCannotBeShipInField(String field) {
        String[] oFields = new String[9];
        oFields[0] = this.getTopField(field);
        oFields[1] = oFields[0] == null ? null : this.getRightField(oFields[0]);
        oFields[2] = this.getRightField(field);
        oFields[3] = oFields[2] == null ? null : this.getBottomField(oFields[2]);
        oFields[4] = this.getBottomField(field);
        oFields[5] = oFields[4] == null ? null : this.getLeftField(oFields[4]);
        oFields[6] = this.getLeftField(field);
        oFields[7] = oFields[6] == null ? null : this.getTopField(oFields[6]);
        oFields[8] = field;

        for (String f : oFields) {
            if (f != null && this.getFieldValue(f).equals(Map.SHIP)) {
                return true;
            }
        }

        return false;
    }

    public Character shotToUserMap(String field) {
        if (this.getFieldValue(field).equals(Map.SHIP)) {
            if (isShipSunk(field, new HashMap<>())) {
                return this.shot(field, true, true);
            } else {
                return this.shot(field, true, false);
            }
        } else {
            return this.shot(field, false, false);
        }
    }

    private boolean isShipSunk(String field, HashMap<String, Boolean> fetched) {
        fetched.put(field, true);

        boolean val = true;

        String[] fields = new String[]{this.getTopField(field), this.getBottomField(field), this.getLeftField(field), this.getRightField(field)};

        for (String f : fields) {
            if (f != null && !fetched.containsKey(f) && this.getFieldValue(f) != null) {
                if (this.getFieldValue(f).equals(Map.SHIP)) {
                    return false;
                } else if (this.getFieldValue(f).equals(Map.SUNK)) {
                    val = val && this.isShipSunk(f, fetched);
                }
            }
        }

        return val;
    }

    public Character shot(String field, boolean hit, boolean sunk) {
        if (hit) {
            this.fields.get(this.getLetter(field)).replace(this.getDigit(field), Map.SUNK);

            if (sunk) {
                HashMap<String, Boolean> fetched = new HashMap<>();
                this.setMissAfterSunk(field, fetched);
                return Map.SUNK;
            }

            return Map.HIT;
        } else {
            this.fields.get(this.getLetter(field)).replace(this.getDigit(field), Map.MISS);
            return Map.MISS;
        }
    }

    public boolean canBeShot(String field) {
        return !(this.getFieldValue(field).equals(Map.MISS) || this.getFieldValue(field).equals(Map.SUNK) || this.getFieldValue(field).equals(Map.HIT));
    }

    private void setMissAfterSunk(String field, HashMap<String, Boolean> fetched) {
        fetched.put(field, true);

        if (this.getFieldValue(field).equals(Map.SUNK)) {
            String f;

            //top and top-right
            f = this.getTopField(field);
            if (f != null) {
                if (!fetched.containsKey(f)) {
                    this.setMissAfterSunk(f, fetched);
                }

                f = this.getRightField(f);
                if (f != null && !fetched.containsKey(f)) {
                    this.setMissAfterSunk(f, fetched);
                }
            }

            //right and bottom-right
            f = this.getRightField(field);
            if (f != null) {
                if (!fetched.containsKey(f)) {
                    this.setMissAfterSunk(f, fetched);
                }

                f = this.getBottomField(f);
                if (f != null && !fetched.containsKey(f)) {
                    this.setMissAfterSunk(f, fetched);
                }
            }

            //bottom and bottom-left
            f = this.getBottomField(field);
            if (f != null) {
                if (!fetched.containsKey(f)) {
                    this.setMissAfterSunk(f, fetched);
                }

                f = this.getLeftField(f);
                if (f != null && !fetched.containsKey(f)) {
                    this.setMissAfterSunk(f, fetched);
                }
            }

            //left and top-left
            f = this.getLeftField(field);
            if (f != null) {
                if (!fetched.containsKey(f)) {
                    this.setMissAfterSunk(f, fetched);
                }

                f = this.getTopField(f);
                if (f != null && !fetched.containsKey(f)) {
                    this.setMissAfterSunk(f, fetched);
                }
            }


        } else {
            if (this.getFieldValue(field) == ' ') {
                this.setFieldValue(field, Map.MISS);
            }
        }
    }

    public Character getFieldValue(String field) {
        try {
            return this.fields.get(this.getLetter(field)).get(this.getDigit(field));
        } catch (Exception e) {
            System.out.println(field + " - " + this.getLetter(field) + "   " + this.getDigit(field));
            return ' ';
        }
    }

    public void setFieldValue(String field, Character value) {
        this.fields.get(this.getLetter(field)).replace(this.getDigit(field), value);
    }

    private String getLeftField(String field) {
        int number = Map.mapStringToInt(field);

        if (number - number % 10 == 10) {
            return null;
        } else {
            return mapIntToString(number - 10);
        }
    }

    private String getRightField(String field) {
        int number = Map.mapStringToInt(field);

        if (number - number % 10 == 100) {
            return null;
        } else {
            return mapIntToString(number + 10);
        }
    }

    private String getTopField(String field) {
        int number = Map.mapStringToInt(field);

        if (number % 10 == 0) {
            return null;
        } else {
            return mapIntToString(number - 1);
        }
    }

    private String getBottomField(String field) {
        int number = Map.mapStringToInt(field);

        if (number % 10 == 9) {
            return null;
        } else {
            return mapIntToString(number + 1);
        }
    }

    public static String mapIntToString(int field) {

        int digit = field % 10 + 1;
        String letter = new String(new char[]{(char) (((field - digit) + 1) / 10 + ((int) 'A') - 1)});
        return letter + digit + "";
    }

    private  static int mapStringToInt(String field) {
        field = field.toUpperCase();

        int letterValue = (((int) field.charAt(0)) - ((int) 'A') + 1) * 10;
        int digitValue;
        if (field.length() == 2) {
            digitValue = Integer.parseInt(field.charAt(1) + "") - 1;
        } else {
            digitValue = 9;
        }

        return letterValue + digitValue;
    }

    private char getLetter(String field) {
        return field.charAt(0);
    }

    private char intToChar(int ci) {
        return new String(new char[]{(char) (ci + ((int) 'A') - 1)}).charAt(0);
    }

    private int charToInt(char c) {
        return (int) c - ((int) 'A') + 1;
    }

    private int getDigit(String field) {
        if (field.length() == 3) {
            return 10;
        } else {
            return Integer.parseInt(field.charAt(1) + "");
        }
    }

    public static void displayUserMap(Map map) {
        displayMaps(map, null);
    }

    public static boolean isValidField(String field) {
        HashMap<String, Boolean> validFields = new HashMap<>();

        String[] letters = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
        int[] digits = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

        for (int d : digits) {
            for (String l : letters) {
                validFields.put(l + d, true);
            }
        }
        return validFields.containsKey(field);
    }

    public static void displayMaps(Map userMap, Map secondMap) {

        String[] letters = new String[]{"0", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
        int[] digits = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

        if (secondMap == null) {
            System.out.println("\tMapa użytkownika");
        } else {
            System.out.println("\tMapa użytkownika\t\t\t\tMapa przeciwnika");
        }

        for (int d : digits) {
            StringBuilder line = new StringBuilder();

            // user map
            if (d == 0) {
                for (String l : letters) {
                    if (l.equals("0")) {
                        line.append("   ");
                    } else {
                        line.append(l).append(" ");
                    }
                }
            } else {
                for (String l : letters) {
                    if (l.equals("0")) {
                        if (d == 10) {
                            line.append(d).append(" ");
                        } else {
                            line.append(d).append("  ");
                        }
                    } else {
                        line.append(userMap.getFieldValue(l + d + "")).append(" ");
                    }
                }
            }

            //opponent map

            line.append("\t\t\t");

            if (secondMap != null) {
                if (d == 0) {
                    for (String l : letters) {
                        if (l.equals("0")) {
                            line.append("   ");
                        } else {
                            line.append(l).append(" ");
                        }
                    }
                } else {
                    for (String l : letters) {
                        if (l.equals("0")) {
                            if (d == 10) {
                                line.append(d).append(" ");
                            } else {
                                line.append(d).append("  ");
                            }
                        } else {
                            line.append(secondMap.getFieldValue(l + d + "")).append(" ");
                        }
                    }
                }
            }
            System.out.println(line.toString());
        }
    }
}
