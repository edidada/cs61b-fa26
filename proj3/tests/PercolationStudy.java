import edu.princeton.cs.algs4.QuickFindUF;
import edu.princeton.cs.algs4.Stopwatch;
import edu.princeton.cs.algs4.StdRandom;
import java.util.Random;

/** Optional research driver; production Percolation still uses WeightedQuickUnionUF. */
public class PercolationStudy {
    private static final class QuickFindGrid {
        private final int n;
        private final boolean[] open;
        private final QuickFindUF paths;
        private final QuickFindUF full;
        private int count;
        QuickFindGrid(int width) {
            n=width; open=new boolean[n*n]; paths=new QuickFindUF(n*n+2);full=new QuickFindUF(n*n+1);
        }
        void open(int row,int col) {
            int site=row*n+col;if(open[site])return;open[site]=true;count++;
            if(row==0){paths.union(site,n*n);full.union(site,n*n);}
            if(row==n-1)paths.union(site,n*n+1);
            int[] dr={-1,1,0,0},dc={0,0,-1,1};
            for(int d=0;d<4;d++) {
                int r=row+dr[d],c=col+dc[d];
                if(r>=0&&r<n&&c>=0&&c<n&&open[r*n+c]) {paths.union(site,r*n+c);full.union(site,r*n+c);}
            }
        }
        boolean percolates(){return paths.find(n*n)==paths.find(n*n+1);}
    }
    private static double experiment(boolean quick,int n,int trials,long seed) {
        Random random=new Random(seed); double sum=0;
        for(int trial=0;trial<trials;trial++) {
            Percolation weighted=quick?null:new Percolation(n);
            QuickFindGrid simple=quick?new QuickFindGrid(n):null;
            while(quick?!simple.percolates():!weighted.percolates()) {
                int row=random.nextInt(n),col=random.nextInt(n);
                if(quick)simple.open(row,col);else weighted.open(row,col);
            }
            sum+=(double)(quick?simple.count:weighted.numberOfOpenSites())/(n*n);
        }
        return sum/trials;
    }
    public static void main(String[] args) {
        StdRandom.setSeed(6132026L);
        // Run the supplied original main as required by the threshold exercise.
        PercolationStats.main(new String[0]);
        System.out.println("algorithm,N,T,repetition,seed,seconds,mean_threshold");
        experiment(false,20,10,1);experiment(true,20,10,1);
        for(int n:new int[]{20,40,80})for(int trials:new int[]{30,60})for(int rep=0;rep<3;rep++) {
            long seed=613000L+rep;
            double expected=experiment(false,n,trials,seed);
            // Alternate timing order to reduce systematic warmup bias.
            for(int order=0;order<2;order++) {
                boolean quick=(order+rep)%2==0;
                Stopwatch timer=new Stopwatch();double mean=experiment(quick,n,trials,seed);double elapsed=timer.elapsedTime();
                if(Double.compare(expected,mean)!=0)throw new AssertionError("different opening trajectory");
                System.out.printf(java.util.Locale.ROOT,"%s,%d,%d,%d,%d,%.9f,%.9f%n",quick?"quick-find":"weighted",n,trials,rep,seed,elapsed,mean);
            }
        }
    }
}
