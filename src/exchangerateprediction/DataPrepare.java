/* By: Payam Azad    May 2013
 * ToDo: just process on differences of days not pure value
 */

package exchangerateprediction;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import jxl.Sheet;
import jxl.Workbook;

public class DataPrepare 
{
    String trainFile, testFile, modelFile;
    int depth;  //number of previous values that affect our current features
    int[] Depth;  //number of previous values that affect our current feature for each feature
    public int lowerBound, upperBound;
    String[][] trainData;
    String[][] testData;
    public DataPrepare(String train, String test)
    {
        trainFile = train;
        testFile = test;
    }
    public String[] run(String train, String test, int depth, int lbound, int ubound) throws IOException
    {
        
        readFile();  
        this.depth = depth;
        setDepths(trainData[0].length);
        if(depth == 1)
            processData(trainData, testData);
        writeFile();  
        scale(lbound, ubound);
        String[] res = new String[2];
        res[0] = trainFile;
        res[1] = testFile;
        
        return res;
    }
    
    //HMM to prepare more meaningful data
    private void processData(String[][] trainData, String[][] testData)
    {
        String[][] temptrainData = new String[trainData.length][trainData[0].length];
        String[][] temptestData = new String[testData.length][testData[0].length];
        
        for(int i = 0; i < trainData[0].length; i++)
            if(Depth[i] == 1)
            {
                temptrainData[0][i] = String.valueOf(Double.parseDouble(trainData[1][i]) - Double.parseDouble(trainData[0][i]));
                temptestData[0][i] = String.valueOf(Double.parseDouble(testData[1][i]) - Double.parseDouble(testData[0][i]));
                for(int j = 1; j < trainData.length; j++)
                    temptrainData[j][i] = String.valueOf(Double.parseDouble(trainData[j][i]) - Double.parseDouble(trainData[j-1][i]));
                for(int j = 1; j < testData.length; j++)
                    temptestData[j][i] = String.valueOf(Double.parseDouble(testData[j][i]) - Double.parseDouble(testData[j-1][i]));
            }
        
        for(int i = 0; i < trainData.length; i++)
            for(int j = 0; j < trainData[0].length; j++)
                if(Depth[i] == 1)
                    trainData[i][j] = temptrainData[i][j];
        
        for(int i = 0; i < testData.length; i++)
            for(int j = 0; j < testData[0].length; j++)
                if(Depth[i] == 1)
                    testData[i][j] = temptestData[i][j];
    }
    //TODO: find different depth for each feature
    private void setDepths(int n)
    {
        Depth = new int[n];
        for(int i = 0; i < n; i++)
            Depth[i] = depth;    //same depth for each value
    }
    private void readFile()
    {
        trainData = null;
        testData = null;
        //*********************read train file**************************************
        try
        {
            File input = new File(trainFile);
            Workbook workbook = Workbook.getWorkbook(input);  //read workbook
            Sheet sheet = workbook.getSheet(0);               //get second sheet of workbook
            
            //read content of sheet
            trainData = new String[sheet.getRows()-1][sheet.getColumns()-2];  //store data in Strings
            for(int i = 1; i < sheet.getRows(); i++)
                for(int j = 1; j < sheet.getColumns()-1; j++)
                {
                    String cell = sheet.getCell(j, i).getContents();
                    if(cell == "") cell = "0";
                    if(cell == "#REF!") cell = "0";
                    trainData[i-1][j-1] = cell;
                }       
        }
        catch(Exception e)
        {
            System.out.println("Some Error in reading excel file Happened!");
        }
        
        
        
        //*******************read test file**********************************
        try
        {
            File input = new File(testFile);
            Workbook workbook = Workbook.getWorkbook(input);  //read workbook
            Sheet sheet = workbook.getSheet(0);               //get second sheet of workbook
            
            //read content of sheet
            testData = new String[sheet.getRows()-1][sheet.getColumns()-2];  //store data in Strings
            for(int i = 1; i < sheet.getRows(); i++)
                for(int j = 1; j < sheet.getColumns()-1; j++)
                {
                    String cell = sheet.getCell(j, i).getContents();
                    if(cell == "") cell = "0";
                    if(cell == "#REF!") cell = "0";
                    testData[i-1][j-1] = cell;
                }
        }
        catch(Exception e)
        {
            System.out.println("Some Error in reading excel file Happens!");
        }
    }
    private void writeFile()
    {
         //write train data to libSVM format
         Formatter output;
         try 
         {
             trainFile = trainFile.substring(0, trainFile.length()-4);
             output = new Formatter(new File(trainFile));
            
             for(int i = 0; i < trainData.length; i++)
             {
                output.format(trainData[i][49] + " ");
                int k = 0;
                for(int j = 0; j < trainData[0].length-1; j++)
                {
                    if(j == 49)
                        continue;
                    output.format("%d:%s ", ++k, trainData[i][j]);
                }
                output.format("\n");
             }
             output.close();
             System.out.println("train data format change done!");
         } 
         catch (FileNotFoundException ex) 
         {
             System.out.println("Problem in Data Format");
            Logger.getLogger(DataPrepare.class.getName()).log(Level.SEVERE, null, ex);
         }
        
         //write test data to libSVM format
         try 
         {
            testFile = testFile.substring(0, testFile.length()-4);
            output = new Formatter(new File(testFile));
        
            for(int i = 0; i < testData.length; i++)
            {
                output.format(testData[i][49] + " ");
                int k = 0;
                for(int j = 0; j < testData[0].length-1; j++)
                {
                    if(j == 49)
                        continue;
                    output.format("%d:%s ", ++k, testData[i][j]);
                }
                output.format("\n");
            }
            output.close();
            System.out.println("test data format change done!");
        } 
        catch (FileNotFoundException ex) 
        {
            System.out.println("Problem in Data Format");
            Logger.getLogger(DataPrepare.class.getName()).log(Level.SEVERE, null, ex);
        }
            
     }
    private void scale(int lbound, int ubound) throws IOException
    {
        String scaledTrain = trainFile + ".scaled";
        String scaledTest = testFile + ".scaled";
        String lowerBound = String.valueOf(lbound);
        String upperBound = String.valueOf(ubound);
        String[] trainArguments = {"-l", lowerBound, "-u", upperBound, "-s", "range", trainFile, ">", scaledTrain};
        String[] testArguments = {"-r", "range", testFile, ">", scaledTest};
    
        svm_scale s = new svm_scale();
        s.run(trainArguments);
        s.run(testArguments);
        trainFile = scaledTrain;
        testFile = scaledTest;
    }
}
