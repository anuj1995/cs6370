import static java.lang.System.out;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;


public class SelectRangeQuery {
	
	public static void main (String [] args)
    {
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
        var tups   = new int [] { 10000, 1000, 2000, 50000, 5000 };
        
	    //Create relations (student, professor, course, teaching, transcript)
	    Table studentRelation = new Table("Student", "id name address status", "Integer String String String", "id");
//	    Table professorRelation = new Table ("Professor", "id name deptId",	"Integer String String", "id");
//	    Table courseRelation = new Table ("Course", "crsCode deptId crsName descr", "String String String String", "crsCode");
//        Table teachingRelation = new Table ("Teaching", "crsCode semester profId","String String Integer", "crsCode semester");
//        Table transcriptRelation = new Table ("Transcript", "studId crsCode semester grade","Integer String String String", "studId crsCode semester");
    
        var resultTest = test.generate (tups);
        
        for (int i = 0; i < resultTest.length; i++) {
            out.println (tables [i]);
            for (int j = 0; j < resultTest [i].length; j++) {
                for (int k = 0; k < resultTest [i][j].length; k++) {
                    out.print (resultTest [i][j][k] + ",");
                } // for
                out.println ();
            } // for
            out.println ();
        } // for
        
      //Insert tuples into various relations
        	//STUDENT//
        for (int i = 0; i < resultTest[0].length; i++) {
            studentRelation.insert(resultTest[0][i]);
        }
//        	//PROFESSOR//
//        for (int i = 0; i < resultTest[1].length; i++) {
//            professorRelation.insert(resultTest[1][i]);
//        }
//        	//COURSE//
//        for (int i = 0; i < resultTest[2].length; i++) {
//            courseRelation.insert(resultTest[2][i]);
//        }
//        	//TEACHING//
//        for (int i = 0; i < resultTest[3].length; i++) {
//            teachingRelation.insert(resultTest[3][i]);
//        }
//        	//TRANSCRIPT//
//        for (int i = 0; i < resultTest[4].length; i++) {
//            transcriptRelation.insert(resultTest[4][i]);
//        }
        
        //Random generator to create number for index
        Random randomGenerator = new Random();
        
        double totalTime = 0;
        
        //Select Range Query with 1000 tuples
        for( int i = 0; i < 11 ; i++){
        	
        	var index1 = randomGenerator.nextInt(800) + 1;
        	KeyType a = new KeyType(resultTest[0][index1][0]);
        	KeyType b = new KeyType(resultTest[0][1000][0]);
        	
        	out.println("Index 1 = " + index1);
        	
        	var start = System.nanoTime();
        	
        	if(i == 0)
        	{
            	studentRelation.select(a, b);
            	continue;
        	}

        	
        	studentRelation.select(a, b);
        	var end = System.nanoTime();
        	var timeDuration = (double)(end - start)/1000000000.0;
        	
        	totalTime += timeDuration;
        }
        
        double average = totalTime / 10;
        
        out.println("Average time for range query : " + average);
    } // main
}
