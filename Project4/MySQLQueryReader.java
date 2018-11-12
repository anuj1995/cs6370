import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

import static java.lang.Math.ceil;
import static java.lang.System.out;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;


public class MySQLQueryReader {

  public static void main (String [] args){
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
      int tupleNum = 10000;
      var tables = new String [] { "Student", "Professor", "Course", "Teaching", "Transcript" };
      var tups   = new int [] { tupleNum, tupleNum, tupleNum, tupleNum, tupleNum};

      var resultTest = test.generate (tups);

      Table student = new Table("Student", "id name address status", "Integer String String String", "id");
      Table professor = new Table("Professor", "id name deptId", "Integer String String", "id");
      Table course = new Table("Course", "crsCode deptId crsName descr", "String String String String", "crsCode");
      Table teaching = new Table("Teaching","crsCode semester profId", "String String Integer", "crsCode semester");
      Table transcript = new Table("Transcript", "studId crsCode semester grade", "Integer String String String", "studId crsCode semester");


    for(int i = 0; i < resultTest[0].length; i++){
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
	  int i = 0;
	  double avg = 0;
	  while(i < 11) {
		  double beginTime = System.nanoTime();
		  Table ij_00 = student.i_join("id", "studId", transcript);
		  double stopTime = System.nanoTime();
		  //ij_00.print();
		  System.out.println("index join: " + (stopTime-beginTime) / 1000000 + " ms");
		  if(i != 0) avg += (stopTime-beginTime) / 1000000;
		  i++;
	  }//while
	  avg /=10;
	  System.out.println("index join avg: " + avg);

	  System.out.println();
	  System.out.println("------------------");

	  //equijoin
	  int j = 0;
	  avg = 0;
	  while(j < 11) {
		  double beginTime = System.nanoTime();
		  Table ej_00 = student.join("id", "studId", transcript);
		  double stopTime = System.nanoTime();
		  //ej_00.print();
		  System.out.println("equijoin: " + (stopTime-beginTime)/1000000 + " ms");
		  if(j != 0) avg += (stopTime-beginTime) / 1000000;
		  j++;
	  }//while
	  avg /= 10;
	  System.out.println("equijoin avg: " + avg);


  }//main


}
