import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

import static java.lang.Math.ceil;
import static java.lang.System.out;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;

public class JoinQuery {

	/**
	 * Calculates the execution time for the i_join method between table1 and table2.
	 * @param table1 is the first table to join.
	 * @param table2 is the second table to join.
	 * @param attribute1 is table1's attribute to join on.
	 * @param attribute2 is table2's attribute to join on.
	 * @return time that it takes for i_join to run in milliseconds.
	 */
	public static long i_joinExecutionTime(Table table1, Table table2, String attribute1, String attribute2) {
		long beginTime = System.nanoTime();
		table1.i_join(attribute1, attribute2, table2);
		long stopTime = System.nanoTime();
		
		return (stopTime-beginTime) / 1000000; //time in milliseconds
		
	}//execuationTime
	
	
	/**
	 * Calculates the execution time for the equijoin method between table1 and table2.
	 * @param table1 is the first table to join.
	 * @param table2 is the second table to join.
	 * @param attribute1 is table1's attribute to join on.
	 * @param attribute2 is table2's attribute to join on.
	 * @return time that it takes for join to run in milliseconds.
	 */
	public static long equiJoinExecutionTime(Table table1, Table table2, String attribute1, String attribute2) {
		long beginTime = System.nanoTime();
		table1.join(attribute1, attribute2, table2);
		long stopTime = System.nanoTime();
		
		return (stopTime-beginTime)/1000000; //time in milliseconds
		
	}//execuationTime
	
	/**
	 * Calculates the execution time for the natural join method between table1 and table2.
	 * @param table1 is the first table to join.
	 * @param table2 is the second table to join.
	 * @return time that it takes for join to run in milliseconds.
	 */
	public static long naturalJoinExecutionTime(Table table1, Table table2) {
		long beginTime = System.nanoTime();
		table1.join(table2);
		long stopTime = System.nanoTime();
		
		return (stopTime-beginTime)/1000000; //time in milliseconds
		
	}//execuationTime
	
	
	/**
	 * Prints time in milliseconds and number of tuples to file.
	 * @param ms is the run time in milliseconds.
	 * @param numTuples1 is the number of tuples in table1.
	 * @param numTuples2 is the number of tuples in table2.
	 */
	public void saveToFile(Double ms, int numTuples1, int numTuples2) {
		int totalTuples = numTuples1 + numTuples2;
		
		
		
		
	}//saveToFile
	

  public static void main(String [] args){
	  var test = new TupleGeneratorImpl ();

      test.addRelSchema ("Student",
                         "id name address status",
                         "Integer String String String",
                         "id",
                         null);
      
      test.addRelSchema ("Professor",
                         "id name deptId",
                         "Integer String String",
                         "id",
                         null);
      
      test.addRelSchema ("Course",
                         "crsCode deptId crsName descr",
                         "String String String String",
                         "crsCode",
                         null);
      
      test.addRelSchema ("Teaching",
                         "crsCode semester profId",
                         "String String Integer",
                         "crcCode semester",
                         new String [][] {{ "profId", "Professor", "id" },
                                          { "crsCode", "Course", "crsCode" }});
      
      test.addRelSchema ("Transcript",
                         "studId crsCode semester grade",
                         "Integer String String String",
                         "studId crsCode semester",
                         new String [][] {{ "studId", "Student", "id"},
                                          { "crsCode", "Course", "crsCode" },
                                          { "crsCode semester", "Teaching", "crsCode semester" }});

      var tables = new String [] { "Student", "Professor", "Course", "Teaching", "Transcript" };
      var tups   = new int [] { 10000, 10000, 10000, 10000, 10000 };
  
      var resultTest = test.generate (tups);
      
      Table student = new Table("Student", "id name address status", "Integer String String String", "id");
      Table professor = new Table("Professor", "id name deptId", "Integer String String", "id");
      Table course = new Table("Course", "crsCode deptId crsName descr", "String String String String", "crsCode");
      Table teaching = new Table("Teaching","crsCode semester profId", "String String Integer", "crsCode semester");
      Table transcript = new Table("Transcript", "studId crsCode semester grade", "Integer String String String", "studId crsCode semester");
            
      
      
/*      for (int i = 0; i < resultTest.length; i++) {
          
          for (int j = 0; j < resultTest [i].length; j++) {
        	  //inserting tuples into tables
        	  if(i == 0) student.insert(resultTest [i][j]);
        	  else if(i == 1) professor.insert(resultTest [i][j]);
        	  else if(i == 2) course.insert(resultTest [i][j]);
        	  else if(i == 3) teaching.insert(resultTest [i][j]);
        	  else if(i == 4) transcript.insert(resultTest [i][j]);
        	  
       
          } // for
      } // for
      
*/      for(int i = 0; i < resultTest[0].length; i++){
	        var tt = new Comparable [] {resultTest[0][i][0], resultTest[0][i][1], resultTest[0][i][2], resultTest[0][i][3]};
	        student.insert(tt);
		}
		for(int i = 0; i < resultTest[4].length; i++){
			var tt = new Comparable [] {resultTest[4][i][0], resultTest[4][i][1], resultTest[4][i][2], resultTest[4][i][3]};
			transcript.insert(tt);
		}
		
      
	  
 /*     //printing tables
      professor.print();
      student.print();
      course.print();
      teaching.print();
      transcript.print();
      */
      
      
      
	  
      //index join
      double beginTime = System.nanoTime();
      Table ij_00 = student.i_join("id", "studId", transcript);
      double stopTime = System.nanoTime();	
      ij_00.print();
	  System.out.println("index join: " + (stopTime-beginTime) / 1000000 + " ms");
      
	  
	  //equijoin
      beginTime = System.nanoTime();
	  Table ej_00 = student.join("id", "studId", transcript);
	  stopTime = System.nanoTime();
	  //ej_00.print();
      System.out.println("equijoin: " + (stopTime-beginTime)/1000000 + " ms");
	 
	  
/*      //natural join
      beginTime = System.nanoTime();
      teaching.join(transcript);
      stopTime = System.nanoTime();
	  System.out.println("natural join: " + (stopTime-beginTime)/1000000 + " ms");*/
	  
	  
	  
	  
      
      
	  
	  
	  
	  
  }//main



}//JoinQuery
