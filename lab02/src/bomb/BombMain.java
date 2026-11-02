package bomb;

import common.IntList;

public class BombMain {
    /** Defuses the given phase of bomb b. */
    public static void answers(Bomb b, int phase) {
        // TODO: Find the correct inputs (passwords) to each phase using debugging techniques
        if (phase == 0) {
            b.phase0("39291226");
        }
        if (phase == 1) {
            b.phase1(IntList.of(0, 9, 3, 0, 8));
        }
        if (phase == 2) {
            b.phase2("793227803");
        }
    }
}
