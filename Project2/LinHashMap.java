
/************************************************************************************
 * @file LinHashMap.java
 *
 * @author  John Miller
 */

import java.io.*;
import java.lang.reflect.Array;
import static java.lang.System.out;
import java.util.*;

/************************************************************************************
 * This class provides hash maps that use the Linear Hashing algorithm.
 * A hash table is created that is an array of buckets.
 */
public class LinHashMap <K, V>
       extends AbstractMap <K, V>
       implements Serializable, Cloneable, Map <K, V>
{
    /** The number of slots (for key-value pairs) per bucket.
     */
    private static final int SLOTS = 4;

/************************************************************************************
 * @file LinHashMap.java
 *
 * @author  John Miller
 */

import java.io.*;
import java.lang.reflect.Array;
import static java.lang.System.out;
import java.util.*;

/************************************************************************************
 * This class provides hash maps that use the Linear Hashing algorithm.
 * A hash table is created that is an array of buckets.
 */
public class LinHashMap <K, V>
       extends AbstractMap <K, V>
       implements Serializable, Cloneable, Map <K, V>
{
    /** The number of slots (for key-value pairs) per bucket.
     */
    private static final int SLOTS = 4;

    /** The class for type K.
     */
    private final Class <K> classK;

    /** The class for type V.
     */
    private final Class <V> classV;

    /********************************************************************************
     * This inner class defines buckets that are stored in the hash table.
     */
    private class Bucket
    {
        int    nKeys;
        K []   key;
        V []   value;
        Bucket next;

        @SuppressWarnings("unchecked")
        Bucket (Bucket n)
        {
            nKeys = 0;
            key   = (K []) Array.newInstance (classK, SLOTS);
            value = (V []) Array.newInstance (classV, SLOTS);
            next  = n;
        } // constructor
        
        private void insert(K key, V value)
        {
        	this.key[this.nKeys] = key;
        	this.value[this.nKeys] = value;
        	this.nKeys++;
        	keyCount++;
        }
    } // Bucket inner class

    /** The list of buckets making up the hash table.
     */
    private final List <Bucket> hTable;

    /** The modulus for low resolution hashing
     */
    private int mod1;

    /** The modulus for high resolution hashing
     */
    private int mod2;

    /** Counter for the number buckets accessed (for performance testing).
     */
    private int count = 0;

    /** The index of the next bucket to split.
     */
    private int split = 0;
    
    /** Counter for the total number of keys in the hashtable
     */
    private int keyCount = 0;

    /********************************************************************************
     * Construct a hash table that uses Linear Hashing.
     * @param classK    the class for keys (K)
     * @param classV    the class for keys (V)
     * @param initSize  the initial number of home buckets (a power of 2, e.g., 4)
     */
    public LinHashMap (Class <K> _classK, Class <V> _classV)    // , int initSize)
    {
        classK = _classK;
        classV = _classV;
        hTable = new ArrayList <> ();
        mod1   = 4;                        // initSize;
        mod2   = 2 * mod1;
        
        
        //Initialize starting hashmap
        for(int i = 0; i < mod1; i++)
        {
        	hTable.add(new Bucket(null));
        }
    } // constructor

    /********************************************************************************
     * Return a set containing all the entries as pairs of keys and values.
     * @return  the set view of the map
     */
    public Set <Map.Entry <K, V>> entrySet ()
    {
        var enSet = new HashSet <Map.Entry <K, V>> ();
        
        //Iterate through hash table
        for(int i = 0; i < hTable.size(); i++)
        {
        	Bucket selectedBucket = hTable.get(i);
        	//Once in a bucket, get all v/k tuples to be entered into HashSet
        	for(int j = 0; j < selectedBucket.nKeys; j++) 
        	{
        		K key = selectedBucket.key[j];
        		V value = selectedBucket.value[j];
        		
        		Map.Entry<K, V> entry = new AbstractMap.SimpleEntry<K, V>(key, value);
        		enSet.add(entry);
        	}
        }

        return enSet;
    } // entrySet

    /********************************************************************************
     * Given the key, look up the value in the hash table.
     * @param key  the key used for look up
     * @return  the value associated with the key
     */
    public V get (Object key)
    {
        var location = h (key);
        
        //Hash location if necessary
        if(location < split)
        	location = h2(key);
        
        Bucket selectedBucket = hTable.get(location);
        
        //Check if bucket is empty
        if(selectedBucket.nKeys == 0)
        	return null;
        
        while(selectedBucket != null) 
        {
            //Iterate through bucket looking for matching key
            for(int i = 0; i < selectedBucket.nKeys; i++)
            {
            	if(key.equals(selectedBucket.key[i])) 
            		return selectedBucket.value[i];	
            }
            selectedBucket = selectedBucket.next;
        }

        return null;
    } // get

    /********************************************************************************
     * Put the key-value pair in the hash table.
     * @param key    the key to insert
     * @param value  the value to insert
     * @return  null (not the previous value)
     */
    public V put (K key, V value)
    {
        var location = h (key);
        out.println ("LinearHashMap.put: key = " + key + ", h() = " + location + ", value = " + value);
        
        //Return null if K or V are empty
        if (key.equals(null) || value.equals(null))
        	return null;
        
        //Hash location if necessary
        if(location < split)
        	location = h2(key);
        
        Bucket selectedBucket = hTable.get(location);
        
        //Compare size of bucket to slots to see if we can just insert directly
        if(selectedBucket.nKeys < SLOTS)
        {
        	//Insert key and value into last indexes
        	selectedBucket.insert(key, value);
        	
        	return value;
        }
        
        ////Need to split buckets///////
        
        //Create new bucket
        selectedBucket = new Bucket(null);
        selectedBucket.insert(key, value);
        hTable.add(selectedBucket);
        
        //Calculate load factor
        double loadFactor = ((double)keyCount)/(SLOTS * mod1);
        if(loadFactor >= 0.75) 
        {
        	//Create new bucket
        	Bucket newBucket = new Bucket(null);
        	
        	//Create replacement bucket for split
        	Bucket replacementBucket = new Bucket(null);
        	
        	//Get bucket that is to be split
        	selectedBucket = hTable.get(split);
        	
        	for(int i = 0; i < selectedBucket.nKeys; i++) 
        	{
        		int hash = h2(selectedBucket.key[i]);
        		if(hash == split)
        		{
        			if(replacementBucket.equals(null)) 
        			{
            			replacementBucket.next = new Bucket(null);
                        replacementBucket = replacementBucket.next;
        			}
            		replacementBucket.insert(selectedBucket.key[i], selectedBucket.value[i]);
        		}
        		else
        		{
        			if(newBucket.equals(null))
        			{
        				newBucket.next = new Bucket(null);
        				newBucket = newBucket.next;
        			}
            		newBucket.insert(selectedBucket.key[i], selectedBucket.value[i]);
        		}
        	}//end for loop
        	
            if(split ==  mod1 - 1 )
            {
                split=0;
                mod1= mod1*2;
                mod2= mod1*2;
            }
            else
            	split++;
        }
        
        return null;
    } // put

    /********************************************************************************
     * Return the size (SLOTS * number of home buckets) of the hash table. 
     * @return  the size of the hash table
     */
    public int size ()
    {
        return SLOTS * (mod1 + split);
    } // size

    /********************************************************************************
     * Print the hash table.
     */
    private void print ()
    {
        out.println ("Hash Table (Linear Hashing)");
        out.println ("-------------------------------------------");

        for( int i = 0 ; i < hTable.size() ; i++ )
        {
           if( hTable.get(i).nKeys > 0 )
                out.println("Bucket " + i + ":");
                
            //Print all buckets
            for( int j = 0 ; j < hTable.get(i).nKeys ; j++ )
            {
                out.println( "\tKey: " + hTable.get(i).key[j] +  " | Hash: " + h( hTable.get(i).key[j] )  + " | Value: " + hTable.get(i).value[j] );
            }
        }

        out.println ("-------------------------------------------");
    } // print

    /********************************************************************************
     * Hash the key using the low resolution hash function.
     * @param key  the key to hash
     * @return  the location of the bucket chain containing the key-value pair
     */
    private int h (Object key)
    {
        return key.hashCode () % mod1;
    } // h

    /********************************************************************************
     * Hash the key using the high resolution hash function.
     * @param key  the key to hash
     * @return  the location of the bucket chain containing the key-value pair
     */
    private int h2 (Object key)
    {
        return key.hashCode () % mod2;
    } // h2

    /********************************************************************************
     * The main method used for testing.
     * @param  the command-line arguments (args [0] gives number of keys to insert)
     */
    public static void main (String [] args)
    {

        var totalKeys = 30;
        var RANDOMLY  = false;

        LinHashMap <Integer, Integer> ht = new LinHashMap <> (Integer.class, Integer.class);
        if (args.length == 1) totalKeys = Integer.valueOf (args [0]);

        if (RANDOMLY) {
            var rng = new Random ();
            for (int i = 1; i <= totalKeys; i += 2) ht.put (rng.nextInt (2 * totalKeys), i * i);
        } else {
            for (int i = 1; i <= totalKeys; i += 2) ht.put (i, i * i);
        } // if

        ht.print ();
        for (int i = 0; i <= totalKeys; i++) {
            out.println ("key = " + i + " value = " + ht.get (i));
        } // for
        out.println ("-------------------------------------------");
        out.println ("Average number of buckets accessed = " + ht.count / (double) totalKeys);
        out.println("Key count: " + ht.keyCount);
    } // main

} // LinHashMap class

    /********************************************************************************
     * Construct a hash table that uses Linear Hashing.
     * @param classK    the class for keys (K)
     * @param classV    the class for keys (V)
     * @param initSize  the initial number of home buckets (a power of 2, e.g., 4)
     */
    public LinHashMap (Class <K> _classK, Class <V> _classV)    // , int initSize)
    {
        classK = _classK;
        classV = _classV;
        hTable = new ArrayList <> ();
        mod1   = 4;                        // initSize;
        mod2   = 2 * mod1;
        
        int initSize = 4;
        
        //Initialize starting hashmap
        for(int i = 0; i < initSize; i++)
        {
        	hTable.add(new Bucket(null));
        }
    } // constructor

    /********************************************************************************
     * Return a set containing all the entries as pairs of keys and values.
     * @return  the set view of the map
     */
    public Set <Map.Entry <K, V>> entrySet ()
    {
        var enSet = new HashSet <Map.Entry <K, V>> ();
        
        //Iterate through hash table
        for(int i = 0; i < hTable.size(); i++)
        {
        	Bucket selectedBucket = hTable.get(i);
        	//Once in a bucket, get all v/k tuples to be entered into HashSet
        	for(int j = 0; j < selectedBucket.nKeys; j++) 
        	{
        		K key = selectedBucket.key[j];
        		V value = selectedBucket.value[j];
        		
        		Map.Entry<K, V> entry = new AbstractMap.SimpleEntry<K, V>(key, value);
        		enSet.add(entry);
        	}
        }

        return enSet;
    } // entrySet

    /********************************************************************************
     * Given the key, look up the value in the hash table.
     * @param key  the key used for look up
     * @return  the value associated with the key
     */
    public V get (Object key)
    {
        var location = h (key);
        
        //Hash location if necessary
        if(location < split)
        	location = h2(key);
        
        Bucket selectedBucket = hTable.get(location);
        
        //Check if bucket is empty
        if(selectedBucket.nKeys == 0)
        	return null;
        
        while(selectedBucket != null) 
        {
            //Iterate through bucket looking for matching key
            for(int i = 0; i < selectedBucket.nKeys; i++)
            {
            	if(key.equals(selectedBucket.key[i])) 
            		return selectedBucket.value[i];	
            }
            selectedBucket = selectedBucket.next;
        }

        return null;
    } // get

    /********************************************************************************
     * Put the key-value pair in the hash table.
     * @param key    the key to insert
     * @param value  the value to insert
     * @return  null (not the previous value)
     */
    public V put (K key, V value)
    {
        var location = h (key);
        out.println ("LinearHashMap.put: key = " + key + ", h() = " + location + ", value = " + value);
        
        //Return null if K or V are empty
        if (key.equals(null) || value.equals(null))
        	return null;
        
        //Hash location if necessary
        if(location < split)
        	location = h2(key);
        
        Bucket selectedBucket = hTable.get(location);
        
        //Compare size of bucket to slots to see if we can just insert directly
        if(selectedBucket.nKeys < SLOTS)
        {
        	//Insert key and value into last indexes
        	selectedBucket.insert(key, value);
        	
        	return value;
        }
        
        ////Need to split buckets///////
        
        //Create new bucket
        selectedBucket = new Bucket(null);
        selectedBucket.insert(key, value);
        hTable.add(selectedBucket);
        
        //Calculate load factor
        double loadFactor = ((double)keyCount)/(SLOTS * mod1);
        if(loadFactor >= 0.75) 
        {
        	//Create new bucket
        	Bucket newBucket = new Bucket(null);
        	
        	//Create replacement bucket for split
        	Bucket replacementBucket = new Bucket(null);
        	
        	//Get bucket that is to be split
        	selectedBucket = hTable.get(split);
        	
        	for(int i = 0; i < selectedBucket.nKeys; i++) 
        	{
        		int hash = h2(selectedBucket.key[i]);
        		if(hash == split)
        		{
        			if(replacementBucket.equals(null)) 
        			{
            			replacementBucket.next = new Bucket(null);
                        replacementBucket = replacementBucket.next;
        			}
            		replacementBucket.insert(selectedBucket.key[i], selectedBucket.value[i]);
        		}
        		else
        		{
        			if(newBucket.equals(null))
        			{
        				newBucket.next = new Bucket(null);
        				newBucket = newBucket.next;
        			}
            		newBucket.insert(selectedBucket.key[i], selectedBucket.value[i]);
        		}
        	}//end for loop
        	
            if(split ==  mod1 - 1 )
            {
                split=0;
                mod1= mod1*2;
                mod2= mod1*2;
            }
            else
            	split++;
        }
        
        return null;
    } // put

    /********************************************************************************
     * Return the size (SLOTS * number of home buckets) of the hash table. 
     * @return  the size of the hash table
     */
    public int size ()
    {
        return SLOTS * (mod1 + split);
    } // size

    /********************************************************************************
     * Print the hash table.
     */
    private void print ()
    {
        out.println ("Hash Table (Linear Hashing)");
        out.println ("-------------------------------------------");

        for( int i = 0 ; i < hTable.size() ; i++ )
        {
           if( hTable.get(i).nKeys > 0 )
                System.out.println("Bucket " + i + ":");
                
            //Print all buckets
            for( int j = 0 ; j < hTable.get(i).nKeys ; j++ )
            {
                out.println( "\tKey: " + hTable.get(i).key[j] +  " | Hash: " + h( hTable.get(i).key[j] )  + " | Value: " + hTable.get(i).value[j] );
            }
        }

        out.println ("-------------------------------------------");
    } // print

    /********************************************************************************
     * Hash the key using the low resolution hash function.
     * @param key  the key to hash
     * @return  the location of the bucket chain containing the key-value pair
     */
    private int h (Object key)
    {
        return key.hashCode () % mod1;
    } // h

    /********************************************************************************
     * Hash the key using the high resolution hash function.
     * @param key  the key to hash
     * @return  the location of the bucket chain containing the key-value pair
     */
    private int h2 (Object key)
    {
        return key.hashCode () % mod2;
    } // h2

    /********************************************************************************
     * The main method used for testing.
     * @param  the command-line arguments (args [0] gives number of keys to insert)
     */
    public static void main (String [] args)
    {

        var totalKeys = 30;
        var RANDOMLY  = false;

        LinHashMap <Integer, Integer> ht = new LinHashMap <> (Integer.class, Integer.class);
        if (args.length == 1) totalKeys = Integer.valueOf (args [0]);

        if (RANDOMLY) {
            var rng = new Random ();
            for (int i = 1; i <= totalKeys; i += 2) ht.put (rng.nextInt (2 * totalKeys), i * i);
        } else {
            for (int i = 1; i <= totalKeys; i += 2) ht.put (i, i * i);
        } // if

        ht.print ();
        for (int i = 0; i <= totalKeys; i++) {
            out.println ("key = " + i + " value = " + ht.get (i));
        } // for
        out.println ("-------------------------------------------");
        out.println ("Average number of buckets accessed = " + ht.count / (double) totalKeys);
        out.println("Key count: " + ht.keyCount);
    } // main

} // LinHashMap class
