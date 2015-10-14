/* By: Payam Azad    May 2013
 * This code will use PBIL, a kind of EDA for tuning SVM parameters. 
 */
package exchangerateprediction;

import java.io.IOException;

public class EDATrainer 
{
    String trainFile;
    public EDATrainer(String train)
    {
        trainFile = train;
    }
    
    public void run() throws IOException
    {
        EDA eda = new EDA(11, 20);  //chromosome lengh = 11  population size = 10
        eda.fileName = trainFile;        
        byte[] bestAnswer = eda.run(25);  //50 generations
        //eda.printResults();
        
        //last and best run
        Trainer trainer = new Trainer(trainFile);
        double[] result = trainer.run(bestAnswer);
        String[] parameters = trainer.extractParameters(bestAnswer);
        System.out.print("The best training done by: " + result[0] + " MSE and " + result[1] + " SCC in cross validations with parameters: \n");
        System.out.print(trainer.toString(parameters));
    }
    
}
