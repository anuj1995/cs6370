import static java.lang.System.out;

import java.util.List;
import java.util.Random;


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
	Table professorRelation = new Table ("Professor", "id name deptId", "Integer String String", "id");
	Table courseRelation = new Table ("Course", "crsCode deptId crsName descr", "String String String String", "crsCode");
        Table teachingRelation = new Table ("Teaching", "crsCode semester profId","String String Integer", "crsCode semester");
        Table transcriptRelation = new Table ("Transcript", "studId crsCode semester grade","Integer String String String", "studId crsCode semester");
    
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
        
      //Insert tuples into student relation
        for (int i = 0; i < resultTest [0].length; i++) {
            studentRelation.insert(resultTest[0][i]);
        }
        
        //Random generator to create index
        Random randomGenerator = new Random();
        
        //Create indexes for each relation
        int studentIndex, professorIndex, courseIndex, teachingIndex, transcriptIndex;
        
        //Select Range Query with 1000 tuples
        for( int i = 0; i < 100 ; i++){
		studentIndex = randomGenerator.nextInt(1000) + 1;
            KeyType k = new KeyType(resultTest[0][studentIndex][0]);
            professorRelation.select(k);
        }
        
        //Select Range Query with 5000 tuples
        for( int i = 0; i < 100 ; i++){
        	professorIndex = randomGenerator.nextInt(5000) + 1;
            KeyType k = new KeyType(resultTest[0][professorIndex][0]);
            professorRelation.select(k);
        }
        
        //Select Range Query with 7000 tuples
        for( int i = 0; i < 100 ; i++){
        	courseIndex = randomGenerator.nextInt(7000) + 1;
            KeyType k = new KeyType(resultTest[0][courseIndex][0]);
            courseRelation.select(k);
        }
        
        //Select Range Query with 10000 tuples
        for( int i = 0; i < 100 ; i++){
        	teachingIndex = randomGenerator.nextInt(10000) + 1;
            KeyType k = new KeyType(resultTest[0][teachingIndex][0]);
            teachingRelation.select(k);
        }
        
        //Select Range Query with 50000 tuples
        for( int i = 0; i < 100 ; i++){
        	transcriptIndex = randomGenerator.nextInt(50000) + 1;
            KeyType k = new KeyType(resultTest[0][transcriptIndex][0]);
            transcriptRelation.select(k);
        }
        
        //Join Query
        for( int i = 0; i < 100 ; i++){
            Table results = studentRelation.join("id", "studId", transcriptRelation);
            }
        
        //Print relations
        studentRelation.printIndex();
        transcriptRelation.printIndex();
        
    } // main
}
