import main.*;
import browser.NgordnetQuery;
import browser.NgordnetQueryHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;

public class RankedHyponymsTest {
    @TempDir Path directory;

    @Test
    void rawCountsNotWeightsAndIntersectionBeforeTopK() throws Exception {
        Path syn=directory.resolve("syn"), hyp=directory.resolve("hyp"), words=directory.resolve("words"), years=directory.resolve("years");
        Files.writeString(syn, "0,left,left\n1,right,right\n2,sharedA,shared\n3,sharedB,shared\n4,leftOnly,only\n5,rightOnly,only\n6,zero,zero\n7,missing,missing\n");
        Files.writeString(hyp, "0,2,3,4,6,7\n1,2,3,5,6,7\n");
        Files.writeString(words, "sharedA\t2000\t20\t1\nsharedB\t2001\t100\t1\nleftOnly\t2000\t9999\t1\nrightOnly\t2000\t9999\t1\nzero\t2000\t0\t1\n");
        Files.writeString(years, "2000,100,1,1\n2001,100000,1,1\n");
        AtomicInteger loads = new AtomicInteger();
        HyponymsHandler handler = new HyponymsHandler(new WordNet(syn.toString(),hyp.toString()), () -> {
            loads.incrementAndGet(); return new NGramMap(words.toString(),years.toString());
        });
        assertEquals("[missing, sharedA, sharedB, zero]", handler.handle(new NgordnetQuery(List.of("left","right"),2100,1400,0)));
        assertEquals(0,loads.get());
        assertEquals("[sharedB]", handler.handle(new NgordnetQuery(List.of("left","right"),2000,2001,1)));
        assertEquals("[sharedA]", handler.handle(new NgordnetQuery(List.of("left","right"),2000,2000,1)));
        assertEquals("[sharedA, sharedB]", handler.handle(new NgordnetQuery(List.of("left","right"),2000,2001,100)));
        assertEquals("[]", handler.handle(new NgordnetQuery(List.of("left","right"),2002,2003,3)));
        assertEquals("[]", handler.handle(new NgordnetQuery(List.of("left","right"),2001,2000,3)));
        assertEquals(1,loads.get());
        assertThrows(IllegalArgumentException.class, () -> handler.handle(new NgordnetQuery(List.of("left"),2000,2001,-1)));
    }

    @Test
    void randomizedRankingAgainstPrimitiveMatrixOracle() throws Exception {
        Random random = new Random(61442026);
        Path syn=directory.resolve("syn"), hyp=directory.resolve("hyp"), words=directory.resolve("words"), years=directory.resolve("years");
        StringBuilder s=new StringBuilder("0,root,root\n"), h=new StringBuilder(), w=new StringBuilder(), y=new StringBuilder();
        long[][] raw = new long[30][12];
        for (int year=0;year<12;year++) { y.append(1900+year).append(",100000000000,1,1\n"); }
        for (int i=0;i<30;i++) {
            s.append(i+1).append(",word").append(i).append(",definition\n"); h.append("0,").append(i+1).append('\n');
            for (int year=0;year<12;year++) {
                if (random.nextBoolean()) {
                    raw[i][year] = 3000000000L + random.nextInt(1000000);
                    w.append("word").append(i).append('\t').append(1900+year).append('\t').append(raw[i][year]).append("\t1\n");
                }
            }
        }
        Files.writeString(syn,s);Files.writeString(hyp,h);Files.writeString(words,w);Files.writeString(years,y);
        NgordnetQueryHandler handler=AutograderBuddy.getHyponymsHandler(words.toString(),years.toString(),syn.toString(),hyp.toString());
        for (int trial=0;trial<200;trial++) {
            int start=random.nextInt(12),end=start+random.nextInt(12-start),k=1+random.nextInt(40);
            long[] totals=new long[30];List<Integer> ranked=new ArrayList<>();
            for (int i=0;i<30;i++) {
                for (int year=start;year<=end;year++) { totals[i]+=raw[i][year]; }
                if (totals[i]>0) { ranked.add(i); }
            }
            ranked.sort((a,b) -> { int c=Long.compare(totals[b],totals[a]); return c!=0 ? c : ("word"+a).compareTo("word"+b); });
            List<String> expected=new ArrayList<>();
            for (int i=0;i<Math.min(k,ranked.size());i++) { expected.add("word"+ranked.get(i)); }
            Collections.sort(expected);
            assertEquals(expected.toString(),handler.handle(new NgordnetQuery(List.of("root"),1900+start,1900+end,k)));
        }
    }

    @Test
    void officialEecsWideCountsAndFullDatasetExample() {
        NgordnetQueryHandler eecs=AutograderBuddy.getHyponymsHandler("data/word_history_eecs.csv",Main.YEAR_HISTORY_FILE,"data/synsets_eecs.txt","data/hyponyms_eecs.txt");
        assertEquals("[CS170, CS61A, CS61B, CS61C]",eecs.handle(new NgordnetQuery(List.of("CS61A"),2010,2020,4)));
        assertEquals("[CS61B]",eecs.handle(new NgordnetQuery(List.of("CS61A"),2018,2018,1)));
        assertEquals("[]",eecs.handle(new NgordnetQuery(List.of("CS61A"),1900,1901,5)));
        NgordnetQueryHandler full=AutograderBuddy.getHyponymsHandler(Main.WORD_HISTORY_SIZE14377_FILE,Main.YEAR_HISTORY_FILE,Main.SYNSETS_SIZE82191_FILE,Main.HYPONYMS_SIZE82191_FILE);
        assertEquals("[cake, cookie, kiss, snap, wafer]",full.handle(new NgordnetQuery(List.of("food","cake"),1950,1990,5)));
    }
}
