
/************************************************************************************
 * @file LinHashMap.java
 *
 * @author  John Miller
 */

import static java.lang.System.out;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/************************************************************************************
 * This class provides hash maps that use the Linear Hashing algorithm.
 * A hash table is created that is an array of buckets.
 */
public class LinHashMap <K, V>
       extends AbstractMap <K, V>
       implements Serializable, Cloneable, Map <K, V>
{
	/**
	 * The number of slots (for key-value pairs) per bucket.
	 */
	private static final int SLOTS = 4;

	/**
	 * The class for type K.
	 */
	private final Class<K> classK;

	/**
	 * The class for type V.
	 */
	private final Class<V> classV;

	/********************************************************************************
	 * This inner class defines buckets that are stored in the hash table.
	 */
	private class Bucket {
		int nKeys;
		K[] key;
		V[] value;
		Bucket next;

		@SuppressWarnings("unchecked")
		Bucket(Bucket n) {
			nKeys = 0;
			key = (K[]) Array.newInstance(classK, SLOTS);
			value = (V[]) Array.newInstance(classV, SLOTS);
			next = n;
		} // constructor
	} // Bucket inner class

	/**
	 * The list of buckets making up the hash table.
	 */
	private final List<Bucket> hTable;

	/**
	 * The modulus for low resolution hashing
	 */
	private int mod1;

	/**
	 * The modulus for high resolution hashing
	 */
	private int mod2;

	/**
	 * Counter for the number buckets accessed (for performance testing).
	 */
	private int count = 0;

	/**
	 * The index of the next bucket to split.
	 */
	private int split = 0;
	/**
	 * The number of over flow counter.
	 */
	private int ofCounter = 0;
	/**
	 * The totalnumber of keys.
	 */
	private double tKeys = 0;

	/********************************************************************************
	 * Construct a hash table that uses Linear Hashing.
	 * 
	 * @param classK   the class for keys (K)
	 * @param classV   the class for keys (V)
	 * @param initSize the initial number of home buckets (a power of 2, e.g., 4)
	 */
	public LinHashMap(Class<K> _classK, Class<V> _classV) // , int initSize)
	{
		classK = _classK;
		classV = _classV;
		hTable = new ArrayList<>();
		mod1 = 4; // initSize;
		mod2 = 2 * mod1;
	} // constructor

	/********************************************************************************
	 * Return a set containing all the entries as pairs of keys and values.
	 * 
	 * @return the set view of the map
	 */
	public Set<Map.Entry<K, V>> entrySet() {
		var enSet = new HashSet<Map.Entry<K, V>>();
		for (int j = 0; j < hTable.size(); j++) {
			var nextBucket = hTable.get(j);
			if (nextBucket != null && nextBucket.next != null) {
				while (nextBucket != null) {
					for (int k = 0; k < nextBucket.nKeys; k++) {
						enSet.add(Map.entry((K) nextBucket.key[k], (V) nextBucket.value[k]));
					}
					nextBucket = nextBucket.next;
				}

			} // case for home buckets
			else if (nextBucket != null) {
				for (int k = 0; k < nextBucket.nKeys; k++) {
					enSet.add(Map.entry((K) nextBucket.key[k], (V) nextBucket.value[k]));
				}
			}
		}

		// T O B E I M P L E M E N T E D

		return enSet;
	} // entrySet

	/********************************************************************************
	 * Given the key, look up the value in the hash table.
	 * 
	 * @param key the key used for look up
	 * @return the value associated with the key
	 */
	public V get(Object key) {
		var i = h(key);
		if (i < split) {
			i = h2(key);
		}
		var nextBucket = hTable.get(i);
		int index;
		V value = null;
		// case for home + overflow
		if (nextBucket != null && nextBucket.next != null) {
			while (nextBucket != null) {
				for(int j = 0 ; j < nextBucket.nKeys ; j++)
				{
					if (nextBucket.key[j]!=null && nextBucket.key[j].equals(key) ) {
						index = Arrays.asList(nextBucket.key).indexOf(key);
						value = nextBucket.value[index];
						break;
					}
				}
				nextBucket = nextBucket.next;
			}

		} // case for home buckets
		else if (nextBucket != null) {
			for(int j = 0 ; j < nextBucket.nKeys ; j++)
			{	
				if (nextBucket.key[j]!=null && nextBucket.key[j].equals(key)) {
					index = Arrays.asList(nextBucket.key).indexOf(key);
					value = nextBucket.value[index];
					break;
				}
			}
		}
		return value;

	} // get

	/********************************************************************************
	 * Put the key-value pair in the hash table.
	 * 
	 * @param key   the key to insert
	 * @param value the value to insert
	 * @return null (not the previous value)
	 */
	public V put(K key, V value) {
		var i = h(key);
		double loadFactor;

		ofCounter = 0;

		if (i < split) {
			i = h2(key);
		}
		out.println("LinearHashMap.put: key = " + key + ", h() = " + i + ", value = " + value);

		// initial creation of buckets based on size
		if (hTable.size() == 0) {
			for (int j = 0; j < mod1; j++) {
				Bucket b = new Bucket(null);
				hTable.add(j, b);
			}
		}

		// inserting the key,value in to the hash table
		insert(i, key, value);
		tKeys++;
		// check for split
		loadFactor = tKeys / size();

		// execute split
		if (loadFactor > .75) {
			Bucket b = new Bucket(null);
			hTable.add(b);
			int reloc;
			// reshufle

			var nextBucket = hTable.get(split);
			// reshuffling Home + overflow bucket
			if (nextBucket != null && nextBucket.next != null) {
				while (nextBucket != null) {
					ofCounter++;
					for (int k = 0; k < nextBucket.nKeys; k++) {
						reloc = h2(nextBucket.key[k]);
						if (reloc != split && reloc <= hTable.size() - 1) {
							// insert the values into the tables
							insert(reloc, nextBucket.key[k], nextBucket.value[k]);
							// remove and squeze
							removeAndSqueeze(nextBucket, k);
							k--;
						}
					}
					nextBucket = nextBucket.next;
				}
			}
			// reshuffling home bucket
			else if (nextBucket != null) {
				// maintainig the list of high resolution bucket indexes
				for (int k = 0; k < nextBucket.nKeys; k++) {
					reloc = h2(nextBucket.key[k]);
					if (reloc != split && reloc <= hTable.size() - 1) {
						// insert the values into the tables
						insert(reloc, nextBucket.key[k], nextBucket.value[k]);
						// remove and squeze
						removeAndSqueeze(nextBucket, k);
						if (k != 0)
							k--;
					}
				}
			}
			split++;
		}
		if (split > mod1 - 1) {
			mod1 = mod1 * 2;
			mod2 = mod1 * 2;
			split = 0;
		}

		// T O B E I M P L E M E N T E D

		return null;
	} // put

	/********************************************************************************
	 * Return the size (SLOTS * number of home buckets) of the hash table.
	 * 
	 * @return the size of the hash table
	 */
	public int size() {
		return SLOTS * (mod1 + split);
	} // size

	/********************************************************************************
	 * Print the hash table.
	 */
	private void print() {
		out.println("Hash Table (Linear Hashing)");
		out.println("-------------------------------------------");
		for (int j = 0; j < hTable.size(); j++) {
			var nextBucket = hTable.get(j);
			out.println("Bucket " + j + ":");
			// printing Home + overflow bucket
			if (nextBucket != null && nextBucket.next != null) {
				while (nextBucket != null) {
					for (int k = 0; k < nextBucket.nKeys; k++) {
						out.println(
								"\tKey: " + nextBucket.key[k] + "\t\t | " + "\t\t | \t\tValue: " + nextBucket.value[k]);
					}
					out.println(
							"----------------------------------------------------------------------------------------");
					nextBucket = nextBucket.next;

				}

			}
			// printing home bucket
			else if (nextBucket != null) {
				for (int k = 0; k < nextBucket.nKeys; k++) {
					out.println("\tKey: " + nextBucket.key[k] + "\t\t | " + "\t\t | \t\tValue: " + nextBucket.value[k]);
				}
			}
		}

		out.println("-------------------------------------------");
	} // print

	/********************************************************************************
	 * Hash the key using the low resolution hash function.
	 * 
	 * @param key the key to hash
	 * @return the location of the bucket chain containing the key-value pair
	 */
	private int h(Object key) {
		return (key.hashCode() & 0x7fffffff) % mod1;
	} // h

	/********************************************************************************
	 * Hash the key using the high resolution hash function.
	 * 
	 * @param key the key to hash
	 * @return the location of the bucket chain containing the key-value pair
	 */
	private int h2(Object key) {
		return (key.hashCode() & 0x7fffffff) % mod2;
	} // h2

	private void insert(int i, K key, V value) {
		var IteratorBucket = hTable.get(i);
		if (IteratorBucket.nKeys < SLOTS) {
			IteratorBucket.key[IteratorBucket.nKeys] = key;
			IteratorBucket.value[IteratorBucket.nKeys] = value;
			IteratorBucket.nKeys++;
		}
		// inserting in overflow bucket
		else {
			// var IteratorBucket = hTable.get(i);
			while (IteratorBucket.next != null) {
				IteratorBucket = IteratorBucket.next;
			}
			if (IteratorBucket.nKeys == SLOTS) {
				Bucket nb = new Bucket(null);
				IteratorBucket.next = nb;
				nb.key[nb.nKeys] = key;
				nb.value[nb.nKeys] = value;
				nb.nKeys++;
			} else {
				IteratorBucket.key[IteratorBucket.nKeys] = key;
				IteratorBucket.value[IteratorBucket.nKeys] = value;
				IteratorBucket.nKeys++;
			}
		}
	}

	private void removeAndSqueeze(Bucket bucket, int pos) {
		// var bucket = hTable.get(i);
		// bucket with next pointer = null
		if (bucket.next == null) {
			if (pos != bucket.nKeys - 1) {
				bucket.key[pos] = bucket.key[bucket.nKeys - 1];
				bucket.value[pos] = bucket.value[bucket.nKeys - 1];
				bucket.value[bucket.nKeys - 1] = null;
				bucket.key[bucket.nKeys - 1] = null;
				bucket.nKeys--;
			} else {
				bucket.value[pos] = null;
				bucket.key[pos] = null;
				bucket.nKeys--;
			}

		}
		// bucket which has next pointer
		else {
			var removalBucket = bucket;
			while (bucket.next != null) {
				if (bucket.next.nKeys == 0) {
					bucket.next = null;
					break;
				}
				bucket = bucket.next;

			}

			removalBucket.key[pos] = bucket.key[bucket.nKeys - 1];
			removalBucket.value[pos] = bucket.value[bucket.nKeys - 1];
			bucket.value[bucket.nKeys - 1] = null;
			bucket.key[bucket.nKeys - 1] = null;
			bucket.nKeys--;
		}

	}

	/********************************************************************************
	 * The main method used for testing.
	 * 
	 * @param the command-line arguments (args [0] gives number of keys to insert)
	 */
	public static void main(String[] args) {
		
		var totalKeys = 10000;
		var RANDOMLY = true;

		LinHashMap<Integer, Integer> ht = new LinHashMap<>(Integer.class, Integer.class);
		if (args.length == 1)
			totalKeys = Integer.valueOf(args[0]);

		if (RANDOMLY) {
			var rng = new Random();
			for (int i = 1; i <= totalKeys; i += 2)
				ht.put(rng.nextInt(2 * totalKeys), i * i);
		} else {
			for (int i = 1; i <= totalKeys; i += 2)
				ht.put(i, i * i);
		} // if

		ht.print();
		System.out.println(ht.get(555));
		System.out.println(ht.entrySet());
		/*for (int i = 0; i <= totalKeys; i++) {
			out.println("key = " + i + " value = " + ht.get(i));
		} // for
		out.println("-------------------------------------------");*/
		out.println("Average number of buckets accessed = " + ht.count / (double) totalKeys);
	} // main

} // LinHashMap class
