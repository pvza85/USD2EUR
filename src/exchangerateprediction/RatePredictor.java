
package exchangerateprediction;

public class RatePredictor 
{
    String trainFile, testFile;
    String inputFile, outputFile;
    String modelFile, classificationOutput;
    
    public RatePredictor(String train, String test, String output)
    {
        trainFile = train;
        testFile = test;
        outputFile = output;
    }
    public void prepare(int depth, int lbound, int ubound)
    {
        DataPrepare data = new DataPrepare(trainFile, testFile);
        String train = "", test = "";
        try
        {
            String[] newFiles = data.run(train, test, depth, lbound, ubound);
            trainFile = newFiles[0];
            testFile = newFiles[1];
        }
        catch(Exception e)
        {
            System.out.println("Problem in Data Preparation!");
        }
        
    }
    public void train()
    {
        //GATrainer trainer = new GATrainer(trainFile);
        EDATrainer trainer = new EDATrainer(trainFile);
        try
        {
            trainer.run();
        }
        catch(Exception e)
        {
            System.out.println("Error in Training.");
        }
    }
    public void predict(int depth)
    {
        modelFile = trainFile + ".model";
        Predictor predictor = new Predictor(testFile, modelFile, outputFile);
        try
        {
            predictor.run(0);
        }
        catch(Exception e)
        {
            System.out.println("Error in Predictin.");
        }
    }
    //XXX add classification using regression
    //TODO: add profit calculator
}
