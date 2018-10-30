import static java.lang.System.out;
import java.util.Scanner;
import java.util.Random;

public class SelectPointQuery{


        public static void main(String[] args){
                var test = new TupleGeneratorImpl ();


                var student = new Table("Student", "id name address status",
                                        "Integer String String String","id");
/*
                var professor = new Table("Professor", "id name deptId",
                                        "Integer String String","id");


                var course = new Table("Course","crsCode deptId crsName descr",
                                        "String String String String","crsCode");
/*
                var teaching = new Table("Teaching","crsCode semester profId",
                                   "String String Integer","crcCode semester",
                                   new String [][] {{ "profId", "Professor", "id" },
                                                    { "crsCode", "Course", "crsCode" }});

                var transcript = new Table("Transcript","studId crsCode semester grade",
                                        "Integer String String String", "studId crsCode semester",
                                        new String [][] {{ "studId", "Student", "id"},
                                        { "crsCode", "Course", "crsCode" },
                                        { "crsCode semester", "Teaching", "crsCode semester" }});
*/



                test.addRelSchema ("Student",
                                   "id name address status",
                                   "Integer String String String",
                                   "id",
                                   null);
/*
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
*/
                var tables = new String [] {"Student"};//, "Professor", "Course", "Teaching", "Transcript" };

                Scanner keyboard = new Scanner(System.in);
                out.println("How many tuples do you want to insert?");
                int numOfTups = keyboard.nextInt();

                var tups   = new int [] {numOfTups};//, 1000, 2000, 50000, 5000 };
                //var tups   = new int [] { 10, 10, 10, 10, 10 };

                var resultTest = test.generate (tups);

/*
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
*/

/*

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


*/



                for(int i = 0; i < resultTest[0].length; i++){
                        var tt = new Comparable [] {resultTest[0][i][0], resultTest[0][i][1], resultTest[0][i][2], resultTest[0][i][3]};
                        student.insert(tt);

                }
/*
                for(int i = 0; i < resultTest[0].length; i++){
                        var tt = new Comparable [] {resultTest[1][i][0], resultTest[1][i][1], resultTest[1][i][2]};
                        professor.insert(tt);
                }

                for(int i = 0; i < resultTest[0].length; i++){
                        var tt = new Comparable [] {resultTest[2][i][0], resultTest[2][i][1], resultTest[2][i][2], resultTest[0][i][3]};
                        course.insert(tt);
                }
*/
//                student.print();
//                professor.print();
//                course.print();
                Random rand = new Random();
                int randomIndex = rand.nextInt(numOfTups);
                double totTime = 0;



                for(int i = 0; i < 11; i++){
                        double before = System.nanoTime();

                        var t_iselect = student.select (new KeyType (resultTest[0][randomIndex][0]));
                        //var t_select = student.select (t -> t[student.col("id")].equals (resultTest[0][0][0]) || t[student.col("name")].equals (resultTest[0][9][1]));

                        double after = System.nanoTime();
                        double time = (after - before) / 1000000.0;
                        //out.println(time);
                        if (i != 0){
                                totTime += time;
                        }
                }
                var t_iselect = student.select (new KeyType (resultTest[0][randomIndex][0]));
                t_iselect.print ();
                totTime /= 10.0;
                out.println("On average it took: " + totTime + " ms");






        }
}
