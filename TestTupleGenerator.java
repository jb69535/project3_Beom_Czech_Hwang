 
/*****************************************************************************************
 * @file  TestTupleGenerator.java
 *
 * @author   Sadiq Charaniya, John Miller
 */

import static java.lang.System.out;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

/*****************************************************************************************
 * This class tests the TupleGenerator on the Student Registration Database defined in the
 * Kifer, Bernstein and Lewis 2006 database textbook (see figure 3.6).  The primary keys
 * (see figure 3.6) and foreign keys (see example 3.2.2) are as given in the textbook.
 */
public class TestTupleGenerator
{
    /*************************************************************************************
     * The main method is the driver for TestGenerator.
     * @param args  the command-line arguments
     */
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
        var tups   = new int [] { 500, 1000, 2000, 5000, 10000 };
    
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("results.txt"))) {
            for (int numTuples : tups) {
                Comparable[][][] resultTest = test.generate(new int[]{numTuples, numTuples / 2});

                // Create student tables for different indexing mechanisms
                String studentSchema = "id name address status";
                Class[] studentDomain = new Class[]{Integer.class, String.class, String.class, String.class};
                Table studentTableNoIndex = new Table("Student", studentSchema.split(" "), studentDomain, "id".split(" "), "NoIndex");
                Table studentTableTreeMap = new Table("Student", studentSchema.split(" "), studentDomain, "id".split(" "), "TreeMap");
                Table studentTableHashMap = new Table("Student", studentSchema.split(" "), studentDomain, "id".split(" "), "HashMap");
                Table studentTableLinHashMap = new Table("Student", studentSchema.split(" "), studentDomain, "id".split(" "), "LinHashMap");
                
                // Insert student tuples into tables
                for (int i = 0; i < resultTest[0].length; i++) {
                    studentTableNoIndex.insert(resultTest[0][i]);
                    studentTableTreeMap.insert(resultTest[0][i]);
                    studentTableHashMap.insert(resultTest[0][i]);
                    studentTableLinHashMap.insert(resultTest[0][i]);
                }

                // Create transcript tables for different indexing mechanisms
                String transcriptSchema = "studId crsCode semester grade";
                Class[] transcriptDomain = new Class[]{Integer.class, String.class, String.class, String.class};
                Table transcriptTableNoIndex = new Table("Transcript", transcriptSchema.split(" "), transcriptDomain, "studId".split(" "), "NoIndex");
                Table transcriptTableTreeMap = new Table("Transcript", transcriptSchema.split(" "), transcriptDomain, "studId".split(" "), "TreeMap");
                Table transcriptTableHashMap = new Table("Transcript", transcriptSchema.split(" "), transcriptDomain, "studId".split(" "), "HashMap");
                Table transcriptTableLinHashMap = new Table("Transcript", transcriptSchema.split(" "), transcriptDomain, "studId".split(" "), "LinHashMap");

                // Insert transcript tuples into tables
                for (int i = 0; i < resultTest[1].length; i++) {
                    transcriptTableNoIndex.insert(resultTest[1][i]);
                    transcriptTableTreeMap.insert(resultTest[1][i]);
                    transcriptTableHashMap.insert(resultTest[1][i]);
                    transcriptTableLinHashMap.insert(resultTest[1][i]);
                }

                // Perform select and join operations for each indexing mechanism
                long sumSelectNoIndex = 0, sumSelectTreeMap = 0, sumSelectHashMap = 0, sumSelectLinHashMap = 0;
                long sumJoinNoIndex = 0, sumJoinTreeMap = 0, sumJoinHashMap = 0, sumJoinLinHashMap = 0;

                for (int i = 0; i < 20; i++) {
                    Random gen = new Random();
                    int id = gen.nextInt(numTuples);
                    KeyType key = new KeyType(id);

                    // Select operation with NoIndex
                    long startTimeNoIndex = System.nanoTime();
                    studentTableNoIndex.noIndexSelect(key);  
                    long endTimeNoIndex = System.nanoTime();
                    sumSelectNoIndex += endTimeNoIndex - startTimeNoIndex;

                    // Select operation with TreeMap
                    long startTimeTreeMap = System.nanoTime();
                    studentTableTreeMap.select(key);
                    long endTimeTreeMap = System.nanoTime();
                    sumSelectTreeMap += endTimeTreeMap - startTimeTreeMap;

                    // Select operation with HashMap
                    long startTimeHashMap = System.nanoTime();
                    studentTableHashMap.select(key);
                    long endTimeHashMap = System.nanoTime();
                    sumSelectHashMap += endTimeHashMap - startTimeHashMap;

                    // Select operation with LinHashMap
                    long startTimeLinHashMap = System.nanoTime();
                    studentTableLinHashMap.select(key);
                    long endTimeLinHashMap = System.nanoTime();
                    sumSelectLinHashMap += endTimeLinHashMap - startTimeLinHashMap;

                    // Join operation with NoIndex
                    long startJoinTimeNoIndex = System.nanoTime();
                    studentTableNoIndex.noIndexjoin("id", "studId", transcriptTableNoIndex); 
                    long endJoinTimeNoIndex = System.nanoTime();
                    sumJoinNoIndex += endJoinTimeNoIndex - startJoinTimeNoIndex;

                    // Join operation with TreeMap
                    long startJoinTimeTreeMap = System.nanoTime();
                    studentTableTreeMap.join("id", "studId", transcriptTableTreeMap);
                    long endJoinTimeTreeMap = System.nanoTime();
                    sumJoinTreeMap += endJoinTimeTreeMap - startJoinTimeTreeMap;

                    // Join operation with HashMap
                    long startJoinTimeHashMap = System.nanoTime();
                    studentTableHashMap.join("id", "studId", transcriptTableHashMap);
                    long endJoinTimeHashMap = System.nanoTime();
                    sumJoinHashMap += endJoinTimeHashMap - startJoinTimeHashMap;

                    // Join operation with LinHashMap
                    long startJoinTimeLinHashMap = System.nanoTime();
                    studentTableLinHashMap.join("id", "studId", transcriptTableLinHashMap);
                    long endJoinTimeLinHashMap = System.nanoTime();
                    sumJoinLinHashMap += endJoinTimeLinHashMap - startJoinTimeLinHashMap;
                }

                // Calculate average times and write results to file
                writer.write("Number of tuples: " + numTuples + "\n");
                writer.write("Average select time (NoIndex): " + sumSelectNoIndex / 20.0 + " ns\n");
                writer.write("Average select time (TreeMap): " + sumSelectTreeMap / 20.0 + " ns\n");
                writer.write("Average select time (HashMap): " + sumSelectHashMap / 20.0 + " ns\n");
                writer.write("Average select time (LinHashMap): " + sumSelectLinHashMap / 20.0 + " ns\n");
                writer.write("Average join time (NoIndex): " + sumJoinNoIndex / 20.0 + " ns\n");
                writer.write("Average join time (TreeMap): " + sumJoinTreeMap / 20.0 + " ns\n");
                writer.write("Average join time (HashMap): " + sumJoinHashMap / 20.0 + " ns\n");
                writer.write("Average join time (LinHashMap): " + sumJoinLinHashMap / 20.0 + " ns\n");
                writer.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} // TestTupleGenerator

