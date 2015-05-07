package saga.util;

import static saga.util.TextUtils.alignLeft;
import static saga.util.TextUtils.alignRight;
import static saga.util.TextUtils.fillChar;

public class StringTable {

    public static int[] getColWidths(String[][] table) {
        int[] colWidths = new int[table[0].length];
        for (int col = 0; col < colWidths.length; col++) {
            colWidths[col] = 0;
        }
        for (int col = 0; col < colWidths.length; col++) {
            int maxWidth = colWidths[col];
            for (int row = 0; row < table.length; row++) {
                if (maxWidth < table[row][col].length()) {
                    maxWidth = table[row][col].length();
                }
            }
            colWidths[col] = maxWidth;
        }
        return colWidths;
    }

    public static void printSeparatorLine(int[] colWidths) {
        for (int col = 0; col < colWidths.length; col++) {
            System.out.print("+" + fillChar('-', colWidths[col] + 4));
        }
        System.out.println("+");
    }

    public static void printRow(int[] colWidths, String[] values) {
        for (int col = 0; col < colWidths.length; col++) {
            System.out.print(
                    "|"
                    + (col > 0 ? alignRight("  " + values[col] + "  ", colWidths[col] + 4)
                            : alignLeft("  " + values[col] + "  ", colWidths[col] + 4)));
        }
        System.out.println("|");
    }

}
