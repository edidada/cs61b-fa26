import org.junit.jupiter.api.Test;
import java.util.ArrayDeque;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;
/** Supplementary flood-fill oracle independent of union-find. */
class PercolationOracleTest {
    private boolean[][] reachable(boolean[][] open) {
        int n=open.length; boolean[][] full=new boolean[n][n]; ArrayDeque<Integer> queue=new ArrayDeque<>();
        for(int col=0;col<n;col++) if(open[0][col]) { full[0][col]=true;queue.add(col); }
        int[] dr={-1,1,0,0},dc={0,0,-1,1};
        while(!queue.isEmpty()) {
            int cell=queue.remove(),row=cell/n,col=cell%n;
            for(int direction=0;direction<4;direction++) {
                int r=row+dr[direction],c=col+dc[direction];
                if(r>=0&&r<n&&c>=0&&c<n&&open[r][c]&&!full[r][c]) {full[r][c]=true;queue.add(r*n+c);}
            }
        }
        return full;
    }
    @Test void randomizedEveryOpeningMatchesFloodFill() {
        Random random=new Random(613);
        for(int n=1;n<=12;n++) for(int trial=0;trial<8;trial++) {
            Percolation p=new Percolation(n); boolean[][] open=new boolean[n][n]; int count=0;
            for(int step=0;step<2*n*n;step++) {
                int row=random.nextInt(n),col=random.nextInt(n);p.open(row,col);
                if(!open[row][col]) {open[row][col]=true;count++;}
                boolean[][] full=reachable(open);boolean percolates=false;
                for(int r=0;r<n;r++) for(int c=0;c<n;c++) {
                    assertEquals(open[r][c],p.isOpen(r,c));assertEquals(full[r][c],p.isFull(r,c));
                    if(r==n-1&&full[r][c]) percolates=true;
                }
                assertEquals(count,p.numberOfOpenSites());assertEquals(percolates,p.percolates());
            }
        }
    }
    @Test void singletonAndBackwash() {
        Percolation one=new Percolation(1);assertFalse(one.percolates());assertFalse(one.isFull(0,0));
        one.open(0,0);one.open(0,0);assertTrue(one.percolates());assertEquals(1,one.numberOfOpenSites());
        Percolation p=new Percolation(3);p.open(0,0);p.open(1,0);p.open(2,0);p.open(2,2);
        assertTrue(p.percolates());assertFalse(p.isFull(2,2));p.open(1,2);assertFalse(p.isFull(2,2));
        p.open(0,2);assertTrue(p.isFull(2,2));
    }
    @Test void invalidCoordinates() {
        assertThrows(IllegalArgumentException.class,()->new Percolation(0));
        Percolation p=new Percolation(2);
        for(int[] rc:new int[][]{{-1,0},{0,-1},{2,0},{0,2}}) {
            assertThrows(IndexOutOfBoundsException.class,()->p.open(rc[0],rc[1]));
            assertThrows(IndexOutOfBoundsException.class,()->p.isOpen(rc[0],rc[1]));
            assertThrows(IndexOutOfBoundsException.class,()->p.isFull(rc[0],rc[1]));
        }
    }
    @Test void suppliedStatisticsSingletonBoundary() {
        PercolationStats stats=new PercolationStats(1,4);
        assertEquals(1.0,stats.mean());assertEquals(0.0,stats.stddev());
        assertEquals(1.0,stats.confidenceLow());assertEquals(1.0,stats.confidenceHigh());
        assertTrue(Double.isNaN(new PercolationStats(1,1).stddev()));
        assertThrows(IllegalArgumentException.class,()->new PercolationStats(1,0));
    }
}
