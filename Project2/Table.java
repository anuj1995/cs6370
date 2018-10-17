
/****************************************************************************************
 * @file  Table.java
 *
 * @author   John Miller
 */

import java.io.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import static java.lang.Boolean.*;
import static java.lang.System.out;


/****************************************************************************************
 * This class implements relational database tables (including attribute names, domains
 * and a list of tuples.  Five basic relational algebra operators are provided: project,
 * select, union, minus and join.  The insert data manipulation operator is also provided.
 * Missing are update and delete data manipulation operators.
 */
public class Table
       implements Serializable
{
    /** Relative path for storage directory
     */
    private static final String DIR = "C:\\Users\\anujp\\Desktop\\test" + File.separator;

    /** Filename extension for database files
     */
    private static final String EXT = ".dbf";

    /** Counter for naming temporary tables.
     */
    private static int count = 0;

    /** Table name.
     */
    private final String name;

    /** Array of attribute names.
     */
    private final String [] attribute;

    /** Array of attribute domains: a domain may be
     *  integer types: Long, Integer, Short, Byte
     *  real types: Double, Float
     *  string types: Character, String
     */
    private final Class [] domain;

    /** Collection of tuples (data storage).
     */
    private final List <Comparable []> tuples;

    /** Primary key.
     */
    private final String [] key;

    /** Index into tuples (maps key to tuple number).
     */
    private final Map <KeyType, Comparable []> index;

    /** The supported map types.
     */
    private enum MapType { NO_MAP, TREE_MAP, LINHASH_MAP, BPTREE_MAP }

    /** The map type to be used for indices.  Change as needed.
     */
    private static final MapType mType = MapType.LINHASH_MAP;

    /************************************************************************************
     * Make a map (index) given the MapType.
     */
    private static Map <KeyType, Comparable []> makeMap ()
    {
        switch (mType) {
        case TREE_MAP:    return new TreeMap <> ();
        case LINHASH_MAP: return new LinHashMap <> (KeyType.class, Comparable [].class);
        case BPTREE_MAP:  return new BpTreeMap <> (KeyType.class, Comparable [].class);
        default:          return null;
        } // switch
    } // makeMap

    //-----------------------------------------------------------------------------------
    // Constructors
    //-----------------------------------------------------------------------------------

    /************************************************************************************
     * Construct an empty table from the meta-data specifications.
     *
     * @param _name       the name of the relation
     * @param _attribute  the string containing attributes names
     * @param _domain     the string containing attribute domains (data types)
     * @param _key        the primary key
     */
    public Table (String _name, String [] _attribute, Class [] _domain, String [] _key)
    {
        name      = _name;
        attribute = _attribute;
        domain    = _domain;
        key       = _key;
        tuples    = new ArrayList <> ();
        index     = makeMap ();

    } // primary constructor

    /************************************************************************************
     * Construct a table from the meta-data specifications and data in _tuples list.
     *
     * @param _name       the name of the relation
     * @param _attribute  the string containing attributes names
     * @param _domain     the string containing attribute domains (data types)
     * @param _key        the primary key
     * @param _tuples     the list of tuples containing the data
     */
    public Table (String _name, String [] _attribute, Class [] _domain, String [] _key,
                  List <Comparable []> _tuples)
    {
        name      = _name;
        attribute = _attribute;
        domain    = _domain;
        key       = _key;
        tuples    = _tuples;
        index     = makeMap ();
    } // constructor

    /************************************************************************************
     * Construct an empty table from the raw string specifications.
     *
     * @param _name       the name of the relation
     * @param attributes  the string containing attributes names
     * @param domains     the string containing attribute domains (data types)
     * @param _key        the primary key
     */
    public Table (String _name, String attributes, String domains, String _key)
    {
        this (_name, attributes.split (" "), findClass (domains.split (" ")), _key.split(" "));

        out.println ("DDL> create table " + name + " (" + attributes + ")");
    } // constructor

    //----------------------------------------------------------------------------------
    // Public Methods
    //----------------------------------------------------------------------------------

    /************************************************************************************
     * Project the tuples onto a lower dimension by keeping only the given attributes.
     * Check whether the original key is included in the projection.
     *
     * #usage movie.project ("title year studioNo")
     *
     * @param attributes  the attributes to project onto
     * @return  a table of projected tuples
     */
    public Table project (String attributes)
    {
        out.println ("RA> " + name + ".project (" + attributes + ")");
        var attrs     = attributes.split (" ");
        var colDomain = extractDom (match (attrs), domain);
        var newKey    = (Arrays.asList (attrs).containsAll (Arrays.asList (key))) ? key : attrs;

        List <Comparable []> rows = new ArrayList <> ();

        //  T O   B E   I M P L E M E N T E D
        for(Comparable[] val : tuples){
                rows.add(extract(val, attrs));
        }
        if(rows.size() <=0 )
        {
        	return null;
        }
        return new Table (name + count++, attrs, colDomain, newKey, rows);
    } // project

    /************************************************************************************
     * Select the tuples satisfying the given predicate (Boolean function).
     *
     * #usage movie.select (t -- t[movie.col("year")].equals (1977))
     *
     * @param predicate  the check condition for tuples
     * @return  a table with tuples satisfying the predicate
     */
    public Table select (Predicate <Comparable []> predicate)
    {
        out.println ("RA> " + name + ".select (" + predicate + ")");

        return new Table (name + count++, attribute, domain, key,
                   tuples.stream ().filter (t -> predicate.test (t))
                                   .collect (Collectors.toList ()));
    } // select

    /************************************************************************************
     * Select the tuples satisfying the given key predicate (key = value).  Use an index
     * (Map) to retrieve the tuple with the given key value.
     *
     * @param keyVal  the given key value
     * @return  a table with the tuple satisfying the key predicate
     */
    public Table select (KeyType keyVal)
	{
		out.println("RA> " + name + ".select (" + keyVal + ")");

		List<Comparable[]> rows = new ArrayList<>();
		// get the indexed tuple from the index Map
		Comparable[] tup = index.get(keyVal);
		// Check if it returns null
		if (tup != null) {
			rows.add(tup);
		}

		return new Table(name + count++, attribute, domain, key, rows);
	} // select

    /************************************************************************************
     * Union this table and table2.  Check that the two tables are compatible.
     *
     * #usage movie.union (show)
     *
     * @param table2  the rhs table in the union operation
     * @return  a table representing the union
     */
    public Table union (Table table2)
    {
        out.println ("RA> " + name + ".union (" + table2.name + ")");
        
        if (! compatible (table2)) return null;
        
        List <Comparable []> rows = new ArrayList <> ();
        
        for(Comparable[] val : tuples)
        {
           rows.add(val);
        }
        for(Comparable[] val1 : table2.tuples)
        {
        	if(!rows.contains(val1))
        	{
        		rows.add(val1);
        	}
        	
        }	

        return new Table (name + count++, attribute, domain, attribute, rows);
    } // union

    /************************************************************************************
     * Take the difference of this table and table2.  Check that the two tables are
     * compatible.
     *
     * #usage movie.minus (show)
     *
     * @param table2  The rhs table in the minus operation
     * @return  a table representing the difference
     */
    public Table minus (Table table2)
    {
        out.println ("RA> " + name + ".minus (" + table2.name + ")");
        if (! compatible (table2)) return null;

        List <Comparable []> rows = new ArrayList <> ();

        //  T O   B E   I M P L E M E N T E D
        for(Comparable[] val : tuples){
            if (!table2.tuples.contains(val)){
            	rows.add(val);
            }
        }

        return new Table (name + count++, attribute, domain, key, rows);
    } // minus

    /************************************************************************************
     * Join this table and table2 by performing an "equi-join".  Tuples from both tables
     * are compared requiring attributes1 to equal attributes2.  Disambiguate attribute
     * names by append "2" to the end of any duplicate attribute name.  Implement using
     * a Nested Loop Join algorithm.
     *
     * #usage movie.join ("studioNo", "name", studio)
     *
     * @param attributes1  the attributes of this table to be compared (Foreign Key)
     * @param attributes2  the attributes of table2 to be compared (Primary Key)
     * @param table2      the rhs table in the join operation
     * @return Table a table with tuples satisfying the equality predicate
     */
    public Table join (String attributes1, String attributes2, Table table2)
    {
        out.println ("RA> " + name + ".join (" + attributes1 + ", " + attributes2 + ", "
                                               + table2.name + ")");

        var t_attrs = attributes1.split (" "); //String []
        var u_attrs = attributes2.split (" ");
        var rows    = new ArrayList <Comparable []> ();


        //has all tables combined - works
	    for(Comparable [] val1: this.tuples)
	    {
	    	for(Comparable [] val2 : table2.tuples)
	    	{
	    		for(Comparable c1 : val1)
	    		{
	    			for(Comparable c2 : val2)
	    			{
	    				if(c1.equals(c2))
	    				{
	    					Comparable [] both = Stream.concat(Arrays.stream(val1), Arrays.stream(val2))
	    							.toArray(Comparable[]::new);
	    							 rows.add(both);
	    				}//if
	    				break;	
	    			}//for
	    		}//for
	        }//for
	    }//for
	    
	    if(rows.size() <= 0)
	    {
	    	return null;
	    }
	    	
	    
        return new Table (name + count++, /*ArrayUtil.concat (t_attrs, u_attrs)*/ArrayUtil.concat (attribute, table2.attribute),
                                          ArrayUtil.concat (domain, table2.domain), key, rows);
    } // join

    /************************************************************************************
     * Join this table and table2 by performing an "equi-join".  Same as above, but implemented
     * using an Index Join algorithm.
     *
     * @param attributes1  the attributes of this table to be compared (Foreign Key)
     * @param attributes2  the attributes of table2 to be compared (Primary Key)
     * @param table2      the rhs table in the join operation
     * @return  a table with tuples satisfying the equality predicate
     */
    public Table i_join (String attributes1, String attributes2, Table table2)
    {
    	var t_attrs = attributes1.split (" "); //String []
        var u_attrs = attributes2.split (" ");
    	var rows    = new ArrayList <Comparable []> ();
    	
    	for(Map.Entry<KeyType, Comparable[]> val1 : index.entrySet())
    	{
    		for(Map.Entry<KeyType, Comparable[]> val2 : table2.index.entrySet())
    		{
    			Comparable[] atr1 = extract(val1.getValue(),t_attrs);
    			Comparable[] atr2 = table2.extract(val2.getValue(),u_attrs);
    			if(Arrays.equals(atr1,atr2))
    			{
				Comparable [] both = Stream.concat(Arrays.stream(val1.getValue()), Arrays.stream(val2.getValue()))
						.toArray(Comparable[]::new);
						 rows.add(both);
    			}
    		}
    	}
    	return new Table (name + count++, /*ArrayUtil.concat (t_attrs, u_attrs)*/ArrayUtil.concat (attribute, table2.attribute),
                ArrayUtil.concat (domain, table2.domain), key, rows);
    } // i_join

    /************************************************************************************
     * Join this table and table2 by performing an "equi-join".  Same as above, but implemented
     * using a Hash Join algorithm.
     *
     * @param attributes1  the attributes of this table to be compared (Foreign Key)
     * @param attributes2  the attributes of table2 to be compared (Primary Key)
     * @param table2      the rhs table in the join operation
     * @return  a table with tuples satisfying the equality predicate
     */
    public Table h_join (String attributes1, String attributes2, Table table2)
    {
        return null;
    } // h_join

    /************************************************************************************
     * Join this table and table2 by performing an "natural join".  Tuples from both tables
     * are compared requiring common attributes to be equal.  The duplicate column is also
     * eliminated.
     *
     * #usage movieStar.join (starsIn)
     *
     * @param table2  the rhs table in the join operation
     * @return  a table with tuples satisfying the equality predicate
     */
    public Table join (Table table2)
    {
        out.println ("RA> " + name + ".join (" + table2.name + ")");

        var rows = new ArrayList <Comparable []> ();

        //  T O   B E   I M P L E M E N T E D
        //checking for common attributes
        //concat arrays and then check for duplicates - this works!
        String[] concatAttributes =  ArrayUtil.concat(this.attribute, table2.attribute);
        String [] distinct_attributes = Arrays.stream(concatAttributes).distinct().toArray(String[]::new);
           

        //still working to fix this
        //Comparable [] both_common = new Comparable [this.tuples.size() + table2.tuples.size()]; //array of tuples the tables have in common
        //Comparable [] both_uncommon = new Comparable [this.tuples.size() + table2.tuples.size()]; //array of tuples the table does not share
        Comparable [] both;
        String[] s1 = attribute;
        String[] s2 = table2.attribute;
        String[] common =  getCommonAttributes(table2);
        for(Comparable [] val1: this.tuples)
        {
        	for(Comparable [] val2 : table2.tuples)
        	{
        		for(Comparable c1 : val1)
        		{
        			for(Comparable c2 : val2)
        			{
        				if(c1.equals(c2))
        				{
        					Arrays.sort(s1);
        					Arrays.sort(s2);
    						if(Arrays.equals(s1,s2))  //if all attributes were shared attributes
    						{	
    							both = Arrays.stream(val1).distinct().toArray(Comparable[]::new);
    							rows.add(both);	
    						}//if											

    						else
    						{
								both = Stream.concat(Arrays.stream(val1), Arrays.stream(val2)).distinct().toArray(Comparable[]::new);
								if(extract (val1, common) == extract (val2, common))
    							{
									rows.add(both);
    							}
    						}//else
           				}//if
        				break;
        			}//for
        		}//for
            }//for
         }//for
        
        //case for Cartesian product in natural join
        if(common.length == 0)
        {
        	for(Comparable [] val1: this.tuples)
            {
            	for(Comparable [] val2 : table2.tuples)
            	{
            		rows.add(ArrayUtil.concat(val1, val2));
            	}
            }
        }
        if(rows.size() <= 0 && common.length == 0)
	    {
        	return null;
	    }
        
        return new Table (name + count++, /*ArrayUtil.concat (attribute, table2.attribute)*/distinct_attributes,
                                          ArrayUtil.concat (domain, table2.domain), key, rows);
    } // join

    
    /************************************************************************************
     * Return the Common Attributes of the two tables.
     *
     * #usage String[] common =  getCommonAttributes(table2);
     *
     * @param table2  the given attribute name
     * @return  Common Attributes Array
     */
    
    private String[] getCommonAttributes(Table table2) {
    	List<String> commonAttributes = new ArrayList<String>();
    	var l_attrs = attribute;
    	var r_attrs = table2.attribute;
    	
    	for(var table1Attribute : l_attrs) {
    		
    		if(Arrays.asList(r_attrs).contains(table1Attribute)) {
    		commonAttributes.add(table1Attribute);
    		}
    	}
    	
    	return commonAttributes.toArray(new String[0]);
    }
    
    
    /************************************************************************************
     * Return the column position for the given attribute name.
     *
     * @param attr  the given attribute name
     * @return  a column position
     */
    public int col (String attr)
    {
        for (int i = 0; i < attribute.length; i++) {
           if (attr.equals (attribute [i])) return i;
        } // for

        return -1;  // not found
    } // col

    /************************************************************************************
     * Insert a tuple to the table.
     *
     * #usage movie.insert ("'Star_Wars'", 1977, 124, "T", "Fox", 12345)
     *
     * @param tup  the array of attribute values forming the tuple
     * @return  whether insertion was successful
     */
    public boolean insert (Comparable [] tup)
    {
        out.println ("DML> insert into " + name + " values ( " + Arrays.toString (tup) + " )");

        if (typeCheck (tup)) {
            tuples.add (tup);
            var keyVal = new Comparable [key.length];
            var cols   = match (key);
            for (int j = 0; j < keyVal.length; j++) keyVal [j] = tup [cols [j]];
            if (mType != MapType.NO_MAP) index.put (new KeyType (keyVal), tup);
            return true;
        } else {
            return false;
        } // if
    } // insert

    /************************************************************************************
     * Get the name of the table.
     *
     * @return  the table's name
     */
    public String getName ()
    {
        return name;
    } // getName

    /************************************************************************************
     * Print this table.
     */
    public void print ()
    {
        out.println ("\n Table " + name);
        out.print ("|-");
        for (int i = 0; i < attribute.length; i++) out.print ("---------------");
        out.println ("-|");
        out.print ("| ");
        for (String a : attribute) out.printf ("%15s", a);
        out.println (" |");
        out.print ("|-");
        for (int i = 0; i < attribute.length; i++) out.print ("---------------");
        out.println ("-|");
        for (Comparable [] tup : tuples) {
            out.print ("| ");
            for (Comparable attr : tup) out.printf ("%15s", attr);
            out.println (" |");
        } // for
        out.print ("|-");
        for (int i = 0; i < attribute.length; i++) out.print ("---------------");
        out.println ("-|");
    } // print

    /************************************************************************************
     * Print this table's index (Map).
     */
    public void printIndex ()
    {
        out.println ("\n Index for " + name);
        out.println ("-------------------");
        if (mType != MapType.NO_MAP) {
            for (Map.Entry <KeyType, Comparable []> e : index.entrySet ()) {
                out.println (e.getKey () + " -> " + Arrays.toString (e.getValue ()));
            } // for
        } // if
        out.println ("-------------------");
    } // printIndex

    /************************************************************************************
     * Load the table with the given name into memory.
     *
     * @param name  the name of the table to load
     * @return Table 
     */
    public static Table load (String name)
    {
        Table tab = null;
        try {
            ObjectInputStream ois = new ObjectInputStream (new FileInputStream (DIR + name + EXT));
            tab = (Table) ois.readObject ();
            ois.close ();
        } catch (IOException ex) {
            out.println ("load: IO Exception");
            ex.printStackTrace ();
        } catch (ClassNotFoundException ex) {
            out.println ("load: Class Not Found Exception");
            ex.printStackTrace ();
        } // try
        return tab;
    } // load

    /************************************************************************************
     * Save this table in a file.
     */
    public void save ()
    {
        try {
            var oos = new ObjectOutputStream (new FileOutputStream (DIR + name + EXT));
            oos.writeObject (this);
            oos.close ();
        } catch (IOException ex) {
            out.println ("save: IO Exception");
            ex.printStackTrace ();
        } // try
    } // save

    //----------------------------------------------------------------------------------
    // Private Methods
    //----------------------------------------------------------------------------------

    /************************************************************************************
     * Determine whether the two tables (this and table2) are compatible, i.e., have
     * the same number of attributes each with the same corresponding domain.
     *
     * @param table2  the rhs table
     * @return  whether the two tables are compatible
     */
    private boolean compatible (Table table2)
    {
        if (domain.length != table2.domain.length) {
            out.println ("compatible ERROR: table have different arity");
            return false;
        } // if
        for (int j = 0; j < domain.length; j++) {
            if (domain [j] != table2.domain [j]) {
                out.println ("compatible ERROR: tables disagree on domain " + j);
                return false;
            } // if
        } // for
        return true;
    } // compatible

    /************************************************************************************
     * Match the column and attribute names to determine the domains.
     *
     * @param column  the array of column names
     * @return  an array of column index positions
     */
    private int [] match (String [] column)
    {
        int [] colPos = new int [column.length];

        for (int j = 0; j < column.length; j++) {
            var matched = false;
            for (int k = 0; k < attribute.length; k++) {
                if (column [j].equals (attribute [k])) {
                    matched = true;
                    colPos [j] = k;
                } // for
            } // for
            if ( ! matched) {
                out.println ("match: domain not found for " + column [j]);
            } // if
        } // for

        return colPos;
    } // match

    /************************************************************************************
     * Extract the attributes specified by the column array from tuple t.
     *
     * @param t       the tuple to extract from
     * @param column  the array of column names
     * @return  a smaller tuple extracted from tuple t
     */
    private Comparable [] extract (Comparable [] t, String [] column)
    {
        var tup    = new Comparable [column.length];
        var colPos = match (column);
        for (int j = 0; j < column.length; j++) tup [j] = t [colPos [j]];
        return tup;
    } // extract

    /************************************************************************************
     * Check the size of the tuple (number of elements in list) as well as the type of
     * each value to ensure it is from the right domain.
     *
     * @param t  the tuple as a list of attribute values
     * @return  whether the tuple has the right size and values that comply
     *          with the given domains
     */
    private boolean typeCheck (Comparable [] t)
    {
        //  T O   B E   I M P L E M E N T E D

        return true;
    } // typeCheck

    /************************************************************************************
     * Find the classes in the "java.lang" package with given names.
     *
     * @param className  the array of class name (e.g., {"Integer", "String"})
     * @return  an array of Java classes
     */
    private static Class [] findClass (String [] className)
    {
        var classArray = new Class [className.length];

        for (int i = 0; i < className.length; i++) {
            try {
                classArray [i] = Class.forName ("java.lang." + className [i]);
            } catch (ClassNotFoundException ex) {
                out.println ("findClass: " + ex);
            } // try
        } // for

        return classArray;
    } // findClass

    /************************************************************************************
     * Extract the corresponding domains.
     *
     * @param colPos the column positions to extract.
     * @param group  where to extract from
     * @return  the extracted domains
     */
    private Class [] extractDom (int [] colPos, Class [] group)
    {
        var obj = new Class [colPos.length];

        for (int j = 0; j < colPos.length; j++) {
            obj [j] = group [colPos [j]];
        } // for

        return obj;
    } // extractDom

} // Table class
