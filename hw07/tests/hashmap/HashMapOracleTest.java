package hashmap;

import org.junit.jupiter.api.Test;
import java.util.*;
import java.lang.reflect.Field;
import static org.junit.jupiter.api.Assertions.*;

public class HashMapOracleTest {
    record Key(int value) {
        @Override public int hashCode() { return value % 7 == 0 ? Integer.MIN_VALUE : -7; }
    }

    @Test
    void collisionNullAndMixedOperationsAgainstJdk() {
        for (Class<? extends Collection> bucket : List.of(ArrayList.class, LinkedList.class, HashSet.class, Stack.class, ArrayDeque.class)) {
            MyHashMap<Key,Integer> actual = MyHashMapFactory.createBucketedMap(bucket);
            Map<Key,Integer> expected = new HashMap<>();
            Random random = new Random(6172026);
            for (int step=0;step<12000;step++) {
                Key key=new Key(random.nextInt(250)); int op=random.nextInt(12);
                if (op<7) {
                    Integer value=op==0 ? null : random.nextInt(); actual.put(key,value); expected.put(key,value);
                } else if (op<9) { assertEquals(expected.remove(key),actual.remove(key)); }
                else if (op==9 && step%101==0) { actual.clear();expected.clear(); }
                assertEquals(expected.size(),actual.size());
                assertEquals(expected.containsKey(key),actual.containsKey(key));
                assertEquals(expected.get(key),actual.get(key));
                if (step%100==0) {
                    assertEquals(expected.keySet(),actual.keySet());
                    for (Key k:expected.keySet()) { assertEquals(expected.get(k),actual.get(new Key(k.value()))); }
                }
            }
        }
    }

    @Test
    void loadThresholdAndBackedKeySet() throws Exception {
        MyHashMap<Integer,String> map=new MyHashMap<>(3,.5);
        Field buckets=MyHashMap.class.getDeclaredField("buckets");buckets.setAccessible(true);
        assertEquals(3,((Object[])buckets.get(map)).length);
        map.put(1,null); map.put(1,"updated");assertEquals(3,((Object[])buckets.get(map)).length);
        Set<Integer> keys=map.keySet();map.put(2,null);
        assertEquals(6,((Object[])buckets.get(map)).length);assertTrue(keys.contains(2));
        assertTrue(keys.remove(2));assertFalse(map.containsKey(2));
        map.put(3,"third");map.put(4,"fourth");
        assertTrue(keys.retainAll(Set.of(1,3)));assertFalse(map.containsKey(4));
        Iterator<Integer> iterator=keys.iterator();Integer removed=iterator.next();iterator.remove();assertFalse(map.containsKey(removed));
        assertThrows(IllegalStateException.class,iterator::remove);
        assertThrows(UnsupportedOperationException.class,()->keys.add(9));
        keys.clear();assertEquals(0,map.size());
        MyHashMap<Integer,Integer> small=new MyHashMap<>(1,.1);small.put(1,1);
        assertTrue(1.0/((Object[])buckets.get(small)).length<=.1);
        assertThrows(IllegalArgumentException.class,()->new MyHashMap<>(0));
        assertThrows(IllegalArgumentException.class,()->new MyHashMap<>(1,Double.NaN));
    }
}
