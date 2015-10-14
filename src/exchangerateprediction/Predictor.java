/*
 */
package exchangerateprediction;

import java.io.IOException;

public class Predictor 
{
    int depth;
    String testFile, modelFile, outputFile;
    public Predictor(){}
    public Predictor(String testFile, String modelFile, String outputFile)
    {
        this.testFile = testFile;
        this.modelFile = modelFile;
        this.outputFile = outputFile;
    }
    //XXX ***add real predict ability***
    public void run(int depth) throws IOException
    {
        svm_predict p = new svm_predict();
        String[] argv = {testFile, modelFile, outputFile};
        p.run(argv);
    }
}
