import java.io.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import static java.lang.Boolean.*;
import static java.lang.System.out;


public class UnitTest {

  public static void main(String [] args){
    out.println ();

    var movie = new Table ("movie", "title year length genre studioName producerNo",
                                    "String Integer Integer String String Integer", "title year");

    var cinema = new Table ("cinema", "title year length genre studioName producerNo",
                                      "String Integer Integer String String Integer", "title year");

    var movieStar = new Table ("movieStar", "name address gender birthdate",
                                            "String String Character String", "name");

    var starsIn = new Table ("starsIn", "movieTitle movieYear starName",
                                        "String Integer String", "movieTitle movieYear starName");

    var movieExec = new Table ("movieExec", "certNo name address fee",
                                            "Integer String String Float", "certNo");

    var studio = new Table ("studio", "name address presNo",
                                      "String String Integer", "name");

    var film0 = new Comparable [] { "Star_Wars", 1977, 124, "sciFi", "Fox", 12345 };
    var film1 = new Comparable [] { "Star_Wars_2", 1980, 124, "sciFi", "Fox", 12345 };
    var film2 = new Comparable [] { "Rocky", 1985, 200, "action", "Universal", 12125 };
    var film3 = new Comparable [] { "Rambo", 1978, 100, "action", "Universal", 32355 };

    out.println ();
    movie.insert (film0);
    movie.insert (film1);
    movie.insert (film2);
    movie.insert (film3);
    movie.print ();

    var film4 = new Comparable [] { "Galaxy_Quest", 1999, 104, "comedy", "DreamWorks", 67890 };
    out.println ();
    cinema.insert (film2);
    cinema.insert (film3);
    cinema.insert (film4);
    cinema.print ();

    var star0 = new Comparable [] { "Carrie_Fisher", "Hollywood", 'F', "9/9/99" };
    var star1 = new Comparable [] { "Mark_Hamill", "Brentwood", 'M', "8/8/88" };
    var star2 = new Comparable [] { "Harrison_Ford", "Beverly_Hills", 'M', "7/7/77" };
    out.println ();
    movieStar.insert (star0);
    movieStar.insert (star1);
    movieStar.insert (star2);
    movieStar.print ();

    var cast0 = new Comparable [] { "Star_Wars", 1977, "Carrie_Fisher" };
    out.println ();
    starsIn.insert (cast0);
    starsIn.print ();

    var exec0 = new Comparable [] { 9999, "S_Spielberg", "Hollywood", 10000.00 };
    out.println ();
    movieExec.insert (exec0);
    movieExec.print ();

    var studio0 = new Comparable [] { "Fox", "Los_Angeles", 7777 };
    var studio1 = new Comparable [] { "Universal", "Universal_City", 8888 };
    var studio2 = new Comparable [] { "DreamWorks", "Universal_City", 9999 };
    out.println ();
    studio.insert (studio0);
    studio.insert (studio1);
    studio.insert (studio2);
    studio.print ();
    
    
    //test one select  - 
    System.out.println("___________________");
    System.out.println("SELECT TEST ONE START");
    var t_select =  movieStar.select (new KeyType ("Harrison_Ford"));
    movieStar.print ();
    t_select.print();
    System.out.println();
    System.out.println("SELECT TEST ONE FINISHED");
    System.out.println("___________________");
    System.out.println();
    
    //test two select  - 
    // table with two keys
    System.out.println("___________________");
    System.out.println("SELECT TEST TWO SATRT");
    var t_select1 =  movie.select (new KeyType ("Star_Wars",1977));
    movie.print ();
    t_select1.print();
    System.out.println();
    System.out.println("SELECT TEST TWO FINISHED");
    System.out.println("___________________");
    System.out.println();
    
    //test three select  - 
    //Fail : Passing a key with wrong value
    System.out.println("___________________");
    System.out.println("SELECT TEST THREE SATRT");
    var t_select2 =  movieStar.select (new KeyType ("Hollywood"));
    movieStar.print ();
    try 
    {
    	t_select2.print ();	
    }
    catch(Exception e)
    {
    	System.out.println("couldn't execute as tables are incompatible for minus operation");
    }
    System.out.println();
    System.out.println("SELECT TEST THREE FINISHED");
    System.out.println("___________________");
    System.out.println();
    
    //test one project  - 
    System.out.println("___________________");
    System.out.println("PROJECT TEST ONE START");
    var t_project = movie.project ("title year");
    t_project.print ();	
    System.out.println();
    System.out.println("PROJECT TEST ONE FINISHED");
    System.out.println("___________________");
    System.out.println();

    //test two project  - 
    System.out.println("___________________");
    System.out.println("PROJECT TEST TWO START");
    var t_project1 = movie.project ("tile year");
    try 
    {
    	t_project1.print ();	
    }
    catch(Exception e)
    {
    	System.out.println("couldn't execute as Relattions and are incompatible for project operation");
    }
    	
    System.out.println();
    System.out.println("PROJECT TEST TWO FINISHED");
    System.out.println("___________________");
    System.out.println();
    
    //test one minus  - 
    System.out.println("___________________");
    System.out.println("MINUS TEST ONE START");
    var t_minus = movie.minus (cinema);
    movie.print ();
    cinema.print();
    t_minus.print();
    System.out.println();
    System.out.println("MINUS TEST ONE FINISHED");
    System.out.println("___________________");
    System.out.println();
    
    //test two minus  - 
    //fail : Tables have different arity
    System.out.println("___________________");
    System.out.println("MINUS TEST TWO START");
    var t_minus1 = movie.minus (studio);
    movie.print ();
    studio.print();
    try 
    {
    	t_minus1.print ();	
    }
    catch(Exception e)
    {
    	System.out.println("couldn't execute as tables are incompatible for minus operation");
    }
    System.out.println();
    System.out.println("MINUS TEST TWO FINISHED");
    System.out.println("___________________");
    System.out.println();
    
    
    
    //test one Union  - 
    System.out.println("___________________");
    System.out.println("UNION TEST ONE START");
    var t_union = movie.union (cinema);
    movie.print ();
    cinema.print();
    t_union.print();
    System.out.println();
    System.out.println("UNION TEST ONE FINISHED");
    System.out.println("___________________");
    System.out.println();
    
  
    //test two Union  -
    //Fail :Tables have different arity
    System.out.println("___________________");
    System.out.println("UNION TEST TWO START");
    var t_union1 = movie.union (studio);
    movie.print ();
    studio.print();
    try 
    {
    	t_union1.print ();	
    }
    catch(Exception e)
    {
    	System.out.println("couldn't execute as tables are incompatible for union operation");	
    }
    System.out.println();
    System.out.println("UNION TEST TWO FINISHED");
    System.out.println("___________________");
    System.out.println();
    

    //test one EQUIJOIN  - one attribute each
    System.out.println("___________________");
    System.out.println("EQUIJOIN TEST ONE START");
    var t01_join = movie.join ("studioName", "name", studio);
    movie.print();
    studio.print();
    try 
    {
    	t01_join.print ();	
    }
    catch(Exception e)
    {
    	System.out.println("couldn't execute as tables are incompatible for union operation");	
    }
    System.out.println();
    System.out.println("EQUIJOIN TEST ONE FINISHED");
    System.out.println("___________________");
    System.out.println();


    //test two EQUIJOIN - two attributes each, two attributes have identical names
    //note: identical names from different tables are not appending a 2 to table2's
    //identical attribute
    System.out.println("___________________");
    System.out.println("EQUIJOIN TEST TWO START");
    var t02_join = movie.join ("studioName length", "name length", cinema);
    movie.print();
    cinema.print();
    try 
    {
    	t02_join.print();	
    }
    catch(Exception e)
    {
    	System.out.println("couldn't execute as tables are incompatible for union operation");	
    }
    System.out.println();
    System.out.println("EQUIJOIN TEST TWO FINISHED");
    System.out.println("___________________");
    System.out.println();
    
   
    //test three EQUIJOIN - attributes that share no common values
    //PROBLEM: it brings in attributes that share common attributes
    //that are NOT the parameters to the join method
    System.out.println("___________________");
    System.out.println("EQUIJOIN TEST THREE START");
    var t03_join = movie.join ("title", "studioName", cinema);
    //studio.print();
    movie.print();
    cinema.print();
    try 
    {
    	t02_join.print();	
    }
    catch(Exception e)
    {
    	System.out.println("couldn't execute as tables are incompatible for equi - join operation");	
    }
    t03_join.print();
    System.out.println();
    System.out.println("EQUIJOIN TEST THREE FINISHED");
    System.out.println("___________________");
    System.out.println();

    //test one NATURAL JOIN - all attributes the same
    System.out.println("___________________");
    System.out.println("NATURAL JOIN TEST ONE START");
    var tnj01_join = movie.join (cinema);
    movie.print();
    cinema.print();
    tnj01_join.print();
    System.out.println();
    System.out.println("NATURAL JOIN TEST ONE FINISHED");
    System.out.println("___________________");
    System.out.println();

    //test two NATURAL JOIN - some attributes same
    //PASS: pass, printing error tuple values aren't same
    System.out.println("___________________");
    System.out.println("NATURAL JOIN TEST TWO START");
    var tnj02_join = movieStar.join (movieExec);
    movieStar.print();
    movieExec.print();
    try 
    {
    	tnj02_join.print();	
    }
    catch(Exception e)
    {
    	System.out.println("couldn't execute as tables are incompatible for Natural - join operation");	
    }
    System.out.println();
    System.out.println("NATURAL JOIN TEST TWO FINISHED");
    System.out.println("___________________");
    System.out.println();


    //test three NATURAL JOIN - no attributes the same
    // Pass: prints the cartesian product when no attributes are same
    System.out.println("___________________");
    System.out.println("NATURAL JOIN TEST THREE START");
    var tnj03_join = movieStar.join (starsIn);
    movieStar.print();
    starsIn.print();
    try 
    {
    	tnj03_join.print();	
    }
    catch(Exception e)
    {
    	System.out.println("couldn't execute as tables are incompatible for Natural - join operation");	
    }
    System.out.println();
    System.out.println("NATURAL JOIN TEST THREE FINISHED");
    System.out.println("___________________");
    System.out.println();
  }//main


} //UnitTest
