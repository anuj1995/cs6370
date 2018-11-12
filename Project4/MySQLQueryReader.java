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
    //connect to the database - change to Anuj's credentials
    /*
    MysqlDataSource dataSource = new MysqlDataSource();
    dataSource.setUser("scott");
    dataSource.setPassword("tiger");
    dataSource.setServerName("myDBHost.example.org");
*/

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

    //execute query
    Connection conn = dataSource.getConnection();
    Statement stmt = conn.createStatement();

    //timer for mysql query
    //List the name of the student with id equal to v1 (id).
    double beginTime = System.nanoTime();
    ResultSet rs = stmt.executeQuery("SELECT name FROM student WHERE id=v1");
    double stopTime = System.nanoTime();

    //timer for mysql query
    //List the names of students with id in the range of v2 (id) to v3 (inclusive).
    beginTime = System.nanoTime();
    ResultSet rs = stmt.executeQuery("SELECT name FROM student WHERE id>=v1 AND id<=v3");
    stopTime = System.nanoTime();

    //timer for mysql query
    //List the names of students who have taken course v4 (crsCode).
    beginTime = System.nanoTime();
    ResultSet rs = stmt.executeQuery("SELECT s.name FROM student as s, transcript as t WHERE t.stuId=s.id AND t.crsCode=v4");
    stopTime = System.nanoTime();

    //timer for mysql query
    //List the names of students who have taken a course taught by professor v5 (name).
    beginTime = System.nanoTime();
    ResultSet rs = stmt.executeQuery("SELECT s.name FROM student as s, transcript as tr, teaching as te, professor as p WHERE s.id=tr.studId AND tr.crsCode=te.crsCode AND te.profId=p.id");
    stopTime = System.nanoTime();

    //timer for mysql query
    //List the names of students who have taken a course from department v6 (deptId), but not v7.
    beginTime = System.nanoTime();
    ResultSet rs = stmt.executeQuery("SELECT s.name FROM student as s, transcript as t, course as c WHERE s.id=t.studId AND tr.crsCODE=c.crsCode AND c.deptId=v6 AND c.deptId<>v7");
    stopTime = System.nanoTime();

    //timer for mysql query
    //List the names of students who have taken all courses offered by department v8 (deptId).
    beginTime = System.nanoTime();
    ResultSet rs = stmt.executeQuery("SELECT s.name FROM student as s, transcript as t, course as c WHERE s.id=t.studId AND t.crsCode=c.crsCode AND ALL IN ( SELECT crsCode FROM course WHERE deptId=v8)");
    stopTime = System.nanoTime();

    //close connection to the database
    rs.close();
    stmt.close();
    conn.close();

  }//main


}
